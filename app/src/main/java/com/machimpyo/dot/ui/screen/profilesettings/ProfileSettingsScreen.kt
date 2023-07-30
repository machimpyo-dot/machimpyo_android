package com.machimpyo.dot.ui.screen

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.machimpyo.dot.ui.screen.profilesettings.ProfileSettingsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.ui.theme.LocalSpacing
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun Preview() {

    val rotateState = remember {
        Animatable(0f)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        color = Color.Black,
                        shape = RectangleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("이곳에 이름", color = Color.White)
            }
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        this.transformOrigin = TransformOrigin(0f, 0.5f)
                        this.cameraDistance = 90f
                        this.rotationY = rotateState.value
                    }
                    .size(200.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RectangleShape
                    ),
                contentAlignment = Alignment.Center

            ) {
                if (rotateState.value in -90f..0f) {
                    Text("이곳에 겉표지", color = Color.Black)
                }
            }
        }

        FastOutSlowInEasing

        TextButton(onClick = {
            coroutineScope.launch {
                val targetValue = if (rotateState.targetValue == 0f) -270f else 0f
                rotateState.animateTo(
                    targetValue,
                    tween(1500, delayMillis = 100)
                )
            }
        }) {
            Text("클릭")
        }
    }

}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class,
)
@Composable
fun ProfileSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ProfileSettingsViewModel = hiltViewModel()
) {

    val pagerState = rememberPagerState()

    val spacing = LocalSpacing.current
    val dotTypo = LocalDotTypo.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when(it) {
                is ProfileSettingsViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }
                is ProfileSettingsViewModel.Effect.ShowMessage -> {

                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "회원가입", style = dotTypo.DotAppBarTitle_Body_Small_Bold.copy(
                            color = Color.Black
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.backButtonClicked(navController)
                    }) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->


        ConstraintLayout(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            val (contentRef, indiRef) = createRefs()



            ProfileSettingsContent(
                modifier = Modifier
                    .constrainAs(contentRef) {

                    },
                pagerState = pagerState
            )
        }




    }


}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun NameInputPagerContent(
    modifier: Modifier = Modifier,
    nickname: String,
    nicknameChanged: (String) -> Unit,
    nextButtonClicked: () -> Unit
) {

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val coroutineScope = rememberCoroutineScope()

    val spacing = LocalSpacing.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = spacing.medium)
    ) {
        val (topComment, bottomButton, userInput) = createRefs()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(topComment) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }, horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center
        ) {
            Text(
                "편지에 사용할\n이름(닉네임)을 입력해주세요", style = TextStyle(
                    fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start
                ), modifier = Modifier.align(Alignment.Start)
            )
            Text(
                "편지지에 자동으로 입력돼요!", style = TextStyle(
                    fontSize = 14.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start
                ), modifier = Modifier.align(Alignment.Start)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(userInput) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "본인 이름", style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = Color.White
                ), modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(5.dp)
                    )
                    .padding(all = spacing.medium)
            )
            TextField(modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }, value = nickname, onValueChange = nicknameChanged, placeholder = {
                Text(
                    "본명이나 닉네임을 입력하세요.", style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = Color.LightGray
                    )
                )
            }, shape = RoundedCornerShape(
                topStart = 0.dp, topEnd = 0.dp, bottomStart = 5.dp, bottomEnd = 5.dp
            ), colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ), keyboardActions = KeyboardActions(onNext = {

            }), keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
            )
        }

        TextButton(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(bottomButton) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .bringIntoViewRequester(
                bringIntoViewRequester
            ),
            onClick = nextButtonClicked,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.LightGray
            ),
            enabled = nickname.isNotBlank()) {
            Text(
                "다음", style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start
                ), modifier = Modifier.padding(vertical = 10.dp)
            )
        }

    }

}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalPagerApi::class
)
@Composable
private fun ProfileSettingsContent(
    modifier: Modifier,
    pagerState: PagerState,
) {

    var nickname by remember {
        mutableStateOf("")
    }

    var isFocused by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()


    HorizontalPager(
        count = 3,
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
    ) { index ->

        when (index) {
            0 -> {
                NameInputPagerContent(
                    nickname = nickname,
                    nicknameChanged = {
                        nickname = it
                    },
                    nextButtonClicked = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    })
            }

            1 -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                }
            }
        }

    }


}