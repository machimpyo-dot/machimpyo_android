package com.machimpyo.dot.ui.screen.letter.check

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.data.model.LetterConfig
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.ui.screen.select.Letter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LetterCheckState(
    val selectedColor: Int,
    val selectedPattern: Int,
    var letter: Letter,
    val letterConfig: LetterConfig,
)
@HiltViewModel
class LetterCheckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
) : ViewModel() {
    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    private suspend fun getLetter(uid: Long): Letter {

        //TODO api 처리
        return Letter.getMock()
    }

    private val _state = MutableStateFlow(
        LetterCheckState(
            selectedColor = 0,
            selectedPattern = 0,
            //TODO repo에서 uid로 letter 가져오기
            letter= Letter.getMock(),
            letterConfig = LetterConfig()
            )
    )

    var state = _state.asStateFlow()
    init {
        val letterUid: Long = savedStateHandle.get<Long>("letter_uid")!!

        viewModelScope.launch {
            val chekedLetter = getLetter(letterUid)

            _state.value = _state.value.copy(
            )

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
}