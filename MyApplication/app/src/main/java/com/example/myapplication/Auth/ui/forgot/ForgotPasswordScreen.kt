package com.example.myapplication.Auth.ui.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AuthColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Восстановление пароля", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = AuthColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuthColors.Background,
                    titleContentColor = AuthColors.TextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AuthColors.Background)
                .padding(padding)
                .padding(horizontal = 28.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state.step) {
                ForgotStep.EnterEmail -> EnterEmailStep(state, viewModel)
                ForgotStep.EnterCode -> EnterCodeStep(state, viewModel)
                ForgotStep.Done -> DoneStep(onFinished)
            }
        }
    }
}

@Composable
private fun EnterEmailStep(
    state: ForgotPasswordUiState,
    viewModel: ForgotPasswordViewModel
) {
    Spacer(Modifier.height(40.dp))
    Text(
        "Введите email, на который зарегистрирован аккаунт. Мы пришлём код для смены пароля.",
        color = AuthColors.TextSecondary,
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(32.dp))
    AuthOutlinedField(
        value = state.email,
        onValueChange = viewModel::onEmailChange,
        placeholder = "Почта"
    )

    if (state.error != null) {
        Spacer(Modifier.height(8.dp))
        Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 14.sp)
    }

    Spacer(Modifier.height(32.dp))
    if (state.loading) {
        CircularProgressIndicator(color = AuthColors.Orange, modifier = Modifier.size(36.dp))
    } else {
        OrangePrimaryButton(
            text = "Отправить",
            onClick = viewModel::submitEmail,
            modifier = Modifier.fillMaxWidth(0.7f),
            enabled = state.email.isNotBlank()
        )
    }
}

@Composable
private fun EnterCodeStep(
    state: ForgotPasswordUiState,
    viewModel: ForgotPasswordViewModel
) {
    Spacer(Modifier.height(40.dp))
    Text(
        "Мы отправили ссылку для сброса на ваш email.\nВведите код из письма и новый пароль.",
        color = AuthColors.TextSecondary,
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(24.dp))
    AuthOutlinedField(
        value = state.code,
        onValueChange = viewModel::onCodeChange,
        placeholder = "Код"
    )

    if (state.devHint != null) {
        Spacer(Modifier.height(4.dp))
        Text(
            state.devHint!!,
            color = AuthColors.TextSecondary,
            fontSize = 11.sp
        )
    }

    Spacer(Modifier.height(12.dp))
    AuthPasswordField(
        value = state.newPassword,
        onValueChange = viewModel::onNewPasswordChange,
        placeholder = "Новый пароль"
    )
    Spacer(Modifier.height(12.dp))
    AuthPasswordField(
        value = state.newPasswordConfirm,
        onValueChange = viewModel::onNewPasswordConfirmChange,
        placeholder = "Подтвердить пароль"
    )

    if (state.error != null) {
        Spacer(Modifier.height(8.dp))
        Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 14.sp)
    }

    Spacer(Modifier.height(32.dp))
    if (state.loading) {
        CircularProgressIndicator(color = AuthColors.Orange, modifier = Modifier.size(36.dp))
    } else {
        OrangePrimaryButton(
            text = "Сменить пароль",
            onClick = viewModel::submitReset,
            modifier = Modifier.fillMaxWidth(0.7f),
            enabled = state.code.isNotBlank() &&
                    state.newPassword.isNotBlank() &&
                    state.newPasswordConfirm.isNotBlank()
        )
    }
}

@Composable
private fun DoneStep(onFinished: () -> Unit) {
    Spacer(Modifier.height(80.dp))
    Text(
        text = "Пароль успешно изменён!",
        color = AuthColors.TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = "Теперь вы можете войти с новым паролем.",
        color = AuthColors.TextSecondary,
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(40.dp))
    OrangePrimaryButton(
        text = "Перейти ко входу",
        onClick = onFinished,
        modifier = Modifier.fillMaxWidth(0.7f)
    )
}
