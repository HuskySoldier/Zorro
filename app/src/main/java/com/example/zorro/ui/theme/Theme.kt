package com.example.zorro.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = MossGreen,
    primaryVariant = MossGreen,
    secondary = StoneGray,
    background = DeepEarth,
    surface = StoneGray,
    onPrimary = LightMist,
    onBackground = LightMist,
    onSurface = LightMist
)

@Composable
fun ACZTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        content = content
    )
}