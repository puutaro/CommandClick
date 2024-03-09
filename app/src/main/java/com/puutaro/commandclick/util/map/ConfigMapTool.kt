package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.SharePrefTool
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
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreffernceMap
        )
        val currentScriptFileName = SharePrefTool.getCurrentFannelName(
            readSharePreffernceMap
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
}