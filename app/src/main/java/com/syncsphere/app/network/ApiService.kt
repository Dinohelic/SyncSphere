package com.syncsphere.app.network

import com.syncsphere.app.models.*
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @GET("api/tasks")
    suspend fun getTasks(@Header("Authorization") token: String): Response<List<TaskDto>>

    @POST("api/tasks")
    suspend fun createTask(@Header("Authorization") token: String, @Body createTaskRequest: CreateTaskRequest): Response<TaskDto>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(@Header("Authorization") token: String, @Path("id") id: String, @Body updateTaskRequest: UpdateTaskRequest): Response<TaskDto>

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>

    @GET("api/dashboard/stats")
    suspend fun getDashboardStats(@Header("Authorization") token: String): Response<DashboardStatsResponse>

    @GET("api/announcements")
    suspend fun getAnnouncements(@Header("Authorization") token: String): Response<List<AnnouncementDto>>

    @POST("api/announcements")
    suspend fun createAnnouncement(@Header("Authorization") token: String, @Body createAnnouncementRequest: CreateAnnouncementRequest): Response<AnnouncementDto>

    @PUT("api/announcements/{id}")
    suspend fun updateAnnouncement(@Header("Authorization") token: String, @Path("id") id: String, @Body createAnnouncementRequest: CreateAnnouncementRequest): Response<AnnouncementDto>

    @DELETE("api/announcements/{id}")
    suspend fun deleteAnnouncement(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>

    @GET("api/events")
    suspend fun getEvents(@Header("Authorization") token: String): Response<List<EventDto>>

    @POST("api/events")
    suspend fun createEvent(@Header("Authorization") token: String, @Body createEventRequest: CreateEventRequest): Response<EventDto>

    @PUT("api/events/{id}")
    suspend fun updateEvent(@Header("Authorization") token: String, @Path("id") id: String, @Body createEventRequest: CreateEventRequest): Response<EventDto>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>
}

