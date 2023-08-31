package com.machimpyo.dot.ui.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.data.model.Library
import com.machimpyo.dot.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThirdPartyLibraryViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        initState()
    }

    private fun initState() = viewModelScope.launch {

    }

    data class State(
        val libraries: List<Library> = emptyList()
    )
}

