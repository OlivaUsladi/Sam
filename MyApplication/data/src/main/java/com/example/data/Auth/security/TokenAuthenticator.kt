package com.example.data.Auth.security

import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Auth.datasource.remote.api.AuthApiService
import com.example.data.Auth.datasource.remote.dto.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

//При 401 (Неавторизованный пользователь)
class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val authApiServiceProvider: () -> AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        if (response.request.url.encodedPath.startsWith("/api/auth/")) return null

        val refresh = tokenStorage.getRefreshToken() ?: return null

        val newAccess = synchronized(this) {
            val current = tokenStorage.getAccessToken()
            val originalAuth = response.request.header("Authorization")
            if (current != null && originalAuth != "Bearer $current") {
                current
            } else {
                refreshTokensBlocking(refresh)
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccess")
            .build()
    }

    private fun refreshTokensBlocking(refresh: String): String? = runBlocking {
        try {
            val resp = authApiServiceProvider().refresh(RefreshRequestDto(refresh))
            tokenStorage.saveTokens(resp.accessToken, resp.refreshToken)
            resp.accessToken
        } catch (e: Throwable) {
            tokenStorage.clear()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response
        var n = 1
        while (r?.priorResponse != null) { n++; r = r.priorResponse }
        return n
    }
}
