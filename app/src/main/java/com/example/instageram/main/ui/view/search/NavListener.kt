package com.example.instageram.main.ui.view.search

interface NavListener {
    fun goToMyProfile()
    fun goToOtherProfile(postOwnerUID: String)
    fun goToLoveDetail(postID: String)
    fun goToCommentDetail(postID: String)
}