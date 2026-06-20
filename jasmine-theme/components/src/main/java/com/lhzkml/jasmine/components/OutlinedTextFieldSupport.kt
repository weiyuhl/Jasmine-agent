/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhzkml.jasmine.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

internal fun Modifier.outlineCutout(
    labelSize: () -> Size,
    alignment: Alignment.Horizontal,
    paddingValues: PaddingValues,
) =
    this.drawWithContent {
        val labelSizeValue = labelSize()
        val labelWidth = labelSizeValue.width
        if (labelWidth > 0f) {
            val innerPadding = OutlinedTextFieldInnerPadding.toPx()
            val leftPadding = paddingValues.calculateStartPadding(layoutDirection).toPx()
            val rightPadding = paddingValues.calculateEndPadding(layoutDirection).toPx()
            val labelCenter =
                alignment.align(
                    size = labelWidth.roundToInt(),
                    space = (size.width - leftPadding - rightPadding).roundToInt(),
                    layoutDirection = layoutDirection,
                ) + leftPadding + (labelWidth / 2)
            val left = (labelCenter - (labelWidth / 2) - innerPadding).coerceAtLeast(0f)
            val right = (labelCenter + (labelWidth / 2) + innerPadding).coerceAtMost(size.width)
            val labelHeight = labelSizeValue.height
            clipRect(left, -labelHeight / 2, right, labelHeight / 2, ClipOp.Difference) {
                this@drawWithContent.drawContent()
            }
        } else {
            this@drawWithContent.drawContent()
        }
    }

private val OutlinedTextFieldInnerPadding = 4.dp
