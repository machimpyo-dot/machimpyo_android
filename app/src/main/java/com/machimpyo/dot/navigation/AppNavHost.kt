package com.machimpyo.dot.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.machimpyo.dot.ui.screen.ProfileSettingsScreen
import com.machimpyo.dot.ui.screen.login.LogInScreen

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.AnimatingComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    content: @Composable() (AnimatedVisibilityScope.(NavBackStackEntry) -> Unit)
): Unit {

    val _enterTransition = enterTransition ?: {
        slideIntoContainer(
            AnimatedContentScope.SlideDirection.Left,
            animationSpec = tween(700)
        )
    }

    val _exitTransition = exitTransition
        ?: {
        slideOutOfContainer(
            AnimatedContentScope.SlideDirection.Left,
            animationSpec = tween(700)
        )
    }

    val _popEnterTransition = popEnterTransition
        ?: {
        slideIntoContainer(
            AnimatedContentScope.SlideDirection.Left,
            animationSpec = tween(700)
        )
    }

    val _popExitTransition = popExitTransition
        ?: {
        slideOutOfContainer(
            AnimatedContentScope.SlideDirection.Left,
            animationSpec = tween(700)
        )
    }

    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = _enterTransition,
        exitTransition = _exitTransition,
        popEnterTransition = _popEnterTransition,
        popExitTransition = _popExitTransition,
        content = content
    )
}
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = ROUTE_LOGIN
) {

    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        AnimatingComposable(
            route = ROUTE_PROFILE_SETTINGS
        ) {
            ProfileSettingsScreen(navController = navController)
        }

        /*
        로그인 화면
         */
        AnimatingComposable(
            route = ROUTE_LOGIN
        ) {
            LogInScreen(navController = navController)
        }
        /*
        아래는 예시코드에요! (지울 예정)
         */
        AnimatingComposable(
            route = ROUTE_SPLASH
        ) {
            
        }


    }
}