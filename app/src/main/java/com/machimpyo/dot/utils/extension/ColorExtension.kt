package com.machimpyo.dot.utils.extension

import androidx.compose.ui.graphics.Color

fun Color.Companion.random(): Color {
    return Color((0..255).random(), (0..255).random(), (0..255).random())
}