package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.UsePath

class UpdatelastModifyForEdit {
    private val guardDirPathList = listOf(
        UsePath.cmdclickAppDirAdminPath,
        UsePath.cmdclickConfigDirPath,
        UsePath.cmdclickSystemAppDirPath
    )
    fun judge(
        currentAppDirPath: String
    ): Boolean {
        return guardDirPathList.indexOf(currentAppDirPath).let {
            it == -1
        }
    }
}