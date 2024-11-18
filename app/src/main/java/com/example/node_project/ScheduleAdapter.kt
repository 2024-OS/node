package com.example.node_project

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.models.ScheduleItem

class ScheduleAdapter(
    private val scheduleList: MutableList<ScheduleItem>,
    private val onItemChecked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskEditText: EditText = itemView.findViewById(R.id.taskTextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val task = scheduleList[position]

        // EditText 초기화
        holder.taskEditText.setText(task.task)

        // 체크박스 초기화
        holder.checkBox.isChecked = task.isChecked

        // EditText 클릭 이벤트
        holder.taskEditText.setOnClickListener {
            holder.taskEditText.isFocusableInTouchMode = true
            holder.taskEditText.isFocusable = true
            holder.taskEditText.requestFocus()

            // 키보드 표시
            val imm = holder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(holder.taskEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        // EditText 엔터 키 이벤트
        holder.taskEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val updatedText = holder.taskEditText.text.toString()
                scheduleList[position].task = updatedText

                // 키보드 숨김
                val imm = holder.itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(holder.taskEditText.windowToken, 0)

                // EditText 비활성화
                holder.taskEditText.isFocusable = false
                holder.taskEditText.isFocusableInTouchMode = false

                true
            } else {
                false
            }
        }

        // 체크박스 변경 이벤트
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isChecked = isChecked
            onItemChecked(position, isChecked)
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    fun updateData(newList: MutableList<ScheduleItem>) {
        scheduleList.clear()
        scheduleList.addAll(newList)
        notifyDataSetChanged()
    }
}
