package com.puutaro.commandclick.activity_lib

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.activity.MainActivity


object NotifierSetter {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getPermissionAndSet(
        activity: MainActivity
    ){
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
            return
        }
        try {
            activity.getNotifierSetterLaunch.launch(
                postNotifications
            )
        } catch (e: Exception){
            return
        }
    }

    fun set(
        activity: MainActivity
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                return@registerForActivityResult
            }
            return@registerForActivityResult
        }
    }
}
