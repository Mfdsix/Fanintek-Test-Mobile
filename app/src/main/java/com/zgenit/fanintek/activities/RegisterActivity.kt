package com.zgenit.fanintek.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.zgenit.fanintek.R
import com.zgenit.fanintek.utils.Extensions.toast
import com.zgenit.fanintek.utils.FirebaseHelper.firebaseAuth
import com.zgenit.fanintek.utils.FirebaseHelper.firebaseUser
import com.zgenit.fanintek.utils.RegexHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordConfirmation: EditText
    private lateinit var btnRegister: Button
    private lateinit var txtLogin: TextView
    private lateinit var firestoreDB: FirebaseFirestore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firestoreDB = FirebaseFirestore.getInstance()

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
            edtName.error = "Nama Harus Diisi"
            return
        }
        if (name.length < 3) {
            edtName.error = "Nama Harus Minimal 3 Huruf"
            return
        }
        if (name.length > 50) {
            edtName.error = "Nama Harus Maksimal 50 Huruf"
            return
        }
        if (email.isEmpty()) {
            edtEmail.error = "Email Harus Diisi"
            return
        }
        if(!RegexHelper().isValidEmail(email)){
            edtEmail.error = "Email Tidak Valid"
            return
        }
        if (password.isEmpty()) {
            edtPassword.error = "Password Harus Diisi"
            return
        }
        if(password.length < 8){
            edtPassword.error = "Password Harus Minimal 8 Karakter"
            return
        }
        if(!RegexHelper().isValidPassword(password)){
            edtPassword.error = "Password Harus Mengandung angka, huruf besar dan kecil"
            return
        }
        if (passwordConfirmation.isEmpty()){
            edtPasswordConfirmation.error = "Konfirmasi Password Harus Diisi"
            return
        }
        if(passwordConfirmation != password){
            edtPasswordConfirmation.error = "Konfirmasi Password Tidak Sama"
            return
        }

        // do register
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification()
                    saveUserData(name, email)
                } else {
                    toast("Register Gagal")
                }
            }
    }

    private fun saveUserData(name: String, email: String){
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )

        firestoreDB.collection("users").add(user)
            .addOnSuccessListener { documentReference ->
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Silahkan Cek Email Anda")
                    goToLogin()
                }
            }
        }
    }

    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}