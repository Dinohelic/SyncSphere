package com.syncsphere.app.models

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

