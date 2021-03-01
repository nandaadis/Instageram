package com.example.instageram.main.data.model

import com.example.instageram.main.ui.view.myprofile.MyProfileMyPhotoAdapter
import com.example.instageram.main.ui.view.postdetail.LoveDetailAdapter

data class LoveDetailModel(
    val holder: LoveDetailAdapter.ViewHolder,
    val userid: String = "",
    val username: String = "",
    val color: Int = -1920697
)
