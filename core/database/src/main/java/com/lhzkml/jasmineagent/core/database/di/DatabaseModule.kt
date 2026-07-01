@file:Suppress("DEPRECATION")

package com.lhzkml.jasmineagent.core.database.di

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lhzkml.jasmineagent.core.database.AgentDao
import com.lhzkml.jasmineagent.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Singleton
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

  @Provides
  @Singleton
  fun provideAgentDao(appDatabase: AppDatabase): AgentDao = appDatabase.agentDao()

  @Provides
  @Singleton
  fun provideAppDatabase(
    @ApplicationContext appContext: Context,
    encryptedPreferences: SharedPreferences,
  ): AppDatabase {
    System.loadLibrary("sqlcipher")
    val passphrase = DatabasePassphrase.generate(appContext.packageName, encryptedPreferences)
    val factory = SupportOpenHelperFactory(passphrase)
    return Room.databaseBuilder(appContext, AppDatabase::class.java, "Agent")
      .openHelperFactory(factory)
      .addMigrations(MIGRATION_1_2)
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

  private companion object {
    val MIGRATION_1_2 =
      object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL("ALTER TABLE agent ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0")
          db.execSQL("ALTER TABLE agent ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0")
          db.execSQL("ALTER TABLE agent ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'")
          db.execSQL("ALTER TABLE agent ADD COLUMN description TEXT")
          db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_agent_name ON agent(name)")
          db.execSQL("CREATE INDEX IF NOT EXISTS index_agent_created_at ON agent(created_at)")
        }
      }
  }
}

internal object DatabasePassphrase {
  fun generate(packageName: String, encryptedPreferences: SharedPreferences): ByteArray {
    val salt = getOrCreateSecret(encryptedPreferences, PASSPHRASE_SALT_KEY)
    val secret = getOrCreateSecret(encryptedPreferences, PASSPHRASE_SECRET_KEY)
    val password = "$packageName:${secret.toHex()}".toCharArray()
    val spec = PBEKeySpec(password, salt, PBKDF2_ITERATIONS, PASSPHRASE_BITS)

    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val secretKey = factory.generateSecret(spec)

    password.fill('\u0000')
    spec.clearPassword()

    return secretKey.encoded
  }

  private fun getOrCreateSecret(encryptedPreferences: SharedPreferences, key: String): ByteArray =
    encryptedPreferences.getString(key, null)?.hexToByteArray()
      ?: ByteArray(SECRET_BYTES).apply {
        SecureRandom().nextBytes(this)
        encryptedPreferences.edit { putString(key, toHex()) }
      }

  private fun String.hexToByteArray(): ByteArray =
    chunked(HEX_BYTE_LENGTH).map { it.toInt(HEX_RADIX).toByte() }.toByteArray()

  private fun ByteArray.toHex(): String = joinToString(separator = "") { "%02x".format(it) }

  private const val PASSPHRASE_SALT_KEY = "db_passphrase_salt"
  private const val PASSPHRASE_SECRET_KEY = "db_passphrase_secret"
  private const val SECRET_BYTES = 32
  private const val HEX_BYTE_LENGTH = 2
  private const val HEX_RADIX = 16
  private const val PBKDF2_ITERATIONS = 100_000
  private const val PASSPHRASE_BITS = 256
}
