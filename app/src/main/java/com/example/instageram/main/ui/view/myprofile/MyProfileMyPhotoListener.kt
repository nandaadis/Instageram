package com.example.instageram.main.ui.view.myprofile

interface MyProfileMyPhotoListener {
    fun onLoading()
    fun onSuccess(byteArray: ByteArray)
    fun onFailure(message:String)
}