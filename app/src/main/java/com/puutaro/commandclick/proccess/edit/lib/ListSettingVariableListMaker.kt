package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SettingVariableReader
import java.io.File

object ListSettingVariableListMaker {
    fun make(
        settingVariableName: String,
        currentAppDirPath: String,
        currentScriptFileName: String,
        fannelDirName: String,
        scriptContentsList: List<String>,
        settingSectionStart: String,
        settingSectionEnd: String,
    ): List<String> {
        val settingVariables = CommandClickVariables.substituteVariableListFromHolder(
            scriptContentsList,
            settingSectionStart,
            settingSectionEnd
        )?.joinToString("\n")?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                fannelDirName,
                currentScriptFileName,
            )
        }?.split("\n")
            ?: emptyList()
        return makeFromSettingVariableList(
            settingVariableName,
            currentAppDirPath,
            currentScriptFileName,
            fannelDirName,
            settingVariables
        )
    }

    fun makeFromSettingVariableList(
        settingVariableName: String,
        currentAppDirPath: String,
        currentScriptFileName: String,
        fannelDirName: String,
        settingVariablesList: List<String>,
    ): List<String> {
        val filePrefix = EditSettings.filePrefix
        val listSettingVariableListSource =
            SettingVariableReader.getStrListByReplace(
            settingVariablesList,
            settingVariableName,
            currentScriptFileName,
            currentAppDirPath
        )
        return listSettingVariableListSource.map {
            if (
                !it.startsWith(filePrefix)
            ) return@map QuoteTool.trimBothEdgeQuote(it)
                .replace(",", "\n")
            val listSettingVariablePath = decideSettingVariableName(settingVariableName).let {
                ScriptPreWordReplacer.replace(
                    it,
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptFileName
                )
            }
//                it.removePrefix(filePrefix)
            val listSettingVariablePathObj = File(listSettingVariablePath)
            val listSettingVariableDirPath = listSettingVariablePathObj.parent
                ?: return@map QuoteTool.trimBothEdgeQuote(it)
                    .replace(",", "\n")
            val hideSettingVariableFileName = listSettingVariablePathObj.name
            SettingFile.read(
                listSettingVariableDirPath,
                hideSettingVariableFileName
            ).replace(",", "\n")
        }
            .joinToString("\n")
            .replace(
                Regex("\n\n*"),
                "\n"
            ).let {
                ScriptPreWordReplacer.replace(
                    it,
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptFileName,
                )
            }.split("\n")
            .filter {
                it.isNotEmpty()
            }
    }

    private fun decideSettingVariableName(
        variableName: String
    ): String {
        return when(variableName){
            CommandClickScriptVariable.HIDE_SETTING_VARIABLES
            -> "${UsePath.fannelSettingVariablsDirPath}/${UsePath.hideSettingVariablesConfig}"
            CommandClickScriptVariable.IGNORE_HISTORY_PATHS
            -> "${UsePath.fannelSettingVariablsDirPath}/${UsePath.ignoreHistoryPathsConfig}"
            else -> String()
        }
    }
}