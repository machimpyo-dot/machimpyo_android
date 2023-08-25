package com.machimpyo.dot.ui.screen.content.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fresh.materiallinkpreview.parsing.OpenGraphMetaDataProvider
import com.fresh.materiallinkpreview.ui.CardLinkPreview
import com.fresh.materiallinkpreview.ui.CardLinkPreviewProperties
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.utils.extension.hasBackStackEntry
import com.machimpyo.dot.utils.extension.random
import java.net.URL

@Preview
@Composable
private fun Preview() {
    ContentDetailScreen(navController = rememberNavController(), viewModel = null, contentUid = 0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ContentDetailViewModel?,
    contentUid: Int
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val dotTypo = LocalDotTypo.current


    val state = viewModel?.state?.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel?.initState(contentUid = contentUid)
    }

    if (viewModel != null) {
        LaunchedEffect(viewModel) {
            viewModel.effect.collect {
                when (it) {
                    is ContentDetailViewModel.Effect.NavigateTo -> {
                        navController.navigate(it.route, it.builder)
                    }

                    is ContentDetailViewModel.Effect.ShowMessage -> {
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
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "퇴사 꿀팁", style = dotTypo.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    if (navController.hasBackStackEntry()) {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DotColor.primaryColor
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dot_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    "dot edit",
                    style = dotTypo.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Light
                    )
                )
            }

            Text(
                state?.value?.title ?: "",
                style = dotTypo.headlineLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )



            Text(
                state?.value?.subtitle ?: "", style = dotTypo.labelSmall.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = DotColor.grey1
            )

            //질문
            Column(
                modifier = Modifier
                    .padding(end = 64.dp)
                    .background(
                        color = DotColor.grey6,
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    state?.value?.questionText ?: "",
                    style = dotTypo.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "질문자", style = dotTypo.bodyMedium.copy(
                        color = DotColor.primaryColor,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End
                    ), modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 64.dp)
                    .background(
                        color = DotColor.grey1,
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = 10.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    state?.value?.replyText ?: "",
                    style = dotTypo.bodyMedium.copy(
                        Color.Black,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Box(
                modifier = Modifier
                    .padding(start = 64.dp)
                    .background(
                        color = DotColor.grey1,
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = 10.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .padding(16.dp)
                    .align(Alignment.End),
            ) {
                state?.value?.photoId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                }
            }

            state?.value?.previewMetaData?.let {

                Box (
                    modifier = Modifier.padding(start = 64.dp).clickable {
                        viewModel.goToWebViewScreen(it)
                    }
                ) {
                    CardLinkPreview(
                        openGraphMetaData = it,
                        cardLinkPreviewProperties = CardLinkPreviewProperties.Builder(
                            drawWithCardOutline = false
                        ).build()
                    )
                }


            }

        }
    }
}
