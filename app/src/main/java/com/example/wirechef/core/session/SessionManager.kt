package com.example.wirechef.core.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("wirechef_session", Context.MODE_PRIVATE)

    fun saveUserId(id: Int) {
        prefs.edit().putInt("USER_ID", id).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("USER_ID", -1)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}