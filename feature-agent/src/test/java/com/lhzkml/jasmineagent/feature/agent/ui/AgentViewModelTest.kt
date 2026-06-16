package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecord
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecordStatus
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryFailure
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentUseCase
import com.lhzkml.jasmineagent.core.domain.usecase.GetAgentsUseCase
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AgentViewModelTest {

  private lateinit var testDispatcher: TestDispatcher
  private lateinit var viewModel: AgentViewModel
  private lateinit var fakeRepository: FakeAgentRepository
  private lateinit var addAgentUseCase: AddAgentUseCase
  private lateinit var getAgentsUseCase: GetAgentsUseCase

  @Before
  fun setup() {
    testDispatcher = StandardTestDispatcher()
    Dispatchers.setMain(testDispatcher)
    fakeRepository = FakeAgentRepository()
    addAgentUseCase = AddAgentUseCase(fakeRepository)
    getAgentsUseCase = GetAgentsUseCase(fakeRepository)
    viewModel = AgentViewModel(addAgentUseCase, getAgentsUseCase, SavedStateHandle())
  }

  @After
  fun tearDown() {
    viewModel.viewModelScope.cancel()
    Dispatchers.resetMain()
  }

  @Test
  fun uiState_initiallyLoading() =
    runTest(testDispatcher) {
      val localViewModel = AgentViewModel(addAgentUseCase, getAgentsUseCase, SavedStateHandle())

      try {
        val initialState = localViewModel.uiState.value
        assertTrue("Initial state should be Loading", initialState is AgentUiState.Loading)
      } finally {
        localViewModel.viewModelScope.cancel()
      }
    }

  @Test
  fun uiState_becomesSuccess_whenRepositoryEmitsData() =
    runTest(testDispatcher) {
      fakeRepository.emit(listOf("Agent1", "Agent2"))
      advanceUntilIdle()

      val state = viewModel.uiState.value
      assertTrue("State should be Success", state is AgentUiState.Success)
      assertEquals("Should have 2 agents", 2, (state as AgentUiState.Success).data.size)
    }

  @Test
  fun uiState_becomesError_whenRepositoryThrowsException() =
    runTest(testDispatcher) {
      fakeRepository.throwError(AgentRepositoryException(AgentRepositoryFailure.STORAGE))
      viewModel.retryLoadAgents()
      advanceUntilIdle()

      val state = viewModel.uiState.value
      assertTrue("State should be Error", state is AgentUiState.Error)
      assertEquals(
        "Error should be storage",
        AgentLoadError.Storage,
        (state as AgentUiState.Error).error,
      )
    }

  @Test
  fun addAgent_withValidName_succeeds() =
    runTest(testDispatcher) {
      fakeRepository.emit(emptyList())
      advanceUntilIdle()

      viewModel.onAgentNameChanged("ValidAgent")
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertEquals("Add state should return to Idle", AddAgentState.Idle, addState)
      assertEquals("Agent name should clear after success", "", viewModel.agentName.value)
      assertTrue(
        "Agent should be added to repository",
        fakeRepository.addedAgents.contains("ValidAgent"),
      )
    }

  @Test
  fun addAgent_withEmptyName_showsError() =
    runTest(testDispatcher) {
      viewModel.onAgentNameChanged("")
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
      assertTrue(
        "Error should be EmptyName",
        (addState as AddAgentState.Error).error is AddAgentError.EmptyName,
      )
    }

  @Test
  fun addAgent_withBlankName_showsError() =
    runTest(testDispatcher) {
      viewModel.onAgentNameChanged("   ")
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
    }

  @Test
  fun addAgent_withNameTooLong_showsError() =
    runTest(testDispatcher) {
      val longName = "A".repeat(101)
      viewModel.onAgentNameChanged(longName)
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
      assertTrue(
        "Error should be NameTooLong",
        (addState as AddAgentState.Error).error is AddAgentError.NameTooLong,
      )
    }

  @Test
  fun addAgent_withNameTooShort_showsError() =
    runTest(testDispatcher) {
      viewModel.onAgentNameChanged("A")
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
      assertTrue(
        "Error should be NameTooShort",
        (addState as AddAgentState.Error).error is AddAgentError.NameTooShort,
      )
    }

  @Test
  fun addAgent_withInvalidCharacters_showsError() =
    runTest(testDispatcher) {
      viewModel.onAgentNameChanged("Agent@#\$%")
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
      assertTrue(
        "Error should be InvalidCharacters",
        (addState as AddAgentState.Error).error is AddAgentError.InvalidCharacters,
      )
    }

  @Test
  fun addAgent_withValidSpecialCharacters_succeeds() =
    runTest(testDispatcher) {
      fakeRepository.emit(emptyList())
      viewModel.onAgentNameChanged("Valid-Agent_123.0")
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertEquals("Add state should return to Idle", AddAgentState.Idle, addState)
    }

  @Test
  fun addAgent_withRepositoryError_showsStorageError() =
    runTest(testDispatcher) {
      fakeRepository.setShouldThrowOnAdd(true)
      fakeRepository.emit(emptyList())
      advanceUntilIdle()

      viewModel.onAgentNameChanged("ValidAgent")
      viewModel.addAgent()
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
      assertTrue(
        "Error should be Storage",
        (addState as AddAgentState.Error).error is AddAgentError.Storage,
      )
    }

  @Test
  fun onAgentNameChanged_clearsPreviousValidationError() =
    runTest(testDispatcher) {
      viewModel.onAgentNameChanged("")
      viewModel.addAgent()
      advanceUntilIdle()
      assertTrue(
        "State should be Error before reset",
        viewModel.addAgentState.value is AddAgentState.Error,
      )

      viewModel.onAgentNameChanged("ValidAgent")

      assertEquals(
        "State should be Idle after input changes",
        AddAgentState.Idle,
        viewModel.addAgentState.value,
      )
    }

  @Test
  fun agentName_restoresFromSavedStateHandle() =
    runTest(testDispatcher) {
      val savedStateHandle = SavedStateHandle(mapOf("agent_name" to "Restored"))
      val localViewModel = AgentViewModel(addAgentUseCase, getAgentsUseCase, savedStateHandle)

      try {
        assertEquals("Restored", localViewModel.agentName.value)
      } finally {
        localViewModel.viewModelScope.cancel()
      }
    }

  @Test
  fun eventsChannel_emitsShowError_onValidationFailure() =
    runTest(testDispatcher) {
      val event = async { viewModel.events.first() }

      viewModel.onAgentNameChanged("")
      viewModel.addAgent()
      advanceUntilIdle()

      assertTrue("Should emit ShowError event", event.await() is AgentEvent.ShowError)
    }

  @Test
  fun eventsChannel_emitsAgentAdded_onSuccess() =
    runTest(testDispatcher) {
      fakeRepository.emit(emptyList())
      advanceUntilIdle()

      val event = async { viewModel.events.first() }

      viewModel.onAgentNameChanged("NewAgent")
      viewModel.addAgent()
      advanceUntilIdle()

      val agentAdded = event.await()
      assertTrue("Should emit AgentAdded event", agentAdded is AgentEvent.AgentAdded)
      assertEquals(
        "Event should contain agent name",
        "NewAgent",
        (agentAdded as AgentEvent.AgentAdded).name,
      )
    }

  @Test
  fun addAgent_whileAlreadyAdding_ignoresDuplicateSubmission() =
    runTest(testDispatcher) {
      fakeRepository.emit(emptyList())
      fakeRepository.setAddDelayMillis(1_000)
      viewModel.onAgentNameChanged("NewAgent")

      viewModel.addAgent()
      viewModel.addAgent()
      advanceUntilIdle()

      assertEquals("Repository add should run once", 1, fakeRepository.addCallCount)
      assertEquals(listOf("NewAgent"), fakeRepository.addedAgents)
    }

  private class FakeAgentRepository : AgentRepository {
    private val addedAgentStore = CopyOnWriteArrayList<String>()
    private val addCallCounter = AtomicInteger(0)
    val addedAgents: List<String>
      get() = addedAgentStore.toList()

    val addCallCount: Int
      get() = addCallCounter.get()

    private val shouldThrowOnAdd = AtomicBoolean(false)
    private val addDelayMillis = AtomicLong(0L)
    private val _agents = MutableStateFlow<List<String>>(emptyList())
    private val errorToThrow = AtomicReference<AgentRepositoryException?>(null)

    fun emit(agents: List<String>) {
      errorToThrow.set(null)
      _agents.update { agents }
    }

    fun throwError(exception: AgentRepositoryException) {
      errorToThrow.set(exception)
    }

    fun setShouldThrowOnAdd(shouldThrow: Boolean) {
      shouldThrowOnAdd.set(shouldThrow)
    }

    fun setAddDelayMillis(delayMillis: Long) {
      addDelayMillis.set(delayMillis)
    }

    override val agents: Flow<List<String>> = flow {
      errorToThrow.get()?.let { throw it }
      _agents.collect { emit(it) }
    }

    override fun getAgents(limit: Int): Flow<List<AgentRecord>> = flow {
      errorToThrow.get()?.let { throw it }
      _agents.collect { names ->
        emit(names.take(limit).mapIndexed { index, name -> agentRecord(index + 1, name) })
      }
    }

    override suspend fun getById(uid: Int): AgentRecord? =
      _agents.value.getOrNull(uid - 1)?.let { agentRecord(uid, it) }

    override suspend fun getByName(name: String): AgentRecord? =
      _agents.value.firstOrNull { it == name }?.let { agentRecord(name = it) }

    override suspend fun add(name: String) {
      addCallCounter.incrementAndGet()
      val delayMillis = addDelayMillis.get()
      if (delayMillis > 0) {
        delay(delayMillis)
      }
      if (shouldThrowOnAdd.get()) {
        throw AgentRepositoryException(AgentRepositoryFailure.STORAGE)
      }
      addedAgentStore.add(name)
    }

    override suspend fun updateStatus(uid: Int, status: AgentRecordStatus) = Unit

    override suspend fun delete(uid: Int) {
      _agents.update { agents -> agents.filterIndexed { index, _ -> index != uid - 1 } }
    }

    override suspend fun getActiveCount(): Int = _agents.value.size

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
