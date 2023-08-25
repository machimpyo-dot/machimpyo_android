package com.machimpyo.dot.ui.topappbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.machimpyo.dot.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoCenteredTopAppBar(
    navigation: @Composable () -> Unit = {},
    actionButtons: @Composable () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black
        ),
        title = {
//            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
        },
        navigationIcon = {navigation()},
        actions = {actionButtons()}
    )
}
