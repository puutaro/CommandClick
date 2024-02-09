package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object FannelStateRooterManager {

    private val tsvDefaultKeyNameForFannelStateRooterMap = "default"

    fun makeSettingVariableList(
        currentScriptContentsList: List<String>,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        settingSectionStart: String,
        settingSectionEnd: String,
    ): List<String>? {

        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val mainFannelSettingVariableList =
            makeMainFannelSettingVariableList(
                currentScriptContentsList,
                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap,
                settingSectionStart,
                settingSectionEnd,
            )
        val onShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.on_shortcut
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        if(
            !onShortcut
        ) return mainFannelSettingVariableList

        val currentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_state
        )
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
        ) return mainFannelSettingVariableList
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
        ) return mainFannelSettingVariableList
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
        ) return mainFannelSettingVariableList
        val virtualFannelConList =
            SetReplaceVariabler.execReplaceByReplaceVariables(
                ReadText(settingVariablePath).readText(),
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            ).split("\n")
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

        return CommandClickVariables.substituteVariableListFromHolder(
            virtualFannelConList,
            settingSectionStart,
            settingSectionEnd,
        )
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
                "\n"
            )
        }.toMap()
    }

    private fun makeMainFannelSettingVariableList(
        currentScriptContentsList: List<String>,
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
        settingSectionStart: String,
        settingSectionEnd: String,
    ): List<String>? {
        return CommandClickVariables.substituteVariableListFromHolder(
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