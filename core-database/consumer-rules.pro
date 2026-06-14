# Room consumer rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# SQLCipher
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Keep database entities and enums
-keep class com.lhzkml.jasmineagent.core.database.Agent { *; }
-keep class com.lhzkml.jasmineagent.core.database.AgentStatus { *; }
-keep interface com.lhzkml.jasmineagent.core.database.AgentDao { *; }
-keep class com.lhzkml.jasmineagent.core.database.AppDatabase { *; }
-keep class com.lhzkml.jasmineagent.core.database.MIGRATION_1_2 { *; }

# Security Crypto
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**
