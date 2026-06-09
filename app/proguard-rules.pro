# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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
-dontwarn androidx.room.paging.**

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