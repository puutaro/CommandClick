package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object LayoutSettingFile {

    fun read(
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        onImport: Boolean = true
    ): List<String> {
        return SettingFile.readLayout(
            settingFilePath,
            fannelPath,
            setReplaceVariableCompleteMap,
            onImport
        ).let {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "layoutSettingFile.txt").absolutePath,
//                listOf(
//                    "settingFile: ${it}",
//                ).joinToString("\n") + "\n------\n"
//            )
            QuoteTool.layoutSplitBySurroundedIgnore(
                it,
            )
        }
    }

    fun readFromList(
        firstSettingConList: List<String>,
        fannelName: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        onImport: Boolean = true
    ): List<String> {
        return SettingFile.readLayoutFromList(
            firstSettingConList,
            fannelName,
            setReplaceVariableCompleteMap,
            onImport
        ).let {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "layoutSettingFile.txt").absolutePath,
//                listOf(
//                    "settingFile: ${it}",
//                ).joinToString("\n") + "\n------\n"
//            )
            QuoteTool.layoutSplitBySurroundedIgnore(
                it,
            )
        }
    }


}