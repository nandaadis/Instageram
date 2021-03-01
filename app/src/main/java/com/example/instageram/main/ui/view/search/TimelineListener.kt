package com.example.instageram.main.ui.view.search

interface TimelineListener {
    fun onNoQuery()
    fun onNoUserID()
    fun onFailure(message:String)
}