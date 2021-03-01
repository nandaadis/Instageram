package com.example.instageram.main.data

import com.example.instageram.pushnotification.PushNotifModel

class PostRepository(private val firebase: PostSource) {

    fun getPostDetail(postID:String) = firebase.getPostDetail(postID)
    fun getUserProfile(profilUID: String) = firebase.getUserProfile(profilUID)
    fun getPhoto(imagePath: String) = firebase.getPhoto(imagePath)
    fun getCurrentUserUID() = firebase.getCurrentUserUID()
    fun sendLove(postID:String) = firebase.sendLove(postID)
    fun sendUnlove(postID:String) = firebase.sendUnlove(postID)
    fun sendNotif(message: PushNotifModel) = firebase.sendNotif(message)
    fun checkQuery() = firebase.checkQuery()
    fun checkUserId() = firebase.checkUserId()
    fun getCurrentUserProfile() = firebase.getCurrentUserProfile()


}