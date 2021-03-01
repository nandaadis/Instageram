package com.example.instageram.main.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.instageram.auth.data.AuthRepository
import com.example.instageram.auth.ui.viewmodel.AuthViewModel
import com.example.instageram.main.data.MyProfileRepository


class MyProfileModelFactory(
    private val repository: MyProfileRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MyProfileViewModel(repository) as T
        }
}