package com.gudaocat.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gudaocat.app.data.model.User
import com.gudaocat.app.data.repository.AuthRepository
import com.gudaocat.app.data.repository.CatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
)

class AuthViewModel(
    private val repository: AuthRepository,
    private val catRepository: CatRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val loggedIn = repository.isLoggedIn()
            if (loggedIn) {
                val result = repository.getMe()
                val user = result.getOrNull()
                if (user != null) {
                    _state.value = _state.value.copy(isLoggedIn = true, user = user)
                    catRepository.refreshCats()
                } else {
                    _state.value = _state.value.copy(isLoggedIn = false)
                }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.login(username, password)
            val user = result.getOrNull()
            if (user != null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    user = user,
                    isLoggedIn = true,
                )
                catRepository.refreshCats()
            } else {
                val e = result.exceptionOrNull()
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e?.message ?: "登录失败",
                )
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.register(username, password)
            val user = result.getOrNull()
            if (user != null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    user = user,
                    isLoggedIn = true,
                )
                catRepository.refreshCats()
            } else {
                val e = result.exceptionOrNull()
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e?.message ?: "注册失败",
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _state.value = AuthState()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
