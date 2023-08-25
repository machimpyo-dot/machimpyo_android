package com.machimpyo.dot.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.screen.HomeScreen
import com.machimpyo.dot.ui.screen.ProfileSettingsScreen
import com.machimpyo.dot.ui.screen.letter.write.LetterWriteScreen
import com.machimpyo.dot.ui.screen.login.LogInScreen
import com.machimpyo.dot.ui.screen.select.color.SelectLetterColorScreen
import com.machimpyo.dot.ui.screen.select.pattern.SelectLetterDesignScreen

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
    navController: NavHostController,
    startDestination: String
) {



    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {



        /*
         홈화면
         */
        AnimatingComposable(
            route = ROUTE_HOME
        ) {
            HomeScreen(navController = navController)
        }
        /*
        편지 쓰는 화면
         */
        AnimatingComposable(
            route = "$ROUTE_LETTER_WRITE/{color_id}/{pattern_id}",
            arguments = listOf(
                navArgument("color_id") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                },
                navArgument("pattern_id") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                }
            )
        ) {
            LetterWriteScreen(navController = navController)
        }

        /*
        편지지 무늬 고르는 화면
         */
        AnimatingComposable(
            route = "$ROUTE_SELECT_LETTER_DESIGN/{color_id}",
            arguments = listOf(
                navArgument("color_id") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                }
            )
        ) {
            SelectLetterDesignScreen(navController = navController)
        }


        /*
        편지지 색상 고르는 화면
         */
        AnimatingComposable(
            route = ROUTE_SELECT_LETTER_COLOR
        ) {
            SelectLetterColorScreen(
                navController = navController
            )
        }

        /*
        유저 정보 기입 화면
         */
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
        스플래시 화면
         */
        AnimatingComposable(
            route = ROUTE_SPLASH
        ) {
            
        }


    }
}