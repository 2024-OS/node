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

class BudgetFragment : Fragment() {

    private lateinit var binding: FragmentBudgetBinding
    private lateinit var budgetAdapter: BudgetAdapter
    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.budAddButton.setOnClickListener {
            val newItem = BudgetItem()
            viewModel.addBudgetItem(newItem)
        }

        binding.budDelButton.setOnClickListener {
            viewModel.deleteCheckedItems()
        }
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter(mutableListOf()) { position, updatedItem ->
            viewModel.updateBudgetItem(updatedItem.id, updatedItem) // ID를 기반으로 업데이트
        }
        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.budgetRecyclerView.adapter = budgetAdapter
    }

    private fun observeViewModel() {
        viewModel.budgetItems.observe(viewLifecycleOwner) { items ->
            budgetAdapter.updateData(items.toMutableList())
        }
    }
}
