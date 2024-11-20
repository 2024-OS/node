package com.example.node_project

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PlanList2 : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan_list2, container, false)
        setupMapView(view, savedInstanceState) // MapView 초기화
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

        // 지도 설정을 위한 메서드 호출
        setupMapOptions()
    }

    // 지도 설정 함수
    private fun setupMapOptions() {
        val location = LatLng(37.7749, -122.4194) // 예시 위치
        addMarkerAtLocation(location, "예시 마커") // 마커 추가
        moveCameraToLocation(location, 10f) // 카메라 위치 조정
    }

    // 마커 추가 함수
    private fun addMarkerAtLocation(location: LatLng, title: String) {
        googleMap.addMarker(MarkerOptions().position(location).title(title))
    }

    // 카메라 이동 함수
    private fun moveCameraToLocation(location: LatLng, zoomLevel: Float) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
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
