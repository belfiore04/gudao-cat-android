package com.gudaocat.app.data.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gudaocat.app.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
private val TOKEN_KEY = stringPreferencesKey("jwt_token")

object NetworkModule {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun createTokenProvider(context: Context): TokenProvider {
        return object : TokenProvider {
            override suspend fun getToken(): String? {
                return context.dataStore.data.map { it[TOKEN_KEY] }.first()
            }

            override suspend fun saveToken(token: String) {
                context.dataStore.edit { it[TOKEN_KEY] = token }
            }

            override suspend fun clearToken() {
                context.dataStore.edit { it.remove(TOKEN_KEY) }
            }
        }
    }

    private fun createOkHttpClient(tokenProvider: TokenProvider): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private fun createRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    fun createApiService(tokenProvider: TokenProvider): ApiService {
        val retrofit = createRetrofit(createOkHttpClient(tokenProvider))
        return retrofit.create(ApiService::class.java)
    }
}
