package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment

object EditFragmentTitle {
    fun make(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String
    ): String {
        val backstackOrder =
            editFragment
                .activity
                ?.supportFragmentManager
                ?.getBackStackEntryCount()
                ?: 0
        return "(${backstackOrder}) " +
                "${UsePath.makeOmitPath(currentAppDirPath)}/${currentScriptFileName}"
    }
}