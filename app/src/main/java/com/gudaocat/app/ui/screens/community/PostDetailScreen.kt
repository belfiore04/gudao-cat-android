package com.gudaocat.app.ui.screens.community

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Place
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gudaocat.app.data.mock.MockData
import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.OrangeLight
import com.gudaocat.app.ui.theme.Pink
import com.gudaocat.app.ui.theme.TextGray
import com.gudaocat.app.ui.utils.rememberImageModel

@Composable
fun PostDetailScreen(
    postId: Int,
    onBack: () -> Unit,
    onAuthorClick: (Int) -> Unit = {},
    onCatClick: (Int) -> Unit = {},
) {
    val post = MockData.postById(postId)
    val postImageModel = rememberImageModel(post?.images?.firstOrNull())

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
                    text = "帖子详情",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        if (post == null) {
            item {
                Text(
                    text = "没有找到这条帖子。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray,
                    modifier = Modifier.padding(20.dp),
                )
            }
        } else {
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    val author = MockData.userById(post.user_id)
                    val relatedCat = post.cat_id?.let { MockData.catById(it) }
                    Card(
                        onClick = { onAuthorClick(post.user_id) },
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCardLight),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Orange, OrangeLight, Pink))),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = author?.username?.firstOrNull()?.uppercase() ?: "?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = author?.username ?: "未知用户",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = post.created_at ?: "刚刚",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextGray,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = post.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    relatedCat?.let { cat ->
                        Spacer(modifier = Modifier.height(14.dp))
                        RelatedCatTag(
                            cat = cat,
                            onClick = { onCatClick(cat.id) },
                        )
                    }

                    if (postImageModel != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AsyncImage(
                            model = postImageModel,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(22.dp)),
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.FavoriteBorder, contentDescription = "点赞", tint = Orange)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${post.like_count}", color = TextGray)
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = "评论", tint = TextGray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("评论", color = TextGray)
                    }
                }
            }
        }
    }
}

@Composable
private fun RelatedCatTag(
    cat: Cat,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardLight),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.Pets,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cat.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (cat.location != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Place,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = cat.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                        )
                    }
                }
            }
            Text(
                text = "查看档案",
                style = MaterialTheme.typography.labelMedium,
                color = Orange,
            )
        }
    }
}
