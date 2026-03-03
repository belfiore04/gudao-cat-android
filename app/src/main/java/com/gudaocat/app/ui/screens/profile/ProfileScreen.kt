package com.gudaocat.app.ui.screens.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.OrangeLight
import com.gudaocat.app.ui.theme.Pink
import com.gudaocat.app.ui.theme.TextGray
import com.gudaocat.app.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
) {
    val state by authViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 头像 + 用户信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 渐变头像占位
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Orange, OrangeLight, Pink))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.user?.username?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = state.user?.username ?: "未登录",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.user?.bio ?: "这只猫奴还没有签名~",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 统计数据
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem("0", "关注")
            StatItem("0", "粉丝")
            StatItem("0", "帖子")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 功能列表
        ProfileMenuItem(Icons.Rounded.Pets, "我创建的猫咪", onClick = {})
        ProfileMenuItem(Icons.Rounded.PostAdd, "我的帖子", onClick = {})
        ProfileMenuItem(Icons.Rounded.Favorite, "我的点赞", onClick = {})
        ProfileMenuItem(Icons.Rounded.Edit, "编辑资料", onClick = {})
        ProfileMenuItem(Icons.Rounded.Settings, "设置", onClick = {})

        Spacer(modifier = Modifier.height(16.dp))

        // 退出登录
        ProfileMenuItem(
            icon = Icons.Rounded.ExitToApp,
            title = "退出登录",
            tint = MaterialTheme.colorScheme.error,
            onClick = {
                authViewModel.logout()
                onLogout()
            },
        )
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            color = Orange,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextGray,
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    tint: Color = Orange,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
