package com.machimpyo.dot.ui.screen.select

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo
import com.machimpyo.dot.utils.extension.LetterPatternList
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content


@Composable
fun Letter(
    clickable: Boolean = false,
    isClickIndication: Boolean = true,
    onClick: () -> Unit = {},
    background: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    border: BorderStroke? = null,
    content: @Composable () -> Unit = {}
) {
    val interactionSource = remember{MutableInteractionSource()}

    Card(
        modifier= modifier,
        shape = MaterialTheme.shapes.large.copy(CornerSize(5.dp)),
        colors = CardDefaults.cardColors(containerColor = color),
        border = border
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = clickable,
                onClick= onClick,
                indication = if(isClickIndication) LocalIndication.current else null,
                interactionSource = interactionSource,
            )
        ){
            background()
            content()
        }
    }
}

@Composable
fun LetterBackground(id: Int) {
    Image(
        painter = painterResource(id = LetterPatternList[id]),
        contentDescription = "letter__$id",
        modifier= Modifier.fillMaxSize()
    )
}


//@Composable
//fun LetterBackground(id: Int) {
//    SVGImage(path = findSVGUrlFromId(id))
//}
//
//@Composable
//fun SVGImage(path: String) {
//    AsyncImage(
//        modifier= Modifier.fillMaxSize(),
//        model = ImageRequest.Builder(LocalContext.current)
//            .data(path)
//            .decoderFactory(SvgDecoder.Factory())
//            .build(),
//        contentDescription = null,
//    )
//}

@Composable
fun LetterTitle(
    title: String,
    hint: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false
) {
    val dotTypo = LocalDotTypo.current
    
    val textStyle = dotTypo.headlineLarge

    BasicTextField(
        value = title,
        onValueChange = onValueChange,
        singleLine = true,
        //hint
        decorationBox = { innerTextField ->
            if (title.isEmpty()) {
                Text(
                    text = hint,
                    style =  textStyle.copy(
                        color = DotColor.grey2
                    ),
                )
            }
            innerTextField()
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = textStyle,
        readOnly = readOnly
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LetterContent(
    content: String,
//    maxLine: Int,
    hint: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false
) {
    val dotTypo = LocalDotTypo.current

    val textStyle = dotTypo.bodyMedium

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    //content 작성
    BasicTextField(
        value = content,

        onValueChange = onValueChange,

//        maxLines = maxLine,

        //hint
        decorationBox = { innerTextField ->
            if (content.isEmpty()) {
                Text(
                    text = hint,
                    style =  textStyle.copy(
                        color = DotColor.grey2
                    ),
                )
            }
            innerTextField()
        },

        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusEvent { focusState ->
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        textStyle = textStyle,

        readOnly = readOnly
    )
}

@Composable
fun LetterTextRemain(now: Int, max: Int, modifier:Modifier = Modifier) {
    val dotTypo = LocalDotTypo.current

    Text(
        text = "${now} / ${max}",

        modifier= modifier,
        style = dotTypo.bodySmall
    )
}