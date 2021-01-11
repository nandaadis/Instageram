package com.example.instageram.auth.ui.view

interface AuthListener {
    fun onLoading()
    fun onSuccess()
    fun onFailure(message:String)
}