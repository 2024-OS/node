package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentPlanListBinding
import androidx.navigation.fragment.findNavController

class PlanList : Fragment() {

    private lateinit var binding: FragmentPlanListBinding
    private lateinit var planListAdapter: PlanListAdapter
    private val itemList = mutableListOf<PlanItem>() // 계획 항목 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanListBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        setupRecyclerView()
        // 버튼 리스너 설정
        setupButtonListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        planListAdapter = PlanListAdapter(itemList) // 어댑터에 항목 리스트 전달
        binding.recPlan.layoutManager = LinearLayoutManager(context) // 레이아웃 매니저 설정
        binding.recPlan.adapter = planListAdapter // 어댑터 설정
    }

    private fun setupButtonListeners() {
        binding.addButtonPlan.setOnClickListener { addNewItem() } // 추가 버튼 클릭 이벤트
        binding.deleteButton.setOnClickListener { deleteCheckedItems() } // 삭제 버튼 클릭 이벤트
        binding.mapButton.setOnClickListener { navigateToMap() } // 지도 버튼 클릭 이벤트
    }

    private fun addNewItem() {
        val newItem = PlanItem(title = "새 계획") // 새 계획 항목 생성
        itemList.add(newItem) // 리스트에 추가
        planListAdapter.notifyItemInserted(itemList.size - 1) // 어댑터에 변경 사항 알림
    }

    private fun deleteCheckedItems() {
        // 체크된 항목 가져오기 및 삭제
        val checkedItems = planListAdapter.getCheckedItems()
        itemList.removeAll(checkedItems) // 리스트에서 체크된 항목 삭제
        planListAdapter.notifyDataSetChanged() // 어댑터에 변경 사항 알림
    }

    private fun navigateToMap() {
        findNavController().navigate(R.id.action_planList_to_mapFrag) // 새로운 프래그먼트로 이동
    }
}
