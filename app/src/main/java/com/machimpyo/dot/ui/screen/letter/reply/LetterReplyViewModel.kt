package com.machimpyo.dot.ui.screen.letter.reply

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
import com.machimpyo.dot.ui.screen.letter.write.SelectLetterWriteState
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.utils.extension.DOMAIN_URI_PREFIX
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SelectLetterReplyState(
    val selectedColor: String,
    val selectedPattern: Int,
    var letter: Letter,
    val letterConfig: LetterConfig,
    val sendResult: Boolean,
)
@HiltViewModel
class LetterReplyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val prefs: LetterDesignSharedPreferences,
) : ViewModel() {
    fun send() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    letter = _state.value.letter.copy(
                        uid = async {
                            repository.createLetter(_state.value.letter).getOrNull()
                        }.await(),
                    ),
                    sendResult = true,
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    sendResult = false,
                )
                Log.e("REPLY", "${e}")
            }finally {
                Log.i("TAG", "${_state.value.letter}")
            }
        }

    }


    fun titleValueChange(title: String) {
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
        SelectLetterReplyState(
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
            letterConfig = LetterConfig(),
            sendResult = false
        )
    )

    var state = _state.asStateFlow()

    fun getSelectedColor(): Color {
        return Color(android.graphics.Color.parseColor("#${_state.value.selectedColor}"))
    }


    init {
        _state.value = _state.value.copy(
            selectedColor = savedStateHandle.get<String>("color_id")!!,
            selectedPattern = savedStateHandle.get<Int>("pattern_id")!!,
        )

        _state.value = _state.value.copy(
            letter = _state.value.letter.copy(
                //관련된 편지의 uid를 담아서 보냄, 백에서 relatedLetterUid 부분을 보고 답장인지 아닌지 판별함
                relatedLetterUid = savedStateHandle.get<Long>("letter_uid")!!,
                colorcode= _state.value.selectedColor,
                letterDesignUid = _state.value.selectedPattern.toLong(),
            )
        )

        Log.i("TAG",_state.value.letter.toString())
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

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToHomeScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_HOME
            )
        )
    }
}