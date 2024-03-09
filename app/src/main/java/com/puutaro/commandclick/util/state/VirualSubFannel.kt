package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.util.CcPathTool

object VirtualSubFannel {
    fun makePath(
        readSharePreferenceMap: Map<String, String>,
    ): String {
        val fannelDirName = CcPathTool.makeFannelDirName(
            SharePrefTool.getCurrentFannelName(readSharePreferenceMap),
        )
        return listOf(
            SharePrefTool.getCurrentAppDirPath(readSharePreferenceMap),
            fannelDirName,
            "virtualSubFannel.js",
        ).joinToString("/")
    }
}