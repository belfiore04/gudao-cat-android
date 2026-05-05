package com.gudaocat.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.data.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatListState(
    val isLoading: Boolean = false,
    val cats: List<Cat> = emptyList(),
    val selectedCat: Cat? = null,
    val error: String? = null,
    val savedCat: Cat? = null,
)

@HiltViewModel
class CatViewModel @Inject constructor(
    private val repository: CatRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CatListState())
    val state: StateFlow<CatListState> = _state.asStateFlow()

    fun loadCats() {
        viewModelScope.launch {
            Log.d("GudaoCat", "CatViewModel.loadCats")
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.listCats()
                .onSuccess { cats ->
                    Log.d("GudaoCat", "CatViewModel.loadCats success count=${cats.size}")
                    _state.value = _state.value.copy(isLoading = false, cats = cats)
                }
                .onFailure { error ->
                    Log.e("GudaoCat", "CatViewModel.loadCats failed", error)
                    _state.value = _state.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun loadCat(catId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, selectedCat = null)
            repository.getCat(catId)
                .onSuccess { cat -> _state.value = _state.value.copy(isLoading = false, selectedCat = cat) }
                .onFailure { error -> _state.value = _state.value.copy(isLoading = false, error = error.message) }
        }
    }

    fun createCat(name: String, location: String, habits: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, savedCat = null)
            repository.createCat(name, location, habits)
                .onSuccess { cat ->
                    _state.value = _state.value.copy(isLoading = false, savedCat = cat)
                    onSaved()
                }
                .onFailure { error -> _state.value = _state.value.copy(isLoading = false, error = error.message) }
        }
    }
}
