package com.example.myapplication.Auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Auth.use_case.ConsentVersionUseCase
import com.example.domain.Auth.use_case.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_CONSENT_VERSION = "1.0"

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val termsAccepted: Boolean = false,
    val consentVersion: String = DEFAULT_CONSENT_VERSION,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val consentVersionUseCase: ConsentVersionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val v = runCatching { consentVersionUseCase() }.getOrNull()
            if (v != null) _state.update { it.copy(consentVersion = v) }
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v, error = null) }
    fun onEmailChange(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onPasswordConfirmChange(v: String) =
        _state.update { it.copy(passwordConfirm = v, error = null) }
    fun onTermsChange(checked: Boolean) =
        _state.update { it.copy(termsAccepted = checked, error = null) }

    fun submit() {
        val s = _state.value

        val localError = validate(s)
        if (localError != null) {
            _state.update { it.copy(error = localError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val r = runCatching {
                registerUseCase(
                    name = s.name.trim(),
                    email = s.email.trim(),
                    password = s.password,
                    acceptedTermsVersion = s.consentVersion
                )
            }
            r.fold(
                onSuccess = { _state.update { it.copy(loading = false, success = true) } },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            loading = false,
                            error = e.message?.takeIf { m -> m.isNotBlank() }
                                ?: "Не удалось зарегистрироваться"
                        )
                    }
                }
            )
        }
    }

    private fun validate(s: RegisterUiState): String? {
        if (s.name.isBlank() || s.email.isBlank() ||
            s.password.isBlank() || s.passwordConfirm.isBlank()
        ) return "Заполните все поля"
        if (s.password != s.passwordConfirm) return "Пароли не совпадают"
        if (s.password.length < 8) return "Пароль не короче 8 символов"
        if (!s.password.any { it.isDigit() } || !s.password.any { it.isLetter() })
            return "Пароль должен содержать буквы и цифры"
        if (!s.termsAccepted)
            return "Чтобы продолжить, согласитесь с пользовательским соглашением"
        return null
    }
}
