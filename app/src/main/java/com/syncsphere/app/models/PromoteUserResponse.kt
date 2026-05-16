package com.syncsphere.app.models

data class PromoteUserResponse(
    val success: Boolean,
    val message: String?,
    val data: PromoteUserData?
)

data class PromoteUserData(
    val user: UserResponse?
)
