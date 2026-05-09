package com.syncsphere.app.network

import com.syncsphere.app.models.AuthResponse
import com.syncsphere.app.models.LoginRequest
import com.syncsphere.app.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>
}

