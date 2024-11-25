package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemPlanBinding

class PlanListAdapter(
    private val planList: MutableList<PlanItem>,
    private val onMapButtonClick: (PlanItem) -> Unit
) : RecyclerView.Adapter<PlanListAdapter.PlanViewHolder>() {

    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlanItem) {
            binding.itemName.setText(item.title)
            binding.checkBox.isChecked = item.isChecked
            binding.scoreText.text = item.score.toString()

            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }

            binding.heartButton.setOnClickListener(null)
            binding.heartButton.setOnClickListener {
                item.score += 1
                binding.scoreText.text = item.score.toString()
            }

            binding.mapButton2.setOnClickListener {
                onMapButtonClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val item = planList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = planList.size

    fun getCheckedItems(): List<PlanItem> = planList.filter { it.isChecked }

    fun addItem(item: PlanItem) {
        planList.add(item)
        notifyItemInserted(planList.size - 1)
    }

    fun removeItem(item: PlanItem) {
        val index = planList.indexOfFirst { it.title == item.title }
        if (index != -1) {
            planList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateAllItems(newItems: List<PlanItem>) {
        planList.clear()
        planList.addAll(newItems)
        notifyDataSetChanged()
    }
}
