package com.example.instageram.main.ui.view.myprofile

interface GetMyProfileListener {
    fun onLoading()
    fun onSuccess()
    fun onFailure(message:String)
    fun onLoadingPhotoProfile()
    fun onSuccessPhotoProfile(byteArray: ByteArray)
}