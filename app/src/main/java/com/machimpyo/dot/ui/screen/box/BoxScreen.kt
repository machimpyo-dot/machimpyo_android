package com.machimpyo.dot.ui.screen.box

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.utils.extension.clickableWithoutRipple
import eu.wewox.tagcloud.TagCloud
import eu.wewox.tagcloud.rememberTagCloudState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BoxViewModel = hiltViewModel()
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

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

    Scaffold(modifier = modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState
        ) {
            Snackbar {
                Text(it.visuals.message)
            }
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

        }
    }
}


@Preview
@Composable
fun BoxContentsPreview() {
    BoxContents(
        navController = rememberNavController()
    )
}

@Composable
fun BoxContents(
    modifier: Modifier = Modifier,
    navController: NavController,
    names: List<String> = List(1) { "이름 예시$it" }
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(modifier = Modifier.align(Alignment.Center)
            .size(200.dp)
            .background(
                color = Color.Gray
            )
        )


        Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .clickableWithoutRipple {

            }) {
            Image(modifier = Modifier.fillMaxWidth().scale(1.2f),
                painter = painterResource(id = R.drawable.vector_169),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = DotColor.primaryColor),
                contentScale = ContentScale.Fit
            )
            Text("My Letter Box", modifier = Modifier.padding(horizontal = 30.dp, vertical = 16.dp), style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White
            ))
        }


        TagCloud(state = rememberTagCloudState(), modifier = Modifier.padding(64.dp).align(Alignment.Center)
        ) {
            items(names) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .tagCloudItemFade()
                        .tagCloudItemScaleDown()
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(it, style = MaterialTheme.typography.labelLarge)
                }
            }
        }



    }
}