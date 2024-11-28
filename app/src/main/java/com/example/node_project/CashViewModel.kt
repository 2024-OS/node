package com.example.node_project

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CashViewModel(application: Application) : AndroidViewModel(application) {

    // 아이템 목록을 담을 LiveData (리스트 형태로 날짜와 키를 쌍으로 저장)
    private val _itemList = MutableLiveData<List<Pair<String, String>>>()
    val itemList: LiveData<List<Pair<String, String>>> get() = _itemList

    // 각 아이템의 세부 정보를 저장할 LiveData (날짜, 금액, 내용, 이미지 URL)
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount

    private val _content = MutableLiveData<String>()
    val content: LiveData<String> get() = _content

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    // 데이터 저장 성공 여부를 나타내는 LiveData
    private val _dataSaved = MutableLiveData<Boolean>()
    val dataSaved: LiveData<Boolean> get() = _dataSaved

    // 데이터 로드 실패 메시지를 나타내는 LiveData
    private val _dataLoadError = MutableLiveData<String>()

    // Firebase 데이터베이스와 저장소에 접근하는 객체들
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.reference.child("cash_items")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.reference

    // 현재 선택된 아이템의 고유 키 (수정 또는 삭제 시 사용)
    private var currentKey: String? = null

    // Firebase에서 실시간으로 아이템 리스트를 로드하는 메서드
    fun loadItems() {
        myRef.orderByChild("date").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<Pair<String, String>>()
                for (dataSnapshot in snapshot.children) {
                    val key = dataSnapshot.key
                    val cashItem = dataSnapshot.getValue(CashModel::class.java)
                    cashItem?.let {
                        newList.add(Pair(it.date, key ?: ""))
                    }
                }
                _itemList.value = newList // 아이템 목록 LiveData 갱신
            }

            override fun onCancelled(error: DatabaseError) {
                _dataLoadError.value = "데이터 로드 실패: ${error.message}"
            }
        })
    }

    // 개별 아이템을 로드하는 메서드 (수정할 때 사용)
    fun loadItem(key: String) {
        myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val cashModel = snapshot.getValue(CashModel::class.java)
                    cashModel?.let {
                        _date.value = it.date
                        _amount.value = it.amount
                        _content.value = it.content
                        _imageUrl.value = it.imageUrl
                        currentKey = key // 현재 아이템의 키 저장
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _dataLoadError.value = "데이터 로드 실패: ${error.message}"
            }
        })
    }

    // 아이템을 저장하는 메서드 (이미지가 있을 경우 업로드 포함)
    fun saveItem(date: String, amount: String, content: String, imageUri: Uri?) {
        val key = currentKey ?: System.currentTimeMillis().toString()

        if (imageUri != null) {
            uploadImageToFirebaseStorage(date, amount, content, key, imageUri)
        } else {
            saveDataToFirebase(date, amount, content, _imageUrl.value.orEmpty(), key)
        }
    }

    // 이미지를 Firebase Storage에 업로드하고, 업로드 후 데이터 저장
    private fun uploadImageToFirebaseStorage(date: String, amount: String, content: String, key: String, imageUri: Uri) {
        val oldImageUrl = _imageUrl.value.orEmpty()
        if (oldImageUrl.isNotEmpty()) {
            storage.getReferenceFromUrl(oldImageUrl).delete()
        }

        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveDataToFirebase(date, amount, content, uri.toString(), key)
                }
            }
            .addOnFailureListener { exception ->
                _dataLoadError.value = "이미지 업로드 실패: ${exception.message}"
            }
    }

    // Firebase에 데이터를 저장하는 메서드 (이미지 URL 포함)
    private fun saveDataToFirebase(date: String, amount: String, content: String, imageUrl: String, key: String) {
        val cashModel = CashModel(date, amount, content, imageUrl)
        myRef.child(key).setValue(cashModel)
            .addOnCompleteListener { task ->
                _dataSaved.value = task.isSuccessful
            }
    }

    // 아이템을 삭제하는 메서드
    fun deleteItem() {
        currentKey?.let { key ->
            myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cashModel = snapshot.getValue(CashModel::class.java)
                    cashModel?.imageUrl?.let { url ->
                        storage.getReferenceFromUrl(url).delete()
                    }

                    myRef.child(key).removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                _dataSaved.value = true
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
