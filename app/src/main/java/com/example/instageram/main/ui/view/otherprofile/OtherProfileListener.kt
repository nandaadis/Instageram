package com.example.instageram.main.ui.view.otherprofile

interface OtherProfileListener {
    fun onLoading()
    fun onSuccess()
    fun onFailure(message:String)
    fun onSuccessPhotoProfile()
    fun onFollowSuccess()
    fun onUnFollowSuccess()
}