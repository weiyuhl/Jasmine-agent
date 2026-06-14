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

import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
  tableName = "agent",
  indices = [Index(value = ["name"], unique = true), Index(value = ["created_at"])],
)
data class Agent(
  @PrimaryKey(autoGenerate = true) val uid: Int = 0,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
  @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
  @ColumnInfo(name = "status") val status: AgentStatus = AgentStatus.ACTIVE,
  @ColumnInfo(name = "description") val description: String? = null,
)

enum class AgentStatus {
  ACTIVE,
  INACTIVE,
  ARCHIVED,
}

@Dao
interface AgentDao {
  @Query("SELECT * FROM agent WHERE status = 'ACTIVE' ORDER BY created_at DESC")
  fun getActiveAgentsPagingSource(): PagingSource<Int, Agent>

  @Query("SELECT name FROM agent WHERE status = 'ACTIVE' ORDER BY name COLLATE NOCASE")
  fun getActiveAgentNames(): Flow<List<String>>

  @Query("SELECT * FROM agent ORDER BY created_at DESC LIMIT :limit")
  fun getAgents(limit: Int = 10): Flow<List<Agent>>

  @Query("SELECT * FROM agent WHERE uid = :uid") suspend fun getAgentById(uid: Int): Agent?

  @Query("SELECT * FROM agent WHERE name = :name") suspend fun getAgentByName(name: String): Agent?

  @Insert suspend fun insertAgent(item: Agent)

  @Query("UPDATE agent SET status = :status, updated_at = :updatedAt WHERE uid = :uid")
  suspend fun updateAgentStatus(
    uid: Int,
    status: AgentStatus,
    updatedAt: Long = System.currentTimeMillis(),
  )

  @Query("DELETE FROM agent WHERE uid = :uid") suspend fun deleteAgent(uid: Int)

  @Query("SELECT COUNT(*) FROM agent WHERE status = 'ACTIVE'")
  suspend fun getActiveAgentCount(): Int
}
