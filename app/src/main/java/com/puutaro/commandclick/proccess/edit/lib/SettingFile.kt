package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.ReadText

object SettingFile {

    fun read(
        settingFilePath: String,
    ): String {
        return ReadText(
            settingFilePath
        ).textToList().let {
            formSettingContents(it)
        }
    }

    fun formSettingContents(
        settingCon: List<String>
    ): String {
        return settingCon.map{
            it.trim()
        }.filter {
            it.isNotEmpty()
                    && !it.startsWith("#")
                    && !it.startsWith("//")
        }.joinToString("").let {
            QuoteTool.removeDoubleQuoteByIgnoreBackSlash(
                it
            )
        } ?: String()
    }
}