package com.gudaocat.app.data.api

import com.gudaocat.app.data.model.LoginRequest
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.data.model.CatCreateRequest
import com.gudaocat.app.data.model.Comment
import com.gudaocat.app.data.model.CommentCreateRequest
import com.gudaocat.app.data.model.Post
import com.gudaocat.app.data.model.PostCreateRequest
import com.gudaocat.app.data.model.RecognitionJob
import com.gudaocat.app.data.model.RegisterRequest
import com.gudaocat.app.data.model.TokenResponse
import com.gudaocat.app.data.model.UploadResponse
import com.gudaocat.app.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): User

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @GET("api/auth/me")
    suspend fun getMe(): User

    @GET("api/users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): User

    @GET("api/users/{userId}/cats")
    suspend fun getUserCats(@Path("userId") userId: Int): List<Cat>

    @GET("api/users/{userId}/posts")
    suspend fun getUserPosts(@Path("userId") userId: Int): List<Post>

    @GET("api/cats/")
    suspend fun getCats(): List<Cat>

    @POST("api/cats/")
    suspend fun createCat(@Body request: CatCreateRequest): Cat

    @GET("api/cats/{catId}")
    suspend fun getCat(@Path("catId") catId: Int): Cat

    @GET("api/posts/")
    suspend fun getPosts(): List<Post>

    @POST("api/posts/")
    suspend fun createPost(@Body request: PostCreateRequest): Post

    @POST("api/posts/{postId}/like")
    suspend fun togglePostLike(@Path("postId") postId: Int): Post

    @GET("api/posts/{postId}")
    suspend fun getPost(@Path("postId") postId: Int): Post

    @GET("api/posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: Int): List<Comment>

    @Multipart
    @POST("api/uploads/images")
    suspend fun uploadImage(@Part image: MultipartBody.Part): UploadResponse

    @POST("api/posts/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: Int,
        @Body request: CommentCreateRequest,
    ): Comment

    @Multipart
    @POST("api/recognition/jobs")
    suspend fun createRecognitionJob(
        @Part image: MultipartBody.Part,
        @Part("threshold") threshold: RequestBody? = null,
    ): RecognitionJob

    @GET("api/recognition/jobs/{jobId}")
    suspend fun getRecognitionJob(@Path("jobId") jobId: String): RecognitionJob
}
