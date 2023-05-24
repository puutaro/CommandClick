package com.puutaro.commandclick.activity_lib

import android.Manifest
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecRestartIntent
import com.puutaro.commandclick.activity_lib.manager.FragmentStartHandler
import com.puutaro.commandclick.activity_lib.permission.NotifierSetter
import com.puutaro.commandclick.activity_lib.permission.StorageAccessSetter
import kotlinx.coroutines.*


class InitManager(
    private val activity: MainActivity,
) {

    companion object {
        var onStorageAccessPermissionResponse = false
        var onNotificationPermissionResponse = false
    }

    fun invoke(){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                onStorageAccessPermissionResponse = false
                StorageAccessSetter.storageAccessProcess(
                    activity
                )
            }
            withContext(Dispatchers.Main){
                while (true) {
                    if (
                        onStorageAccessPermissionResponse
                    ) break
                    delay(200)
                }
            }
            withContext(Dispatchers.Main){
                delay(10)
                when(
                    StorageAccessSetter.checkPermissionGranted(
                        activity
                    )
                ){
                    PackageManager.PERMISSION_GRANTED
                    -> {}
                    else -> {
                        val mngr = activity.getSystemService(
                            Context.ACTIVITY_SERVICE
                        ) as? ActivityManager
                        if (mngr == null) return@withContext
                        mngr.appTasks.forEach {
                            it.finishAndRemoveTask()
                        }
                        ExecRestartIntent.execRestartIntent(activity)
                    }
                }
            }
            if(
                StorageAccessSetter.checkPermissionGranted(
                    activity
                ) != PackageManager.PERMISSION_GRANTED
            ) return@launch
            withContext(Dispatchers.Main) {
                onNotificationPermissionResponse = false
                NotifierSetter.getPermissionAndSet(
                    activity
                )
            }
            withContext(Dispatchers.Main){
                if (
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                ) return@withContext
                while(true) {
                    if (
                        onNotificationPermissionResponse
                    ) break
                    delay(200)
                }
            }
            withContext(Dispatchers.Main){
                when(
                    ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    PackageManager.PERMISSION_GRANTED -> {
                        FragmentStartHandler.handle(activity)
                    }
                    else -> activity.finish()
                }
            }
        }
    }
}
