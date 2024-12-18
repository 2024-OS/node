// Model

package com.example.node_project

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


data class CashModel(
    val date: String = "",
    val amount: String = "",
    val content: String = "",
    val imageUrl: String = "" // Firebase에서 저장되는 이미지 URL
)