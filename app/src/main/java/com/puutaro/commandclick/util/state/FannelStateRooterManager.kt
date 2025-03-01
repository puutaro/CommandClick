package com.puutaro.commandclick.util.state

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object FannelStateRooterManager {

    private const val tsvDefaultKeyNameForFannelStateRooterMap =
        "default"

    fun makeSettingVariableList(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingSectionStart: String,
        settingSectionEnd: String,
        settingFannelPath: String,
    ): List<String>? {
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val settingVariableBeforeImportList = getSettingVariableList(
            settingFannelPath,
//            currentAppDirPath,
            currentFannelName,
            setReplaceVariableMap,
            settingSectionStart,
            settingSectionEnd,
        )
        val importDisableValList = ListSettingVariableListMaker.makeFromSettingVariableList(
            context,
            fannelInfoMap,
            setReplaceVariableMap,
            null,
            null,
            null,
            CommandClickScriptVariable.IMPORT_DISABLE_VAL_LIST,
            settingVariableBeforeImportList
        )
        return SettingVariableImportManager.import(
            settingVariableBeforeImportList,
            importDisableValList,
//            currentAppDirPath,
            currentFannelName,
            setReplaceVariableMap,
            settingSectionStart,
            settingSectionEnd,
        )
    }

    fun getSettingFannelPath(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val defaultSettingFilePath =
            File(
                cmdclickDefaultAppDirPath,
                currentFannelName
            ).absolutePath

        val onShortcut = FannelInfoTool.getOnShortcut(
            fannelInfoMap
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        val currentFannelState =
            FannelInfoTool.getCurrentStateName(
                fannelInfoMap
            )
        if(
            !onShortcut
            && currentFannelState.isEmpty()
        ) return defaultSettingFilePath
        val fannelStateRootTableFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateRootTableFilePath,
//            currentAppDirPath,
            currentFannelName
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "vfanenl_before_fannelStateRootTableFilePathNoFile.txt").absolutePath,
//            listOf(
//                "currentAppDirPath: ${currentAppDirPath}",
//                "currentFannelName: ${currentFannelName}",
//                "onShortcut: ${onShortcut}",
//                "currentFannelState: ${currentFannelState}",
//                "fannelStateRootTableFilePath: ${fannelStateRootTableFilePath}"
//            ).joinToString("\n\n")
//        )

        if(
            !File(fannelStateRootTableFilePath).isFile
        ) return defaultSettingFilePath
        val fannelStateRooterMap = createFannelStateRooterMap(
            fannelStateRootTableFilePath,
//            currentAppDirPath,
            currentFannelName,
            setReplaceVariableMap
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "vfanenl_before_fannelStateRooterMap_empty.txt").absolutePath,
//            listOf(
//                "currentAppDirPath: ${currentAppDirPath}",
//                "currentFannelName: ${currentFannelName}",
//                "onShortcut: ${onShortcut}",
//                "currentFannelState: ${currentFannelState}",
//                "fannelStateRootTableFilePath: ${fannelStateRootTableFilePath}",
//                "fanenlStateRooterMapSrcCon: ${ReadText(fannelStateRootTableFilePath).readText()}",
//                "fannelStateRooterMap: ${fannelStateRooterMap}",
//            ).joinToString("\n\n")
//        )

        if(
            fannelStateRooterMap.isEmpty()
        ) return defaultSettingFilePath
        val defaultSettingValFilePath = fannelStateRooterMap.get(
            tsvDefaultKeyNameForFannelStateRooterMap
        ) ?: File(cmdclickDefaultAppDirPath, currentFannelName).absolutePath
        val settingVariablePath =
            fannelStateRooterMap.get(
                currentFannelState
            )?: defaultSettingValFilePath
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "vfanenl_before_settingVariablePathEmpty.txt").absolutePath,
//            listOf(
//                "readSharePreferenceMap: ${readSharePreferenceMap}",
//                "currentAppDirPath: ${currentAppDirPath}",
//                "currentFannelName: ${currentFannelName}",
//                "onShortcut: ${onShortcut}",
//                "currentFannelState: ${currentFannelState}",
//                "fannelStateRootTableFilePath: ${fannelStateRootTableFilePath}",
//                "fanenlStateRooterMapSrcCon: ${ReadText(fannelStateRootTableFilePath).readText()}",
//                "fannelStateRooterMap: ${fannelStateRooterMap}",
//                "defaultSettingValFilePath: ${defaultSettingValFilePath}",
//                "settingVariablePath: ${settingVariablePath}",
//            ).joinToString("\n\n")
//            )
        if(
            settingVariablePath.isEmpty()
            || !File(settingVariablePath).isFile
        ) return defaultSettingFilePath
        return settingVariablePath
//        val virtualFannelConList =
//            SetReplaceVariabler.execReplaceByReplaceVariables(
//                ReadText(settingVariablePath).readText(),
//                setReplaceVariableMap,
//                currentAppDirPath,
//                currentFannelName,
//            ).split("\n")

//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "vfanenl_last.txt").absolutePath,
//            listOf(
//                "readSharePreferenceMap: ${readSharePreferenceMap}",
//                "currentAppDirPath: ${currentAppDirPath}",
//                "currentFannelName: ${currentFannelName}",
//                "onShortcut: ${onShortcut}",
//                "currentFannelState: ${currentFannelState}",
//                "fannelStateRootTableFilePath: ${fannelStateRootTableFilePath}",
//                "fanenlStateRooterMapSrcCon: ${ReadText(fannelStateRootTableFilePath).readText()}",
//                "fannelStateFooterMapListCon: ${ReadText(fannelStateRootTableFilePath).textToList().filter {
//                    val stateToSettingValPathList =
//                        it.trim().split("\t")
//                    stateToSettingValPathList.size == 2
//                }}",
//                "fannelStateRooterMap: ${fannelStateRooterMap}",
//                "defaultSettingValFilePath: ${defaultSettingValFilePath}",
//                "settingVariablePath: ${settingVariablePath}",
//                "virtualFannelConListSrc: ${virtualFannelConList}",
//                "virtualFannelConList: ${CommandClickVariables.substituteVariableListFromHolder(
//                    virtualFannelConList,
//                    settingSectionStart,
//                    settingSectionEnd,
//                )}"
//            ).joinToString("\n\n")
//        )
    }

    private fun createFannelStateRooterMap(
        fannelStateRootTableFilePath: String,
//        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): Map<String, String> {
        return ReadText(fannelStateRootTableFilePath).textToList().filter {
            val stateToSettingValPathList =
                it.trim().split("\t")
            stateToSettingValPathList.size == 2
        }.joinToString("\n").replace(
            "\t",
            "=",
        ).let {
            val replaceMapSrc = SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName,
            )
            CmdClickMap.createMap(
                replaceMapSrc,
                '\n'
            )
        }.toMap()
    }

    private fun getSettingVariableList(
        currentFannelPath: String,
//        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
        settingSectionStart: String,
        settingSectionEnd: String,
    ): List<String>? {
        val currentScriptContentsList =
            ReadText(currentFannelPath).textToList()
        return CommandClickVariables.extractValListFromHolder(
                currentScriptContentsList,
                settingSectionStart,
                settingSectionEnd
            )?.joinToString("\n")?.let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
//                    currentAppDirPath,
                    currentFannelName,
                )
            }?.split("\n")
    }
}