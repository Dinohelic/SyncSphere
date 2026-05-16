package com.syncsphere.app.models

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthData?
)

data class AuthData(
    val token: String?,
    val user: UserResponse?
)

