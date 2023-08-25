package com.machimpyo.dot.ui.screen.select.pattern

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.popup.MainDialog
import com.machimpyo.dot.ui.screen.select.DotPosition
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.ui.screen.select.LetterBackground
import com.machimpyo.dot.ui.screen.select.color.BackgroundContent
import com.machimpyo.dot.ui.screen.select.color.BackgroundText
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.dotTypo
import com.machimpyo.dot.ui.topappbar.Back
import com.machimpyo.dot.ui.topappbar.LogoCenteredTopAppBar
import com.machimpyo.dot.utils.extension.LetterColorList
import com.machimpyo.dot.utils.extension.LetterPatternList
import com.machimpyo.dot.utils.extension.TextImportance
import com.machimpyo.dot.utils.extension.TextList

@Composable
fun SelectLetterDesignScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: SelectLetterDesignViewModel = hiltViewModel()
) {
    var select by remember { mutableStateOf(false) }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val patternSize: Int = LetterPatternList.size

    var selectedPattern by remember {
        Log.i("고른패턴", ""+viewModel.state.value.selectedPattern)
        mutableStateOf(viewModel.state.value.selectedPattern)
    }

    val eachRotation:Float = 360.0f / (patternSize*4).toFloat()
    var nowRotation by remember{
        Log.i("현재회전", ""+selectedPattern + "/ ${eachRotation*selectedPattern} 도")
        mutableStateOf(0f - eachRotation*(selectedPattern))
    }

    val angle by animateFloatAsState(
        targetValue = nowRotation,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    val selectedLetterState by remember{ mutableStateOf(false)}
    
    val backgroundTextList = TextList(
        normalTextColor = Color.Black
    ).apply {
        addText("select\n")
        addText("design\n", TextImportance.IMPORTANT)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is SelectLetterDesignViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is SelectLetterDesignViewModel.Effect.ShowMessage -> {
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

        LetterSelectedPopup(visible = select, onClick = {
            viewModel.goToLetterWritecreen()
        })

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            val (background, selectedLetterColumn, circularList) = createRefs()


            BackgroundContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .constrainAs(background) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    } ,
                color = Color.Black,
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .constrainAs(selectedLetterColumn) {
                    top.linkTo(parent.top, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        nowRotation += eachRotation
                        if (selectedPattern > 0) {
                            selectedPattern--
                        } else {
                            selectedPattern = patternSize - 1
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.left),
                            contentDescription = "",
                            tint = Color.White,
                        )
                    }

                    Letter(
                        modifier = modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(18 / 40f),
                        color = LetterColorList[viewModel.state.value.selectedColor],
                        border= BorderStroke(width = 1.dp, color= DotColor.grey6),
                        background = {
                            LetterBackground(id = selectedPattern)
                        },
                        onClick = {
                            viewModel.setPattern(selectedPattern)
                            select = true
                        }
                    )

                    IconButton(onClick = {
                        nowRotation -= eachRotation
                        if (selectedPattern < patternSize - 1) {
                            selectedPattern ++
                        } else {
                            selectedPattern = 0
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.right),
                            contentDescription = "",
                            tint = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                BackgroundText(
                    text = backgroundTextList,
                    style = MaterialTheme.dotTypo.displayMedium.copy(
                        fontSize = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                val question: String = "편지지 디자인을 골라주세요!"
                Text(
                    text = question,
                    style = MaterialTheme.dotTypo.bodyMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                DotPosition(modifier = modifier.fillMaxWidth(0.1f), nowPosition = selectedPattern, total = patternSize)

                Spacer(modifier = Modifier.height(20.dp))
            }


            CircularList(
                modifier = Modifier
                    .constrainAs(circularList) {
                        top.linkTo(parent.bottom, -50.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .rotate(angle)
                ) {
                LetterDesignList(
                    patternSize = patternSize,
                    letterColor = LetterColorList[viewModel.state.value.selectedColor],
                )
            }
        }
    }
}

@Composable
fun LetterDesignList(letterColor: Color, patternSize: Int) {
    val letterPatternNum = patternSize
    val degree = 360.0f / (letterPatternNum*4)

    for (i in 0..letterPatternNum*4-1) {
        Letter(
            background = {
                LetterBackground(id = i % letterPatternNum)
             },
            modifier = Modifier
                .size(76.5.dp, 170.dp)
                .rotate((i) * degree),

            color= letterColor,

            border = BorderStroke(1.dp, DotColor.grey4),

        )
    }

}

@Composable
fun LetterSelectedPopup(visible: Boolean, onClick: () -> Unit = {}) {
    AnimatedVisibility(visible = visible) {

        MainDialog(
            mainText = "선택완료",
            mainIcon = painterResource(id = R.drawable.check),
            mainBody = {
                Button(
                    modifier = Modifier.size(63.dp, 30.dp),
                    onClick = onClick,
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DotColor.grey6,
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "확인",
                        style = MaterialTheme.dotTypo.labelMedium.copy(
                            fontWeight= FontWeight.SemiBold,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            )
                        ),
                    )
                }
            }
        )
    }
}