package com.lhzkml.jasmineagent.core.database.di

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import java.security.KeyStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseModuleSecurityTest {

  private lateinit var context: Context
  private lateinit var databaseModule: DatabaseModule
  private lateinit var testPreferences: SharedPreferences

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    databaseModule = DatabaseModule()
    testPreferences = context.getSharedPreferences(TEST_PREFERENCES_NAME, Context.MODE_PRIVATE)
    testPreferences.edit().clear().apply()
  }

  @Test
  fun testEncryptedPreferencesCreation() {
    assumeAndroidKeyStoreAvailable()

    val prefs = databaseModule.provideEncryptedPreferences(context)
    assertNotNull("EncryptedSharedPreferences should not be null", prefs)
  }

  @Test
  fun testEncryptedPreferencesStoreAndRetrieve() {
    assumeAndroidKeyStoreAvailable()

    val prefs = databaseModule.provideEncryptedPreferences(context)
    val testKey = "test_key"
    val testValue = "secret_value_12345"

    prefs.edit().putString(testKey, testValue).apply()
    val retrieved = prefs.getString(testKey, null)

    assertEquals("Stored value should match retrieved value", testValue, retrieved)
  }

  @Test
  fun testPassphraseConsistency() {
    val passphrase1 = generatePassphraseViaReflection(context, testPreferences)
    val passphrase2 = generatePassphraseViaReflection(context, testPreferences)

    assertTrue("Passphrase should be 32 bytes", passphrase1.size == 32)
    assertTrue("Passphrase should be consistent", passphrase1.contentEquals(passphrase2))
  }

  @Test
  fun testPassphraseUniquenessAcrossContexts() {
    val passphrase1 = generatePassphraseViaReflection(context, testPreferences)

    testPreferences.edit().clear().apply()

    val passphrase2 = generatePassphraseViaReflection(context, testPreferences)

    assertNotEquals(
      "Passphrase should be different after clearing salt",
      passphrase1.contentToString(),
      passphrase2.contentToString(),
    )
  }

  @Test
  fun testSaltStoredInEncryptedPreferences() {
    generatePassphraseViaReflection(context, testPreferences)

    val salt = testPreferences.getString("db_passphrase_salt", null)
    assertNotNull("Salt should be stored", salt)
    assertEquals("Salt should be 64 hex characters (32 bytes)", 64, salt?.length)
    assertTrue("Salt should be valid hex", salt?.matches(Regex("^[0-9a-f]{64}$")) == true)
  }

  @Test
  fun testPassphraseDerivationStrength() {
    val passphrase = generatePassphraseViaReflection(context, testPreferences)

    val uniqueBytes = passphrase.toSet().size
    assertTrue("Passphrase should have high entropy (at least 20 unique bytes)", uniqueBytes >= 20)
  }

  private fun generatePassphraseViaReflection(
    context: Context,
    prefs: SharedPreferences,
  ): ByteArray {
    val method =
      DatabaseModule::class
        .java
        .getDeclaredMethod("generatePassphrase", Context::class.java, SharedPreferences::class.java)
    method.isAccessible = true
    return method.invoke(databaseModule, context, prefs) as ByteArray
  }

  private fun assumeAndroidKeyStoreAvailable() {
    assumeTrue(
      "AndroidKeyStore is only available on an Android runtime.",
      isAndroidKeyStoreAvailable(),
    )
  }

  private fun isAndroidKeyStoreAvailable(): Boolean =
    runCatching { KeyStore.getInstance(ANDROID_KEYSTORE) }.isSuccess

  private companion object {
    const val ANDROID_KEYSTORE = "AndroidKeyStore"
    const val TEST_PREFERENCES_NAME = "database_module_security_test"
  }
}
