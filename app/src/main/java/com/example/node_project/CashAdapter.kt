// View (UI)
// RecyclerView의 Adapter로 각각 데이터 표시
// 클릭 하면 onItemClick으로 화면 이동
// ViewHolder 패턴으로 RecyclerView 항목 효율적으로 표시

package com.example.node_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 각각의 목록을 표시하는 View

class CashAdapter(
    private val itemList: List<Pair<String, String>>, // 아이템 리스트 (날짜, 키)
    private val onItemClick: (Pair<String, String>) -> Unit // 목록 클릭하면 호출
) : RecyclerView.Adapter<CashAdapter.CashViewHolder>() {


    // RecyclerView의 각 목록 처리
    inner class CashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { // 빠르게 재사용 할수 있는 ViewHolder 객체
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView) // 날짜를 찾아와서 표시하는거임

        init {
            // 각 항목을 클릭하면 호출
            itemView.setOnClickListener {
                val item = itemList[bindingAdapterPosition] // bindingAdapterPosition: 인덱션 데이터를 가져옴
                onItemClick(item) // 클릭한 목록 데이터를 onItemClick에 전달함
            }
        }
    }



    // RecyclerView 각 목록에 대한 데이터뷰를 inflate 함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashViewHolder { // RecyclerView의 부모뷰로 inflate할 때,
                                                                                        // parent를 전달해서 RecyclerView의 context 사용
        // item_cash.xml을 inflate 함
        val view = LayoutInflater.from(parent.context)  // RecyclerView의 부모 context 참조
            .inflate(R.layout.item_cash, parent, false) // 각 목록에 대한 레이아웃도 inflate
                                                                   // RecyclerView가 레이아웃은 부모뷰에 직접 추가 안해서 false
        return CashViewHolder(view) // (view = inflate View)를 기반으로 새로운 ViewHolder(각 항목을 화면에 표시하는 데 필요한 뷰를 저장하고 이용) 생성
    }



    // RecyclerView가 목록을 화면에 표시할 때 호출 됨
    // 각 목록의 데이터를 실제로 View에 연결 함
    override fun onBindViewHolder(holder: CashViewHolder, position: Int) {
        val (date, _) = itemList[position] // 목록에서 날짜만 가져옴
        holder.dateTextView.text = date // TextView에 날짜를 넣어서 화면에 표시함
    }



    // RecyclerView에 표시할 목록의 개수 반환
    override fun getItemCount(): Int = itemList.size // 아이템 리스트의 크기를 반환
}
