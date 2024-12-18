package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentCalenderBinding
import com.example.node_project.viewmodel.CalendarViewModel
import java.util.Calendar
import java.util.TimeZone
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()
    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CalendarScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel // Data Binding 설정
        binding.lifecycleOwner = viewLifecycleOwner // LiveData 관찰
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CalendarView를 한국 시간대로 초기화
        setCalendarViewToKoreanTime()

        viewModel.initializeDate() // 초기 날짜 설정
        viewModel.fetchTasks() // Firebase 데이터 불러오기

        setupRecyclerView() // RecyclerView 초기화
        observeViewModel() // ViewModel의 LiveData 관찰 및 UI 업데이트

        // 날짜 선택 이벤트
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = convertToKoreanDate(year, month, dayOfMonth) // 한국 시간으로 날짜 변환
            viewModel.setDate(date) // ViewModel에 설정된 날짜 전달
        }

        // 추가 버튼
        binding.button3.setOnClickListener {
            viewModel.addTask("새 작업")
            binding.scheduleRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }

        // 삭제 버튼
        binding.button4.setOnClickListener {
            viewModel.removeCheckedTasks()
        }
    }

    private fun setCalendarViewToKoreanTime() {
        // 한국 시간대 설정
        val koreanTimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val calendar = Calendar.getInstance(koreanTimeZone)

        // 현재 날짜 설정 (UTC 오프셋 적용)
        val offset = koreanTimeZone.getOffset(calendar.timeInMillis)
        val todayInMillis = System.currentTimeMillis() + offset

        // CalendarView에 오늘 날짜 적용
        binding.calendarView.date = todayInMillis
    }


    // RecyclerView 초기화
    private fun setupRecyclerView() {
        adapter = CalendarScheduleAdapter(
            mutableListOf(),
            { position, isChecked -> viewModel.updateTaskCheckedState(position, isChecked) },
            { position, text -> viewModel.updateTaskText(position, text) }
        )
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.scheduleRecyclerView.adapter = adapter
    }

    // ViewModel의 LiveData 관찰
    private fun observeViewModel() {
        viewModel.tasksForDate.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.updateTasks(tasks)
        })
        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            binding.scheduleTitle.text = getString(R.string.schedule_title, date ?: "날짜 없음")
        })
    }

    // 날짜를 한국 시간대로 변환
    private fun convertToKoreanDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        calendar.set(year, month, dayOfMonth)
        val format = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
