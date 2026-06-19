# Navigation3 serializable keys used as top-level routes.
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
