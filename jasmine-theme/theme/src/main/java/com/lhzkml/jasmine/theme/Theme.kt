package com.lhzkml.jasmine.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
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
        LocalJasmineSpacing provides spacing,
        LocalJasmineSizing provides sizing,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = JasmineTypography,
            shapes = JasmineShapes,
            content = content,
        )
    }
}
