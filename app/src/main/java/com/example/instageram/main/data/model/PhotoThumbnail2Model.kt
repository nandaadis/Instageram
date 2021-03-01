package com.example.instageram.main.data.model

import com.example.instageram.main.ui.view.myprofile.MyProfileMyPhotoAdapter
import com.example.instageram.main.ui.view.otherprofile.OtherProfilePhotoAdapter

data class PhotoThumbnail2Model(
    val photo: ByteArray,
    val holder: OtherProfilePhotoAdapter.ViewHolder,
)
