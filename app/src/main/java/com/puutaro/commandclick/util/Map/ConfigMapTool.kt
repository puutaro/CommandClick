package com.puutaro.commandclick.util.Map

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

object ConfigMapTool {
    fun create(
        configPath: String,
        defaultConfigMapStr: String,
        readSharePreffernceMap: Map<String, String>,
        setReplaceVariableMap:  Map<String, String>? = null,
    ): Map<String, String>? {
        val propertySeparator = ","
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        val settingMenuSettingFilePath = ScriptPreWordReplacer.replace(
            configPath,
            currentAppDirPath,
            currentScriptFileName,
        )
        val settingMenuSettingFilePathObj = File(settingMenuSettingFilePath)
        return when (settingMenuSettingFilePathObj.isFile) {
            true -> {
                val parentDirPath = settingMenuSettingFilePathObj.parent
                    ?: return null
                SettingFile.read(
                    parentDirPath,
                    settingMenuSettingFilePathObj.name
                )
            }

            else -> {
                SettingFile.formSettingContents(
                    defaultConfigMapStr.split("\n")
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