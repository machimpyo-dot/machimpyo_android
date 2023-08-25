package com.machimpyo.dot.ui.screen.select.color

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.ui.theme.LocalSpacing
import com.machimpyo.dot.ui.theme.dotTypo
import com.machimpyo.dot.utils.extension.LetterColorList
import com.machimpyo.dot.utils.extension.TextImportance
import com.machimpyo.dot.utils.extension.TextList

@Composable
fun SelectLetterColorScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SelectLetterColorViewModel = hiltViewModel<SelectLetterColorViewModel>()
) {

    val spacing = LocalSpacing.current
    val dotTypo = LocalDotTypo.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val context = LocalContext.current

    val backgroundTextList = TextList().apply {
        addText("what ")
        addText("color\n",TextImportance.IMPORTANT)
        addText("do you\n")
        addText("{like}?")
    }


    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is SelectLetterColorViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is SelectLetterColorViewModel.Effect.ShowMessage -> {
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
        modifier = modifier.fillMaxSize(),
//        topBar = {
//            CenterAlignedTopAppBar(title = {
//                Text("편지색상고르기화면")
//            })
//        },
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

            val (background, letterCarousel) = createRefs()

            BackgroundContent(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .fillMaxWidth()
                    .constrainAs(background) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    } ,
                color = Color.Black,
            ) {
                BackgroundText(
                    text = backgroundTextList,
                    style= MaterialTheme.dotTypo.displayMedium.copy(
                        fontSize = 52.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                val question: String = "원하는 편지지 색을 골라주세요!"

                Text(text = question,
                    style = MaterialTheme.dotTypo.bodyMedium,
                    color = Color.White)
            }

            CircularCarousel(
                numItems = LetterColorList.size,
                itemFraction = 0.25f,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .padding(horizontal = 20.dp)
                    .constrainAs(letterCarousel) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                ColorLetterFactory(viewModel = viewModel, index = it) {
                    viewModel.setColor(it)
                    Log.i(
                        "선택된 색상",
                        "${viewModel.state.value.selectedColor}번, 값 ${viewModel.state.value.selectedColor}"
                    )
                    viewModel.goToSelectDesignScreen()
                }
            }


        }
    }
}

@Composable
fun BackgroundContent(
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color,
    image: ImageBitmap? = null,
    content: @Composable () -> (Unit) = {}
    ) {

    val boxModifier = modifier

    Column(
        modifier = boxModifier.background(color).padding(20.dp),
        ){
        content()
    }
}

@Composable
fun BackgroundText(
    text: TextList,
    style: TextStyle = MaterialTheme.dotTypo.displayMedium,
) {
    text.textToComposable(style = style)
}