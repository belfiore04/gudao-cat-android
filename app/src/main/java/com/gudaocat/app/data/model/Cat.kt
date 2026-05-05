package com.gudaocat.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Cat(
    val id: Int,
    val name: String,
    val habits: String? = null,
    val location: String? = null,
    val photos: List<String>? = null,
    val creator_id: Int? = null,
    val created_at: String? = null,
)

@Serializable
data class CatCreateRequest(
    val name: String,
    val habits: String? = null,
    val location: String? = null,
)
