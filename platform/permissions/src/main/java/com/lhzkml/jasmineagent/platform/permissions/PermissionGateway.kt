package com.lhzkml.jasmineagent.platform.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class PlatformPermission(val manifestName: String, val minSdk: Int = 1) {
  PostNotifications(Manifest.permission.POST_NOTIFICATIONS, Build.VERSION_CODES.TIRAMISU),
  Camera(Manifest.permission.CAMERA),
  RecordAudio(Manifest.permission.RECORD_AUDIO),
  ReadMediaImages(Manifest.permission.READ_MEDIA_IMAGES, Build.VERSION_CODES.TIRAMISU),
  ReadMediaVideo(Manifest.permission.READ_MEDIA_VIDEO, Build.VERSION_CODES.TIRAMISU),
  ReadMediaAudio(Manifest.permission.READ_MEDIA_AUDIO, Build.VERSION_CODES.TIRAMISU),
}

enum class PermissionGrantState {
  Granted,
  Denied,
  NotRequired,
}

data class PermissionState(
  val permission: PlatformPermission,
  val grantState: PermissionGrantState,
  val shouldShowRationale: Boolean,
)

interface PermissionGateway {
  fun state(permission: PlatformPermission, activity: Activity? = null): PermissionState

  fun states(
    permissions: Collection<PlatformPermission>,
    activity: Activity? = null,
  ): List<PermissionState>

  fun isGranted(permission: PlatformPermission): Boolean

  fun missingPermissions(permissions: Collection<PlatformPermission>): List<PlatformPermission>
}

class AndroidPermissionGateway
@Inject
constructor(@ApplicationContext private val context: Context) : PermissionGateway {

  override fun state(permission: PlatformPermission, activity: Activity?): PermissionState {
    if (Build.VERSION.SDK_INT < permission.minSdk) {
      return PermissionState(
        permission,
        PermissionGrantState.NotRequired,
        shouldShowRationale = false,
      )
    }

    val granted =
      ContextCompat.checkSelfPermission(context, permission.manifestName) ==
        PackageManager.PERMISSION_GRANTED
    val grantState = if (granted) PermissionGrantState.Granted else PermissionGrantState.Denied
    val shouldShowRationale =
      activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, permission.manifestName)
      } ?: false

    return PermissionState(permission, grantState, shouldShowRationale)
  }

  override fun states(
    permissions: Collection<PlatformPermission>,
    activity: Activity?,
  ): List<PermissionState> = permissions.map { state(it, activity) }

  override fun isGranted(permission: PlatformPermission): Boolean =
    state(permission).grantState != PermissionGrantState.Denied

  override fun missingPermissions(
    permissions: Collection<PlatformPermission>
  ): List<PlatformPermission> = permissions.filter {
    state(it).grantState == PermissionGrantState.Denied
  }
}
