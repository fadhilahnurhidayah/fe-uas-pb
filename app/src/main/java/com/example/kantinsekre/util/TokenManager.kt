package com.example.kantinsekre.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString("auth_token", token) }
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun getUserId(): Int? {
        val userId = prefs.getInt("user_id", -1)
        return if (userId == -1) null else userId
    }

    fun clearAll() {
        prefs.edit {
            remove("auth_token")
                .remove("user_id")
        }
    }
}
