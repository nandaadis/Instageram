package com.example.instageram.main.data

import android.net.Uri
import com.example.instageram.main.data.model.EditProfileModel

class MyProfileRepository(private val firebase: MyProfileSource) {

    fun getMyProfile() = firebase.getMyProfileData()
    fun getMyProfileEdit() = firebase.getMyProfileEditData()
    fun getPhotoProfile(imagePath: String) = firebase.getPhotoProfile(imagePath)
    fun getCurrentUserUID() = firebase.getCurrentUserUID()
    fun uploadImage(
        photo: Uri,
        photopathold: String,
        userID: String,
        username: String,
        title: String,
        bio: String
    ) = firebase.uploadImage(
        photo,
        photopathold,
        userID,
        username,
        title,
        bio
    )

    fun setUserProfile(EditProfileMyProfile: EditProfileModel) =
        firebase.setUserProfile(EditProfileMyProfile)

    fun uploadImagePost(photo: Uri) = firebase.uploadImagePost(photo)
    fun makePost(photopath: String, desc: String) = firebase.makePost(photopath, desc)

    fun getPostData(postUID: String) = firebase.getPostData(postUID)
    fun getThumbnail(imagePath: String) = firebase.getThumbnail(imagePath)
    fun getPhotolist() = firebase.getPhotolist()
}