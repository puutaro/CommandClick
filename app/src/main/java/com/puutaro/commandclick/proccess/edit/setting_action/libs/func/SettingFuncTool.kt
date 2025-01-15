package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object SettingFuncTool {

    const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

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

    fun replaceShellCmdByFieldVarName(
        execShellCmd: String,
        inputConForShellVar: String,
        delimiter: String,
        fieldVarPrefix: String,
    ): String {
        if (
            delimiter == defaultNullMacroStr
            || fieldVarPrefix == defaultNullMacroStr
        ) return execShellCmd
        val fieldVarMarkToElList =
            inputConForShellVar.split(delimiter)
                .mapIndexed { index, el ->
                    "${'$'}${fieldVarPrefix}${index}" to el
                }
        var execShellCmdWithReplace = execShellCmd
        fieldVarMarkToElList.forEach { (fieldVarName, el) ->
            execShellCmdWithReplace = execShellCmdWithReplace.replace(
                fieldVarName,
                el
            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lDelireplace.txt").absolutePath,
//            listOf(
//                "execShellCmd: ${execShellCmd}",
//                "inputConForShellVar: ${inputConForShellVar}",
//                "delimiter: ${delimiter}",
//                "fieldVarPrefix: ${fieldVarPrefix}",
//                "fieldVarMarkToElList: ${fieldVarMarkToElList}",
//                "execShellCmdWithReplace: ${execShellCmdWithReplace}",
//                "execShellCmdWithReplaceWithRep: ${execShellCmdWithReplace.replace(
//                    Regex("[$]${fieldVarPrefix}[0-9]{1,5}"),
//                    String()
//                )}",
//            ).joinToString("\n\n")
//        )
        return execShellCmdWithReplace.replace(
            Regex("[$]${fieldVarPrefix}[0-9]{1,5}"),
            String()
        )
    }
}