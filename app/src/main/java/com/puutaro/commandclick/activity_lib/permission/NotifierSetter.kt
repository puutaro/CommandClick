package com.puutaro.commandclick.activity_lib.permission

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.init.ActivityFinisher
import com.puutaro.commandclick.activity_lib.manager.FragmentStartHandler

object NotifierSetter {

    fun getPermissionAndSet(
        activity: MainActivity
    ){
        if(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            FragmentStartHandler.handle(
                activity
            )
            return
        }
        val postNotifications = Manifest.permission.POST_NOTIFICATIONS
        val checkingRunCommandPermission =
            ContextCompat.checkSelfPermission(
                activity,
                postNotifications
            )
        if(
            checkingRunCommandPermission ==
            PackageManager.PERMISSION_GRANTED
        ) {
            FragmentStartHandler.handle(
                activity
            )
            return
        }
        activity.getNotifierSetterLaunch.launch(
            postNotifications
        )
    }

    fun set(
        activity: MainActivity
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if(isGranted){
                FragmentStartHandler.handle(
                    activity
                )
                return@registerForActivityResult
            }
            ActivityFinisher.finish(activity)
        }
    }
}
