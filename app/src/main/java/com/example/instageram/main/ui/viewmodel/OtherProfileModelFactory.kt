package com.example.instageram.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instageram.main.data.OtherProfileRepository
import com.example.instageram.main.data.PostRepository


class OtherProfileModelFactory(private val repository: OtherProfileRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OtherProfileViewModel(repository) as T
    }
}