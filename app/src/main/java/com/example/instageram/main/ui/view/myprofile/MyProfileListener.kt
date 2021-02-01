package com.example.instageram.main.ui.view.myprofile

interface MyProfileListener {
    fun onLoading()
    fun onSuccess()
    fun onFailure(message:String)
}