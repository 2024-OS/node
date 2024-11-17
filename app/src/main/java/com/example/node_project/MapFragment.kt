package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.node_project.databinding.FragmentPlanList2Binding

class MapFragment : Fragment() {

    private lateinit var binding: FragmentPlanList2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanList2Binding.inflate(inflater, container, false)

        // 지도 관련 초기화 코드 추가

        return binding.root
    }
}
