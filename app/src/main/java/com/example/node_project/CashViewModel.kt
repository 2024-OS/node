// ViewModel
// 데이터를 LiveData로 노출함

package com.example.node_project

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// UI 관련 데이터를 관리하고, Repository를 통해 Firebase 작업을 수행함
class CashViewModel(application: Application) : AndroidViewModel(application) {

    // Firebase 작업을 처리하는 Repository 인스턴스
    private val repository = CashRepository()

    // MutableLiveData - 변경 가능한 데이터로 내부에서만 수정 가능
    // LiveData - 외부에서 읽기만 가능하도록 함
    private val _itemList = MutableLiveData<List<Pair<String, String>>>() // 목록 데이터 저장
    val itemList: LiveData<List<Pair<String, String>>> get() = _itemList // LiveData

    // 날짜 저장하는 LiveData
    private val _date = MutableLiveData("") // 초기값을 빈 문자열로 설정
    val date: LiveData<String> get() = _date

    // 금액 저장하는 LiveData
    private val _amount = MutableLiveData("") // 초기값을 빈 문자열로 설정
    val amount: LiveData<String> get() = _amount

    // 내용 저장하는 LiveData
    private val _content = MutableLiveData("") // 초기값을 빈 문자열로 설정
    val content: LiveData<String> get() = _content

    // 이미지 URL 저장하는 LiveData
    private val _imageUrl = MutableLiveData("") // 초기값을 빈 문자열로 설정
    val imageUrl: LiveData<String> get() = _imageUrl

    // 데이터 저장 완료됐는지 저장하는 LiveData
    private val _dataSaved = MutableLiveData<Boolean>() // 저장 성공 여부
    val dataSaved: LiveData<Boolean> get() = _dataSaved

    // 데이터 로드 실패하면 에러 메시지를 저장하는 LiveData
    private val _dataLoadError = MutableLiveData<String>()

    // 수정 중 목록의 고유 키 저장
    private var currentKey: String? = null

    // Firebase에서 전체 목록 데이터를 불러오는 함수
    fun loadItems() {
        repository.loadItems(
            { list ->
                // Firebase에서 불러온 데이터를 LiveData에 업데이트
                _itemList.value = list
            },
            { error ->
                // 데이터 로드 실패하면 에러 메시지 LiveData
                _dataLoadError.value = error
            }
        )
    }

    // Firebase에서 특정 항목 데이터를 불러오는 함수
    fun loadItem(key: String) {
        repository.loadItem(
            key,
            { cashModel ->
                // Firebase에서 불러온 해당 목록 LiveData에 설정
                _date.value = cashModel.date
                _amount.value = cashModel.amount
                _content.value = cashModel.content
                _imageUrl.value = cashModel.imageUrl
                currentKey = key
            },
            { error ->
                // 데이터 로드 실패하면 에러 메시지 LiveData
                _dataLoadError.value = error
            }
        )
    }

    // Firebase에 데이터를 저장하는 함수
    fun saveItem(date: String, amount: String, content: String, imageUri: Uri?) {
        val key = currentKey ?: System.currentTimeMillis().toString()
        repository.saveItem(
            date = date,
            amount = amount,
            content = content,
            imageUri = imageUri,
            key = key,
            oldImageUrl = _imageUrl.value
        )
        { success ->
            // 저장 성공 여부를 LiveData에 설정
            _dataSaved.value = success
        }
    }

    // Firebase에서 데이터를 삭제하는 함수
    fun deleteItem()
    {
        currentKey?.let { key ->
            repository.deleteItem(
                key = key,
                imageUrl = _imageUrl.value
            )
            { success ->
                // 삭제 성공 여부를 LiveData에 설정
                _dataSaved.value = success
            }
        }
    }
}
