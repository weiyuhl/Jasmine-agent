# ViewModel and UI State classes
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AgentViewModel { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState$* { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AddAgentState { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AddAgentState$* { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AddAgentError { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AddAgentError$* { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AgentEvent { *; }
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AgentEvent$* { *; }

# Compose functions (usually not needed but safe)
-keep class com.lhzkml.jasmineagent.feature.agent.ui.AgentScreenKt { *; }

# Kotlinx Serialization (if used in this module)
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class com.lhzkml.jasmineagent.feature.agent.** {
    *** Companion;
}
