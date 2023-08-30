package com.machimpyo.dot

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class DotApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "33977efb0747b30c4d78156641109a43")
    }
}