package com.example.myapplication.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Auth.repository.AuthRepository
import com.example.domain.Auth.use_case.GetMeUseCase
import com.example.domain.Auth.use_case.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val getMeUseCase: GetMeUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _name = MutableStateFlow(authRepository.currentUserName() ?: "")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow(authRepository.currentUserEmail() ?: "")
    val email = _email.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut = _loggedOut.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { getMeUseCase() }.onSuccess { user ->
                _name.value = user.name
                _email.value = user.email
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { logoutUseCase() }
            _loggedOut.value = true
        }
    }
}