package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object ListSettingVariableListMaker {

    private val filePrefix = EditSettings.filePrefix
    private val setReplaceVariableValName =
        CommandClickScriptVariable.SET_REPLACE_VARIABLE

    fun makeFromSettingVariableList(
        settingVariableName: String,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingVariablesList: List<String>?,
        onImport: Boolean = true
    ): List<String> {
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val listSettingVariableListSource =
            SettingVariableReader.getStrListByReplace(
            settingVariablesList,
            settingVariableName,
            currentFannelName,
            currentAppDirPath
        )
        return listSettingVariableListSource.map {
            when (
                it.startsWith(filePrefix)
            ) {
                false -> QuoteTool.trimBothEdgeQuote(it)
                else -> {
                    val listSettingVariablePath =
                        decideSettingVariableListPath(
                            settingVariableName,
                            it,
                            setReplaceVariableMap,
                            currentAppDirPath,
                            currentFannelName
                        )
                    SettingFile.read(
                        listSettingVariablePath,
                        File(currentAppDirPath, currentFannelName).absolutePath,
                        setReplaceVariableMap,
                        onImport
                    )
                }
            }.let {
                QuoteTool.replaceBySurroundedIgnore(
                    it,
                    ',',
                    "\n"
                )
            }
        }.let {
            removeMultipleNewLinesAndReplace(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
        }.let {
            DuplicatedKey.leaveAfterEl(
                it,
                settingVariableName
            )
        }
    }

    private fun removeMultipleNewLinesAndReplace(
        valueList: List<String>,
        setReplaceVariableMap: Map<String, String>?,
        currentAppDirPath: String,
        currentFannelName: String,
    ): List<String> {
        return execRemoveMultipleNewLinesAndReplace(
            valueList.joinToString("\n"),
            setReplaceVariableMap,
            currentAppDirPath,
            currentFannelName,
        ).split("\n")
    }

    fun execRemoveMultipleNewLinesAndReplace(
        valueListCon: String,
        setReplaceVariableMap: Map<String, String>?,
        currentAppDirPath: String,
        currentFannelName: String,
    ): String {
        return valueListCon.replace(
            Regex("\n\n*"),
            "\n"
        ).split("\n")
            .filter {
                it.isNotEmpty()
            }.joinToString("\n").let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
                    currentAppDirPath,
                    currentFannelName,
                )
            }
    }

    private fun decideSettingVariableListPath(
        variableName: String,
        lineWithFilePrefix: String,
        setReplaceVariableMap: Map<String, String>?,
        currentAppDirPath: String,
        currentFannelName: String,
    ): String {
//        val isOnlyFilePrefix =
//            lineWithFilePrefix.trim() == filePrefix
        val isFilePrefix =
            lineWithFilePrefix.startsWith(filePrefix)
                    && lineWithFilePrefix.trim() != filePrefix
        return when(true){
            isFilePrefix ->
                lineWithFilePrefix.removePrefix(filePrefix)
            else
            -> decideFixSettingFilePath(variableName)
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
        }
    }

    private fun decideFixSettingFilePath(
        variableName: String,
    ): String {
        return when(variableName){
            setReplaceVariableValName,
                -> File(
                UsePath.fannelSettingVariablsDirPath,
                UsePath.setReplaceVariablesConfig
            ).absolutePath
            CommandClickScriptVariable.SET_VARIABLE_TYPE
            ->  File(
                UsePath.fannelSettingVariablsDirPath,
                UsePath.setVariableTypesConfig
            ).absolutePath
            CommandClickScriptVariable.HIDE_SETTING_VARIABLES
            -> File(
                UsePath.fannelSettingVariablsDirPath,
                UsePath.hideSettingVariablesConfig
            ).absolutePath
            CommandClickScriptVariable.IGNORE_HISTORY_PATHS
            -> File(
                UsePath.fannelSettingVariablsDirPath,
                UsePath.ignoreHistoryPathsConfig
            ).absolutePath
            CommandClickScriptVariable.SETTING_BUTTON_CONFIG
            -> UsePath.settingButtonConfigPath
            CommandClickScriptVariable.EDIT_BUTTON_CONFIG
            -> UsePath.editButtonConfigPath
            CommandClickScriptVariable.PLAY_BUTTON_CONFIG
            -> UsePath.playButtonConfigPath
            CommandClickScriptVariable.EXTRA_BUTTON_CONFIG
            -> UsePath.extraButtonConfigPath
            CommandClickScriptVariable.LIST_INDEX_CONFIG
            -> UsePath.listIndexForEditConfigPath
            CommandClickScriptVariable.QR_DIALOG_CONFIG
            -> UsePath.qrDialogConfigPath
            else -> String()
        }
    }
}

private object DuplicatedKey {
    fun leaveAfterEl(
        valLineList: List<String>,
        settingVariableName: String,
    ): List<String> {
        val reversedValList = valLineList.reversed()
        val prefixList = mutableListOf<String>()
        return reversedValList.map {
                valueLine ->
            if(
                prefixList.any {
                    duplicatedJudgeHandler(
                        valueLine,
                        it,
                        settingVariableName,
                    )
                }
            ) return@map String()
            val prefix = valueLine.split("=").firstOrNull()
                ?.let { "${it}=" }
                ?: return@map String()
            prefixList.add(prefix)
            valueLine
        }.filter {
            it.isNotEmpty()
        }.reversed()
    }

    private fun duplicatedJudgeHandler(
        valueLine: String,
        prefixEl: String,
        settingVariableName: String,
    ): Boolean {
        return when(settingVariableName) {
            CommandClickScriptVariable.SET_VARIABLE_TYPE -> {
                val typeSeparator = ":"
                val rawPrefixEl =
                    prefixEl
                        .split(typeSeparator)
                        .firstOrNull()
                        ?: return false
                valueLine.startsWith("${rawPrefixEl}${typeSeparator}")
            }
            else -> valueLine.startsWith(prefixEl)
        }
    }
}