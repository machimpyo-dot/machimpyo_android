//package com.machimpyo.dot.ui.alert
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Snackbar
//import androidx.compose.runtime.Composable
//import androidx.compose.material3.SnackbarData
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import com.machimpyo.dot.ui.theme.LocalDotTypo
//import com.machimpyo.dot.ui.theme.LocalSpacing
//import com.machimpyo.dot.ui.theme.PrimaryOrange
//import com.machimpyo.dot.ui.theme.SnackBarContainer
//
//
//@Composable
//fun SnackBarScreen(
//    snackBarData: SnackbarData
//) {
//    val spacing = LocalSpacing.current
//    val dotTypo = LocalDotTypo.current
//
//    Snackbar(
//        modifier = Modifier
//            .padding(spacing.small)
//            .fillMaxWidth(),
//        action = {
//            snackBarData.visuals.actionLabel?.let { actionText ->
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.BottomEnd
//                ) {
//                    TextButton(
//                        onClick = {
//                            snackBarData.performAction()
//                        }
//                    ) {
//                        Text(
//                            text = actionText,
//                            style = dotTypo.DotSnackBarAction_Body_Medium_ExtraBold.copy(
//                                color = PrimaryOrange
//                            )
//                        )
//                    }
//                }
//            }
//
//        },
//        actionOnNewLine = snackBarData.visuals.actionLabel != null,
//        containerColor = SnackBarContainer,
//        contentColor = Color.White,
//        actionContentColor = PrimaryOrange
//    ) {
//        Box(
//            modifier = Modifier.padding(vertical = spacing.medium),
//            contentAlignment = Alignment.CenterStart
//        ) {
//            Text(
//                text = snackBarData.visuals.message,
//                style = dotTypo.DotSnackBarContent_Body_Large_Normal.copy(
//                    color = Color.White
//                )
//            )
//        }
//
//    }
//
//}