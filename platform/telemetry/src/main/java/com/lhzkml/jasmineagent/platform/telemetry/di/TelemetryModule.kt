package com.lhzkml.jasmineagent.platform.telemetry.di

import com.lhzkml.jasmineagent.platform.telemetry.LogcatTelemetrySink
import com.lhzkml.jasmineagent.platform.telemetry.TelemetrySink
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TelemetryModule {
  @Binds @Singleton abstract fun bindTelemetrySink(sink: LogcatTelemetrySink): TelemetrySink
}
