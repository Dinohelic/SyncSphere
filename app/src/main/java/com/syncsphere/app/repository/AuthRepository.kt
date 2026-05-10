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
                        if (raw != null) {
                            val obj = JSONObject(raw)
                            val msg = obj.optString("message", null)
                            if (msg != null && msg.isNotBlank()) {
                                // Try to include validation issues if present
                                val issues = obj.optJSONArray("issues")
                                if (issues != null && issues.length() > 0) {
                                    val sb = StringBuilder(msg)
                                    for (i in 0 until issues.length()) {
                                        val it = issues.optJSONObject(i)
                                        if (it != null) {
                                            sb.append("\n• ").append(it.optString("message", it.toString()))
                                        } else {
                                            sb.append("\n• ").append(issues.optString(i))
                                        }
                                    }
                                    sb.toString()
                                } else {
                                    msg
                                }
                            } else raw
                        } else null
                    } catch (e: Exception) {
                        raw
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
                        if (raw != null) {
                            val obj = JSONObject(raw)
                            obj.optString("message", raw)
                        } else null
                    } catch (e: Exception) {
                        raw
                    }

                    Result.failure(Exception(message ?: "Registration failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

