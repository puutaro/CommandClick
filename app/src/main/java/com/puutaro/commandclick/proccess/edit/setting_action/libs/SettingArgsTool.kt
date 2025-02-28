package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.util.map.CmdClickMap

object SettingArgsTool {
    fun makeArgsPairList(
        ifMap: Map<String, String>,
        argKey: String,
        varNameToValueStrMap: Map<String, String?>?,
        separator: Char,
    ): List<Pair<String, String>> {
        return execMakeArgsPairList(
            ifMap.get(
                argKey
            ),
            varNameToValueStrMap,
            separator,
        )
    }

    fun makeArgsPairListForImport(
        ifMap: Map<String, String>,
        argKey: String,
        varNameToValueStrMap: Map<String, String?>?,
        separator: Char,
        loopVarMap: Map<String, String>?,
    ): List<Pair<String, String>> {
        val ifArgsCon = ifMap.get(argKey)?.let {
            CmdClickMap.replaceByAtVar(
                it,
                loopVarMap
            )
        }
        return execMakeArgsPairList(
            ifArgsCon,
            varNameToValueStrMap,
            separator,
        )
    }

    private fun execMakeArgsPairList(
        ifArgsCon: String?,
        varNameToValueStrMap: Map<String, String?>?,
        separator: Char,
    ): List<Pair<String, String>> {
        if(
            ifArgsCon.isNullOrEmpty()
        ) return emptyList()
        return CmdClickMap.createMap(
            ifArgsCon,
            separator
        ).asSequence().filter {
            it.first.isNotEmpty()
        }.map {
                argNameToValueStr ->
            argNameToValueStr.first to when(
                varNameToValueStrMap.isNullOrEmpty()
            ) {
                true -> argNameToValueStr.second
                else -> CmdClickMap.replaceByBackslashToNormal(
                    argNameToValueStr.second,
                    varNameToValueStrMap,
                )
            }
        }.toList()
    }
}