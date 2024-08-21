package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment

object FragmentTagManager {


    private const val separateUnderBar = "___"
    private const val fannelStateIndex = 3


    fun makeCmdValEditTag(
//        parentAppDirPath: String,
        scriptFileName: String,
        fannelState: String,
    ): String {
        return makeTag(
            FragmentTagPrefix.Prefix.CMD_VAL_EDIT_PREFIX.str,
            scriptFileName,
            fannelState,
        )
    }

    fun makeSettingValEditTag(
//        parentAppDirPath: String,
        scriptFileName: String,
        fannelState: String = String(),
    ): String {
        return makeTag(
            FragmentTagPrefix.Prefix.SETTING_VAL_EDIT_PREFIX.str,
//            parentAppDirPath,
            scriptFileName,
            fannelState,
        )
    }

    private fun makeTag(
        prefix: String,
//        parentAppDirPath: String,
        scriptFileName: String,
        fannelState: String = String(),
    ): String {
        return listOf(
            prefix,
//            parentAppDirPath,
            scriptFileName,
            fannelState,
        ).joinToString(separateUnderBar)
    }

    fun execGetStateFromEditFragTag(
        editFragment: EditFragment,
    ): String {
        val tag = editFragment.tag
            ?: return String()
        return tag.split(separateUnderBar).getOrNull(fannelStateIndex)
            ?: String()

    }
}