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

class LoginActivity : AppCompatActivity() {

    private lateinit var wrapper: ScrollView
    private lateinit var loading: RelativeLayout
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtRegister: TextView
    private lateinit var txtForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        wrapper = findViewById(R.id.wrapper)
        loading = findViewById(R.id.loading)
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
            edtEmail.error = resources.getString(R.string.email_required)
            return
        }
        if(!RegexHelper().isValidEmail(email)){
            edtEmail.error = resources.getString(R.string.email_not_valid)
            return
        }
        if(password.isEmpty()){
            edtPassword.error = resources.getString(R.string.password_required)
            return
        }

    // do login
        showLoading()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    println(signIn.result)
                    hideLoading()
                    if(FirebaseAuth.getInstance().currentUser?.isEmailVerified != true) {
                        toast(resources.getString(R.string.email_unverified))
                    }else{
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    hideLoading()
                    toast(resources.getString(R.string.login_failed))
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