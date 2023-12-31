package com.machimpyo.dot.ui.screen.letter.check

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.kakao.sdk.common.KakaoSdk.init
import com.machimpyo.dot.data.model.ColorList
import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.data.model.LetterConfig
import com.machimpyo.dot.data.store.LetterDesignSharedPreferences
import com.machimpyo.dot.navigation.ROUTE_LETTER_CHECK
import com.machimpyo.dot.navigation.ROUTE_LETTER_REPLY
import com.machimpyo.dot.navigation.ROUTE_SELECT_LETTER_DESIGN
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.service.FirebaseDeepLinkService
import com.machimpyo.dot.ui.screen.select.color.SelectLetterColorViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LetterCheckState(
    val selectedColor: String,
    val selectedPattern: Int,
    var letter: Letter,
    val letterConfig: LetterConfig,
    val canReply: Boolean,
    val userUid: String,
)
@HiltViewModel
class LetterCheckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val prefs: LetterDesignSharedPreferences,
    ) : ViewModel() {

    private var colorList: ColorList? = null

    private fun updateCanReply(loginUserUid: String) {

        Log.e("TAG", _state.value.letter.senderUid.toString())
        if(_state.value.letter.relatedLetterUid != null ) {
            _state.value = _state.value.copy(canReply = false)
        }
        if(_state.value.letter.senderUid == loginUserUid){
            _state.value = _state.value.copy(canReply = false)
        }

        _state.value = _state.value.copy(canReply = true)

    }

    fun updateUserUid(uid: String) {
        _state.value = _state.value.copy(
            userUid = uid
        )
    }

    private suspend fun getLetter(uid: Long) {
        _state.value = _state.value.copy(
            letter = repository.getLetter(uid).getOrThrow()
        )

        Log.i("TAG","${_state.value.letter}")
    }

    private suspend fun getLetterColorList(): ColorList {
        if (prefs.getLetterColorList().isNullOrEmpty()) {
            val letterColorList = repository.getLetterDesign().getOrThrow()
            prefs.putLetterColorList(letterColorList)
        }

        prefs.getLetterColorList()?.let {
            return ColorList(it.toList())
        }

        return ColorList()
    }

    fun getSelectedColor(): Color {
        return Color(android.graphics.Color.parseColor("#${_state.value.selectedColor}"))
    }

//    fun openDeepLink() {
//        viewModelScope.launch {
//            FirebaseDeepLinkService.latestOpenLink?.let { link ->
//                if (link.nav == ROUTE_LETTER_CHECK) {
//                    _state.value = _state.value.copy(
//                        letter = async { repository.getLetter(link.uid.toLong()).getOrThrow()}.await()
//                    )
//                }
//            }
//        }
//
//    }

    private val _state = MutableStateFlow(
        LetterCheckState(
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
            canReply = false,
            userUid = ""
            )
    )

    var state = _state.asStateFlow()
    init {
        viewModelScope.async {
            colorList= async { getLetterColorList()}.await()

            val letterUid: Long? = savedStateHandle.get<Long>("letter_uid")
            letterUid?.let {

                async { getLetter(it)}.await()

                _state.value = _state.value.copy(
                    //TODO DB에서 #을 처리하지 않으셔서 우선 임시방편임
                    selectedColor= "${_state.value.letter.colorcode}",
                    selectedPattern = _state.value.letter.letterDesignUid!!.toInt()
                )

                _state.value = _state.value.copy(
                    letter= _state.value.letter.copy(
                        uid = letterUid
                    )
                )

                updateCanReply(_state.value.userUid)

            }

        }
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

    fun goToReplyScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_LETTER_REPLY + "/${state.value.letter.uid}/${state.value.selectedColor}/${state.value.selectedPattern}"
            )
        )
    }

}