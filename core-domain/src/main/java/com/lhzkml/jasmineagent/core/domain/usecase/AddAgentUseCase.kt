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

  data class RepositoryFailure(val message: String?, val cause: Throwable) : AddAgentResult
}

class AddAgentUseCase @Inject constructor(private val repository: AgentRepository) {

  public suspend operator fun invoke(name: String): AddAgentResult {
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
      AddAgentResult.RepositoryFailure(e.message, e)
    }
  }
}
