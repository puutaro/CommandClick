package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath

object CcPathTool {
    fun makeFannelDirName(
        fannelName: String
    ): String {
        return fannelName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
    }
}