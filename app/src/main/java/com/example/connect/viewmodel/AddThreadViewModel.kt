package com.example.connect.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.connect.model.ThreadModel
import com.example.connect.model.UserModel
import com.example.connect.utils.SharePref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class AddThreadViewModel:ViewModel() {
    private val db=FirebaseDatabase.getInstance().getReference()
    val userRef=db.child("threads")
    private val storageRef= Firebase.storage.reference
    private val imageRef=storageRef.child("threads/${UUID.randomUUID()}.jpg")

    private val _isPosted=MutableLiveData<Boolean>()
    val isPosted:LiveData<Boolean> = _isPosted


    fun saveImage(thread: String, userId: String,imageuri: Uri) {
        val uploadTask=imageRef.putFile(imageuri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener{
                saveData(thread,userId,it.toString())
            }
        }
    }

    fun saveData(thread: String, userId: String,imageUrl: String) {
        val ThreadData=ThreadModel(
            thread,
            imageUrl,
            userId,
            System.currentTimeMillis().toString()
        )

        userRef.child(userRef.push().key!!).setValue(ThreadData)
            .addOnSuccessListener {
                _isPosted.postValue(true)
            }.addOnFailureListener {
                _isPosted.postValue(false)
            }
    }

}