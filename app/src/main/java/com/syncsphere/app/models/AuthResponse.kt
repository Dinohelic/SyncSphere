package com.syncsphere.app.models

data class AuthResponse(
    val message: String,
    val token: String?,
    val user: UserResponse?
)

