package com.example.line_dev.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CosmosColorScheme = lightColorScheme(
    primary = WinterGray,
    onPrimary = White,
    secondary = IceBlue,
    onSecondary = White,
    tertiary = SilverMilk,
    background = SoftWhite,
    onBackground = WinterGray,
    surface = White,
    onSurface = WinterGray,
    surfaceVariant = SoftWhite,
    onSurfaceVariant = IceBlue,
    outline = SilverMilkLight,
)

@Composable
fun LinedevTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CosmosColorScheme,
        typography = Typography,
        content = content
    )
}