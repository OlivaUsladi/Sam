package com.example.myapplication.Auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.Auth.components.PageDots
import com.example.myapplication.Auth.ui.login.LoginScreen
import com.example.myapplication.Auth.ui.register.RegisterScreen

@Composable
fun AuthSwipeScreen(
    startWithRegister: Boolean,
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onTermsClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = if (startWithRegister) 1 else 0
    ) { 2 }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                if (page == 0) {
                    LoginScreen(
                        onLoginSuccess = onLoginSuccess,
                        onForgotPasswordClick = onForgotPasswordClick
                    )
                } else {
                    RegisterScreen(
                        onRegisterSuccess = onRegisterSuccess,
                        onTermsClick = onTermsClick
                    )
                }
            }
        }

        PageDots(
            count = 2,
            current = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
