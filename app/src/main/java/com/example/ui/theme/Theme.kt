package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandEmerald,
    onPrimary = Color.Black,
    primaryContainer = BrandEmeraldDark,
    onPrimaryContainer = Color.White,
    secondary = BrandAmber,
    onSecondary = Color.Black,
    secondaryContainer = BrandAmberDark,
    onSecondaryContainer = Color.White,
    tertiary = BrandCyan,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = BrandEmeraldDark,
    onPrimary = Color.White,
    primaryContainer = BrandEmeraldLight,
    onPrimaryContainer = Color.Black,
    secondary = BrandAmberDark,
    onSecondary = Color.White,
    secondaryContainer = BrandAmberLight,
    onSecondaryContainer = Color.Black,
    tertiary = BrandCyan,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun ScratchMartTheme(
    darkTheme: Boolean = true, // Default to dark aesthetic for rich gaming/commerce feel
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
