package com.example.node_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class CashItemFragment : Fragment() {

    private lateinit var etDate: EditText
    private lateinit var etAmount: EditText
    private lateinit var etContent: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash_item, container, false)

        etDate = view.findViewById(R.id.etDate)
        etAmount = view.findViewById(R.id.etAmount)
        etContent = view.findViewById(R.id.etContent)

        val btnComplete = view.findViewById<Button>(R.id.btnComplete)
        btnComplete.setOnClickListener {
            val date = etDate.text.toString()
            val amount = etAmount.text.toString()
            val content = etContent.text.toString()

            // 날짜, 금액, 내용이 모두 입력되었으면
            if (date.isNotEmpty() && amount.isNotEmpty() && content.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString("date", date)
                    putString("amount", amount)
                    putString("content", content)
                }

                // 결과를 Cash Fragment로 전달
                parentFragmentManager.setFragmentResult("requestKey", bundle)

                // 목록 화면으로 돌아가기
                findNavController().navigateUp()  // Cash 화면으로 돌아감
            }
        }

        return view
    }
}
