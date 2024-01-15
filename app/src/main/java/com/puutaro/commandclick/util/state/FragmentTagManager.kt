package com.puutaro.commandclick.util.state

object FragmentTagManager {

    enum class Prefix(

        val str: String
    ) {
        CMD_EDIT_PREFIX("cmd_edit"),
        SETTING_EDIT_PREFIX("setting_edit"),
    }

    private const val separateUnderBar = "___"

    fun makeCmdValEditTag(
        parentAppDirPath: String,
        scriptFileName: String,
    ): String {
        return makeTag(
            Prefix.CMD_EDIT_PREFIX.str,
            parentAppDirPath,
            scriptFileName,
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
    ): String {
        return listOf(
            prefix,
            parentAppDirPath,
            scriptFileName,
        ).joinToString(separateUnderBar)
//                mode
    }
}