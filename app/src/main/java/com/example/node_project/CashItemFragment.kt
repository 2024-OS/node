package com.example.node_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
        storageReference = storage.reference

        // UI 연결
        etDate = view.findViewById(R.id.etDate)
        etAmount = view.findViewById(R.id.etAmount)
        etContent = view.findViewById(R.id.etContent)
        ivImage = view.findViewById(R.id.ivImage)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnComplete = view.findViewById<Button>(R.id.btnComplete)

        // 전달받은 데이터 가져오기
        val date = arguments?.getString("date") ?: ""
        if (date.isNotEmpty()) {
            loadItemFromFirebase(date)
        }

        // 완료 버튼 클릭 리스너
        btnComplete.setOnClickListener {
            val newDate = etDate.text.toString()
            val amount = etAmount.text.toString()
            val content = etContent.text.toString()

            if (imageUri != null) {
                uploadImageToFirebaseStorage(newDate, amount, content, date)
            } else {
                saveDataToFirebase(newDate, amount, content, originalImageUrl, date)
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

    // Firebase에서 데이터 로드
    private fun loadItemFromFirebase(date: String) {
        val sanitizedDate = date.replace(".", "_")
        myRef.child(sanitizedDate).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cashItem = snapshot.getValue(CashItem::class.java)
                if (cashItem != null) {
                    etDate.setText(cashItem.date)
                    etAmount.setText(cashItem.amount)
                    etContent.setText(cashItem.content)
                    originalImageUrl = cashItem.imageUrl

                    // 이미지 URL이 있으면 로드
                    if (cashItem.imageUrl.isNotEmpty()) {
                        Glide.with(requireContext()).load(cashItem.imageUrl).into(ivImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("데이터 로드 실패: ${error.message}")
            }
        })
    }

    // Firebase Storage에 이미지 업로드
    private fun uploadImageToFirebaseStorage(date: String, amount: String, content: String, oldDate: String) {
        if (originalImageUrl.isNotEmpty()) {
            // 기존 이미지 삭제
            val oldImageRef = storage.getReferenceFromUrl(originalImageUrl)
            oldImageRef.delete()
                .addOnSuccessListener {
                    Log.d("CashItemFragment", "기존 이미지 삭제 성공")
                }
                .addOnFailureListener { exception ->
                    Log.e("CashItemFragment", "기존 이미지 삭제 실패: ${exception.message}")
                }
        }

        // 새 이미지 업로드
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveDataToFirebase(date, amount, content, uri.toString(), oldDate)
                }
            }
            .addOnFailureListener { exception ->
                showToast("이미지 업로드 실패: ${exception.message}")
            }
    }



    private fun saveDataToFirebase(newDate: String, amount: String, content: String, imageUrl: String, oldDate: String) {
        val sanitizedOldDate = oldDate.replace(".", "_")
        val sanitizedNewDate = newDate.replace(".", "_")

        // 기존 데이터 삭제 후 새 데이터 저장
        if (sanitizedOldDate != sanitizedNewDate) {
            myRef.child(sanitizedOldDate).removeValue()
        }

        val cashItem = CashItem(newDate, amount, content, imageUrl)
        myRef.child(sanitizedNewDate).setValue(cashItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("수정 완료")
                    findNavController().popBackStack()
                } else {
                    showToast("수정 실패")
                }
            }
    }

    // 이미지 선택을 위한 openGallery 함수 추가
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryForResult.launch(intent)
    }


    // Toast 메시지 표시
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}