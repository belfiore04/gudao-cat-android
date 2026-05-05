package com.gudaocat.app.data.repository

import android.content.Context
import android.net.Uri
import com.gudaocat.app.data.api.ApiService
import com.gudaocat.app.data.model.Comment
import com.gudaocat.app.data.model.CommentCreateRequest
import com.gudaocat.app.data.model.Post
import com.gudaocat.app.data.model.PostCreateRequest
import com.gudaocat.app.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CommunityRepository(
    private val api: ApiService,
) {
    suspend fun listPosts(): Result<List<Post>> = runCatching { api.getPosts() }

    suspend fun createPost(content: String, catId: Int?, imageUrls: List<String>): Result<Post> {
        return runCatching {
            api.createPost(
                PostCreateRequest(
                    content = content,
                    cat_id = catId,
                    images = imageUrls.ifEmpty { null },
                )
            )
        }
    }

    suspend fun uploadImage(context: Context, uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: error("无法读取图片")
                val body = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", "post.jpg", body)
                api.uploadImage(part).url
            }
        }
    }

    suspend fun toggleLike(postId: Int): Result<Post> {
        return runCatching { api.togglePostLike(postId) }
    }

    suspend fun getPost(postId: Int): Result<Post> = runCatching { api.getPost(postId) }

    suspend fun listComments(postId: Int): Result<List<Comment>> {
        return runCatching { api.getComments(postId) }
    }

    suspend fun createComment(postId: Int, content: String): Result<Comment> {
        return runCatching { api.createComment(postId, CommentCreateRequest(content)) }
    }

    suspend fun getUser(userId: Int): Result<User> = runCatching { api.getUser(userId) }

    suspend fun getUserCats(userId: Int) = runCatching { api.getUserCats(userId) }

    suspend fun getUserPosts(userId: Int) = runCatching { api.getUserPosts(userId) }
}
