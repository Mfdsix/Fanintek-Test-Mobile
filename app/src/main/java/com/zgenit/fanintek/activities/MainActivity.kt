package com.zgenit.fanintek.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.zgenit.fanintek.R
import com.zgenit.fanintek.utils.Extensions.toast
import com.zgenit.fanintek.utils.FirebaseHelper.firebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var firestoreDB: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestoreDB = FirebaseFirestore.getInstance()
        getUsers()
    }

    private fun getUsers(){
        firestoreDB.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (user in result){
                    println(user)
                }
            }
            .addOnFailureListener { exception ->
                println(exception)
                toast("Gagal Mendapatkan Data: " + exception)
            }
    }
}