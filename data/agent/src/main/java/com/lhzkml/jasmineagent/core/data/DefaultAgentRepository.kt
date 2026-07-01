package com.lhzkml.jasmineagent.core.data

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentDao
import com.lhzkml.jasmineagent.core.database.AgentStatus
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryException
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepositoryFailure
import com.lhzkml.jasmineagent.core.model.AgentRecord
import com.lhzkml.jasmineagent.core.model.AgentRecordStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DefaultAgentRepository @Inject constructor(private val agentDao: AgentDao) : AgentRepository {

  override val agents: Flow<List<String>> =
    agentDao.getActiveAgentNames(AgentRepository.DEFAULT_AGENT_LIMIT).mapStorageFailures()

  override fun getAgents(limit: Int): Flow<List<AgentRecord>> =
    agentDao.getAgents(limit).mapAgents().mapStorageFailures()

  override suspend fun getById(uid: Int): AgentRecord? =
    try {
      agentDao.getAgentById(uid)?.toRecord()
    } catch (e: SQLiteException) {
      storageFailure(e)
    }

  override suspend fun getByName(name: String): AgentRecord? =
    try {
      agentDao.getAgentByName(name)?.toRecord()
    } catch (e: SQLiteException) {
      storageFailure(e)
    }

  override suspend fun add(name: String) {
    try {
      val existingAgent = agentDao.getAgentByName(name)
      if (existingAgent != null) {
        duplicateName()
      }
      agentDao.insertAgent(Agent(name = name))
    } catch (e: SQLiteConstraintException) {
      duplicateName(e)
    } catch (e: SQLiteException) {
      storageFailure(e)
    }
  }

  override suspend fun updateStatus(uid: Int, status: AgentRecordStatus) {
    try {
      agentDao.updateAgentStatus(uid, status.toDatabaseStatus())
    } catch (e: SQLiteException) {
      storageFailure(e)
    }
  }

  override suspend fun delete(uid: Int) {
    try {
      agentDao.deleteAgent(uid)
    } catch (e: SQLiteException) {
      storageFailure(e)
    }
  }

  override suspend fun getActiveCount(): Int =
    try {
      agentDao.getActiveAgentCount()
    } catch (e: SQLiteException) {
      storageFailure(e)
    }
}

private fun duplicateName(cause: Throwable? = null): Nothing =
  throw AgentRepositoryException(AgentRepositoryFailure.DUPLICATE_NAME, cause)

private fun storageFailure(cause: Throwable): Nothing =
  throw AgentRepositoryException(AgentRepositoryFailure.STORAGE, cause)

private fun <T> Flow<T>.mapStorageFailures(): Flow<T> = catch { cause ->
  if (cause is SQLiteException) {
    storageFailure(cause)
  }
  throw cause
}

private fun Flow<List<Agent>>.mapAgents(): Flow<List<AgentRecord>> = map { agents ->
  agents.map { it.toRecord() }
}

private fun Agent.toRecord(): AgentRecord =
  AgentRecord(
    uid = uid,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    status = status.toRecordStatus(),
    description = description,
  )

private fun AgentStatus.toRecordStatus(): AgentRecordStatus =
  when (this) {
    AgentStatus.ACTIVE -> AgentRecordStatus.ACTIVE
    AgentStatus.INACTIVE -> AgentRecordStatus.INACTIVE
    AgentStatus.ARCHIVED -> AgentRecordStatus.ARCHIVED
  }

private fun AgentRecordStatus.toDatabaseStatus(): AgentStatus =
  when (this) {
    AgentRecordStatus.ACTIVE -> AgentStatus.ACTIVE
    AgentRecordStatus.INACTIVE -> AgentStatus.INACTIVE
    AgentRecordStatus.ARCHIVED -> AgentStatus.ARCHIVED
  }
