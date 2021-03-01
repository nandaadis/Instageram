package com.example.instageram.main.data.model

import com.example.instageram.main.ui.view.postdetail.DetailCommentAdapter
import com.example.instageram.main.ui.view.postdetail.LoveDetailAdapter

data class CommentDetailModel(
    val holder: DetailCommentAdapter.ViewHolder,
    val userid: String = "",
    val color: Int = -1920697,
    val model: CommentListModel
)
