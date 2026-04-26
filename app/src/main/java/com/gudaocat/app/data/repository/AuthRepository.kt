package com.gudaocat.app.data.repository

import com.gudaocat.app.BuildConfig
import com.gudaocat.app.data.api.ApiService
import com.gudaocat.app.data.api.TokenProvider
import com.gudaocat.app.data.mock.MockData
import com.gudaocat.app.data.model.LoginRequest
import com.gudaocat.app.data.model.RegisterRequest
import com.gudaocat.app.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenProvider: TokenProvider,
) {
    suspend fun login(username: String, password: String): Result<User> {
        if (BuildConfig.DEMO_MODE) {
            tokenProvider.saveToken(MockData.demoToken)
            return Result.success(MockData.currentUser)
        }

        return try {
            val token = api.login(LoginRequest(username, password))
            tokenProvider.saveToken(token.access_token)
            val user = api.getMe()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String): Result<User> {
        if (BuildConfig.DEMO_MODE) {
            tokenProvider.saveToken(MockData.demoToken)
            return Result.success(
                MockData.currentUser.copy(
                    username = username.ifBlank { MockData.currentUser.username },
                )
            )
        }

        return try {
            api.register(RegisterRequest(username, password))
            // 注册成功后自动登录
            login(username, password).getOrThrow()
            val user = api.getMe()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<User> {
        if (BuildConfig.DEMO_MODE) {
            return Result.success(MockData.currentUser)
        }

        return try {
            Result.success(api.getMe())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenProvider.clearToken()
    }

    suspend fun isLoggedIn(): Boolean {
        if (BuildConfig.DEMO_MODE) {
            return tokenProvider.getToken() != null
        }

        return tokenProvider.getToken() != null
    }
}
