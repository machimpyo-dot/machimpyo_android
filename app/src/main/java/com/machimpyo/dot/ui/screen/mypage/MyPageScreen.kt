package com.machimpyo.dot.ui.screen.mypage

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.PlainTooltipState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.utils.DashedDivider
import com.machimpyo.dot.utils.extension.clickableWithoutRipple
import com.machimpyo.dot.utils.extension.hasBackStackEntry
import com.machimpyo.dot.utils.extension.toFormattedDate
import com.webtoonscorp.android.readmore.material.ReadMoreText
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MyPageViewModel,
    authViewModel: AuthViewModel
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val dotTypo = LocalDotTypo.current

    var isSwiped by remember {
        mutableStateOf(false)
    }
    var currentPage by remember {
        mutableStateOf<Int?>(null)
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val state by viewModel.state.collectAsState()

    val pagerState = rememberPagerState()

    val userState by authViewModel.userState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.updateCompanyImage(currentPage, it)
            }
        }
    )

    LaunchedEffect(pagerState.currentPage) {
        currentPage = if (pagerState.pageCount > 0) {
            pagerState.currentPage
        } else {
            null
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            when (it) {
                is MyPageViewModel.Effect.NavigateTo -> {
                    navController.navigate(it.route, it.builder)
                }

                is MyPageViewModel.Effect.ShowMessage -> {
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
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) {
                Snackbar {
                    Text(it.message)
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            if (!isSwiped) {
                LargeTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .fillMaxHeight(0.2f),
                    title = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "${userState.userInfo?.nickName}",
                                style = dotTypo.headlineLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                )
                            )
                            Text(
                                "의 편지보관함",
                                style = dotTypo.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                )
                            )
                        }

                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Black,
                        scrolledContainerColor = Color.Black,
                        navigationIconContentColor = Color.White,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )


                HorizontalPager(
                    count = state.letterBoxItems.size,
                    itemSpacing = 20.dp,
                    state = pagerState,
                    contentPadding = PaddingValues(start = 40.dp, end = 40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    val swipeableState =
                        rememberSwipeableState(initialValue = 0, confirmStateChange = {
                            if (it == 1) { //스와이프 된 경우
                                isSwiped = true
                            }
                            true
                        })

                    val anchors = mapOf(
                        0f to 0, //여기서 0이 위에서 initialValue 랑 같음 닫힌 상태
                        -Float.MAX_VALUE to 1 //열린 상태
                    )

                    val interactionSource = remember {
                        MutableInteractionSource()
                    }

                    val isCardSwiping = interactionSource.collectIsDraggedAsState()


                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f)
                                .padding(vertical = 30.dp)
                                .swipeable(
                                    state = swipeableState,
                                    anchors = anchors,
                                    thresholds = { _, _ -> FractionalThreshold(0.8f) },
                                    orientation = Orientation.Vertical,
                                    interactionSource = interactionSource,
                                )
                                .offset {
                                    if (isCardSwiping.value) {
                                        IntOffset(
                                            0,
                                            swipeableState.offset.value.roundToInt()
                                        )
                                    } else {
                                        IntOffset.Zero
                                    }
                                },
                        ) {
                            val (cardRef, lottieRef) = createRefs()

                            Card(
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = DotColor.primaryColor
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .constrainAs(cardRef) {
                                        top.linkTo(parent.top, margin = screenWidth.div(2))
                                        bottom.linkTo(parent.bottom, margin = 64.dp)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    }
                                    .graphicsLayer {
                                        lerp(
                                            start = ScaleFactor(0.95f, 0.85f),
                                            stop = ScaleFactor(1f, 1f),
                                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                        ).also { scale ->
                                            scaleX = scale.scaleX
                                            scaleY = scale.scaleY
                                        }
                                        alpha = lerp(
                                            start = ScaleFactor(0.5f, 0.5f),
                                            stop = ScaleFactor(1f, 1f),
                                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                        ).scaleX
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 64.dp),
                                    verticalArrangement = Arrangement.Bottom,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                    ) {

                                        state.letterBoxItems.getOrNull(page)?.photoUrl.let { photoUrl ->

                                            AsyncImage(
                                                model = photoUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape)
                                                    .align(Alignment.Center),
                                                contentScale = ContentScale.Crop,
                                                placeholder = painterResource(id = R.drawable.dot_icon),
                                                error = painterResource(id = R.drawable.dot_icon),
                                                fallback = painterResource(id = R.drawable.dot_icon),
                                            )

                                        }

//                                        IconButton(
//                                            modifier = Modifier
//                                                .background(
//                                                    color = Color.White,
//                                                    shape = CircleShape
//                                                )
//                                                .border(
//                                                    width = 1.dp,
//                                                    color = DotColor.grey1,
//                                                    shape = CircleShape
//                                                )
//                                                .size(25.dp)
//                                                .align(Alignment.BottomEnd),
//                                            onClick = {
//                                                photoPickerLauncher.launch(
//                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                                                )
//                                            }
//                                        ) {
//                                            Icon(
//                                                imageVector = Icons.Default.CameraAlt,
//                                                tint = DotColor.grey3,
//                                                contentDescription = null,
//                                                modifier = Modifier.size(15.dp)
//                                            )
//                                       }


                                    }

                                    state.letterBoxItems.getOrNull(page)?.name?.let {
                                        Text(
                                            it, style = dotTypo.displaySmall.copy(
                                                color = Color.White
                                            )
                                        )
                                    }

                                    state.letterBoxItems.getOrNull(page)?.exitDate?.let {
                                        Text(
                                            "~ ${it.toFormattedDate("yy.MM.dd")}",
                                            style = dotTypo.headlineSmall.copy(
                                                color = Color.White
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    DashedDivider(
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        "Recently", style = dotTypo.headlineSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .align(Alignment.Start)
                                    )

                                    Text(
                                        state.letterBoxItems.getOrNull(page)?.recentLetterContents
                                            ?: "아직 도착한 편지가 없어요 :(", style = dotTypo.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraLight
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .align(Alignment.Start)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(
                                        screenWidth
                                            .div(3)
                                            .times(2)
                                    )
                                    .constrainAs(lottieRef) {
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                        top.linkTo(parent.top)
                                    }
                            ) {
                                AnimatedVisibility(
                                    visible = page == currentPage,
                                    enter = fadeIn() + scaleIn(
                                        animationSpec = spring()
                                    ),
                                    exit = fadeOut() + scaleOut(
                                        animationSpec = spring()
                                    ),
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.box),
                                        contentDescription = null
                                    )
                                }
                            }

                        }

                        val tooltipState = remember {
                            PlainTooltipState()
                        }

                        val offsetState = remember {
                            Animatable(0f)
                        }

                        SideEffect {
                            coroutineScope.launch {
                                while (true) {
                                    offsetState.animateTo(
                                        -8f,
                                        animationSpec = tween(
                                            durationMillis = 800
                                        )
                                    )
                                    offsetState.animateTo(
                                        8f,
                                        animationSpec = tween(
                                            durationMillis = 800
                                        )
                                    )
                                }
                            }
                        }



                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp)
                                .graphicsLayer {
                                    translationY = offsetState.value
                                }
                        ) {
                            AnimatedVisibility(
                                visible = !isCardSwiping.value && !pagerState.isScrollInProgress,
                                enter = fadeIn() + slideInVertically(
                                    animationSpec = spring()
                                ),
                                exit = fadeOut(tween(100))
                            ) {
                                PlainTooltipBox(
                                    tooltip = {
                                        Text(
                                            "카드를 위로 스와이프 해보세요 :)",
                                            style = dotTypo.bodyMedium.copy(
                                                color = Color.White
                                            )
                                        )
                                    },
                                    containerColor = Color.Transparent,
                                    tooltipState = tooltipState,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        tint = Color.White,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .rotate(90f)
                                            .clickableWithoutRipple {
                                                coroutineScope.launch {
                                                    tooltipState.show()
                                                }
                                            }
                                    )
                                }
                            }

                        }

                    }


                }

                if (navController.hasBackStackEntry()) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

            } else {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        val topBarHeight =
                            300.dp * (1f - scrollBehavior.state.collapsedFraction).absoluteValue


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .nestedScroll(
                                    scrollBehavior.nestedScrollConnection
                                )
                        ) {

                            currentPage?.let {
                                state.letterBoxItems.getOrNull(it)?.photoUrl?.let { photoUrl ->
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Inside,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(topBarHeight),
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .height(topBarHeight)
                                    .background(
                                        color = DotColor.primaryColor.copy(
                                            alpha = 0.6f
                                        )
                                    )
                                    .padding(horizontal = 32.dp),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                currentPage?.let {
                                    state.letterBoxItems.getOrNull(it)?.name?.let { name ->
                                        Text(
                                            name,
                                            style = dotTypo.displaySmall.copy(
                                                color = Color.White,
                                            ),
                                            modifier = Modifier.align(Alignment.Start),
                                        )
                                    }
                                }

                                // ~ 22.08.03

                                currentPage?.let {
                                    state.letterBoxItems.getOrNull(it)?.exitDate?.let { exitDate ->
                                        Text(
                                            "~ ${exitDate.toFormattedDate("yy.MM.dd")}",
                                            style = dotTypo.headlineSmall.copy(
                                                color = Color.White
                                            ),
                                            modifier = Modifier.align(Alignment.Start),
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            CenterAlignedTopAppBar(
                                title = {},
                                scrollBehavior = scrollBehavior,
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.Transparent,
                                    scrolledContainerColor = Color.Transparent
                                ),
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            isSwiped = false
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            modifier = Modifier.rotate(90f),
                                            tint = DotColor.white
                                        )
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->


                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.Black
                            )
                            .padding(innerPadding)
                            .padding(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        item {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .height(5.dp)
                                    .width(100.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        currentPage?.let {
                            state.letterBoxItems.getOrNull(it)?.talks?.let { talks ->

                                talks.forEach { talk ->
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    color = Color.White,
                                                    shape = RoundedCornerShape(
                                                        topStart = 0.dp,
                                                        topEnd = 10.dp,
                                                        bottomStart = 10.dp,
                                                        bottomEnd = 10.dp
                                                    )
                                                )
                                                .clickable {
                                                    val letterUid = talk.myTalk.uid
                                                    viewModel.goToLetterCheckScreen(letterUid!!)
                                                }
                                                .padding(horizontal = 16.dp, vertical = 24.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Start
                                            ) {

                                                AsyncImage(
                                                    model = talk.myTalk.profileUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(
                                                            CircleShape
                                                        ),
                                                    error = painterResource(id = R.drawable.dot_icon),
                                                    fallback = painterResource(id = R.drawable.dot_icon),
                                                    placeholder = painterResource(id = R.drawable.dot_icon)
                                                )

                                                Spacer(modifier = Modifier.width(16.dp))

                                                Text(
                                                    talk.myTalk.title,
                                                    style = dotTypo.headlineSmall.copy(
                                                        DotColor.primaryColor,
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            ReadMoreText(
                                                text = talk.myTalk.content,
                                                expanded = false,
                                                color = DotColor.grey5,
                                                readMoreText = "더보기",
                                                readMoreMaxLines = 2,
                                                style = dotTypo.bodyMedium.copy(
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                            )
                                        }


                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }

                                    items(talk.replyTalks) { replyTalk ->

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        color = DotColor.primaryColor,
                                                        shape = RoundedCornerShape(
                                                            topStart = 10.dp,
                                                            topEnd = 10.dp,
                                                            bottomStart = 10.dp,
                                                            bottomEnd = 0.dp
                                                        )
                                                    )
                                                    .clickable {
                                                        val letterUid = replyTalk.uid
                                                        viewModel.goToLetterCheckScreen(letterUid!!)
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 24.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Start
                                                ) {

                                                    AsyncImage(
                                                        model = replyTalk.profileUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .clip(
                                                                CircleShape
                                                            ),
                                                        error = painterResource(id = R.drawable.dot_icon),
                                                        fallback = painterResource(id = R.drawable.dot_icon),
                                                        placeholder = painterResource(id = R.drawable.dot_icon)
                                                    )

                                                    Spacer(modifier = Modifier.width(16.dp))

                                                    Text(
                                                        replyTalk.title,
                                                        style = dotTypo.headlineSmall.copy(
                                                            Color.White,
                                                            fontWeight = FontWeight.SemiBold
                                                        ),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                ReadMoreText(
                                                    text = replyTalk.content,
                                                    expanded = false,
                                                    color = Color.White,
                                                    readMoreText = "더보기",
                                                    readMoreMaxLines = 2,
                                                    style = dotTypo.bodyMedium.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    fontSize = 16.sp,
                                                )


                                            }

                                            Spacer(modifier = Modifier.height(2.dp))
                                        }

                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(32.dp))
                                    }

                                    item {
                                        TextButton(
                                            onClick = {
                                                viewModel.goToThirdPartyLibraryCheckScreen()
                                            },
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        ) {
                                            Text("관련 라이브러리", style = dotTypo.bodyMedium.copy(
                                                color = Color.White,
                                                textAlign = TextAlign.End
                                            ))
                                        }
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(32.dp))
                                    }

                                }

                            }
                        }


                    }


                }

            }


        }
    }
}


//sealed class SwipeState<T>
//
//data class SwipedState<T>(
//    val page: Int,
//    val data: T
//) : SwipeState<T>()
//
//class UnSwipedState<T> : SwipeState<T>()


@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun SwipingLazyColumn() {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        itemsIndexed(List(30) { "text $it" }) { index, item ->

            val swipeableState = rememberSwipeableState(initialValue = 0, confirmStateChange = {
                if (it == 1) { //스와이프 된 경우
                    Log.e("TAG", "스와이프 됨!!!")
                }
                true
            })

            val anchors = mapOf(
                0f to 0, //여기서 0이 위에서 initialValue 랑 같음 닫힌 상태
                screenWidth to 1 //열린 상태
            )

            val interactionSource = remember {
                MutableInteractionSource()
            }

            val isSwiping = interactionSource.collectIsDraggedAsState()


            Card(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.8f) },
                        orientation = Orientation.Horizontal,
                        interactionSource = interactionSource,

                        )
                    .offset {
                        if (isSwiping.value) {
                            IntOffset(swipeableState.offset.value.roundToInt(), 0)
                        } else {
                            IntOffset.Zero
                        }
                    }
            ) {
                Text(
                    item,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 32.dp),
                )
            }
        }
    }
}
