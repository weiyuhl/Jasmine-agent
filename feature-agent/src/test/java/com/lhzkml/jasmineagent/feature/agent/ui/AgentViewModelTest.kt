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

import com.lhzkml.jasmineagent.core.data.AgentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for [AgentViewModel]. */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class AgentViewModelTest {
  @Test
  fun uiState_initiallyLoading() = runTest {
    val viewModel = AgentViewModel(FakeAgentRepository())
    assertEquals(AgentUiState.Loading, viewModel.uiState.first())
  }

  @Test
  fun uiState_onItemSaved_isDisplayed() = runTest {
    val repository = FakeAgentRepository()
    val viewModel = AgentViewModel(repository)
    viewModel.addAgent("Test Agent")
    val state = viewModel.uiState.first()
    assertTrue(state is AgentUiState.Success)
    assertEquals(listOf("Test Agent"), (state as AgentUiState.Success).data)
  }
}

private class FakeAgentRepository : AgentRepository {

  private val data = mutableListOf<String>()

  override val agents: Flow<List<String>>
    get() = flow { emit(data.toList()) }

  override suspend fun add(name: String) {
    data.add(0, name)
  }
}
