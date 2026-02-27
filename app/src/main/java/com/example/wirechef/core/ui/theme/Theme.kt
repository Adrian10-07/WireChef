package com.example.wirechef.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = YellowPrimary,
    secondary = TealSecondary,
    background = DarkBackground,
    surface = DarkBackground, // Color de las tarjetas en modo oscuro
    onPrimary = DarkBackground, // Texto negro sobre botones amarillos
    onSecondary = WhiteCard,    // Texto blanco sobre botones teal
    onBackground = WhiteCard,
    onSurface = WhiteCard
)

private val LightColorScheme = lightColorScheme(
    primary = YellowPrimary,
    secondary = TealSecondary,
    background = LightBackground,
    surface = WhiteCard, // Color de las tarjetas en modo claro
    onPrimary = DarkBackground,
    onSecondary = WhiteCard,
    onBackground = DarkBackground,
    onSurface = DarkBackground
)

@Composable
fun WireChefTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}