package com.machimpyo.dot.data.store

import android.content.Context
import android.content.SharedPreferences
import com.machimpyo.dot.data.model.ColorList

class LetterDesignSharedPreferences (
    private val context: Context
){

    private val PREFS_NAME = "letter_design_prefs"
    private val COLOR_LIST = "color_list"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    fun getLetterColorList(): MutableSet<String>? {
        return prefs.getStringSet(COLOR_LIST, null)
    }

    fun putLetterColorList(colorList: ColorList) {
        prefs.edit().putStringSet(COLOR_LIST, colorList.toSet()).apply()
    }


}