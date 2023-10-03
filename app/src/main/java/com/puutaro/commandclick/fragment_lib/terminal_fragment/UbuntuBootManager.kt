package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager

object UbuntuBootManager {
    fun boot(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
            ?: return
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuSetupCompFile.isFile
        ) return
        UbuntuServiceManager.monitoringAndLaunchUbuntuService(
            terminalFragment.activity,
            false,
            true
        )
    }
}