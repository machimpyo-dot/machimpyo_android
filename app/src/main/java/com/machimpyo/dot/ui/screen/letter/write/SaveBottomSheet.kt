package com.machimpyo.dot.ui.screen.letter.write

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.dotTypo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SaveBottomSheetViewModel = hiltViewModel(),
) {
//    val scope = rememberCoroutineScope()
//    val scaffoldState = rememberBottomSheetScaffoldState()

    val state by viewModel.state.collectAsState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = Color.White,
        dragHandle = {BottomSheetDefaults.DragHandle()}
    ) {
        SaveBottomSheetTop(saveNum = state.tempLetterNum)
        SaveBottomSheetItemFactory(state.tempLetterList)
    }
}
@Composable
fun SaveBottomSheetTop(
    saveNum: Int,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (saveTextConstraint, saveNumConstraint, saveInfoConstraint) = createRefs()
        Text(text = "임시저장",
            style = MaterialTheme.dotTypo.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.constrainAs(saveTextConstraint) {
                start.linkTo(parent.start)
                linkTo(parent.top,parent.bottom)
            }
        )

        Text(text = ""+saveNum,
            style = MaterialTheme.dotTypo.labelSmall,
            modifier = Modifier.constrainAs(saveNumConstraint) {
                start.linkTo(saveTextConstraint.start)
                linkTo(parent.top,parent.bottom)
            }
        )

        Text(text = "최대 5개까지 저장",
            style = MaterialTheme.dotTypo.labelSmall,
            color = DotColor.grey5,
            modifier = Modifier.constrainAs(saveInfoConstraint) {
                end.linkTo(parent.end)
                linkTo(parent.top,parent.bottom)
            }
        )
    }
}

@Composable
fun SaveBottomSheetItemFactory(letterList: List<Letter>) {
    for (letter in letterList) {
        SaveBottomSheetItem(letter)
    }
}

@Composable
fun SaveBottomSheetItem(letter: Letter) {
    Column {
        Text(text = letter.title,
            style = MaterialTheme.dotTypo.labelMedium,
            color = DotColor.grey6)
        Text(text = letter.content,
            style = MaterialTheme.dotTypo.labelSmall,
            color = DotColor.grey4)
    }
}