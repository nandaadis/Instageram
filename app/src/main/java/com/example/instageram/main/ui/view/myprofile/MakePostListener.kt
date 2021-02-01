package com.example.instageram.main.ui.view.myprofile

interface MakePostListener {
    fun onLoading()
    fun makePostSuccess()
    fun makePostFailure(message:String)
}