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

package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.data.AgentRepository
import com.lhzkml.jasmineagent.core.data.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.validation.AgentNameValidator
import com.lhzkml.jasmineagent.core.domain.validation.ValidationError
import com.lhzkml.jasmineagent.core.domain.validation.ValidationResult
import javax.inject.Inject

sealed interface AddAgentResult {
  data class Success(val name: String) : AddAgentResult

  data class ValidationFailure(val error: ValidationError, val cause: Throwable? = null) :
    AddAgentResult

  data class RepositoryFailure(val message: String, val cause: Throwable) : AddAgentResult
}

class AddAgentUseCase @Inject constructor(private val repository: AgentRepository) {

  suspend operator fun invoke(name: String): AddAgentResult {
    val validationResult = AgentNameValidator.validate(name)

    if (validationResult is ValidationResult.Invalid) {
      return AddAgentResult.ValidationFailure(validationResult.error)
    }

    return try {
      repository.add(name.trim())
      AddAgentResult.Success(name.trim())
    } catch (e: IllegalArgumentException) {
      AddAgentResult.ValidationFailure(ValidationError.AlreadyExists(name.trim()), e)
    } catch (e: AgentRepositoryException) {
      AddAgentResult.RepositoryFailure(e.message ?: "Unknown error", e)
    }
  }
}
