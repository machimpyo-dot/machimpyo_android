package com.machimpyo.dot.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.machimpyo.dot.data.model.response.UserInfo
import com.machimpyo.dot.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _userState = MutableStateFlow<UserState>(UserState())
    val userState: StateFlow<UserState> = _userState

    private val authStateListener = FirebaseAuth.IdTokenListener { auth ->
        updateUserInfo(auth.currentUser)
    }

    init {
        firebaseAuth.addIdTokenListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeIdTokenListener(authStateListener)
    }

    fun updateUserInfo(user: FirebaseUser? = firebaseAuth.currentUser) = viewModelScope.launch(Dispatchers.IO) {

        if(user == null) {
            _userState.update {
                UserState()
            }
            return@launch
        }
        Log.e("TAG", "AuthViewModel 유저 정보 업데이트 !!!")
        val result = repository.getUserInfo()
        Log.e("TAG", "서버로 부터 유저정보 가져오기 $result")
        if(result.isFailure) {
            return@launch
        }

        val userInfo = result.getOrNull()

        _userState.update {
            UserState(
                user,
                userInfo
            )
        }
    }

    fun kakaoLogin(context: Context, callback: (Boolean)->Unit) = viewModelScope.launch {
        repository.signInWithKakao(context) { result ->
            //여기서 동적링크였는지 확인 가능 && 딥링크도 받아야함 -> 딥링크에서 편지 uid 받아야함
            callback(result.isSuccess)
            Log.e("TAG", "로그인 테스트!!! $result")
        }
    }

    fun logOut() = viewModelScope.launch {
        firebaseAuth.signOut()
    }

    data class UserState(
        val user: FirebaseUser? = null,
        val userInfo: UserInfo? = null,
        //딥링크인지
    )
}