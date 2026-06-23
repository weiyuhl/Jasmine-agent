package com.lhzkml.jasmineagent.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val White = Color(0xFFFFFFFF)
private val WhiteSoft = Color(0xFFFAFAFA)
private val Ink = Color(0xFF111111)
private val InkSoft = Color(0xFF2B2B2B)
private val Neutral = Color(0xFF6F6F6F)
private val NeutralSoft = Color(0xFFE7E7E7)
private val Success = Color(0xFF287A4B)
private val Error = Color(0xFFBA1A1A)

private val AgentLightColorScheme =
  lightColorScheme(
    primary = Ink,
    onPrimary = White,
    primaryContainer = Color(0xFFF2F2F2),
    onPrimaryContainer = Ink,
    inversePrimary = InkSoft,
    secondary = InkSoft,
    onSecondary = White,
    secondaryContainer = Color(0xFFEDEDED),
    onSecondaryContainer = InkSoft,
    tertiary = Success,
    onTertiary = White,
    tertiaryContainer = Color(0xFFE7F4EC),
    onTertiaryContainer = Color(0xFF0D2A18),
    error = Error,
    onError = White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = White,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = WhiteSoft,
    onSurfaceVariant = Neutral,
    surfaceTint = Ink,
    inverseSurface = Ink,
    inverseOnSurface = White,
    outline = Color(0xFF8A8A8A),
    outlineVariant = NeutralSoft,
    scrim = Color(0xFF000000),
    surfaceDim = Color(0xFFE8E8E8),
    surfaceBright = White,
    surfaceContainerLowest = White,
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = Color(0xFFF2F2F2),
    surfaceContainerHigh = Color(0xFFECECEC),
    surfaceContainerHighest = Color(0xFFE6E6E6),
  )

private val AgentDarkColorScheme =
  darkColorScheme(
    primary = White,
    onPrimary = Ink,
    primaryContainer = Color(0xFF2A2A2A),
    onPrimaryContainer = White,
    inversePrimary = Ink,
    secondary = Color(0xFFE6E6E6),
    onSecondary = Ink,
    secondaryContainer = Color(0xFF343434),
    onSecondaryContainer = Color(0xFFE6E6E6),
    tertiary = Color(0xFF8FD7A9),
    onTertiary = Color(0xFF0D2A18),
    tertiaryContainer = Color(0xFF1F4A31),
    onTertiaryContainer = Color(0xFFE7F4EC),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF101010),
    onBackground = Color(0xFFEDEDED),
    surface = Color(0xFF101010),
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF1C1C1C),
    onSurfaceVariant = Color(0xFFB8B8B8),
    surfaceTint = White,
    inverseSurface = Color(0xFFEDEDED),
    inverseOnSurface = Ink,
    outline = Color(0xFF8F8F8F),
    outlineVariant = Color(0xFF3A3A3A),
    scrim = Color(0xFF000000),
    surfaceDim = Color(0xFF101010),
    surfaceBright = Color(0xFF363636),
    surfaceContainerLowest = Color(0xFF0B0B0B),
    surfaceContainerLow = Color(0xFF191919),
    surfaceContainer = Color(0xFF1F1F1F),
    surfaceContainerHigh = Color(0xFF292929),
    surfaceContainerHighest = Color(0xFF343434),
  )

private val AgentTypography =
  Typography(
    displayLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 64.sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
      ),
  )

private val AgentShapes =
  Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp),
  )

@Composable
fun AgentMaterialTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) AgentDarkColorScheme else AgentLightColorScheme,
    typography = AgentTypography,
    shapes = AgentShapes,
    content = content,
  )
}
