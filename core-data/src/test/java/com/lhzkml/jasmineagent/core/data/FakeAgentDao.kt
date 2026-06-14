package com.lhzkml.jasmineagent.core.data

import androidx.paging.PagingSource
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentDao
import com.lhzkml.jasmineagent.core.database.AgentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Test-only fake DAO for core-data layer tests. */
class FakeAgentDao : AgentDao {

  private val data = mutableListOf<Agent>()

  override fun getActiveAgentsPagingSource(): PagingSource<Int, Agent> {
    throw UnsupportedOperationException("PagingSource not supported in fake")
  }

  override fun getActiveAgentNames(): Flow<List<String>> = flow {
    emit(data.filter { it.status == AgentStatus.ACTIVE }.map { it.name })
  }

  override fun getAgents(limit: Int): Flow<List<Agent>> = flow { emit(data.take(limit)) }

  override suspend fun getAgentById(uid: Int): Agent? = data.find { it.uid == uid }

  override suspend fun getAgentByName(name: String): Agent? = data.find { it.name == name }

  override suspend fun insertAgent(item: Agent) {
    data.add(0, item)
  }

  override suspend fun updateAgentStatus(uid: Int, status: AgentStatus, updatedAt: Long) {
    val index = data.indexOfFirst { it.uid == uid }
    if (index != -1) {
      data[index] = data[index].copy(status = status, updatedAt = updatedAt)
    }
  }

  override suspend fun deleteAgent(uid: Int) {
    data.removeAll { it.uid == uid }
  }

  override suspend fun getActiveAgentCount(): Int = data.count { it.status == AgentStatus.ACTIVE }
}
