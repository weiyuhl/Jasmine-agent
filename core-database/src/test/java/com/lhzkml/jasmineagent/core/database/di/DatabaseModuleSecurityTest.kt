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
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseModuleSecurityTest {

  private lateinit var context: Context
  private lateinit var databaseModule: DatabaseModule

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    databaseModule = DatabaseModule()
  }

  @Test
  fun testEncryptedPreferencesCreation() {
    val prefs = databaseModule.provideEncryptedPreferences(context)
    assertNotNull("EncryptedSharedPreferences should not be null", prefs)
  }

  @Test
  fun testEncryptedPreferencesStoreAndRetrieve() {
    val prefs = databaseModule.provideEncryptedPreferences(context)
    val testKey = "test_key"
    val testValue = "secret_value_12345"

    prefs.edit().putString(testKey, testValue).apply()
    val retrieved = prefs.getString(testKey, null)

    assertEquals("Stored value should match retrieved value", testValue, retrieved)
  }

  @Test
  fun testPassphraseConsistency() {
    val prefs = databaseModule.provideEncryptedPreferences(context)
    val passphrase1 = generatePassphraseViaReflection(context, prefs)
    val passphrase2 = generatePassphraseViaReflection(context, prefs)

    assertTrue("Passphrase should be 32 bytes", passphrase1.size == 32)
    assertTrue("Passphrase should be consistent", passphrase1.contentEquals(passphrase2))
  }

  @Test
  fun testPassphraseUniquenessAcrossContexts() {
    val prefs1 = databaseModule.provideEncryptedPreferences(context)
    val passphrase1 = generatePassphraseViaReflection(context, prefs1)

    prefs1.edit().clear().apply()

    val passphrase2 = generatePassphraseViaReflection(context, prefs1)

    assertNotEquals(
      "Passphrase should be different after clearing salt",
      passphrase1.contentToString(),
      passphrase2.contentToString(),
    )
  }

  @Test
  fun testSaltStoredInEncryptedPreferences() {
    val prefs = databaseModule.provideEncryptedPreferences(context)
    prefs.edit().clear().apply()

    generatePassphraseViaReflection(context, prefs)

    val salt = prefs.getString("db_passphrase_salt", null)
    assertNotNull("Salt should be stored", salt)
    assertEquals("Salt should be 64 hex characters (32 bytes)", 64, salt?.length)
    assertTrue("Salt should be valid hex", salt?.matches(Regex("^[0-9a-f]{64}$")) == true)
  }

  @Test
  fun testPassphraseDerivationStrength() {
    val prefs = databaseModule.provideEncryptedPreferences(context)
    val passphrase = generatePassphraseViaReflection(context, prefs)

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
}
