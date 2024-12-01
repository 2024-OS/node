// ViewModel
// LiveData로 UI랑 로직 분리
// Firebase에서 데이터 불러오거나 저장
// 데이터 저장인지 실패인지 상태 관리

package com.example.node_project

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

// 앱의 데이터 처리 및 Firebase와의 상호작용을 담당
// UI에 변화가 생기더라도 데이터를 안전하게 관리

class CashViewModel(application: Application) : AndroidViewModel(application) {

    private val _itemList = MutableLiveData<List<Pair<String, String>>>()
    val itemList: LiveData<List<Pair<String, String>>> get() = _itemList // 외부에서 접근할 수 있는 LiveData로 함


    // 개별 목록의 데이터를 저장함 (날짜, 금액, 내용, 이미지 URL) -> 값 변경할 수 있는 MutableLiveData
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date  // 날짜

    private val _amount = MutableLiveData<String>()
    val amount: LiveData<String> get() = _amount  // 금액

    private val _content = MutableLiveData<String>()
    val content: LiveData<String> get() = _content  // 내용

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl  // 이미지 URL


    // 데이터가 저장됐는지 알려줌
    private val _dataSaved = MutableLiveData<Boolean>()
    val dataSaved: LiveData<Boolean> get() = _dataSaved

    // 데이터 로드가 실패하면 알려줌
    private val _dataLoadError = MutableLiveData<String>()

    // Firebase랑 Storage에 접근하는 거
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance() // Firebase RealTime Data 인스턴스
    private val myRef: DatabaseReference = database.reference.child("cash_items") // firebase의 'cash_items' 참조

    private val storage: FirebaseStorage = FirebaseStorage.getInstance() // Firebase Storage 인스턴스
    private val storageReference: StorageReference = storage.reference // Firebase의 'Storage' 참조

    // 선택된 목록의 고유 키 (수정이나 삭제할 때 사용함)
    private var currentKey: String? = null


    // Firebase 'cash_items'에서 실시간으로 목록의 상세 데이터를 불러옴
    fun loadItems() {
        // myRef : cash_items에 참조, orderByChild : 날짜별로 정리, addValueEventListner : 실시간으로 데이터 업데이트(firebase에서 제공)
        myRef.orderByChild("date").addValueEventListener(object : ValueEventListener {

            // 데이터 변경되거나 로드되면 호출 됨
            override fun onDataChange(snapshot: DataSnapshot) {

                // 새로운 데이터 목록을 저장할 수정가능 리스트 (Kotlin에서 제공하는 mutableListOf)
                val newList = mutableListOf<Pair<String, String>>()

                // snapshot.children : Firebase의 모든 자식 노드를 순회함
                for (dataSnapshot in snapshot.children) {
                    val key = dataSnapshot.key // 각 목록의 고유 키
                    val cashItem = dataSnapshot.getValue(CashModel::class.java) // Firebase 데이터를 CashModel로 변환

                    cashItem?.let { // let : null이 아니면 실행
                        // cashItem에 날짜와 키를 Pair로 저장
                        newList.add(Pair(it.date, key ?: ""))
                    }
                }
                // LiveData(_itemList)에 업데이트함 , LiveData = UI와 비즈니스 로직을 분리하고, 데이터를 UI에 자동으로 업데이트
                _itemList.value = newList
            }

            // 데이터 로드 실패하면 호출 됨
            override fun onCancelled(error: DatabaseError) {
                // 에러 메시지를 LiveData에 설정해서 사용자한테 알림
                _dataLoadError.value = "데이터 로드 실패: ${error.message}"
            }
        })
    }

    // firebase에서 개별 세부 정보를 고유키로 불러와서 수정 가능함
    fun loadItem(key: String) {
        // child(key) : key를 이용해서 Firebase에서 그 목록을 찾음
        // addListenerForSingValueEvent : 데이터가 한번이라도 변경되거나 조회되면 종료됨
        myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // 데이터가 존재하면 CashModel로 변환해서 LiveData에 저장함
                    val cashModel = snapshot.getValue(CashModel::class.java)
                    cashModel?.let {  // cashModel이 null이 아니면
                        _date.value = it.date
                        _amount.value = it.amount
                        _content.value = it.content
                        _imageUrl.value = it.imageUrl
                        currentKey = key // 값을 LiveData에 저장함
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                _dataLoadError.value = "데이터 로드 실패: ${error.message}"
            }
        })
    }

    // 데이터들 firebase에 저장함
    fun saveItem(date: String, amount: String, content: String, imageUri: Uri?) {
        // 선택되거나 새로 만들어지는 key임
        val key = currentKey ?: System.currentTimeMillis().toString()

        // 이미지 URI가 null이 아니면,
        if (imageUri != null) {
            // 이미지를 Firebase Storage에 업로드 후 데이터 저장함
            uploadImageToFirebaseStorage(date, amount, content, key, imageUri)
        } else {
            // 이미지가 null이면 데이터만 Firebase에 저장함
            saveDataToFirebase(date, amount, content, _imageUrl.value.orEmpty(), key)
        }
    }

    // Firebase Storage에 이미지를 업로드하고, 데이터 저장
    private fun uploadImageToFirebaseStorage(date: String, amount: String, content: String, key: String, imageUri: Uri) {
        // 이전 이미지 URL이 있으면 그 이미지는 삭제함
        val oldImageUrl = _imageUrl.value.orEmpty()
        if (oldImageUrl.isNotEmpty()) {
            storage.getReferenceFromUrl(oldImageUrl).delete() // 참조해서 없앰
        }

        // 새로운 이미지 파일은 Firebase Storage에 업로드 함 (images 폴더에 업로드)
        //System.currentTimeMillis : 현 시간을 밀리초 단위로 변환하는 함수로, 고유 파일명으로 만들어서 파일명이 겹칠 위험이 없음
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")

        // imageUrl을 firebase Storage의 imageRef 위치에 업로드
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // 업로드 성공 하면, URL이랑 데이터를 Firebase에 저장함
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveDataToFirebase(date, amount, content, uri.toString(), key)
                }
            }
            .addOnFailureListener { exception ->
                // 이미지 업로드 실패하면 에러 메시지 LiveData로
                _dataLoadError.value = "이미지 업로드 실패: ${exception.message}"
            }
    }

    // Firebase Realtime Database에 데이터를 저장함
    private fun saveDataToFirebase(date: String, amount: String, content: String, imageUrl: String, key: String) {
        // 저장할 데이터를 CashModel로 묶어서(안 그러면 각 값을 개별적으로 해야함)
        val cashModel = CashModel(date, amount, content, imageUrl)

        //  Firebase에 저장함 , child : 참조
        myRef.child(key).setValue(cashModel)

            .addOnCompleteListener { task ->
                // 데이터 저장 완료면 LiveData에 설정해줌
                _dataSaved.value = task.isSuccessful
            }
    }

    // 목록이랑 데이터랑 이미지를 Firebase에서 삭제함
    fun deleteItem() {
        currentKey?.let { key ->
            // 이미지 URL 이용해서 삭제할거임
            myRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                // oveeride : 부모 함수를 자식 클래스에서 재정의 할때 사용
                // onDataChange : 데이터 읽으면 호출 됨
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Firebase 스냅샷 데이터를 cashModel 형태로 변환함
                    val cashModel = snapshot.getValue(CashModel::class.java)
                    cashModel?.imageUrl?.let { url -> // imageUrl이 있으면 삭제함
                        storage.getReferenceFromUrl(url).delete()
                    }

                    // Firebase에서 데이터 삭제
                    myRef.child(key).removeValue()

                        // 작업이 성공하거나 실패했을 때 결과를 처리하는 걸 도와줌
                        .addOnCompleteListener {
                            // 삭제가 완료되면 dataSaved를 true로 설정
                            if (it.isSuccessful) {
                                _dataSaved.value = true
                            }
                        }
                }
                // onCasncelled : 데이터 읽을 때 취소되거나 에러뜨면 실행
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
