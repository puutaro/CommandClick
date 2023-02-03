package com.puutaro.commandclick.activity_lib.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat


class WriteExternalStoragePermission {
    companion object {
        fun get(
            activity: Activity,
            requestPermissionLauncher: ActivityResultLauncher<String>
        ){
            when (
            ContextCompat.checkSelfPermission(
                activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ) {
                PackageManager.PERMISSION_GRANTED -> {
                    println("pass")
                }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
        }
    }
}