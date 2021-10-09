package com.zgenit.fanintek.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import com.zgenit.fanintek.R
import com.zgenit.fanintek.utils.Extensions.toast
import com.zgenit.fanintek.utils.FirebaseHelper.firebaseAuth
import com.zgenit.fanintek.utils.FirebaseHelper.firebaseUser
import com.zgenit.fanintek.utils.RegexHelper
import kotlin.math.sign

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtRegister: TextView
    private lateinit var txtForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtRegister)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)

        btnLogin.setOnClickListener { doLogin() }
        txtRegister.setOnClickListener { goToRegister() }
        txtForgotPassword.setOnClickListener { goToForgotPassword() }
    }

    private fun doLogin(){
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        // input validations
        if (email.isEmpty()) {
            edtEmail.error = "Email Harus Diisi"
            return
        }
        if(!RegexHelper().isValidEmail(email)){
            edtEmail.error = "Email Tidak Valid"
            return
        }
        if(password.isEmpty()){
            edtPassword.error = "Password Harus Diisi"
            return
        }

    // do login
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    if(firebaseUser?.isEmailVerified != true) {
                        toast("Email Belum Terverifikasi")
                    }else{
                        toast("Login Berhasil")
//                    println(firebaseUser?.displayName)
//                    println(firebaseUser?.email)
//                    println(firebaseUser?.isEmailVerified)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    toast("Login Gagal")
                }
            }
    }

    private fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun goToForgotPassword(){
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        firebaseUser?.isEmailVerified?.let {
            startActivity(Intent(this, MainActivity::class.java))
            toast("welcome back")
        }
    }
}