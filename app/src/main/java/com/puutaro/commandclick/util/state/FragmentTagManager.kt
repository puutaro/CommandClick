package com.puutaro.commandclick.util.state

object FragmentTagManager {

    enum class Prefix(
        
        val str: String
    ) {
        cmdEditPrefix("cmd_edit"),
        settingEditPrefix("setting_edit"),
    }

    enum class OnShortcutSuffix(
        val str: String
    ) {
        ON("ON"),
        OFF("OFF"),
    }
    private val sepalateUnderBar = "___"

    fun makeTag(
        prefix: String,
        parentAppDirPath: String,
        scriptFileName: String,
        mode: String,
    ): String {
        return "${prefix}$sepalateUnderBar" +
                "${parentAppDirPath}$sepalateUnderBar" +
                "${scriptFileName}$sepalateUnderBar" +
                mode
    }
}