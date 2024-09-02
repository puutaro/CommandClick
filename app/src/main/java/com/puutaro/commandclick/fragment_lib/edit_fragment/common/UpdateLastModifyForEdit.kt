package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment

object UpdateLastModifyForEdit {
//    private val guardDirPathList = listOf(
//        UsePath.cmdclickAppDirAdminPath,
//    )
    fun judge(
        editFragment: EditFragment,
//        currentAppDirPath: String,
    ): Boolean {
//        if(
//            guardDirPathList.indexOf(
//                currentAppDirPath
//            ) != -1
//        ) return false
        return editFragment.onUpdateLastModify
    }
}