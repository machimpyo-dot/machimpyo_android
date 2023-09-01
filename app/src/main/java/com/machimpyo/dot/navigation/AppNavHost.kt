package com.machimpyo.dot.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.machimpyo.dot.service.FirebaseDeepLinkService
import com.machimpyo.dot.service.Link
import com.machimpyo.dot.ui.auth.AuthViewModel
import com.machimpyo.dot.ui.screen.HomeScreen
import com.machimpyo.dot.ui.screen.ProfileSettingsScreen
import com.machimpyo.dot.ui.screen.SplashScreen
import com.machimpyo.dot.ui.screen.box.BoxScreen
import com.machimpyo.dot.ui.screen.box.BoxViewModel
import com.machimpyo.dot.ui.screen.content.detail.ContentDetailScreen
import com.machimpyo.dot.ui.screen.content.detail.ContentDetailViewModel
import com.machimpyo.dot.ui.screen.home.HomeViewModel
import com.machimpyo.dot.ui.screen.letter.check.LetterCheckScreen
import com.machimpyo.dot.ui.screen.letter.check.LetterCheckViewModel
import com.machimpyo.dot.ui.screen.letter.reply.LetterReplyScreen
import com.machimpyo.dot.ui.screen.letter.write.LetterWriteScreen
import com.machimpyo.dot.ui.screen.library.ThirdPartyLibraryScreen
import com.machimpyo.dot.ui.screen.library.ThirdPartyLibraryViewModel
import com.machimpyo.dot.ui.screen.login.LogInScreen
import com.machimpyo.dot.ui.screen.login.LogInViewModel
import com.machimpyo.dot.ui.screen.mypage.MyPageScreen
import com.machimpyo.dot.ui.screen.mypage.MyPageViewModel
import com.machimpyo.dot.ui.screen.profilesettings.ProfileSettingsViewModel
import com.machimpyo.dot.ui.screen.select.color.SelectLetterColorScreen
import com.machimpyo.dot.ui.screen.select.pattern.SelectLetterDesignScreen
import com.machimpyo.dot.ui.screen.splash.SplashViewModel
import com.machimpyo.dot.ui.screen.web.WebViewScreen
import com.machimpyo.dot.ui.screen.web.WebViewViewModel
import com.machimpyo.dot.utils.extension.DOMAIN_URI_PREFIX

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
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        }

    val _popEnterTransition = popEnterTransition
        ?: {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(700)
            )
        }
//
    val _popExitTransition = popExitTransition //이대로 씀
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
//        exitTransition = _exitTransition,
//        popEnterTransition = _popEnterTransition,
        popExitTransition = _popExitTransition,
        content = content
    )
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel
) {

    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        /*
        스플래시 화면
         */
        AnimatingComposable(
            ROUTE_SPLASH
        ) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = viewModel)
        }

        /*
        서드파티 라이브러리 화면
         */
        AnimatingComposable(
            ROUTE_THIRD_PARTY_LIBRARY
        ) {
            val viewModel: ThirdPartyLibraryViewModel = hiltViewModel()
            ThirdPartyLibraryScreen(viewModel = viewModel, navController = navController)
        }

        /*
        박스 화면
         */
        AnimatingComposable(
            ROUTE_BOX
        ) {
            val boxViewModel: BoxViewModel = hiltViewModel()
            BoxScreen(navController = navController, viewModel = boxViewModel)
        }

        /*
        웹뷰 화면
         */
        AnimatingComposable(
            "$ROUTE_WEB_VIEW?url={url}",
            arguments = listOf(
                navArgument(
                    "url"
                ) {
                    defaultValue = null
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {navBackStackEntry->

            val url = navBackStackEntry.arguments?.getString("url")

            if(url == null) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(navController = navController, viewModel = viewModel, authViewModel = authViewModel)
            } else {
                val viewModel: WebViewViewModel = hiltViewModel()
                WebViewScreen(navController = navController, viewModel = viewModel, url = url)
            }

        }


        /*
        콘텐츠 세부 화면
         */
        AnimatingComposable(
            "$ROUTE_CONTENT_DETAIL?contentUid={contentUid}",
            arguments = listOf(
                navArgument(
                    "contentUid"
                ) {
                    defaultValue = 0
                    type = NavType.IntType
                    nullable = false
                }
            )
        ) {navBackStackEntry->

            val contentUid = navBackStackEntry.arguments?.getInt("contentUid")

            if(contentUid == null) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(navController = navController, viewModel = viewModel, authViewModel = authViewModel)
            } else {
                val viewModel: ContentDetailViewModel = hiltViewModel()
                ContentDetailScreen(navController = navController, viewModel = viewModel, contentUid = contentUid)
            }

        }

        /*
        마이 페이지 화면
         */
        AnimatingComposable(
            route = ROUTE_MY_PAGE
        ) {
            val viewModel: MyPageViewModel = hiltViewModel()
            MyPageScreen(navController = navController, viewModel = viewModel, authViewModel = authViewModel)
        }

        /*
         홈화면
         */
        AnimatingComposable(
            route = ROUTE_HOME
        ) {
            val viewModel: HomeViewModel = hiltViewModel()

            HomeScreen(navController = navController, viewModel = viewModel, authViewModel = authViewModel)
        }

        /*
        편지 확인하는 화면
         */
        AnimatingComposable(
            route = "$ROUTE_LETTER_CHECK/{letter_uid}",
            deepLinks= listOf(navDeepLink { uriPattern = "${Link(nav= ROUTE_LETTER_CHECK, uid = "{letter_uid}")}"}),
            arguments = listOf(
                navArgument("letter_uid") {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) {
            val userState by authViewModel.userState.collectAsState()

            if (userState.user == null) {
                val viewModel: LogInViewModel = hiltViewModel()

                LogInScreen(
                    navController = navController,
                    viewModel = viewModel,
                    authViewModel = authViewModel
                )
            } else {

                val viewModel: LetterCheckViewModel = hiltViewModel()

                LetterCheckScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    viewModel = viewModel
                )

            }

        }

        /*
        편지 쓰는 화면
         */
        AnimatingComposable(
            route = "$ROUTE_LETTER_WRITE/{color_id}/{pattern_id}",
            arguments = listOf(
                navArgument("color_id") {
                    type = NavType.StringType
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
        편지 답장 쓰는 화면
        */
        AnimatingComposable(
            route = "$ROUTE_LETTER_REPLY/{letter_uid}/{color_id}/{pattern_id}",
            arguments = listOf(
                navArgument("letter_uid") {
                    type = NavType.LongType
                    nullable = false
                },
                navArgument("color_id") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("pattern_id") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                }
            )
        ) {
            LetterReplyScreen(navController = navController)
        }

        /*
        편지지 무늬 고르는 화면
         */
        AnimatingComposable(
            route = "$ROUTE_SELECT_LETTER_DESIGN/{color_id}",
            arguments = listOf(
                navArgument("color_id") {
                    type = NavType.StringType
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
            val viewModel: ProfileSettingsViewModel = hiltViewModel()
            ProfileSettingsScreen(
                navController = navController,
                viewModel = viewModel,
                authViewModel = authViewModel
            )
        }

        /*
        로그인 화면
         */
        AnimatingComposable(
            route = ROUTE_LOGIN
        ) {
            val viewModel: LogInViewModel = hiltViewModel()
            LogInScreen(navController = navController, viewModel = viewModel, authViewModel = authViewModel)
        }

    }
}