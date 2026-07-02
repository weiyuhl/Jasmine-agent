package com.lhzkml.jasmineagent.platform.os

import android.annotation.SuppressLint
import android.os.Build
import android.os.ext.SdkExtensions
import javax.inject.Inject

object AndroidApiLevel {
  const val ANDROID_8 = 26
  const val ANDROID_9 = 28
  const val ANDROID_10 = 29
  const val ANDROID_11 = 30
  const val ANDROID_12 = 31
  const val ANDROID_12L = 32
  const val ANDROID_13 = 33
  const val ANDROID_14 = 34
  const val ANDROID_15 = 35
  const val ANDROID_16 = 36
  const val ANDROID_17 = 37
}

data class AndroidRuntimeSnapshot(
  val sdkInt: Int,
  val previewSdkInt: Int,
  val release: String,
  val codename: String,
  val securityPatch: String,
  val manufacturer: String,
  val brand: String,
  val model: String,
  val device: String,
  val supportedAbis: List<String>,
  val sdkExtensionVersions: Map<Int, Int>,
) {
  val isPreview: Boolean = codename != "REL" || previewSdkInt > 0

  fun isAtLeast(apiLevel: Int): Boolean = sdkInt >= apiLevel
}

data class AndroidCompatibilityWindow(
  val minimumInstallApi: Int = AndroidApiLevel.ANDROID_8,
  val legacyCompatibilityApi: Int = AndroidApiLevel.ANDROID_9,
  val currentTargetApi: Int = AndroidApiLevel.ANDROID_17,
)

interface AndroidRuntimeInfo {
  fun snapshot(): AndroidRuntimeSnapshot

  fun isAtLeast(apiLevel: Int): Boolean

  fun extensionVersion(apiLevel: Int): Int
}

class DefaultAndroidRuntimeInfo @Inject constructor() : AndroidRuntimeInfo {
  override fun snapshot(): AndroidRuntimeSnapshot =
    AndroidRuntimeSnapshot(
      sdkInt = Build.VERSION.SDK_INT,
      previewSdkInt = Build.VERSION.PREVIEW_SDK_INT,
      release = Build.VERSION.RELEASE.orEmpty(),
      codename = Build.VERSION.CODENAME.orEmpty(),
      securityPatch = Build.VERSION.SECURITY_PATCH.orEmpty(),
      manufacturer = Build.MANUFACTURER.orEmpty(),
      brand = Build.BRAND.orEmpty(),
      model = Build.MODEL.orEmpty(),
      device = Build.DEVICE.orEmpty(),
      supportedAbis = Build.SUPPORTED_ABIS.toList(),
      sdkExtensionVersions = androidSdkExtensionVersions(),
    )

  override fun isAtLeast(apiLevel: Int): Boolean = Build.VERSION.SDK_INT >= apiLevel

  override fun extensionVersion(apiLevel: Int): Int = snapshot().sdkExtensionVersions[apiLevel] ?: 0
}

@SuppressLint("NewApi")
private fun androidSdkExtensionVersions(): Map<Int, Int> {
  if (Build.VERSION.SDK_INT < AndroidApiLevel.ANDROID_11) {
    return emptyMap()
  }

  return buildMap {
    put(AndroidApiLevel.ANDROID_11, SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R))
    put(AndroidApiLevel.ANDROID_12, SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S))
    put(
      AndroidApiLevel.ANDROID_12L,
      SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S_V2),
    )
    put(
      AndroidApiLevel.ANDROID_13,
      SdkExtensions.getExtensionVersion(Build.VERSION_CODES.TIRAMISU),
    )
    put(
      AndroidApiLevel.ANDROID_14,
      SdkExtensions.getExtensionVersion(Build.VERSION_CODES.UPSIDE_DOWN_CAKE),
    )
  }
}
