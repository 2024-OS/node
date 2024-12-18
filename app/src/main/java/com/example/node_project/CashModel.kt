// Model
// 데이터 클래스
// Firebase 관련 작업 담당

package com.example.node_project

import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

data class CashModel(
    val date: String = "",      // 날짜
    val amount: String = "",    // 금액
    val content: String = "",   // 내용
    val imageUrl: String = ""   // 이미지 URL
)

// Firebase 작업을 담당하는 Repository 클래스
class CashRepository
{

    // Firebase Realtime Database와 Storage 초기화
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.reference.child("cash_items")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.reference


    // Firebase에서 목록 데이터를 불러옴
    fun loadItems(callback: (List<Pair<String, String>>) -> Unit, errorCallback: (String) -> Unit)
    {
        myRef.orderByChild("date").addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val list = mutableListOf<Pair<String, String>>() // 데이터를 저장할 리스트
                // Firebase 노드를 반복하며 데이터를 가져옴
                for (data in snapshot.children)
                {
                    val key = data.key // 각 항목의 키
                    val item = data.getValue(CashModel::class.java) // CashModel로 데이터 변환
                    item?.let { list.add(Pair(it.date, key ?: "")) } // null이 아니면 리스트에 추가
                }
                callback(list) // 성공 시 콜백 호출
            }

            override fun onCancelled(error: DatabaseError)
            {
                errorCallback("Failed to load data: ${error.message}") // 실패 시 오류 메시지 반환
            }
        })
    }


    // 특정 데이터를 불러옴
    fun loadItem(key: String, callback: (CashModel) -> Unit, errorCallback: (String) -> Unit)
    {
        myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                snapshot.getValue(CashModel::class.java)?.let { callback(it) } // 성공 시 데이터 반환
            }

            override fun onCancelled(error: DatabaseError)
            {
                errorCallback("Failed to load item: ${error.message}") // 실패 시 오류 메시지 반환
            }
        })
    }


    // 데이터를 저장하거나 기존 이미지를 삭제 후 새로운 이미지를 업로드 함
    fun saveItem(
        date: String,
        amount: String,
        content: String,
        imageUri: Uri?,
        key: String,
        oldImageUrl: String?,
        callback: (Boolean) -> Unit
    ) {
        if (!oldImageUrl.isNullOrEmpty())
        {
            // 기존 이미지 URL이 존재하면 Storage에서 삭제
            storage.getReferenceFromUrl(oldImageUrl).delete()
                .addOnCompleteListener{
                    uploadNewImage(date, amount, content, imageUri, key, callback) // 새 이미지 업로드
                }
                .addOnFailureListener {
                    uploadNewImage(date, amount, content, imageUri, key, callback) // 실패 시에도 새 이미지 업로드
                }
        } else
        {
            // 기존 이미지가 없으면 바로 새 이미지 업로드
            uploadNewImage(date, amount, content, imageUri, key, callback)
        }
    }


    // 새 이미지를 Firebase Storage에 업로드하고 데이터를 저장
    private fun uploadNewImage(
        date: String,
        amount: String,
        content: String,
        imageUri: Uri?,
        key: String,
        callback: (Boolean) -> Unit
    ) {
        if (imageUri != null)
        {
            // 새로운 이미지 파일을 Storage에 업로드
            val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveToDatabase(date, amount, content, uri.toString(), key, callback)
                }
            }.addOnFailureListener { callback(false) } // 실패 시 콜백 호출
        } else
        {
            // 이미지가 없으면 데이터만 저장
            saveToDatabase(date, amount, content, "", key, callback)
        }
    }


    // 데이터를 Firebase Realtime Database에 저장하는 메서드
    private fun saveToDatabase(
        date: String,
        amount: String,
        content: String,
        imageUrl: String,
        key: String,
        callback: (Boolean) -> Unit
    ) {
        val data = CashModel(date, amount, content, imageUrl) // CashModel 데이터 생성
        myRef.child(key).setValue(data).addOnCompleteListener { task ->
            callback(task.isSuccessful) // 저장 성공 여부 반환
        }
    }


    // 데이터를 삭제하고 Storage에 있는 이미지를 삭제하는 메서드
    fun deleteItem(key: String, imageUrl: String?, callback: (Boolean) -> Unit)
    {
        if (!imageUrl.isNullOrEmpty())
        {
            // 이미지 URL이 존재하면 Storage에서 삭제
            storage.getReferenceFromUrl(imageUrl).delete()
                .addOnSuccessListener {
                    // 이미지 삭제 성공 후 데이터 삭제
                    myRef.child(key).removeValue().addOnCompleteListener { task ->
                        callback(task.isSuccessful)
                    }
                }
                .addOnFailureListener {
                    // 이미지 삭제 실패 시에도 데이터 삭제 시도
                    myRef.child(key).removeValue().addOnCompleteListener { task ->
                        callback(task.isSuccessful)
                    }
                }
        } else
        {
            // 이미지 URL이 없으면 데이터만 삭제
            myRef.child(key).removeValue().addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
        }
    }
}
