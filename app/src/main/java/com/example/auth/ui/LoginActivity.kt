package com.example.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.auth.R
import com.example.auth.ui.home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

const val RC_SIGN_IN = 4321

class MainActivity : AppCompatActivity(), KodeinAware, AuthInterface {

    override val kodein: Kodein by kodein()
    private val authFactory: AuthFactory by instance()

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authViewModel = ViewModelProvider(this, authFactory).get(AuthViewModel::class.java)

        authViewModel.authInterface = this

        loginEmail.requestFocus()

        //facebook
        facebookSignUp.setPermissions(listOf("email", "public_profile"))
        facebookSignUp.registerCallback(authViewModel.callbackManager, authViewModel.facebookCallback)

        facebookCusBtn.setOnClickListener {
            mainProgress.visibility = View.VISIBLE
            facebookSignUp.performClick()
        }

        //google
        googleSignIn.setOnClickListener {
            mainProgress.visibility = View.VISIBLE
            startActivityForResult(authViewModel.setGoogleSignInClient(applicationContext).signInIntent, RC_SIGN_IN)
        }

        signUpBtn.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }

        submitMobile.setOnClickListener {
            if (mobile.text.isNullOrEmpty()){
                Toast.makeText(applicationContext, "mobile is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mobile.text.length != 10){
                Toast.makeText(applicationContext, "invalid mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(applicationContext, MobileAuth::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra("mobileNo", mobile.text.toString())
            })
            finish()
        }

        loginButton.setOnClickListener {
            if (loginEmail.text.isNullOrEmpty()){
                Toast.makeText(applicationContext, "Email is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (loginPass.text.isNullOrEmpty()){
                Toast.makeText(applicationContext, "Password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.loginWithEmail(loginEmail.text.toString(), loginPass.text.toString())
            mainProgress.visibility = View.VISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authViewModel.callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { task.getResult(ApiException::class.java)!!.let { authViewModel.signInByGoogle(it.idToken!!) }
            } catch (e: ApiException) { mainProgress.visibility = View.GONE }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        authViewModel.getUser()
        authViewModel.currentUser.observe(this, Observer { startHome() })
        super.onStart()
    }

    override fun onDestroy() {
        facebookSignUp.unregisterCallback(authViewModel.callbackManager)
        super.onDestroy()
    }

    override fun onSuccess() {
        startHome()
    }

    override fun onCancel() { mainProgress.visibility = View.GONE }

    private fun startHome(){
        startActivity(Intent(applicationContext, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        finish()
    }

    override fun onError(error: String) {
        Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
    }

    override fun onProcessEnd() {
        mainProgress.visibility = View.GONE
    }

}