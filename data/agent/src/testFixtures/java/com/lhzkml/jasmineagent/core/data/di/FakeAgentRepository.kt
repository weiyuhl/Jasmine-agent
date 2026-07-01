package com.lhzkml.jasmineagent.core.data.di

import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.model.AgentRecord
import com.lhzkml.jasmineagent.core.model.AgentRecordStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Singleton
class FakeAgentRepository @Inject constructor() : AgentRepository {

  private val _agents = MutableStateFlow(listOf("One", "Two", "Three"))
  override val agents: Flow<List<String>> = _agents

  override fun getAgents(limit: Int): Flow<List<AgentRecord>> = _agents.map { names ->
    names.take(limit).mapIndexed { index, name -> agentRecord(index + 1, name) }
  }

  override suspend fun getById(uid: Int): AgentRecord? =
    _agents.value.getOrNull(uid - 1)?.let { agentRecord(uid, it) }

  override suspend fun getByName(name: String): AgentRecord? =
    _agents.value.firstOrNull { it == name }?.let { agentRecord(name = it) }

  override suspend fun add(name: String) {
    _agents.update { current -> listOf(name) + current }
  }

  override suspend fun updateStatus(uid: Int, status: AgentRecordStatus) = Unit

  override suspend fun delete(uid: Int) {
    _agents.update { current -> current.filterIndexed { index, _ -> index != uid - 1 } }
  }

  override suspend fun getActiveCount(): Int = _agents.value.size

  private fun agentRecord(
    uid: Int = 0,
    name: String,
  ): AgentRecord =
    AgentRecord(
      uid = uid,
      name = name,
      createdAt = 0L,
      updatedAt = 0L,
      status = AgentRecordStatus.ACTIVE,
      description = null,
    )
}
