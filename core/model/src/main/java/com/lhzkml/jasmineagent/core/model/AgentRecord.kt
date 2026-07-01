package com.lhzkml.jasmineagent.core.model

/** Read-only Agent model shared across domain, data, and feature UI layers. */
data class AgentRecord(
  val uid: Int,
  val name: String,
  val createdAt: Long,
  val updatedAt: Long,
  val status: AgentRecordStatus,
  val description: String?,
)

enum class AgentRecordStatus {
  ACTIVE,
  INACTIVE,
  ARCHIVED,
}
