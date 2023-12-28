package com.puutaro.commandclick.proccess.ubuntu

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.intent.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.LinuxCmd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object UbuntuController {

    fun execScriptByBackground(
        fragment: Fragment,
        backgroundShellPath: String,
        argsTabSepaStr:String,
        monitorNum: Int,
    ){
        val context = fragment.context
            ?: return
        if(
            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
        ) {
            Toast.makeText(
                context,
                "Launch ubuntu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val monitorFileName = UsePath.decideMonitorName(monitorNum)
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeUbuntu.BACKGROUND_CMD_START.action,
            listOf(
                UbuntuServerIntentExtra.backgroundShellPath.schema to
                        backgroundShellPath,
                UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema to
                        argsTabSepaStr,
                UbuntuServerIntentExtra.backgroundMonitorFileName.schema to
                        monitorFileName
            )
        )
    }

    fun boot(
        fragment: Fragment,
    ){
        val context = fragment.context
            ?: return
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuSetupCompFile.isFile
        ) return
        UbuntuServiceManager.monitoringAndLaunchUbuntuService(
            fragment.activity,
            false,
            true
        )
    }
    suspend fun bootWait(
        fragment: Fragment,
    ){
        val context = fragment.context
            ?: return
        var isBootSuccess = false
        if(
            !UbuntuFiles(context).ubuntuSetupCompFile.isFile
        ){
            Toast.makeText(
                context,
                "Setup ubuntu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if(
            LinuxCmd.isBasicProcess()
        ){
            return
        }
        boot(fragment)
        withContext(Dispatchers.IO) {
            for (i in 1..50) {
                withContext(Dispatchers.Main) toast@ {
                    if(i % 5 != 0) return@toast
                    Toast.makeText(
                        context,
                        "boot${".".repeat(i / 10 + 1)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (
                    LinuxCmd.isBasicProcess()
                ) {
                    isBootSuccess = true
                    break
                }
                delay(300)
            }
        }
        if(!isBootSuccess) {
            Toast.makeText(
                context,
                "boot failure",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        Toast.makeText(
            context,
            "boot ok",
            Toast.LENGTH_SHORT
        ).show()
    }
}