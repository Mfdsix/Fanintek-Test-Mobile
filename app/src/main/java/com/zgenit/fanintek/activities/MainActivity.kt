package com.zgenit.fanintek.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zgenit.fanintek.R
import com.zgenit.fanintek.adapters.UserAdapter
import com.zgenit.fanintek.models.UserModel

class MainActivity : AppCompatActivity() {

    private lateinit var firestoreDB: FirebaseFirestore
    private lateinit var txtName: TextView
    private lateinit var userLoading: ProgressBar
    private lateinit var userMessage: TextView
    private lateinit var rvUsers: RecyclerView
    private lateinit var btnLogout: Button
    private var users: ArrayList<UserModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestoreDB = FirebaseFirestore.getInstance()

        txtName = findViewById(R.id.txtName)
        userLoading = findViewById(R.id.userLoading)
        userMessage = findViewById(R.id.userMessage)
        rvUsers = findViewById(R.id.rvUsers)
        btnLogout = findViewById(R.id.btnLogout)

        rvUsers.layoutManager = LinearLayoutManager(this)
        val adapter = UserAdapter(users)
        rvUsers.adapter = adapter

        txtName.text = FirebaseAuth.getInstance().currentUser?.displayName
        getUsers()

        btnLogout.setOnClickListener { doLogout() }
    }

    private fun getUsers(){
        firestoreDB.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (user in result){
                    users.add(
                        UserModel(
                            userName = user.data.getValue("name").toString(),
                            email = user.data.getValue("email").toString(),
                        )
                    )
                }

                if(users.size == 0){
                    userRequestStatus(false, resources.getString(R.string.no_user_found), false)
                }else{
                    userRequestStatus(false, null, true)
                }
            }
            .addOnFailureListener { exception ->
                println(exception)
                userRequestStatus(false, resources.getString(R.string.request_failed), false)
            }
    }

    private fun doLogout(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.confirmation)
        builder.setMessage(R.string.logout_confirmation)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        builder.setNegativeButton(R.string.no) {_, _ ->}
        builder.show()
    }

    private fun userRequestStatus(isLoading: Boolean, message: String?, hasData: Boolean){
        userMessage.visibility = View.GONE

        userLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
        if(message != null){
            userMessage.text = message
            userMessage.visibility = View.VISIBLE
        }
        if(hasData){
            rvUsers.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser == null || !firebaseUser.isEmailVerified){
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}