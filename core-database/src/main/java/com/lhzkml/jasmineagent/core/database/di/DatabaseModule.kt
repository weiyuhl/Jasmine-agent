/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhzkml.jasmineagent.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import com.lhzkml.jasmineagent.core.database.AppDatabase
import com.lhzkml.jasmineagent.core.database.AgentDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAgentDao(appDatabase: AppDatabase): AgentDao {
        return appDatabase.agentDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        val passphrase = generatePassphrase(appContext)
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Agent"
        )
            .openHelperFactory(factory)
            .build()
    }

    private fun generatePassphrase(context: Context): ByteArray {
        // In production, derive the passphrase from a securely stored key
        // (e.g., Android Keystore via EncryptedSharedPreferences).
        // This implementation uses a combination of application package name
        // and a hardcoded seed as the passphrase material.
        val seed = "jasmine-agent-db-secret"
        return (context.packageName + seed).toByteArray(Charsets.UTF_8)
    }
}