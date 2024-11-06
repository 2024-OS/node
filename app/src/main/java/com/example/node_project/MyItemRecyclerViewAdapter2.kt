package com.example.node_project

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.node_project.placeholder.PlaceholderContent.PlaceholderItem
import com.example.node_project.databinding.FragmentPlanListBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 */
class MyItemRecyclerViewAdapter2(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentPlanListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        // 각 항목의 내용을 필요에 맞게 설정
        holder.binding.root.setOnClickListener {
            // 클릭 이벤트 등 필요시 추가
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: FragmentPlanListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // itemNumber와 content 관련 참조 제거
    }
}
