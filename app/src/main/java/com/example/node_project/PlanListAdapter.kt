package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemPlanBinding

class PlanListAdapter(private val onItemUpdated: (PlanItem) -> Unit) : ListAdapter<PlanItem, PlanListAdapter.PlanViewHolder>(PlanDiffCallback()) {
    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlanItem) {
            binding.itemName.setText(item.title)
            binding.scoreText.text = item.score.toString()

            // 체크박스 리스너 임시 제거
            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = item.isChecked
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                onItemUpdated(item)
            }


            binding.heartButton.setOnClickListener {
                item.score += 1
                binding.scoreText.text = item.score.toString()
                onItemUpdated(item)
            }

            binding.itemName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    item.title = binding.itemName.text.toString()
                    onItemUpdated(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getCheckedItems(): List<PlanItem> = currentList.filter { it.isChecked }
}

class PlanDiffCallback : DiffUtil.ItemCallback<PlanItem>() {
    override fun areItemsTheSame(oldItem: PlanItem, newItem: PlanItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlanItem, newItem: PlanItem): Boolean {
        return oldItem == newItem
    }
}