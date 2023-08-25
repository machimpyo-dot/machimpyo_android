package com.machimpyo.dot.data.model

import androidx.annotation.DrawableRes

data class HomeContent(
    @DrawableRes
    val thumbnail: Int,
    val title: String,
    val subtitle: String,
    val hashTags: List<String>,
    val contentUid: Int
)