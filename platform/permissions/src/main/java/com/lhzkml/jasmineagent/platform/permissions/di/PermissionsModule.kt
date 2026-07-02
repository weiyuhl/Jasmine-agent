package com.lhzkml.jasmineagent.platform.permissions.di

import com.lhzkml.jasmineagent.platform.permissions.AndroidPermissionGateway
import com.lhzkml.jasmineagent.platform.permissions.PermissionGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionsModule {
  @Binds
  @Singleton
  abstract fun bindPermissionGateway(gateway: AndroidPermissionGateway): PermissionGateway
}
