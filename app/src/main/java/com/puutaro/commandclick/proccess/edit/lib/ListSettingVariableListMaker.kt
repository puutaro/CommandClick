package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit_list.config_settings.SettingActionForEditList
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.AltRegexTool
import java.io.File

object ListSettingVariableListMaker {

    private val filePrefix = EditSettings.filePrefix
    private val setReplaceVariableValName =
        CommandClickScriptVariable.SET_REPLACE_VARIABLE

    fun makeConfigMapFromSettingValList(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
        targetSettingConfigValName: String,
        settingVariableList: List<String>?,
        defaultButtonConfigCon: String,
    ): Map<String, String> {
        val settingButtonConfigMapStr =
            makeFromSettingVariableList(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                settingActionAsyncCoroutine,
                imageActionAsyncCoroutine,
                targetSettingConfigValName,

                settingVariableList
            ).joinToString(",")
                .let {
                    if(
                        it.isNotEmpty()
                    ) return@let it
                    defaultButtonConfigCon
                }
        return createFromSettingVal(
            settingButtonConfigMapStr,
            String(),
            fannelInfoMap,
            setReplaceVariableMap
        )
    }
    fun makeFromSettingVariableList(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
        settingVariableName: String,
        settingVariablesList: List<String>?,
    ): List<String> {
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val listSettingVariableListSource =
            SettingVariableReader.getStrListByReplace(
            settingVariablesList,
            settingVariableName,
            currentFannelName,
//            currentAppDirPath
        )
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
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
//                            currentAppDirPath,
                            currentFannelName
                        )
                    SettingFile.read(
                        context,
                        File(cmdclickDefaultAppDirPath, currentFannelName).absolutePath,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        settingActionAsyncCoroutine,
                        imageActionAsyncCoroutine,
                        listSettingVariablePath,
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
//                currentAppDirPath,
                currentFannelName,
            )
        }.let {
            DuplicatedKey.leaveAfterEl(
                it,
                settingVariableName
            )
        }
    }

    fun makeFromSettingPath(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
        settingPath: String,
    ): Map<String, String> {
        val currentFannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
        val configMapStr = SettingFile.read(
            context,
            File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath,
            setReplaceVariableMap,
            busyboxExecutor,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            settingPath,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "ltitleSetting.txt").absolutePath,
//            listOf(
//                "settingPath: ${settingPath}",
//                "currentFannelName: ${currentFannelName}",
//                "configMapStr: ${configMapStr}",
//                "settingMap: ${createFromSettingVal(
//                    configMapStr,
//                    String(),
//                    fannelInfoMap,
//                    setReplaceVariableMap
//                )}"
//            ).joinToString("\n")
//        )
        return createFromSettingVal(
            configMapStr,
            String(),
            fannelInfoMap,
            setReplaceVariableMap
        )
    }

    private fun createFromSettingVal(
        settingValConSrc: String,
        defaultConfigMapStr: String,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap:  Map<String, String>? = null,
    ): Map<String, String> {
        val propertySeparator = ','
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentScriptFileName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val settingValCon = ScriptPreWordReplacer.replace(
            settingValConSrc,
//            currentAppDirPath,
            currentScriptFileName,
        )
        val configMapPairList = when (true) {
            settingValCon.isNotEmpty() ->
                SettingFile.formSettingContents(
                    settingValCon
                        //.split("\n")
                )
            else ->
                SettingFile.formSettingContents(
                    defaultConfigMapStr
                        //.split("\n")
                )
        }.let {
            replaceByPreWordAndRepValMap(
                it,
//                currentAppDirPath,
                currentScriptFileName,
                setReplaceVariableMap,
            )
        }.let {
            CmdClickMap.createMap(
                it,
                propertySeparator
            )
        }.reversed()
        val configMap = configMapPairList.toMap().filterKeys { it.isNotEmpty() }
        return configMap
    }

    private fun removeMultipleNewLinesAndReplace(
        valueList: List<String>,
        setReplaceVariableMap: Map<String, String>?,
//        currentAppDirPath: String,
        currentFannelName: String,
    ): List<String> {
        return execRemoveMultipleNewLinesAndReplace(
            valueList.joinToString("\n"),
            setReplaceVariableMap,
//            currentAppDirPath,
            currentFannelName,
        ).split("\n")
    }

    fun execRemoveMultipleNewLinesAndReplace(
        valueListCon: String,
        setReplaceVariableMap: Map<String, String>?,
//        currentAppDirPath: String,
        currentFannelName: String,
    ): String {
//        valueListCon.replace(
//            Regex("\n\n*"),
//            "\n"
//        )
        return AltRegexTool.consecCharToOne(
            valueListCon,
            '\n',
        ).split("\n")
            .filter {
                it.isNotEmpty()
            }.joinToString("\n").let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
//                    currentAppDirPath,
                    currentFannelName,
                )
            }
    }

    private fun replaceByPreWordAndRepValMap(
        targetStr: String,
//        currentAppDirPath: String,
        currentScriptFileName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
        return targetStr.let {
            ScriptPreWordReplacer.replace(
                it,
//                currentAppDirPath,
                currentScriptFileName
            )
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentScriptFileName
            )
        }
    }

    private fun decideSettingVariableListPath(
        variableName: String,
        lineWithFilePrefix: String,
        setReplaceVariableMap: Map<String, String>?,
//        currentAppDirPath: String,
        currentFannelName: String,
    ): String {
        val isFilePrefix =
            lineWithFilePrefix.startsWith(filePrefix)
                    && AltRegexTool.trim(lineWithFilePrefix) != filePrefix
        return when(true){
            isFilePrefix ->
                lineWithFilePrefix.removePrefix(filePrefix)
            else
            -> decideFixSettingFilePath(variableName)
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
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
//            CommandClickScriptVariable.HIDE_SETTING_VARIABLES
//            -> File(
//                UsePath.fannelSettingVariablsDirPath,
//                UsePath.hideSettingVariablesConfig
//            ).absolutePath
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
            CommandClickScriptVariable.EDIT_LIST_CONFIG
            -> UsePath.editListForEditConfigPath
            CommandClickScriptVariable.FANNEL_STATE_CONFIG
            -> UsePath.fannelStateConfigPath
//            CommandClickScriptVariable.QR_DIALOG_CONFIG
//            -> UsePath.qrDialogConfigPath
            CommandClickScriptVariable.EDIT_BOX_TITLE_CONFIG
            -> UsePath.editTitleConfigPath
            CommandClickScriptVariable.IMPORT_DISABLE_VAL_LIST
            -> UsePath.importDisableValListPath
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
        val prefixList = ArrayList<String>(reversedValList.size)
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