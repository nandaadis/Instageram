package com.example.instageram.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instageram.main.data.MyProfileRepository
import com.example.instageram.main.data.PostRepository

class PostModelFactory(
    private val repository: PostRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostViewModel(repository) as T
    }
}