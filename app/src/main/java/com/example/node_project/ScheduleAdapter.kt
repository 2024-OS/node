package com.example.node_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.models.ScheduleItem

class ScheduleAdapter(
    private var scheduleList: List<ScheduleItem>,
    private val onItemChecked: (Int, Boolean) -> Unit,
    private val onItemTextChanged: (Int, String) -> Unit
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

        holder.taskEditText.setText(task.task)
        holder.taskEditText.setOnFocusChangeListener { _, _ ->
            onItemTextChanged(position, holder.taskEditText.text.toString())
        }

        holder.checkBox.isChecked = task.isChecked
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onItemChecked(position, isChecked)
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    fun updateData(newData: List<ScheduleItem>) {
        scheduleList = newData
        notifyDataSetChanged()
    }
}
