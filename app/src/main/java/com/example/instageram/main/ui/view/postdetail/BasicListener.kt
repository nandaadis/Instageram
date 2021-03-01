package com.example.instageram.main.ui.view.postdetail

interface BasicListener {
    fun onLoading()
    fun onSuccess()
    fun onFailure(message:String)
}