# SQLCipher bridge classes used by DatabaseModule.
-keep class net.zetetic.database.sqlcipher.SupportOpenHelperFactory { *; }
-dontwarn net.zetetic.database.sqlcipher.**

# Security Crypto public entry points used by DatabaseModule.
-keep class androidx.security.crypto.EncryptedSharedPreferences { *; }
-keep class androidx.security.crypto.MasterKey { *; }
-dontwarn androidx.security.crypto.**
