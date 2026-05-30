package com.example.domain.Auth.use_case

import com.example.domain.Auth.model.AuthSession
import com.example.domain.Auth.model.ForgotPasswordResult
import com.example.domain.Auth.model.User
import com.example.domain.Auth.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthUseCasesTest {

    private val repo: AuthRepository = mock()
    private val session = AuthSession("acc", "ref", 900, User(1, "Тест", "t@t.ru"))

    @Test
    fun `LoginUseCase delegates to repository`() = runTest {
        whenever(repo.login("t@t.ru", "Pass123!")).thenReturn(session)
        val result = LoginUseCase(repo).invoke("t@t.ru", "Pass123!")
        assertEquals(session, result)
        verify(repo).login("t@t.ru", "Pass123!")
    }

    @Test
    fun `RegisterUseCase delegates to repository`() = runTest {
        whenever(repo.register("Тест", "t@t.ru", "Pass123!", "1.0")).thenReturn(session)
        val result = RegisterUseCase(repo).invoke("Тест", "t@t.ru", "Pass123!", "1.0")
        assertEquals(session, result)
        verify(repo).register("Тест", "t@t.ru", "Pass123!", "1.0")
    }

    @Test
    fun `ForgotPasswordUseCase delegates to repository`() = runTest {
        val res = ForgotPasswordResult("Код выдан", "tok123")
        whenever(repo.forgotPassword("t@t.ru")).thenReturn(res)
        val result = ForgotPasswordUseCase(repo).invoke("t@t.ru")
        assertEquals(res, result)
    }

    @Test
    fun `ResetPasswordUseCase delegates to repository`() = runTest {
        ResetPasswordUseCase(repo).invoke("tok", "NewPass1")
        verify(repo).resetPassword("tok", "NewPass1")
    }

    @Test
    fun `LogoutUseCase delegates to repository`() = runTest {
        LogoutUseCase(repo).invoke()
        verify(repo).logout()
    }

    @Test
    fun `GetMeUseCase delegates to repository`() = runTest {
        val user = User(1, "Тест", "t@t.ru")
        whenever(repo.me()).thenReturn(user)
        val result = GetMeUseCase(repo).invoke()
        assertEquals(user, result)
    }

    @Test
    fun `IsLoggedInUseCase returns true when logged in`() {
        whenever(repo.isLoggedIn()).thenReturn(true)
        assertTrue(IsLoggedInUseCase(repo).invoke())
    }

    @Test
    fun `ConsentVersionUseCase returns version from repo`() = runTest {
        whenever(repo.consentVersion()).thenReturn("1.0")
        assertEquals("1.0", ConsentVersionUseCase(repo).invoke())
    }

    @Test
    fun `IsOnboardingCompletedUseCase delegates`() {
        whenever(repo.isOnboardingCompleted()).thenReturn(true)
        assertTrue(IsOnboardingCompletedUseCase(repo).invoke())
    }

    @Test
    fun `MarkOnboardingCompletedUseCase delegates`() {
        MarkOnboardingCompletedUseCase(repo).invoke()
        verify(repo).markOnboardingCompleted()
    }
}
