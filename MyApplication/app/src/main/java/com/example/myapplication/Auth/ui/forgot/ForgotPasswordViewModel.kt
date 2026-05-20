package com.example.myapplication.Auth.ui.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Auth.use_case.ForgotPasswordUseCase
import com.example.domain.Auth.use_case.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ForgotStep { EnterEmail, EnterCode, Done }

data class ForgotPasswordUiState(
    val step: ForgotStep = ForgotStep.EnterEmail,
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val newPasswordConfirm: String = "",
    val devHint: String? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    fun onEmailChange(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onCodeChange(v: String) = _state.update { it.copy(code = v, error = null) }
    fun onNewPasswordChange(v: String) = _state.update { it.copy(newPassword = v, error = null) }
    fun onNewPasswordConfirmChange(v: String) =
        _state.update { it.copy(newPasswordConfirm = v, error = null) }

    fun submitEmail() {
        val s = _state.value
        if (s.email.isBlank()) {
            _state.update { it.copy(error = "Введите email") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { forgotPasswordUseCase(s.email.trim()) }.fold(
                onSuccess = { r ->
                    _state.update {
                        it.copy(
                            loading = false,
                            step = ForgotStep.EnterCode,
                            code = r.resetToken.orEmpty(),
                            devHint = r.resetToken?.let { tok ->
                                "DEV: код для отладки автоматически подставлен."
                            }
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(loading = false, error = e.message ?: "Не удалось отправить запрос")
                    }
                }
            )
        }
    }

    fun submitReset() {
        val s = _state.value
        if (s.code.isBlank()) {
            _state.update { it.copy(error = "Введите код") }
            return
        }
        if (s.newPassword.length < 8 ||
            !s.newPassword.any { it.isDigit() } || !s.newPassword.any { it.isLetter() }
        ) {
            _state.update { it.copy(error = "Пароль — минимум 8 символов, буквы и цифры") }
            return
        }
        if (s.newPassword != s.newPasswordConfirm) {
            _state.update { it.copy(error = "Пароли не совпадают") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { resetPasswordUseCase(s.code.trim(), s.newPassword) }.fold(
                onSuccess = {
                    _state.update { it.copy(loading = false, step = ForgotStep.Done) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = e.message ?: "Не удалось сменить пароль"
                        )
                    }
                }
            )
        }
    }
}
