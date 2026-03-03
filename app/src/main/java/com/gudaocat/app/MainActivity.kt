package com.gudaocat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gudaocat.app.ui.navigation.AppNavigation
import com.gudaocat.app.ui.theme.GudaoCatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GudaoCatTheme {
                AppNavigation()
            }
        }
    }
}
