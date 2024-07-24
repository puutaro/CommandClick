package com.puutaro.commandclick.util.file

import com.puutaro.commandclick.common.variable.path.UsePath
import java.io.File

object SdPath {
    const val rootfsRelativePath = "${UsePath.cmdclickDirName}/ubuntu/backup/rootfs"

    fun getSdUseRootPath(): String {
        return ReadText(
            UsePath.sdRootDirTxtPath
        ).readText()
            .split("\n")
            .firstOrNull()
            ?.trim()
            ?: String()
    }
}