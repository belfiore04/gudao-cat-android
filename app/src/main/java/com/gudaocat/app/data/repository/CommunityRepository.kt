package com.gudaocat.app.data.repository

import com.gudaocat.app.data.api.ApiService
import com.gudaocat.app.data.model.Comment
import com.gudaocat.app.data.model.CommentCreateRequest
import com.gudaocat.app.data.model.Post
import com.gudaocat.app.data.model.PostCreateRequest
import com.gudaocat.app.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun listPosts(): Result<List<Post>> = runCatching { api.getPosts() }

    suspend fun createPost(content: String): Result<Post> {
        return runCatching { api.createPost(PostCreateRequest(content = content)) }
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

