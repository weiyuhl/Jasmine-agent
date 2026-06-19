package com.lhzkml.jasmineagent.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.contentPaddingHorizontal
import androidx.compose.foundation.style.contentPaddingVertical
import androidx.compose.foundation.style.fillWidth
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Provides reusable Material3 component styles for the Jasmine app.
 *
 * These styles centralize the color definitions for custom components such as [AgentNameField] and
 * [SaveAgentButton], so that the same appearance can be applied consistently without duplicating
 * color logic in each composable.
 */
object JasmineStyles {
  val agentFormContainer: Style = Style {
    contentPadding(16.dp)
    fillWidth()
  }

  val saveButtonSurface: Style = Style {
    shape(RoundedCornerShape(8.dp))
    width(96.dp)
    contentPaddingHorizontal(0.dp)
    contentPaddingVertical(0.dp)
  }

  /** Creates [TextFieldColors] suitable for the agent name text field. */
  @Composable
  fun agentNameFieldColors(): TextFieldColors {
    val colorScheme = MaterialTheme.colorScheme

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
    val colorScheme = MaterialTheme.colorScheme

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
    val colorScheme = MaterialTheme.colorScheme

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
