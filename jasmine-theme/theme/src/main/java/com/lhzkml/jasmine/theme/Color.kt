package com.lhzkml.jasmine.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object JasmineColors {
    val White = Color(0xFFFFFFFF)
    val WhiteSoft = Color(0xFFFAFAFA)
    val Ink = Color(0xFF111111)
    val InkSoft = Color(0xFF2B2B2B)
    val Neutral = Color(0xFF6F6F6F)
    val NeutralSoft = Color(0xFFE7E7E7)
    val Success = Color(0xFF287A4B)
    val Warning = Color(0xFF9A5A00)
    val Error = Color(0xFFBA1A1A)
}

val JasmineLightColorScheme =
    lightColorScheme(
        primary = JasmineColors.Ink,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF2F2F2),
        onPrimaryContainer = JasmineColors.Ink,
        secondary = JasmineColors.InkSoft,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFEDEDED),
        onSecondaryContainer = JasmineColors.InkSoft,
        tertiary = JasmineColors.Success,
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE7F4EC),
        onTertiaryContainer = Color(0xFF0D2A18),
        error = JasmineColors.Error,
        background = JasmineColors.White,
        onBackground = JasmineColors.Ink,
        surface = JasmineColors.White,
        onSurface = JasmineColors.Ink,
        surfaceDim = Color(0xFFE8E8E8),
        surfaceBright = JasmineColors.White,
        surfaceContainerLowest = JasmineColors.White,
        surfaceContainerLow = Color(0xFFF7F7F7),
        surfaceContainer = Color(0xFFF2F2F2),
        surfaceContainerHigh = Color(0xFFECECEC),
        surfaceContainerHighest = Color(0xFFE6E6E6),
        surfaceVariant = JasmineColors.WhiteSoft,
        onSurfaceVariant = JasmineColors.Neutral,
        outline = Color(0xFF8A8A8A),
        outlineVariant = JasmineColors.NeutralSoft,
        inverseSurface = JasmineColors.Ink,
        inverseOnSurface = JasmineColors.White,
    )

val JasmineDarkColorScheme =
    darkColorScheme(
        primary = JasmineColors.White,
        onPrimary = JasmineColors.Ink,
        primaryContainer = Color(0xFF2A2A2A),
        onPrimaryContainer = JasmineColors.White,
        secondary = Color(0xFFE6E6E6),
        onSecondary = JasmineColors.Ink,
        secondaryContainer = Color(0xFF343434),
        onSecondaryContainer = Color(0xFFE6E6E6),
        tertiary = Color(0xFF8FD7A9),
        onTertiary = Color(0xFF0D2A18),
        tertiaryContainer = Color(0xFF1F4A31),
        onTertiaryContainer = Color(0xFFE7F4EC),
        error = Color(0xFFFFB4AB),
        background = Color(0xFF101010),
        onBackground = Color(0xFFEDEDED),
        surface = Color(0xFF101010),
        onSurface = Color(0xFFEDEDED),
        surfaceDim = Color(0xFF101010),
        surfaceBright = Color(0xFF363636),
        surfaceContainerLowest = Color(0xFF0B0B0B),
        surfaceContainerLow = Color(0xFF191919),
        surfaceContainer = Color(0xFF1F1F1F),
        surfaceContainerHigh = Color(0xFF292929),
        surfaceContainerHighest = Color(0xFF343434),
        surfaceVariant = Color(0xFF1C1C1C),
        onSurfaceVariant = Color(0xFFB8B8B8),
        outline = Color(0xFF8F8F8F),
        outlineVariant = Color(0xFF3A3A3A),
        inverseSurface = Color(0xFFEDEDED),
        inverseOnSurface = JasmineColors.Ink,
    )
