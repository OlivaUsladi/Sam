package com.example.data.Auth.datasource.local

import android.content.Context
import android.content.SharedPreferences

class OnboardingStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    fun markCompleted() {
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
    }

    companion object {
        private const val FILE_NAME = "sam_onboarding"
        private const val KEY_COMPLETED = "completed"
    }
}
