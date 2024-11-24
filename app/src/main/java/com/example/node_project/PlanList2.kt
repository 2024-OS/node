package com.example.node_project

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

@Suppress("DEPRECATION")
class PlanList2 : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var placeEditText: EditText // 장소 입력 필드
    private lateinit var searchButton: Button // 검색 버튼
    private lateinit var deleteButton: Button // 삭제 버튼
    private var markers: MutableMap<String, Marker> = mutableMapOf() // 마커 저장용 맵
    private var selectedMarker: Marker? = null // 선택된 마커

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan_list2, container, false)
        setupMapView(view, savedInstanceState) // MapView 초기화

        // UI 구성 요소 초기화
        placeEditText = view.findViewById(R.id.placeEditText) // EditText
        searchButton = view.findViewById(R.id.searchButton) // 버튼
        deleteButton = view.findViewById(R.id.deleteButton) // 삭제 버튼

        // 검색 버튼 클릭 리스너 설정
        searchButton.setOnClickListener {
            val query = placeEditText.text.toString()
            searchPlace(query) // 장소 검색 호출
        }

        // 삭제 버튼 클릭 리스너 설정
        deleteButton.setOnClickListener {
            selectedMarker?.let {
                removeMarker(it.title ?: "") // 선택된 마커 삭제 호출
                selectedMarker = null // 선택된 마커 초기화
            } ?: run {
                Toast.makeText(requireContext(), "선택된 마커가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // MapView 초기화
    private fun setupMapView(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setOnMarkerClickListener(this) // 마커 클릭 리스너 설정
        setupMapOptions() // 지도 설정 호출
        loadSavedPlaces() // 저장된 장소 불러오기
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        selectedMarker = marker // 선택된 마커 저장
        Toast.makeText(requireContext(), "${marker.title} 마커가 선택되었습니다.", Toast.LENGTH_SHORT).show()
        return true
    }

    // 지도 설정
    private fun setupMapOptions() {
        val location = LatLng(37.60153324458494, 126.86503171920776) // 기본 위치
        addMarkerAtLocation(location, "한국항공대학교") // 마커 추가
        moveCameraToLocation(location, 15f) // 카메라 위치 조정
    }

    // 장소 검색 및 마커 추가
    private fun searchPlace(query: String) {
        val geoCoder = android.location.Geocoder(requireContext(), Locale.getDefault())
        val results = geoCoder.getFromLocationName(query, 1)

        if (!results.isNullOrEmpty()) {
            val location = results[0]
            val latitude = location.latitude
            val longitude = location.longitude

            addMarkerAtLocation(LatLng(latitude, longitude), query) // 마커 추가
            moveCameraToLocation(LatLng(latitude, longitude), 15f) // 카메라 위치 조정

            saveToPreferences(latitude, longitude, query) // 결과 저장
            Toast.makeText(requireContext(), "$query 저장 완료", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "장소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 마커 삭제
    private fun removeMarker(title: String) {
        markers.entries.find { it.key.equals(title, ignoreCase = true) }?.let { entry ->
            val marker = entry.value
            marker.remove() // 지도에서 마커 제거
            markers.remove(entry.key) // 리스트에서 마커 제거
            Toast.makeText(requireContext(), "$title 마커가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(requireContext(), "$title 마커를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 저장 결과 함수
    private fun saveToPreferences(latitude: Double, longitude: Double, query: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(query, "$latitude,$longitude")
            apply()
        }
    }

    // 마커 추가
    private fun addMarkerAtLocation(location: LatLng, title: String) {
        val marker = googleMap.addMarker(MarkerOptions().position(location).title(title))
        if (marker != null) {
            markers[title] = marker // 마커를 맵에 저장
        }
    }

    // 카메라 이동
    private fun moveCameraToLocation(location: LatLng, zoomLevel: Float) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    // 저장된 장소 불러오기
    private fun loadSavedPlaces() {
        val sharedPreferences = requireActivity().getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        allEntries.forEach { (key, value) ->
            val coordinates = value.toString().split(",")
            if (coordinates.size == 2) {
                val latitude = coordinates[0].toDoubleOrNull()
                val longitude = coordinates[1].toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    addMarkerAtLocation(LatLng(latitude, longitude), key) // 마커 추가
                }
            }
        }
    }

    // 생명주기 메서드 처리
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