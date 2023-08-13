package com.machimpyo.dot.ui.screen.login

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kakao.sdk.user.UserApiClient
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.ui.theme.LocalSpacing

@Preview
@Composable
fun LoginButtonPreview() {
    LogInScreen(navController = rememberNavController())
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: LogInViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val spacing = LocalSpacing.current
    val dotTypo = LocalDotTypo.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when(it) {
                is LogInViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }
                is LogInViewModel.Effect.ShowMessage -> {
                    val snackBarResult = snackbarHostState.showSnackbar(
                        message = it.message,
                        actionLabel = it.actionLabel,
                        duration = SnackbarDuration.Short
                    )

                    when(snackBarResult) {
                        SnackbarResult.ActionPerformed-> {
                            it.action()
                        }
                        SnackbarResult.Dismissed-> {
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
        ) {

            val (buttonRef) = createRefs()

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
                    authViewModel.kakaoLogin(context)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE500)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "카카오톡으로 로그인",
                    style = dotTypo.bodyMedium.copy(
                        color = Color.Black
                    )
                )
            }
        }
    }
}