package com.example.node_project

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.models.ScheduleItem

class ScheduleAdapter(
    private val scheduleList: List<ScheduleItem>,
    private val onItemChecked: (Int, Boolean) -> Unit, // 체크 이벤트 전달
    private val onItemTextChanged: (Int, String) -> Unit // 텍스트 변경 이벤트 전달
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

        // 텍스트 변경 이벤트 처리
        holder.taskEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                onItemTextChanged(position, newText) // 텍스트 변경 이벤트 전달
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 체크박스 초기화
        holder.checkBox.isChecked = task.isChecked

        // 체크박스 변경 이벤트 처리
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onItemChecked(position, isChecked)
        }
    }

    override fun getItemCount(): Int = scheduleList.size
}
