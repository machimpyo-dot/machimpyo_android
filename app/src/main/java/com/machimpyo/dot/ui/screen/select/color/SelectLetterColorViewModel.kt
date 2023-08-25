package com.machimpyo.dot.ui.screen.select.color

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.navigation.ROUTE_SELECT_LETTER_DESIGN
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.utils.extension.LetterColorList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectLetterColorState(
    var selectedColor: Int,
)
@HiltViewModel
class SelectLetterColorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
) : ViewModel() {
    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToSelectDesignScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_SELECT_LETTER_DESIGN + "/${state.value.selectedColor}"
            )
        )
    }

    var state = mutableStateOf(
        SelectLetterColorState(
             selectedColor = 0
        )
    )
        private set

    fun setColor(index: Int) {
        state.value.selectedColor = index
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