package com.example.auth.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auth.R
import com.example.auth.repository.AuthRep
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel(private val authRep: AuthRep): ViewModel() {

    private val _currentUser = MutableLiveData<FirebaseUser>()
    val currentUser :LiveData<FirebaseUser> = _currentUser

    var authInterface: AuthInterface? = null

    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    private lateinit var googleSignInClient: GoogleSignInClient

    var verificationCodeSent: String? = null

    fun setGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context.applicationContext, gso)
        authRep.googleSignInClient = googleSignInClient
        return googleSignInClient
    }

    val facebookCallback = object: FacebookCallback<LoginResult>{
        override fun onSuccess(result: LoginResult?) {
            handleFacebookAccessToken(result?.accessToken!!)
        }
        override fun onCancel() {
            authInterface?.onCancel()
        }
        override fun onError(error: FacebookException?) {
            authInterface?.onError(error?.message.toString())
            authInterface?.onProcessEnd()
        }
    }


    private val phoneNoCallback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {}
        override fun onVerificationFailed(p0: FirebaseException) {
            authInterface?.onError(p0.message.toString())
            authInterface?.onProcessEnd()
        }
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationCodeSent = p0
        }
    }

    fun handleFacebookAccessToken(token: AccessToken){
        FacebookAuthProvider.getCredential(token.token).let {
            CoroutineScope(Dispatchers.Main).launch {
                val authResult = authRep.signUpWithCredentials(it)
                if (authResult.first != null) authInterface?.onSuccess()
                else authInterface?.onError(authResult.second)
                authInterface?.onProcessEnd()
            }
        }
    }

    fun signInByGoogle(idToken: String){
        GoogleAuthProvider.getCredential(idToken, null).let {
            CoroutineScope(Dispatchers.Main).launch {
                val authResult = authRep.signUpWithCredentials(it)
                if (authResult.first != null) authInterface?.onSuccess()
                else authInterface?.onError(authResult.second)
                authInterface?.onProcessEnd()
            }
        }
    }

    fun getUser() {
        authRep.getUser().let {
            if (it != null) _currentUser.value = it
        }
    }

    fun loginWithEmail(email: String, pass: String){
        CoroutineScope(Dispatchers.Main).launch {
            val authResult = authRep.loginWithEmailAndPassword(email, pass)
            if (authResult.first != null) authInterface?.onSuccess()
            else authInterface?.onError(authResult.second)
            authInterface?.onProcessEnd()
        }
    }

    fun signUpWithEmail(emailOrMobile: String, pass: String){
        CoroutineScope(Dispatchers.Main).launch {
            val authResult = authRep.signUpWithEmailAndPassword(emailOrMobile, pass)
            if (authResult.first != null) authInterface?.onSuccess()
            else authInterface?.onError(authResult.second)
            authInterface?.onProcessEnd()
        }
    }

    fun sendOtpMobile(mobile: String){
        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber("+91$mobile", 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, phoneNoCallback)
    }

    fun verifyCode(code: String){
        val credential = PhoneAuthProvider.getCredential(verificationCodeSent!!, code)
        handlePhoneNoToken(credential)
    }

    private fun handlePhoneNoToken(phoneAuthCredential: PhoneAuthCredential){
        CoroutineScope(Dispatchers.Main).launch {
            val authResult = authRep.signUpWithCredentials(phoneAuthCredential)
            if (authResult.first != null) authInterface?.onSuccess()
            else authInterface?.onError(authResult.second)
            authInterface?.onProcessEnd()
        }
    }

    fun signOut() = authRep.signOut()

    private val _userImage = MutableLiveData<Uri>()
    val userImage : LiveData<Uri> = _userImage

    private val _userName = MutableLiveData<String>()
    val userName : LiveData<String> = _userName

    fun getUserDetail(){
        val det = authRep.getUserDetails()
        try {
            if (det.first != null && det.second != null){
                _userImage.value = det.first
                _userName.value = det.second
            }
        }catch (e: Exception){
            _userName.value = "MobileNo"
        }
    }

}