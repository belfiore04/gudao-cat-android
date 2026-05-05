package com.gudaocat.app.data.repository

import android.util.Log
import com.gudaocat.app.data.api.ApiService
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.data.model.CatCreateRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun listCats(): Result<List<Cat>> {
        Log.d("GudaoCat", "CatRepository.listCats request")
        return runCatching { api.getCats() }
    }

    suspend fun getCat(catId: Int): Result<Cat> = runCatching { api.getCat(catId) }

    suspend fun createCat(name: String, location: String?, habits: String?): Result<Cat> {
        return runCatching {
            api.createCat(
                CatCreateRequest(
                    name = name,
                    location = location?.ifBlank { null },
                    habits = habits?.ifBlank { null },
                )
            )
        }
    }
}
