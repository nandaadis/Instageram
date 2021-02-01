package com.example.instageram.main.ui.view.myprofile

interface MyProfileEditProfilListener {
    fun onLoading()
    fun onSuccess()
    fun onFailure(message:String)
    fun onLoadingPhotoProfile()
    fun onSuccessPhotoProfile(byteArray: ByteArray)
    fun uploadImageFailure(message:String)
    fun setEditProfileSuccess()
    fun setEditProfileFailure(message:String)
}