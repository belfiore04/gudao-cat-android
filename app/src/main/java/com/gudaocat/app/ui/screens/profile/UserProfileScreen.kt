package com.gudaocat.app.ui.screens.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gudaocat.app.data.mock.MockData
import com.gudaocat.app.ui.components.CatCard
import com.gudaocat.app.ui.components.PostCard
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.OrangeLight
import com.gudaocat.app.ui.theme.Pink
import com.gudaocat.app.ui.theme.TextGray

@Composable
fun UserProfileScreen(
    userId: Int,
    onBack: () -> Unit,
    onCatClick: (Int) -> Unit = {},
    onPostClick: (Int) -> Unit = {},
) {
    val user = MockData.userById(userId)
    val cats = MockData.catsByCreator(userId)
    val posts = MockData.postsByUser(userId)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
    ) {
        item {
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
                    text = "用户主页",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Orange, OrangeLight, Pink))),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = user?.username?.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = user?.username ?: "未知用户",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user?.bio ?: "还没有填写简介。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray,
                        )
                    }
                }
            }
        }

        item {
            SectionTitle("创建的猫咪")
        }

        if (cats.isEmpty()) {
            item { EmptyHint("还没有创建猫咪档案。") }
        } else {
            items(cats) { cat ->
                CatCard(
                    cat = cat,
                    onClick = { onCatClick(cat.id) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
        }

        item {
            SectionTitle("发布的动态")
        }

        if (posts.isEmpty()) {
            item { EmptyHint("还没有发布社区动态。") }
        } else {
            items(posts) { post ->
                PostCard(
                    post = post,
                    authorName = user?.username,
                    authorAvatar = user?.avatar,
                    onClick = { onPostClick(post.id) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = TextGray,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    )
}
