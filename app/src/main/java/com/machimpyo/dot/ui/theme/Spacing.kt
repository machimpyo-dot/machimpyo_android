package com.machimpyo.dot.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.machimpyo.dot.R

data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val extraMedium: Dp = 24.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 64.dp,
    val image_100: Dp = 100.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

@Composable
private fun isTablet(): Boolean {
    return booleanResource(R.bool.is_tablet)
}

@Composable
fun Dp.compatSize(
): Dp {
    return if (isTablet()) {
        this.times(1.5f)
    } else {
        this
    }
}