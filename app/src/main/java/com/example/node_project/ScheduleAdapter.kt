package com.example.node_project

// Android UI 및 RecyclerView 관련 라이브러리 임포트
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.models.ScheduleItem

// RecyclerView 어댑터 클래스.

class ScheduleAdapter(
    private var scheduleList: MutableList<ScheduleItem>, // 작업 리스트
    private val onItemChecked: (Int, Boolean) -> Unit, // 체크 상태 변경
    private val onItemTextChanged: (Int, String) -> Unit // 텍스트 변경
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    //ViewHolder 클래스. (각 작업 항목에 대한 뷰를 관리)
    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskEditText: EditText = itemView.findViewById(R.id.taskTextView) // 작업 내용을 입력하는 EditText
        val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox) // 작업 완료 여부를 표시하는 CheckBox

        //작업 데이터를 뷰에 바인딩(코드와 UI 요소를 연결)
        fun bind(task: ScheduleItem, position: Int) {
            taskEditText.setText(task.task) // 작업 내용을 EditText에 설정
            taskEditText.setSelection(taskEditText.text.length) // 커서 위치를 텍스트 끝으로 설정

            // EditText가 포커스를 잃었을 때 텍스트 변경 이벤트를 전달
            taskEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { // 포커스를 잃은 경우
                    onItemTextChanged(position, taskEditText.text.toString()) // 텍스트 변경 이벤트 호출
                }
            }

            // TextWatcher는 텍스트 변경 이벤트를 감지
            taskEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })

            checkBox.isChecked = task.isChecked // 체크 상태를 CheckBox에 설정
            // CheckBox의 상태가 변경되었을 때 이벤트를 전달
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onItemChecked(position, isChecked) // 체크 상태 변경 이벤트 호출
            }
        }
    }

    //ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view) // 생성된 뷰를 사용해 ViewHolder 생성
    }

    //ViewHolder와 데이터를 바인딩.
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position], position) // position에 해당하는 작업 데이터를 바인딩
    }

    //RecyclerView에 표시할 항목의 총 개수 반환
    override fun getItemCount(): Int = scheduleList.size // 작업 리스트의 크기를 반환


    // RecyclerView의 작업 리스트를 업데이트.
    fun updateTasks(newTasks: List<ScheduleItem>) {
        scheduleList = newTasks.toMutableList() // 새로운 작업 리스트로 업데이트
        notifyDataSetChanged() // RecyclerView 갱신
    }
}
