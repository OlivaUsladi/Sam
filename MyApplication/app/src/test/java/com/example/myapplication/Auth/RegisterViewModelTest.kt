package com.example.myapplication.Auth

import com.example.domain.Auth.model.AuthSession
import com.example.domain.Auth.model.User
import com.example.domain.Auth.use_case.ConsentVersionUseCase
import com.example.domain.Auth.use_case.RegisterUseCase
import com.example.myapplication.Auth.ui.register.RegisterViewModel
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
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var consentVersionUseCase: ConsentVersionUseCase
    private lateinit var viewModel: RegisterViewModel

    private val mockSession = AuthSession(
        accessToken = "token",
        refreshToken = "refresh",
        expiresInSeconds = 3600L,
        user = User(id = 1, name = "Тест", email = "test@mail.ru")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        registerUseCase = Mockito.mock(RegisterUseCase::class.java)
        consentVersionUseCase = Mockito.mock(ConsentVersionUseCase::class.java)
        viewModel = RegisterViewModel(registerUseCase, consentVersionUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submit with empty fields shows error`() = runTest {
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Заполните все поля", viewModel.state.value.error)
    }

    @Test
    fun `submit with mismatched passwords shows error`() = runTest {
        viewModel.onNameChange("Тест")
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("Password1")
        viewModel.onPasswordConfirmChange("Password2")
        viewModel.onTermsChange(true)
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Пароли не совпадают", viewModel.state.value.error)
    }

    @Test
    fun `submit with short password shows error`() = runTest {
        viewModel.onNameChange("Тест")
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("Pass1")
        viewModel.onPasswordConfirmChange("Pass1")
        viewModel.onTermsChange(true)
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Пароль не короче 8 символов", viewModel.state.value.error)
    }

    @Test
    fun `submit with password without digits shows error`() = runTest {
        viewModel.onNameChange("Тест")
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("PasswordNoDigit")
        viewModel.onPasswordConfirmChange("PasswordNoDigit")
        viewModel.onTermsChange(true)
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Пароль должен содержать буквы и цифры", viewModel.state.value.error)
    }

    @Test
    fun `submit with password without letters shows error`() = runTest {
        viewModel.onNameChange("Тест")
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("12345678")
        viewModel.onPasswordConfirmChange("12345678")
        viewModel.onTermsChange(true)
        viewModel.submit()
        advanceUntilIdle()
        assertEquals("Пароль должен содержать буквы и цифры", viewModel.state.value.error)
    }

    @Test
    fun `submit without terms accepted shows error`() = runTest {
        viewModel.onNameChange("Тест")
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("Password1")
        viewModel.onPasswordConfirmChange("Password1")
        viewModel.onTermsChange(false)
        viewModel.submit()
        advanceUntilIdle()
        assertEquals(
            "Чтобы продолжить, согласитесь с пользовательским соглашением",
            viewModel.state.value.error
        )
    }

    @Test
    fun `submit with valid data calls registerUseCase and sets success`() = runTest {
        viewModel.onNameChange("Тест")
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("Password1")
        viewModel.onPasswordConfirmChange("Password1")
        viewModel.onTermsChange(true)

        Mockito.`when`(
            registerUseCase.invoke(
                name = "Тест",
                email = "test@mail.ru",
                password = "Password1",
                acceptedTermsVersion = "1.0"
            )
        ).thenReturn(mockSession)

        viewModel.submit()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.success)
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `submit with server error shows error message`() = runTest {
        viewModel.onNameChange("Тест")
        viewModel.onEmailChange("test@mail.ru")
        viewModel.onPasswordChange("Password1")
        viewModel.onPasswordConfirmChange("Password1")
        viewModel.onTermsChange(true)

        Mockito.`when`(
            registerUseCase.invoke(
                name = "Тест",
                email = "test@mail.ru",
                password = "Password1",
                acceptedTermsVersion = "1.0"
            )
        ).thenThrow(RuntimeException("Пользователь с таким email уже существует"))

        viewModel.submit()
        advanceUntilIdle()
        assertEquals(
            "Пользователь с таким email уже существует",
            viewModel.state.value.error
        )
    }
}