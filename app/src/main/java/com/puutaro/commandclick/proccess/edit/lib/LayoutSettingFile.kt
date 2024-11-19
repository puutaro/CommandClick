package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object LayoutSettingFile {

    fun read(
        context: Context?,
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
    ): List<String> {
        return SettingFile.readLayout(
            context,
            settingFilePath,
            fannelPath,
            setReplaceVariableCompleteMap,
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
        context: Context?,
        firstSettingConList: List<String>,
        fannelName: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
    ): List<String> {
        return SettingFile.readLayoutFromList(
            context,
            firstSettingConList,
            fannelName,
            setReplaceVariableCompleteMap,
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