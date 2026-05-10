package com.syncsphere.app.repository

import android.content.Context
import com.syncsphere.app.models.UserResponse
import com.syncsphere.app.network.ApiService
import com.syncsphere.app.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val context: Context
) {
    private val token get() = "Bearer ${TokenManager.getToken(context)}"

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
}
