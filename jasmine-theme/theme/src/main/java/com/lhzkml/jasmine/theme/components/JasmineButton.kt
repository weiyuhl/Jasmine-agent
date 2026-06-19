package com.lhzkml.jasmine.theme.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.lhzkml.jasmine.theme.JasmineTheme

@Composable
fun JasminePrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = JasmineTheme.shapes.medium,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = JasmineTheme.sizing.buttonMinHeight),
        enabled = enabled,
        shape = shape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = JasmineTheme.colorScheme.primary,
                contentColor = JasmineTheme.colorScheme.onPrimary,
            ),
        content = content,
    )
}
