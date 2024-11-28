package com.example.node_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CashAdapter(
    private val itemList: List<Pair<String, String>>, // 날짜와 키를 저장
    private val onItemClick: (Pair<String, String>) -> Unit // 클릭 시 호출되는 함수
) : RecyclerView.Adapter<CashAdapter.CashViewHolder>() {

    inner class CashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        init {
            itemView.setOnClickListener {
                val item = itemList[adapterPosition] // 클릭된 날짜와 키를 가져옴
                onItemClick(item) // 클릭 시 onItemClick 호출
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cash, parent, false) // 날짜를 보여줄 뷰 레이아웃
        return CashViewHolder(view)
    }

    override fun onBindViewHolder(holder: CashViewHolder, position: Int) {
        val (date, _) = itemList[position] // 날짜만 표시
        holder.dateTextView.text = date
    }

    override fun getItemCount(): Int = itemList.size
}
