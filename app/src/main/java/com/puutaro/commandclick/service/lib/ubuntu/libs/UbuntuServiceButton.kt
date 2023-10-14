package com.puutaro.commandclick.service.lib.ubuntu.libs

import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.ubuntu.ButtonLabel

object UbuntuServiceButton {
    fun addOpenTerminal(
        ubuntuService: UbuntuService,
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        ubuntuService.notificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.ic_terminal,
            ButtonLabel.TERMINAL.label,
            PendingIntentForUbuntu.createOpenTerminal(ubuntuService)
        )
    }

    fun addManager(
        ubuntuService: UbuntuService,
    ){
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuLaunchCompFile?.isFile != true
        ) return
        ubuntuService.notificationBuilder?.addAction(
            com.puutaro.commandclick.R.drawable.icons8_support,
            ButtonLabel.BACKUP.label,
            PendingIntentForUbuntu.createManager(ubuntuService)
        )
    }
}