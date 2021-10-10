package com.zgenit.fanintek.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.zgenit.fanintek.R
import com.zgenit.fanintek.utils.Extensions.toast
import com.zgenit.fanintek.utils.RegexHelper

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var wrapper: ScrollView
    private lateinit var loading: RelativeLayout
    private lateinit var edtEmail: EditText
    private lateinit var btnForgotPassword: Button
    private lateinit var txtLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        wrapper = findViewById(R.id.wrapper)
        loading = findViewById(R.id.loading)
        edtEmail = findViewById(R.id.edtEmail)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
        txtLogin = findViewById(R.id.txtLogin)

        btnForgotPassword.setOnClickListener { doForgotPassword() }
        txtLogin.setOnClickListener { goToLogin() }
    }

    private fun doForgotPassword(){
        val email = edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            edtEmail.error = resources.getString(R.string.email_required)
            return
        }
        if(!RegexHelper().isValidEmail(email)){
            edtEmail.error = resources.getString(R.string.email_not_valid)
            return
        }

        // do forgot password
        showLoading()
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                hideLoading()
                if (task.isSuccessful) {
                    edtEmail.text = null
                    toast(resources.getString(R.string.check_your_mail))
                }else{
                    toast(resources.getString(R.string.request_failed))
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
}