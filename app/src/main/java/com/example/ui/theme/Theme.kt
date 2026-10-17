package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Cyan500,
    onPrimary = Navy900,
    primaryContainer = Navy700,
    onPrimaryContainer = Cyan300,
    secondary = Cyan400,
    onSecondary = Navy900,
    secondaryContainer = Navy600,
    onSecondaryContainer = PureWhite,
    tertiary = Cyan300,
    onTertiary = Navy900,
    background = Navy900,
    onBackground = TextPrimaryDark,
    surface = Navy800,
    onSurface = TextPrimaryDark,
    surfaceVariant = Navy700,
    onSurfaceVariant = TextSecondaryDark,
    outline = Navy600
)

private val LightColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = PureWhite,
    primaryContainer = Cyan100,
    onPrimaryContainer = Navy800,
    secondary = Cyan500,
    onSecondary = PureWhite,
    secondaryContainer = Cyan100,
    onSecondaryContainer = Navy900,
    tertiary = Cyan400,
    onTertiary = Navy900,
    background = OffWhite,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceBorderLight
)

@Composable
fun SmartRefProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryOverride: Color? = null,
    secondaryOverride: Color? = null,
    content: @Composable () -> Unit
) {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = baseScheme.copy(
        primary = primaryOverride ?: baseScheme.primary,
        secondary = secondaryOverride ?: baseScheme.secondary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
