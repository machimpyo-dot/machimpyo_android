package com.machimpyo.dot.ui.screen.profilesettings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.ui.screen.login.LogInViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var searchJob: Job? = null

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

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

    fun searchCompany(company: String) {
        if(company.isBlank()) {
            return
        }
        searchJob?.cancel() // 이전 검색 코루틴 취소
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            //TODO 검색 추가
        }
    }

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