package com.machimpyo.dot.ui.topappbar

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.machimpyo.dot.R

@Composable
fun Send(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.send),
            contentDescription = "send",
            tint = Color.White
        )
    }
}

@Composable
fun Save(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.store) ,
            contentDescription = "Store",
            tint = Color.White
        )

    }
}

@Composable
fun Back(onCLick: () -> Unit = {}){
    IconButton(onClick = onCLick) {
        Icon(
            painter= painterResource(id = R.drawable.back_arrow),
            contentDescription = "Back",
            tint = Color.White
        )
    }
}