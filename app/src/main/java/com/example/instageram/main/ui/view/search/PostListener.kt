package com.example.instageram.main.ui.view.search

interface PostListener {
    fun onLoading()
    fun onSuccess()
    fun onFailure(message:String)
}