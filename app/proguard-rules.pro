# SQLCipher - keep native methods and database classes
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.database.**

# Android Keystore alias for SQLCipher (if using KeyStore)
-keepclassmembers class * extends javax.crypto.KeyGenerator {
    public <init>(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.lhzkml.jasmineagent.**$$serializer { *; }
-keepclassmembers class com.lhzkml.jasmineagent.** {
    *** Companion;
}
-keepclasseswithmembers class com.lhzkml.jasmineagent.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Navigation3
-keep class * extends androidx.navigation3.runtime.NavKey

# Kotlin Coroutines - minimal rules
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ProfileInstaller
-dontwarn androidx.profileinstaller.**
-keep class androidx.profileinstaller.** { *; }

# Startup
-keep class androidx.startup.** { *; }
-dontwarn androidx.startup.**

# Compose - minimal rules (compiler handles most)
-dontwarn androidx.compose.**

# Security Crypto (EncryptedSharedPreferences / MasterKey)
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
    <fields>;
}

# Tink / Security Crypto internal annotations (errorprone)
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.crypto.tink.**
-keep class com.google.crypto.tink.** { *; }
