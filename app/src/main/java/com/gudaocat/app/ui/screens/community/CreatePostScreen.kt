package com.gudaocat.app.ui.screens.community

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCard
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.TextGray
import com.gudaocat.app.viewmodel.CommunityViewModel

@Composable
fun CreatePostScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CommunityViewModel,
    cats: List<Cat>,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var content by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf<Int?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "返回", tint = TextGray)
            }
            Text(
                text = "发布动态",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("记录今天遇到的猫咪...", color = TextGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Orange,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "关联猫咪",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow {
                item {
                    CatChoiceCard(
                        label = "不关联",
                        selected = selectedCatId == null,
                        onClick = { selectedCatId = null },
                    )
                }
                items(cats) { cat ->
                    CatChoiceCard(
                        label = cat.name,
                        selected = selectedCatId == cat.id,
                        onClick = { selectedCatId = cat.id },
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "图片",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (selectedImageUri == null) {
                Card(
                    onClick = {
                        imagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardLight),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null, tint = Orange)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "选择一张图片",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardLight),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "已选择图片",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                        )
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "已选择图片",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { selectedImageUri = null }) {
                                Icon(Icons.Rounded.Close, contentDescription = "移除图片", tint = TextGray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                onClick = {
                    viewModel.createPost(
                        context = context,
                        content = content,
                        catId = selectedCatId,
                        imageUri = selectedImageUri,
                        onSaved = onSaved,
                    )
                },
                enabled = content.isNotBlank() && !state.isPosting,
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (content.isNotBlank()) Orange else DarkCardLight,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (state.isPosting) "发布中..." else "发布",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (content.isNotBlank()) MaterialTheme.colorScheme.onPrimary else TextGray,
                    modifier = Modifier.padding(16.dp),
                )
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = TextGray)
            }
        }
    }
}

@Composable
private fun CatChoiceCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Orange else DarkCardLight,
        ),
        modifier = Modifier.padding(end = 10.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.Pets,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimary else Orange,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
