package com.example.node_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.models.CalendarScheduleItem

class CalendarScheduleAdapter(
    private var scheduleList: MutableList<CalendarScheduleItem>,
    private val onItemChecked: (Int, Boolean) -> Unit,
    private val onItemTextChanged: (Int, String) -> Unit
) : RecyclerView.Adapter<CalendarScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskEditText: EditText = itemView.findViewById(R.id.taskTextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)

        fun bind(task: CalendarScheduleItem, position: Int) {
            taskEditText.setText(task.task)
            checkBox.isChecked = task.isChecked

            taskEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    onItemTextChanged(position, taskEditText.text.toString())
                }
            }

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

    fun updateTasks(newTasks: List<CalendarScheduleItem>) {
        scheduleList = newTasks.toMutableList()
        (this as RecyclerView.Adapter<*>).notifyDataSetChanged()
    }
}
