package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object FannelStateRooterManager {

    private const val tsvDefaultKeyNameForFannelStateRooterMap =
        "default"

    fun makeSettingVariableList(
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingSectionStart: String,
        settingSectionEnd: String,
        settingFannelPath: String,
    ): List<String>? {
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )

        return getSettingVariableList(
                settingFannelPath,
                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap,
                settingSectionStart,
                settingSectionEnd,
            ).let {
            SettingVariableImportManager.import(
                it,
                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap,
                settingSectionStart,
                settingSectionEnd,
            )
        }
    }

    fun getSettingFannelPath(
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val defaultSettingFilePath =
            File(
                currentAppDirPath,
                currentFannelName
            ).absolutePath

        val onShortcut = SharePrefTool.getOnShortcut(
            readSharePreferenceMap
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        val currentFannelState =
            SharePrefTool.getCurrentStateName(
                readSharePreferenceMap
            )
        if(
            !onShortcut
            && currentFannelState.isEmpty()
        ) return defaultSettingFilePath
        val fannelStateRootTableFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateRootTableFilePath,
            currentAppDirPath,
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
            currentAppDirPath,
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
        ) ?: File(currentAppDirPath, currentFannelName).absolutePath
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
        currentAppDirPath: String,
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
                currentAppDirPath,
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
        currentAppDirPath: String,
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
                    currentAppDirPath,
                    currentFannelName,
                )
            }?.split("\n")
    }
}