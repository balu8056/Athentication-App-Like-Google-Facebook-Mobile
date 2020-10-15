package com.example.auth.ui

interface AuthInterface {

    fun onSuccess()
    fun onCancel()
    fun onError(error: String)

    fun onProcessEnd()

}