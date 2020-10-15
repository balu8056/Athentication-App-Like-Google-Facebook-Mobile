package com.example.auth.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.auth.R
import com.example.auth.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SignUpActivity : AppCompatActivity(), KodeinAware, AuthInterface {

    override val kodein: Kodein by kodein()
    private val authFactory: AuthFactory by instance()

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        authViewModel = ViewModelProvider(this, authFactory).get(AuthViewModel::class.java)

        authViewModel.authInterface = this

        loginBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
        }

        submitSignUp.setOnClickListener {
            if (signUpEmailMobile.text.isNullOrEmpty()) {
                toast("Fill email or mobile")
                return@setOnClickListener
            }
            if (signUpPass.text.isNullOrEmpty()){
                toast("Fill password")
                return@setOnClickListener
            }
            if (confirmPassword.text.isNullOrEmpty()){
                toast("Enter password again")
                return@setOnClickListener
            }
            if (signUpPass.text.toString() != confirmPassword.text.toString()){
                Log.e("pass", "${signUpPass.text} == ${confirmPassword.text}")
                toast("Password not matching")
                return@setOnClickListener
            }
            authViewModel.signUpWithEmail(signUpEmailMobile.text.toString(), signUpPass.text.toString())
            mainProgress.visibility = View.VISIBLE
        }

    }

    private fun startHome(){
        startActivity(Intent(applicationContext, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        finish()
    }

    private fun toast(msg: String){
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess() {
        startHome()
    }

    override fun onCancel() {
        mainProgress.visibility = View.GONE
    }

    override fun onError(error: String) {
        toast(error)
    }

    override fun onProcessEnd() {
        mainProgress.visibility = View.GONE
    }
}