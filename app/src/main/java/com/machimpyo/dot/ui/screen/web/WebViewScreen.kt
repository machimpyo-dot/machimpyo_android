package com.machimpyo.dot.ui.screen.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.utils.extension.hasBackStackEntry


@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: WebViewViewModel?,
    url: String
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val state = viewModel?.state?.collectAsState()


    val dotTypo = LocalDotTypo.current

    LaunchedEffect(viewModel) {
        viewModel?.initState(url)
    }

    if (viewModel != null) {
        LaunchedEffect(viewModel) {
            viewModel.effect.collect {
                when (it) {
                    is WebViewViewModel.Effect.NavigateTo -> {
                        navController.navigate(it.route, it.builder)
                    }

                    is WebViewViewModel.Effect.ShowMessage -> {
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


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
                CenterAlignedTopAppBar(
                    title = {
                            Text("관련 사이트", style = dotTypo.bodyMedium.copy(
                                color = DotColor.grey6,
                                fontWeight = FontWeight.SemiBold
                            ))
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                tint = DotColor.grey5,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
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

        state?.value?.url?.let {
            val webViewState = rememberWebViewState(url = it)

            val webClient = remember {
                object: AccompanistWebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                    }
                }

            }

            WebView(
                state = webViewState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onCreated = { webView->
                    webView.settings.javaScriptEnabled = true
                },
                client = webClient
            )
        }

        if(state?.value?.url == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("유효하지 않은 URL입니다", style = dotTypo.bodyMedium.copy(
                    color = DotColor.grey6
                ))
            }
        }

    }
}