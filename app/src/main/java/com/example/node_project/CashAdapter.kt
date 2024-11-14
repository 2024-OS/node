package com.example.node_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CashAdapter(
    private val itemList: List<CashItem>,
    private val onItemClick: (CashItem) -> Unit
) : RecyclerView.Adapter<CashAdapter.CashViewHolder>() {

    inner class CashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val item = itemList[adapterPosition]  // adapterPosition 사용
                onItemClick(item)  // 클릭 시 onItemClick 호출
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashViewHolder {
        // 텍스트 뷰를 동적으로 생성
        val textView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textSize = 16f
            setPadding(16, 16, 16, 16)
        }
        return CashViewHolder(textView)
    }

    override fun onBindViewHolder(holder: CashViewHolder, position: Int) {
        val currentItem = itemList[position]
        (holder.itemView as TextView).text = currentItem.date  // 예시로 날짜만 표시
    }

    override fun getItemCount(): Int = itemList.size
}
