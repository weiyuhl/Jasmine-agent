# Android Keystore alias for SQLCipher (if using KeyStore)
-keepclassmembers class * extends javax.crypto.KeyGenerator {
    public <init>(...);
}

# Local crash reports should keep their own frame names in release builds.
-keep class com.lhzkml.jasmineagent.diagnostics.CrashReporter { *; }

# Kotlin metadata and serialization annotations used by Navigation 3 keys.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Navigation3 serializable keys
-keep,includedescriptorclasses class com.lhzkml.jasmineagent.core.navigation.**$$serializer { *; }
-keepclassmembers class com.lhzkml.jasmineagent.core.navigation.** {
    *** Companion;
}
-keepclasseswithmembers class com.lhzkml.jasmineagent.core.navigation.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.lhzkml.jasmineagent.core.navigation.Main { *; }
-keep class com.lhzkml.jasmineagent.core.navigation.BlankOne { *; }
-keep class com.lhzkml.jasmineagent.core.navigation.BlankTwo { *; }

# Security Crypto (EncryptedSharedPreferences / MasterKey)
-keep class androidx.security.crypto.EncryptedSharedPreferences { *; }
-keep class androidx.security.crypto.MasterKey { *; }
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
    <fields>;
}

# Tink / Security Crypto internal annotations (errorprone)
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.crypto.tink.**
