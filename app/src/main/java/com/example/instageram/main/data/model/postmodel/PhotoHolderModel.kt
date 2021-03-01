package com.example.instageram.main.data.model.postmodel

import com.example.instageram.main.ui.view.search.PostAdapter

data class PhotoHolderModel(
    val photo: ByteArray,
    val holder: PostAdapter.ViewHolder
)
