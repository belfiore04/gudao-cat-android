package com.gudaocat.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val avatar: String? = null,
    val bio: String? = null,
    val created_at: String? = null,
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
)

@Serializable
data class TokenResponse(
    val access_token: String,
    val token_type: String = "bearer",
)
