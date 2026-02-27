package com.example.wirechef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wirechef.core.navigation.WireChefNavigation
import com.example.wirechef.core.ui.theme.WireChefTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WireChefTheme {
                WireChefNavigation()
            }
        }
    }
}