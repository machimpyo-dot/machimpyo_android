package com.machimpyo.dot.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.repository.MainRepository
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.navigation.ROUTE_SELECT_LETTER_COLOR
import com.machimpyo.dot.repository.AuthRepository
import com.machimpyo.dot.ui.screen.profilesettings.ProfileSettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private var _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect


    fun goToSelectColorScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_SELECT_LETTER_COLOR
            )
        )
    }

    fun signOut() = viewModelScope.launch {
        authRepository.logOut()
    }
    fun handleDatePicker(
        isVisible: Boolean
    ) = viewModelScope.launch {
        _effect.emit(
            Effect.HandleDatePicker(
                isVisible
            )
        )
    }

    fun handleExitDate(exitDate: Long?) = viewModelScope.launch {
        //TODO(나중에 서버로 전송 처리해야겠지?)
        _state.update {
            it.copy(
                exitDate = exitDate
            )
        }
    }

    data class State(
        val exitDate: Long? = null
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

        data class HandleDatePicker(
            val isVisible: Boolean
        ) : Effect()
    }
}