package com.machimpyo.dot.ui.screen.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.machimpyo.dot.data.model.Library
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.utils.getMockThirdPartyLibraries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdPartyLibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: ThirdPartyLibraryViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    val dotTypo = LocalDotTypo.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "관련 라이브러리", style = dotTypo.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            tint = Color.Black,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LibrariesLazyColumn()
        }

    }

}

@Preview
@Composable
private fun Preview() {
    LibrariesLazyColumn()
}
@Composable
fun LibrariesLazyColumn(
    modifier: Modifier = Modifier,
    libraries: List<Library> = getMockThirdPartyLibraries()
) {

    val dotTypo = LocalDotTypo.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(libraries) { library ->

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 15.dp
                ),
                colors = CardDefaults.cardColors(
                    contentColor = Color.White,
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        library.title,
                        style = dotTypo.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp
                        ),
                    )
                    Text(
                        library.url,
                        style = dotTypo.bodyMedium.copy(
                            color = Color(0xFF00A0FF),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Start
                        ),
                        textDecoration = TextDecoration.Underline
                    )
                    Text(
                        library.content,
                        style = dotTypo.bodyMedium.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start
                        ),
                    )
                }
            }
        }
    }
}