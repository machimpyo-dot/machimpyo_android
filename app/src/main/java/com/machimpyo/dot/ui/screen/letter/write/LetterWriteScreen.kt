package com.machimpyo.dot.ui.screen.letter.write

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.popup.MainDialog
import com.machimpyo.dot.ui.popup.SelectButton
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.ui.screen.select.LetterBackground
import com.machimpyo.dot.ui.screen.select.LetterContent
import com.machimpyo.dot.ui.screen.select.LetterTextRemain
import com.machimpyo.dot.ui.screen.select.LetterTitle
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.ui.topappbar.Back
import com.machimpyo.dot.ui.topappbar.LogoCenteredTopAppBar
import com.machimpyo.dot.ui.topappbar.Save
import com.machimpyo.dot.ui.topappbar.Send
import com.machimpyo.dot.utils.extension.LetterColorList

@Composable
fun LetterWriteScreen(
    modifier: Modifier = Modifier
        .background(Color.Black),
    navController: NavController,
    viewModel: LetterWriteViewModel = hiltViewModel()
) {

    //뒤로가기 버튼 금지 처리
    BackHandler(){}

    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }



    var back by remember { mutableStateOf(false) }
    var send by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        SaveBottomSheet(
            onDismiss = {showBottomSheet = false}
        )
    }

    BackPopup(
        visible = back,
        deleteOnClick = {
            navController.popBackStack()
        },
        saveOnClick = {
            viewModel.save()
            viewModel.goToHomeScreen()
        },
        backOnClick = {back = false},
    )

    SendPopup(
        visible = send,
        checkOnClick = {
            send = false
            viewModel.goToHomeScreen()
        },
        url= state.letter.url!!
    )

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is LetterWriteViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is LetterWriteViewModel.Effect.ShowMessage -> {
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
                             back = true
                         }
                     )
                },
                actionButtons = {
                    Send(
                        onClick = {
                            //coroutine으로
                            send = true
                            viewModel.send()
                        }
                    )

                    Save(
                        onClick = {
                            showBottomSheet = true
                        }
                    )
                }
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
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
                .padding(20.dp)
                ,
            ) {

            Letter(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 18 / 40f),
                background = { LetterBackground(id = state.selectedPattern) },
                color = LetterColorList[state.selectedColor],
                clickable = true,
                isClickIndication = false,
                ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    LetterTitle(
                        title = state.letter.title,
                        hint = "사장님께",
                        onValueChange = viewModel::titleValueChange
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LetterContent(
                        content = state.letter.contents,
//                        maxLine = state.letterConfig.contentMaxLine,
                        hint = "그동안 감사했습니다...",
                        onValueChange = viewModel::contentValueChange
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LetterTextRemain(
                        now = state.letter.contents.length,
                        max = state.letterConfig.contentMaxLength,
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.End)
                    )
                }

            }
        }
    }
}

@Composable
fun BackPopup(
    visible: Boolean,
    deleteOnClick: () -> Unit = {},
    saveOnClick: () -> Unit = {},
    backOnClick: () -> Unit = {},
) {
    AnimatedVisibility(visible = visible) {

        MainDialog(
            mainText = "편지지를 다시 고르시면\n글이 삭제됩니다.",
        ) {
            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DotColor.grey3)
                )

                SelectButton(
                    text = "삭제",
                    isCrucial = true,
                    onClick = deleteOnClick
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DotColor.grey3)
                )

                SelectButton(
                    text = "임시저장",
                    isCrucial = false,
                    onClick = saveOnClick
                )


                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DotColor.grey3)
                )

                SelectButton(
                    text = "취소",
                    isCrucial = false,
                    onClick = backOnClick
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DotColor.grey3)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPopup(
    visible: Boolean,
    url: String,
    checkOnClick: () -> Unit = {},
) {
    val dotTypo = LocalDotTypo.current

    AnimatedVisibility(visible= visible) {

        MainDialog(
            mainIcon = painterResource(id = R.drawable.heart),
            mainText = "편지 완료"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = "링크 공유",
                    style = dotTypo.labelSmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "해당 양식은 URL에서 공유할 수 있습니다.",
                    style = dotTypo.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,),
                    color = DotColor.grey5
                )

                Spacer(modifier = Modifier.height(10.dp))


                val URLColor = DotColor.grey4
                val clipboardManager = LocalClipboardManager.current

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(0.87.dp, URLColor)
                        )
                        .padding(10.dp),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier= Modifier.fillMaxWidth(0.8f),
                        text = url,
                        style = dotTypo.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            ),
                        ),
                        color = URLColor,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    //접근성 (IconButton 기본 사이즈 48x48를 없애기 위함)
                    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                        IconButton(
                            onClick = {
                                //생성된 url 복사
                                clipboardManager.setText(
                                    AnnotatedString(url)
                                )
                            },
                            modifier= Modifier.size(15.dp)
                            ) {
                            Icon(
                                painter = painterResource(id = R.drawable.copy),
                                contentDescription = "copy",
                                tint = URLColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                ConfirmButton(
                    onClick = checkOnClick
                )
            }
        }
    }
}

@Composable
fun ConfirmButton(
    onClick: () -> Unit = {}
) {
    val dotTypo = LocalDotTypo.current

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
            style = dotTypo.labelMedium.copy(
                fontWeight= FontWeight.SemiBold,
                textAlign= TextAlign.Center ,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            ),
        )
    }
}
