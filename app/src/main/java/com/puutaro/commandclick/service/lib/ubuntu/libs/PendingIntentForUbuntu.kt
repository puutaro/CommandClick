package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.app.PendingIntent
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.PendingIntentCreator

object PendingIntentForUbuntu {
    fun createOpenTerminal(
        ubuntuService: UbuntuService
    ): PendingIntent {
        return PendingIntentCreator.create(
            ubuntuService.applicationContext,
            BroadCastIntentSchemeUbuntu.OPEN_FANNEL.action,
            listOf(
//                Pair(
//                    UbuntuServerIntentExtra.fannelDirPath.schema,
//                    UsePath.cmdclickDefaultAppDirPath
//                ),
                Pair(
                    UbuntuServerIntentExtra.fannelName.schema,
                    SystemFannel.tapTerminal
                ),
            )
        )
    }

    fun createManager(
        ubuntuService: UbuntuService
    ): PendingIntent {
        return PendingIntentCreator.create(
            ubuntuService.applicationContext,
            BroadCastIntentSchemeUbuntu.FOREGROUND_CMD_START.action,
            listOf(
                Pair(
                    UbuntuServerIntentExtra.foregroundShellPath.schema,
                    ubuntuService.ubuntuFiles?.ubuntuManagerShellPath?.absolutePath
                        ?: String()
                ),
                Pair(
                    UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema,
                    String()
                ),
                Pair(
                    UbuntuServerIntentExtra.foregroundTimeout.schema,
                    "2000"
                ),
            )
        )
    }
}