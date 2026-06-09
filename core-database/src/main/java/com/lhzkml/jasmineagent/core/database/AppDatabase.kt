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

package com.lhzkml.jasmineagent.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Agent::class],
    version = 1,
    // TODO: When sensitive data is stored, enable SQLCipher encryption:
    // 1. Add dependency: implementation("net.zetetic:android-database-sqlcipher:4.6.0")
    // 2. Add import: import net.zetetic.database.sqlcipher.SupportFactory 
    // 3. In DatabaseModule, use SupportFactory with a secure passphrase:
    //    val passphrase = /* get from EncryptedSharedPreferences or KeyStore */
    //    Room.databaseBuilder(context, AppDatabase::class.java, "Agent")
    //        .openHelperFactory(SupportFactory(passphrase.toByteArray()))
    //        .build()
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
}
