package com.machimpyo.dot.ui.screen.select.pattern

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.navigation.ROUTE_LETTER_WRITE
import com.machimpyo.dot.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectLetterDesignState(
    var selectedColor: Int,
    var selectedPattern: Int
)
@HiltViewModel
class SelectLetterDesignViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
) : ViewModel() {
    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect



    fun goToLetterWritecreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_LETTER_WRITE + "/${state.value.selectedColor}/${state.value.selectedPattern}"
            )
        )
    }

    var state = mutableStateOf(
        SelectLetterDesignState(
            selectedColor = 0,
            selectedPattern = 0
        )
    )
        private set

    init {
        state.value = state.value.copy(
            selectedColor = savedStateHandle.get<Int>("color_id")!!,
        )
    }

    fun setPattern(index: Int) {
        state.value.selectedPattern = index
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