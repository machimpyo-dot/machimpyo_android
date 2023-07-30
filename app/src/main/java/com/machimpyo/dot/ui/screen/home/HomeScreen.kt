package com.machimpyo.dot.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import eu.wewox.tagcloud.TagCloud
import eu.wewox.tagcloud.rememberTagCloudState
import kotlinx.serialization.json.JsonNull.content

@Preview(showBackground = true)
@Composable
private fun Preview() {

//    HomeScreen()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        for (i in 1..10) {
            Text(
                "글씨 크기 ${30 - i * 2}",
                style = TextStyle(
                    fontSize = (30-i*2).sp,
                    letterSpacing = (-1.2).sp
                )
            )
        }
    }
}

@Composable
fun HomeScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("box4.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )


    val labels = List(32) { "보낸사람 #$it" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Black
            ),
        contentAlignment = Alignment.Center
    ) {
        TagCloud(state = rememberTagCloudState()) {
            items(labels) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = Color.White
                            )
                    ) {
                    }

                    Text(
                        text = it,
                        modifier = Modifier
                            .tagCloudItemFade()
                            .tagCloudItemScaleDown(),
                        color = Color.White
                    )
                }

            }
        }
    }


}