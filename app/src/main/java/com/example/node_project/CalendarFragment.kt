package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.models.ScheduleItem
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var scheduleTitle: TextView
    private lateinit var addButton: Button
    private lateinit var deleteButton: Button
    private lateinit var adapter: ScheduleAdapter

    private val scheduleData = mutableMapOf<String, MutableList<ScheduleItem>>()
    private var selectedDate: String = getTodayDate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        // View 초기화
        calendarView = view.findViewById(R.id.calendarView)
        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView)
        scheduleTitle = view.findViewById(R.id.scheduleTitle)
        addButton = view.findViewById(R.id.button3)
        deleteButton = view.findViewById(R.id.button4)

        setupRecyclerView()
        setupCalendarView()
        setupButtons()

        return view
    }

    private fun setupRecyclerView() {
        adapter = ScheduleAdapter(scheduleData[selectedDate] ?: mutableListOf()) { position, isChecked ->
            // Handle checkbox changes
        }
        scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        scheduleRecyclerView.adapter = adapter
    }

    private fun setupCalendarView() {
        // 초기 선택 날짜 설정
        scheduleTitle.text = selectedDate

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
            scheduleTitle.text = selectedDate
            updateScheduleList()
        }
    }

    private fun setupButtons() {
        addButton.setOnClickListener {
            val currentList = scheduleData.getOrPut(selectedDate) { mutableListOf() }
            currentList.add(ScheduleItem("새 작업"))
            updateScheduleList()
        }

        deleteButton.setOnClickListener {
            val currentList = scheduleData[selectedDate] ?: return@setOnClickListener
            val iterator = currentList.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().isChecked) iterator.remove()
            }
            updateScheduleList()
        }
    }

    private fun updateScheduleList() {
        val currentList = scheduleData[selectedDate] ?: mutableListOf()
        adapter.updateData(currentList)
    }

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
