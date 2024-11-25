package com.example.node_project

import android.app.Activity
import android.content.Intent
import androidx.navigation.Navigation
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
    private var currentKey: String? = null   // 기존 데이터의 고유 키

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
        currentKey = arguments?.getString("key") // 고유 키 전달 확인

        if (!currentKey.isNullOrEmpty()) {
            loadItemFromFirebase(currentKey!!)
        } else {
            clearFields()
        }

        // 완료 버튼 클릭 리스너
        btnComplete.setOnClickListener {
            val newDate = etDate.text.toString()
            val amount = etAmount.text.toString()
            val content = etContent.text.toString()

            if (currentKey.isNullOrEmpty()) {
                // 새로운 데이터 생성
                val uniqueKey = System.currentTimeMillis().toString() // 고유 키 생성
                if (imageUri != null) {
                    uploadImageToFirebaseStorage(newDate, amount, content, uniqueKey)
                } else {
                    saveDataToFirebase(newDate, amount, content, "", uniqueKey)
                }
            } else {
                // 기존 데이터 수정
                if (imageUri != null) {
                    uploadImageToFirebaseStorage(newDate, amount, content, currentKey!!)
                } else {
                    saveDataToFirebase(newDate, amount, content, originalImageUrl, currentKey!!)
                }
            }
        }

        // 삭제 버튼 클릭 리스너
        btnDelete.setOnClickListener {
            if (!currentKey.isNullOrEmpty()) {
                deleteItemFromFirebase(currentKey!!)
            }
            clearFields()
            Navigation.findNavController(requireView()).popBackStack()
        }

        // 이미지 클릭 리스너
        ivImage.setOnClickListener {
            openGallery()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryForResult.launch(intent)
    }

    private fun loadItemFromFirebase(key: String) {
        myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val cashItem = snapshot.getValue(CashItem::class.java)
                    if (cashItem != null) {
                        etDate.setText(cashItem.date)
                        etAmount.setText(cashItem.amount)
                        etContent.setText(cashItem.content)
                        originalImageUrl = cashItem.imageUrl

                        // 이미지 로드
                        if (cashItem.imageUrl.isNotEmpty()) {
                            Glide.with(requireContext()).load(cashItem.imageUrl).into(ivImage)
                        } else {
                            ivImage.setImageResource(R.drawable.picture) // 기본 이미지
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("데이터 로드 실패: ${error.message}")
            }
        })
    }

    private fun uploadImageToFirebaseStorage(date: String, amount: String, content: String, key: String) {
        // 기존 이미지 삭제
        if (originalImageUrl.isNotEmpty()) {
            val oldImageRef = storage.getReferenceFromUrl(originalImageUrl)
            oldImageRef.delete()
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }

        // 새 이미지 업로드
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveDataToFirebase(date, amount, content, uri.toString(), key)
                }
            }
            .addOnFailureListener { exception ->
                showToast("이미지 업로드 실패: ${exception.message}")
            }
    }

    private fun saveDataToFirebase(date: String, amount: String, content: String, imageUrl: String, key: String) {
        val cashItem = CashItem(date, amount, content, imageUrl)
        myRef.child(key).setValue(cashItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast(if (currentKey == null) "새 데이터 생성 성공" else "수정 성공")
                    if (isAdded && view != null) { // Fragment가 활성 상태인지 확인
                        findNavController().popBackStack()
                    }
                } else {
                    showToast("데이터 저장 실패")
                }
            }
    }

    private fun deleteItemFromFirebase(key: String) {
        myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cashItem = snapshot.getValue(CashItem::class.java)
                if (cashItem != null && cashItem.imageUrl.isNotEmpty()) {
                    // Firebase Storage에서 이미지 삭제
                    val imageRef = storage.getReferenceFromUrl(cashItem.imageUrl)
                    imageRef.delete()
                }

                // Realtime Database에서 데이터 삭제
                myRef.child(key).removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("데이터 및 이미지 삭제 성공")
                        } else {
                            showToast("데이터 삭제 실패")
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun clearFields() {
        etDate.text.clear()
        etAmount.text.clear()
        etContent.text.clear()
        ivImage.setImageResource(R.drawable.picture) // 기본 이미지 설정
        imageUri = null
        originalImageUrl = ""
        currentKey = null
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
