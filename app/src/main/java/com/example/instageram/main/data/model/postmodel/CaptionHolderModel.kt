package com.example.instageram.main.data.model.postmodel

import com.example.instageram.main.data.model.CaptionModel
import com.example.instageram.main.ui.view.search.PostAdapter

data class CaptionHolderModel(
    val caption: CaptionModel,
    val holder: PostAdapter.ViewHolder,
    val postOwnerUID: String,
    val postID: String
)
