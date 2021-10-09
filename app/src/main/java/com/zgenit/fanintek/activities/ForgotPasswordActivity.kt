package com.zgenit.fanintek.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.zgenit.fanintek.R
import com.zgenit.fanintek.utils.Extensions.toast
import com.zgenit.fanintek.utils.FirebaseHelper.firebaseAuth
import com.zgenit.fanintek.utils.RegexHelper

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnForgotPassword: Button
    private lateinit var txtLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtEmail = findViewById(R.id.edtEmail)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
        txtLogin = findViewById(R.id.txtLogin)

        btnForgotPassword.setOnClickListener { doForgotPassword() }
        txtLogin.setOnClickListener { goToLogin() }
    }

    private fun doForgotPassword(){
        val email = edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            edtEmail.error = "Email Harus Diisi"
            return
        }
        if(!RegexHelper().isValidEmail(email)){
            edtEmail.error = "Email Tidak Valid"
            return
        }

        // do forgot password
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Email Telah Dikirim")
                }else{
                    toast("Gagal Mengirim Email")
                }
            }
    }
    private fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}