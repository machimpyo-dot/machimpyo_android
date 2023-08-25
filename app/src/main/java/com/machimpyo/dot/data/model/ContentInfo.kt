package com.machimpyo.dot.data.model

import androidx.annotation.DrawableRes
import com.fresh.materiallinkpreview.models.OpenGraphMetaData

data class ContentInfo(
    val questionText: String,
    val title: String,
    val subtitle: String,
    val referenceUrl: String,
    @DrawableRes
    val photoId: Int,
    val replyText: String
)