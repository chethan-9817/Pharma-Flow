package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MedColorScheme = lightColorScheme(
    primary = MedicalBlue,
    onPrimary = Color.White,
    primaryContainer = MedicalBlueLight,
    onPrimaryContainer = MedicalBlueDark,
    secondary = EcoEmerald,
    onSecondary = Color.White,
    secondaryContainer = EcoEmeraldLight,
    onSecondaryContainer = EcoEmeraldDark,
    tertiary = ExpiryAmber,
    onTertiary = Color.White,
    background = MedSlate,
    onBackground = MedTextPrimary,
    surface = MedWhite,
    onSurface = MedTextPrimary,
    surfaceVariant = MedSurfaceElevated,
    onSurfaceVariant = MedTextSecondary,
    outline = MedBorder
)

@Composable
fun MedLifecycleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MedColorScheme,
        typography = Typography,
        content = content
    )
}
