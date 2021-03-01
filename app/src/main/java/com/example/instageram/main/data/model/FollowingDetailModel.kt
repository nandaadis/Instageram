package com.example.instageram.main.data.model

import com.example.instageram.main.ui.view.postdetail.FollowerDetailAdapter
import com.example.instageram.main.ui.view.postdetail.FollowingDetailAdapter

data class FollowingDetailModel (
    val holder: FollowingDetailAdapter.ViewHolder,
    val userid: String = "",
    val username: String = "",
    val color: Int = -1920697
)
