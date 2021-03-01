package com.example.instageram.main.data

import com.example.instageram.pushnotification.PushNotifModel

class OtherProfileRepository(private val firebase: OtherProfileSource) {

    fun getProfileData(UserID: String) = firebase.getProfileData(UserID)
    fun getPhotoProfile(imagePath: String) = firebase.getPhotoProfile(imagePath)
    fun onFollow(userID: String) = firebase.onFollow(userID)
    fun onUnfollowUser(userID: String) = firebase.onUnfollowUser(userID)
    fun onUnfollowCurrentUser(userID: String) = firebase.onUnfollowCurrentUser(userID)
    fun getCurrentUserProfile() = firebase.getCurrentUserProfile()
    fun sendNotif(message: PushNotifModel) = firebase.sendNotif(message)
    fun getCurrentUserUID() = firebase.getCurrentUserUID()

}