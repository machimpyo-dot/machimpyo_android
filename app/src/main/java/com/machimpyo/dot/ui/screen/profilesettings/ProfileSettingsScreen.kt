package com.machimpyo.dot.ui.screen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.ui.theme.LocalSpacing
import com.machimpyo.dot.utils.extension.toFormattedDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitDatePickerDialog(
    dismissButtonClicked: () -> Unit,
    confirmButtonClicked: (DatePickerState) -> Unit,
    initialSelectedDateMillis: Long? = null
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)

    Log.e("TAG", "전달받은 밀리: $initialSelectedDateMillis")

    DatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                confirmButtonClicked(state)
            }) {
                Text(
                    "확인", style = MaterialTheme.typography.bodyMedium.copy(
                        color = DotColor.primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = dismissButtonClicked) {
                Text(
                    "닫기", style = MaterialTheme.typography.bodyMedium.copy(
                        color = DotColor.grey4,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = DotColor.white
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        DatePicker(
            state = state,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = DotColor.white,
                selectedDayContainerColor = DotColor.primaryColor,
                selectedDayContentColor = DotColor.white,
                todayContentColor = DotColor.primaryColor,
                todayDateBorderColor = DotColor.primaryColor,
                headlineContentColor = DotColor.grey6,
                selectedYearContainerColor = DotColor.primaryColor,
                selectedYearContentColor = DotColor.white
            ),
            title = {},
        )
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

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var isDatePickerVisible by remember {
        mutableStateOf(false)
    }

    val state = viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is ProfileSettingsViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is ProfileSettingsViewModel.Effect.ShowMessage -> {
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

                is ProfileSettingsViewModel.Effect.HandleDatePicker -> {
                    isDatePickerVisible = it.isVisible
                }
            }
        }
    }

    val progress by animateFloatAsState(
        targetValue = pagerState.currentPage.toFloat().div(3),
        label = ""
    )


    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) {
                Snackbar {
                    Text(it.visuals.message)
                }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "회원가입", style = dotTypo.bodyMedium.copy(
                            color = DotColor.grey6,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (pagerState.currentPage == 0) {
                            viewModel.backButtonClicked(navController)
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }

                    }) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->


        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val (contentRef, indiRef) = createRefs()

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(
                        indiRef
                    ) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                color = DotColor.primaryColor,
                trackColor = DotColor.grey1
            )


            ProfileSettingsContent(
                modifier = Modifier
                    .constrainAs(contentRef) {
                        top.linkTo(indiRef.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                pagerState = pagerState,
                navController = navController
            )
        }

        if (isDatePickerVisible) {
            ExitDatePickerDialog(
                dismissButtonClicked = {
                    viewModel.handleDatePicker(false)
                }, confirmButtonClicked = { datePickerState ->
                    datePickerState.selectedDateMillis?.let {
                        viewModel.handleExitDate(it)
                    }
                    viewModel.handleDatePicker(false)
                },
                initialSelectedDateMillis = state.value.exitDate
            )
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
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
                "편지에 사용할\n이름(닉네임)을 입력해주세요", style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                ), modifier = Modifier.align(Alignment.Start)
            )
            Text(
                "편지지에 자동으로 입력돼요!", style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
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
                "본인 이름", style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = DotColor.primaryColor
                ), modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = spacing.small)
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = DotColor.primaryColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    },
                value = nickname,
                onValueChange = nicknameChanged,
                placeholder = {
                    Text(
                        "본명이나 닉네임을 입력하세요.", style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            color = DotColor.grey3
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = DotColor.grey6,
                    cursorColor = DotColor.primaryColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardActions = KeyboardActions(onNext = { nextButtonClicked() }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
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
                "다음", style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                    color = DotColor.white
                ), modifier = Modifier.padding(vertical = 10.dp)
            )
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExitDateInputPagerContent(
    modifier: Modifier = Modifier,
    nextButtonClicked: () -> Unit,
    viewModel: ProfileSettingsViewModel = hiltViewModel()
) {

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val coroutineScope = rememberCoroutineScope()

    val spacing = LocalSpacing.current

    val state = viewModel.state.collectAsState()

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
                "언제 퇴사 예정이신가요?", style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                ), modifier = Modifier.align(Alignment.Start)
            )
            Text(
                "퇴사 일정에 맞춰 편지를 관리해보세요", style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
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
                "퇴사 예정일", style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = DotColor.primaryColor
                ), modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = spacing.small)
            )

            OutlinedButton(
                onClick = {
                    if(state.value.isNotAssigned) {
                        viewModel.showMessageForAssigningExitDate()
                        return@OutlinedButton
                    }
                    viewModel.handleDatePicker(true)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = DotColor.white,
                    contentColor = DotColor.primaryColor,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                val commentOrExitDate = state.value.exitDate?.toFormattedDate() ?: "퇴사 예정일 설정"

                Text(
                    commentOrExitDate, style = MaterialTheme.typography.bodyMedium.copy(
                        color = DotColor.grey6
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "퇴사 결정을 아직 못했어요 :(", style = MaterialTheme.typography.bodyMedium.copy(
                        color = DotColor.grey5,
                    ),
                    modifier = Modifier.padding(start = spacing.small)
                )

                Checkbox(
                    checked = state.value.isNotAssigned, onCheckedChange = {
                        viewModel.handleNotAssignedExitDate(it)
                    },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = DotColor.grey3,
                        checkedColor = DotColor.primaryColor,
                        checkmarkColor = DotColor.white
                    )
                )

            }
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
            )) {
            Text(
                "완료", style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                    color = DotColor.white
                ), modifier = Modifier.padding(vertical = 10.dp)
            )
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompanyInputPagerContent(
    modifier: Modifier = Modifier,
    company: String,
    companyChanged: (String) -> Unit,
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
                "현재 다니고 있는\n회사가 어딘지 알려주세요", style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                ), modifier = Modifier.align(Alignment.Start)
            )
            Text(
                "편지들을 회사별로 관리해보세요!", style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
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
                "회사 이름", style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = DotColor.primaryColor
                ), modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = spacing.small)
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = DotColor.primaryColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    },
                value = company,
                onValueChange = companyChanged,
                placeholder = {
                    Text(
                        "퇴사할 회사를 입력해주세요", style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            color = DotColor.grey3
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = DotColor.grey6,
                    cursorColor = DotColor.primaryColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardActions = KeyboardActions(onNext = { nextButtonClicked() }),
                keyboardOptions = KeyboardOptions(
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
            enabled = company.isNotBlank()) {
            Text(
                "다음", style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start,
                    color = DotColor.white
                ), modifier = Modifier.padding(vertical = 10.dp)
            )
        }

    }

}

@OptIn(
    ExperimentalPagerApi::class
)
@Composable
private fun ProfileSettingsContent(
    modifier: Modifier,
    pagerState: PagerState,
    navController: NavController,
    viewModel: ProfileSettingsViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()

    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        count = 3,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false,
        state = pagerState,
    ) { index ->

        when (index) {
            0 -> {
                NameInputPagerContent(
                    nickname = state.value.nickname,
                    nicknameChanged = {
                        viewModel.handleNickname(it)
                    },
                    nextButtonClicked = {
                        coroutineScope.launch {
                            focusManager.clearFocus()
                            pagerState.animateScrollToPage(1)
                        }
                    })
            }

            1 -> {
                CompanyInputPagerContent(company = state.value.company, companyChanged = {
                    viewModel.handleCompany(it)
                }, nextButtonClicked = {
                    coroutineScope.launch {
                        focusManager.clearFocus()
                        pagerState.animateScrollToPage(2)
                    }
                })
            }

            else -> {
                ExitDateInputPagerContent(
                    modifier = Modifier.fillMaxSize(),
                    nextButtonClicked = {
                        viewModel.goToHomeScreen(navController = navController)
                    }
                )
            }
        }

    }


}