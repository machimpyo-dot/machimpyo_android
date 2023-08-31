package com.machimpyo.dot.ui.screen.mypage

import android.net.Uri
import android.util.Log
import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.data.model.LetterBoxItem
import com.machimpyo.dot.navigation.ROUTE_LETTER_CHECK
import com.machimpyo.dot.navigation.ROUTE_WEB_VIEW
import com.machimpyo.dot.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {
    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    private var _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state

    init {
        initState()
    }

    private fun initState() = viewModelScope.launch(Dispatchers.IO) {

        val result = repository.getLetterBox()

        if(result.isFailure) {
            Effect.ShowMessage("현재 편지보관함 상태를 확인할 수 없습니다")
            return@launch
        }

        val letterBoxItems = result.getOrNull() ?: emptyList()

        _state.update {
            it.copy(
                letterBoxItems = letterBoxItems
            )
        }
    }

    fun updateCompanyImage(currentPage: Int?, uri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        currentPage ?: return@launch

        uri ?: return@launch
        if(state.value.letterBoxItems.isEmpty()) return@launch
        val currentLetterBoxItem = state.value.letterBoxItems.getOrNull(currentPage) ?: return@launch
        val currentCompany = currentLetterBoxItem.company

        //TODO 업데이트하고 회사 이미지 url response로 받아야함

        val updatedLetterBoxItems = state.value.letterBoxItems.map {
            if(it.company.uid == currentCompany.uid) {
                it.copy(
                    company = currentCompany.copy(
                        photoUrl = uri
                    )
                )
            } else {
                it
            }
        }

        _state.update {
            it.copy(
                letterBoxItems = updatedLetterBoxItems
            )
        }

    }

    fun goToLetterCheckScreen(letterUid: Long) = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                "$ROUTE_LETTER_CHECK/$letterUid"
            )
        )
    }

    fun goToThirdPartyLibraryCheckScreen() = viewModelScope.launch {
        val url = "https://androidnut.notion.site/ed0c4d675287443c8c41e055389dfb8f?pvs=4"
        val route = "$ROUTE_WEB_VIEW?url={$url}"

        _effect.emit(
            Effect.NavigateTo(
                route
            )
        )
    }

    data class State(
        val letterBoxItems: List<LetterBoxItem> = emptyList()
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
    }
}