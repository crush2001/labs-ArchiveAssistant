package com.lyihub.archiveassistant.ui.theme

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkTerracotta,
    onPrimary = DarkIvory,
    secondary = DarkCoral,
    onSecondary = DarkIvory,
    tertiary = DarkMuted,
    onTertiary = DarkIvory,
    background = DarkParchment,
    onBackground = Ivory,
    surface = DarkIvory,
    onSurface = Ivory,
    surfaceVariant = DarkWarmSurfaceVariant,
    onSurfaceVariant = DarkMuted,
    outline = DarkWarmBorder,
    outlineVariant = DarkWarmSurface,
    inverseSurface = Ivory,
    inverseOnSurface = DarkText,
    error = Color(0xFFB3261E),
    onError = Ivory,
)

private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Ivory,
    secondary = Coral,
    onSecondary = Ivory,
    tertiary = Muted,
    onTertiary = Ivory,
    background = Parchment,
    onBackground = DarkText,
    surface = Ivory,
    onSurface = DarkText,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = Muted,
    outline = WarmBorder,
    outlineVariant = WarmSurface,
    inverseSurface = DarkText,
    inverseOnSurface = Ivory,
    error = Color(0xFFB3261E),
    onError = Ivory,
)

@Composable
fun ArchiveAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                androidx.compose.material3.dynamicDarkColorScheme(context)
            } else {
                androidx.compose.material3.dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

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
