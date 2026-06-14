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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Agent::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
  abstract fun agentDao(): AgentDao
}

val MIGRATION_1_2 =
  object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
      db.execSQL(CREATE_AGENT_NEW_TABLE_SQL)

      db.execSQL(COPY_AGENT_ROWS_SQL)

      db.execSQL("DROP TABLE agent")

      db.execSQL("ALTER TABLE agent_new RENAME TO agent")

      db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_agent_name ON agent(name)")
      db.execSQL("CREATE INDEX IF NOT EXISTS index_agent_created_at ON agent(created_at)")
    }
  }

private val CREATE_AGENT_NEW_TABLE_SQL =
  """
  CREATE TABLE agent_new (
    uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name TEXT NOT NULL,
    created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000),
    updated_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000),
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    description TEXT
  )
  """
    .trimIndent()

private val COPY_AGENT_ROWS_SQL =
  """
  INSERT INTO agent_new (uid, name, created_at, updated_at, status)
  SELECT uid, name, strftime('%s', 'now') * 1000, strftime('%s', 'now') * 1000, 'ACTIVE'
  FROM agent
  """
    .trimIndent()
