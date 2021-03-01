package com.example.instageram.main.data.model.postmodel

import com.example.instageram.main.data.model.PostModel
import com.example.instageram.main.ui.view.search.PostAdapter

data class PostHolderModel(
    val post: PostModel,
    val holder: PostAdapter.ViewHolder
)
