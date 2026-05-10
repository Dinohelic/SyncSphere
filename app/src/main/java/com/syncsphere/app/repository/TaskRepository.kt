package com.syncsphere.app.repository

import android.content.Context
import com.syncsphere.app.models.*
import com.syncsphere.app.network.ApiService
import com.syncsphere.app.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val apiService: ApiService,
    private val context: Context
) {
    private val token get() = "Bearer ${TokenManager.getToken(context)}"

    suspend fun getTasks(): Result<List<TaskDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTasks(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get tasks"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(createTaskRequest: CreateTaskRequest): Result<TaskDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createTask(token, createTaskRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(id: String, updateTaskRequest: UpdateTaskRequest): Result<TaskDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateTask(token, id, updateTaskRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteTask(token, id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to delete task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboardStats(): Result<DashboardStatsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDashboardStats(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

