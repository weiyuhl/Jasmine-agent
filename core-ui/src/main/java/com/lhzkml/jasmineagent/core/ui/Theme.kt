package com.lhzkml.jasmineagent.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WhiteColorScheme =
  lightColorScheme(
    primary = JasmineBlack,
    onPrimary = JasmineWhite,
    primaryContainer = JasmineWhite,
    onPrimaryContainer = JasmineBlack,
    secondary = JasmineBlack,
    onSecondary = JasmineWhite,
    secondaryContainer = JasmineWhite,
    onSecondaryContainer = JasmineBlack,
    tertiary = JasmineBlack,
    onTertiary = JasmineWhite,
    tertiaryContainer = JasmineWhite,
    onTertiaryContainer = JasmineBlack,
    background = JasmineWhite,
    onBackground = JasmineBlack,
    surface = JasmineWhite,
    onSurface = JasmineBlack,
    surfaceVariant = JasmineContainer,
    onSurfaceVariant = JasmineTextMuted,
    outline = JasmineBorder,
    outlineVariant = JasmineBorder,
    error = JasmineError,
    onError = JasmineWhite,
    errorContainer = JasmineWhite,
    onErrorContainer = JasmineError,
    inverseSurface = JasmineBlack,
    inverseOnSurface = JasmineWhite,
    inversePrimary = JasmineWhite,
    surfaceTint = JasmineWhite,
    scrim = JasmineBlack,
  )

@Composable
fun JasmineTheme(content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = WhiteColorScheme, typography = Typography, content = content)
}
