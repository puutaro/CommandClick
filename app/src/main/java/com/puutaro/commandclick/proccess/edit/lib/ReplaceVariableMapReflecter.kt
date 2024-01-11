package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod

object ReplaceVariableMapReflecter {
    fun reflect(
        replaceTargetStr: String?,
        editParameters: EditParameters,
    ): String? {
        val setReplaceVariableMap = editParameters.setReplaceVariableMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        var innerExecCmd = replaceTargetStr
        setReplaceVariableMap?.forEach {
            val replaceVariable = "\${${it.key}}"
            val replaceString = it.value
                .let {
                    ScriptPreWordReplacer.replace(
                        it,
                        currentAppDirPath,
                        currentScriptName
                    )
                }
            innerExecCmd = innerExecCmd?.replace(
                replaceVariable,
                replaceString
            )
        }
        return innerExecCmd
    }
}