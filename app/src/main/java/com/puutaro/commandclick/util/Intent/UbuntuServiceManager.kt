package com.puutaro.commandclick.util.Intent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ServiceUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.NotifierSetter
import com.puutaro.commandclick.activity_lib.permission.StorageAccessSetter
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.shell.LinuxCmd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object UbuntuServiceManager {

    fun monitoringAndLaunchUbuntuService(
        activity: Activity?,
        onInitDelay: Boolean,
        onBasicProcessMonitor: Boolean
    ) {
        if(
            activity == null
        ) return
        CoroutineScope(Dispatchers.IO).launch {

            val isLaunch = withContext(Dispatchers.IO) {
                if(
                    activity !is MainActivity
                ) return@withContext true
                for(i in 1..300) {
                    val isGranted =
                        StorageAccessSetter.checkPermissionGranted(activity)
                            && NotifierSetter.checkPermission(activity)
                    if(
                        isGranted
                    ) return@withContext true
                    delay(1000)
                }
                return@withContext false
            }
            if(!isLaunch) return@launch
//            withContext(Dispatchers.IO){
//                if(
//                    !onInitDelay
//                ) return@withContext
//                delay(8000)
//            }
            withContext(Dispatchers.IO){
                delay(200)
                var isUbuntuService = false
                for(i in 1..3) {
                    delay(300)
                    isUbuntuService =
                        ServiceUtils.isServiceRunning(UbuntuService::class.java)
                    if(
                        isUbuntuService
                    ) break
                }
                val isBasicProcess = if(
                    !onBasicProcessMonitor
                ) true
                else LinuxCmd.isBasicProcess(activity)

                if(
                    isUbuntuService
                    && isBasicProcess
                ) return@withContext
                launch(activity)
            }
        }
    }


    fun launchByNoCoroutine(context: Context?): Boolean {
        if(
            context == null
        ) return false
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuSetupCompFile.isFile
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "Setup ubuntu",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            return  false
        }
        val isBasicProcess =
            LinuxCmd.isBasicProcess(context)
        if(
            isBasicProcess
        ) return true
        launch(context)
        return true
    }

    fun launch(
        context: Context?,
    ){
        if(context == null) return
        val intent = Intent(
            context,
            UbuntuService::class.java
        )
        killAllProcess(context)
        intent.putExtra(UbuntuServerIntentExtra.ubuntuStartCommand.schema, "on")
        CoroutineScope(Dispatchers.IO).launch{
            withContext(Dispatchers.IO){
                context.stopService(intent)
                delay(300)
            }
            withContext(Dispatchers.IO){
                ContextCompat.startForegroundService(context, intent)
            }
        }
    }


    private fun killAllProcess(
        context: Context?
    ){
        if(context == null) return
        LinuxCmd.killProcess(context)
    }
}