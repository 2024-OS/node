package com.example.node_project

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.ItemBudgetBinding
import com.example.node_project.models.BudgetItem


class BudgetAdapter(
    private var budgetList: MutableList<BudgetItem>, // RecyclerView에 표시할 예산 항목 리스트
    private val onItemUpdated: (Int, BudgetItem) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    //RecyclerView 데이터를 업데이트
    fun updateData(newList: MutableList<BudgetItem>) {
        budgetList = newList // 리스트를 새로운 데이터로 교체
        notifyDataSetChanged() // RecyclerView 갱신
    }

    //새로운 항목 추가
    fun addItem(item: BudgetItem) {
        budgetList.add(0, item) // 리스트의 맨 앞에 항목 추가
        notifyItemInserted(0) // 해당 위치의 항목 갱신
    }

    //체크된 항목 삭제
    fun deleteCheckedItems() {
        budgetList.removeAll { it.isChecked } // 체크된 항목 필터링 후 삭제
        notifyDataSetChanged() // RecyclerView 갱신
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding) // ViewHolder 반환
    }

    // ViewHolder와 데이터를 바인딩
    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = budgetList[position] // 현재 항목 가져오기
        holder.bind(item) // 항목 데이터 바인딩

        // CheckBox 상태 설정
        holder.binding.checkBox.setOnCheckedChangeListener(null) // 기존 리스너 제거
        holder.binding.checkBox.isChecked = item.isChecked // 현재 체크 상태 설정
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked // 체크 상태 업데이트
            onItemUpdated(position, item)
        }

        // EditText를 통해 항목 데이터 수정
        holder.binding.itemName.setText(item.itemName)
        holder.binding.itemName.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemName, "itemName"))

        holder.binding.itemQuantity.setText(item.itemQuantity)
        holder.binding.itemQuantity.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemQuantity, "itemQuantity"))

        holder.binding.itemPrice.setText(item.itemPrice)
        holder.binding.itemPrice.setOnEditorActionListener(createEditorActionListener(position, holder.binding.itemPrice, "itemPrice"))

        // 링크 입력 및 저장
        holder.binding.itemDescriptionEdit.setText(item.itemDescription)
        holder.binding.itemDescriptionEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val link = holder.binding.itemDescriptionEdit.text.toString()
                item.itemDescription = link // 항목의 설명(링크) 업데이트
                holder.binding.itemDescriptionEdit.visibility = View.GONE
                holder.binding.itemDescription.visibility = View.VISIBLE
                holder.binding.itemDescription.text = link // 텍스트 설정
                onItemUpdated(position, item)
                true
            } else {
                false
            }
        }

        // 링크 클릭 이벤트 처리
        holder.binding.itemDescription.setOnClickListener {
            val link = item.itemDescription
            if (link.isNotEmpty() && android.util.Patterns.WEB_URL.matcher(link).matches()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                it.context.startActivity(intent) // 유효한 링크라면 열기
            } else {
                Toast.makeText(it.context, "유효한 링크가 아닙니다.", Toast.LENGTH_SHORT).show() // 링크가 유효하지 않은 경우
            }
        }

        // 항목 설명 초기 상태 설정
        if (item.itemDescription.isNotEmpty()) {
            holder.binding.itemDescription.visibility = View.VISIBLE // 설명 표시
            holder.binding.itemDescriptionEdit.visibility = View.GONE // 설명 입력창 숨기기
        } else {
            holder.binding.itemDescription.visibility = View.GONE // 설명 숨기기
            holder.binding.itemDescriptionEdit.visibility = View.VISIBLE // 설명 입력창 표시
        }
    }

    //RecyclerView에 표시할 항목 개수를 반환
    override fun getItemCount(): Int = budgetList.size

    // 항목 데이터 수정 리스너 생성
    private fun createEditorActionListener(position: Int, textView: TextView, field: String): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val updatedText = textView.text.toString() // 수정된 텍스트 가져오기
                val updatedItem = budgetList[position] // 현재 항목 가져오기

                // 수정된 내용를 항목에 반영
                when (field) {
                    "itemName" -> updatedItem.itemName = updatedText
                    "itemQuantity" -> updatedItem.itemQuantity = updatedText
                    "itemPrice" -> updatedItem.itemPrice = updatedText
                }

                onItemUpdated(position, updatedItem)
                textView.clearFocus()
                true
            } else {
                false
            }
        }
    }

    //ViewHolder 클래스
    inner class BudgetViewHolder(val binding: ItemBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        // 항목 데이터를 ViewHolder에 바인딩
        fun bind(item: BudgetItem) {
            binding.itemName.setText(item.itemName) // 이름 설정
            binding.itemQuantity.setText(item.itemQuantity) // 수량 설정
            binding.itemPrice.setText(item.itemPrice) // 가격 설정
            binding.itemDescription.text = item.itemDescription // 설명 설정
        }
    }
}
