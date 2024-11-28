package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemPlanBinding

class PlanListAdapter(private val planList: MutableList<PlanItem>,
                      private val onMapButtonClick: (PlanItem) -> Unit) : RecyclerView.Adapter<PlanListAdapter.PlanViewHolder>() {
    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlanItem) {
            binding.itemName.setText(item.title)            // 자동으로 수정 가능 상태로 설정됨
            binding.checkBox.isChecked = item.isChecked     // 체크박스 상태 설정
            binding.scoreText.text = item.score.toString()  // 점수 표시

            // 체크박스 리스너 설정
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked // 체크 상태 업데이트
            }

            // 하트 버튼 클릭 리스너 설정
            binding.heartButton.setOnClickListener {
                item.score += 1 // 점수 증가
                binding.scoreText.text = item.score.toString() // 점수 업데이트
            }
        }
    }


    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    // ViewHolder와 데이터 바인딩
    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val item = planList[position]
        holder.bind(item) // 바인딩 메소드 호출
    }

    // 아이템 수 반환
    override fun getItemCount(): Int = planList.size

    // 체크된 항목 반환
    fun getCheckedItems(): List<PlanItem> = planList.filter { it.isChecked }

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

    // 모든 항목 업데이트
    fun updateAllItems(newItems: List<PlanItem>) {
        planList.clear()
        planList.addAll(newItems)
        notifyDataSetChanged() // 전체 데이터 갱신
    }
}
