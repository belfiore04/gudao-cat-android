package com.gudaocat.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.data.model.RecognitionJob
import com.gudaocat.app.data.repository.RecognitionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecognitionState(
    val isLoading: Boolean = false,
    val job: RecognitionJob? = null,
    val matchedCat: Cat? = null,
    val confidence: Float = 0f,
    val unknown: Boolean = false,
    val error: String? = null,
)

class RecognitionViewModel(
    private val repository: RecognitionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(RecognitionState())
    val state: StateFlow<RecognitionState> = _state.asStateFlow()
    private var pollingJob: Job? = null

    fun recognize(context: Context, uri: Uri) {
        viewModelScope.launch {
            _state.value = RecognitionState(isLoading = true)
            repository.createJob(context, uri)
                .onSuccess { job ->
                    _state.value = RecognitionState(isLoading = true, job = job)
                    pollJob(job.id)
                }
                .onFailure { error ->
                    _state.value = RecognitionState(isLoading = false, error = error.message)
                }
        }
    }

    private fun pollJob(jobId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            repeat(120) {
                delay(1000)
                repository.getJob(jobId)
                    .onSuccess { job ->
                        if (job.status == "completed" || job.status == "failed") {
                            applyFinalJob(job)
                            return@launch
                        }
                        _state.value = _state.value.copy(job = job, isLoading = true)
                    }
                    .onFailure { error ->
                        _state.value = RecognitionState(isLoading = false, error = error.message)
                        return@launch
                    }
            }
            _state.value = _state.value.copy(isLoading = false, error = "识别超时，请稍后重试")
        }
    }

    private fun applyFinalJob(job: RecognitionJob) {
        val result = job.result
        val cat = result?.top1?.cat
        _state.value = RecognitionState(
            isLoading = false,
            job = job,
            matchedCat = if (result?.accepted == true) cat else null,
            confidence = result?.top1?.distance?.let { (1f - it).coerceIn(0f, 1f) } ?: 0f,
            unknown = result?.detected == true && result.accepted.not(),
            error = if (job.status == "failed") job.error else null,
        )
    }
}
