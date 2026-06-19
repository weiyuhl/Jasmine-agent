package com.lhzkml.jasmine.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class JasmineSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

@Immutable
data class JasmineSizing(
    val iconButton: Dp = 40.dp,
    val buttonMinHeight: Dp = 44.dp,
    val toolbarHeight: Dp = 56.dp,
)

val LocalJasmineSpacing = staticCompositionLocalOf { JasmineSpacing() }
val LocalJasmineSizing = staticCompositionLocalOf { JasmineSizing() }

object JasmineTheme {
    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes

    val spacing: JasmineSpacing
        @Composable @ReadOnlyComposable get() = LocalJasmineSpacing.current

    val sizing: JasmineSizing
        @Composable @ReadOnlyComposable get() = LocalJasmineSizing.current
}
