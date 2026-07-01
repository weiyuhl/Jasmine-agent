package com.lhzkml.jasmineagent.core.data.di

import com.lhzkml.jasmineagent.core.data.DefaultAgentRepository
import com.lhzkml.jasmineagent.core.data.RustAgentNamePolicy
import com.lhzkml.jasmineagent.core.domain.repository.AgentRepository
import com.lhzkml.jasmineagent.core.domain.validation.AgentNamePolicy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

  @Singleton
  @Binds
  fun bindsAgentRepository(agentRepository: DefaultAgentRepository): AgentRepository

  @Singleton @Binds fun bindsAgentNamePolicy(policy: RustAgentNamePolicy): AgentNamePolicy
}
