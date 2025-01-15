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

    object FieldVarPrefix {
        fun replaceElementByFieldVarName(
            targetCon: String,
            fieldVarNameToValueStrList: List<Pair<String, String>>?,
            fieldVarPrefix: String,
        ): String {
            if (
                fieldVarNameToValueStrList.isNullOrEmpty()
            ) return targetCon

            var targetConWithReplace = targetCon
            fieldVarNameToValueStrList.forEach { (fieldVarName, el) ->
                targetConWithReplace =
                    targetConWithReplace.replace(
                        Regex("""([^\\])[$]${fieldVarName}"""),
                        "$1${el}"
                    ).replace(
                        Regex("""^[$]${fieldVarName}"""),
                        el
                    )
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lDelireplace.txt").absolutePath,
//                listOf(
//                    "execShellCmd: ${targetCon}",
//                    "fieldVarMarkToValueStrList: ${fieldVarNameToValueStrList}",
//                    "execShellCmdWithReplace: ${targetConWithReplace}",
//                    "execShellCmdWithReplaceWithRep: ${
//                        targetConWithReplace.replace(
//                            Regex("[$]${fieldVarPrefix}[0-9]{1,5}"),
//                            String()
//                        )
//                    }",
//                ).joinToString("\n\n")+ "\n\n==========\n\n"
//            )
            return targetConWithReplace.replace(
                Regex("[$]${fieldVarPrefix}[0-9]{1,5}"),
                String()
            )
        }

        fun makeFieldVarNameToValueStrList(
            inputCon: String,
            delimiter: String,
            fieldVarPrefix: String,
        ): List<Pair<String, String>>? {
            if (
                delimiter == defaultNullMacroStr
                || fieldVarPrefix == defaultNullMacroStr
            ) return null
//            FileSystems.updateFile(
//                File(
//                    UsePath.cmdclickDefaultAppDirPath,
//                    "lDelireplace_makeFieldVarMarkToValueStrList.txt"
//                ).absolutePath,
//                listOf(
//                    "inputConForShellVar: ${inputCon}",
//                    "delimiter: ${delimiter}",
//                    "fieldVarPrefix: ${fieldVarPrefix}",
//                    "pair: ${
//                        inputCon.split(delimiter)
//                            .mapIndexed { index, el ->
//                                "${'$'}${fieldVarPrefix}${index}" to el
//                            }
//                    }",
//                ).joinToString("\n\n") + "\n\n==========\n\n"
//            )
            return inputCon.split(delimiter)
                .mapIndexed { index, el ->
                    "${fieldVarPrefix}${index}" to el
                }
        }
    }
}