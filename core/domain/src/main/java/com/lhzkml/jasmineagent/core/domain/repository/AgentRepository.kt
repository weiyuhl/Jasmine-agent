package com.lhzkml.jasmineagent.core.domain.repository

import com.lhzkml.jasmineagent.core.model.AgentRecord
import com.lhzkml.jasmineagent.core.model.AgentRecordStatus
import kotlinx.coroutines.flow.Flow

/** Repository boundary for reading and mutating Agent records. */
interface AgentRepository {
  /** Emits the active agent names ordered for display. */
  val agents: Flow<List<String>>

  /** Emits full agent records, limited for list screens that do not need the full table. */
  fun getAgents(limit: Int = DEFAULT_AGENT_LIMIT): Flow<List<AgentRecord>>

  /** Returns a single agent by database id, or null when it does not exist. */
  suspend fun getById(uid: Int): AgentRecord?

  /** Returns a single agent by its unique name, or null when it does not exist. */
  suspend fun getByName(name: String): AgentRecord?

  /** Adds a new active agent with a unique, already-normalized name. */
  suspend fun add(name: String)

  /** Updates the lifecycle status of an existing agent. */
  suspend fun updateStatus(uid: Int, status: AgentRecordStatus)

  /** Permanently deletes an agent by database id. */
  suspend fun delete(uid: Int)

  /** Returns the number of active agents. */
  suspend fun getActiveCount(): Int

  companion object {
    const val DEFAULT_AGENT_LIMIT = 10
  }
}

enum class AgentRepositoryFailure {
  DUPLICATE_NAME,
  STORAGE,
}

class AgentRepositoryException(
  val failure: AgentRepositoryFailure,
  cause: Throwable? = null,
) : IllegalStateException(failure.name, cause)
