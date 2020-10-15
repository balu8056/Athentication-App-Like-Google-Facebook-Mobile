package com.example.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.auth.R
import com.example.auth.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_mobile_auth.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MobileAuth : AppCompatActivity(), KodeinAware, AuthInterface {

    override val kodein: Kodein by kodein()
    private val authFactory: AuthFactory by instance()

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_auth)

        authViewModel = ViewModelProvider(this, authFactory).get(AuthViewModel::class.java)

        authViewModel.authInterface = this

        intent.extras?.getString("mobileNo")?.let {
            if (it.isNotEmpty()) authViewModel.sendOtpMobile(it)
        }

        verifyPhoneOtp.setOnClickListener {
            if (otpForVerification.text.isNullOrEmpty()){
                Toast.makeText(applicationContext, "Enter Otp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.verifyCode(otpForVerification.text.toString())
            mainProgress.visibility = View.VISIBLE
        }

        loginBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }

    }

    private fun startHome(){
        startActivity(Intent(applicationContext, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        finish()
    }

    override fun onSuccess() {
        startHome()
    }

    override fun onCancel() {
        mainProgress.visibility = View.GONE
    }

    override fun onError(error: String) {
        Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
    }

    override fun onProcessEnd() {
        mainProgress.visibility = View.GONE
    }

}