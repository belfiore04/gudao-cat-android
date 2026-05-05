package com.gudaocat.app.data.repository

import android.content.Context
import android.net.Uri
import com.gudaocat.app.data.api.ApiService
import com.gudaocat.app.data.model.RecognitionJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class RecognitionRepository(
    private val api: ApiService,
) {
    suspend fun createJob(context: Context, uri: Uri): Result<RecognitionJob> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: error("无法读取图片")
                val body = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", "cat.jpg", body)
                api.createRecognitionJob(part)
            }
        }
    }

    suspend fun getJob(jobId: String): Result<RecognitionJob> = runCatching {
        api.getRecognitionJob(jobId)
    }
}
