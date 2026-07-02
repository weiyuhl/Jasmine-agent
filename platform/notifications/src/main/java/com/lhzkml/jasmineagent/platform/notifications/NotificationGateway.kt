package com.lhzkml.jasmineagent.platform.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.lhzkml.jasmineagent.platform.os.AndroidApiLevel
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

  fun ensureDefaultChannels()

  fun notificationsEnabled(): Boolean

  fun settingsIntent(channelId: String? = null): Intent

  fun buildNotification(spec: PlatformNotificationSpec): Notification

  fun notify(spec: PlatformNotificationSpec): NotificationPostResult

  fun cancel(id: Int)
}

object PlatformNotificationChannels {
  const val AGENT_EXECUTION = "agent_execution"
  const val DIAGNOSTICS = "diagnostics"
  const val FOREGROUND_SERVICE = "foreground_service"

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
      PlatformNotificationChannel(
        id = FOREGROUND_SERVICE,
        name = "Foreground services",
        description = "User-visible work that must keep running in the foreground.",
        importance = NotificationManager.IMPORTANCE_LOW,
      ),
    )
}

class AndroidNotificationGateway
@Inject
constructor(@ApplicationContext private val context: Context) : NotificationGateway {
  private val notificationManagerCompat = NotificationManagerCompat.from(context)

  override fun ensureChannels(channels: Collection<PlatformNotificationChannel>) {
    if (Build.VERSION.SDK_INT < AndroidApiLevel.ANDROID_8) {
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

  override fun ensureDefaultChannels() {
    ensureChannels(PlatformNotificationChannels.defaults)
  }

  override fun notificationsEnabled(): Boolean =
    notificationManagerCompat.areNotificationsEnabled() &&
      (Build.VERSION.SDK_INT < AndroidApiLevel.ANDROID_13 ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED)

  override fun settingsIntent(channelId: String?): Intent {
    val intent =
      if (Build.VERSION.SDK_INT >= AndroidApiLevel.ANDROID_8 && channelId != null) {
        Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
          .putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
      } else {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
      }

    return intent
      .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }

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
        ensureDefaultChannels()
        notificationManagerCompat.notify(spec.id, buildNotification(spec))
        NotificationPostResult.Posted
      }
      .getOrElse { NotificationPostResult.Failed(it) }
  }

  override fun cancel(id: Int) {
    notificationManagerCompat.cancel(id)
  }
}
