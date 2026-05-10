package com.syncsphere.app.repository

import com.syncsphere.app.models.EventDto
import android.content.Context
import com.syncsphere.app.models.CreateEventRequest
import com.syncsphere.app.network.ApiService
import com.syncsphere.app.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class EventRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    private val token get() = "Bearer ${TokenManager.getToken(context)}"

    suspend fun getEvents(): Result<List<EventDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getEvents(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(createEventRequest: CreateEventRequest): Result<EventDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createEvent(token, createEventRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

