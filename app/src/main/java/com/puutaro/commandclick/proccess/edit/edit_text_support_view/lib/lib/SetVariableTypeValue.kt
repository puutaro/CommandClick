package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod

object SetVariableTypeValue {
    fun makeByReplace(
        editParameters: EditParameters,
    ): String? {
        val currentSetVariableMap = editParameters.setVariableMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        val fannelDirName = currentScriptName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                fannelDirName,
                currentScriptName
            )
        }.let {
            ReplaceVariableMapReflecter.reflect(
                QuoteTool.trimBothEdgeQuote(it),
                editParameters
            )
        }
    }
}