package com.lhzkml.jasmineagent.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AgentRepository {
  val agents: Flow<List<String>>

  fun getAgents(limit: Int = DEFAULT_AGENT_LIMIT): Flow<List<AgentRecord>>

  suspend fun getById(uid: Int): AgentRecord?

  suspend fun getByName(name: String): AgentRecord?

  suspend fun add(name: String)

  suspend fun updateStatus(uid: Int, status: AgentRecordStatus)

  suspend fun delete(uid: Int)

  suspend fun getActiveCount(): Int

  companion object {
    const val DEFAULT_AGENT_LIMIT = 10
  }
}

data class AgentRecord(
  val uid: Int,
  val name: String,
  val createdAt: Long,
  val updatedAt: Long,
  val status: AgentRecordStatus,
  val description: String?,
)

enum class AgentRecordStatus {
  ACTIVE,
  INACTIVE,
  ARCHIVED,
}

enum class AgentRepositoryFailure {
  DUPLICATE_NAME,
  STORAGE,
}

class AgentRepositoryException(
  val failure: AgentRepositoryFailure,
  cause: Throwable? = null,
) : IllegalStateException(failure.name, cause)
