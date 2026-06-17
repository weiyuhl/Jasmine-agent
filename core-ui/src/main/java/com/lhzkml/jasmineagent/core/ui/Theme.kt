package com.lhzkml.jasmineagent.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Lightweight app wrapper that does not install a Material3 theme. */
@Composable
fun JasmineTheme(content: @Composable () -> Unit) {
  Box(modifier = Modifier.fillMaxSize().background(JasmineWhite)) { content() }
}
