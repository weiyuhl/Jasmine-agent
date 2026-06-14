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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentResult
import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentUseCase
import com.lhzkml.jasmineagent.core.domain.usecase.GetAgentsUseCase
import com.lhzkml.jasmineagent.core.domain.validation.ValidationError
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
          _events.send(AgentEvent.ShowError(error.message))
        }
        is AddAgentResult.RepositoryFailure -> {
          val error = AddAgentError.DatabaseError(result.message, result.cause)
          _addAgentState.value = AddAgentState.Error(error)
          _events.send(AgentEvent.ShowError(error.message))
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

  private fun ValidationError.toAddAgentError(): AddAgentError {
    return when (this) {
      is ValidationError.EmptyInput -> AddAgentError.EmptyName
      is ValidationError.TooLong -> AddAgentError.NameTooLong(actual, max)
      is ValidationError.TooShort -> AddAgentError.NameTooShort(actual, min)
      is ValidationError.InvalidCharacters ->
        AddAgentError.InvalidCharacters(
          "Invalid characters: ${invalidChars.joinToString(", ") { "'$it'" }}"
        )
      is ValidationError.AlreadyExists -> AddAgentError.DuplicateName(name)
      is ValidationError.Custom -> AddAgentError.CustomError(message)
    }
  }

  private fun loadAgents() {
    loadAgentsJob?.cancel()
    loadAgentsJob =
      viewModelScope.launch {
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
  val message: String

  data object EmptyName : AddAgentError {
    override val message: String = "Agent name cannot be empty"
  }

  data class NameTooLong(val actual: Int, val max: Int) : AddAgentError {
    override val message: String = "Agent name too long ($actual characters, max $max)"
  }

  data class NameTooShort(val actual: Int, val min: Int) : AddAgentError {
    override val message: String = "Agent name too short ($actual characters, min $min)"
  }

  data class InvalidCharacters(val details: String) : AddAgentError {
    override val message: String =
      "Agent name contains invalid characters. Only letters, numbers, spaces, hyphens, underscores, and dots are allowed. $details"
  }

  data class DuplicateName(val name: String) : AddAgentError {
    override val message: String = "Agent with name '$name' already exists"
  }

  data class CustomError(override val message: String) : AddAgentError

  data class DatabaseError(override val message: String, val cause: Throwable) : AddAgentError
}

sealed interface AgentEvent {
  data class ShowError(val message: String) : AgentEvent

  data class AgentAdded(val name: String) : AgentEvent
}
