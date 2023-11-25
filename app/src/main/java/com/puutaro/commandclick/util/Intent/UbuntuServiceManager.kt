package com.puutaro.commandclick.util.Intent

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.intent.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.LinuxCmd
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
        if(activity == null) return
        val cmdclickTempUbuntuServiceDirPath = UsePath.cmdclickTempUbuntuServiceDirPath
        val cmdclickTmpUbuntuServiceActiveFileName = UsePath.cmdclickTmpUbuntuServiceActiveFileName
        val cmdclickTmpUbuntuServiceActiveFile = File("${cmdclickTempUbuntuServiceDirPath}/${cmdclickTmpUbuntuServiceActiveFileName}")
        CoroutineScope(Dispatchers.IO).launch {

            withContext(Dispatchers.IO) {
                if(!onInitDelay) return@withContext
                delay(8000)
            }
            withContext(Dispatchers.IO){
                FileSystems.removeFiles(
                    cmdclickTempUbuntuServiceDirPath,
                    cmdclickTmpUbuntuServiceActiveFileName
                )
            }
            withContext(Dispatchers.IO){
                delay(200)
                val ubuntuIntent = Intent()
                ubuntuIntent.action = BroadCastIntentSchemeUbuntu.IS_ACTIVE_UBUNTU_SERVICE.action
                for(i in 1..3) {
                    activity.sendBroadcast(ubuntuIntent)
                    delay(300)
                    if (
                        cmdclickTmpUbuntuServiceActiveFile.isFile
                    ) break
                }
                val isBasicProcess = if(
                    !onBasicProcessMonitor
                ) true
                else LinuxCmd.isBasicProcess()

                if(
                    cmdclickTmpUbuntuServiceActiveFile.isFile
                    && isBasicProcess
                ) return@withContext
                launch(activity)
            }
        }
    }

    fun launch(
        activity: Activity?,
    ){
        if(activity == null) return
        val intent = Intent(
            activity,
            UbuntuService::class.java
        )
        killAllProcess(activity)
        intent.putExtra(UbuntuServerIntentExtra.ubuntuStartCommand.schema, "on")
        CoroutineScope(Dispatchers.IO).launch{
            withContext(Dispatchers.IO){
                activity.stopService(intent)
                delay(300)
            }
            withContext(Dispatchers.IO){
                ContextCompat.startForegroundService(activity, intent)
            }
        }
    }


    private fun killAllProcess(
        activity: Activity?
    ){
        if(activity == null) return
        LinuxCmd.killProcess(activity.packageName)
    }
}