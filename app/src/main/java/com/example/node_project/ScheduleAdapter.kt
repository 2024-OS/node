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
    private var scheduleList: MutableList<ScheduleItem>,
    private val onItemChecked: (Int, Boolean) -> Unit,
    private val onItemTextChanged: (Int, String) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskEditText: EditText = itemView.findViewById(R.id.taskTextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)

        fun bind(task: ScheduleItem, position: Int) {
            // TextWatcher 동적으로 추가 및 제거
            taskEditText.setText(task.task)
            taskEditText.setSelection(taskEditText.text.length) // 커서 위치 유지

            taskEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    onItemTextChanged(position, taskEditText.text.toString())
                }
            }

            taskEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })

            checkBox.isChecked = task.isChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onItemChecked(position, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position], position)
    }

    override fun getItemCount(): Int = scheduleList.size

    fun updateTasks(newTasks: List<ScheduleItem>) {
        scheduleList = newTasks.toMutableList()
        notifyDataSetChanged()
    }
}
