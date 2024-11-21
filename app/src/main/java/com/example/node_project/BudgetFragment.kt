package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {

    private lateinit var binding: FragmentBudgetBinding
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetBinding.inflate(inflater, container, false)
        val view = binding.root

        // RecyclerView 초기화
        budgetAdapter = BudgetAdapter(mutableListOf())
        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.budgetRecyclerView.adapter = budgetAdapter

        // 추가 버튼을 눌러 아이템 추가
        binding.budAddButton.setOnClickListener {
            budgetAdapter.addItem(BudgetItem())
            binding.budgetRecyclerView.scrollToPosition(0) // 리스트 맨 위로 스크롤
        }

        // 삭제 버튼 클릭 리스너 설정 (예: 체크된 항목 삭제)
        binding.budDelButton.setOnClickListener {
            budgetAdapter.deleteCheckedItems()
        }

        return view
    }
}
