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
    private val itemList = mutableListOf<PlanItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupButtonListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        planListAdapter = PlanListAdapter(itemList) { item ->
            navigateToMap(item)
        }
        binding.recPlan.layoutManager = LinearLayoutManager(context)
        binding.recPlan.adapter = planListAdapter
    }

    private fun setupButtonListeners() {
        binding.addButtonPlan.setOnClickListener { addNewItem() }
        binding.deleteButton.setOnClickListener { deleteCheckedItems() }
        binding.mapButton.setOnClickListener { navigateToMap() }
    }

    private fun addNewItem() {
        val newItem = PlanItem(title = "새 장소")
        itemList.add(newItem)
        planListAdapter.notifyItemInserted(itemList.size - 1)
    }

    private fun deleteCheckedItems() {
        val checkedItems = planListAdapter.getCheckedItems()
        itemList.removeAll(checkedItems)
        planListAdapter.notifyDataSetChanged()
    }

    private fun navigateToMap(item: PlanItem? = null) {
        findNavController().navigate(R.id.action_planList_to_mapFrag) // 지도 프래그먼트로 이동
    }
}
