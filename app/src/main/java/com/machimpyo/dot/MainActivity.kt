package com.machimpyo.dot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.auth.FirebaseAuth
import com.machimpyo.dot.navigation.AppNavHost
import com.machimpyo.dot.navigation.ROUTE_HOME
import com.machimpyo.dot.navigation.ROUTE_LOGIN
import com.machimpyo.dot.navigation.ROUTE_PROFILE_SETTINGS
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.theme.MachimpyoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navHostController = rememberAnimatedNavController()

            val authViewModel: AuthViewModel = hiltViewModel()

            val user by authViewModel.user.collectAsState()

            MachimpyoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navHostController,
                        startDestination =
                        if(user == null) ROUTE_LOGIN
                        //아래쪽에서 이미 설정되어있는 유저면 홈화면으로 이동시켜주어야함
                        //이때 uid로 동일 사용자인지 구분할 필요 있음
                        //datastore 고려해보고 있음
                        else ROUTE_PROFILE_SETTINGS
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
}
