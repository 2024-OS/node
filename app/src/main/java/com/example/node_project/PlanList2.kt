package com.example.node_project

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale
import com.google.maps.android.SphericalUtil

@Suppress("DEPRECATION")
class PlanList2 : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val defaultLocation = LatLng(37.60153324458494, 126.86503171920776) // 기본 위치 설정
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var placeEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var addButton: Button
    private lateinit var deleteButton: Button
    private lateinit var markerRecyclerView: RecyclerView
    private lateinit var planMarkerListAdapter: PlanMarkerListAdapter
    private var markers: MutableMap<String, Marker> = mutableMapOf()
    private var selectedMarker: Marker? = null
    private var searchedLocation: LatLng? = null
    private var searchedTitle: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan_list2, container, false)
        setupMapView(view, savedInstanceState)

        placeEditText = view.findViewById(R.id.placeEditText)
        searchButton = view.findViewById(R.id.searchButton)
        addButton = view.findViewById(R.id.addButton)
        deleteButton = view.findViewById(R.id.deleteButton)
        markerRecyclerView = view.findViewById(R.id.markerRecyclerView)

        searchButton.setOnClickListener {
            val query = placeEditText.text.toString()
            if (query.isBlank()) {
                Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                searchPlace(query)
            }
        }

        addButton.setOnClickListener {
            searchedLocation?.let { location ->
                searchedTitle?.let { title ->
                    addMarkerAtLocation(location, title)
                    saveToPreferences(location.latitude, location.longitude, title)
                    Toast.makeText(requireContext(), "$title 마커가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    searchedLocation = null
                    searchedTitle = null
                }
            } ?: run {
                Toast.makeText(requireContext(), "추가할 마커가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            selectedMarker?.let {
                removeMarker(it.title ?: "")
                selectedMarker = null
            } ?: run {
                Toast.makeText(requireContext(), "선택된 마커가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        setupMarkerRecyclerView()

        return view
    }

    private fun setupMapView(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        setupMapOptions()
        loadSavedPlaces()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        selectedMarker = marker
        val distance = SphericalUtil.computeDistanceBetween(defaultLocation, marker.position)
        val distanceInKm = distance / 1000
        Toast.makeText(requireContext(), "${marker.title} 마커가 선택됨. 항공대와의 거리: ${String.format("%.2f", distanceInKm)} km", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun setupMapOptions() {
        val location = LatLng(37.60153324458494, 126.86503171920776)
        addMarkerAtLocation(location, "한국항공대학교")
        moveCameraToLocation(location, 15f)
    }

    private fun searchPlace(query: String) {
        val geoCoder = android.location.Geocoder(requireContext(), Locale.getDefault())
        val results = geoCoder.getFromLocationName(query, 1)

        if (!results.isNullOrEmpty()) {
            val location = results[0]
            val latitude = location.latitude
            val longitude = location.longitude

            searchedLocation = LatLng(latitude, longitude)
            searchedTitle = query
            moveCameraToLocation(searchedLocation!!, 15f)
            Toast.makeText(requireContext(), "$query 위치가 검색되었습니다. '마커추가' 버튼을 누르세요.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "장소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeMarker(title: String) {
        markers.entries.find { it.key.equals(title, ignoreCase = true) }?.let { entry ->
            val marker = entry.value
            marker.remove()
            markers.remove(entry.key)
            removeFromPreferences(title)
            Toast.makeText(requireContext(), "$title 마커가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            updateMarkerList()
        } ?: run {
            Toast.makeText(requireContext(), "$title 마커를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromPreferences(title: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(title)
            apply()
        }
    }

    private fun saveToPreferences(latitude: Double, longitude: Double, query: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(query, "$latitude,$longitude")
            apply()
        }
    }

    private fun addMarkerAtLocation(location: LatLng, title: String) {
        val markerOptions = MarkerOptions()
            .position(location)
            .title(title)
            .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(title)))

        val marker = googleMap.addMarker(markerOptions)
        if (marker != null) {
            markers[title] = marker
            updateMarkerList()
        }
    }

    private fun createCustomMarker(title: String): Bitmap {
        val markerView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_marker, null)
        val markerTextView = markerView.findViewById<TextView>(R.id.markerTextView)
        markerTextView.text = title

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        markerView.draw(canvas)
        return bitmap
    }

    private fun moveCameraToLocation(location: LatLng, zoomLevel: Float) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    private fun loadSavedPlaces() {
        val sharedPreferences = requireActivity().getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        allEntries.forEach { (key, value) ->
            val coordinates = value.toString().split(",")
            if (coordinates.size == 2) {
                val latitude = coordinates[0].toDoubleOrNull()
                val longitude = coordinates[1].toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    addMarkerAtLocation(LatLng(latitude, longitude), key)
                }
            }
        }
    }

    private fun setupMarkerRecyclerView() {
        markerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        planMarkerListAdapter = PlanMarkerListAdapter(markers.values.toList()) { marker ->
            moveCameraToLocation(marker.position, 15f)
        }
        markerRecyclerView.adapter = planMarkerListAdapter
    }

    private fun updateMarkerList() {
        planMarkerListAdapter.updateMarkers(markers.values.toList())
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}