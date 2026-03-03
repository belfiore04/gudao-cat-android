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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gudaocat.app.data.model.Post
import com.gudaocat.app.ui.components.PostCard
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.TextGray

@Composable
fun CommunityScreen() {
    val mockPosts = remember {
        listOf(
            Post(id = 1, user_id = 1, content = "今天在图书馆遇到了橘座大人！超级亲人，一直蹭我的腿 🐱", like_count = 42),
            Post(id = 2, user_id = 2, content = "操场的小黑炭今天终于让我摸了！激动到模糊", like_count = 128),
            Post(id = 3, user_id = 3, content = "B座的奶牛猫又在门口拦路了，不给摸不让走的那种哈哈哈", like_count = 67),
        )
    }

    Scaffold(
        containerColor = DarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: 发帖 */ },
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
                items(mockPosts) { post ->
                    PostCard(post = post)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
