package com.machimpyo.dot.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.machimpyo.dot.R


private val pressStart2P = FontFamily(
    Font(R.font.press_start_2p, FontWeight.Normal, FontStyle.Normal)
)
private val pretendard = FontFamily(
    Font(R.font.pretendard_black, FontWeight.Black),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_extralight, FontWeight.ExtraLight),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_thin, FontWeight.Thin),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
)

data class DotTypo(
    val DotContents_Body_Small_Bold: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    val DotMainBranding_Display_Medium_Normal: TextStyle = TextStyle(
        fontFamily = pressStart2P,
        fontWeight = FontWeight.Normal,
        fontSize = 52.sp
    ),

    val DotCalendar_Body_Medium_SemiBold : TextStyle= TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),

    val DotCalendar_Body_Small_ExtraLight: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 16.sp
    ),

    val DotCalendar_Title_Small_Medium: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),

    val DotTitle_Title_Large_ExtraBold : TextStyle= TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp
    ),

    val DotSubtitle_Body_Medium_Medium: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),

    val DotContents_Body_Small_Normal: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    val DotContents_Body_Small_SemiBold: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),

    val DotContents_Label_Large_Normal: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    val DotAppBarTitle_Body_Small_Bold: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),

    val DotHead_Title_Medium_ExtraBold: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp
    ),

    val DotHead_Body_Medium_Medium: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),

    val Dot_Display_Body_Medium_Bold: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),

    val Dot_Display_Body_Medium_Normal: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    /*
        스낵바 텍스트
         */
    val DotSnackBarContent_Body_Large_Normal: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    val DotSnackBarAction_Body_Medium_ExtraBold: TextStyle = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp
    ),

)

val LocalDotTypo = compositionLocalOf { DotTypo() }

val MaterialTheme.dotTypo: DotTypo
    @Composable
    @ReadOnlyComposable
    get() = LocalDotTypo.current