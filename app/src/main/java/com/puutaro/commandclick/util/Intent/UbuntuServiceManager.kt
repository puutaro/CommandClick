package com.puutaro.commandclick.util.Intent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
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
                    File(
                        cmdclickTempUbuntuServiceDirPath,
                        cmdclickTmpUbuntuServiceActiveFileName
                    ).absolutePath
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
                else LinuxCmd.isBasicProcess(activity)

                if(
                    cmdclickTmpUbuntuServiceActiveFile.isFile
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