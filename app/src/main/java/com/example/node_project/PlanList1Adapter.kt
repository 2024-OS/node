package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemPlanBinding

class PlanListAdapter(private val onItemUpdated: (PlanList1Item) -> Unit)
    : ListAdapter<PlanList1Item, PlanListAdapter.PlanViewHolder>(PlanDiffCallback()) {

    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlanList1Item) {
            // 아이템 데이터를 뷰에 바인딩
            binding.itemName.setText(item.title)
            binding.scoreText.text = item.score.toString()

            // 체크박스 리스너 설정
            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.isChecked = item.isChecked
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                onItemUpdated(item)
            }

            // 하트 버튼 클릭 리스너 설정
            binding.heartButton.setOnClickListener {
                item.score += 1
                binding.scoreText.text = item.score.toString()
                onItemUpdated(item)
            }

            // 아이템 이름 변경 리스너 설정
            binding.itemName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    item.title = binding.itemName.text.toString()
                    onItemUpdated(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // 체크된 아이템 반환
    fun getCheckedItems(): List<PlanList1Item> = currentList.filter { it.isChecked }
}

// DiffUtil을 사용한 리스트 업데이트 최적화
class PlanDiffCallback : DiffUtil.ItemCallback<PlanList1Item>() {
    override fun areItemsTheSame(oldItem: PlanList1Item, newItem: PlanList1Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlanList1Item, newItem: PlanList1Item): Boolean {
        return oldItem == newItem
    }
}