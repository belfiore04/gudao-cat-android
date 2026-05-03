package com.gudaocat.app.ui.screens.recognize

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.gudaocat.app.data.mock.MockData
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.ui.components.CatCard
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.OrangeLight
import com.gudaocat.app.ui.theme.Pink
import com.gudaocat.app.ui.theme.TextGray
import java.io.File

private enum class RecognitionStep {
    Idle,
    Captured,
    Analyzing,
    Result,
}

@Composable
fun RecognizeScreen(
    onCatClick: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(RecognitionStep.Idle) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var recognizedCat by remember { mutableStateOf<Cat?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri = pendingCameraUri
            recognizedCat = null
            step = RecognitionStep.Captured
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            recognizedCat = null
            step = RecognitionStep.Captured
        }
    }

    LaunchedEffect(step) {
        if (step == RecognitionStep.Analyzing) {
            kotlinx.coroutines.delay(900)
            recognizedCat = MockData.recognizedCat
            step = RecognitionStep.Result
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "识猫",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "拍照或选择图片，完成检测、分割与身份匹配",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray,
        )

        Spacer(modifier = Modifier.height(28.dp))

        selectedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "待识别图片",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp)),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ActionCard(
                title = "拍照识别",
                icon = Icons.Rounded.CameraAlt,
                onClick = {
                    val uri = createTempImageUri(context)
                    pendingCameraUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.weight(1f),
            )
            ActionCard(
                title = "相册选择",
                icon = Icons.Rounded.PhotoLibrary,
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (step == RecognitionStep.Captured) {
            Card(
                onClick = {
                    recognizedCat = null
                    step = RecognitionStep.Analyzing
                },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Orange),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "开始分析",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(20.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (step == RecognitionStep.Analyzing) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardLight),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = Orange)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "正在检测、分割并匹配猫咪档案...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        recognizedCat?.let { cat ->
            Text(
                text = "识别结果：匹配度 92%",
                style = MaterialTheme.typography.titleMedium,
                color = Orange,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            CatCard(
                cat = cat,
                onClick = { onCatClick(cat.id) },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardLight),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Orange.copy(alpha = 0.22f), Orange.copy(alpha = 0f)),
                            ),
                            shape = CircleShape,
                        ),
                )
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Orange,
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun createTempImageUri(context: Context): Uri {
    val imageFile = File.createTempFile(
        "gudao-cat-",
        ".jpg",
        context.cacheDir,
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile,
    )
}
