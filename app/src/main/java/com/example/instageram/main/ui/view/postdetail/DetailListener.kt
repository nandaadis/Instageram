package com.example.instageram.main.ui.view.postdetail

interface DetailListener {
    fun onLoading()
    fun onSuccess()
    fun onSuccessPhoto()
    fun onSuccessPhotoProfile()
    fun onFailure(message:String)
}