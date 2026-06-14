package com.lhzkml.jasmineagent.test.app.testdi

import com.lhzkml.jasmineagent.core.data.AgentRepository
import com.lhzkml.jasmineagent.core.data.di.DataModule
import com.lhzkml.jasmineagent.core.data.di.FakeAgentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataModule::class])
interface FakeDataModule {
  @Binds fun bindRepository(fakeRepository: FakeAgentRepository): AgentRepository
}
