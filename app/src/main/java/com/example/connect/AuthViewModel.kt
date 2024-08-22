package com.example.connect

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.connect.model.UserModel
import com.example.connect.utils.SharePref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class AuthViewModel:ViewModel() {
    val auth=FirebaseAuth.getInstance()
    private val db=FirebaseDatabase.getInstance().getReference()
    val userRef=db.child("users")
    private val storageRef= Firebase.storage.reference
    private val imageRef=storageRef.child("users/${UUID.randomUUID()}.jpg")

    private val _firebaseUser=MutableLiveData<FirebaseUser>()
    val firebaseUser:LiveData<FirebaseUser> = _firebaseUser

    private val _error=MutableLiveData<String>()
    val error:LiveData<String> = _error

    init {
        _firebaseUser.value=auth.currentUser
    }

    fun login(email: String, password: String,context: Context){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    _firebaseUser.postValue(auth.currentUser)
                    getData(auth.currentUser!!.uid,context)
                }

                else
                    _error.postValue("Incorrect Email or Password")
            }
    }

    private fun getData(uid: String,context: Context){
        userRef.child(uid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData=snapshot.getValue(UserModel::class.java)
                SharePref.storeData(
                    userData!!.name,
                    userData!!.email,
                    userData!!.bio,
                    userData!!.username,
                    userData!!.imageUrl,
                    context
                )
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun register(email: String, password: String,name: String,bio: String,username: String,imageuri: Uri,context: Context){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    _firebaseUser.postValue(auth.currentUser)
                    saveImage(email,password,name,bio,username,imageuri,auth.currentUser?.uid,context)
                }

                else
                    _error.postValue("Something went wrong")
            }
    }



    private fun saveImage(email: String, password: String, name: String, bio: String, username: String, imageuri: Uri, uid: String?,context: Context) {
        val uploadTask=imageRef.putFile(imageuri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener{
                saveData(email,password,name,bio,username,it.toString(),uid,context)
            }
        }
    }

    private fun saveData(email: String, password: String, name: String, bio: String, username: String, imageUrl: String, uid: String?,context: Context) {
        val userData=UserModel(
            email,
            password,
            name,
            bio,
            username,
            imageUrl,
            uid!!
        )

        userRef.child(uid!!).setValue(userData)
            .addOnSuccessListener {
                SharePref.storeData(name,email,bio,username,imageUrl, context)
            }
    }

    fun logout(){
        auth.signOut()
        _firebaseUser.postValue(null)
    }
}