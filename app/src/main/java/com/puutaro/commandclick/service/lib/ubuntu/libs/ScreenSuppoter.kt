package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ScreenMonitor {

    fun killInMonitorOff(
        ubuntuService: UbuntuService
    ){
        val ubuntuRunningProcessNum =
            ProcessManager.UbuntuRunningSystemProcessType.values().size
        val noSleepSignal = 0L
        val settingSectionStart = ubuntuService.settingSectionStart
        val settingSectionEnd = ubuntuService.settingSectionEnd
        ubuntuService.monitorScreenJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val sleepDelayMinutes = makeSleepDelayMinutes(
                    settingSectionStart,
                    settingSectionEnd
                )
                if(sleepDelayMinutes == noSleepSignal) return@withContext
                delay(sleepDelayMinutes)
                val processNum = ProcessManager.processNumCalculator(ubuntuService)
                if (
                    processNum > ubuntuRunningProcessNum
                ) return@withContext
                ubuntuService.screenOffKill = true
                killFrontProcess(ubuntuService)
                killSubFrontProcess(ubuntuService)
            }
        }
    }

    fun launchScreenRestart(
        ubuntuService: UbuntuService
    ){
        if(!ubuntuService.screenOffKill) return
        ubuntuService.notificationBuilderHashMap.clear()
        ubuntuService.monitorScreenJob?.cancel()
        ProcessManager.removeLaunchCompFile(ubuntuService)
        val sleepingIntent = Intent()
        sleepingIntent.action = BroadCastIntentScheme.ON_SLEEPING_NOTIFICATION.action
        ubuntuService.sendBroadcast(sleepingIntent)
        ubuntuService.screenOffKill = false
    }


    private fun makeSleepDelayMinutes(
        settingSectionStart: String,
        settingSectionEnd: String
    ): Long {
        val defaultDelaySleepTime = 20L
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            CommandClickVariables.makeScriptContentsList(
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

    private fun killFrontProcess(
        ubuntuService: UbuntuService
    ){
        ubuntuService.ubuntuFiles?.let {
            BusyboxExecutor(ubuntuService.applicationContext, it).executeKillFrontProcess(
                ubuntuService.cmdclickMonitorFileName
            )
        }
    }

    private fun killSubFrontProcess(
        ubuntuService: UbuntuService
    ){
        ubuntuService.ubuntuFiles?.let {
            BusyboxExecutor(ubuntuService.applicationContext, it).executeKillSubFrontProcess(
                ubuntuService.cmdclickMonitorFileName
            )
        }
    }
}