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

package com.lhzkml.jasmineagent.core.data

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface AgentRepository {
  val agents: Flow<List<String>>

  suspend fun add(name: String)
}

class AgentRepositoryException(message: String, cause: Throwable? = null) :
  IllegalStateException(message, cause)

class DefaultAgentRepository @Inject constructor(private val agentDao: AgentDao) : AgentRepository {

  override val agents: Flow<List<String>> = agentDao.getActiveAgentNames()

  override suspend fun add(name: String) {
    val existingAgent = agentDao.getAgentByName(name)
    require(existingAgent == null) { "Agent with name '$name' already exists" }
    try {
      agentDao.insertAgent(Agent(name = name))
    } catch (e: SQLiteConstraintException) {
      throw IllegalArgumentException("Agent with name '$name' already exists", e)
    } catch (e: SQLiteException) {
      throw AgentRepositoryException("Failed to add agent '$name'", e)
    }
  }
}
