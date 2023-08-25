package com.machimpyo.dot.ui.screen.letter.write

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.navigation.ROUTE_LETTER_WRITE
import com.machimpyo.dot.repository.MainRepository
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
    Letter.getMock(),
    Letter.getMock(),
    Letter.getMock(),
    Letter.getMock(),
    Letter.getMock(),
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

    private suspend fun getTempLetters(): List<Letter> {

        return testList
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
        viewModelScope.launch {
            val tempLetters = getTempLetters()

            _state.value = _state.value.copy(
                tempLetterList = tempLetters,
                tempLetterNum = tempLetters.size,
                isEmpty = tempLetters.isEmpty()
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