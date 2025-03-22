package com.puutaro.commandclick.util.sd

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.AltRegexTool

object SdPath {
    const val rootfsRelativePath = "${UsePath.cmdclickDirName}/ubuntu/backup/rootfs"

    fun getSdUseRootPath(): String {
        return ReadText(
            UsePath.sdRootDirTxtPath
        ).readText()
            .split("\n")
            .firstOrNull()
            ?.let {
                AltRegexTool.trim(it)
            }
            ?: String()
    }
}