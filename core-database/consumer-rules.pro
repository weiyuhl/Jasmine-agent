# SQLCipher bridge classes used by DatabaseModule.
-keep class net.sqlcipher.database.SQLiteDatabase { *; }
-keep class net.sqlcipher.database.SupportFactory { *; }
-dontwarn net.sqlcipher.**

# Security Crypto public entry points used by DatabaseModule.
-keep class androidx.security.crypto.EncryptedSharedPreferences { *; }
-keep class androidx.security.crypto.MasterKey { *; }
-dontwarn androidx.security.crypto.**
