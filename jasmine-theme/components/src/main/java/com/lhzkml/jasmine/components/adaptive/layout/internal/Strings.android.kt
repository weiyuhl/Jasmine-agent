/*
 * Copyright 2025 The Android Open Source Project
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

package com.lhzkml.jasmine.components.adaptive.layout.internal

import androidx.annotation.StringRes
import com.lhzkml.jasmine.components.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.os.ConfigurationCompat
import java.util.Locale

@JvmInline
@Immutable
internal value class Strings(@StringRes val value: Int) {
    companion object {
        inline val defaultPaneTitlePrimary
            get() = Strings(R.string.m3_adaptive_default_pane_title_primary)

        inline val defaultPaneTitleSecondary
            get() = Strings(R.string.m3_adaptive_default_pane_title_secondary)

        inline val defaultPaneTitleTertiary
            get() = Strings(R.string.m3_adaptive_default_pane_title_tertiary)

        inline val defaultPaneExpansionDragHandleContentDescription
            get() =
                Strings(R.string.m3_adaptive_default_pane_expansion_drag_handle_content_description)

        inline val defaultPaneExpansionDragHandleStateDescription
            get() =
                Strings(R.string.m3_adaptive_default_pane_expansion_drag_handle_state_description)

        inline val defaultPaneExpansionDragHandleActionDescription
            get() =
                Strings(R.string.m3_adaptive_default_pane_expansion_drag_handle_action_description)

        inline val defaultPaneExpansionProportionAnchorDescription
            get() =
                Strings(R.string.m3_adaptive_default_pane_expansion_proportion_anchor_description)

        inline val defaultPaneExpansionStartOffsetAnchorDescription
            get() =
                Strings(R.string.m3_adaptive_default_pane_expansion_start_offset_anchor_description)

        inline val defaultPaneExpansionEndOffsetAnchorDescription
            get() =
                Strings(R.string.m3_adaptive_default_pane_expansion_end_offset_anchor_description)

        val dragToResizeClickToExpandDescription: Strings
            get() = Strings(R.string.m3_adaptive_drag_to_resize_click_to_expand_description)

        val dragToResizeClickToCollapseDescription: Strings
            get() = Strings(R.string.m3_adaptive_drag_to_resize_click_to_collapse_description)

        val dragToResizeClickToPartiallyExpandDescription: Strings
            get() =
                Strings(R.string.m3_adaptive_drag_to_resize_click_to_partially_expand_description)

        val dragToResizeExpandedStateDescription: Strings
            get() = Strings(R.string.m3_adaptive_drag_to_resize_expanded_state_description)

        val dragToResizeCollapsedStateDescription: Strings
            get() = Strings(R.string.m3_adaptive_drag_to_resize_collapsed_state_description)

        val dragToResizePartiallyExpandedStateDescription: Strings
            get() =
                Strings(R.string.m3_adaptive_drag_to_resize_partially_expanded_state_description)
    }
}

@Composable
@ReadOnlyComposable
internal fun getString(string: Strings): String {
    val resources = LocalResources.current
    return resources.getString(string.value)
}

@Composable
@ReadOnlyComposable
internal fun getString(string: Strings, vararg formatArgs: Any): String {
    val raw = getString(string)
    val locale =
        ConfigurationCompat.getLocales(LocalConfiguration.current).get(0) ?: Locale.getDefault()
    return String.format(locale, raw, *formatArgs)
}

internal fun CompositionLocalConsumerModifierNode.getString(string: Strings): String {
    // Force invalidation when LocalConfiguration changes.
    currentValueOf(LocalConfiguration)
    val context = currentValueOf(LocalContext)
    val resources = context.resources
    return resources.getString(string.value)
}

internal fun CompositionLocalConsumerModifierNode.getString(
    string: Strings,
    vararg formatArgs: Any,
): String {
    val raw = getString(string)
    val configuration = currentValueOf(LocalConfiguration)
    val locale = ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
    return String.format(locale, raw, *formatArgs)
}
