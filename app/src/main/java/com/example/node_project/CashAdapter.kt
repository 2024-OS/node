package com.example.node_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CashAdapter(
    private val itemList: List<Pair<String, String>>, // (고유 키, 날짜)
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CashAdapter.CashViewHolder>() {

    inner class CashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        init {
            itemView.setOnClickListener {
                val key = itemList[adapterPosition].first // 고유 키 전달
                onItemClick(key)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cash, parent, false)
        return CashViewHolder(view)
    }

    override fun onBindViewHolder(holder: CashViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.dateTextView.text = currentItem.second // 날짜 표시
    }

    override fun getItemCount(): Int = itemList.size
}
