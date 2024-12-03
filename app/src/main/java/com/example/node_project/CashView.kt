// View (UI)
// 목록 상세 데이터를 입력 받는 화면처리
// CahsViewModel.kt로 데이터 저장하거나 삭제
// 갤러리 이미지 선택하고, ViewModel에 전달

package com.example.node_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.Button
import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CashView : Fragment() {

    // UI 요소 선언
    private lateinit var etDate: EditText  // 날짜 입력 EditText
    private lateinit var etAmount: EditText // 금액 입력 EditText
    private lateinit var etContent: EditText // 내용 입력 EditText
    private lateinit var ivImage: ImageView // 이미지 표시 및 선택 ImageView


    // ViewModel로 데이터 처리 (LiveData로 UI랑 연결)
    private val viewModel: CashViewModel by viewModels()

    // 이미지의 URI 저장할 변수
    private var imageUri: Uri? = null


    // Fragment가 화면에 생성되면 호출
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, // container은 부모뷰
        savedInstanceState: Bundle? // 화면이 변경돼도 전 화면 데이터를 가지고 있는 번들
    ): View? {
        // 레이아웃을 View로 변환
        val view = inflater.inflate(R.layout.fragment_cash_item, container, false)

        // UI 요소들 초기화 (R은 리로스파일 참조하는 클래스)
        etDate = view.findViewById(R.id.etDate)
        etAmount = view.findViewById(R.id.etAmount)
        etContent = view.findViewById(R.id.etContent)
        ivImage = view.findViewById(R.id.ivImage)


        // 삭제버튼과 완료버튼
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnComplete = view.findViewById<Button>(R.id.btnComplete)

        // argument로 전달된 'key' 값이 있으면, 해당 아이템 로드함
        val key = arguments?.getString("key")
        key?.let { viewModel.loadItem(it) }

        // LiveData랑 UI를 연결해서 실시간으로 바뀌게 설정함
        setupObservers()


        // '완료' 버튼 누르면 입력된 데이터 저장
        btnComplete.setOnClickListener {
            // ViewModel에 데이터 저장해달라고 함
            viewModel.saveItem(
                etDate.text.toString(),
                etAmount.text.toString(),
                etContent.text.toString(),
                imageUri
            )
        }

        // '삭제' 버튼 누르면 데이터 삭제
        btnDelete.setOnClickListener {
            // ViewModel에 데이터 삭제해달라고 함
            viewModel.deleteItem()
            // 바로 전 화면으로 돌아가기
            findNavController().popBackStack()
        }


        // 이미지 그림 클릭하면 갤러리 열기
        ivImage.setOnClickListener {
            openGallery()
        }

        return view
    }

    // ViewModel의 LiveData를 관찰해서, 값이 변경되면 UI 바로 업데이트 (observe 이용)
    private fun setupObservers() {
        // 날짜 LiveData
        viewModel.date.observe(viewLifecycleOwner) {
            etDate.setText(it) // it은 날짜의 최신 데이터임
        }

        // 금액 LiveData
        viewModel.amount.observe(viewLifecycleOwner) {
            etAmount.setText(it) // it은 금액의 최신 데이터임
        }

        // 내용 LiveData
        viewModel.content.observe(viewLifecycleOwner) {
            etContent.setText(it) // it은 내용의 최신 데이터임
        }

        // 이미지 URL이 있으면 사진 불러오기
        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            if (url.isNotEmpty()) {
                // Glide로 이미지 URL을 ImageView에 불러옴
                Glide.with(this).load(url).into(ivImage)
            } else {
                // URL이 없으면 기본 이미지 그림으로 냅두기
                ivImage.setImageResource(R.drawable.picture)
            }
        }

        // 데이터가 저장 완료되면, 이전 화면으로 돌아가기
        viewModel.dataSaved.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                findNavController().popBackStack()
            }
        }
    }


    // 갤러리 열기 (인텐트 = 앱의 다른 화면을 보여주도록하는 메시지 객체임)
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // 갤러리에서 선택한 이미지 결과 기다리고, 받아옴
        startActivityForResult(intent, 100)
    }


    // 갤러리에서 선택한 이미지를 받음
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 이미지가 잘 선택되면,
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // 선택한 이미지 URI를 저장
            imageUri = data?.data
            // 선택한 이미지를 ImageView에 표시
            ivImage.setImageURI(imageUri)
        }
    }
}