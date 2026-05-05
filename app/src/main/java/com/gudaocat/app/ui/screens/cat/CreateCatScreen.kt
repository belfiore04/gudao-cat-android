package com.gudaocat.app.ui.screens.cat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gudaocat.app.ui.theme.DarkBg
import com.gudaocat.app.ui.theme.DarkCard
import com.gudaocat.app.ui.theme.DarkCardLight
import com.gudaocat.app.ui.theme.Orange
import com.gudaocat.app.ui.theme.TextGray
import com.gudaocat.app.viewmodel.CatViewModel

@Composable
fun CreateCatScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CatViewModel,
) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var habits by remember { mutableStateOf("") }

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
                text = "为它建档",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            Text(
                text = "补充猫咪的基础信息，后续可继续添加照片和观察记录。",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray,
            )

            Spacer(modifier = Modifier.height(20.dp))

            CatTextField(
                value = name,
                onValueChange = { name = it },
                label = "猫咪名称",
                imeAction = ImeAction.Next,
            )
            Spacer(modifier = Modifier.height(14.dp))
            CatTextField(
                value = location,
                onValueChange = { location = it },
                label = "常出没位置",
                imeAction = ImeAction.Next,
            )
            Spacer(modifier = Modifier.height(14.dp))
            CatTextField(
                value = habits,
                onValueChange = { habits = it },
                label = "习性记录",
                singleLine = false,
                imeAction = ImeAction.Done,
                modifier = Modifier.height(130.dp),
            )

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                onClick = { viewModel.createCat(name, location, habits, onSaved) },
                enabled = name.isNotBlank() && !state.isLoading,
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (name.isNotBlank()) Orange else DarkCardLight,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "创建猫咪档案",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (name.isNotBlank()) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        TextGray
                    },
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
private fun CatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    imeAction: ImeAction,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = TextGray) },
        modifier = modifier.fillMaxWidth(),
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
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
    )
}
