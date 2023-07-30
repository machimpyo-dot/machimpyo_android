package com.machimpyo.dot.ui.screen.profilesettings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machimpyo.dot.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var searchJob: Job? = null

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

}