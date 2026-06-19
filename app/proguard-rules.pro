# SQLCipher - keep the bridge classes used by Room's openHelperFactory.
-keep class net.sqlcipher.database.SQLiteDatabase { *; }
-keep class net.sqlcipher.database.SupportFactory { *; }
-dontwarn net.sqlcipher.**

# Android Keystore alias for SQLCipher (if using KeyStore)
-keepclassmembers class * extends javax.crypto.KeyGenerator {
    public <init>(...);
}

# Kotlin metadata and serialization annotations used by Navigation 3 keys.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Navigation3 serializable keys
-keep,includedescriptorclasses class com.lhzkml.jasmineagent.feature.agent.navigation.keys.**$$serializer { *; }
-keepclassmembers class com.lhzkml.jasmineagent.feature.agent.navigation.keys.** {
    *** Companion;
}
-keepclasseswithmembers class com.lhzkml.jasmineagent.feature.agent.navigation.keys.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.lhzkml.jasmineagent.feature.agent.navigation.keys.Main { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankOne { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankTwo { *; }

# Security Crypto (EncryptedSharedPreferences / MasterKey)
-keep class androidx.security.crypto.EncryptedSharedPreferences { *; }
-keep class androidx.security.crypto.MasterKey { *; }
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
    <fields>;
}

# Tink / Security Crypto internal annotations (errorprone)
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.crypto.tink.**
