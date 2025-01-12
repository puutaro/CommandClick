package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.util.map.CmdClickMap

object SettingFuncTool {
    fun makeArgsPairList(
        argsPairListBeforeBsEscape: List<Pair<String, String>>,
        varNameToValueStrMap: Map<String, String?>,
    ): List<Pair<String, String>> {
        return argsPairListBeforeBsEscape.map {
                (argName, valueStr) ->
            argName to
                    CmdClickMap.replaceByBackslashToNormal(
                        valueStr,
                        varNameToValueStrMap,
                    )
        }
    }
}