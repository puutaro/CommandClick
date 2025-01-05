package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager


object SettingIfManager {

    private const val judgeTargetArgName = "judgeTarget"
    private const val judgeTargetArgIndex = -1

    fun handle(
        judgeTargetStrSrc: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<Boolean?, IfCheckErr?> {
        val judgeTargetStrToErr = checkNotExistIfArgsVarErr(
            judgeTargetStrSrc,
            judgeTargetArgName,
            judgeTargetArgIndex,
//            varNameToValueStrMap,
        ).let {
                judgeTargetStrToErr ->
            if(
                judgeTargetStrToErr.second != null
            ) return null to judgeTargetStrToErr.second
            judgeTargetStrToErr.first
        } ?: return null to launchTypeCheckErr(
            judgeTargetArgName,
            judgeTargetArgIndex,
            judgeTargetStrSrc,
            "not exist",
        )
        val ifCheckErr = checkArgs(
            argsPairList,
//            varNameToValueStrMap,
        )
        if(
            ifCheckErr != null
        ) return null to ifCheckErr
        val baseRegexIndex = 0
        val judgeBaseRegexStr = argsPairList.get(baseRegexIndex).second
        val judgeBaseRegex =
            try {
                judgeBaseRegexStr.toRegex()
            } catch(e: Exception){
                val spanJudgeBaseRegexStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    judgeBaseRegexStr
                        .replace("<", "＜")
                        .replace(">", "＞")
                        .replace("%", "％    ")
                )
                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    argsNameList.get(baseRegexIndex)
                )
                val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    baseRegexIndex.toString()
                )
                val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    SettingActionKeyManager.SettingSubKey.S_IF.key
                )
                return null to IfCheckErr("Failure to compile '${spanSIfKey}' method args regex: ${spanJudgeBaseRegexStr}: name: ${spanArgName}, index: ${spanArgIndex}")
            }
//            ?: return raiseBaseRegexFailToCompileErr(
//            argsPairList
//        )
//        SettingFuncTool.getValueStrFromMapOrIt(
//            argsPairList.get(0).second,
//            varNameToValueStrMap,
//        )

//        val judgeBaseRegex = try {
//            SettingFuncTool.getValueStrFromMapOrIt(
//                judgeBaseRegexStr,
////                varNameToValueStrMap,
//            )?.toRegex()
//        } catch (e: Exception){
//            return raiseBaseRegexFailToCompileErr(
//                argsPairList
//            )
//        } ?: return raiseBaseRegexFailToCompileErr(
//            argsPairList
//        )
        val matchTypeStr =
            argsPairList.get(1).second
//        SettingFuncTool.getValueStrFromMapOrIt(
//            argsPairList.get(1).second,
//            varNameToValueStrMap,
//        ) ?: return raiseMatchTypeErr()
        val matchType = JudgeType.entries.firstOrNull {
            it.str == matchTypeStr
        } ?: let {
            return raiseMatchTypeErr()
        }
        return when(matchType){
            JudgeType.EQUAL -> {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lifargs.txt").absolutePath,
//                    listOf(
//                        "argsPairList.get(0).second: ${argsPairList.get(0).second}",
//                        "argsPairList.get(1).second: ${argsPairList.get(1).second}",
//                        "judgeBaseRegex: ${judgeBaseRegex}",
//                        "matchType: ${matchType}",
//                    ).joinToString("\n")
//                )
                judgeBaseRegex.containsMatchIn(judgeTargetStrToErr)
            }
            JudgeType.NOT_EQUAL -> {
                !judgeBaseRegex.containsMatchIn(judgeTargetStrToErr)
            }
        } to null

    }

    private fun raiseBaseRegexFailToCompileErr(
        argsPairList: List<Pair<String, String>>
    ): Pair<Boolean?, IfCheckErr> {
        val spanJudgeBaseRegexStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argsPairList.get(0).second
                .replace("<", "＜")
                .replace(">", "＞")
                .replace("%", "％    ")
        )
        val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            SettingActionKeyManager.SettingSubKey.S_IF.key
        )
        return null to IfCheckErr("Failure to compile '${spanSIfKey}' method args regex: ${spanJudgeBaseRegexStr}")
    }

    private fun raiseMatchTypeErr(): Pair<Boolean?, IfCheckErr> {
        val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            SettingActionKeyManager.SettingSubKey.S_IF.key
        )
        return null to IfCheckErr(
            "'${spanSIfKey}' Match type must be ${
                JudgeType.entries.map { "'${it.str}'" }.joinToString(" or ")
            }")
    }

    class IfCheckErr(
        val errMessage: String
    )

    private fun checkArgs(
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>?,
    ): IfCheckErr? {
        if(
            argsNameList.isEmpty()
        ) return null
        argsNameList.forEachIndexed {
                index, argName ->
            val argPair = argsPairList.getOrNull(index)
                ?: let {
                    val spanArgsNameListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        argsNameList.joinToString(", ")
                    )
                    val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        SettingActionKeyManager.SettingSubKey.S_IF.key
                    )
                    return IfCheckErr(
                        "'${spanSIfKey}' method all args not exist: args list: ${spanArgsNameListCon}"
                    )
                }
            if(
                argPair.first.isEmpty()
            ) {
                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    argName
                )
                val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    (index + 1).toString()
                )
                val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    SettingActionKeyManager.SettingSubKey.S_IF.key
                )
                return IfCheckErr("'${spanSIfKey}' method args not exist: name: ${spanArgName}, index: ${spanArgIndex}")
            }
            checkNotExistIfArgsVarErr(
                argPair.second,
                argName,
                index,
//                varNameToValueStrMap,
            ).let {
                    strToArgErr ->
                val argErr = strToArgErr.second
                    ?: return@let
                return argErr
            }
        }
        return null
    }

    private enum class JudgeType(
        val str: String,
    ) {
        EQUAL("equal"),
        NOT_EQUAL("notEqual"),
    }

    private val argsNameList = listOf(
        "judgeBaseRegex",
        "matchType",
    )

    private fun checkNotExistIfArgsVarErr(
        argStr: String,
        argName: String,
        index: Int,
//        varNameToValueStrMap: Map<String, String?>?,
    ): Pair<String?, IfCheckErr?> {
        if(
            !SettingActionKeyManager.ValueStrVar.matchStringVarName(argStr)
        ) return argStr to null
        return null to launchTypeCheckErr(
            argName,
            index,
            argStr,
            "not exist string if args var name"
        )
//        SettingActionKeyManager.ValueStrVar.matchStringVarName(argStr).let {
//                isStrVarRegex ->
//            if(isStrVarRegex) return@let
//        }
//        val strKey = SettingActionKeyManager.ValueStrVar.convertStrKey(argStr)
//        val runPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
//        (strKey.startsWith(runPrefix)).let {
//                isRunPrefix ->
//            if(!isRunPrefix) return@let
//            return null to launchTypeCheckErr(
//                argName,
//                index,
//                argStr,
//                "disables ${runPrefix} prefix"
//            )
//        }
//        val valueStr =
//            varNameToValueStrMap?.get(strKey)
//        if(
//            valueStr is String
//        ) return valueStr to null
////                            FileSystems.updateFile(
////                                File(UsePath.cmdclickDefaultAppDirPath, "iarg.txt").absolutePath,
////                                listOf(
////                                    "argStr: ${argStr}",
////                                    "bitmapKey: ${bitmapKey}"
////                                ).joinToString("\n")
////                            )
//        return null to launchTypeCheckErr(
//            argName,
//            index,
//            argStr,
//            "not exist string var name"
//        )
    }

    private fun launchTypeCheckErr(
        argName: String,
        index: Int,
        argStr: String,
        valurStrErrBody: String,
    ): IfCheckErr {
        val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argName
        )
        val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            (index + 1).toString()
        )
        val spanArgStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argStr
        )
        val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            SettingActionKeyManager.SettingSubKey.S_IF.key
        )
        return IfCheckErr(
            "Arg ${spanArgName} ${valurStrErrBody}: ${spanArgStr}, ${spanSIfKey} arg, index: ${spanArgIndex}"
        )
    }
}