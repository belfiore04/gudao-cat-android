package com.gudaocat.app.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gudaocat.app.ui.components.PostCard
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.TextGray
import com.gudaocat.app.viewmodel.CommunityViewModel

@Composable
fun CommunityScreen(
    onPostClick: (Int) -> Unit = {},
    onAuthorClick: (Int) -> Unit = {},
    onCreatePostClick: () -> Unit = {},
    viewModel: CommunityViewModel,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadPosts() }
    val posts = state.posts

    Scaffold(
        containerColor = DarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePostClick,
                containerColor = Orange,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {
                Icon(Icons.Rounded.Add, "发帖", modifier = Modifier.size(28.dp))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // 标题
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "社区",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "最新",
                    style = MaterialTheme.typography.titleMedium,
                    color = Orange,
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.error != null) {
                    item {
                        Text(
                            text = state.error ?: "加载失败",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray,
                        )
                    }
                }
                if (!state.isLoading && posts.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCardLight),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "还没有社区动态，点右下角发布第一条。",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                    }
                }
                items(posts) { post ->
                    PostCard(
                        post = post,
                        authorName = "用户 #${post.user_id}",
                        authorAvatar = null,
                        onClick = { onPostClick(post.id) },
                        onAuthorClick = onAuthorClick,
                        onLikeClick = viewModel::toggleLike,
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
