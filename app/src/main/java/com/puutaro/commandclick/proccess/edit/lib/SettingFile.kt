package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText

object SettingFile {

    fun read(
        settingFileDirPath: String,
        settingFileName: String,
    ): String {
        return ReadText(
            settingFileDirPath,
            settingFileName
        ).textToList().map{
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