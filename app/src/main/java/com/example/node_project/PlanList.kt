package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentPlanListBinding


class PlanList : Fragment() {
    // View Binding을 위한 변수
    private lateinit var binding: FragmentPlanListBinding
    // RecyclerView 어댑터
    private lateinit var planListAdapter: PlanListAdapter
    // ViewModel 인스턴스 생성 (by viewModels()는 Kotlin 속성 위임을 사용)
    private val viewModel: PlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // View Binding을 통해 레이아웃 인플레이트
        binding = FragmentPlanListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupButtonListeners()
        observePlans()

        return binding.root
    }

    private fun setupRecyclerView() {
        // RecyclerView 어댑터 초기화 및 설정
        planListAdapter = PlanListAdapter { item ->
            viewModel.updatePlan(item)
        }
        binding.recPlan.layoutManager = LinearLayoutManager(context)
        binding.recPlan.adapter = planListAdapter
    }

    private fun setupButtonListeners() {
        // 버튼 클릭 리스너 설정
        binding.addButtonPlan.setOnClickListener { addNewItem() }
        binding.deleteButton.setOnClickListener { deleteCheckedItems() }
        binding.mapButton.setOnClickListener { navigateToMap() }
    }

    private fun observePlans() {
        // ViewModel의 plans LiveData 관찰
        viewModel.plans.observe(viewLifecycleOwner, Observer { plans ->
            planListAdapter.submitList(plans)
        })
    }

    private fun addNewItem() {
        // 새 항목 추가
        val newItem = PlanItem(title = "새 장소")
        viewModel.addPlan(newItem)
    }

    private fun deleteCheckedItems() {
        // 체크된 항목 삭제
        val checkedItems = planListAdapter.getCheckedItems()
        viewModel.deletePlans(checkedItems)
    }

    private fun navigateToMap() {
        // 지도 화면으로 네비게이션
        findNavController().navigate(R.id.action_planList_to_mapFrag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment 파괴 시 삭제된 항목 정리
        viewModel.clearDeletedItems()
    }
}
