package com.puutaro.commandclick.common.variable.variant

import android.Manifest
import android.webkit.PermissionRequest

enum class PermissionType(
    val str: String
) {
    CAMERA(Manifest.permission.CAMERA),
    VIDEO(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
}