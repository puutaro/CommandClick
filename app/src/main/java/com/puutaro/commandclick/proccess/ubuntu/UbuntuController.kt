package com.puutaro.commandclick.proccess.ubuntu

import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.shell.LinuxCmd
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
            ToastUtils.showShort("Launch ubuntu")
            return
        }
        val monitorFileName = UsePath.decideMonitorName(monitorNum)
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeUbuntu.BACKGROUND_CMD_START.action,
            sequenceOf(
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
            ToastUtils.showShort("Setup ubuntu")
            return
        }
        if(
            LinuxCmd.isBasicProcess(context)
        ){
            return
        }
        boot(fragment)
        withContext(Dispatchers.IO) {
            for (i in 1..50) {
                withContext(Dispatchers.Main) toast@ {
                    if(i % 5 != 0) return@toast
                    ToastUtils.showShort("boot${".".repeat(i / 10 + 1)}")
                }
                if (
                    LinuxCmd.isBasicProcess(context)
                ) {
                    isBootSuccess = true
                    break
                }
                delay(300)
            }
        }
        if(!isBootSuccess) {
            ToastUtils.showShort("boot failure")
            return
        }
        ToastUtils.showShort("boot ok")
    }
}