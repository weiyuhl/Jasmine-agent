package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.domain.repository.AgentRecord
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecordStatus
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryFailure
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteAgentUseCaseTest {

  private lateinit var fakeRepository: FakeAgentRepository
  private lateinit var useCase: DeleteAgentUseCase

  @Before
  fun setup() {
    fakeRepository = FakeAgentRepository()
    useCase = DeleteAgentUseCase(fakeRepository)
  }

  @Test
  fun invoke_withExistingAgent_returnsSuccessAndDeletes() = runTest {
    fakeRepository.add("Disposable")
    val result = useCase(1)

    assertEquals(DeleteAgentResult.Success, result)
    assertEquals(emptyList<String>(), fakeRepository.names)
  }

  @Test
  fun invoke_withStorageFailure_returnsRepositoryFailure() = runTest {
    fakeRepository.shouldThrowOnDelete.set(true)

    val result = useCase(1)

    assertTrue(result is DeleteAgentResult.RepositoryFailure)
    assertEquals(
      DeleteAgentRepositoryError.STORAGE,
      (result as DeleteAgentResult.RepositoryFailure).error,
    )
  }

  private class FakeAgentRepository : AgentRepository {
    private val nameStore = CopyOnWriteArrayList<String>()
    val shouldThrowOnDelete = AtomicBoolean(false)
    val names: List<String>
      get() = nameStore.toList()

    override val agents: Flow<List<String>> = flowOf(nameStore.toList())

    override fun getAgents(limit: Int): Flow<List<AgentRecord>> =
      flowOf(nameStore.take(limit).mapIndexed { index, name -> agentRecord(index + 1, name) })

    override suspend fun getById(uid: Int): AgentRecord? =
      nameStore.getOrNull(uid - 1)?.let { agentRecord(uid, it) }

    override suspend fun getByName(name: String): AgentRecord? =
      nameStore.firstOrNull { it == name }?.let { agentRecord(name = it) }

    override suspend fun add(name: String) {
      nameStore.add(name)
    }

    override suspend fun updateStatus(uid: Int, status: AgentRecordStatus) = Unit

    override suspend fun delete(uid: Int) {
      if (shouldThrowOnDelete.get()) {
        throw AgentRepositoryException(AgentRepositoryFailure.STORAGE)
      }
      nameStore.removeAt(uid - 1)
    }

    override suspend fun getActiveCount(): Int = nameStore.size

    private fun agentRecord(
      uid: Int = 0,
      name: String,
    ): AgentRecord =
      AgentRecord(
        uid = uid,
        name = name,
        createdAt = 0L,
        updatedAt = 0L,
        status = AgentRecordStatus.ACTIVE,
        description = null,
      )
  }
}
