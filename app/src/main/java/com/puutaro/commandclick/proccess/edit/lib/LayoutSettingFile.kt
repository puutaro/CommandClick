package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.util.str.QuoteTool

object LayoutSettingFile {

    fun read(
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        onImport: Boolean = true
    ): List<String> {
        return SettingFile.read(
            settingFilePath,
            fannelPath,
            setReplaceVariableCompleteMap,
            onImport
        ).let {
            QuoteTool.layoutSplitBySurroundedIgnore(
                it,
            )
        }
    }


}