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
    private lateinit var binding: FragmentPlanListBinding
    private lateinit var planListAdapter: PlanListAdapter
    private val viewModel: PlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        viewModel.plans.observe(viewLifecycleOwner, Observer { plans ->
            planListAdapter.submitList(plans)
        })
    }

    private fun addNewItem() {
        val newItem = PlanItem(title = "새 장소")
        viewModel.addPlan(newItem)
    }

    private fun deleteCheckedItems() {
        val checkedItems = planListAdapter.getCheckedItems()
        viewModel.deletePlans(checkedItems)
    }

    private fun navigateToMap() {
        findNavController().navigate(R.id.action_planList_to_mapFrag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearDeletedItems()
    }
}
