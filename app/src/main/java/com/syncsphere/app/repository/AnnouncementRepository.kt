package com.syncsphere.app.repository

import android.content.Context
import com.syncsphere.app.models.AnnouncementDto
import com.syncsphere.app.models.CreateAnnouncementRequest
import com.syncsphere.app.network.ApiService
import com.syncsphere.app.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnnouncementRepository @Inject constructor(
    private val apiService: ApiService,
    private val context: Context
) {
    private val token get() = "Bearer ${TokenManager.getToken(context)}"

    suspend fun getAnnouncements(): Result<List<AnnouncementDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAnnouncements(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get announcements"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAnnouncement(createAnnouncementRequest: CreateAnnouncementRequest): Result<AnnouncementDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createAnnouncement(token, createAnnouncementRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create announcement"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
