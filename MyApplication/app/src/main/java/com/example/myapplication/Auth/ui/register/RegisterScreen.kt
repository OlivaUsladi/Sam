package com.example.myapplication.Auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.Auth.components.AuthOutlinedField
import com.example.myapplication.Auth.components.AuthPasswordField
import com.example.myapplication.Auth.components.OrangePrimaryButton
import com.example.myapplication.Auth.theme.AuthColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onTermsClick: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) onRegisterSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))
        Text(
            text = "Регистрация",
            color = AuthColors.TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(36.dp))
        AuthOutlinedField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            placeholder = "Имя"
        )

        Spacer(Modifier.height(12.dp))
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

        Spacer(Modifier.height(12.dp))
        AuthPasswordField(
            value = state.passwordConfirm,
            onValueChange = viewModel::onPasswordConfirmChange,
            placeholder = "Подтвердить пароль"
        )

        Spacer(Modifier.height(20.dp))
        TermsAgreementRow(
            checked = state.termsAccepted,
            onCheckedChange = viewModel::onTermsChange,
            onTermsClick = onTermsClick
        )

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.error!!,
                color = Color(0xFFD32F2F),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(20.dp))
        if (state.loading) {
            CircularProgressIndicator(color = AuthColors.Orange, modifier = Modifier.size(36.dp))
        } else {
            OrangePrimaryButton(
                text = "Регистрация",
                onClick = viewModel::submit,
                enabled = state.termsAccepted &&
                        state.name.isNotBlank() &&
                        state.email.isNotBlank() &&
                        state.password.isNotBlank() &&
                        state.passwordConfirm.isNotBlank(),
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun TermsAgreementRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit
) {
    val text = remember {
        buildAnnotatedString {
            append("Я согласен с условиями ")
            withStyle(
                SpanStyle(
                    color = AuthColors.Orange,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("пользовательского соглашения")
            }
            append(" на обработку моих персональных данных")
        }
    }

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AuthColors.Orange,
                uncheckedColor = AuthColors.BorderGrey,
                checkmarkColor = Color.White
            )
        )
        ClickableAgreementText(
            text = text,
            onClick = onTermsClick,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun ClickableAgreementText(
    text: AnnotatedString,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.ClickableText(
        text = text,
        modifier = modifier,
        style = androidx.compose.ui.text.TextStyle(
            color = AuthColors.TextPrimary,
            fontSize = 13.sp
        ),
        onClick = { onClick() }
    )
}
