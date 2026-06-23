package com.lhzkml.jasmineagent.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lhzkml.jasmine.components.ButtonColors
import com.lhzkml.jasmine.components.ButtonDefaults
import com.lhzkml.jasmine.components.TextFieldColors
import com.lhzkml.jasmine.components.TextFieldDefaults
import com.lhzkml.jasmine.theme.JasmineTheme

/**
 * Provides reusable Jasmine component styles for the Jasmine app.
 *
 * These styles centralize the color definitions for custom components such as [AgentNameField] and
 * [SaveAgentButton], so that the same appearance can be applied consistently without duplicating
 * color logic in each composable.
 */
object JasmineStyles {
  /** Creates [TextFieldColors] suitable for the agent name text field. */
  @Composable
  fun agentNameFieldColors(): TextFieldColors {
    val colorScheme = JasmineTheme.colorScheme

    return TextFieldDefaults.colors(
      focusedTextColor = colorScheme.onSurface,
      unfocusedTextColor = colorScheme.onSurface,
      disabledTextColor = colorScheme.onSurfaceVariant,
      errorTextColor = colorScheme.onSurface,
      focusedContainerColor = colorScheme.surface,
      unfocusedContainerColor = colorScheme.surface,
      disabledContainerColor = colorScheme.surfaceVariant,
      errorContainerColor = colorScheme.surface,
      cursorColor = colorScheme.primary,
      errorCursorColor = colorScheme.error,
      focusedIndicatorColor = colorScheme.primary,
      unfocusedIndicatorColor = colorScheme.outline,
      disabledIndicatorColor = colorScheme.outlineVariant,
      errorIndicatorColor = colorScheme.error,
      focusedLabelColor = colorScheme.primary,
      unfocusedLabelColor = colorScheme.onSurfaceVariant,
      disabledLabelColor = colorScheme.onSurfaceVariant,
      errorLabelColor = colorScheme.error,
      focusedSupportingTextColor = colorScheme.onSurfaceVariant,
      unfocusedSupportingTextColor = colorScheme.onSurfaceVariant,
      disabledSupportingTextColor = colorScheme.onSurfaceVariant,
      errorSupportingTextColor = colorScheme.error,
    )
  }

  /** Creates [ButtonColors] suitable for the primary action button. */
  @Composable
  fun primaryButtonColors(enabled: Boolean): ButtonColors {
    val colorScheme = JasmineTheme.colorScheme

    return ButtonDefaults.buttonColors(
      containerColor = colorScheme.primary,
      contentColor = colorScheme.onPrimary,
      disabledContainerColor = colorScheme.primary.copy(alpha = DisabledContainerAlpha),
      disabledContentColor = colorScheme.onPrimary.copy(alpha = DisabledContentAlpha),
    )
  }

  /** Creates [ButtonColors] suitable for a secondary / text button. */
  @Composable
  fun secondaryButtonColors(): ButtonColors {
    val colorScheme = JasmineTheme.colorScheme

    return ButtonDefaults.buttonColors(
      containerColor = Color.Transparent,
      contentColor = colorScheme.primary,
      disabledContainerColor = Color.Transparent,
      disabledContentColor = colorScheme.onSurface.copy(alpha = DisabledContentAlpha),
    )
  }

  private const val DisabledContainerAlpha = 0.38f
  private const val DisabledContentAlpha = 0.74f
}
