package com.machimpyo.dot.utils.extension

import androidx.navigation.NavController

fun NavController.hasBackStackEntry(): Boolean {
    return previousBackStackEntry?.destination != null
}
