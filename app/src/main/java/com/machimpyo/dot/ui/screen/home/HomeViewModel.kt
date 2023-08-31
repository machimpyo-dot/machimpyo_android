package com.machimpyo.dot.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.repository.MainRepository
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.data.model.ExitState
import com.machimpyo.dot.data.model.HomeContent
import com.machimpyo.dot.data.model.response.AbstractLetter
import com.machimpyo.dot.data.model.response.UserInfo
import com.machimpyo.dot.navigation.ROUTE_BOX
import com.machimpyo.dot.navigation.ROUTE_CONTENT_DETAIL
import com.machimpyo.dot.navigation.ROUTE_SELECT_LETTER_COLOR
import com.machimpyo.dot.repository.AuthRepository
import com.machimpyo.dot.utils.extension.toLocalDate
import com.machimpyo.dot.utils.extension.toLong
import com.machimpyo.dot.utils.getMockHomeContents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

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
        if(exitDate == null) return@launch

        repository.updateUserExitDate(exitDate.toLocalDate(), true)
        _state.update {
            it.copy(
                exitDate = exitDate
            )
        }
    }

    fun initState(userInfo: UserInfo?) = viewModelScope.launch(Dispatchers.IO) {

        userInfo?: return@launch

        val exitDate = userInfo.exitDate?.toLong()
        val exitState = ExitState.fromExitDate(userInfo.exitDate)
        val profileUrl = userInfo.profileImg
        val nickname = userInfo.nickName
        val company = userInfo.company

        val contents: List<HomeContent> = getMockHomeContents()
        val abstractLettersResult = repository.getAbstractLetters()

        val abstractLetters = abstractLettersResult.getOrNull() ?: emptyList()

        _state.update {
            it.copy(
                exitDate = exitDate,
                exitState = exitState,
                profileUrl = profileUrl,
                nickname = nickname,
                company = company,
                contents = contents,
                abstractLetters = abstractLetters
            )
        }
    }

    fun goToContentDetailScreen(contentUid: Int) = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(route = "$ROUTE_CONTENT_DETAIL?contentUid=$contentUid")
        )
    }

    fun goToBoxScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(ROUTE_BOX)
        )
    }

    data class State(
        val exitDate: Long? = null,
        val exitState: ExitState = ExitState.IsNotAssigned,
        val profileUrl: String? = null,
        val nickname: String? = null,
        val abstractLetters: List<AbstractLetter> = emptyList(),
        val company: String? = null,
        val contents: List<HomeContent> = getMockHomeContents(),
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