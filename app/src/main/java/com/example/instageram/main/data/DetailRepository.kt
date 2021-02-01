package com.example.instageram.main.data

import com.example.instageram.pushnotification.PushNotifModel

class DetailRepository(private val firebase: DetailSource) {

    fun getPostDetail(postID:String) = firebase.getPostDetail(postID)
    fun getPostUser(postID:String) = firebase.getPostUser(postID)
    fun getPhoto(imagePath: String) = firebase.getPhoto(imagePath)
    fun getCurrentUserUID() = firebase.getCurrentUserUID()
    fun sendLove(postID:String) = firebase.sendLove(postID)
    fun sendUnlove(postID:String) = firebase.sendUnlove(postID)
    fun sendNotif(message: PushNotifModel) = firebase.sendNotif(message)
}