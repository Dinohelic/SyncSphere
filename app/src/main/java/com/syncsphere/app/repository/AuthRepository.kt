package com.syncsphere.app.repository

import com.syncsphere.app.models.AuthResponse
import com.syncsphere.app.models.LoginRequest
import com.syncsphere.app.models.RegisterRequest
import com.syncsphere.app.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(loginRequest)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val raw = response.errorBody()?.string()
                    val message = try {
                        if (raw == null) {
                            "Unable to connect. Please try again."
                        } else if (raw.trim().startsWith("<")) {
                            // HTML or unexpected payload from server
                            "Unable to connect. Please try again."
                        } else {
                            val obj = JSONObject(raw)
                            val msg = obj.optString("message", null)
                            if (!msg.isNullOrBlank()) msg else raw
                        }
                    } catch (e: Exception) {
                        if (!raw.isNullOrBlank() && raw.trim().startsWith("<")) {
                            "Unable to connect. Please try again."
                        } else {
                            raw ?: "Unable to connect. Please try again."
                        }
                    }

                    Result.failure(Exception(message ?: "Login failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(registerRequest: RegisterRequest): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(registerRequest)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val raw = response.errorBody()?.string()
                    val message = try {
                        if (raw == null) {
                            "Unable to connect. Please try again."
                        } else if (raw.trim().startsWith("<")) {
                            "Unable to connect. Please try again."
                        } else {
                            val obj = JSONObject(raw)
                            obj.optString("message", raw)
                        }
                    } catch (e: Exception) {
                        if (!raw.isNullOrBlank() && raw.trim().startsWith("<")) {
                            "Unable to connect. Please try again."
                        } else {
                            raw ?: "Unable to connect. Please try again."
                        }
                    }

                    Result.failure(Exception(message ?: "Registration failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

