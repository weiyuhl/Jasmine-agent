package com.lhzkml.jasmineagent.core.ui.theme

import androidx.compose.runtime.Composable

@Deprecated(
  message = "Use com.lhzkml.jasmineagent.core.designsystem.theme.AgentMaterialTheme.",
  replaceWith =
    ReplaceWith(
      "AgentMaterialTheme(darkTheme, content)",
      "com.lhzkml.jasmineagent.core.designsystem.theme.AgentMaterialTheme",
    ),
)
@Composable
fun AgentMaterialTheme(
  darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  com.lhzkml.jasmineagent.core.designsystem.theme.AgentMaterialTheme(
    darkTheme = darkTheme,
    content = content,
  )
}
