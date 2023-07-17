package com.machimpyo.dot.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        /*
        아래는 예시코드에요! (지울 예정)
         */
        composable(
            route = ROUTE_SPLASH
        ) {
            
        }

        /*
        https://google.github.io/accompanist/navigation-animation/ 참고
         */
//        composable(
//            route = ROUTE_SPLASH,
//            enterTransition = {
//
//            },
//            exitTransition = {
//
//            },
//            popEnterTransition = {
//
//            },
//            popExitTransition = {
//
//            }
//        ) {
//
//        }
    }
}