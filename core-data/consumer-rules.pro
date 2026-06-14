# Repository interface and implementation
-keep interface com.lhzkml.jasmineagent.core.data.AgentRepository { *; }
-keep class com.lhzkml.jasmineagent.core.data.DefaultAgentRepository { *; }

# Hilt bindings
-keep class com.lhzkml.jasmineagent.core.data.di.** { *; }
