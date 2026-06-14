package com.lhzkml.jasmineagent.core.data.di

import com.lhzkml.jasmineagent.core.data.AgentRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class FakeAgentRepository @Inject constructor() : AgentRepository {

  private val _agents = MutableStateFlow(listOf("One", "Two", "Three"))
  override val agents: Flow<List<String>> = _agents

  override suspend fun add(name: String) {
    _agents.update { current -> listOf(name) + current }
  }
}
