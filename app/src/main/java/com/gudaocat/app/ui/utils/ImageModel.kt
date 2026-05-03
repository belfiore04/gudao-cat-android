package com.gudaocat.app.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val DrawablePrefix = "drawable/"

@Composable
fun rememberImageModel(source: String?): Any? {
    if (source.isNullOrBlank()) return null

    val context = LocalContext.current
    return remember(source, context.packageName) {
        if (source.startsWith(DrawablePrefix)) {
            val resourceName = source.removePrefix(DrawablePrefix).substringBeforeLast(".")
            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
                .takeIf { it != 0 }
        } else {
            source
        }
    }
}
