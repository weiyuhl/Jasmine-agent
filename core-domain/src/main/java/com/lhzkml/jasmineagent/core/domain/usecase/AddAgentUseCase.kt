package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryFailure
import com.lhzkml.jasmineagent.core.domain.validation.AgentNameValidator
import com.lhzkml.jasmineagent.core.domain.validation.ValidationError
import com.lhzkml.jasmineagent.core.domain.validation.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

sealed interface AddAgentResult {
  data class Success(val name: String) : AddAgentResult

  data class ValidationFailure(val error: ValidationError, val cause: Throwable? = null) :
    AddAgentResult

  data class RepositoryFailure(val error: AddAgentRepositoryError, val cause: Throwable) :
    AddAgentResult
}

enum class AddAgentRepositoryError {
  STORAGE
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
    } catch (e: CancellationException) {
      throw e
    } catch (e: AgentRepositoryException) {
      when (e.failure) {
        AgentRepositoryFailure.DUPLICATE_NAME ->
          AddAgentResult.ValidationFailure(ValidationError.AlreadyExists(name.trim()), e)
        AgentRepositoryFailure.STORAGE ->
          AddAgentResult.RepositoryFailure(AddAgentRepositoryError.STORAGE, e)
      }
    }
  }
}
