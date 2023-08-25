package com.machimpyo.dot.ui.screen.letter.write

import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.BuildConfig
import com.machimpyo.dot.navigation.ROUTE_HOME
import com.machimpyo.dot.navigation.ROUTE_LETTER_CHECK
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.service.AndroidArgumentDynamicLink
import com.machimpyo.dot.service.DotDynamicLink
import com.machimpyo.dot.service.FirebaseDeepLinkService
import com.machimpyo.dot.service.Link
import com.machimpyo.dot.ui.screen.select.Letter
import com.machimpyo.dot.utils.extension.DOMAIN_URI_PREFIX
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SelectLetterWriteState(
    val selectedColor: Int,
    val selectedPattern: Int,
    var letter: Letter,
)
@HiltViewModel
class LetterWriteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
) : ViewModel() {
    fun send() {
        state.value.letter.copy(
//            uuid = repository.getUUID()
        )

        val link = Link(
            url= DOMAIN_URI_PREFIX,
            nav = ROUTE_LETTER_CHECK,
            uuid = state.value.letter.uuid,
        )
        Log.i("DYNAMIC_LINK", "1link: ${link.toString()}")

        val androidArgumentDynamicLink = AndroidArgumentDynamicLink(
            apn = BuildConfig.APPLICATION_ID,
            amv = BuildConfig.VERSION_CODE,
        )
        Log.i("DYNAMIC_LINK", "androidArgumentDynamicLink: ${androidArgumentDynamicLink.toString()}")


        val dynamicLink = DotDynamicLink(
            link = link,
            androidArgumentDynamicLink = androidArgumentDynamicLink
        )

        viewModelScope.async {
            state.value.letter.url = FirebaseDeepLinkService.makeDynamicLink(
                callback = { setDeepLinkUrl(it) },
                dynamicLink= dynamicLink
            ).toString()
        }

    }

    fun save() {
//        TODO("Not yet implemented")
    }

    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    fun goToHomeScreen() = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                ROUTE_HOME
            )
        )
    }

    private fun setDeepLinkUrl(url: String,) {
        state.value.letter.copy(
//            uuid = repository.getUUID()
            url= url
        )
    }

    fun titleValueChange(title: String) {
//        _state.value.letter.title = title
        _state.value = _state.value.copy(
            letter= _state.value.letter.copy(
                title= title,
            )
        )
    }
    fun contentValueChange(content: String) {
        //MaxLength보다 크면 조기리턴
        if (content.length > _state.value.letter.contentMaxLength) return
        //줄이 contentMaxLine만큼 있으면 조기 리턴
        if (content.count { alphabet -> alphabet == '\n' } >= _state.value.letter.contentMaxLine) return

        //            _state.value.letter.content = content
        _state.value = _state.value.copy(
            letter= _state.value.letter.copy(
                content= content,
            )
        )
    }

    private val _state = MutableStateFlow(
        SelectLetterWriteState(
            selectedColor = 0,
            selectedPattern = 0,
            letter= Letter(title= ""),
        )
    )

    var state = _state.asStateFlow()
    init {
        _state.value = _state.value.copy(
            selectedColor = savedStateHandle.get<Int>("color_id")!!,
            selectedPattern = savedStateHandle.get<Int>("pattern_id")!!,
        )
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
}