package com.machimpyo.dot.ui.screen.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakao.sdk.user.UserApiClient
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.popup.ProgressBar
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.ui.theme.LocalSpacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: LogInViewModel,
    authViewModel: AuthViewModel
) {

    val spacing = LocalSpacing.current
    val dotTypo = LocalDotTypo.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is LogInViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is LogInViewModel.Effect.ShowMessage -> {
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
        modifier = Modifier.fillMaxSize(),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            val (buttonRef, centerTextRef, subTextRef, imgRef) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.box),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(imgRef) {
                        bottom.linkTo(subTextRef.top, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Fit
            )

            Text(
                "진심인 마음 제대로 전달하고 싶으니까!",
                style = dotTypo.bodyMedium.copy(
                    color = DotColor.grey6,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.constrainAs(subTextRef) {
                    bottom.linkTo(centerTextRef.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            )

            Text(
                "마지막점을\n찍을 준비되었나요?",
                style = dotTypo.headlineLarge.copy(
                    color = DotColor.grey6,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.constrainAs(centerTextRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            )

            Button(
                modifier = Modifier
                    .height(56.dp)
                    .constrainAs(buttonRef) {
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                onClick = {
//                    viewModel.goToHomeScreen()
                    viewModel.setLoading(isLoading = true)
                    authViewModel.kakaoLogin(context) { isSuccessful->
                        if(isSuccessful) {
                            viewModel.setLoading(isLoading = false)
                        } else {
                            viewModel.loginFail()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE500)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (iconRef, textRef) = createRefs()

                    Icon(
                        painter = painterResource(id = R.drawable.kakao_icon),
                        contentDescription = null,
                        tint = Color(0xFF382929),
                        modifier = Modifier.constrainAs(iconRef) {
                            top.linkTo(textRef.top)
                            bottom.linkTo(textRef.bottom)
                            end.linkTo(textRef.start, margin = 32.dp)
                        }
                    )

                    Text(
                        "카카오톡으로 로그인",
                        style = dotTypo.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.constrainAs(textRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                    )
                }
            }
                
        }

        if(state.isLoading) {
            ProgressBar()
        }
    }
}