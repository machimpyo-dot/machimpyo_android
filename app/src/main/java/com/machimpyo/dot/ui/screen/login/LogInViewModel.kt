package com.machimpyo.dot.ui.screen.login


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.navigation.ROUTE_PROFILE_SETTINGS
import com.machimpyo.dot.repository.AuthRepository
import com.machimpyo.dot.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val repository: MainRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect


    fun goToHomeScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_PROFILE_SETTINGS
            )
        )
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    fun loginFail() = viewModelScope.launch {
        _effect.emit(
            Effect.ShowMessage("로그인 오류")
        )
    }


    data class State(
        val isLoading: Boolean = false
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