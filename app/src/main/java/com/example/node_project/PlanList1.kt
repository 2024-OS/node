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

class PlanList1 : Fragment() {
    private lateinit var binding: FragmentPlanListBinding
    private lateinit var planListAdapter: PlanListAdapter
    private val viewModel: PlanList1ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupButtonListeners()
        observePlans()

        return binding.root
    }

    private fun setupRecyclerView() {
        planListAdapter = PlanListAdapter { item ->
            viewModel.updatePlan(item)
        }
        binding.recPlan.layoutManager = LinearLayoutManager(context)
        binding.recPlan.adapter = planListAdapter
    }

    private fun setupButtonListeners() {
        binding.addButtonPlan.setOnClickListener { addNewItem() }
        binding.deleteButton.setOnClickListener { deleteCheckedItems() }
        binding.mapButton.setOnClickListener { navigateToMap() }
    }

    private fun observePlans() {
        viewModel.plans.observe(viewLifecycleOwner) { plans ->
            planListAdapter.submitList(plans)
        }
    }

    private fun addNewItem() {
        val newItem = PlanList1Item(title = "새 장소") // 기본 제목으로 새 아이템 생성
        viewModel.addPlan(newItem)
    }

    private fun deleteCheckedItems() {
        val checkedItems = planListAdapter.getCheckedItems() // 체크된 아이템 가져오기
        viewModel.deletePlans(checkedItems)
    }

    private fun navigateToMap() {
        findNavController().navigate(R.id.action_planList_to_mapFrag) // 지도 화면으로 네비게이션
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearDeletedItems() // Fragment 파괴 시 삭제된 항목 정리
    }
}
