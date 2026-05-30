package com.example.myapplication.Auth

import com.example.domain.Auth.use_case.ForgotPasswordUseCase
import com.example.domain.Auth.use_case.ResetPasswordUseCase
import com.example.myapplication.Auth.ui.forgot.ForgotPasswordViewModel
import com.example.myapplication.Auth.ui.forgot.ForgotStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var forgotPasswordUseCase: ForgotPasswordUseCase
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase
    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        forgotPasswordUseCase = Mockito.mock(ForgotPasswordUseCase::class.java)
        resetPasswordUseCase = Mockito.mock(ResetPasswordUseCase::class.java)
        viewModel = ForgotPasswordViewModel(forgotPasswordUseCase, resetPasswordUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitEmail with empty email shows error`() = runTest {
        viewModel.submitEmail()
        advanceUntilIdle()
        assertEquals("Введите email", viewModel.state.value.error)
    }

    @Test
    fun `submitReset with empty code shows error`() = runTest {
        viewModel.submitReset()
        advanceUntilIdle()
        assertEquals("Введите код", viewModel.state.value.error)
    }

    @Test
    fun `submitReset with short password shows error`() = runTest {
        viewModel.onCodeChange("abc123")
        viewModel.onNewPasswordChange("Pass1")
        viewModel.onNewPasswordConfirmChange("Pass1")
        viewModel.submitReset()
        advanceUntilIdle()
        assertEquals(
            "Пароль — минимум 8 символов, буквы и цифры",
            viewModel.state.value.error
        )
    }

    @Test
    fun `submitReset with mismatched passwords shows error`() = runTest {
        viewModel.onCodeChange("abc123")
        viewModel.onNewPasswordChange("Password1")
        viewModel.onNewPasswordConfirmChange("Password2")
        viewModel.submitReset()
        advanceUntilIdle()
        assertEquals("Пароли не совпадают", viewModel.state.value.error)
    }

    @Test
    fun `submitReset with password without digits shows error`() = runTest {
        viewModel.onCodeChange("abc123")
        viewModel.onNewPasswordChange("PasswordOnly")
        viewModel.onNewPasswordConfirmChange("PasswordOnly")
        viewModel.submitReset()
        advanceUntilIdle()
        assertEquals(
            "Пароль — минимум 8 символов, буквы и цифры",
            viewModel.state.value.error
        )
    }

    @Test
    fun `successful reset transitions to Done`() = runTest {
        viewModel.onCodeChange("abc123")
        viewModel.onNewPasswordChange("Password1")
        viewModel.onNewPasswordConfirmChange("Password1")
        Mockito.`when`(resetPasswordUseCase.invoke("abc123", "Password1"))
            .thenReturn(Unit)
        viewModel.submitReset()
        advanceUntilIdle()
        assertEquals(ForgotStep.Done, viewModel.state.value.step)
    }
}
