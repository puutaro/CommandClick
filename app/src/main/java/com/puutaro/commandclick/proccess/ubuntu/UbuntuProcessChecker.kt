package com.puutaro.commandclick.proccess.ubuntu

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.shell.LinuxCmd

object UbuntuProcessChecker {
    fun isExist(
        context: Context?,
        ubuntuFilesSrc: UbuntuFiles?
    ): Boolean {
        if(
            context == null
        ) return false
        val ubuntuFiles = when(ubuntuFilesSrc == null){
            true -> UbuntuFiles(context)
            false -> ubuntuFilesSrc
        }
        if (
            !ubuntuFiles.ubuntuLaunchCompFile.isFile
        ) {
            ToastUtils.showShort("Launch ubuntu")
            return false
        }
        if(
            !LinuxCmd.isBasicProcess(context)
        ) {
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeUbuntu.STOP_UBUNTU_SERVICE.action
            )
            return false
        }
        return true
    }
}