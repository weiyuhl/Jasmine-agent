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

package com.lhzkml.jasmineagent.data

import com.lhzkml.jasmineagent.core.data.DefaultAgentRepository
import com.lhzkml.jasmineagent.core.data.FakeAgentDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [DefaultAgentRepository]. */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultAgentRepositoryTest {

  @Test
  fun agents_newItemSaved_itemIsReturned() = runTest {
    val repository = DefaultAgentRepository(FakeAgentDao())

    repository.add("Repository")

    assertEquals(repository.agents.first().size, 1)
  }
}
