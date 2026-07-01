package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.model.AgentRecord
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Reads agents for presentation while hiding repository details from feature modules. */
class GetAgentsUseCase @Inject constructor(private val repository: AgentRepository) {

  /** Emits active agent names for simple display-only consumers. */
  public operator fun invoke(): Flow<List<String>> = repository.agents

  /** Emits full agent records with a caller-controlled limit for list screens. */
  public fun records(limit: Int = AgentRepository.DEFAULT_AGENT_LIMIT): Flow<List<AgentRecord>> =
    repository.getAgents(limit)
}
