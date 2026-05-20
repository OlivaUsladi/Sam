package com.example.data.Auth.security

import com.example.data.Auth.datasource.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        val isAuthEndpoint = path.startsWith("/api/auth/")
        if (isAuthEndpoint) {
            return chain.proceed(original)
        }

        val accessToken = tokenStorage.getAccessToken()
        val builder = original.newBuilder()
        if (accessToken != null && original.header(HEADER_AUTH) == null) {
            builder.header(HEADER_AUTH, "Bearer $accessToken")
        }

        return chain.proceed(builder.build())
    }

    companion object {
        private const val HEADER_AUTH = "Authorization"
    }
}
