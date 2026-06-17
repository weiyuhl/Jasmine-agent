package com.lhzkml.jasmineagent.core.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val JasmineWhite = Color(0xFFFFFFFF)
val JasmineBlack = Color(0xFF111111)
val JasmineTextMuted = Color(0xFF5F6368)
val JasmineBorder = Color(0xFFE5E7EB)
val JasmineContainer = Color(0xFFF8F9FA)
val JasmineDisabled = Color(0xFFDADCE0)
val JasmineError = Color(0xFFB3261E)

private val JasmineDarkBackground = Color(0xFF111111)
private val JasmineDarkSurface = Color(0xFF1A1A1A)
private val JasmineDarkContainer = Color(0xFF242424)
private val JasmineDarkTextMuted = Color(0xFFC4C7C5)
private val JasmineDarkBorder = Color(0xFF444746)
private val JasmineDarkError = Color(0xFFFFB4AB)
private val JasmineDarkOnError = Color(0xFF690005)

val JasmineLightColorScheme =
  lightColorScheme(
    primary = JasmineBlack,
    onPrimary = JasmineWhite,
    primaryContainer = JasmineContainer,
    onPrimaryContainer = JasmineBlack,
    secondary = JasmineBlack,
    onSecondary = JasmineWhite,
    secondaryContainer = JasmineContainer,
    onSecondaryContainer = JasmineBlack,
    background = JasmineWhite,
    onBackground = JasmineBlack,
    surface = JasmineWhite,
    onSurface = JasmineBlack,
    surfaceVariant = JasmineContainer,
    onSurfaceVariant = JasmineTextMuted,
    error = JasmineError,
    onError = JasmineWhite,
    outline = JasmineBorder,
    outlineVariant = JasmineDisabled,
    inverseSurface = JasmineBlack,
    inverseOnSurface = JasmineWhite,
  )

val JasmineDarkColorScheme =
  darkColorScheme(
    primary = JasmineWhite,
    onPrimary = JasmineBlack,
    primaryContainer = JasmineDarkContainer,
    onPrimaryContainer = JasmineWhite,
    secondary = JasmineWhite,
    onSecondary = JasmineBlack,
    secondaryContainer = JasmineDarkContainer,
    onSecondaryContainer = JasmineWhite,
    background = JasmineDarkBackground,
    onBackground = JasmineWhite,
    surface = JasmineDarkSurface,
    onSurface = JasmineWhite,
    surfaceVariant = JasmineDarkContainer,
    onSurfaceVariant = JasmineDarkTextMuted,
    error = JasmineDarkError,
    onError = JasmineDarkOnError,
    outline = JasmineDarkBorder,
    outlineVariant = JasmineDarkBorder,
    inverseSurface = JasmineWhite,
    inverseOnSurface = JasmineBlack,
  )
