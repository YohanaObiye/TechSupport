package com.example.techsupport

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Date

object Utils {
    fun getAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    fun getDatabaseReference(): FirebaseDatabase = FirebaseDatabase.getInstance()
    fun getDate() = Date().time
    fun getUserId() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // get unique id from firebase
    fun getUniqueId(): String = FirebaseDatabase.getInstance().reference.push().key ?: ""
}
