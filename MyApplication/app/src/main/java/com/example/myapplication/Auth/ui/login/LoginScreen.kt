package com.example.myapplication.Auth.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.Auth.components.AuthOutlinedField
import com.example.myapplication.Auth.components.AuthPasswordField
import com.example.myapplication.Auth.components.OrangePrimaryButton
import com.example.myapplication.Auth.theme.AuthColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthColors.Background)
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))
        Text(
            text = "Вход",
            color = AuthColors.TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(48.dp))
        AuthOutlinedField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = "Почта"
        )

        Spacer(Modifier.height(12.dp))
        AuthPasswordField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = "Пароль"
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextButton(onClick = onForgotPasswordClick) {
                Text("Забыли пароль?", color = AuthColors.LinkBlue)
            }
        }

        if (state.error != null) {
            Text(
                text = state.error!!,
                color = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        if (state.loading) {
            CircularProgressIndicator(color = AuthColors.Orange, modifier = Modifier.size(36.dp))
        } else {
            OrangePrimaryButton(
                text = "Войти",
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth(0.55f),
                enabled = state.email.isNotBlank() && state.password.isNotBlank()
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}
