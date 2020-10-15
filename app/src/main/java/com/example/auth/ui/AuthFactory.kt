package com.example.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.auth.repository.AuthRep

@Suppress("UNCHECKED_CAST")
class AuthFactory(private val authRep: AuthRep): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(authRep) as T
    }
}