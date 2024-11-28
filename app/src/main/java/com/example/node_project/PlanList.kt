package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.node_project.databinding.FragmentPlanListBinding
import androidx.navigation.fragment.findNavController


class PlanList : Fragment() {
    private lateinit var binding: FragmentPlanListBinding // View 바인딩 객체로, XML 레이아웃 파일에 정의된 UI 요소에 쉽게 접근할 수 있게 함
    private lateinit var planListAdapter: PlanListAdapter // RecyclerView의 어댑터 객체로, 목록의 항목을 관리하는 역할
    private val itemList = mutableListOf<PlanItem>()      // PlanItem 객체의 리스트를 저장하는 MutableList로, 이 리스트가 RecyclerView에 표시

    override fun onCreateView(  // 사용자 인터페이스(사용자 화면)를 생성하고 반환
        inflater: LayoutInflater, container: ViewGroup?,              // inflate 메서드는 XML 레이아웃 파일을 뷰 객체로 변환하는 작업을 수행
        // XML에서 설정한 UI 요소들을 binding 객체를 통해 쉽게 사용할 수 있게 됨
        savedInstanceState: Bundle?    // Fragment의 생명주기 동안 UI 상태를 유지, 액티비티, Fragment 간에 데이터 전송
    ): View {
        binding = FragmentPlanListBinding.inflate(inflater, container, false)

        setupRecyclerView()     // RecyclerView 설정 -> 안하면 RecyclerView가 동작하지 않음
        setupButtonListeners()  // 버튼 리스너 설정  -> 안하면 버튼이 동작하지 않음

        return binding.root     // UI를 사용자에게 보여줄 준비가 완료됨
    }

    private fun setupRecyclerView() {
        planListAdapter = PlanListAdapter(itemList, onMapButtonClick = {})                  // 어댑터에 항목 리스트 전달
        binding.recPlan.layoutManager = LinearLayoutManager(context) // 레이아웃 매니저 설정
        binding.recPlan.adapter = planListAdapter                    // 어댑터 설정
    }

    private fun setupButtonListeners() {
        binding.addButtonPlan.setOnClickListener { addNewItem() }        // 추가 버튼 클릭
        binding.deleteButton.setOnClickListener { deleteCheckedItems() } // 삭제 버튼 클릭
        binding.mapButton.setOnClickListener { navigateToMap() }         // 지도 버튼 클릭
    }


    private fun addNewItem() {
        val newItem = PlanItem(title = "새 장소")                          // 새 장소 항목 생성
        itemList.add(newItem)                                             // 리스트에 추가
        planListAdapter.notifyItemInserted(itemList.size - 1)     // 어댑터에 변경 사항 알림
    }

    private fun deleteCheckedItems() {           // 체크된 항목 가져오기 및 삭제
        val checkedItems = planListAdapter.getCheckedItems()
        itemList.removeAll(checkedItems)        // 리스트에서 체크된 항목 삭제
        planListAdapter.notifyDataSetChanged()  // 어댑터에 변경 사항 알림
    }

    private fun navigateToMap() {
        findNavController().navigate(R.id.action_planList_to_mapFrag) // 지도 프래그먼트로 이동
    }
}