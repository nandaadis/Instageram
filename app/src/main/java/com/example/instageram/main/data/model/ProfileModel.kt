package com.example.instageram.main.data.model

data class ProfileModel(
    val photopath: String = "",
    val userid: String = "",
    val username: String = "",
    val title: String = "",
    val bio: String = "",
    val follower: ArrayList<String> = arrayListOf(),
    val following: ArrayList<String> = arrayListOf(),
    val post: ArrayList<String> = arrayListOf(),
    val token: String = "",
    val color: Int = -1920697
)
