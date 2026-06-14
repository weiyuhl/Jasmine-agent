package com.lhzkml.jasmineagent.core.data.di

import com.lhzkml.jasmineagent.core.data.AgentRepository
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentStatus
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

  override fun getAgents(limit: Int): Flow<List<Agent>> = _agents.map { names ->
    names.take(limit).mapIndexed { index, name -> Agent(uid = index + 1, name = name) }
  }

  override suspend fun getById(uid: Int): Agent? =
    _agents.value.getOrNull(uid - 1)?.let { Agent(uid = uid, name = it) }

  override suspend fun getByName(name: String): Agent? =
    _agents.value.firstOrNull { it == name }?.let { Agent(name = it) }

  override suspend fun add(name: String) {
    _agents.update { current -> listOf(name) + current }
  }

  override suspend fun updateStatus(uid: Int, status: AgentStatus) = Unit

  override suspend fun delete(uid: Int) {
    _agents.update { current -> current.filterIndexed { index, _ -> index != uid - 1 } }
  }

  override suspend fun getActiveCount(): Int = _agents.value.size
}
