package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn

object SetVariableTypeValue {
    fun makeByReplace(
        editParameters: EditParameters,
    ): String? {
        val currentSetVariableMap = editParameters.setVariableMap
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
    }
}