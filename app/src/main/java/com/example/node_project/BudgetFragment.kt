package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentBudgetBinding
import com.example.node_project.models.BudgetItem
import com.example.node_project.viewmodel.BudgetViewModel


//예산 관리 화면을 구성하는 Fragment 클래스
class BudgetFragment : Fragment() {

    private lateinit var binding: FragmentBudgetBinding // View Binding 객체
    private lateinit var budgetAdapter: BudgetAdapter // RecyclerView 어댑터
    private val viewModel: BudgetViewModel by viewModels() // BudgetViewModel 객체를 생성 및 연결

    //Fragment의 뷰를 생성
    override fun onCreateView(
        inflater: LayoutInflater, // XML 레이아웃을 View 객체로 변환
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBudgetBinding.inflate(inflater, container, false) // View Binding 초기화
        return binding.root // Fragment의 루트 뷰 반환
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView() // RecyclerView 초기화
        observeViewModel() // ViewModel의 데이터를 관찰하고 UI 업데이트

        // '추가' 버튼 클릭 리스너
        binding.budAddButton.setOnClickListener {
            val newItem = BudgetItem() // 빈 BudgetItem 객체 생성
            viewModel.addBudgetItem(newItem) // ViewModel을 통해 Firebase에 항목 추가
        }

        // '삭제' 버튼 클릭 리스너
        binding.budDelButton.setOnClickListener {
            viewModel.deleteCheckedItems() // ViewModel을 통해 체크된 항목 삭제
        }
    }

    // RecyclerView를 초기화
    private fun setupRecyclerView() {
        // 어댑터 초기
        budgetAdapter = BudgetAdapter(mutableListOf()) { position, updatedItem ->
            // ViewModel을 통해 항목 업데이트
            viewModel.updateBudgetItem(updatedItem.id, updatedItem)
        }
        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // RecyclerView에 어댑터 연결
        binding.budgetRecyclerView.adapter = budgetAdapter
    }

    //ViewModel 데이터를 관찰하고 UI를 업데이트
    private fun observeViewModel() {
        viewModel.budgetItems.observe(viewLifecycleOwner) { items ->
            // RecyclerView 어댑터 데이터를 업데이트
            budgetAdapter.updateData(items.toMutableList())
        }
    }
}
