package com.gudaocat.app.data.repository

import android.util.Log
import com.gudaocat.app.data.api.ApiService
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.data.model.CatCreateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CatRepository(
    private val api: ApiService,
) {
    private val _cats = MutableStateFlow<List<Cat>>(emptyList())
    val cats: StateFlow<List<Cat>> = _cats.asStateFlow()

    suspend fun listCats(): Result<List<Cat>> = refreshCats()

    suspend fun refreshCats(): Result<List<Cat>> {
        Log.d("GudaoCat", "CatRepository.listCats request")
        return runCatching { api.getCats() }
            .onSuccess { _cats.value = it }
    }

    suspend fun getCat(catId: Int): Result<Cat> = runCatching { api.getCat(catId) }

    suspend fun createCat(name: String, location: String?, habits: String?): Result<Cat> {
        return try {
            val cat = api.createCat(
                CatCreateRequest(
                    name = name,
                    location = location?.ifBlank { null },
                    habits = habits?.ifBlank { null },
                )
            )
            refreshCats()
            Result.success(cat)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}
