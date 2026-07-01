package com.lhzkml.jasmineagent.core.database.di

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DatabaseModuleSecurityTest {

  private lateinit var testPreferences: FakeSharedPreferences

  @Before
  fun setup() {
    testPreferences = FakeSharedPreferences()
  }

  @Test
  fun testPassphraseConsistency() {
    val passphrase1 = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)
    val passphrase2 = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)

    assertTrue("Passphrase should be 32 bytes", passphrase1.size == 32)
    assertTrue("Passphrase should be consistent", passphrase1.contentEquals(passphrase2))
  }

  @Test
  fun testPassphraseUniquenessAcrossFreshPreferences() {
    val passphrase1 = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)

    testPreferences.clear()

    val passphrase2 = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)

    assertNotEquals(
      "Passphrase should be different after clearing salt and secret",
      passphrase1.contentToString(),
      passphrase2.contentToString(),
    )
  }

  @Test
  fun testPassphraseChangesAcrossPackageNames() {
    val passphrase1 = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)
    val passphrase2 = DatabasePassphrase.generate("$TEST_PACKAGE_NAME.debug", testPreferences)

    assertNotEquals(
      "Passphrase should include the package name in its derivation input",
      passphrase1.contentToString(),
      passphrase2.contentToString(),
    )
  }

  @Test
  fun testPassphraseInputsStoredInEncryptedPreferences() {
    DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)

    val salt = testPreferences.getString("db_passphrase_salt", null)
    val secret = testPreferences.getString("db_passphrase_secret", null)

    assertNotNull("Salt should be stored", salt)
    assertEquals("Salt should be 64 hex characters (32 bytes)", 64, salt?.length)
    assertTrue("Salt should be valid hex", salt?.matches(Regex("^[0-9a-f]{64}$")) == true)

    assertNotNull("Secret should be stored", secret)
    assertEquals("Secret should be 64 hex characters (32 bytes)", 64, secret?.length)
    assertTrue("Secret should be valid hex", secret?.matches(Regex("^[0-9a-f]{64}$")) == true)
  }

  @Test
  fun testPassphraseChangesWhenSecretChanges() {
    val passphrase1 = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)
    val originalSalt = testPreferences.getString("db_passphrase_salt", null)

    testPreferences.edit().remove("db_passphrase_secret").apply()

    val passphrase2 = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)

    assertEquals(
      "Salt should not change when only secret is cleared",
      originalSalt,
      testPreferences.getString("db_passphrase_salt", null),
    )
    assertNotEquals(
      "Passphrase should change when random secret changes",
      passphrase1.contentToString(),
      passphrase2.contentToString(),
    )
  }

  @Test
  fun testPassphraseDerivationStrength() {
    val passphrase = DatabasePassphrase.generate(TEST_PACKAGE_NAME, testPreferences)

    val uniqueBytes = passphrase.toSet().size
    assertTrue("Passphrase should have high entropy (at least 20 unique bytes)", uniqueBytes >= 20)
  }

  private companion object {
    const val TEST_PACKAGE_NAME = "com.lhzkml.jasmineagent.test"
  }
}

private class FakeSharedPreferences : SharedPreferences {
  private val values = mutableMapOf<String, Any?>()

  override fun getAll(): MutableMap<String, *> = values.toMutableMap()

  override fun getString(key: String?, defValue: String?): String? =
    values[key] as? String ?: defValue

  override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? =
    @Suppress("UNCHECKED_CAST") (values[key] as? MutableSet<String>) ?: defValues

  override fun getInt(key: String?, defValue: Int): Int = values[key] as? Int ?: defValue

  override fun getLong(key: String?, defValue: Long): Long = values[key] as? Long ?: defValue

  override fun getFloat(key: String?, defValue: Float): Float = values[key] as? Float ?: defValue

  override fun getBoolean(key: String?, defValue: Boolean): Boolean =
    values[key] as? Boolean ?: defValue

  override fun contains(key: String?): Boolean = values.containsKey(key)

  override fun edit(): SharedPreferences.Editor = FakeEditor()

  override fun registerOnSharedPreferenceChangeListener(
    listener: SharedPreferences.OnSharedPreferenceChangeListener?
  ) = Unit

  override fun unregisterOnSharedPreferenceChangeListener(
    listener: SharedPreferences.OnSharedPreferenceChangeListener?
  ) = Unit

  fun clear() {
    values.clear()
  }

  private inner class FakeEditor : SharedPreferences.Editor {
    private val edits = mutableMapOf<String, Any?>()
    private val removals = mutableSetOf<String>()
    private var clearRequested = false

    override fun putString(key: String?, value: String?): SharedPreferences.Editor = apply {
      key?.let { edits[it] = value }
    }

    override fun putStringSet(
      key: String?,
      values: MutableSet<String>?,
    ): SharedPreferences.Editor = apply { key?.let { edits[it] = values } }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor = apply {
      key?.let { edits[it] = value }
    }

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor = apply {
      key?.let { edits[it] = value }
    }

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = apply {
      key?.let { edits[it] = value }
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = apply {
      key?.let { edits[it] = value }
    }

    override fun remove(key: String?): SharedPreferences.Editor = apply {
      key?.let { removals += it }
    }

    override fun clear(): SharedPreferences.Editor = apply { clearRequested = true }

    override fun commit(): Boolean {
      applyChanges()
      return true
    }

    override fun apply() {
      applyChanges()
    }

    private fun applyChanges() {
      if (clearRequested) {
        values.clear()
      }
      removals.forEach(values::remove)
      values.putAll(edits)
    }
  }
}
