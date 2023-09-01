package com.machimpyo.dot.ui.screen.letter.write

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.BuildConfig
import com.machimpyo.dot.data.model.ColorList
import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.data.model.LetterConfig
import com.machimpyo.dot.data.store.LetterDesignSharedPreferences
import com.machimpyo.dot.navigation.ROUTE_HOME
import com.machimpyo.dot.navigation.ROUTE_LETTER_CHECK
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.service.AndroidArgumentDynamicLink
import com.machimpyo.dot.service.DotDynamicLink
import com.machimpyo.dot.service.FirebaseDeepLinkService
import com.machimpyo.dot.service.Link
import com.machimpyo.dot.service.SocialMetaTagArgumentDynamicLink
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.utils.extension.DOMAIN_URI_PREFIX
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SelectLetterWriteState(
    val selectedColor: String,
    val selectedPattern: Int,
    var letter: Letter,
    val letterConfig: LetterConfig,
)
@HiltViewModel
class LetterWriteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val prefs: LetterDesignSharedPreferences,
    ) : ViewModel() {
    fun send() {
        viewModelScope.launch {

        _state.value = _state.value.copy(
            letter= _state.value.letter.copy(
                uid = async {repository.createLetter(_state.value.letter).getOrNull()}.await()
            )
        )

        val link = Link(
            url= DOMAIN_URI_PREFIX,
            nav = ROUTE_LETTER_CHECK,
            uid = _state.value.letter.uid.toString(),
        )
        val androidArgumentDynamicLink = AndroidArgumentDynamicLink(
            apn = BuildConfig.APPLICATION_ID,
            amv = BuildConfig.VERSION_CODE,
        )

        val socialMetaTagArgumentDynamicLink = SocialMetaTagArgumentDynamicLink(
            st= "감사의 편지가 도착했어요",
            sd = "지금 편지를 열어서 마음을 확인하고 감사의 답장을 작성해보세요",
            si = Uri.parse("https://postfiles.pstatic.net/MjAyMzA4MjhfMTAx/MDAxNjkzMjMzNTk0Mzg2.6w4sCI1GLIDPp95rCGN7XzrE8BrZIk586xkb1milf28g.byrIxJ1qxnoJrNvaiGwxI2BmPqjPDxpZIGeJN4xwI6Ag.PNG.jay3925_/KakaoTalk_20230828_211151312_02.png?type=w966")
        )


        val dynamicLink = DotDynamicLink(
            link = link,
            androidArgumentDynamicLink = androidArgumentDynamicLink,
            socialMetaTagArgumentDynamicLink = socialMetaTagArgumentDynamicLink,
        )

            _state.value = _state.value.copy(
                letter = _state.value.letter.copy(
                    url = FirebaseDeepLinkService.makeDynamicLink(
                        callback = { setDeepLinkUrl(it) },
                        dynamicLink= dynamicLink
                    ).toString()
                )
            )
        }

    }

    fun save() {
        viewModelScope.launch{
            async { repository.updateTempLetter(letter = _state.value.letter)}.await()
        }
    }

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToHomeScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_HOME
            )
        )
    }

    private fun setDeepLinkUrl(url: String,) {
        _state.value = _state.value.copy(
            letter= _state.value.letter.copy(
                url= url,
            )
        )
    }

    fun titleValueChange(title: String) {
//        _state.value.letter.title = title
        _state.value = _state.value.copy(

            letter= _state.value.letter.copy(
                title= title,
            )
        )
    }
    fun contentValueChange(content: String) {
        //MaxLength보다 크면 조기리턴
        if (content.length > _state.value.letterConfig.contentMaxLength) return
        //줄이 contentMaxLine만큼 있으면 조기 리턴
        if (content.count { alphabet -> alphabet == '\n' } >= _state.value.letterConfig.contentMaxLine) return

        //            _state.value.letter.content = content
        _state.value = _state.value.copy(
            letter= _state.value.letter.copy(
                content= content,
            )
        )
    }

    private val _state = MutableStateFlow(
        SelectLetterWriteState(
            selectedColor = "FFFFFF",
            selectedPattern = 0,
            letter= Letter(
                title= "",
                content = "",
                uid = 0,
                profileUrl = "",
                nickname = "",
                url = "",
                letterDesignUid = null,
                colorcode = null,
                relatedLetterUid = null,
                ),
            letterConfig = LetterConfig()
        )
    )

    var state = _state.asStateFlow()

    fun getSelectedColor(): Color {
        return Color(android.graphics.Color.parseColor("#${_state.value.selectedColor}"))
    }

    fun changeLetter(letter: Letter) {
        _state.value = _state.value.copy(
            letter = letter,
            selectedColor = letter.colorcode!!,
            selectedPattern = letter.letterDesignUid!!.toInt(),
        )
    }


    init {
        _state.value = _state.value.copy(
            selectedColor = savedStateHandle.get<String>("color_id")!!,
            selectedPattern = savedStateHandle.get<Int>("pattern_id")!!,
        )

        _state.value = _state.value.copy(
            letter = _state.value.letter.copy(
                colorcode= _state.value.selectedColor,
                letterDesignUid = _state.value.selectedPattern.toLong(),
            )
        )
    }

    sealed class Effect {
        data class ShowMessage(
            val message: String,
            val actionLabel: String? = null,
            val action: () -> Unit = {},
            val dismissed: () -> Unit = {}
        ) : Effect()

        data class NavigateTo(
            val route: String,
            val builder: NavOptionsBuilder.() -> Unit = {}
        ) : Effect()
    }
}