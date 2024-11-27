package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemBudgetBinding
import com.example.node_project.models.BudgetItem

class BudgetAdapter(
    private var budgetList: MutableList<BudgetItem>,
    private val onItemUpdated: (Int, BudgetItem) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    fun updateData(newList: MutableList<BudgetItem>) {
        budgetList = newList
        notifyDataSetChanged() // 데이터 전체 업데이트
    }

    fun addItem(item: BudgetItem) {
        budgetList.add(0, item) // 새 아이템을 맨 위에 추가
        notifyItemInserted(0) // 특정 위치만 갱신
    }

    fun deleteCheckedItems() {
        budgetList.removeAll { it.isChecked }
        notifyDataSetChanged() // 전체 리스트 갱신
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = budgetList[position]
        holder.bind(item)

        // 기존 리스너 제거 후 CheckBox 상태 설정
        holder.binding.checkBox.setOnCheckedChangeListener(null)
        holder.binding.checkBox.isChecked = item.isChecked

        // CheckBox 클릭 리스너 등록
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
            onItemUpdated(position, item) // 변경된 항목 업데이트
        }

        // EditText 필드에 데이터 설정 및 업데이트 리스너
        holder.binding.itemName.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemName, "itemName"))
        holder.binding.itemQuantity.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemQuantity, "itemQuantity"))
        holder.binding.itemPrice.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemPrice, "itemPrice"))
        holder.binding.itemDescription.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemDescription, "itemDescription"))
    }

    override fun getItemCount(): Int = budgetList.size

    private fun createEditorActionListener(position: Int, textView: TextView, field: String): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val updatedText = textView.text.toString()
                val updatedItem = budgetList[position]

                when (field) {
                    "itemName" -> updatedItem.itemName = updatedText
                    "itemQuantity" -> updatedItem.itemQuantity = updatedText
                    "itemPrice" -> updatedItem.itemPrice = updatedText
                    "itemDescription" -> updatedItem.itemDescription = updatedText
                }

                onItemUpdated(position, updatedItem) // 수정된 데이터 업데이트
                textView.clearFocus() // 포커스 해제
                true
            } else {
                false
            }
        }
    }

    inner class BudgetViewHolder(val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BudgetItem) {
            binding.itemName.setText(item.itemName)
            binding.itemQuantity.setText(item.itemQuantity)
            binding.itemPrice.setText(item.itemPrice)
            binding.itemDescription.setText(item.itemDescription)
        }
    }
}
