package com.syncsphere.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

object TokenManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val TOKEN_KEY = "auth_token"
    private const val USER_NAME_KEY = "user_full_name"
    private const val USER_EMAIL_KEY = "user_email"
    private const val USER_ROLE_KEY = "user_role"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        return getPreferences(context).getString(TOKEN_KEY, null)
    }

    fun clearToken(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(TOKEN_KEY)
        editor.remove(USER_NAME_KEY)
        editor.remove(USER_EMAIL_KEY)
        editor.remove(USER_ROLE_KEY)
        editor.apply()
    }

    fun saveUserProfile(context: Context, fullName: String?, email: String?, role: String?) {
        val editor = getPreferences(context).edit()
        editor.putString(USER_NAME_KEY, fullName)
        editor.putString(USER_EMAIL_KEY, email)
        editor.putString(USER_ROLE_KEY, role)
        editor.apply()
    }

    fun getUserName(context: Context): String? = getPreferences(context).getString(USER_NAME_KEY, null)

    fun getUserEmail(context: Context): String? = getPreferences(context).getString(USER_EMAIL_KEY, null)

    fun getUserRole(context: Context): String? {
        val savedRole = getPreferences(context).getString(USER_ROLE_KEY, null)
        if (!savedRole.isNullOrBlank()) return savedRole

        val token = getToken(context) ?: return null
        return decodeRoleFromJwt(token)
    }

    private fun decodeRoleFromJwt(token: String): String? {
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val normalized = payload.replace('-', '+').replace('_', '/')
            val padding = (4 - normalized.length % 4) % 4
            val payloadBytes = Base64.decode(normalized + "=".repeat(padding), Base64.DEFAULT)
            val payloadJson = JSONObject(String(payloadBytes, Charsets.UTF_8))
            payloadJson.optString("role").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}

