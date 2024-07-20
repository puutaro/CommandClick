package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.util.CcPathTool

object VirtualSubFannel {
    fun makePath(
        fannelInfoMap: Map<String, String>,
    ): String {
        val fannelDirName = CcPathTool.makeFannelDirName(
            FannelInfoTool.getCurrentFannelName(fannelInfoMap),
        )
        return listOf(
            FannelInfoTool.getCurrentAppDirPath(fannelInfoMap),
            fannelDirName,
            "virtualSubFannel.js",
        ).joinToString("/")
    }
}