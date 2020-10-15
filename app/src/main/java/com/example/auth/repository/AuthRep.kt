package com.example.auth.repository

import android.net.Uri
import android.util.Log
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRep {

    private val auth = FirebaseAuth.getInstance()

    init {
        auth.useAppLanguage()
    }

    var googleSignInClient: GoogleSignInClient? = null

    fun getUserDetails(): Pair<Uri?, String?>{
        return try {
            Pair(auth.currentUser?.photoUrl, auth.currentUser?.displayName)
        }catch (e: Exception){
            Pair(null, null)
        }

    }

    fun getUser() = auth.currentUser

    suspend fun loginWithEmailAndPassword(email: String, pass: String): Pair<AuthResult?, String>{
        return try {
            Pair(
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        Log.e("login", "success")
                    }.await(),
                "Success"
            )
        }catch (e: Exception){
            Pair(null, e.message.toString())
        }
    }

    suspend fun signUpWithEmailAndPassword(email: String, pass: String): Pair<AuthResult?, String>{
        return try {
            Pair(
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        Log.e("signUp", "success")
                    }.await(),
                "Success"
            )
        }catch (e: Exception){
            Pair(null, e.message.toString())
        }
    }

    suspend fun signUpWithCredentials(authCredential: AuthCredential): Pair<AuthResult?, String> {
        return try {
            Pair(
                auth.signInWithCredential(authCredential)
                    .addOnSuccessListener {
                        Log.e("signUp", "success")
                    }
                    .await(),
                "Success")
        }catch (e: Exception){
            Pair(null, e.message.toString())
        }
    }

    fun signOut(): Boolean{
        return try {
            try { LoginManager.getInstance().logOut() }catch (e: Exception){}
            try { googleSignInClient?.signOut() }catch (e: Exception){}
            try { auth.signOut() }catch (e: Exception){}
            true
        }catch (e: Exception){
            false
        }
    }

}