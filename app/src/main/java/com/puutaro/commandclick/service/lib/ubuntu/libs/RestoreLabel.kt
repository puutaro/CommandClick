package com.puutaro.commandclick.service.lib.ubuntu.libs

import com.puutaro.commandclick.service.UbuntuService

object RestoreLabel {

    private val restorePrefix = "[Re]"

    fun decide(
        ubuntuService: UbuntuService,
        titleEntry: String
    ): String {
        return when(ubuntuService.isUbuntuRestore) {
            true -> "${restorePrefix} ${titleEntry}"
            false -> titleEntry
        }
    }
}