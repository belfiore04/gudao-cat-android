package com.gudaocat.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int,
    val user_id: Int,
    val content: String,
    val images: List<String>? = null,
    val video: String? = null,
    val like_count: Int = 0,
    val created_at: String? = null,
)
