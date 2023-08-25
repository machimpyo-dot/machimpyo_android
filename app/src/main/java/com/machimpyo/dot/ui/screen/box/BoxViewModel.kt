package com.machimpyo.dot.ui.screen.box

import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.data.model.LetterName
import com.machimpyo.dot.navigation.ROUTE_MY_PAGE
import com.machimpyo.dot.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BoxViewModel @Inject constructor(
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
        val result = repository.getLetterNames()

        if(result.isFailure) {
            _effect.emit(
                Effect.ShowMessage(
                    "지금은 편지를 가져올 수 없어요 :("
                )
            )
            return@launch
        }

        val letterNames = result.getOrNull()?.toMutableList() ?: emptyList<LetterName>().toMutableList()

        if(letterNames.size < 30) {
            for(i in 0 until 30 - letterNames.size) {
                letterNames.add(
                    LetterName(
                        letterUid = null,
                        nickName = null
                    )
                )
            }
        }

        _state.update {
            it.copy(
                letterNames = letterNames
            )
        }
    }

    fun goToMyPageScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(ROUTE_MY_PAGE)
        )
    }

    fun goToLetterCheckScreen() = viewModelScope.launch {
        //TODO - 나중에 나경님이랑 같이 추가
    }

    data class State(
        val letterNames: List<LetterName> = emptyList()
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