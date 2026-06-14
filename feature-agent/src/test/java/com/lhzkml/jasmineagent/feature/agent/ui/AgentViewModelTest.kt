package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.lifecycle.viewModelScope
import com.lhzkml.jasmineagent.core.data.AgentRepository
import com.lhzkml.jasmineagent.core.data.AgentRepositoryException
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentStatus
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentUseCase
import com.lhzkml.jasmineagent.core.domain.usecase.GetAgentsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
    viewModel = AgentViewModel(addAgentUseCase, getAgentsUseCase)
  }

  @After
  fun tearDown() {
    viewModel.viewModelScope.cancel()
    Dispatchers.resetMain()
  }

  @Test
  fun uiState_initiallyLoading() =
    runTest(testDispatcher) {
      val localViewModel = AgentViewModel(addAgentUseCase, getAgentsUseCase)

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
      fakeRepository.throwError(AgentRepositoryException("Database error"))
      viewModel.retryLoadAgents()
      advanceUntilIdle()

      val state = viewModel.uiState.value
      assertTrue("State should be Error", state is AgentUiState.Error)
      assertEquals(
        "Error message should match",
        "Database error",
        (state as AgentUiState.Error).throwable.message,
      )
    }

  @Test
  fun addAgent_withValidName_succeeds() =
    runTest(testDispatcher) {
      fakeRepository.emit(emptyList())
      advanceUntilIdle()

      viewModel.addAgent("ValidAgent")
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Success", addState is AddAgentState.Success)
      assertTrue(
        "Agent should be added to repository",
        fakeRepository.addedAgents.contains("ValidAgent"),
      )
    }

  @Test
  fun addAgent_withEmptyName_showsError() =
    runTest(testDispatcher) {
      viewModel.addAgent("")
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
      viewModel.addAgent("   ")
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
    }

  @Test
  fun addAgent_withNameTooLong_showsError() =
    runTest(testDispatcher) {
      val longName = "A".repeat(101)
      viewModel.addAgent(longName)
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
      viewModel.addAgent("A")
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
      viewModel.addAgent("Agent@#\$%")
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
      viewModel.addAgent("Valid-Agent_123.0")
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Success", addState is AddAgentState.Success)
    }

  @Test
  fun addAgent_withRepositoryError_showsDatabaseError() =
    runTest(testDispatcher) {
      fakeRepository.setShouldThrowOnAdd(true)
      fakeRepository.emit(emptyList())
      advanceUntilIdle()

      viewModel.addAgent("ValidAgent")
      advanceUntilIdle()

      val addState = viewModel.addAgentState.value
      assertTrue("Add state should be Error", addState is AddAgentState.Error)
      assertTrue(
        "Error should be DatabaseError",
        (addState as AddAgentState.Error).error is AddAgentError.DatabaseError,
      )
    }

  @Test
  fun resetAddAgentState_setsStateToIdle() =
    runTest(testDispatcher) {
      viewModel.addAgent("")
      advanceUntilIdle()
      assertTrue(
        "State should be Error before reset",
        viewModel.addAgentState.value is AddAgentState.Error,
      )

      viewModel.resetAddAgentState()

      assertEquals(
        "State should be Idle after reset",
        AddAgentState.Idle,
        viewModel.addAgentState.value,
      )
    }

  @Test
  fun eventsChannel_emitsShowError_onValidationFailure() =
    runTest(testDispatcher) {
      val event = async { viewModel.events.first() }

      viewModel.addAgent("")
      advanceUntilIdle()

      assertTrue("Should emit ShowError event", event.await() is AgentEvent.ShowError)
    }

  @Test
  fun eventsChannel_emitsAgentAdded_onSuccess() =
    runTest(testDispatcher) {
      fakeRepository.emit(emptyList())
      advanceUntilIdle()

      val event = async { viewModel.events.first() }

      viewModel.addAgent("NewAgent")
      advanceUntilIdle()

      val agentAdded = event.await()
      assertTrue("Should emit AgentAdded event", agentAdded is AgentEvent.AgentAdded)
      assertEquals(
        "Event should contain agent name",
        "NewAgent",
        (agentAdded as AgentEvent.AgentAdded).name,
      )
    }

  private class FakeAgentRepository : AgentRepository {
    val addedAgents = mutableListOf<String>()
    private var shouldThrowOnAdd = false
    private val _agents = MutableStateFlow<List<String>>(emptyList())
    private var errorToThrow: AgentRepositoryException? = null

    fun emit(agents: List<String>) {
      errorToThrow = null
      _agents.value = agents
    }

    fun throwError(exception: AgentRepositoryException) {
      errorToThrow = exception
    }

    fun setShouldThrowOnAdd(shouldThrow: Boolean) {
      shouldThrowOnAdd = shouldThrow
    }

    override val agents: Flow<List<String>> = flow {
      errorToThrow?.let { throw it }
      _agents.collect { emit(it) }
    }

    override fun getAgents(limit: Int): Flow<List<Agent>> = flow {
      errorToThrow?.let { throw it }
      _agents.collect { names ->
        emit(names.take(limit).mapIndexed { index, name -> Agent(uid = index + 1, name = name) })
      }
    }

    override suspend fun getById(uid: Int): Agent? =
      _agents.value.getOrNull(uid - 1)?.let { Agent(uid = uid, name = it) }

    override suspend fun getByName(name: String): Agent? =
      _agents.value.firstOrNull { it == name }?.let { Agent(name = it) }

    override suspend fun add(name: String) {
      if (shouldThrowOnAdd) {
        throw AgentRepositoryException("Repository error")
      }
      addedAgents.add(name)
    }

    override suspend fun updateStatus(uid: Int, status: AgentStatus) = Unit

    override suspend fun delete(uid: Int) {
      _agents.value = _agents.value.filterIndexed { index, _ -> index != uid - 1 }
    }

    override suspend fun getActiveCount(): Int = _agents.value.size
  }
}
