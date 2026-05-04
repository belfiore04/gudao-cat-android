package com.gudaocat.app.ui.screens.recognize

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Pets
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.gudaocat.app.data.mock.MockData
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.ui.components.CatCard
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCard
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.TextGray
import java.io.File

private enum class RecognitionStep {
    Camera,
    Analyzing,
    Matched,
    Unknown,
}

@Composable
fun RecognizeScreen(
    onCatClick: (Int) -> Unit = {},
    onCreateCatClick: () -> Unit = {},
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(RecognitionStep.Camera) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var flashEnabled by remember { mutableStateOf(false) }
    var recognizedCat by remember { mutableStateOf<Cat?>(null) }
    var confidence by remember { mutableStateOf(0f) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionAsked by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
        permissionAsked = true
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            recognizedCat = null
            step = RecognitionStep.Analyzing
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission && !permissionAsked) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(step, selectedImageUri) {
        if (step == RecognitionStep.Analyzing && selectedImageUri != null) {
            kotlinx.coroutines.delay(1200)
            recognizedCat = MockData.recognizedCat
            confidence = 0.92f
            step = RecognitionStep.Matched
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "拍照识猫",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (hasCameraPermission) {
                    "把猫咪对准扫描框，拍照后自动检测与匹配"
                } else {
                    "未开启相机权限，当前可从相册选择图片识别"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            CameraPanel(
                hasCameraPermission = hasCameraPermission,
                selectedImageUri = selectedImageUri,
                flashEnabled = flashEnabled,
                onFlashToggle = { flashEnabled = !flashEnabled },
                onImageCaptureReady = { imageCapture = it },
                onPickImage = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (hasCameraPermission) {
                ShutterButton(
                    enabled = step != RecognitionStep.Analyzing,
                    onClick = {
                        val capture = imageCapture ?: return@ShutterButton
                        capturePhoto(
                            context = context,
                            imageCapture = capture,
                            onSaved = { uri ->
                                selectedImageUri = uri
                                recognizedCat = null
                                step = RecognitionStep.Analyzing
                            },
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (step) {
                RecognitionStep.Matched -> {
                    recognizedCat?.let { cat ->
                        MatchedResult(
                            cat = cat,
                            confidence = confidence,
                            onCatClick = { onCatClick(cat.id) },
                        )
                    }
                }
                RecognitionStep.Unknown -> {
                    UnknownResult(onCreateCatClick = onCreateCatClick)
                }
                else -> Unit
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (step == RecognitionStep.Analyzing) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun CameraPanel(
    hasCameraPermission: Boolean,
    selectedImageUri: Uri?,
    flashEnabled: Boolean,
    onFlashToggle: () -> Unit,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(DarkCard)
            .border(1.dp, Orange.copy(alpha = 0.35f), RoundedCornerShape(28.dp)),
    ) {
        when {
            selectedImageUri != null -> {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "待识别图片",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            hasCameraPermission -> {
                CameraPreview(
                    flashEnabled = flashEnabled,
                    onImageCaptureReady = onImageCaptureReady,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            else -> {
                GalleryOnlyState(
                    modifier = Modifier.fillMaxSize(),
                    onPickImage = onPickImage,
                )
            }
        }

        if (hasCameraPermission) {
            IconButton(
                onClick = onFlashToggle,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .background(Color.Black.copy(alpha = 0.42f), CircleShape),
            ) {
                Icon(
                    if (flashEnabled) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                    contentDescription = "闪光灯",
                    tint = Orange,
                )
            }
        }

        if (hasCameraPermission || selectedImageUri != null) {
            ScannerFrame(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(220.dp),
            )
        }

        Card(
            onClick = onPickImage,
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.46f)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.PhotoLibrary,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "相册选择",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(
    flashEnabled: Boolean,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { previewContext ->
            PreviewView(previewContext).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageCapture = ImageCapture.Builder()
                        .setFlashMode(
                            if (flashEnabled) {
                                ImageCapture.FLASH_MODE_ON
                            } else {
                                ImageCapture.FLASH_MODE_OFF
                            }
                        )
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture,
                        )
                        onImageCaptureReady(imageCapture)
                    } catch (_: Exception) {
                        onImageCaptureReady(null)
                    }
                },
                ContextCompat.getMainExecutor(context),
            )
        },
    )
}

@Composable
private fun ScannerFrame(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scan")
    val scanProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scan_progress",
    )

    BoxWithConstraints(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Orange, RoundedCornerShape(18.dp)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = maxHeight * scanProgress)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Orange, Color.Transparent)
                    )
                ),
        )
    }
}

@Composable
private fun GalleryOnlyState(
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Rounded.PhotoLibrary,
            contentDescription = null,
            tint = Orange,
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "相机权限未开启",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "仍可从本地相册选择猫咪照片进行识别。",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Card(
            onClick = onPickImage,
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Orange),
        ) {
            Text(
                text = "从相册选择",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun ShutterButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .border(4.dp, Orange.copy(alpha = if (enabled) 1f else 0.35f), CircleShape)
            .background(Color.White)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(if (enabled) Color.White else TextGray),
        )
    }
}

@Composable
private fun MatchedResult(
    cat: Cat,
    confidence: Float,
    onCatClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "识别成功 · 置信度 ${(confidence * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            color = Orange,
        )
        Spacer(modifier = Modifier.height(12.dp))
        CatCard(
            cat = cat,
            onClick = onCatClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            onClick = onCatClick,
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Orange),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "查看完整档案",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun UnknownResult(
    onCreateCatClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardLight),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Pets, contentDescription = null, tint = Orange)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "暂未匹配到已登记猫咪",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "可以为它创建新的猫咪档案，补充名称、地点和习性记录。",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                onClick = onCreateCatClick,
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Orange),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "为它建档",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingOverlay() {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.62f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {},
            ),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardLight),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(color = Orange)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "正在上传并识别猫咪...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "请稍候，识别过程中无法重复操作",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                )
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSaved: (Uri) -> Unit,
) {
    val imageFile = File.createTempFile(
        "gudao-cat-",
        ".jpg",
        context.cacheDir,
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSaved(Uri.fromFile(imageFile))
            }

            override fun onError(exception: ImageCaptureException) = Unit
        },
    )
}
