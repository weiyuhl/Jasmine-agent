package com.lhzkml.jasmineagent.platform.os.di

import com.lhzkml.jasmineagent.platform.os.AndroidRuntimeInfo
import com.lhzkml.jasmineagent.platform.os.DefaultAndroidRuntimeInfo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OsModule {
  @Binds
  @Singleton
  abstract fun bindAndroidRuntimeInfo(info: DefaultAndroidRuntimeInfo): AndroidRuntimeInfo
}
