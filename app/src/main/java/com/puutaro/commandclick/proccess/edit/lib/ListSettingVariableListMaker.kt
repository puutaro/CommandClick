package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

object ListSettingVariableListMaker {

    private val filePrefix = EditSettings.filePrefix

    fun makeFromSettingVariableList(
        settingVariableName: String,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingVariablesList: List<String>?,
    ): List<String> {
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
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
                    .replace(",", "\n")
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
                        listSettingVariablePath
                    ).replace(",", "\n")
                }
            }
        }.let {
            removeMultipleNewLinesAndReplace(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
        }
    }

    private fun removeMultipleNewLinesAndReplace(
        valueList: List<String>,
        setReplaceVariableMap: Map<String, String>?,
        currentAppDirPath: String,
        currentFannelName: String,
    ): List<String> {
        return valueList.joinToString("\n").replace(
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
            }.split("\n")
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
            CommandClickScriptVariable.SET_REPLACE_VARIABLE,
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
            CommandClickScriptVariable.LIST_INDEX_CONFIG
            -> UsePath.listIndexForEditConfigPath
            CommandClickScriptVariable.QR_DIALOG_CONFIG
            -> UsePath.qrDialogConfigPath
            else -> String()
        }
    }
}