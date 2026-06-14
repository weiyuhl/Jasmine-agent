package com.lhzkml.jasmineagent.core.data

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentDao
import com.lhzkml.jasmineagent.core.database.AgentStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface AgentRepository {
  val agents: Flow<List<String>>

  fun getAgents(limit: Int = DEFAULT_AGENT_LIMIT): Flow<List<Agent>>

  suspend fun getById(uid: Int): Agent?

  suspend fun getByName(name: String): Agent?

  suspend fun add(name: String)

  suspend fun updateStatus(uid: Int, status: AgentStatus)

  suspend fun delete(uid: Int)

  suspend fun getActiveCount(): Int

  companion object {
    const val DEFAULT_AGENT_LIMIT = 10
  }
}

class AgentRepositoryException(message: String, cause: Throwable? = null) :
  IllegalStateException(message, cause)

class DefaultAgentRepository @Inject constructor(private val agentDao: AgentDao) : AgentRepository {

  override val agents: Flow<List<String>> = agentDao.getActiveAgentNames()

  override fun getAgents(limit: Int): Flow<List<Agent>> = agentDao.getAgents(limit)

  override suspend fun getById(uid: Int): Agent? = agentDao.getAgentById(uid)

  override suspend fun getByName(name: String): Agent? = agentDao.getAgentByName(name)

  override suspend fun add(name: String) {
    val existingAgent = agentDao.getAgentByName(name)
    require(existingAgent == null) { "Agent with name '$name' already exists" }
    try {
      agentDao.insertAgent(Agent(name = name))
    } catch (e: SQLiteConstraintException) {
      throw IllegalArgumentException("Agent with name '$name' already exists", e)
    } catch (e: SQLiteException) {
      throw AgentRepositoryException("Failed to add agent '$name'", e)
    }
  }

  override suspend fun updateStatus(uid: Int, status: AgentStatus) {
    try {
      agentDao.updateAgentStatus(uid, status)
    } catch (e: SQLiteException) {
      throw AgentRepositoryException("Failed to update agent '$uid'", e)
    }
  }

  override suspend fun delete(uid: Int) {
    try {
      agentDao.deleteAgent(uid)
    } catch (e: SQLiteException) {
      throw AgentRepositoryException("Failed to delete agent '$uid'", e)
    }
  }

  override suspend fun getActiveCount(): Int =
    try {
      agentDao.getActiveAgentCount()
    } catch (e: SQLiteException) {
      throw AgentRepositoryException("Failed to count active agents", e)
    }
}
