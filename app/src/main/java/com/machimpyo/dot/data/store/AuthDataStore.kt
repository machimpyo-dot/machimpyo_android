package com.machimpyo.dot.data.store

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.machimpyo.dot.UserInfoProto
import com.machimpyo.dot.data.model.response.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AuthDataStore @Inject constructor(
    private val context: Context
) {
    companion object {
        private val Context.userInfoDataStore: DataStore<UserInfoProto> by dataStore(
            fileName = "user_info.pb",
            serializer = UserInfoProtoSerializer
        )
        private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore("firebase_id_token")
        val ID_TOKEN = stringPreferencesKey("id_token")
    }


//    private var _userInfo: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
//    val userInfo: StateFlow<UserInfo?> = _userInfo
//
//    private var _idToken: MutableStateFlow<String?> = MutableStateFlow(null)
//    val idToken: StateFlow<String?> = _idToken

    fun getIdTokenFlow(): Flow<Preferences> {
        return context.tokenDataStore.data
    }

    fun getUserInfoFlow(): Flow<UserInfoProto> {
        return context.userInfoDataStore.data
    }

    fun updateUserInfo(
        data: UserInfo?
    ) = CoroutineScope(Dispatchers.IO).launch {
        context.userInfoDataStore.updateData {
            data?.toProto() ?: UserInfoProto.getDefaultInstance()
        }
//        _userInfo.update {
//            data
//        }
    }

    fun updateIdToken(
        data: String?
    ) = CoroutineScope(Dispatchers.IO).launch {
        Log.e("TAG", "아이디 토큰 업데이트 한다!!! $data")
        context.tokenDataStore.edit { prefs ->
            if (data == null) {
                prefs.remove(ID_TOKEN)
            } else {
                prefs[ID_TOKEN] = data
            }
            Log.e("TAG", "아이디 토큰 업데이트 한다!!222 $data")
        }
        val pref = context.tokenDataStore.data.first().toPreferences()
        Log.e("TAG", "아이디 토큰 업데이트 한다!!333 ${pref[ID_TOKEN]}")
//        _idToken.update {
//            data
//        }
    }
}

object UserInfoProtoSerializer : Serializer<UserInfoProto> {

    override suspend fun readFrom(input: InputStream): UserInfoProto {
        try {
            return UserInfoProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserInfoProto, output: OutputStream) = t.writeTo(output)
    override val defaultValue: UserInfoProto
        get() = UserInfoProto.getDefaultInstance()
}
