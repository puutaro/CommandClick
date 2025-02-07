package com.puutaro.commandclick.proccess.ubuntu

import android.content.Context
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.shell.LinuxCmd
import kotlinx.coroutines.delay
import java.io.File

object ResAndProcess {
    suspend fun wait(
        context: Context?,
        resFilePath: String,
        procName: String,
    ){
        var beforeLength = 0L
        val resFilePathObj = File(resFilePath)
        val twoSec = 2000
        val waitMilliSec = 200L
        val waitTimes = 200
        for (i in 1..waitTimes) {
            delay(waitMilliSec)
            val soFarWaitTime = i * waitMilliSec
            if(
                soFarWaitTime > twoSec
                && !LinuxCmd.isProcessCheck(
                    context,
                    procName
                )
            ) break
            if (
                !resFilePathObj.isFile
            ) {
                continue
            }
            val resFileLen = resFilePathObj.length()
            if(beforeLength != resFileLen){
                beforeLength = resFileLen
                continue
            }
            if(
                LinuxCmd.isProcessCheck(
                    context,
                    procName
                )
            ) continue
            break
        }
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeUbuntu.CMD_KILL_BY_ADMIN.action,
            sequenceOf(
                UbuntuServerIntentExtra.ubuntuCroutineJobTypeListForKill.schema to
                        procName
            )
        )
    }
}