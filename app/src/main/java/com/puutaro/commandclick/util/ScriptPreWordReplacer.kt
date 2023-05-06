package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.UsePath

object ScriptPreWordReplacer {
    fun replace(
        tergetString: String,
        currentScriptPath: String,
        currentAppDirPath: String,
        fannelDirName: String,
        currentScriptName: String
    ): String {
        return tergetString
            .replace("\${0}", currentScriptPath)
            .replace("\${00}", UsePath.cmdclickDirPath)
            .replace("\${01}", currentAppDirPath)
            .replace("\${001}", fannelDirName)
            .replace("\${02}", currentScriptName)
    }
}