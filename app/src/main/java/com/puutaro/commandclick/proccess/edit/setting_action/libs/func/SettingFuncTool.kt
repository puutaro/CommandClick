package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.map.CmdClickMap

object SettingFuncTool {
    fun makeArgsPairList(
        argsPairListBeforeBsEscape: List<Pair<String, String>>,
        varNameToValueStrMap: Map<String, String?>?,
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

    fun makeArgsPairListByEscape(
        argsPairListBeforeBsEscape: List<Pair<String, String>>,
        varNameToValueStrMap: Map<String, String?>?,
    ): List<Pair<String, String>> {
        return argsPairListBeforeBsEscape.map {
                (argName, valueStr) ->
            argName to
                    CmdClickMap.replaceByBackslashToNormalByEscape(
                        valueStr,
                        varNameToValueStrMap,
                    )
        }
    }

    fun makeJoinStrBySeparator(
        joinStrToErr: Pair<String, FuncCheckerForSetting. FuncCheckErr?>,
        separator: String,
        defaultJoinStr: String?,
    ): String {
        val joinStrSrc = joinStrToErr.first
        return if (
            joinStrSrc == defaultJoinStr
        ) separator
        else joinStrSrc
    }
}