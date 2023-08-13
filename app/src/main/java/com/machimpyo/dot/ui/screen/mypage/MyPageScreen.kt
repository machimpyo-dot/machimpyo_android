package com.machimpyo.dot.ui.screen.mypage

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.machimpyo.dot.ui.theme.DotColor
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MyPageViewModel = hiltViewModel()
) {

    val snackbarHostState = remember {
        SnackbarHostState()
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

                else -> {}
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

            PagerPreview()
        }
    }
}


sealed class SwipeState<T>

data class SwipedState<T>(
    val page: Int,
    val data: T
) : SwipeState<T>()

class UnSwipedState<T> : SwipeState<T>()

@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Preview
@Composable
fun PagerPreview() {

    //TODO(이걸로 가도 될듯? )
    var swipeState: SwipeState<String> by remember {
        mutableStateOf(UnSwipedState())
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp.value

    val pagerState = rememberPagerState()

    when (swipeState) {
        is UnSwipedState -> {
            HorizontalPager(
                count = 10,
                itemSpacing = 20.dp,
                state = pagerState,
                contentPadding = PaddingValues(start = 40.dp, end = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) { page ->
                val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                val swipeableState = rememberSwipeableState(initialValue = 0, confirmStateChange = {
                    if (it == 1) { //스와이프 된 경우
                        swipeState = SwipedState(page = page, data = "페이지 왈왈")
                    }
                    true
                })

                val anchors = mapOf(
                    0f to 0, //여기서 0이 위에서 initialValue 랑 같음 닫힌 상태
                    -screenHeight to 1 //열린 상태
                )

                val interactionSource = remember {
                    MutableInteractionSource()
                }

                val isSwiping = interactionSource.collectIsDraggedAsState()


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 30.dp)
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
                        .swipeable(
                            state = swipeableState,
                            anchors = anchors,
                            thresholds = { _, _ -> FractionalThreshold(0.8f) },
                            orientation = Orientation.Vertical,
                            interactionSource = interactionSource,

                            )
                        .offset {
                            if (isSwiping.value) {
                                IntOffset(0, swipeableState.offset.value.roundToInt())
                            } else {
                                IntOffset.Zero
                            }
                        },
                ) {
                    Card(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = DotColor.primaryColor
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .height(
                                screenHeight.dp
                                    .div(10)
                                    .times(9)
                            )
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "페이지 인덱스: $page",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White
                                )
                            )
                        }
                    }

                    val lottieSize = screenHeight.dp.div(5)

                    Box(
                        modifier = Modifier
                            .size(lottieSize)
                            .align(Alignment.TopCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedVisibility(
                            visible = page == currentPage,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(lottieSize)
                                    .background(color = Color.Gray, shape = CircleShape)
                            )
                        }
                    }


                }

            }
        }

        is SwipedState -> {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

            val topBarTitleColor by animateColorAsState(
                targetValue = if (scrollBehavior.state.collapsedFraction < 0.5f) Color.White else Color.Gray,
                label = ""
            )
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    LargeTopAppBar(
                        title = {
                            Text(
                                "타이틀", style = MaterialTheme.typography.headlineLarge.copy(
                                    color = topBarTitleColor
                                )
                            )
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = DotColor.primaryColor,
                            scrolledContainerColor = Color.Transparent
                        )
                    )
                }
            ) { innerPadding ->

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(100) {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(16.dp),
                        ) {
                            Text(
                                "카드: $it", style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.Gray
                                )
                            )
                        }

                    }
                }

            }
        }
    }


}



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
