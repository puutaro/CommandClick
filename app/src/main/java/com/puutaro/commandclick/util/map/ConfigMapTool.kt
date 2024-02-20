package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

object ConfigMapTool {

    val filePrefix = EditSettings.filePrefix
    fun create(
        configPath: String,
        defaultConfigMapStr: String,
        readSharePreffernceMap: Map<String, String>,
        setReplaceVariableMap:  Map<String, String>? = null,
    ): Map<String, String> {
        val propertySeparator = ','
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptFileName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val settingMenuSettingFilePath = ScriptPreWordReplacer.replace(
            configPath,
            currentAppDirPath,
            currentScriptFileName,
        )
        val settingMenuSettingFilePathObj = File(settingMenuSettingFilePath)
        return when (settingMenuSettingFilePathObj.isFile) {
            true -> {
                SettingFile.read(
                    settingMenuSettingFilePathObj.absolutePath,
                    File(currentAppDirPath, currentScriptFileName).absolutePath,
                    setReplaceVariableMap,
                )
            }

            else -> {
                SettingFile.formSettingContents(
                    QuoteTool.splitBySurroundedIgnore(
                        defaultConfigMapStr,
                        '\n'
                    )
                )
            }
        }.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentScriptFileName
            )
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentScriptFileName
            )
        }.let {
            CmdClickMap.createMap(
                it,
                propertySeparator
            )
        }.toMap().filterKeys { it.isNotEmpty() }
    }

    fun createFromSettingVal(
        settingValConSrc: String,
        defaultConfigMapStr: String,
        readSharePreffernceMap: Map<String, String>,
        setReplaceVariableMap:  Map<String, String>? = null,
    ): Map<String, String> {
        val propertySeparator = ','
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptFileName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val settingValCon = ScriptPreWordReplacer.replace(
            settingValConSrc,
            currentAppDirPath,
            currentScriptFileName,
        )
        return when (true) {
            settingValCon.startsWith(filePrefix) -> {
                val settingButtonConfigPath = replaceByPreWordAndRepValMap(
                    UsePath.settingButtonConfigPath,
                    currentAppDirPath,
                    currentScriptFileName,
                    setReplaceVariableMap,
                )
                SettingFile.read(
                    settingButtonConfigPath,
                    File(currentAppDirPath, currentScriptFileName).absolutePath,
                    setReplaceVariableMap,
                )
            }
            settingValCon.isNotEmpty() ->
                SettingFile.formSettingContents(settingValCon.split("\n"))
            else ->
                SettingFile.formSettingContents(
                    defaultConfigMapStr.split("\n")
                )
        }.let {
            replaceByPreWordAndRepValMap(
                it,
                currentAppDirPath,
                currentScriptFileName,
                setReplaceVariableMap,
            )
        }.let {
            CmdClickMap.createMap(
                it,
                propertySeparator
            )
        }.reversed().toMap().filterKeys { it.isNotEmpty() }
    }

    private fun replaceByPreWordAndRepValMap(
        targetStr: String,
        currentAppDirPath: String,
        currentScriptFileName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
        return targetStr.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentScriptFileName
            )
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentScriptFileName
            )
        }
    }
}