package com.example.connect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.connect.model.ThreadModel
import com.example.connect.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchViewModel:ViewModel() {
    private val db=FirebaseDatabase.getInstance().getReference()
    val users=db.child("users")

    private val _Users=MutableLiveData<List<UserModel>>()
    val userList:LiveData<List<UserModel>> = _Users

    init {
        fetchUsers {
            _Users.value=it
        }
    }

    private fun fetchUsers(onResult: (List<UserModel>)->Unit){
        users.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val result= mutableListOf<UserModel>()

                for(threadSnapshot in snapshot.children){
                    val user=threadSnapshot.getValue(UserModel::class.java)
                    result.add(user!!)
                }

                onResult(result)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun fetchUserFromThread(thread: ThreadModel,onResult:(UserModel)->Unit){
        db.child("users").child(thread.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user=snapshot.getValue(UserModel::class.java)
                    user?.let(onResult)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}