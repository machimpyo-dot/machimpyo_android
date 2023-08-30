package com.machimpyo.dot.data.model

import android.util.Log
import androidx.compose.ui.graphics.Color
import android.graphics.Color as ColorInt


data class ColorList(
    val list: List<String> = emptyList()
) {

    private val TAG = "Color List"

    fun getColor(index: Int): Color {
        if (index >= list.size) {
            Log.e(TAG,"index out of bound.")
            return Color.White
        }

        return Color(ColorInt.parseColor("#${list[index]}"))
    }


    fun toSet(): Set<String> {
        return list.toSet()
    }
}