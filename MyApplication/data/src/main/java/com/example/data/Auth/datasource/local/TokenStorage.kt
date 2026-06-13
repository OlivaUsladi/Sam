package com.example.data.Auth.datasource.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenStorage(context: Context) {

    private val prefs: SharedPreferences = build(context)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putString(KEY_REFRESH, refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun saveUserInfo(name: String, email: String) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun getUserId(): Int? {
        val v = prefs.getInt(KEY_USER_ID, -1)
        return if (v == -1) null else v
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null && getRefreshToken() != null

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val FILE_NAME = "sam_secure_auth"
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"

        private fun build(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            return try {
                EncryptedSharedPreferences.create(
                    context,
                    FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                context.deleteSharedPreferences(FILE_NAME)
                EncryptedSharedPreferences.create(
                    context,
                    FILE_NAME,
                    MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
        }
    }
}