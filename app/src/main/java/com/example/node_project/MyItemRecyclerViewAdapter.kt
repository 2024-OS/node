package com.example.node_project

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import com.example.node_project.models.BudgetItem
import com.example.node_project.databinding.ItemBudgetBinding


class MyItemRecyclerViewAdapter(
    private val values: List<BudgetItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // item_budget.xml의 바인딩 클래스를 사용하여 ViewHolder를 초기화
        return ViewHolder(
            ItemBudgetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.itemName.setText(item.itemName)
        holder.itemQuantity.setText(item.itemQuantity)
        holder.itemPrice.setText(item.itemPrice)
        holder.checkBox.isChecked = item.isChecked
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        val checkBox: CheckBox = binding.checkBox
        val itemName: EditText = binding.itemName
        val itemQuantity: EditText = binding.itemQuantity
        val itemPrice: EditText = binding.itemPrice

        override fun toString(): String {
            return super.toString() + " '" + itemName.text + "'"
        }
    }
}
