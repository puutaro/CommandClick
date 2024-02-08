package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.fragment.EditFragment

object FragmentTagManager {

    enum class Prefix(

        val str: String
    ) {
        CMD_EDIT_PREFIX("cmd_edit"),
        SETTING_EDIT_PREFIX("setting_edit"),
    }

    private const val separateUnderBar = "___"
    private const val fannelStateIndex = 3


    fun makeCmdValEditTag(
        parentAppDirPath: String,
        scriptFileName: String,
        fannelState: String,
    ): String {
        return makeTag(
            Prefix.CMD_EDIT_PREFIX.str,
            parentAppDirPath,
            scriptFileName,
            fannelState,
        )
    }

    fun makeSettingValEditTag(
        parentAppDirPath: String,
        scriptFileName: String,
    ): String {
        return makeTag(
            Prefix.SETTING_EDIT_PREFIX.str,
            parentAppDirPath,
            scriptFileName,
        )
    }

    private fun makeTag(
        prefix: String,
        parentAppDirPath: String,
        scriptFileName: String,
        fannelState: String = String(),
    ): String {
        return listOf(
            prefix,
            parentAppDirPath,
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