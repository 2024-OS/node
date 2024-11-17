package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentPlanListBinding

class PlanList : Fragment() {

    private lateinit var binding: FragmentPlanListBinding
    private lateinit var planListAdapter: PlanListAdapter
    private val itemList = mutableListOf<PlanItem>() // 계획 항목 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanListBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        planListAdapter = PlanListAdapter(itemList) // 어댑터에 항목 리스트 전달
        binding.recPlan.layoutManager = LinearLayoutManager(context)
        binding.recPlan.adapter = planListAdapter

        // 추가 버튼 클릭 시 새 항목 추가
        binding.addButtonPlan.setOnClickListener {
            addNewItem() // 새 계획 항목 추가
        }

        // 삭제 버튼 클릭 시 체크된 항목 삭제
        binding.deleteButton.setOnClickListener {
            deleteCheckedItems() // 체크된 항목 삭제
        }

        return binding.root
    }

    // 새로운 항목 추가
    private fun addNewItem() {
        val newItem = PlanItem(title = "새 계획", description = "")
        itemList.add(newItem) // 새 계획 항목을 리스트에 추가
        planListAdapter.notifyItemInserted(itemList.size - 1) // 어댑터에 변경 사항 알림
    }

    // 체크된 항목만 삭제하는 함수
    private fun deleteCheckedItems() {
        val checkedItems = planListAdapter.getCheckedItems() // 체크된 항목 가져오기
        for (item in checkedItems) {
            itemList.remove(item) // 리스트에서 항목 삭제
        }
        planListAdapter.notifyDataSetChanged() // 어댑터에 변경 사항 알림
    }
}
