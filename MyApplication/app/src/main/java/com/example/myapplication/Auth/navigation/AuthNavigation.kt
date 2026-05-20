package com.example.myapplication.Auth.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.Auth.ui.AuthSwipeScreen
import com.example.myapplication.Auth.ui.forgot.ForgotPasswordScreen
import com.example.myapplication.Auth.ui.onboarding.OnboardingScreen
import com.example.myapplication.Auth.ui.splash.SplashScreen
import com.example.myapplication.Auth.ui.terms.TermsScreen
import com.example.domain.Auth.use_case.IsLoggedInUseCase
import com.example.domain.Auth.use_case.IsOnboardingCompletedUseCase
import com.example.domain.Auth.use_case.MarkOnboardingCompletedUseCase
import org.koin.androidx.compose.get

sealed class AuthRoutes(val route: String) {
    object Splash : AuthRoutes("splash")
    object Onboarding : AuthRoutes("onboarding")
    object Auth : AuthRoutes("auth?startRegister={startRegister}") {
        fun build(startRegister: Boolean) = "auth?startRegister=$startRegister"
    }
    object Forgot : AuthRoutes("forgot")
    object Terms : AuthRoutes("terms")
}

@Composable
fun AuthNavHost(
    navController: NavHostController,
    onLoggedIn: () -> Unit
) {
    val isLoggedIn: IsLoggedInUseCase = get()
    val isOnboarded: IsOnboardingCompletedUseCase = get()
    val markOnboarded: MarkOnboardingCompletedUseCase = get()

    NavHost(navController = navController, startDestination = AuthRoutes.Splash.route) {

        composable(AuthRoutes.Splash.route) {
            SplashScreen(
                onTimeout = {
                    when {
                        isLoggedIn() -> onLoggedIn()
                        !isOnboarded() -> navController.navigate(AuthRoutes.Onboarding.route) {
                            popUpTo(AuthRoutes.Splash.route) { inclusive = true }
                        }
                        else -> navController.navigate(AuthRoutes.Auth.build(startRegister = false)) {
                            popUpTo(AuthRoutes.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(AuthRoutes.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    markOnboarded()
                    navController.navigate(AuthRoutes.Auth.build(startRegister = true)) {
                        popUpTo(AuthRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = AuthRoutes.Auth.route,
            arguments = listOf(
                androidx.navigation.navArgument("startRegister") {
                    type = androidx.navigation.NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val startRegister = backStackEntry.arguments?.getBoolean("startRegister") ?: false
            AuthSwipeScreen(
                startWithRegister = startRegister,
                onLoginSuccess = onLoggedIn,
                onRegisterSuccess = onLoggedIn,
                onTermsClick = { navController.navigate(AuthRoutes.Terms.route) },
                onForgotPasswordClick = { navController.navigate(AuthRoutes.Forgot.route) }
            )
        }

        composable(AuthRoutes.Forgot.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onFinished = { navController.popBackStack() }
            )
        }

        composable(AuthRoutes.Terms.route) {
            TermsScreen(onBack = { navController.popBackStack() })
        }
    }
}
