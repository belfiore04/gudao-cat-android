package com.gudaocat.app

import android.content.Context
import com.gudaocat.app.data.api.NetworkModule
import com.gudaocat.app.data.repository.AuthRepository
import com.gudaocat.app.data.repository.CatRepository
import com.gudaocat.app.data.repository.CommunityRepository
import com.gudaocat.app.data.repository.RecognitionRepository

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val tokenProvider = NetworkModule.createTokenProvider(appContext)
    private val apiService = NetworkModule.createApiService(tokenProvider)

    val catRepository = CatRepository(apiService)
    val authRepository = AuthRepository(apiService, tokenProvider)
    val communityRepository = CommunityRepository(apiService)
    val recognitionRepository = RecognitionRepository(apiService)
}
