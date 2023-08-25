package com.machimpyo.dot.ui.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.machimpyo.dot.R
import com.machimpyo.dot.ui.theme.DotColor
import com.machimpyo.dot.ui.theme.dotTypo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDialog(
    dialogProperties: DialogProperties = DialogProperties(),
    mainIcon: Painter? = null,
    mainText: String? = null,
    mainBody: @Composable () -> Unit = {},
) {
    AlertDialog(
        modifier= Modifier
            .width(260.dp)
            .wrapContentHeight()
            .defaultMinSize(260.dp, 260.dp)
        ,
        onDismissRequest = { /*TODO*/ },
        properties = dialogProperties,
    ){


        ConstraintLayout(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(20.dp),
            ) {

            var (headConstraint, mainConstraint , mainBodyConstraint) = createRefs()

            Icon(painterResource(id = R.drawable.line_24), contentDescription = "head",
                tint= Color.LightGray,
                modifier = Modifier.constrainAs(headConstraint) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (mainIcon != null || mainText != null) {
                            160.dp
                        } else {
                            0.dp
                        }
                    )
                    .constrainAs(mainConstraint) {
                        top.linkTo(headConstraint.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(mainBodyConstraint.top)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {

                mainIcon?.let {
                    Icon(
                        painter = it, contentDescription = "main icon",
                        modifier = Modifier
                            .wrapContentSize(),
                        tint = Color.Unspecified
                    )
                }
                if (mainIcon != null && mainText != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                mainText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.dotTypo.labelLarge.copy(
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign= TextAlign.Center ,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None),),
                        color = DotColor.grey6,
                        modifier = Modifier
                            .wrapContentSize()
                    )
                }

            }


            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(mainBodyConstraint) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ){
                mainBody()
            }

        }
    }
}

@Preview (showSystemUi = true)
@Composable
fun Preview() {
    MainDialog(
        mainText = "선택완료",
        mainIcon = painterResource(id = R.drawable.check),
        mainBody = {
            Button(
                modifier = Modifier.size(63.dp, 30.dp),
                onClick = {},
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DotColor.grey6,
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "확인",
                    style = MaterialTheme.dotTypo.labelMedium.copy(
                        fontWeight= FontWeight.SemiBold,
                        textAlign= TextAlign.Center ,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        )
                    ),
                )
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun Preview2() {
    MainDialog(
        mainText = "편지지를 다시 고르시면\n글이 삭제됩니다."
    ) {
        Column {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DotColor.grey3)
            )

            SelectButton(
                text = "삭제",
                isCrucial = true
            )

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DotColor.grey3)
            )

            SelectButton(
                text = "임시저장",
                isCrucial = false
            )


            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DotColor.grey3)
            )

            SelectButton(
                text = "취소",
                isCrucial = false
            )

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DotColor.grey3)
            )
        }
    }
}

@Composable
fun SelectButton(
    text: String = "",
    onClick: () -> Unit = {},
    isCrucial: Boolean = false,
) {
    Button(
        modifier= Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RectangleShape,
        colors= ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ),
        onClick = onClick) {
        Text(
            text = text,
            style = MaterialTheme.dotTypo.labelMedium.copy(
                color = if(isCrucial) DotColor.primaryColor else Color.Black,
                fontWeight = if(isCrucial) FontWeight.SemiBold else FontWeight.Medium,
                textAlign= TextAlign.Center ,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.None
                )
            )
        )
    }
}