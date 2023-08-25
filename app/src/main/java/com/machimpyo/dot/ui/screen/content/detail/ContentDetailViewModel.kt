package com.machimpyo.dot.ui.screen.content.detail

import androidx.annotation.DrawableRes
import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fresh.materiallinkpreview.models.OpenGraphMetaData
import com.fresh.materiallinkpreview.parsing.OpenGraphMetaDataProvider
import com.machimpyo.dot.R
import com.machimpyo.dot.data.model.ContentInfo
import com.machimpyo.dot.navigation.ROUTE_WEB_VIEW
import com.machimpyo.dot.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URL

@HiltViewModel
class ContentDetailViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {
    private var _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    private var _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state


    fun initState(
        contentUid: Int
    ) = viewModelScope.launch{

        val result = repository.getContentInfo(contentUid)

        if(result.isFailure && result.getOrNull() == null) {
            _effect.emit(
                Effect.ShowMessage("네트워크 오류, 다음에 다시 시도해주세요 :(")
            )
            return@launch
        }

        val contentInfo = result.getOrNull()!!

        val previewResult = OpenGraphMetaDataProvider().startFetchingMetadataAsync(URL(contentInfo.referenceUrl))

        _state.update {
            it.copy(
                questionText = contentInfo.questionText,
                title = contentInfo.title,
                subtitle = contentInfo.subtitle,
                previewMetaData = previewResult.getOrNull(),
                photoId = contentInfo.photoId,
                replyText = contentInfo.replyText
            )
        }
    }

    fun goToWebViewScreen(metaData: OpenGraphMetaData) = viewModelScope.launch {
        _effect.emit(
            Effect.NavigateTo(
                "$ROUTE_WEB_VIEW?url=${metaData.url}"
            )
        )
    }

    data class State(
        val questionText: String = "",
        val title: String = "",
        val subtitle: String = "",
        val previewMetaData: OpenGraphMetaData? = null,
        @DrawableRes
        val photoId: Int = R.drawable.dot_icon,
        val replyText: String = ""
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