# Navigation3 serializable keys used as top-level routes.
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
