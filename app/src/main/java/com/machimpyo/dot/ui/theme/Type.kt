package com.machimpyo.dot.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.machimpyo.dot.R
import org.w3c.dom.Text

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

val LocalDotTypo = compositionLocalOf { Typography }

val MaterialTheme.dotTypo: Typography
    @Composable
    @ReadOnlyComposable
    get() = LocalDotTypo.current

// Set of Material typography styles to start with
val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = pressStart2P,
        fontWeight = FontWeight.Normal,
        fontSize = 52.sp,
        letterSpacing = (-1.2).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        letterSpacing = (-1.2).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = (-1.2).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 21.sp,
        letterSpacing = (-1.2).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = (-1.2).sp
    ),
    labelSmall = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = (-1.2).sp
    )

)