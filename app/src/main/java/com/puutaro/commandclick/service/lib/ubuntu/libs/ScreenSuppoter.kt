package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ScreenMonitor {

    fun killInMonitorOff(
        ubuntuService: UbuntuService
    ){
        FileSystems.writeFile(
            File(
                UsePath.cmdclickTempUbuntuServiceDirPath,
                UsePath.cmdclickTmpUbuntuMonitorOff
            ).absolutePath,
            String()
        )
        val ubuntuRunningProcessNum =
            ProcessManager.UbuntuRunningSystemProcessType.values().size
        val noSleepSignal = 0L
        val settingSectionStart = ubuntuService.settingSectionStart
        val settingSectionEnd = ubuntuService.settingSectionEnd
        val ubuntuLaunchCompFile = ubuntuService.ubuntuFiles?.ubuntuLaunchCompFile
            ?: return
        ubuntuService.monitorScreenJob?.cancel()
        ubuntuService.monitorScreenJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val sleepDelayMinutes = makeSleepDelayMinutes(
                    settingSectionStart,
                    settingSectionEnd
                )
                if(sleepDelayMinutes == noSleepSignal) {
                    return@withContext
                }
                delay(sleepDelayMinutes)
                if (
                    !ubuntuLaunchCompFile.isFile
                ) return@withContext
                val processNum = ProcessManager.processNumCalculator(ubuntuService)
                if (
                    processNum > ubuntuRunningProcessNum
                ) return@withContext
                ubuntuService.screenOffKill = true
                ProcessManager.finishProcessForSleep(ubuntuService)
//                LinuxCmd.killProcess(ubuntuService.packageName)
//                killFrontProcess(ubuntuService)
//                killSubFrontProcess(ubuntuService)
            }
        }
    }

    fun launchScreenRestart(
        ubuntuService: UbuntuService
    ){
        FileSystems.removeFiles(
            File(
                UsePath.cmdclickTempUbuntuServiceDirPath,
                UsePath.cmdclickTmpUbuntuMonitorOff
            ).absolutePath
        )
        ubuntuService.monitorScreenJob?.cancel()
        if(!ubuntuService.screenOffKill) return
        ubuntuService.notificationBuilderHashMap.clear()
        ProcessManager.removeLaunchCompFile(ubuntuService)
        val sleepingIntent = Intent()
        sleepingIntent.action = BroadCastIntentSchemeUbuntu.ON_SLEEPING_NOTIFICATION.action
        ubuntuService.sendBroadcast(sleepingIntent)
        ubuntuService.screenOffKill = false
    }


    private fun makeSleepDelayMinutes(
        settingSectionStart: String,
        settingSectionEnd: String
    ): Long {
        val defaultDelaySleepTime = 20L
        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            CommandClickVariables.makeMainFannelConList(
                UsePath.cmdclickSystemAppDirPath,
                UsePath.cmdclickConfigFileName
            ),
            settingSectionStart,
            settingSectionEnd
        )
        val sleepDelayMinutesStr = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF,
            defaultDelaySleepTime.toString()
        )
        return try{
            convertMiliSecToMinutes(
                sleepDelayMinutesStr.toLong()
            )
        } catch(e: Exception){
            convertMiliSecToMinutes(
                defaultDelaySleepTime
            )
        }
    }

    private fun convertMiliSecToMinutes(miliTime: Long): Long {
        return miliTime * 1000 * 60
    }

//    private fun killFrontProcess(
//        ubuntuService: UbuntuService
//    ){
//        LinuxCmd.killFrontProcess(ubuntuService.packageName)
//    }
//
//    private fun killSubFrontProcess(
//        ubuntuService: UbuntuService
//    ){
//        LinuxCmd.killSubFrontProcess(ubuntuService.packageName)
//    }
}