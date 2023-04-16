package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
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
        var innerExecCmd = replaceTargetStr
        setReplaceVariableMap?.forEach {
            val replaceVariable = "\${${it.key}}"
            val replaceString = it.value
                .replace("\${01}", currentAppDirPath)
            innerExecCmd = innerExecCmd?.replace(
                replaceVariable,
                replaceString
            )
        }
        return innerExecCmd
    }
}