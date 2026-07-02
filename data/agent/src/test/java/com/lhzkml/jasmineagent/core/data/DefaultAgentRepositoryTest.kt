package com.lhzkml.jasmineagent.core.data

import android.database.sqlite.SQLiteException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryFailure
import com.lhzkml.jasmineagent.core.model.AgentRecordStatus
import javax.inject.Provider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
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
      val repository = repository()

      repository.add("Repository")

      assertEquals(listOf("Repository"), repository.agents.first())
    }

  @Test
  fun getAgents_returnsLimitedAgentModels() =
    runTest(testDispatcher) {
      val repository = repository()

      repository.add("First")
      repository.add("Second")

      assertEquals(listOf("Second"), repository.getAgents(limit = 1).first().map { it.name })
    }

  @Test
  fun delete_removesAgentFromActiveNames() =
    runTest(testDispatcher) {
      val repository = repository()

      repository.add("Disposable")
      val agent = repository.getByName("Disposable")
      repository.delete(requireNotNull(agent).uid)

      assertEquals(emptyList<String>(), repository.agents.first())
    }

  @Test
  fun updateStatus_removesInactiveAgentFromActiveNames() =
    runTest(testDispatcher) {
      val repository = repository()

      repository.add("Inactive")
      val agent = repository.getByName("Inactive")
      repository.updateStatus(requireNotNull(agent).uid, AgentRecordStatus.INACTIVE)

      assertEquals(emptyList<String>(), repository.agents.first())
      assertEquals(0, repository.getActiveCount())
    }

  @Test
  fun add_whenDuplicateCheckFails_mapsStorageFailure() =
    runTest(testDispatcher) {
      val dao = FakeAgentDao()
      val repository = repository(dao)
      dao.throwGetByNameError(SQLiteException("query failed"))

      val exception = expectRepositoryException { repository.add("Broken") }

      assertEquals(AgentRepositoryFailure.STORAGE, exception.failure)
    }

  @Test
  fun agents_whenDaoFlowFails_mapsStorageFailure() =
    runTest(testDispatcher) {
      val dao = FakeAgentDao()
      val repository = repository(dao)
      dao.throwActiveNamesError(SQLiteException("flow failed"))

      val exception = expectRepositoryException { repository.agents.first() }

      assertEquals(AgentRepositoryFailure.STORAGE, exception.failure)
    }

  @Test
  fun agents_whenDaoProviderFails_mapsStorageFailure() =
    runTest(testDispatcher) {
      val repository =
        DefaultAgentRepository(Provider { throw UnsatisfiedLinkError("sqlcipher failed to load") })

      val exception = expectRepositoryException { repository.agents.first() }

      assertEquals(AgentRepositoryFailure.STORAGE, exception.failure)
    }

  @Test
  fun agents_whenDaoProviderIsCancelled_rethrowsCancellation() =
    runTest(testDispatcher) {
      val repository = DefaultAgentRepository(Provider { throw CancellationException("cancelled") })

      try {
        repository.agents.first()
        fail("Expected CancellationException")
      } catch (_: CancellationException) {
        // Expected cancellation should keep structured concurrency semantics.
      }
    }

  private suspend fun expectRepositoryException(
    block: suspend () -> Unit
  ): AgentRepositoryException =
    try {
      block()
      fail("Expected AgentRepositoryException")
      error("unreachable")
    } catch (e: AgentRepositoryException) {
      e
    }

  private fun repository(dao: FakeAgentDao = FakeAgentDao()): DefaultAgentRepository =
    DefaultAgentRepository(Provider { dao })
}
