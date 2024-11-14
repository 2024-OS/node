package com.example.node_project

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemBudgetBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BudgetAdapter(private val budgetList: MutableList<BudgetItem>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    // Firebase Database 참조
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("budget_items")

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
            binding.itemLink.setText(item.itemLink) // 링크 입력란 추가
            binding.checkBox.isChecked = item.isChecked

            // 체크박스 상태 변경 리스너
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                database.child(item.id).child("isChecked").setValue(isChecked)
            }

            // 이름 변경 시 Firebase 업데이트
            binding.itemName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.itemName = s.toString()
                    database.child(item.id).child("itemName").setValue(item.itemName)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // 수량 변경 시 Firebase 업데이트
            binding.itemQuantity.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.itemQuantity = s.toString()
                    database.child(item.id).child("itemQuantity").setValue(item.itemQuantity)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // 가격 변경 시 Firebase 업데이트
            binding.itemPrice.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.itemPrice = s.toString()
                    database.child(item.id).child("itemPrice").setValue(item.itemPrice)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // 링크 변경 시 Firebase 업데이트 (추가된 부분)
            binding.itemLink.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.itemLink = s.toString()
                    database.child(item.id).child("itemLink").setValue(item.itemLink)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    // 전체 항목 업데이트
    fun updateAllItems(newItems: List<BudgetItem>) {
        budgetList.clear()
        budgetList.addAll(newItems)
        notifyDataSetChanged() // 전체 리스트 갱신
    }

    // 항목 추가
    fun addItem(item: BudgetItem) {
        budgetList.add(item)
        notifyItemInserted(budgetList.size - 1)
    }

    // 항목 삭제
    fun removeItem(item: BudgetItem) {
        val index = budgetList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            budgetList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    // 체크된 항목 가져오기
    fun getCheckedItems(): List<BudgetItem> {
        return budgetList.filter { it.isChecked }
    }
}
