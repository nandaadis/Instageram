package com.example.instageram.main.data.model

import com.example.instageram.main.ui.view.myprofile.MyProfileMyPhotoAdapter

data class PhotoThumbnailModel(
    val photo: ByteArray,
    val holder: MyProfileMyPhotoAdapter.ViewHolder,
)
