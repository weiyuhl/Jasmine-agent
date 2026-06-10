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
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lhzkml.jasmineagent.core.database.AgentDao
import com.lhzkml.jasmineagent.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Singleton
import net.sqlcipher.database.SupportFactory

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
    return Room.databaseBuilder(appContext, AppDatabase::class.java, "Agent")
      .openHelperFactory(factory)
      .build()
  }

  @Provides
  @Singleton
  fun provideEncryptedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
    val masterKey =
      MasterKey.Builder(appContext).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    return EncryptedSharedPreferences.create(
      appContext,
      "jasmine_db_secrets",
      masterKey,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
  }

  private fun generatePassphrase(context: Context): ByteArray {
    val prefs = provideEncryptedPreferences(context)
    val saltKey = "db_passphrase_salt"
    var salt = prefs.getString(saltKey, null)

    if (salt == null) {
      val bytes = ByteArray(32)
      SecureRandom().nextBytes(bytes)
      salt = bytes.joinToString(separator = "") { "%02x".format(it) }
      prefs.edit { putString(saltKey, salt) }
    }

    val material = context.packageName + salt.toString()
    return MessageDigest.getInstance("SHA-256").digest(material.toByteArray(Charsets.UTF_8))
  }
}
