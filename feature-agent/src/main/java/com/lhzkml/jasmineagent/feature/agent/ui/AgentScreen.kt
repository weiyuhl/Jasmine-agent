/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.lhzkml.jasmineagent.feature.agent.ui

import com.lhzkml.jasmineagent.core.ui.MyApplicationTheme
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Error
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Loading
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Success
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lhzkml.jasmineagent.feature.agent.navigation.Main
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey

@Composable
fun AgentScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        is Loading -> LoadingContent(modifier)
        is Error -> ErrorContent(s.throwable.message, modifier)
        is Success -> AgentContent(
            items = s.data,
            onSave = viewModel::addAgent,
            onItemClick = onItemClick,
            modifier = modifier
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: "Unknown error",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
internal fun AgentContent(
    items: List<String>,
    onSave: (name: String) -> Unit,
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.safeDrawingPadding()) {
        var nameAgent by remember { mutableStateOf("Compose") }
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                value = nameAgent,
                onValueChange = { nameAgent = it }
            )

            Button(modifier = Modifier.width(96.dp), onClick = { onSave(nameAgent) }) {
                Text("Save")
            }
        }
        items.forEach { item ->
            Text(
                text = "Saved item: $item",
                modifier = Modifier.clickable { onItemClick(Main) }
            )
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        AgentContent(items = listOf("Compose", "Room", "Kotlin"), onSave = {}, onItemClick = {})
    }
}

@Preview(showBackground = true, widthDp = 340)
@Composable
private fun PortraitPreview() {
    MyApplicationTheme {
        AgentContent(items = listOf("Compose", "Room", "Kotlin"), onSave = {}, onItemClick = {})
    }
}