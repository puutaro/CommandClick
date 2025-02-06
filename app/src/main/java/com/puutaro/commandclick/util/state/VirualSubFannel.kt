package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.CcPathTool

object VirtualSubFannel {
    fun makePath(
        fannelInfoMap: HashMap<String, String>,
    ): String {
        val fannelDirName = CcPathTool.makeFannelDirName(
            FannelInfoTool.getCurrentFannelName(fannelInfoMap),
        )
        return listOf(
            UsePath.cmdclickDefaultAppDirPath,
            fannelDirName,
            "virtualSubFannel.js",
        ).joinToString("/")
    }
}