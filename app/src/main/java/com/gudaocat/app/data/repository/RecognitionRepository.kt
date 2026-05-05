package com.gudaocat.app.data.repository

import android.content.Context
import android.net.Uri
import com.gudaocat.app.data.api.ApiService
import com.gudaocat.app.data.model.RecognitionJob
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecognitionRepository @Inject constructor(
    private val api: ApiService,
) {
    suspend fun recognize(context: Context, uri: Uri): Result<RecognitionJob> {
        return runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("无法读取图片")
            val body = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("image", "cat.jpg", body)
            val created = api.createRecognitionJob(part)

            var job = created
            repeat(120) {
                if (job.status == "completed" || job.status == "failed") return@runCatching job
                delay(1000)
                job = api.getRecognitionJob(created.id)
            }
            error("识别超时，请稍后查看结果")
        }
    }
}

