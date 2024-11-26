package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentPlanListBinding
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast

class PlanList : Fragment() {
    private lateinit var binding: FragmentPlanListBinding
    private lateinit var planListAdapter: PlanListAdapter
    private lateinit var database: DatabaseReference
    private val itemList = mutableListOf<PlanItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanListBinding.inflate(inflater, container, false)

        // Firebase 초기화
        database = FirebaseDatabase.getInstance().getReference("PlanItems")

        setupRecyclerView()
        setupButtonListeners()

        // Firebase 데이터 로드
        loadDataFromFirebase()

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
        val uniqueKey = database.push().key ?: return // 고유 키 생성

        newItem.id = uniqueKey // PlanItem에 고유 키 저장

        // Firebase에 데이터 저장
        database.child(uniqueKey).setValue(newItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    itemList.add(newItem)
                    planListAdapter.notifyItemInserted(itemList.size - 1)
                    showToast("데이터 생성 성공")
                } else {
                    showToast("데이터 생성 실패: ${task.exception?.message}")
                }
            }
    }

    private fun deleteCheckedItems() {
        val checkedItems = planListAdapter.getCheckedItems()

        checkedItems.forEach { item ->
            if (item.id.isNotEmpty()) {
                database.child(item.id).removeValue() // Firebase에서 삭제
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val position = itemList.indexOf(item)
                            itemList.remove(item)
                            planListAdapter.notifyItemRemoved(position)
                            showToast("삭제 성공")
                        } else {
                            showToast("삭제 실패: ${task.exception?.message}")
                        }
                    }
            }
        }
    }

    private fun loadDataFromFirebase() {
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val loadedItems = snapshot.children.mapNotNull {
                    val item = it.getValue(PlanItem::class.java)
                    item?.apply { id = it.key ?: "" } // Firebase 키를 PlanItem에 저장
                }
                itemList.clear()
                itemList.addAll(loadedItems)
                planListAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener { exception ->
            showToast("데이터 로드 실패: ${exception.message}")
        }
    }

    private fun navigateToMap(item: PlanItem? = null) {
        findNavController().navigate(R.id.action_planList_to_mapFrag) // 지도 프래그먼트로 이동
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}