package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryFailure
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/** Outcome of deleting an existing agent. */
sealed interface DeleteAgentResult {
  data object Success : DeleteAgentResult

  data class RepositoryFailure(val error: DeleteAgentRepositoryError, val cause: Throwable) :
    DeleteAgentResult
}

enum class DeleteAgentRepositoryError {
  STORAGE
}

/** Deletes an agent by id and maps storage failures into domain results. */
class DeleteAgentUseCase @Inject constructor(private val repository: AgentRepository) {

  public suspend operator fun invoke(uid: Int): DeleteAgentResult =
    try {
      repository.delete(uid)
      DeleteAgentResult.Success
    } catch (e: CancellationException) {
      throw e
    } catch (e: AgentRepositoryException) {
      when (e.failure) {
        AgentRepositoryFailure.DUPLICATE_NAME,
        AgentRepositoryFailure.STORAGE ->
          DeleteAgentResult.RepositoryFailure(
            DeleteAgentRepositoryError.STORAGE,
            e,
          )
      }
    }
}
