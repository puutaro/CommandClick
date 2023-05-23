package com.puutaro.commandclick.activity_lib.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.InitManager
import kotlinx.coroutines.*

object NotifierSetter {

    private var onPermissionResponse = false
    fun getPermissionAndSet(
        activity: MainActivity
    ){
        if(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) return
        val postNotifications = Manifest.permission.POST_NOTIFICATIONS
        val checkingRunCommandPermission =
            ContextCompat.checkSelfPermission(
                activity,
                postNotifications
            )
        if(
            checkingRunCommandPermission ==
            PackageManager.PERMISSION_GRANTED
        ) return
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main){
                onPermissionResponse = false
                activity.getNotifierSetterLaunch.launch(
                    postNotifications
                )
            }
            withContext(Dispatchers.Main){
                while (true) {
                    if (onPermissionResponse) break
                    delay(200)
                }
            }
        }
    }

    fun set(
        activity: MainActivity
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            onPermissionResponse = true
            InitManager.onNotificationPermissionResponse = true
        }
    }
}
