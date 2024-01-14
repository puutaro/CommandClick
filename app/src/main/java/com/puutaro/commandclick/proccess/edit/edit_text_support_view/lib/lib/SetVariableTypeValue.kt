package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object SetVariableTypeValue {
    fun makeByReplace(
        editParameters: EditParameters,
    ): String? {
        val currentSetVariableMap = editParameters.setVariableMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreferenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
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