package com.example.instageram.main.data.model

data class PostModel(
    val postid: String = "",
    val postowner: String = "",
    val loveby: ArrayList<String> = arrayListOf(),
    val photopath: ArrayList<String> = arrayListOf(),
    val timestamp: String = "",
    val desc: String = "",
    val viewby: ArrayList<String> = arrayListOf(),
    val commentcount: Int = 0
)
