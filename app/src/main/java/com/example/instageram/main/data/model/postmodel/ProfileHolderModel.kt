package com.example.instageram.main.data.model.postmodel

import com.example.instageram.main.data.model.ProfileModel
import com.example.instageram.main.ui.view.search.PostAdapter

data class ProfileHolderModel(
    val profile: ProfileModel,
    val holder: PostAdapter.ViewHolder
)
