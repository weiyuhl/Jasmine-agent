package com.lhzkml.jasmineagent.core.data

import android.database.sqlite.SQLiteException
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentDao
import com.lhzkml.jasmineagent.core.database.AgentStatus
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Test-only fake DAO for core-data layer tests. */
class FakeAgentDao : AgentDao {

  private val mutex = Mutex()
  private val data = mutableListOf<Agent>()
  private val nextUid = AtomicInteger(1)
  private val activeNamesError = AtomicReference<SQLiteException?>(null)
  private val getByNameError = AtomicReference<SQLiteException?>(null)

  override fun getActiveAgentNames(limit: Int): Flow<List<String>> = flow {
    val names = mutex.withLock {
      activeNamesError.get()?.let { throw it }
      data.filter { it.status == AgentStatus.ACTIVE }.map { it.name }.take(limit)
    }
    emit(names)
  }

  override fun getAgents(limit: Int): Flow<List<Agent>> = flow {
    emit(mutex.withLock { data.take(limit) })
  }

  override suspend fun getAgentById(uid: Int): Agent? = mutex.withLock {
    data.find { it.uid == uid }
  }

  override suspend fun getAgentByName(name: String): Agent? {
    return mutex.withLock {
      getByNameError.get()?.let { throw it }
      data.find { it.name == name }
    }
  }

  override suspend fun insertAgent(item: Agent) {
    mutex.withLock {
      val uid = if (item.uid == 0) nextUid.getAndIncrement() else item.uid
      data.add(0, item.copy(uid = uid))
    }
  }

  override suspend fun updateAgentStatus(uid: Int, status: AgentStatus, updatedAt: Long) {
    mutex.withLock {
      val index = data.indexOfFirst { it.uid == uid }
      if (index != -1) {
        data[index] = data[index].copy(status = status, updatedAt = updatedAt)
      }
    }
  }

  override suspend fun deleteAgent(uid: Int) {
    mutex.withLock {
      data.removeAll { it.uid == uid }
    }
  }

  override suspend fun getActiveAgentCount(): Int = mutex.withLock {
    data.count { it.status == AgentStatus.ACTIVE }
  }

  fun throwActiveNamesError(exception: SQLiteException) {
    activeNamesError.set(exception)
  }

  fun throwGetByNameError(exception: SQLiteException) {
    getByNameError.set(exception)
  }
}
