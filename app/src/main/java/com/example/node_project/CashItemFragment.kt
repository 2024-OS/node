package com.example.node_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CashItemFragment : Fragment() {
    private lateinit var etDate: EditText
    private lateinit var etAmount: EditText
    private lateinit var etContent: EditText
    private lateinit var ivImage: ImageView
    private var imageUri: Uri? = null

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
        storageReference = storage.reference

        // UI 연결
        etDate = view.findViewById(R.id.etDate)
        etAmount = view.findViewById(R.id.etAmount)
        etContent = view.findViewById(R.id.etContent)
        ivImage = view.findViewById(R.id.ivImage)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnComplete = view.findViewById<Button>(R.id.btnComplete)

        // 완료 버튼 클릭 시 데이터 저장
        btnComplete.setOnClickListener {
            val date = etDate.text.toString()
            val amount = etAmount.text.toString()
            val content = etContent.text.toString()

            if (date.isNotEmpty() && imageUri != null) {
                uploadImageToFirebaseStorage(date, amount, content)
            } else {
                showToast("모든 항목을 입력해주세요.")
            }
        }

        // 삭제 버튼 클릭 시 초기화
        btnDelete.setOnClickListener {
            etDate.text.clear()
            etAmount.text.clear()
            etContent.text.clear()
            ivImage.setImageDrawable(null)
            findNavController().navigateUp() // 이전 화면으로 돌아가기
        }

        // 이미지 클릭 시 갤러리 열기
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
                    saveDataToFirebase(date, amount, content, uri.toString()) // 날짜만 저장
                }
            }
            .addOnFailureListener { exception ->
                showToast("이미지 업로드 실패: ${exception.message}")
            }
    }

    // Firebase에 데이터 저장
    private fun saveDataToFirebase(date: String, amount: String, content: String, imageUrl: String) {
        val cashItem = CashItem(date, amount, content, imageUrl) // 금액, 내용은 사용되지 않음
        myRef.child(date).setValue(cashItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("저장 성공")
                    findNavController().popBackStack() // 이전 화면으로 돌아가기
                } else {
                    showToast("저장 실패")
                }
            }
    }

    // Toast 메시지 표시
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
