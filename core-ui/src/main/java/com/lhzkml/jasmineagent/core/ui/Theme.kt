package com.lhzkml.jasmineagent.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val JasmineShapes =
  Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp),
  )

/** App theme backed by Material 3 with only Jasmine-owned colors, type, and shapes. */
@Composable
fun JasmineTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
  val colorScheme = if (darkTheme) JasmineDarkColorScheme else JasmineLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = JasmineTypography,
    shapes = JasmineShapes,
  ) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
      content()
    }
  }
}
