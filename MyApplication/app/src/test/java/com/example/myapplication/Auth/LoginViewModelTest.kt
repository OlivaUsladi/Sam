package com.example.myapplication.Auth

import com.example.domain.Auth.model.AuthSession
import com.example.domain.Auth.model.User
import com.example.domain.Auth.use_case.LoginUseCase
import com.example.myapplication.Auth.ui.login.LoginViewModel
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
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    private val mockSession = AuthSession(
        accessToken = "token",
        refreshToken = "refresh",
        expiresInSeconds = 3600L,
        user = User(id = 1, name = "Test", email = "test@mail.ru")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = Mockito.mock(LoginUseCase::class.java)
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submit with empty email shows error`() = runTest {
        viewModel.onPasswordChange("password")
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Заполните email и пароль", viewModel.state.value.error)
    }

    @Test
    fun `submit with empty password shows error`() = runTest {
        viewModel.onEmailChange("test@mail.ru")
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Заполните email и пароль", viewModel.state.value.error)
    }

    @Test
    fun `successful login sets success true`() = runTest {
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("Password1")
        Mockito.`when`(loginUseCase.invoke("test@mail.ru", "Password1"))
            .thenReturn(mockSession)
        viewModel.submit()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.success)
    }

    @Test
    fun `failed login shows error`() = runTest {
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("wrong")
        Mockito.`when`(loginUseCase.invoke("test@mail.ru", "wrong"))
            .thenThrow(RuntimeException("Неверный email или пароль"))
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Неверный email или пароль", viewModel.state.value.error)
    }

    @Test
    fun `onEmailChange clears error`() = runTest {
        viewModel.submit()
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.error)
        viewModel.onEmailChange("new@mail.ru")
        assertNull(viewModel.state.value.error)
        assertEquals("new@mail.ru", viewModel.state.value.email)
    }
}