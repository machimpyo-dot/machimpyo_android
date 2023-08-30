package com.machimpyo.dot.utils.extension

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import com.machimpyo.dot.ui.theme.PrimaryOrange

/**
 * 한 줄에 2개의 색이 있을 때 사용가능한 클래스
 * 단어와 중요도를 엮어 매핑하면 됨
 * ex) 나는 "중요한" 사람이야 =>  mapOf("나는" to TextImportance.NORMAL, "중요한" to TextImportance.IMPORTANT, "사람이야" to TextImportance.NORMAL)
 * addText(str,importance) 로 추가할 수도 있음
 * textToComposable()는 textList에 있는 text들이 Normal인지 Important인지를 확인하고 그에 맞는 색으로 style 된 Text 컴포저블이 반환됨
 * **/
class TextList(
    textMap: Map<String, TextImportance>? = null,
    var normalTextColor: Color = Color.White,
    var importantTextColor: Color =  PrimaryOrange,
) {

    val textList: MutableMap<String, TextImportance>
    init {
        if (textMap != null) {
            textList = mutableMapOf<String,TextImportance>()
            textList.putAll(textMap)
        } else {
            textList = mutableMapOf<String,TextImportance>()
        }
    }

    fun addText(text: String, importance: TextImportance = TextImportance.NORMAL) {
        textList.put(text, importance)
    }

    fun getText(): String {
        return textList.toString()
    }

    @Composable
    fun textToComposable(style: TextStyle){
        Text(text = buildAnnotatedString {

            for ((text, importance) in textList) {

                withStyle(style = ParagraphStyle(lineHeight = TextUnit(style.fontSize.value + 5f, type = TextUnitType.Sp))){
                    withStyle(
                        SpanStyle(fontFamily = style.fontFamily , fontSize = style.fontSize,
                            fontWeight = if (importance == TextImportance.IMPORTANT) FontWeight.Bold else null,
                            color = if (importance == TextImportance.IMPORTANT) importantTextColor else normalTextColor)
                    ){
                        append(text)
                    }
                }
            }
        })
    }
}

enum class TextImportance {
    IMPORTANT,
    NORMAL,
}