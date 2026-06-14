package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentResult
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentUseCase
import com.lhzkml.jasmineagent.core.domain.usecase.GetAgentsUseCase
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Error
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Loading
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AgentViewModel
@Inject
constructor(
  private val addAgentUseCase: AddAgentUseCase,
  private val getAgentsUseCase: GetAgentsUseCase,
) : ViewModel() {

  private val _uiState = MutableStateFlow<AgentUiState>(Loading)
  val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

  private val _addAgentState = MutableStateFlow<AddAgentState>(AddAgentState.Idle)
  val addAgentState: StateFlow<AddAgentState> = _addAgentState.asStateFlow()

  private val _events = Channel<AgentEvent>(Channel.BUFFERED)
  val events = _events.receiveAsFlow()

  private var loadAgentsJob: Job? = null

  init {
    loadAgents()
  }

  fun addAgent(name: String) {
    _addAgentState.value = AddAgentState.Adding

    viewModelScope.launch {
      when (val result = addAgentUseCase(name)) {
        is AddAgentResult.Success -> {
          _addAgentState.value = AddAgentState.Success
          _events.send(AgentEvent.AgentAdded(result.name))
        }
        is AddAgentResult.ValidationFailure -> {
          val error = result.error.toAddAgentError()
          _addAgentState.value = AddAgentState.Error(error)
          _events.send(AgentEvent.ShowError(error))
        }
        is AddAgentResult.RepositoryFailure -> {
          val error = AddAgentError.DatabaseError(result.message, result.cause)
          _addAgentState.value = AddAgentState.Error(error)
          _events.send(AgentEvent.ShowError(error))
        }
      }
    }
  }

  fun retryLoadAgents() {
    loadAgents()
  }

  fun resetAddAgentState() {
    _addAgentState.value = AddAgentState.Idle
  }

  private fun loadAgents() {
    loadAgentsJob?.cancel()
    loadAgentsJob = viewModelScope.launch {
      getAgentsUseCase()
        .map<List<String>, AgentUiState> { Success(data = it) }
        .catch { emit(Error(it, canRetry = true)) }
        .collect { _uiState.value = it }
    }
  }
}

sealed interface AgentUiState {
  data object Loading : AgentUiState

  data class Error(val throwable: Throwable, val canRetry: Boolean = true) : AgentUiState

  data class Success(val data: List<String>) : AgentUiState
}

sealed interface AddAgentState {
  data object Idle : AddAgentState

  data object Adding : AddAgentState

  data object Success : AddAgentState

  data class Error(val error: AddAgentError) : AddAgentState
}

sealed interface AddAgentError {
  data object EmptyName : AddAgentError

  data class NameTooLong(val actual: Int, val max: Int) : AddAgentError

  data class NameTooShort(val actual: Int, val min: Int) : AddAgentError

  data class InvalidCharacters(val invalidChars: Set<Char>) : AddAgentError

  data class DuplicateName(val name: String) : AddAgentError

  data class DatabaseError(val message: String?, val cause: Throwable) : AddAgentError
}

sealed interface AgentEvent {
  data class ShowError(val error: AddAgentError) : AgentEvent

  data class AgentAdded(val name: String) : AgentEvent
}
