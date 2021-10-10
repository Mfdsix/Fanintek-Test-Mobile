package com.zgenit.fanintek.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.zgenit.fanintek.R
import com.zgenit.fanintek.utils.Extensions.toast
import com.zgenit.fanintek.utils.RegexHelper


class RegisterActivity : AppCompatActivity() {

    private lateinit var wrapper: ScrollView
    private lateinit var loading: RelativeLayout
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordConfirmation: EditText
    private lateinit var btnRegister: Button
    private lateinit var txtLogin: TextView
    private lateinit var firestoreDB: FirebaseFirestore
    val TAG = "Login Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firestoreDB = FirebaseFirestore.getInstance()

        wrapper = findViewById(R.id.wrapper)
        loading = findViewById(R.id.loading)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtPasswordConfirmation = findViewById(R.id.edtPasswordConfirmation)
        btnRegister = findViewById(R.id.btnRegister)
        txtLogin = findViewById(R.id.txtLogin)

        btnRegister.setOnClickListener { doRegister() }
        txtLogin.setOnClickListener { goToLogin() }
    }

    private fun doRegister() {
        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val passwordConfirmation = edtPasswordConfirmation.text.toString().trim()

        // input validations
        if (name.isEmpty()) {
            edtName.error = resources.getString(R.string.name_required)
            return
        }
        if (name.length < 3) {
            edtName.error = resources.getString(R.string.name_minimum)
            return
        }
        if (name.length > 50) {
            edtName.error = resources.getString(R.string.name_maximum)
            return
        }
        if (email.isEmpty()) {
            edtEmail.error = resources.getString(R.string.email_required)
            return
        }
        if(!RegexHelper().isValidEmail(email)){
            edtEmail.error = resources.getString(R.string.email_not_valid)
            return
        }
        if (password.isEmpty()) {
            edtPassword.error = resources.getString(R.string.password_required)
            return
        }
        if(password.length < 8){
            edtPassword.error = resources.getString(R.string.password_minimum)
            return
        }
        if(!RegexHelper().isValidPassword(password)){
            edtPassword.error = resources.getString(R.string.password_validation)
            return
        }
        if (passwordConfirmation.isEmpty()){
            edtPasswordConfirmation.error = resources.getString(R.string.password_required)
            return
        }
        if(passwordConfirmation != password){
            edtPasswordConfirmation.error = resources.getString(R.string.password_not_same)
            return
        }

        // do register
        showLoading()
        Log.d(TAG, "Request create user with email and password")
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully create user")
                    sendEmailVerification()
                    updateDisplayName(name)
                    saveUserData(name, email)
                } else {
                    Log.d(TAG, "Failed to create user: " + task.exception)
                    hideLoading()
                    toast(resources.getString(R.string.request_failed))
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to create user: " + it.message)
            }
    }

    private fun saveUserData(name: String, email: String){
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )

        Log.d(TAG, "Request save user data")
        firestoreDB.collection("users").add(user)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully save user data")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to save user data: " + it.message)
                hideLoading()
                toast(resources.getString(R.string.request_failed))
            }
    }

    private fun sendEmailVerification() {
        FirebaseAuth.getInstance().currentUser?.let {
            Log.d(TAG, "Request send email verification")
            it.sendEmailVerification().addOnCompleteListener { task ->
                hideLoading()
                if (task.isSuccessful) {
                    Log.d(TAG, "Successfully send email")
                    toast(resources.getString(R.string.check_your_mail))
                    goToLogin()
                    finish()
                }else{
                    Log.d(TAG, "Failed to send email: " + task.exception)
                }
            }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to send email: " + it.message)
                }
        }
    }

    private fun updateDisplayName(name: String){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).build()

        Log.d(TAG, "Request update display name")
        FirebaseAuth.getInstance().currentUser?.let {
            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Log.d(TAG, "Successfully update display name")
                    }else{
                        Log.d(TAG, "Failed to update display name: " + task.exception)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to update display name: " + exception.message)
                }
        }
    }

    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        wrapper.visibility = View.GONE
    }

    private fun hideLoading(){
        loading.visibility = View.GONE
        wrapper.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser != null && firebaseUser.isEmailVerified){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}