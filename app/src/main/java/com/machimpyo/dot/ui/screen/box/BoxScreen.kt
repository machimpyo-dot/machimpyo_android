package com.machimpyo.dot.ui.screen.box

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.utils.extension.clickableWithoutRipple
import com.machimpyo.dot.utils.extension.hasBackStackEntry
import eu.wewox.tagcloud.TagCloud
import eu.wewox.tagcloud.rememberTagCloudState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BoxScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BoxViewModel?
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val state = viewModel?.state?.collectAsState()

    val dotTypo = LocalDotTypo.current

    if (viewModel != null) {
        LaunchedEffect(viewModel) {
            viewModel.effect.collect {
                when (it) {
                    is BoxViewModel.Effect.NavigateTo -> {
                        navController.navigate(it.route, it.builder)
                    }

                    is BoxViewModel.Effect.ShowMessage -> {
                        val snackBarResult = snackbarHostState.showSnackbar(
                            message = it.message,
                            actionLabel = it.actionLabel,
                            duration = SnackbarDuration.Short
                        )

                        when (snackBarResult) {
                            SnackbarResult.ActionPerformed -> {
                                it.action()
                            }

                            SnackbarResult.Dismissed -> {
                                it.dismissed()
                            }
                        }
                    }
                }
            }
        }
    }


    Scaffold(modifier = modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState
        ) {
            Snackbar {
                Text(it.visuals.message)
            }
        }

    }, topBar = {
        CenterAlignedTopAppBar(title = { }, navigationIcon = {
            if (navController.hasBackStackEntry()) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        tint = DotColor.grey6,
                        contentDescription = null
                    )
                }
            }
        })
    }) { innerPadding ->

        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("box.json"))
        val lottieState = animateLottieCompositionAsState(
            composition,
            iterations = 1,
            speed = 0.8f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LottieAnimation(
                composition = composition,
                progress = { lottieState.progress },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(200.dp)

            )

            AnimatedVisibility(
                visible = lottieState.progress > 0.9f,
                enter = fadeIn() + slideInVertically(
                    animationSpec = tween(durationMillis = 400),
                    initialOffsetY = {
                        it / 2
                    }
                ),
                exit = fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                Box(modifier = Modifier
                    .clickableWithoutRipple {
                        viewModel?.goToMyPageScreen()
                    }) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(1.2f),
                        painter = painterResource(id = R.drawable.vector_169),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = DotColor.primaryColor),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        "My Letter Box",
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White
                        )
                    )
                }
            }



            AnimatedVisibility(
                visible = lottieState.progress > 0.9f,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut()
            ) {
                TagCloud(
                    state = rememberTagCloudState(), modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {

                    state?.value?.letterNames?.let { letterNames ->
                        items(letterNames) { letterName ->

                            val clickModifier = if(letterName.letterUid != null) {
                                Modifier.tagCloudItemFade()
                                    .tagCloudItemScaleDown()
                                    .clickable {
                                        viewModel.goToLetterCheckScreen()
                                    }
                            } else {
                                Modifier.tagCloudItemFade()
                                    .tagCloudItemScaleDown()
                                    .clickable {
                                        //TODO - 할수도 안 할 수도
                                    }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = clickModifier

                            ) {

                                val color = if(letterName.letterUid == null) Color.Black else DotColor.primaryColor

                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(color = color)
                                )

                                if (letterName.letterUid != null) {
                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        letterName.nickName ?: "",
                                        style = dotTypo.bodyMedium.copy(
                                            color = color,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                }

                            }
                        }
                    }

                }
            }


        }
    }
}
