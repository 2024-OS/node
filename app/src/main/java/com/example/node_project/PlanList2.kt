package com.example.node_project

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentPlanList2Binding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

// 계획 목록과 Google Map을 표시하는 Fragment 클래스
class PlanList2 : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentPlanList2Binding // XML 레이아웃과 연결하는 바인딩 객체
    private val viewModel: PlanList2ViewModel by viewModels() // ViewModel을 통해 UI 관련 데이터를 관리
    private lateinit var googleMap: GoogleMap // GoogleMap 객체
    private lateinit var planList2Adapter: PlanList2Adapter // RecyclerView 어댑터

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Fragment의 UI를 생성
        binding = FragmentPlanList2Binding.inflate(inflater, container, false) // XML 레이아웃을 인플레이트
        return binding.root // Fragment의 뷰 반환
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Fragment의 뷰가 생성된 후 호출
        setupMapView(savedInstanceState) // 지도 뷰 설정
        setupUI() // UI 요소의 클릭 리스너 설정
        observeViewModel() // ViewModel의 데이터를 관찰
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        // Google Map을 초기화
        binding.mapView.onCreate(savedInstanceState) // MapView의 생명주기 관리
        binding.mapView.getMapAsync(this) // 비동기로 Google Map 준비
    }

    private fun setupUI() {
        // UI 버튼 클릭 리스너 설정
        binding.searchButton.setOnClickListener {
            val query = binding.placeEditText.text.toString() // 입력된 검색어 가져오기
            if (query.isBlank()) {
                Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show() // 빈 검색어 경고
            } else {
                viewModel.searchPlace(query, binding.placeEditText) // 장소 검색
            }
        }

        binding.addButton.setOnClickListener {
            viewModel.addMarker() // 마커 추가
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteSelectedMarker() // 선택된 마커 삭제
        }

        setupMarkerRecyclerView() // 마커 RecyclerView 설정
    }

    private fun observeViewModel() {
        // ViewModel의 LiveData를 관찰하여 UI 업데이트
        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            updateMarkerList(markers.values.toList()) // 마커 목록 업데이트
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() // Toast 메시지 표시
            }
        }

        viewModel.searchedLocation.observe(viewLifecycleOwner) { location ->
            location?.let { moveCameraToLocation(it, 15f) } // 검색된 위치로 카메라 이동
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map // GoogleMap 객체 초기화
        viewModel.setGoogleMap(googleMap) // ViewModel에 GoogleMap 설정
        googleMap.setOnMarkerClickListener { marker ->
            viewModel.onMarkerClick(marker) // 마커 클릭 이벤트 처리
        }
        setupMapOptions() // 지도 옵션 설정
        viewModel.loadSavedPlaces() // 저장된 장소 로드
    }

    private fun setupMapOptions() {
        // 기본 위치로 카메라 이동
        moveCameraToLocation(viewModel.defaultLocation, 15f)
    }

    private fun moveCameraToLocation(location: LatLng, zoomLevel: Float) {
        // 주어진 위치로 카메라 이동
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    private fun setupMarkerRecyclerView() {
        // 마커 목록을 표시할 RecyclerView 설정
        binding.markerRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // 수직 레이아웃 매니저 설정
        planList2Adapter = PlanList2Adapter(emptyList()) { marker ->
            moveCameraToLocation(marker.position, 15f) // 마커 클릭 시 해당 위치로 카메라 이동
        }
        binding.markerRecyclerView.adapter = planList2Adapter // RecyclerView에 어댑터 설정
    }

    private fun updateMarkerList(markers: List<Marker>) {
        // RecyclerView 어댑터에 마커 목록 업데이트
        planList2Adapter.updateMarkers(markers)
    }

    // Fragment 생명주기 관리
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume() // MapView 재개
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause() // MapView 일시 중지
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy() // MapView 파괴
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory() // 메모리 부족 시 MapView 처리
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState) // MapView의 상태 저장
    }
}
