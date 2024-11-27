package com.example.node_project

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemBudgetBinding
import com.example.node_project.models.BudgetItem

class BudgetAdapter(
    private var budgetList: MutableList<BudgetItem>,
    private val onItemUpdated: (Int, BudgetItem) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    fun updateData(newList: MutableList<BudgetItem>) {
        budgetList = newList
        notifyDataSetChanged()
    }

    fun addItem(item: BudgetItem) {
        budgetList.add(0, item)
        notifyItemInserted(0)
    }

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

        // CheckBox 설정
        holder.binding.checkBox.setOnCheckedChangeListener(null)
        holder.binding.checkBox.isChecked = item.isChecked
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
            onItemUpdated(position, item)
        }

        // 물품, 수량, 가격 설정
        holder.binding.itemName.setText(item.itemName)
        holder.binding.itemName.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemName, "itemName"))

        holder.binding.itemQuantity.setText(item.itemQuantity)
        holder.binding.itemQuantity.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemQuantity, "itemQuantity"))

        holder.binding.itemPrice.setText(item.itemPrice)
        holder.binding.itemPrice.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemPrice, "itemPrice"))

        // 링크 입력 후 저장 및 표시
        holder.binding.itemDescriptionEdit.setText(item.itemDescription)
        holder.binding.itemDescriptionEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val link = holder.binding.itemDescriptionEdit.text.toString()
                item.itemDescription = link
                holder.binding.itemDescriptionEdit.visibility = View.GONE
                holder.binding.itemDescription.visibility = View.VISIBLE
                holder.binding.itemDescription.text = link
                onItemUpdated(position, item) // 업데이트
                true
            } else {
                false
            }
        }

        // 링크 클릭 이벤트
        holder.binding.itemDescription.setOnClickListener {
            val link = item.itemDescription
            if (link.isNotEmpty() && android.util.Patterns.WEB_URL.matcher(link).matches()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                it.context.startActivity(intent)
            } else {
                Toast.makeText(it.context, "유효한 링크가 아닙니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 초기 상태 설정
        if (item.itemDescription.isNotEmpty()) {
            holder.binding.itemDescription.visibility = View.VISIBLE
            holder.binding.itemDescriptionEdit.visibility = View.GONE
        } else {
            holder.binding.itemDescription.visibility = View.GONE
            holder.binding.itemDescriptionEdit.visibility = View.VISIBLE
        }
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
                }

                onItemUpdated(position, updatedItem)
                textView.clearFocus()
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
            binding.itemDescription.text = item.itemDescription
        }
    }
}
