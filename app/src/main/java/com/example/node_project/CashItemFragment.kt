package com.example.node_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.provider.MediaStore

class CashItemFragment : Fragment() {

    private lateinit var etDate: EditText
    private lateinit var etAmount: EditText
    private lateinit var etContent: EditText
    private lateinit var ivImage: ImageView

    private var imageUri: Uri? = null  // 선택된 이미지 URI를 저장


    // 갤러리에서 이미지를 선택하기 위한 ActivityResultLauncher
    private val openGalleryForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data  // 이미지 URI 저장
            ivImage.setImageURI(imageUri)  // 선택된 이미지를 ImageView에 표시
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash_item, container, false)

        etDate = view.findViewById(R.id.etDate)
        etAmount = view.findViewById(R.id.etAmount)
        etContent = view.findViewById(R.id.etContent)
        ivImage = view.findViewById(R.id.ivImage)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        val btnComplete = view.findViewById<Button>(R.id.btnComplete)
        btnComplete.setOnClickListener {
            val date = etDate.text.toString()
            val amount = etAmount.text.toString()
            val content = etContent.text.toString()

            // 날짜, 금액, 내용이 모두 입력되었고, 이미지가 선택되었으면
            if (date.isNotEmpty() && amount.isNotEmpty() && content.isNotEmpty() && imageUri != null) {
                val bundle = Bundle().apply {
                    putString("date", date)
                    putString("amount", amount)
                    putString("content", content)
                    putString("imageUri", imageUri.toString())  // 이미지 URI를 Bundle에 추가
                }

                // 결과를 Cash Fragment로 전달
                parentFragmentManager.setFragmentResult("requestKey", bundle)

                // 목록 화면으로 돌아가기
                findNavController().navigateUp()  // Cash 화면으로 돌아감
            }
        }

        // 이미지를 클릭하면 갤러리로 이동
        ivImage.setOnClickListener {
            openGallery()  // 갤러리 열기
        }

        btnDelete.setOnClickListener {
            // EditText 내용 초기화 (삭제)
            etDate.text.clear()
            etAmount.text.clear()
            etContent.text.clear()

            // 이미지도 초기화 (삭제)
            ivImage.setImageDrawable(null)

            // 회계 내역 화면으로 돌아가기
            findNavController().navigateUp()
        }

        return view
    }

    // 갤러리에서 이미지 선택
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryForResult.launch(intent)  // ActivityResultLauncher를 사용해 갤러리 열기
    }
}
