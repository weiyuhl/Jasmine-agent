package com.lhzkml.jasmineagent.platform.files.di

import com.lhzkml.jasmineagent.platform.files.AndroidAppFileGateway
import com.lhzkml.jasmineagent.platform.files.AppFileGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FilesModule {
  @Binds @Singleton abstract fun bindAppFileGateway(gateway: AndroidAppFileGateway): AppFileGateway
}
