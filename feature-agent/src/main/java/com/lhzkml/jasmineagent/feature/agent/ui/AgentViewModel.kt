package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentResult
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentUseCase
import com.lhzkml.jasmineagent.core.domain.usecase.GetAgentsUseCase
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Error
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Loading
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AgentViewModel
@Inject
constructor(
  private val addAgentUseCase: AddAgentUseCase,
  private val getAgentsUseCase: GetAgentsUseCase,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val _uiState = MutableStateFlow<AgentUiState>(Loading)
  val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

  private val _addAgentState = MutableStateFlow<AddAgentState>(AddAgentState.Idle)
  val addAgentState: StateFlow<AddAgentState> = _addAgentState.asStateFlow()

  private val _agentName = MutableStateFlow(savedStateHandle[AGENT_NAME_KEY] ?: "")
  val agentName: StateFlow<String> = _agentName.asStateFlow()

  private val _events = MutableSharedFlow<AgentEvent>(extraBufferCapacity = 1)
  val events: SharedFlow<AgentEvent> = _events

  private val loadAgentsJob = AtomicReference<Job?>(null)
  private val addAgentInFlight = AtomicBoolean(false)

  init {
    loadAgents()
  }

  fun onAgentNameChanged(name: String) {
    savedStateHandle[AGENT_NAME_KEY] = name
    _agentName.update { name }
    _addAgentState.update { state ->
      if (state is AddAgentState.Error) AddAgentState.Idle else state
    }
  }

  fun addAgent() {
    if (!addAgentInFlight.compareAndSet(false, true)) {
      return
    }

    _addAgentState.update { AddAgentState.Adding }
    val name = agentName.value

    viewModelScope.launch {
      try {
        when (val result = addAgentUseCase(name)) {
          is AddAgentResult.Success -> {
            savedStateHandle[AGENT_NAME_KEY] = ""
            _agentName.update { "" }
            _addAgentState.update { AddAgentState.Idle }
            _events.emit(AgentEvent.AgentAdded(result.name))
          }
          is AddAgentResult.ValidationFailure -> {
            val error = result.error.toAddAgentError()
            _addAgentState.update { AddAgentState.Error(error) }
            _events.emit(AgentEvent.ShowError(error))
          }
          is AddAgentResult.RepositoryFailure -> {
            val error = result.error.toAddAgentError()
            _addAgentState.update { AddAgentState.Error(error) }
            _events.emit(AgentEvent.ShowError(error))
          }
        }
      } finally {
        addAgentInFlight.set(false)
      }
    }
  }

  fun retryLoadAgents() {
    loadAgents()
  }

  private fun loadAgents() {
    val nextJob =
      viewModelScope.launch(start = CoroutineStart.LAZY) {
        getAgentsUseCase()
          .map<List<String>, AgentUiState> { Success(data = it) }
          .catch {
            if (it is CancellationException) {
              throw it
            }
            emit(Error(AgentLoadError.Storage, canRetry = true))
          }
          .collect { state -> _uiState.update { state } }
      }
    loadAgentsJob.getAndSet(nextJob)?.cancel()
    nextJob.start()
  }

  private companion object {
    const val AGENT_NAME_KEY = "agent_name"
  }
}

sealed interface AgentUiState {
  data object Loading : AgentUiState

  data class Error(val error: AgentLoadError, val canRetry: Boolean = true) : AgentUiState

  data class Success(val data: List<String>) : AgentUiState
}

enum class AgentLoadError {
  Storage
}

sealed interface AddAgentState {
  data object Idle : AddAgentState

  data object Adding : AddAgentState

  data class Error(val error: AddAgentError) : AddAgentState
}

sealed interface AddAgentError {
  data object EmptyName : AddAgentError

  data class NameTooLong(val actual: Int, val max: Int) : AddAgentError

  data class NameTooShort(val actual: Int, val min: Int) : AddAgentError

  data class InvalidCharacters(val invalidChars: Set<Char>) : AddAgentError

  data class DuplicateName(val name: String) : AddAgentError

  data object Storage : AddAgentError
}

sealed interface AgentEvent {
  data class ShowError(val error: AddAgentError) : AgentEvent

  data class AgentAdded(val name: String) : AgentEvent
}
