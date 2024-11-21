package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemBudgetBinding

class BudgetAdapter(private val budgetList: MutableList<BudgetItem>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    // 아이템을 맨 위에 추가
    fun addItem(item: BudgetItem) {
        budgetList.add(0, item)
        notifyItemInserted(0)
    }

    // 체크된 항목 삭제
    fun deleteCheckedItems() {
        budgetList.removeAll { it.isChecked }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = budgetList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = budgetList.size

    inner class BudgetViewHolder(private val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BudgetItem) {
            binding.itemName.setText(item.itemName)
            binding.itemQuantity.setText(item.itemQuantity)
            binding.itemPrice.setText(item.itemPrice)
            binding.checkBox.isChecked = item.isChecked
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }
        }
    }
}
