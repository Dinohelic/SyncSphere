package com.syncsphere.app.repository

import android.content.Context
import com.syncsphere.app.models.UserResponse
import com.syncsphere.app.network.ApiService
import com.syncsphere.app.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val context: Context
) {
    private val token get() = "Bearer ${TokenManager.getToken(context).orEmpty()}"

    suspend fun getUsers(): Result<List<UserResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUsers(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get users"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun promoteUser(userId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.promoteUser(token, userId)
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(body?.message ?: "User promoted successfully")
            } else {
                val raw = response.errorBody()?.string()
                val message = try {
                    raw?.let { JSONObject(it).optString("message", it) }
                } catch (_: Exception) {
                    raw
                }
                Result.failure(Exception(message ?: "Failed to promote user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
