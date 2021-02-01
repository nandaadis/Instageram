package com.example.instageram.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instageram.main.data.DetailRepository
import com.example.instageram.main.data.MyProfileRepository

class DetailModelFactory(
    private val repository: DetailRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailViewModel(repository) as T
    }
}
