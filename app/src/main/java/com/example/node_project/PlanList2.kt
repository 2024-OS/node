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

class PlanList2 : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentPlanList2Binding
    private val viewModel: PlanList2ViewModel by viewModels()
    private lateinit var googleMap: GoogleMap
    private lateinit var planMarkerListAdapter: PlanMarkerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // View Binding을 통해 레이아웃 인플레이트
        binding = FragmentPlanList2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState) 미사용
        setupMapView(savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        // MapView 초기화 및 지도 로드
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    private fun setupUI() {
        // 버튼 클릭 리스너 설정
        binding.searchButton.setOnClickListener {
            val query = binding.placeEditText.text.toString()  // 검색어 입력받기
            if (query.isBlank()) {
                Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.searchPlace(query)
            }
        }

        binding.addButton.setOnClickListener {
            viewModel.addMarker()
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteSelectedMarker()
        }

        setupMarkerRecyclerView()
    }

    private fun observeViewModel() {
        // ViewModel의 LiveData를 관찰하여 UI 업데이트
        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            updateMarkerList(markers.values.toList())
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.searchedLocation.observe(viewLifecycleOwner) { location ->
            location?.let { moveCameraToLocation(it, 15f) }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        viewModel.setGoogleMap(googleMap)
        googleMap.setOnMarkerClickListener { marker ->
            viewModel.onMarkerClick(marker)
        }
        setupMapOptions()
        viewModel.loadSavedPlaces()
    }

    private fun setupMapOptions() {
        // 초기 지도 설정
        moveCameraToLocation(viewModel.defaultLocation, 15f)
    }

    private fun moveCameraToLocation(location: LatLng, zoomLevel: Float) {
        // 클릭된 마커 위치로 이동
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    private fun setupMarkerRecyclerView() {
        // RecyclerView 설정
        binding.markerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        planMarkerListAdapter = PlanMarkerListAdapter(emptyList()) { marker ->
            moveCameraToLocation(marker.position, 15f)
        }
        binding.markerRecyclerView.adapter = planMarkerListAdapter
    }

    private fun updateMarkerList(markers: List<Marker>) {
        // 마커 목록 업데이트
        planMarkerListAdapter.updateMarkers(markers)
    }

    // MapView 생명주기 메서드들
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}
