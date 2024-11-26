package com.example.node_project

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemPlanBinding
import com.google.firebase.database.FirebaseDatabase

class PlanListAdapter(
    private val planList: MutableList<PlanItem>,
    private val onMapButtonClick: (PlanItem) -> Unit
) : RecyclerView.Adapter<PlanListAdapter.PlanViewHolder>() {

    private val database = FirebaseDatabase.getInstance().getReference("PlanItems")

    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlanItem) {
            binding.itemName.setText(item.title)
            binding.checkBox.isChecked = item.isChecked
            binding.scoreText.text = item.score.toString()

            // 제목 변경
            binding.itemName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { // Focus가 사라졌을 때만 Firebase 업데이트
                    item.title = binding.itemName.text.toString()
                    updateItemInFirebase(item)
                }
            }

            // 체크박스 상태 변경
            binding.checkBox.setOnCheckedChangeListener(null) // 기존 리스너 제거
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                updateItemInFirebase(item)
            }

            // 점수 올리기 버튼
            binding.heartButton.setOnClickListener {
                item.score += 1
                binding.scoreText.text = item.score.toString()
                updateItemInFirebase(item)
            }

            // 지도 버튼 클릭
            binding.mapButton2.setOnClickListener {
                onMapButtonClick(item)
            }
        }

        private fun updateItemInFirebase(item: PlanItem) {
            if (item.id.isNotEmpty()) {
                database.child(item.id).setValue(item)
                    .addOnFailureListener { exception ->
                        Log.e("PlanListAdapter", "Failed to update item: ${exception.message}")
                    }
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
}