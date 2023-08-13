package com.machimpyo.dot.ui.screen.profilesettings

import android.util.Log
import androidx.compose.animation.expandHorizontally
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.navigation.ROUTE_HOME
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.ui.screen.login.LogInViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var searchJob: Job? = null

    private var _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToHomeScreen(navController: NavController) = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                route = ROUTE_HOME
            ) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                currentRoute?.let {
                    popUpTo(it)
                }
            }
        )

    }

    fun backButtonClicked(
        navController: NavController
    ) = viewModelScope.launch {
        val previousRouteId = navController.previousBackStackEntry?.destination?.id
        val previousRoute = navController.previousBackStackEntry?.destination?.route
        try {
            _effect.emit(
                Effect.NavigateTo(
                    previousRoute!!
                ) {
                    popUpTo(id = previousRouteId!!) {
                        inclusive = true
                    }
                }
            )
        } catch (e: Exception) {
            _effect.emit(
                Effect.ShowMessage(message = "마지막 페이지 입니다")
            )
        }

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

    fun handleNickname(nickname: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                nickname = nickname
            )
        }
    }

    fun handleCompany(company: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                company = company
            )
        }
    }

    fun handleExitDate(exitDate: Long?) = viewModelScope.launch {
        //TODO(나중에 서버로 전송 처리해야겠지?)
        _state.update {
            it.copy(
                exitDate = exitDate
            )
        }
    }

    fun handleNotAssignedExitDate(isNotAssigned: Boolean) = viewModelScope.launch {
        _state.update {
            it.copy(
                isNotAssigned = isNotAssigned
            )
        }
    }

    fun showMessageForAssigningExitDate() = viewModelScope.launch {
        _effect.emit(
            Effect.ShowMessage(
                "퇴사 미결정을 해제해 주세요"
            )
        )
    }

    data class State(
        val nickname: String = "",
        val company: String = "",
        val exitDate: Long? = null,
        val isNotAssigned: Boolean = false
    )


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

        data class HandleDatePicker(
            val isVisible: Boolean
        ) : Effect()
    }


}