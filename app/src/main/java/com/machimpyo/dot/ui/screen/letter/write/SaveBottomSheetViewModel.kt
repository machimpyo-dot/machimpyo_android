package com.machimpyo.dot.ui.screen.letter.write

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.navigation.ROUTE_HOME
import com.machimpyo.dot.navigation.ROUTE_LETTER_WRITE
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.ui.topappbar.Save
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SaveState(
    val tempLetterList: List<Letter>,
    val tempLetterNum: Int,
    val isEmpty:Boolean,
)

val testList = listOf<Letter>(
    Letter("이나경","안녕하세요"),
    Letter("김종구", "반가워요"),
    Letter("이재준","ㅓ아ㅣ버ㅣㅏ어 래ㅑㅔ버ㅐㅑ거비ㅏㅓ리ㅏ멍 리ㅏㅁ ㅓㅣ아러;;ㅣ받 ㅓ개ㅔㅑ 며ㅓ야러미아ㅓㄹ")
)
@HiltViewModel
class SaveBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
): ViewModel(){

    private var _effect = MutableSharedFlow<LetterWriteViewModel.Effect>()
    val effect: SharedFlow<LetterWriteViewModel.Effect> = _effect
    fun goToWriteScreen() = viewModelScope.launch {
        _effect.emit(
            LetterWriteViewModel.Effect.NavigateTo(
                ROUTE_LETTER_WRITE
            )
        )
    }

    private val _state = MutableStateFlow(
        SaveState(
            tempLetterList = listOf<Letter>(),
            tempLetterNum = 0,
            isEmpty = true,
        )
    )

    var state = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(
            tempLetterList = testList,
            tempLetterNum = testList.size,
            isEmpty = testList.isEmpty()
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