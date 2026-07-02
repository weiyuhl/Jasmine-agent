package com.lhzkml.jasmineagent.platform.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lhzkml.jasmineagent.platform.os.AndroidApiLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val ACCESS_LOCAL_NETWORK_PERMISSION = "android.permission.ACCESS_LOCAL_NETWORK"

enum class PlatformPermission(
  val manifestName: String,
  val minSdk: Int = 1,
  val maxSdk: Int = Int.MAX_VALUE,
) {
  PostNotifications(Manifest.permission.POST_NOTIFICATIONS, AndroidApiLevel.ANDROID_13),
  LocalNetwork(ACCESS_LOCAL_NETWORK_PERMISSION, AndroidApiLevel.ANDROID_17),
  NearbyWifiDevices(Manifest.permission.NEARBY_WIFI_DEVICES, AndroidApiLevel.ANDROID_13),
  Camera(Manifest.permission.CAMERA),
  RecordAudio(Manifest.permission.RECORD_AUDIO),
  AccessCoarseLocation(Manifest.permission.ACCESS_COARSE_LOCATION),
  AccessFineLocation(Manifest.permission.ACCESS_FINE_LOCATION),
  ReadExternalStorage(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    maxSdk = AndroidApiLevel.ANDROID_12L,
  ),
  WriteExternalStorage(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    maxSdk = AndroidApiLevel.ANDROID_9,
  ),
  ReadMediaImages(Manifest.permission.READ_MEDIA_IMAGES, AndroidApiLevel.ANDROID_13),
  ReadMediaVideo(Manifest.permission.READ_MEDIA_VIDEO, AndroidApiLevel.ANDROID_13),
  ReadMediaAudio(Manifest.permission.READ_MEDIA_AUDIO, AndroidApiLevel.ANDROID_13),
  ReadMediaVisualUserSelected(
    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
    AndroidApiLevel.ANDROID_14,
  );

  fun appliesTo(sdkInt: Int): Boolean = sdkInt in minSdk..maxSdk
}

enum class PlatformMediaAccess {
  Images,
  Video,
  Audio,
  ImagesAndVideo,
  VisualUserSelected,
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

  fun mediaReadPermissionsFor(
    access: PlatformMediaAccess,
    sdkInt: Int = Build.VERSION.SDK_INT,
  ): List<PlatformPermission>

  fun localNetworkPermissionsFor(sdkInt: Int = Build.VERSION.SDK_INT): List<PlatformPermission>
}

class AndroidPermissionGateway
@Inject
constructor(@ApplicationContext private val context: Context) : PermissionGateway {

  override fun state(permission: PlatformPermission, activity: Activity?): PermissionState {
    if (!permission.appliesTo(Build.VERSION.SDK_INT)) {
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

  override fun mediaReadPermissionsFor(
    access: PlatformMediaAccess,
    sdkInt: Int,
  ): List<PlatformPermission> = mediaReadPermissionsForSdk(access = access, sdkInt = sdkInt)

  override fun localNetworkPermissionsFor(sdkInt: Int): List<PlatformPermission> =
    when {
      sdkInt >= AndroidApiLevel.ANDROID_17 -> listOf(PlatformPermission.LocalNetwork)
      sdkInt >= AndroidApiLevel.ANDROID_16 -> listOf(PlatformPermission.NearbyWifiDevices)
      else -> emptyList()
    }
}

fun mediaReadPermissionsForSdk(
  access: PlatformMediaAccess,
  sdkInt: Int = Build.VERSION.SDK_INT,
): List<PlatformPermission> =
  when {
    sdkInt >= AndroidApiLevel.ANDROID_14 && access == PlatformMediaAccess.VisualUserSelected ->
      listOf(
        PlatformPermission.ReadMediaImages,
        PlatformPermission.ReadMediaVideo,
        PlatformPermission.ReadMediaVisualUserSelected,
      )
    sdkInt >= AndroidApiLevel.ANDROID_13 -> granularMediaPermissions(access)
    else -> listOf(PlatformPermission.ReadExternalStorage)
  }.filter { it.appliesTo(sdkInt) }

private fun granularMediaPermissions(access: PlatformMediaAccess): List<PlatformPermission> =
  when (access) {
    PlatformMediaAccess.Images -> listOf(PlatformPermission.ReadMediaImages)
    PlatformMediaAccess.Video -> listOf(PlatformPermission.ReadMediaVideo)
    PlatformMediaAccess.Audio -> listOf(PlatformPermission.ReadMediaAudio)
    PlatformMediaAccess.ImagesAndVideo,
    PlatformMediaAccess.VisualUserSelected ->
      listOf(PlatformPermission.ReadMediaImages, PlatformPermission.ReadMediaVideo)
  }
