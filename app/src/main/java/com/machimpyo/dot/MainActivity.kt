package com.machimpyo.dot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import com.machimpyo.dot.navigation.AppNavHost
import com.machimpyo.dot.navigation.ROUTE_HOME
import com.machimpyo.dot.navigation.ROUTE_LETTER_CHECK
import com.machimpyo.dot.navigation.ROUTE_LOGIN
import com.machimpyo.dot.navigation.ROUTE_PROFILE_SETTINGS
import com.machimpyo.dot.service.FirebaseDeepLinkService
import com.machimpyo.dot.service.Link
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.theme.MachimpyoTheme
import com.machimpyo.dot.utils.ThemeHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_UPDATE = 1001

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateListener : OnSuccessListener<AppUpdateInfo>

    private fun initAppUpdateSettings() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateListener = OnSuccessListener<AppUpdateInfo> { appUpdateInfo->
            try {
                with(appUpdateInfo) {
                    if(updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        requestAppUpdate(appUpdateInfo)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        appUpdateManager.appUpdateInfo.addOnSuccessListener(appUpdateListener)
    }
    private fun requestAppUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            this@MainActivity,
            REQUEST_CODE_UPDATE
        )
    }

    private fun showUpdateRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("업데이트 필요")
            .setMessage("업데이트 후 사용할 수 있습니다.")
            .setPositiveButton("업데이트") { dialog, which ->
                // 다이얼로그 확인 버튼 클릭 시 처리
                appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo->
                    appUpdateInfo?.let {
                        requestAppUpdate(it)
                    }
                }
            }
            .setCancelable(false) // 사용자가 백 버튼으로 다이얼로그를 닫을 수 없게 함
            .show()
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        initAppUpdateSettings()
        setupThemeMode()
        setContent {

            val navHostController = rememberAnimatedNavController()

            val authViewModel: AuthViewModel = hiltViewModel()

            val userState by authViewModel.userState.collectAsState()

            LaunchedEffect(Unit) {
//                authViewModel.logOut()

//                async {
//                    FirebaseDeepLinkService.readDynamicLink(
//                        callback = { link ->
////                            authViewModel.updateIsDeeplink(link != null)
////                        FirebaseDeepLinkService.openDynamicLink(
////                            callback = {openLink: Link ->
////                                deepLinkDestination = "${openLink.nav}/${openLink.uid}"
////                                navHostController.navigate(deepLinkDestination)
////                            },
////                            link = link
////                        )
//                        },
//                        intent = intent
//                    )
//                }
            }

            MachimpyoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    AppNavHost(
                        navController = navHostController,
                        startDestination =
                            if(userState.user == null) ROUTE_LOGIN
    //                        //아래쪽에서 이미 설정되어있는 유저면 홈화면으로 이동시켜주어야함
    //                        //이때 uid로 동일 사용자인지 구분할 필요 있음
    //                        //datastore 고려해보고 있음
//                            else if (userState.isDeeplink && FirebaseDeepLinkService.latestOpenLink != null) {
//                                FirebaseDeepLinkService.latestOpenLink!!.nav
//                            }

                            else if (userState.userInfo?.nickName != null && userState.userInfo?.company != null) {
                                ROUTE_HOME
                            }

                            else ROUTE_PROFILE_SETTINGS
                        ,
                        authViewModel = authViewModel
                    )
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->

            val bottoms = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            view.updatePadding(bottom = bottoms)

            insets

        }

    }

    private fun setupThemeMode() {
        ThemeHelper.applyTheme(ThemeHelper.ThemeMode.LIGHT)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityResult(requestCode, resultCode, data)",
        "androidx.activity.ComponentActivity"
    )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_UPDATE) {
            if(resultCode != RESULT_OK) {
                showUpdateRequiredDialog()
            }
        }
    }
}
