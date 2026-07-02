package com.lhzkml.jasmineagent.platform.permissions

import com.lhzkml.jasmineagent.platform.os.AndroidApiLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionGatewayTest {
  @Test
  fun readMediaUsesLegacyStorageBeforeAndroid13() {
    val permissions =
      mediaReadPermissionsForSdk(
        access = PlatformMediaAccess.ImagesAndVideo,
        sdkInt = AndroidApiLevel.ANDROID_12L,
      )

    assertEquals(listOf(PlatformPermission.ReadExternalStorage), permissions)
  }

  @Test
  fun readMediaUsesGranularPermissionsOnAndroid13() {
    val permissions =
      mediaReadPermissionsForSdk(
        access = PlatformMediaAccess.ImagesAndVideo,
        sdkInt = AndroidApiLevel.ANDROID_13,
      )

    assertEquals(
      listOf(PlatformPermission.ReadMediaImages, PlatformPermission.ReadMediaVideo),
      permissions,
    )
  }

  @Test
  fun partialMediaAccessAddsVisualSelectionOnAndroid14AndAbove() {
    val permissions =
      mediaReadPermissionsForSdk(
        access = PlatformMediaAccess.VisualUserSelected,
        sdkInt = AndroidApiLevel.ANDROID_14,
      )

    assertEquals(
      listOf(
        PlatformPermission.ReadMediaImages,
        PlatformPermission.ReadMediaVideo,
        PlatformPermission.ReadMediaVisualUserSelected,
      ),
      permissions,
    )
  }

  @Test
  fun localNetworkPermissionIsOnlyApplicableFromAndroid17() {
    assertFalse(PlatformPermission.LocalNetwork.appliesTo(AndroidApiLevel.ANDROID_16))
    assertTrue(PlatformPermission.LocalNetwork.appliesTo(AndroidApiLevel.ANDROID_17))
  }
}
