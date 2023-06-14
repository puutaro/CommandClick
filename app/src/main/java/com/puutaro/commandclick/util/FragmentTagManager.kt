package com.puutaro.commandclick.util.Intent

object FragmentTagManager {

    enum class Prefix(
        val str: String
    ) {
        indexPrefix("index"),
        cmdEditPrefix("cmd_edit"),
        settingEditPrefix("setting_edit"),
    }

    enum class Suffix(
        val str: String
    ) {
        ON("ON"),
        OFF("OFF"),
    }
    private val sepalateUnderBar = "___"
    val prefixIndex = 0
    val parentAppDirPathIndex = 1
    val scriptFileNameIndex = 2
    val modeIndex = 3

    fun makeTag(
        prefix: String,
        parentAppDirPath: String,
        scriptFileName: String,
        mode: String,
    ): String {
        return "${prefix}${sepalateUnderBar}" +
                "${parentAppDirPath}${sepalateUnderBar}" +
                "${scriptFileName}${sepalateUnderBar}" +
                mode
    }

    fun makeListFromTag(
        tag: String,
    ): List<String> {
        return tag.split(sepalateUnderBar)
    }
}