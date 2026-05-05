package com.gudaocat.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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

@Serializable
data class PostCreateRequest(
    val content: String,
    val images: List<String>? = null,
    val video: String? = null,
)

@Serializable
data class Comment(
    val id: Int,
    val post_id: Int,
    val user_id: Int,
    val content: String,
    val created_at: String? = null,
)

@Serializable
data class CommentCreateRequest(
    val content: String,
)

@Serializable
data class RecognitionMatch(
    val cat_id: Int,
    val cat_name: String,
    val distance: Float,
    val photo_url: String? = null,
    val cat: Cat? = null,
)

@Serializable
data class RecognitionResult(
    val detected: Boolean,
    val accepted: Boolean = false,
    val decision: String,
    val top1: RecognitionMatch? = null,
    val matches: List<RecognitionMatch> = emptyList(),
    val threshold: Float,
)

@Serializable
data class RecognitionJob(
    val id: String,
    val kind: String,
    val status: String,
    val filename: String? = null,
    val user_id: Int? = null,
    val progress: Map<String, JsonElement>? = null,
    val result: RecognitionResult? = null,
    val error: String? = null,
)
