package com.machimpyo.dot.utils.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickableWithoutRipple(
    onClickListener: () -> Unit
): Modifier = composed {
    val interactionSource = MutableInteractionSource()

    clickable(
        interactionSource = interactionSource,
        indication = null
    ) {
        onClickListener()
    }
}
