/*
 * Copyright 2023 The Android Open Source Project
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

package com.lhzkml.jasmine.components.internal

import com.lhzkml.jasmine.components.LocalContentColor
import com.lhzkml.jasmine.components.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * A convenience method to provide values to both [LocalContentColor] and [LocalTextStyle] in one
 * call. This is less expensive than nesting calls to [CompositionLocalProvider].
 *
 * Text styles will be merged with the current value of [LocalTextStyle].
 */
@Composable
internal fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content,
    )
}

/**
 * A convenience method to provide values to both [LocalContentColor] and [LocalTextStyle] in one
 * call, as well as other [CompositionLocal] values. This is less expensive than nesting calls to
 * [CompositionLocalProvider].
 *
 * Text styles will be merged with the current value of [LocalTextStyle].
 */
@Composable
internal fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    vararg otherValues: ProvidedValue<*>,
    content: @Composable () -> Unit,
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        *otherValues,
        content = content,
    )
}
