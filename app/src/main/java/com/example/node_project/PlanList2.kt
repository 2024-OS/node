package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

// Fragment에서 사용될 두 파라미터를 상수로 정의
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PlanList2 : Fragment() {
    // 파라미터를 저장할 변수
    private var param1: String? = null
    private var param2: String? = null

    // onCreate에서 전달된 인자값을 가져옵니다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    // Fragment의 뷰를 생성하는 부분
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 여기에서 fragment_plan_list2.xml 파일을 사용하여 화면을 구성
        return inflater.inflate(R.layout.fragment_plan_list2, container, false)
    }

    // 인스턴스를 새로 생성할 때 파라미터를 전달하는 메서드
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlanList2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
