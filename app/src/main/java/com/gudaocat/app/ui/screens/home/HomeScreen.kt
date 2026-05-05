package com.gudaocat.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gudaocat.app.ui.components.CatCard
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCard
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.Pink
import com.gudaocat.app.ui.theme.TextDim
import com.gudaocat.app.ui.theme.TextGray
import com.gudaocat.app.viewmodel.CatViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onCatClick: (Int) -> Unit = {},
    viewModel: CatViewModel,
) {
    val state by viewModel.state.collectAsState()
    val cats = state.cats

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        viewModel.loadCats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
    ) {
        // 顶部区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Orange.copy(alpha = 0.15f), Color.Transparent),
                    )
                )
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 8.dp),
        ) {
            Column {
                // 标题行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Pets,
                            contentDescription = null,
                            tint = Orange,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Orange)) { append("鼓捣") }
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) { append("猫呢") }
                            },
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                    IconButton(onClick = { /* TODO: 通知 */ }) {
                        Icon(Icons.Rounded.Notifications, "通知", tint = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 搜索栏
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("搜索猫咪...", color = TextDim) },
                    leadingIcon = { Icon(Icons.Rounded.Search, null, tint = TextDim) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true,
                    readOnly = true,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 猫咪列表
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = if (state.isLoading) "正在加载猫咪..." else "附近的猫咪",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            if (state.error != null) {
                item {
                    Text(
                        text = state.error ?: "加载失败",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray,
                    )
                }
            }

            if (!state.isLoading && cats.isEmpty()) {
                item {
                    Text(
                        text = "还没有猫咪档案。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray,
                    )
                }
            }

            itemsIndexed(cats) { index, cat ->
                var itemVisible by remember { mutableStateOf(false) }
                LaunchedEffect(visible) {
                    delay(index * 100L)
                    itemVisible = true
                }
                AnimatedVisibility(
                    visible = itemVisible,
                    enter = fadeIn() + slideInVertically { it / 2 },
                ) {
                    CatCard(
                        cat = cat,
                        onClick = { onCatClick(cat.id) },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
