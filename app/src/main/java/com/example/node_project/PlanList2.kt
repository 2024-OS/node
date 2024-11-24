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
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

@Suppress("DEPRECATION")
class PlanList2 : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var placeEditText: EditText // 장소 입력 필드
    private lateinit var searchButton: Button // 검색 버튼

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan_list2, container, false)
        setupMapView(view, savedInstanceState) // MapView 초기화

        // UI 구성 요소 초기화
        placeEditText = view.findViewById(R.id.placeEditText) // EditText
        searchButton = view.findViewById(R.id.searchButton) // Button

        // 검색 버튼 클릭 리스너 설정
        searchButton.setOnClickListener {
            val query = placeEditText.text.toString()
            searchPlace(query) // 장소 검색 함수 호출
        }

        return view
    }

    // MapView 초기화 함수
    private fun setupMapView(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // OnMapReadyCallback 적용
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupMapOptions() // 지도 설정 호출
        loadSavedPlaces() // 저장된 장소 불러오기
    }

    // 지도 설정 함수
    private fun setupMapOptions() {
        val location = LatLng(37.60153324458494, 126.86503171920776) // 한국항공대학교 위치
        addMarkerAtLocation(location, "한국항공대학교") // 마커 추가
        moveCameraToLocation(location, 15f) // 카메라 위치 조정
    }

    // 장소 검색 및 저장 함수
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

    // 결과 저장 함수
    private fun saveToPreferences(latitude: Double, longitude: Double, query: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("SavedPlaces", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(query, "$latitude,$longitude")
            apply()
        }
    }

    // 마커 추가 함수
    private fun addMarkerAtLocation(location: LatLng, title: String) {
        googleMap.addMarker(MarkerOptions().position(location).title(title))
    }

    // 카메라 이동 함수
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
                    addMarkerAtLocation(LatLng(latitude, longitude), key)
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
        mapView.onSaveInstanceState(outState) // 생명주기 상태 저장
    }
}
