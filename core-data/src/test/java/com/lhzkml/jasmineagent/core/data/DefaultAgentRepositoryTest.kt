package com.lhzkml.jasmineagent.core.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** Unit tests for [DefaultAgentRepository]. */
@OptIn(ExperimentalCoroutinesApi::class)
class DefaultAgentRepositoryTest {

  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun agents_newItemSaved_itemIsReturned() =
    runTest(testDispatcher) {
      val repository = DefaultAgentRepository(FakeAgentDao())

      repository.add("Repository")

      assertEquals(listOf("Repository"), repository.agents.first())
    }
}
