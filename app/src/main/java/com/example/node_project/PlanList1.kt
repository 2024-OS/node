package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentPlanListBinding

// 계획 목록을 표시하는 Fragment 클래스
class PlanList1 : Fragment() {
    private lateinit var binding: FragmentPlanListBinding // Fragment와 XML 레이아웃을 연결하는 바인딩 객체
    private lateinit var planListAdapter: PlanListAdapter // RecyclerView에 사용할 어댑터
    private val viewModel: PlanList1ViewModel by viewModels() // ViewModel을 통해 UI 관련 데이터를 관리

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Fragment의 UI를 생성
        binding = FragmentPlanListBinding.inflate(inflater, container, false) // XML 레이아웃을 인플레이트

        setupRecyclerView() // RecyclerView 설정
        setupButtonListeners() // 버튼 클릭 리스너 설정
        observePlans() // 계획 목록 변화를 관찰

        return binding.root // Fragment의 뷰 반환
    }

    private fun setupRecyclerView() {
        // RecyclerView의 어댑터를 설정하고 레이아웃 매니저를 정의
        planListAdapter = PlanListAdapter { item -> // 아이템 업데이트를 위한 람다 함수
            viewModel.updatePlan(item) // ViewModel을 통해 아이템 업데이트
        }
        binding.recPlan.layoutManager = LinearLayoutManager(context) // 수직으로 나열되는 레이아웃 매니저 설정
        binding.recPlan.adapter = planListAdapter // RecyclerView에 어댑터 설정
    }

    private fun setupButtonListeners() {
        // 버튼 클릭 시 각각의 기능 수행
        binding.addButtonPlan.setOnClickListener { addNewItem() } // 새 아이템 추가
        binding.deleteButton.setOnClickListener { deleteCheckedItems() } // 체크된 아이템 삭제
        binding.mapButton.setOnClickListener { navigateToMap() } // 지도 화면으로 이동
    }

    private fun observePlans() {
        // ViewModel의 계획 목록을 관찰하여 변경 시 어댑터에 데이터 전달
        viewModel.plans.observe(viewLifecycleOwner) { plans ->
            planListAdapter.updatePlans(plans) // 어댑터에 새로운 계획 목록 제출
        }
    }

    private fun addNewItem() {
        // 새로운 계획 아이템을 추가하는 메서드
        val newItem = PlanList1Item(title = "새 장소") // 기본 제목으로 새 아이템 생성
        viewModel.addPlan(newItem) // ViewModel에 새 계획 추가 요청
        // ViewModel -> PlanList1Repository (addPlan 메서드 호출)
    }

    private fun deleteCheckedItems() {
        // 체크된 아이템을 삭제하는 메서드
        val checkedItems = planListAdapter.getCheckedItems() // 체크된 아이템 가져오기
        viewModel.deletePlans(checkedItems) // ViewModel에 삭제 요청
        // ViewModel -> PlanList1Repository (deletePlans 메서드 호출)
    }

    private fun navigateToMap() {
        // 지도 화면으로 네비게이션하는 메서드
        findNavController().navigate(R.id.action_planList_to_mapFrag) // 지도 화면으로 네비게이션
    }

    override fun onDestroyView() { // 메모리 누수 방지
        super.onDestroyView()
        viewModel.clearDeletedItems() // Fragment 파괴 시 삭제된 항목 정리
    }
}
