package com.machimpyo.dot.ui.screen.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.navigation.ROUTE_PROFILE_SETTINGS
import com.machimpyo.dot.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect


    fun loginButtonClicked() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_PROFILE_SETTINGS
            )
        )
    }
    data class State(
        val tmpData: Int = 0
    )

    sealed class Effect {
        data class ShowMessage(
            val message: String,
            val actionLabel: String? = null,
            val action: ()->Unit = {},
            val dismissed: ()->Unit = {}
        ): Effect()

        data class NavigateTo(
            val route: String,
            val builder: NavOptionsBuilder.() -> Unit = {}
        ): Effect()

    }


}