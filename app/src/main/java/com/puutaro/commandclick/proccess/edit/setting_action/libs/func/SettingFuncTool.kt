package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager

object SettingFuncTool {
    fun getValueStrFromMapOrIt(
        rawValueStr: String,
        varNameToValueStrMap: Map<String, String?>,
    ): String? {

        if (
            !SettingActionKeyManager.ValueStrVar.matchStringVarName(rawValueStr)
        ) return rawValueStr
        val curSVarKey = SettingActionKeyManager.ValueStrVar.convertStrKey(rawValueStr)
        return varNameToValueStrMap.get(curSVarKey)
    }
}