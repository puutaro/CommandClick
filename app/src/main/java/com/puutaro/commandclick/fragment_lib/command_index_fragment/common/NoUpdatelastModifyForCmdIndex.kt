package com.puutaro.commandclick.fragment_lib.command_index_fragment.common

import com.puutaro.commandclick.common.variable.path.UsePath

class NoUpdatelastModifyForCmdIndex {

    private val guardDirPathList = listOf(
        UsePath.cmdclickAppHistoryDirAdminPath,
        UsePath.cmdclickAppDirAdminPath,
        UsePath.cmdclickSystemAppDirPath
    )
    fun judge(
        currentAppDirPath: String
    ): Boolean {
        return guardDirPathList.indexOf(currentAppDirPath).let {
            it != -1
        }
    }
}