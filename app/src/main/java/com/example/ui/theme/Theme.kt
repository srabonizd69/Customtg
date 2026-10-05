package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TelegramBlue,
    onPrimary = Color.White,
    primaryContainer = TelegramDarkCard,
    onPrimaryContainer = Color.White,
    secondary = TelegramLightBlue,
    onSecondary = Color.Black,
    secondaryContainer = TelegramDarkBubbleOut,
    onSecondaryContainer = Color.White,
    tertiary = TelegramSecretGreen,
    background = TelegramDarkBg,
    onBackground = TelegramDarkTextPrimary,
    surface = TelegramDarkSurface,
    onSurface = TelegramDarkTextPrimary,
    surfaceVariant = TelegramDarkCard,
    onSurfaceVariant = TelegramDarkTextSecondary,
    outline = Color(0xFF2C3847)
)

private val LightColorScheme = lightColorScheme(
    primary = TelegramBlue,
    onPrimary = Color.White,
    primaryContainer = TelegramBlue.copy(alpha = 0.12f),
    onPrimaryContainer = TelegramBlue,
    secondary = TelegramLightBlue,
    onSecondary = Color.White,
    secondaryContainer = TelegramLightBubbleOut,
    onSecondaryContainer = TelegramLightTextPrimary,
    tertiary = TelegramSecretGreen,
    background = TelegramLightBg,
    onBackground = TelegramLightTextPrimary,
    surface = TelegramLightSurface,
    onSurface = TelegramLightTextPrimary,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = TelegramLightTextSecondary,
    outline = Color(0xFFDCDFE6)
)

@Composable
fun TeleProtoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
