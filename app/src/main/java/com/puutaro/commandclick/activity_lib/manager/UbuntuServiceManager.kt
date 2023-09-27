package com.puutaro.commandclick.activity_lib.manager

import android.content.Intent
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object UbuntuServiceManager {

    fun monitoringAndLaunchUbuntuService(
        activity: MainActivity,
        onInitDelay: Boolean
    ): Boolean {
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
                ubuntuIntent.action = BroadCastIntentScheme.IS_ACTIVE_UBUNTU_SERVICE.action
                for(i in 1..3) {
                    activity.sendBroadcast(ubuntuIntent)
                    delay(400)
                    if (
                        cmdclickTmpUbuntuServiceActiveFile.isFile
                    ) break
                }
                if(cmdclickTmpUbuntuServiceActiveFile.isFile) return@withContext
                launch(activity)
            }
        }
        return false
    }

    private fun launch(
        activity: MainActivity,
    ){
        val intent = Intent(
            activity,
            UbuntuService::class.java
        )
        ContextCompat.startForegroundService(activity, intent)
    }


}