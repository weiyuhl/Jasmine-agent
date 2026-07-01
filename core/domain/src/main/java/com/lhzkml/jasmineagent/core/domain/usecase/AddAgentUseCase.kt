package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryFailure
import com.lhzkml.jasmineagent.core.domain.validation.AgentNamePolicy
import com.lhzkml.jasmineagent.core.domain.validation.ValidationError
import com.lhzkml.jasmineagent.core.domain.validation.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/** Outcome of attempting to create a new agent. */
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

/** Validates, normalizes, and persists a new agent name. */
class AddAgentUseCase
@Inject
constructor(
  private val repository: AgentRepository,
  private val namePolicy: AgentNamePolicy,
) {

  /** Adds an agent and maps validation or storage failures into domain results. */
  public suspend operator fun invoke(name: String): AddAgentResult {
    val normalizedName = namePolicy.normalize(name)
    val validationResult = namePolicy.validate(name)

    if (validationResult is ValidationResult.Invalid) {
      return AddAgentResult.ValidationFailure(validationResult.error)
    }

    return try {
      repository.add(normalizedName)
      AddAgentResult.Success(normalizedName)
    } catch (e: CancellationException) {
      throw e
    } catch (e: AgentRepositoryException) {
      when (e.failure) {
        AgentRepositoryFailure.DUPLICATE_NAME ->
          AddAgentResult.ValidationFailure(ValidationError.AlreadyExists(normalizedName), e)
        AgentRepositoryFailure.STORAGE ->
          AddAgentResult.RepositoryFailure(AddAgentRepositoryError.STORAGE, e)
      }
    }
  }
}
