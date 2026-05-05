package com.gudaocat.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.data.model.Comment
import com.gudaocat.app.data.model.Post
import com.gudaocat.app.data.model.User
import com.gudaocat.app.data.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CommunityState(
    val isLoading: Boolean = false,
    val isPosting: Boolean = false,
    val posts: List<Post> = emptyList(),
    val selectedPost: Post? = null,
    val comments: List<Comment> = emptyList(),
    val selectedUser: User? = null,
    val userCats: List<Cat> = emptyList(),
    val userPosts: List<Post> = emptyList(),
    val error: String? = null,
)

class CommunityViewModel(
    private val repository: CommunityRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.listPosts()
                .onSuccess { posts -> _state.value = _state.value.copy(isLoading = false, posts = posts) }
                .onFailure { error -> _state.value = _state.value.copy(isLoading = false, error = error.message) }
        }
    }

    fun createPost(
        context: Context,
        content: String,
        catId: Int?,
        imageUri: Uri?,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isPosting = true, error = null)
            val imageUrls = if (imageUri != null) {
                val uploaded = repository.uploadImage(context, imageUri)
                val url = uploaded.getOrElse { error ->
                    _state.value = _state.value.copy(isPosting = false, error = error.message)
                    return@launch
                }
                listOf(url)
            } else {
                emptyList()
            }

            repository.createPost(content, catId, imageUrls)
                .onSuccess {
                    _state.value = _state.value.copy(isPosting = false)
                    loadPosts()
                    onSaved()
                }
                .onFailure { error -> _state.value = _state.value.copy(isPosting = false, error = error.message) }
        }
    }

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            repository.toggleLike(postId)
                .onSuccess { updated ->
                    _state.value = _state.value.copy(
                        posts = _state.value.posts.map { if (it.id == updated.id) updated else it },
                        selectedPost = _state.value.selectedPost?.let {
                            if (it.id == updated.id) updated else it
                        },
                    )
                }
                .onFailure { error -> _state.value = _state.value.copy(error = error.message) }
        }
    }

    fun loadPost(postId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, selectedPost = null)
            repository.getPost(postId)
                .onSuccess { post -> _state.value = _state.value.copy(isLoading = false, selectedPost = post) }
                .onFailure { error -> _state.value = _state.value.copy(isLoading = false, error = error.message) }
            loadComments(postId)
        }
    }

    fun loadComments(postId: Int) {
        viewModelScope.launch {
            repository.listComments(postId)
                .onSuccess { comments -> _state.value = _state.value.copy(comments = comments) }
                .onFailure { error -> _state.value = _state.value.copy(error = error.message) }
        }
    }

    fun createComment(postId: Int, content: String) {
        viewModelScope.launch {
            repository.createComment(postId, content)
                .onSuccess { loadComments(postId) }
                .onFailure { error -> _state.value = _state.value.copy(error = error.message) }
        }
    }

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val user = repository.getUser(userId).getOrNull()
            val cats = repository.getUserCats(userId).getOrDefault(emptyList())
            val posts = repository.getUserPosts(userId).getOrDefault(emptyList())
            _state.value = _state.value.copy(
                isLoading = false,
                selectedUser = user,
                userCats = cats,
                userPosts = posts,
                error = if (user == null) "用户不存在" else null,
            )
        }
    }
}
