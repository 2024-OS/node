package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemPlanBinding

// RecyclerView의 어댑터 클래스: 계획 아이템을 표시하고 관리
class PlanList1Adapter(private val onItemUpdated: (PlanList1Item) -> Unit) : RecyclerView.Adapter<PlanList1Adapter.PlanViewHolder>() {

    private var planList: List<PlanList1Item> = listOf() // 계획 목록

    // ViewHolder 클래스: RecyclerView의 각 아이템을 표현
    inner class PlanViewHolder(private val binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlanList1Item) {
            // 아이템 데이터를 뷰에 바인딩
            binding.itemName.setText(item.title) // 제목 설정
            binding.scoreText.text = item.score.toString() // 점수 표시

            // 체크박스 리스너 설정
            binding.checkBox.setOnCheckedChangeListener(null) // 기존 리스너 제거
            binding.checkBox.isChecked = item.isChecked // 체크박스 상태 설정
            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked // 체크 상태 업데이트
                onItemUpdated(item) // 아이템 업데이트 호출
            }

            // 하트 버튼 클릭 리스너 설정
            binding.heartButton.setOnClickListener {
                item.score += 1 // 점수 증가
                binding.scoreText.text = item.score.toString() // 점수 업데이트
                onItemUpdated(item) // 아이템 업데이트 호출
            }

            // 아이템 이름 변경 리스너 설정
            binding.itemName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { // 포커스가 떨어지면
                    item.title = binding.itemName.text.toString() // 제목 업데이트
                    onItemUpdated(item) // 아이템 업데이트 호출
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        // 각 아이템 뷰 생성
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding) // ViewHolder 반환
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(planList[position]) // ViewHolder에 아이템 바인딩
    }

    override fun getItemCount(): Int {
        return planList.size // 아이템 수 반환
    }

    // 계획 목록 업데이트
    fun updatePlans(newPlans: List<PlanList1Item>) {
        planList = newPlans // 새로운 계획 목록으로 업데이트
        notifyDataSetChanged() // 전체 목록을 갱신
    }

    // 체크된 아이템 반환
    fun getCheckedItems(): List<PlanList1Item> = planList.filter { it.isChecked } // 체크된 아이템 목록 필터링
}
