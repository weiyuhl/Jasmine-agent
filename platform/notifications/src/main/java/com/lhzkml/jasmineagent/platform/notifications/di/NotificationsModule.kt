package com.lhzkml.jasmineagent.platform.notifications.di

import com.lhzkml.jasmineagent.platform.notifications.AndroidNotificationGateway
import com.lhzkml.jasmineagent.platform.notifications.NotificationGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {
  @Binds
  @Singleton
  abstract fun bindNotificationGateway(gateway: AndroidNotificationGateway): NotificationGateway
}
