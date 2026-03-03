package com.gudaocat.app.data.api

import com.gudaocat.app.data.model.LoginRequest
import com.gudaocat.app.data.model.RegisterRequest
import com.gudaocat.app.data.model.TokenResponse
import com.gudaocat.app.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): User

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @GET("api/auth/me")
    suspend fun getMe(): User
}
