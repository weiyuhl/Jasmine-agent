package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAgentsUseCase @Inject constructor(private val repository: AgentRepository) {

  public operator fun invoke(): Flow<List<String>> = repository.agents
}
