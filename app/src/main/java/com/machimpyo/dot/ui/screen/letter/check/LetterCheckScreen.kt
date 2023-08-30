package com.machimpyo.dot.ui.screen.letter.check

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.machimpyo.dot.data.model.LetterConfig
import com.machimpyo.dot.service.FirebaseDeepLinkService
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.ui.screen.select.LetterBackground
import com.machimpyo.dot.ui.screen.select.LetterContent
import com.machimpyo.dot.ui.screen.select.LetterTitle
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.topappbar.Back
import com.machimpyo.dot.ui.topappbar.LogoCenteredTopAppBar
import com.machimpyo.dot.utils.extension.LetterColorList
import kotlinx.coroutines.async

@Composable
fun LetterCheckScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: LetterCheckViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(viewModel) {
//        if(userState.isDeeplink) viewModel.openDeepLink()

        viewModel.effect.collect {
            when (it) {
                is LetterCheckViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is LetterCheckViewModel.Effect.ShowMessage -> {
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

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            LogoCenteredTopAppBar(
                navigation = {
                    Back(
                        onCLick = {
                            navController.popBackStack()
                        }
                    )
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) {
                Snackbar {
                    Text(it.visuals.message)
                }
            }
        }
    ) { innerPadding ->

        ConstraintLayout(
            modifier= modifier
                .background(DotColor.black)
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
                .padding(20.dp)
            ,
        ) {

            Letter(
                modifier= Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 18 / 40f),
                background ={ LetterBackground(id = state.selectedPattern) },
                color = viewModel.getSelectedColor()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                ) {
                    Spacer(modifier= Modifier.height(50.dp))

                    LetterTitle(
                        title = state.letter.title,
                        hint= "",
                        readOnly = true,
                    )

                    Spacer(modifier= Modifier.height(20.dp))

                    LetterContent(
                        content = state.letter.content,
//                        maxLine = state.letterConfig.contentMaxLine,
                        hint = "",
                        readOnly = true,
                    )

                    Spacer(modifier= Modifier.height(20.dp))
                }

            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    val letter = com.machimpyo.dot.data.model.Letter.getMock()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            LogoCenteredTopAppBar(
                navigation = {
                    Back(
                        onCLick = {
                        }
                    )
                },
            )
        },
    ) { innerPadding ->

        ConstraintLayout(
            modifier = Modifier
                .background(DotColor.black)
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
                .padding(20.dp),
        ) {
            Letter(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 18 / 40f),
                background = { LetterBackground(id = 4) },
                color = LetterColorList[2]
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    LetterTitle(
                        title = letter.title,
                        hint = "",
                        readOnly = true,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LetterContent(
                        content = letter.content,
//                        maxLine = letterConfig.contentMaxLine,
                        hint = "",
                        readOnly = true,
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

            }
        }
    }
}