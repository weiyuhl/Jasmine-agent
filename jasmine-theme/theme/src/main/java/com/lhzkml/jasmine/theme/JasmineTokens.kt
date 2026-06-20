package com.lhzkml.jasmine.theme

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
val LocalJasmineColorScheme = staticCompositionLocalOf { JasmineLightColorScheme }
val LocalJasmineTypography = staticCompositionLocalOf { DefaultJasmineTypography }
val LocalJasmineShapes = staticCompositionLocalOf { DefaultJasmineShapes }

object JasmineTheme {
    val colorScheme: JasmineColorScheme
        @Composable @ReadOnlyComposable get() = LocalJasmineColorScheme.current

    val typography: JasmineTypography
        @Composable @ReadOnlyComposable get() = LocalJasmineTypography.current

    val shapes: JasmineShapes
        @Composable @ReadOnlyComposable get() = LocalJasmineShapes.current

    val spacing: JasmineSpacing
        @Composable @ReadOnlyComposable get() = LocalJasmineSpacing.current

    val sizing: JasmineSizing
        @Composable @ReadOnlyComposable get() = LocalJasmineSizing.current
}
