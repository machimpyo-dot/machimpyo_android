package com.machimpyo.dot.ui.screen.select.pattern

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.data.model.ColorList
import com.machimpyo.dot.data.store.LetterDesignSharedPreferences
import com.machimpyo.dot.navigation.ROUTE_LETTER_WRITE
import com.machimpyo.dot.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kakao.k.r
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectLetterDesignState(
    var selectedColor: String,
    var selectedPattern: Int,
    var isLoading: Boolean = false,
    )
@HiltViewModel
class SelectLetterDesignViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val prefs: LetterDesignSharedPreferences,
    ) : ViewModel() {

    private val _state = MutableStateFlow(
        SelectLetterDesignState(
            selectedColor = "FFFFFF",
            selectedPattern = 0
        )
    )

    var state = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(
            selectedColor = savedStateHandle.get<String>("color_id")!!,
        )
    }

    fun getSelectedColor(): Color {
        return Color(android.graphics.Color.parseColor("#${_state.value.selectedColor}"))
    }
    fun setPattern(index: Int) {
        _state.value = _state.value.copy(
            selectedPattern = index
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

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToLetterWritecreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_LETTER_WRITE + "/${_state.value.selectedColor}/${_state.value.selectedPattern}"
            )
        )
    }
}