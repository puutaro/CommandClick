package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.app.PendingIntent
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.service.lib.PendingIntentCreator

object PendingIntentForUbuntu {
    fun createOpenTerminal(
        ubuntuService: UbuntuService
    ): PendingIntent {
        return PendingIntentCreator.create(
            ubuntuService.applicationContext,
            BroadCastIntentScheme.OPEN_FANNEL.action,
            listOf(
                Pair(
                    UbuntuServerIntentExtra.fannelDirPath.schema,
                    UsePath.cmdclickSystemAppDirPath
                ),
                Pair(
                    UbuntuServerIntentExtra.fannelName.schema,
                    SystemFannel.cmdTerminal
                ),
            )
        )
    }
}