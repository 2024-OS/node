package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentBudgetBinding
import com.google.firebase.database.*

class BudgetFragment : Fragment() {

    private lateinit var binding: FragmentBudgetBinding
    private lateinit var budgetAdapter: BudgetAdapter
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetBinding.inflate(inflater, container, false)

        // Firebase Database 참조
        database = FirebaseDatabase.getInstance().reference.child("budget_items")

        // RecyclerView 설정
        budgetAdapter = BudgetAdapter(mutableListOf())
        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.budgetRecyclerView.adapter = budgetAdapter

        // Firebase에서 항목 불러오기
        loadInitialData()

        // 추가 버튼 클릭 시 새 항목 추가
        binding.budAddButton.setOnClickListener {
            addNewItem()
        }

        // 삭제 버튼 클릭 시 체크된 항목 삭제
        binding.budDelButton.setOnClickListener {
            deleteCheckedItemsFromFirebase()
        }

        return binding.root
    }

    // Firebase에서 모든 항목 불러오기
    private fun loadInitialData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<BudgetItem>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(BudgetItem::class.java)
                    item?.let { items.add(it) }
                }
                budgetAdapter.updateAllItems(items)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 새로운 항목 추가
    private fun addNewItem() {
        val newItemId = database.push().key ?: return
        val newItem = BudgetItem(id = newItemId, itemName = "새 물품", itemQuantity = "1", itemPrice = "100")
        database.child(newItemId).setValue(newItem).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                budgetAdapter.addItem(newItem)
            }
        }
    }

    // 체크된 항목만 삭제하는 함수
    private fun deleteCheckedItemsFromFirebase() {
        val checkedItems = budgetAdapter.getCheckedItems()
        for (item in checkedItems) {
            database.child(item.id).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    budgetAdapter.removeItem(item)
                }
            }
        }
    }
}
