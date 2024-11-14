package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController  // findNavController를 위한 import 추가

class Cash2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash2, container, false)

        // 완료 버튼 설정
        val completeButton = view.findViewById<Button>(R.id.completeButton)
        completeButton.setOnClickListener {
            // 완료 버튼 클릭 시 수행할 작업 (예: 이전 화면으로 돌아가기)
            findNavController().navigateUp()  // 현재 화면을 닫고 이전 화면으로 돌아감
        }

        return view
    }
}
