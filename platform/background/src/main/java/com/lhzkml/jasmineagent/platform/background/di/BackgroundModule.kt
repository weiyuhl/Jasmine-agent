package com.lhzkml.jasmineagent.platform.background.di

import com.lhzkml.jasmineagent.platform.background.BackgroundTaskGateway
import com.lhzkml.jasmineagent.platform.background.ForegroundWorkInfoFactory
import com.lhzkml.jasmineagent.platform.background.WorkManagerBackgroundTaskGateway
import com.lhzkml.jasmineagent.platform.background.WorkManagerForegroundWorkInfoFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackgroundModule {
  @Binds
  @Singleton
  abstract fun bindBackgroundTaskGateway(
    gateway: WorkManagerBackgroundTaskGateway
  ): BackgroundTaskGateway

  @Binds
  @Singleton
  abstract fun bindForegroundWorkInfoFactory(
    factory: WorkManagerForegroundWorkInfoFactory
  ): ForegroundWorkInfoFactory
}
