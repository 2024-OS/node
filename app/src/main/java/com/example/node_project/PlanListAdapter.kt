package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemPlanBinding

class PlanListAdapter(private val planList: MutableList<PlanItem>) :
    RecyclerView.Adapter<PlanListAdapter.PlanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val item = planList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = planList.size

    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlanItem) {
            binding.itemName.setText(item.title)
            binding.checkBox.isChecked = item.isChecked

            // 체크박스 상태 변경 리스너
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }
        }
    }

    // 체크된 항목 가져오기
    fun getCheckedItems(): List<PlanItem> {
        return planList.filter { it.isChecked }
    }

    // 항목 추가
    fun addItem(item: PlanItem) {
        planList.add(item)
        notifyItemInserted(planList.size - 1)
    }

    // 항목 삭제
    fun removeItem(item: PlanItem) {
        val index = planList.indexOfFirst { it.title == item.title }
        if (index != -1) {
            planList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    // 전체 항목 업데이트
    fun updateAllItems(newItems: List<PlanItem>) {
        planList.clear()
        planList.addAll(newItems)
        notifyDataSetChanged() // 전체 리스트 갱신
    }
}
