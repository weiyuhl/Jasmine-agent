package com.lhzkml.jasmineagent.platform.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class PlatformNotificationChannel(
  val id: String,
  val name: String,
  val description: String,
  val importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
)

data class PlatformNotificationSpec(
  val id: Int,
  val channelId: String,
  val title: String,
  val text: String,
  val smallIconResId: Int,
  val ongoing: Boolean = false,
  val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
  val category: String? = null,
)

sealed interface NotificationPostResult {
  data object Posted : NotificationPostResult

  data object BlockedByPermission : NotificationPostResult

  data class Failed(val cause: Throwable) : NotificationPostResult
}

interface NotificationGateway {
  fun ensureChannels(channels: Collection<PlatformNotificationChannel>)

  fun notificationsEnabled(): Boolean

  fun buildNotification(spec: PlatformNotificationSpec): Notification

  fun notify(spec: PlatformNotificationSpec): NotificationPostResult

  fun cancel(id: Int)
}

object PlatformNotificationChannels {
  const val AGENT_EXECUTION = "agent_execution"
  const val DIAGNOSTICS = "diagnostics"

  val defaults =
    listOf(
      PlatformNotificationChannel(
        id = AGENT_EXECUTION,
        name = "Agent execution",
        description = "Ongoing Agent and tool execution status.",
        importance = NotificationManager.IMPORTANCE_DEFAULT,
      ),
      PlatformNotificationChannel(
        id = DIAGNOSTICS,
        name = "Diagnostics",
        description = "Debug and diagnostic status updates.",
        importance = NotificationManager.IMPORTANCE_LOW,
      ),
    )
}

class AndroidNotificationGateway
@Inject
constructor(@ApplicationContext private val context: Context) : NotificationGateway {
  private val notificationManagerCompat = NotificationManagerCompat.from(context)

  override fun ensureChannels(channels: Collection<PlatformNotificationChannel>) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return
    }

    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannels(
      channels.map { channel ->
        NotificationChannel(channel.id, channel.name, channel.importance).apply {
          description = channel.description
        }
      }
    )
  }

  override fun notificationsEnabled(): Boolean =
    notificationManagerCompat.areNotificationsEnabled() &&
      (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED)

  override fun buildNotification(spec: PlatformNotificationSpec): Notification =
    NotificationCompat.Builder(context, spec.channelId)
      .setSmallIcon(spec.smallIconResId)
      .setContentTitle(spec.title)
      .setContentText(spec.text)
      .setStyle(NotificationCompat.BigTextStyle().bigText(spec.text))
      .setOngoing(spec.ongoing)
      .setPriority(spec.priority)
      .setCategory(spec.category)
      .build()

  @SuppressLint("MissingPermission")
  override fun notify(spec: PlatformNotificationSpec): NotificationPostResult {
    if (!notificationsEnabled()) {
      return NotificationPostResult.BlockedByPermission
    }

    return runCatching {
        notificationManagerCompat.notify(spec.id, buildNotification(spec))
        NotificationPostResult.Posted
      }
      .getOrElse { NotificationPostResult.Failed(it) }
  }

  override fun cancel(id: Int) {
    notificationManagerCompat.cancel(id)
  }
}
