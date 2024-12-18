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

    // ViewHolder 클래스
    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskEditText: EditText = itemView.findViewById(R.id.taskTextView) // 작업 내용을 입력하는 EditText
        val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox) // 작업 완료 여부를 나타내는 CheckBox

        // ViewHolder와 데이터를 바인딩
        fun bind(task: CalendarScheduleItem, position: Int) {
            taskEditText.setText(task.task) // 작업 내용을 EditText에 표시
            checkBox.isChecked = task.isChecked // CheckBox의 체크 상태를 설정

            // EditText의 포커스가 변경될 때 텍스트 변경 사항 반영
            taskEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { // 포커스를 잃었을 때
                    onItemTextChanged(position, taskEditText.text.toString()) // 변경된 텍스트를 전달
                }
            }

            // CheckBox의 상태가 변경될 때 체크 이벤트를 처리
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onItemChecked(position, isChecked) // 변경된 체크 상태를 전달
            }
        }
    }

    // ViewHolder를 생성하는
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    // ViewHolder와 데이터를 바인딩
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position], position) // ViewHolder와 데이터를 바인딩
    }

    // RecyclerView 항목의 개수
    override fun getItemCount(): Int = scheduleList.size

    // RecyclerView의 데이터를 업데이트하는
    fun updateTasks(newTasks: List<CalendarScheduleItem>) {
        scheduleList = newTasks.toMutableList() // 새로운 작업 리스트로 데이터 교체
        (this as RecyclerView.Adapter<*>).notifyDataSetChanged() // RecyclerView 갱신
    }
}
