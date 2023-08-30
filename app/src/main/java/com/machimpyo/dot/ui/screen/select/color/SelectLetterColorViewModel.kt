package com.machimpyo.dot.ui.screen.select.color

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.machimpyo.dot.data.model.ColorList
import com.machimpyo.dot.data.store.LetterDesignSharedPreferences
import com.machimpyo.dot.navigation.ROUTE_SELECT_LETTER_DESIGN
import com.machimpyo.dot.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectLetterColorState(
    var selectedColor: String = "",
    var letterColorList: ColorList = ColorList(),
    var isLoading: Boolean = false,
)
@HiltViewModel
class SelectLetterColorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val prefs: LetterDesignSharedPreferences,
) : ViewModel() {


    fun setColor(colorCode: String) {
        _state.value = _state.value.copy(
            selectedColor = colorCode
        )
    }

    private suspend fun getLetterColorList(): ColorList {
        if (prefs.getLetterColorList().isNullOrEmpty()) {
            val letterColorList = repository.getLetterDesign().getOrThrow()
            prefs.putLetterColorList(letterColorList)
        }

        prefs.getLetterColorList()?.let {
            return ColorList(it.toList())
        }

        return ColorList()
    }

    fun getLetterColor(index: Int): Color {
        return _state.value.letterColorList.getColor(index)
    }

    private val _state = MutableStateFlow(
        SelectLetterColorState(
            selectedColor = "FFFFFF",
        )
    )

    var state = _state.asStateFlow()


    init {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {

            _state.value = _state.value.copy(
                letterColorList = async { getLetterColorList() }.await()
            )
            Log.i("COLOR", "${state.value.letterColorList.list}" +
                    "\n${state.value.letterColorList.list.size}")

        }
        _state.value = _state.value.copy(isLoading = false)

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

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToSelectDesignScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_SELECT_LETTER_DESIGN + "/${state.value.selectedColor}"
            )
        )
    }
}