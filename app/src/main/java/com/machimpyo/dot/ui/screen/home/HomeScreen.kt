package com.machimpyo.dot.ui.screen


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.machimpyo.dot.R
import com.machimpyo.dot.data.model.ExitState
import com.machimpyo.dot.data.model.response.AbstractLetter
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.screen.home.HomeViewModel
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.ui.theme.LocalSpacing
import com.machimpyo.dot.ui.theme.pressStart2P
import com.machimpyo.dot.utils.extension.toDDay
import com.machimpyo.dot.utils.extension.toLocalDate
import kotlinx.coroutines.flow.filter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel,
    authViewModel: AuthViewModel
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var isDatePickerVisible by remember {
        mutableStateOf(false)
    }

    val state by viewModel.state.collectAsState()

    val userState by authViewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        viewModel.initState(userState.userInfo)
    }


    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is HomeViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is HomeViewModel.Effect.ShowMessage -> {
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

                is HomeViewModel.Effect.HandleDatePicker -> {
                    isDatePickerVisible = it.isVisible
                }
            }
        }
    }

    HomeContents(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        snackbarHostState = snackbarHostState,
        exitDayState = ExitState.fromExitDate(state.exitDate?.toLocalDate()),
        nickname = state.nickname,
        exitDate = state.exitDate,
        profileUrl = state.profileUrl,
        company = state.company,
        abstractLetters = state.abstractLetters,
        viewModel = viewModel
    )

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
            initialSelectedDateMillis = state.exitDate
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContents(
    modifier: Modifier = Modifier,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    exitDayState: ExitState,
    nickname: String?,
    exitDate: Long?,
    profileUrl: String?,
    company: String?,
    abstractLetters: List<AbstractLetter>,
    viewModel: HomeViewModel,
) {


    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) {
                Snackbar {
                    Text(it.visuals.message)
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.goToSelectColorScreen()
                },
                containerColor = DotColor.primaryColor,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pencil),
                    tint = Color.White,
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val photoUrl = "https://randomuser.me/api/portraits/men/75.jpg"
            /*
            여기에서 필요한 데이터 정리
            profileUrl, 사용자 이름, 퇴사일, 회사이름
             */

            when (exitDayState) {
                ExitState.IsNotAssigned -> {

                    ExitDayBeforeContents(
                        profileUrl = profileUrl,
                        name = nickname,
                        exitDate = exitDate?.toLocalDate(),
                        content = {
                            CalendarAndContents(nickname, exitDate?.toLocalDate())
                        },
                        profileClicked = viewModel::goToBoxScreen
                    )
                }

                ExitState.BeforeExit -> {
                    ExitDayBeforeContents(
                        profileUrl = profileUrl,
                        name = nickname,
                        exitDate = exitDate?.toLocalDate(),
                        content = {
                            CalendarAndContents(nickname, exitDate?.toLocalDate())
                        },
                        profileClicked = viewModel::goToBoxScreen
                    )
                }

                ExitState.ExitDay -> {
                    ExitDayTodayContents(
                        profileUrl = profileUrl,
                        name = nickname,
                        exitDate = exitDate?.toLocalDate(),
                        company = company,
                        abstractLetters = abstractLetters,
                        content = {
                            CalendarAndContents(nickname, exitDate?.toLocalDate())
                        },
                        profileClicked = viewModel::goToBoxScreen
                    )
                }

                ExitState.AfterExit -> {
                    ExitDayAfterContents(
                        profileUrl = profileUrl,
                        name = nickname,
                        exitDate = exitDate?.toLocalDate(),
                        content = { CalendarAndContents(nickname, exitDate?.toLocalDate()) },
                        profileClicked = viewModel::goToBoxScreen
                    )
                }
            }
        }

    }
}


@Composable
fun CalendarAndContents(
    name: String?,
    exitDate: LocalDate?,
    modifier: Modifier = Modifier,
) {

    val dotTypo = LocalDotTypo.current
    val viewModel: HomeViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        CalendarContent(
            nickname = name,
            exitDate = exitDate,
            exitDateEditClicked = {
                viewModel.handleDatePicker(true)
            },
            modifier = Modifier.padding(
                top = 64.dp,
                start = 16.dp,
                end = 16.dp
            )
        )

        Spacer(
            modifier = Modifier
                .height(32.dp)
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
        )

        Divider(modifier = Modifier.fillMaxWidth(), color = DotColor.grey1, thickness = 4.dp)

        Spacer(
            modifier = Modifier
                .height(32.dp)
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = DotColor.primaryColor
                    )
                ) {
                    append("콘텐츠")
                }
                append("를 추천해드려요")
            }, style = dotTypo.headlineLarge.copy(
                color = DotColor.black,
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )
        Text(
            text = "퇴사센스 닷팀이 알려드립니다", style = dotTypo.bodyMedium.copy(
                color = DotColor.grey5,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(state.contents) { content ->
                ContentItem(
                    thumbnail = content.thumbnail,
                    title = content.title,
                    subtitle = content.subtitle,
                    tags = content.hashTags
                ) {
                    viewModel.goToContentDetailScreen(content.contentUid)
                }
            }
        }
    }
}


@Composable
fun ContentItem(
    thumbnail: Int,
    title: String,
    tags: List<String>,
    subtitle: String,
    cardClicked: () -> Unit
) {

    val dotTypo = LocalDotTypo.current

    Card(
        modifier = Modifier
            .width(240.dp)
            .wrapContentHeight()
            .padding(vertical = 16.dp)
            .clickable {
                cardClicked()
            },
        colors = CardDefaults.elevatedCardColors(
            containerColor = DotColor.grey1
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp,
            focusedElevation = 10.dp,
            hoveredElevation = 10.dp,
            draggedElevation = 10.dp,
            disabledElevation = 10.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Image(
                painter = painterResource(id = thumbnail),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
            )

            Text(
                title,
                style = dotTypo.labelSmall.copy(
                    color = DotColor.black,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                color = DotColor.grey2
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach {
                    Text(
                        "#$it", style = dotTypo.labelSmall.copy(
                            color = DotColor.primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Text(
                subtitle,
                style = dotTypo.labelSmall.copy(
                    color = DotColor.black,
                    fontWeight = FontWeight.ExtraLight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ExitDayBeforeContents(
    profileUrl: String?,
    name: String?,
    exitDate: LocalDate?, // null 이면 미정
    content: @Composable () -> Unit, //이름, 퇴사날,
    profileClicked: () -> Unit
) {

    val dotTypo = LocalDotTypo.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {


            AsyncImage(
                model = profileUrl,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        border = BorderStroke(width = 1.dp, color = DotColor.primaryColor),
                        shape = CircleShape
                    )
                    .size(30.dp)
                    .clickable {
                        profileClicked()
                    },
                placeholder = painterResource(id = R.drawable.dot_icon),
                error = painterResource(id = R.drawable.dot_icon),
                fallback = painterResource(id = R.drawable.dot_icon)
            )


            Spacer(modifier = Modifier.width(16.dp))

            Text(
                "Hi, $name", style = dotTypo.bodyMedium.copy(
                    color = DotColor.white
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            exitDate?.toDDay()?.let {
                Text(
                    "D-$it", style = dotTypo.displayMedium.copy(
                        color = DotColor.primaryColor
                    )
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "LETTER\nBox", style = dotTypo.displayMedium.copy(
                        color = DotColor.white
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                ) {
                    content()
                }
            }


            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(250.dp)
                    .align(Alignment.TopEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.box1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }


    }
}

@Composable
fun ExitDayTodayContents(
    profileUrl: String?,
    name: String?,
    exitDate: LocalDate?, // null 이면 미정
    company: String?,
    abstractLetters: List<AbstractLetter>,
    content: @Composable () -> Unit, //이름, 퇴사날,
    profileClicked: () -> Unit
) {
    val dotTypo = LocalDotTypo.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = Color.Black)
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = profileUrl,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        border = BorderStroke(width = 1.dp, color = DotColor.primaryColor),
                        shape = CircleShape
                    )
                    .size(50.dp)
                    .clickable {
                        profileClicked()
                    },
                placeholder = painterResource(id = R.drawable.dot_icon),
                error = painterResource(id = R.drawable.dot_icon),
                fallback = painterResource(id = R.drawable.dot_icon)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "${name}님\n마지막날을 축하해요!", style = dotTypo.bodyMedium.copy(
                    color = DotColor.white,
                    textAlign = TextAlign.Center
                )
            )
        }


        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "${company}에서 받은 편지를 확인해보세요", style = dotTypo.bodyMedium.copy(
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CardStackContent(
                abstractLetters
            )
        }

        Box(
            modifier = Modifier.background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF6610),
                        Color(0xFFFF8E3D),
                        Color(0xFFFFA337),
                        Color(0xFFFFA337),
                        Color(0xFFFFA337),
                        Color(0xFFFFA337)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                content()
            }
        }


    }

}

@Composable
fun ExitDayAfterContents(
    profileUrl: String?,
    name: String?,
    exitDate: LocalDate?, // null 이면 미정
    content: @Composable () -> Unit, //이름, 퇴사날
    profileClicked: () -> Unit
) {
    val dotTypo = LocalDotTypo.current

    val configuration = LocalConfiguration.current

    Box(
        modifier = Modifier.background(color = Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.box3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(configuration.screenWidthDp.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {


                AsyncImage(
                    model = profileUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(30.dp)
                        .clickable {
                            profileClicked()
                        },
                    placeholder = painterResource(id = R.drawable.dot_icon),
                    error = painterResource(id = R.drawable.dot_icon),
                    fallback = painterResource(id = R.drawable.dot_icon)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    "Hi, $name", style = dotTypo.bodyMedium.copy(
                        color = DotColor.white
                    )
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "LETTER\nBox",
                    style = dotTypo.displayMedium.copy(
                        color = DotColor.white
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        //TODO (받은 편지 확인할 수 있게 버튼 처리)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DotColor.grey6
                    ),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        "받은 편지", style = dotTypo.bodyMedium.copy(
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )

            ) {
                content()
            }


        }
    }
}

@Composable
fun rememberFirstVisibleWeekAfterScroll(state: WeekCalendarState): Week {
    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleWeek.value = state.firstVisibleWeek }
    }
    return visibleWeek.value
}

fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}


fun getWeekPageTitle(week: Week): String {
    val firstDate = week.days.first().date
    val lastDate = week.days.last().date
    return when {
        firstDate.yearMonth == lastDate.yearMonth -> {
            firstDate.yearMonth.displayText()
            "${firstDate.year} ${firstDate.month.displayText(short = false)}"
        }

        firstDate.year == lastDate.year -> {
            "${lastDate.year} ${firstDate.month.displayText(short = false)}"
        }

        else -> {
            "${lastDate.year} ${firstDate.month.displayText(short = false)}"
        }
    }
}


@Composable
fun CalendarContent(
    nickname: String?,
    exitDate: LocalDate?,
    exitDateEditClicked: (WeekCalendarState) -> Unit,
    modifier: Modifier = Modifier
) {

    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startDate = remember { currentMonth.minusMonths(100).atStartOfMonth() } // Adjust as needed
    val endDate = remember { currentMonth.plusMonths(100).atEndOfMonth() } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val state = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = firstDayOfWeek
    )
    val visibleWeek = rememberFirstVisibleWeekAfterScroll(state)

    val spacing = LocalSpacing.current
    val dotTypo = LocalDotTypo.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = buildAnnotatedString {
                append("${nickname ?: "(익명)"}님의 ")
                withStyle(
                    style = SpanStyle(
                        color = DotColor.primaryColor
                    )
                ) {
                    append("퇴사")
                }
                append("일정")
            }, style = dotTypo.headlineLarge.copy(
                color = DotColor.black,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            "퇴사 전에 미리 글을 써보는 건 어때요?", style = dotTypo.bodyMedium.copy(
                color = DotColor.grey5,
                fontWeight = FontWeight.Medium
            )
        )



        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        getWeekPageTitle(visibleWeek),
                        style = dotTypo.bodyMedium.copy(
                            color = DotColor.grey6
                        )
                    )
                }

                IconButton(
                    onClick = {
                        exitDateEditClicked(state)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        tint = DotColor.black,
                        contentDescription = null,
                        modifier = Modifier.rotate(90f)
                    )
                }
            }

            WeekCalendar(
                state = state,
                dayContent = {
                    Day(it, exitDate ?: LocalDate.now(ZoneId.systemDefault()))
                },
                weekHeader = {

                }
            )
        }


    }
}


@Composable
fun CardStackContent(
    abstractLetter: List<AbstractLetter>,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current

    val letterWidth = configuration.screenWidthDp.div(6).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFFFA337)),
        contentAlignment = Alignment.CenterStart
    ) {

        val colors = listOf(
            Color(0xFFFF6610),
            Color(0xFFFF8E3D),
            Color(0xFFFFA337),
            Color(0xFFFFA337),
            Color(0xFFFFA337),
            Color(0xFFFFA337)
        )

        "LETTER".forEachIndexed { index, char ->
            ConstraintLayout(
                modifier = Modifier
                    .size(width = letterWidth, height = 200.dp)
                    .zIndex(-index.toFloat())
                    .offset(x = letterWidth * index)
                    .shadow(15.dp)
                    .background(
                        color = colors[index]
                    )
            ) {

                val (imgRef, charRef) = createRefs()

                Box(modifier = Modifier
                    .clip(CircleShape)
                    .size(letterWidth.times(0.4f))
                    .constrainAs(imgRef) {
                        top.linkTo(parent.top, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                    }) {

                    val profileUrl = abstractLetter.getOrNull(index)?.senderProfileUrl

                    AsyncImage(
                        model = profileUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.dot_icon),
                        fallback = painterResource(id = R.drawable.dot_icon),
                        placeholder = painterResource(id = R.drawable.dot_icon)
                    )

                }

                Text(
                    char.toString(), style = androidx.compose.ui.text.TextStyle(
                        fontFamily = pressStart2P,
                        fontSize = 60.sp,
                        color = DotColor.black
                    ),
                    modifier = Modifier.constrainAs(charRef) {
                        top.linkTo(imgRef.bottom, margin = 16.dp)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

            }
        }
    }


}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

@Composable
fun Day(day: WeekDay, exitDay: LocalDate) {

    val dotTypo = LocalDotTypo.current

    val spacing = LocalSpacing.current

    val screenWidth = LocalConfiguration.current.screenWidthDp

    Box(
        modifier = Modifier.size(screenWidth.div(7).dp), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.date.dayOfWeek.displayText(),
                style = dotTypo.bodyLarge.copy(
                    color = if (day.date == exitDay) DotColor.primaryColor else DotColor.grey5
                )
            )


            Text(
                text = day.date.dayOfMonth.toString(),
                style = dotTypo.bodyLarge.copy(
                    color = if (day.date == exitDay) DotColor.grey5 else DotColor.grey3
                )
            )
        }

    }
}