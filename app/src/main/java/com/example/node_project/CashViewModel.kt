package com.example.node_project

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CashViewModel(application: Application) : AndroidViewModel(application) {

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount

    private val _content = MutableLiveData<String>()
    val content: LiveData<String> get() = _content

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.reference.child("cash_items")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.reference

    private var currentKey: String? = null

    fun loadItem(key: String) {
        myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val cashModel = snapshot.getValue(CashModel::class.java)
                    if (cashModel != null) {
                        _date.value = cashModel.date
                        _amount.value = cashModel.amount
                        _content.value = cashModel.content
                        _imageUrl.value = cashModel.imageUrl
                        currentKey = key
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("데이터 로드 실패: ${error.message}")
            }
        })
    }

    fun saveItem(date: String, amount: String, content: String, imageUri: Uri?) {
        val key = currentKey ?: System.currentTimeMillis().toString()
        if (imageUri != null) {
            uploadImageToFirebaseStorage(date, amount, content, key, imageUri)
        } else {
            saveDataToFirebase(date, amount, content, _imageUrl.value.orEmpty(), key)
        }
    }

    private fun uploadImageToFirebaseStorage(date: String, amount: String, content: String, key: String, imageUri: Uri) {
        val oldImageUrl = _imageUrl.value.orEmpty()
        if (oldImageUrl.isNotEmpty()) {
            val oldImageRef = storage.getReferenceFromUrl(oldImageUrl)
            oldImageRef.delete()
        }

        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(imageUri)
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
        val cashModel = CashModel(date, amount, content, imageUrl)
        myRef.child(key).setValue(cashModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("데이터 저장 성공")
                } else {
                    showToast("데이터 저장 실패")
                }
            }
    }

    fun deleteItem() {
        currentKey?.let { key ->
            myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cashModel = snapshot.getValue(CashModel::class.java)
                    cashModel?.imageUrl?.let { url ->
                        val imageRef = storage.getReferenceFromUrl(url)
                        imageRef.delete()
                    }

                    myRef.child(key).removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) showToast("데이터 삭제 성공")
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}
