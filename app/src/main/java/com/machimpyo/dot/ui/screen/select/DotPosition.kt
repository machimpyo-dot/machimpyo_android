package com.machimpyo.dot.ui.screen.select

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.machimpyo.dot.ui.theme.DotColor

@Composable
fun DotPosition(
    modifier: Modifier = Modifier,
    isBorder: Boolean = false,
    nowPosition: Int,
    total: Int,
    ) {

    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically)
    {
        for (i in 0..total - 1) {
            val color by animateColorAsState(
                targetValue =
                if (i != nowPosition) {
                    Color.White
                } else {
                    androidx.compose.material3.MaterialTheme.colorScheme.primary
                },
                animationSpec = tween(
                    500
                ),
                label = "",
            )

            val normalSize by animateDpAsState(
                targetValue =
                if (i != nowPosition) {
                    4.dp
                } else {
                    6.dp
                },
                animationSpec = tween(500), label = ""
            )

            Box(
                modifier = Modifier
                    .size(
                        normalSize
                    )
                    .clip(CircleShape)
                    .background(
                        color
                    )
                    .border(BorderStroke(width = 1.dp, color= DotColor.grey4)),
                )
        }
    }
}