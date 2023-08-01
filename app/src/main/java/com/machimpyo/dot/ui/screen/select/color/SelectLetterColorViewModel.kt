package com.machimpyo.dot.ui.screen.select.color

import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.navigation.ROUTE_SELECT_LETTER_DESIGN
import com.machimpyo.dot.repository.MainRepository
import kotlinx.coroutines.launch

@HiltViewModel
class SelectLetterColorViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {
    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToSelectDesignScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_SELECT_LETTER_DESIGN
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