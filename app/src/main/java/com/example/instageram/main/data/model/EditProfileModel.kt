package com.example.instageram.main.data.model

data class EditProfileModel(
    val photopath: String,
    val userid: String,
    val username: String,
    val title: String = "",
    val bio: String = ""
)
