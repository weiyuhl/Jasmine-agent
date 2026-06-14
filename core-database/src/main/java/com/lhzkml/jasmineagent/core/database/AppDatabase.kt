package com.lhzkml.jasmineagent.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Agent::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
  abstract fun agentDao(): AgentDao
}
