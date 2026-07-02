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
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DefaultAgentRepository @Inject constructor(private val agentDaoProvider: Provider<AgentDao>) :
  AgentRepository {

  private val agentDao: AgentDao
    get() = agentDaoProvider.get()

  override val agents: Flow<List<String>> =
    flow { emitAll(agentDao.getActiveAgentNames(AgentRepository.DEFAULT_AGENT_LIMIT)) }
      .mapStorageFailures()

  override fun getAgents(limit: Int): Flow<List<AgentRecord>> =
    flow { emitAll(agentDao.getAgents(limit)) }.mapAgents().mapStorageFailures()

  override suspend fun getById(uid: Int): AgentRecord? = runStorageOperation {
    agentDao.getAgentById(uid)?.toRecord()
  }

  override suspend fun getByName(name: String): AgentRecord? = runStorageOperation {
    agentDao.getAgentByName(name)?.toRecord()
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
    } catch (e: Throwable) {
      e.toStorageFailure()
    }
  }

  override suspend fun updateStatus(uid: Int, status: AgentRecordStatus) {
    runStorageOperation { agentDao.updateAgentStatus(uid, status.toDatabaseStatus()) }
  }

  override suspend fun delete(uid: Int) {
    runStorageOperation { agentDao.deleteAgent(uid) }
  }

  override suspend fun getActiveCount(): Int = runStorageOperation {
    agentDao.getActiveAgentCount()
  }
}

private fun duplicateName(cause: Throwable? = null): Nothing =
  throw AgentRepositoryException(AgentRepositoryFailure.DUPLICATE_NAME, cause)

private fun storageFailure(cause: Throwable): Nothing =
  throw AgentRepositoryException(AgentRepositoryFailure.STORAGE, cause)

private fun <T> Flow<T>.mapStorageFailures(): Flow<T> = catch { cause ->
  cause.toStorageFailure()
}

private suspend inline fun <T> runStorageOperation(crossinline block: suspend () -> T): T =
  try {
    block()
  } catch (cause: Throwable) {
    cause.toStorageFailure()
  }

private fun Throwable.toStorageFailure(): Nothing {
  if (this is CancellationException) {
    throw this
  }
  if (this is AgentRepositoryException) {
    throw this
  }
  if (isStorageInitializationFailure()) {
    storageFailure(this)
  }
  throw this
}

private fun Throwable.isStorageInitializationFailure(): Boolean =
  this is SQLiteException ||
    this is GeneralSecurityException ||
    this is IOException ||
    this is RuntimeException ||
    this is LinkageError

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
