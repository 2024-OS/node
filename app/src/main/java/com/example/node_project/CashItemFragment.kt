package com.example.node_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CashItemFragment : Fragment() {
    private lateinit var etDate: EditText
    private lateinit var etAmount: EditText
    private lateinit var etContent: EditText
    private lateinit var ivImage: ImageView
    private var imageUri: Uri? = null
    private var originalImageUrl: String = "" // 기존 이미지 URL

    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    // 갤러리에서 이미지를 선택하기 위한 ActivityResultLauncher
    private val openGalleryForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            ivImage.setImageURI(imageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash_item, container, false)

        // Firebase 초기화
        database = FirebaseDatabase.getInstance()
        myRef = database.reference.child("cash_items")
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference // 반드시 초기화

        // UI 연결
        etDate = view.findViewById(R.id.etDate)
        etAmount = view.findViewById(R.id.etAmount)
        etContent = view.findViewById(R.id.etContent)
        ivImage = view.findViewById(R.id.ivImage)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnComplete = view.findViewById<Button>(R.id.btnComplete)

        // 전달받은 날짜 데이터 가져오기
        val date = arguments?.getString("date") ?: ""
        etDate.setText(date)

        // 완료 버튼 클릭 리스너 (데이터 확인 없이 저장)
        btnComplete.setOnClickListener {
            val date = etDate.text.toString()
            val amount = etAmount.text.toString()
            val content = etContent.text.toString()

            // 이미지 URI가 있으면 업로드, 없으면 바로 데이터 저장
            if (imageUri != null) {
                uploadImageToFirebaseStorage(date, amount, content)
            } else {
                saveDataToFirebase(date, amount, content, "") // 이미지 URL 없이 저장
            }
        }

        // 삭제 버튼 클릭 리스너
        btnDelete.setOnClickListener {
            etDate.text.clear()
            etAmount.text.clear()
            etContent.text.clear()
            ivImage.setImageDrawable(null)
            findNavController().navigateUp()
        }

        // 이미지 클릭 리스너
        ivImage.setOnClickListener {
            openGallery()
        }

        return view
    }

    // 갤러리에서 이미지 선택
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryForResult.launch(intent)
    }

    // Firebase Storage에 이미지 업로드
    private fun uploadImageToFirebaseStorage(date: String, amount: String, content: String) {
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveDataToFirebase(date, amount, content, uri.toString()) // 이미지 URL 포함 저장
                }
            }
            .addOnFailureListener { exception ->
                showToast("이미지 업로드 실패: ${exception.message}")
            }
    }

    private fun saveDataToFirebase(date: String, amount: String, content: String, imageUrl: String) {
        val sanitizedDate = date.replace(".", "_") // 점을 밑줄로 변환
        val cashItem = CashItem(date, amount, content, imageUrl) // 입력된 데이터로 객체 생성
        myRef.child(sanitizedDate).setValue(cashItem) // 데이터 저장
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("수정 완료")
                    findNavController().popBackStack()
                } else {
                    showToast("수정 실패")
                }
            }
    }

    // Toast 메시지 표시
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
