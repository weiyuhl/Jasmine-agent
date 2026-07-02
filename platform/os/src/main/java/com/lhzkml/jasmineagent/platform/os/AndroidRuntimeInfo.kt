package com.lhzkml.jasmineagent.platform.os

import android.os.Build
import javax.inject.Inject

data class AndroidRuntimeSnapshot(
  val sdkInt: Int,
  val release: String,
  val codename: String,
  val manufacturer: String,
  val brand: String,
  val model: String,
  val device: String,
  val supportedAbis: List<String>,
)

interface AndroidRuntimeInfo {
  fun snapshot(): AndroidRuntimeSnapshot

  fun isAtLeast(apiLevel: Int): Boolean
}

class DefaultAndroidRuntimeInfo @Inject constructor() : AndroidRuntimeInfo {
  override fun snapshot(): AndroidRuntimeSnapshot =
    AndroidRuntimeSnapshot(
      sdkInt = Build.VERSION.SDK_INT,
      release = Build.VERSION.RELEASE.orEmpty(),
      codename = Build.VERSION.CODENAME.orEmpty(),
      manufacturer = Build.MANUFACTURER.orEmpty(),
      brand = Build.BRAND.orEmpty(),
      model = Build.MODEL.orEmpty(),
      device = Build.DEVICE.orEmpty(),
      supportedAbis = Build.SUPPORTED_ABIS.toList(),
    )

  override fun isAtLeast(apiLevel: Int): Boolean = Build.VERSION.SDK_INT >= apiLevel
}
