package com.lhzkml.jasmine.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun JasmineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    spacing: JasmineSpacing = JasmineSpacing(),
    sizing: JasmineSizing = JasmineSizing(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) JasmineDarkColorScheme else JasmineLightColorScheme

    CompositionLocalProvider(
        LocalJasmineColorScheme provides colorScheme,
        LocalJasmineTypography provides DefaultJasmineTypography,
        LocalJasmineShapes provides DefaultJasmineShapes,
        LocalJasmineSpacing provides spacing,
        LocalJasmineSizing provides sizing,
    ) {
        content()
    }
}
