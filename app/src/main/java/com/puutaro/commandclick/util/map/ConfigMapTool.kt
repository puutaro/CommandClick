package com.puutaro.commandclick.util.map

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object ConfigMapTool {

    val filePrefix = EditSettings.filePrefix
    fun create(
        context: Context?,
        configPath: String,
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
        val settingMenuSettingFilePath = ScriptPreWordReplacer.replace(
            configPath,
//            currentAppDirPath,
            currentScriptFileName,
        )
        val settingMenuSettingFilePathObj = File(settingMenuSettingFilePath)
        return when (settingMenuSettingFilePathObj.isFile) {
            true -> {
                SettingFile.read(
                    context,
                    File(UsePath.cmdclickDefaultAppDirPath, currentScriptFileName).absolutePath,
                    setReplaceVariableMap,
                    null,
                    null,
                    null,
                    settingMenuSettingFilePathObj.absolutePath,

                )
            }

            else -> {
                SettingFile.formSettingContents(
                    defaultConfigMapStr,
//                    QuoteTool.splitBySurroundedIgnore(
//                        defaultConfigMapStr,
//                        '\n'
//                    )
                )
            }
        }.let {
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
        }.let {
            CmdClickMap.createMap(
                it,
                propertySeparator
            )
        }.toMap().filterKeys { it.isNotEmpty() }
    }
}