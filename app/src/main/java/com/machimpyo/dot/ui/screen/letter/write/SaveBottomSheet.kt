package com.machimpyo.dot.ui.screen.letter.write

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.LocalDotTypo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onClickCallBack: (Letter) -> Unit = {},
    viewModel: SaveBottomSheetViewModel = hiltViewModel(),
) {
//    val scope = rememberCoroutineScope()
//    val scaffoldState = rememberBottomSheetScaffoldState()

    val dotTypo = LocalDotTypo.current
    
    val state by viewModel.state.collectAsState()

    ModalBottomSheet(
        modifier= modifier,
        onDismissRequest = { onDismiss() },
        containerColor = Color.White,
        dragHandle = {BottomSheetDefaults.DragHandle()}
    ) {
        SaveBottomSheetTop(saveNum = state.tempLetterNum)
        SaveBottomSheetItemFactory(state.tempLetterList) {
                letter -> onClickCallBack(letter)
                onDismiss()
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}
@Composable
fun SaveBottomSheetTop(
    saveNum: Int,
) {
    val dotTypo = LocalDotTypo.current

    ConstraintLayout(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (saveTextConstraint, saveNumConstraint, saveInfoConstraint) = createRefs()
        Text(text = "임시저장",
            style = dotTypo.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.constrainAs(saveTextConstraint) {
                start.linkTo(parent.start)
                linkTo(parent.top,parent.bottom)
            }
        )

        Text(text = ""+saveNum,
            style = dotTypo.labelSmall,
            modifier = Modifier.constrainAs(saveNumConstraint) {
                start.linkTo(saveTextConstraint.end, margin= 5.dp)
                linkTo(parent.top,parent.bottom)
            }
        )

        Text(text = "최대 5개까지 저장",
            style = dotTypo.labelSmall,
            color = DotColor.grey5,
            modifier = Modifier.constrainAs(saveInfoConstraint) {
                end.linkTo(parent.end)
                linkTo(parent.top,parent.bottom)
            }
        )
    }
}

@Composable
fun SaveBottomSheetItemFactory(letterList: List<Letter>, onClickCallBack: (Letter) -> Unit = {}) {
    for (letter in letterList) {
        SaveBottomSheetItem(
            letter= letter,
            onClickCallBack = {letterItem -> onClickCallBack(letterItem)})
    }
}

@Composable
fun SaveBottomSheetItem(letter: Letter,
                        onClickCallBack: (Letter)->Unit) {

    val dotTypo = LocalDotTypo.current

    Column(
        modifier= Modifier
            .clickable { onClickCallBack(letter) }
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(text = letter.title?:"",
            style = dotTypo.labelMedium,
            color = DotColor.grey6)
        Text(text = letter.content?:"",
            style = dotTypo.labelSmall,
            color = DotColor.grey4)
    }
}