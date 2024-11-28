package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.FuncCheckerForSetting.FuncCheckErr
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object SettingIfManager {

    fun handle(
        judgeTargetStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<Boolean?, IfCheckErr?> {
        val ifCheckErr = checkArgs(
            argsPairList
        )
        if(
            ifCheckErr != null
        ) return null to ifCheckErr
        val judgeBaseRegexStr = argsPairList.get(0).second
        val judgeBaseRegex = try {
            judgeBaseRegexStr.toRegex()
        } catch (e: Exception){
            val spanJudgeBaseRegexStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                judgeBaseRegexStr
                    .replace("<", "＜")
                    .replace(">", "＞")
                    .replace("%", "％")
            )
            return null to IfCheckErr("Failure to compile 'if' method args regex: ${spanJudgeBaseRegexStr}")
        }
        val matchTypeStr = argsPairList.get(1).second
        val matchType = JudgeType.entries.firstOrNull {
            it.str == matchTypeStr
        } ?: return null to IfCheckErr(
            "'if' Match type must be ${
                JudgeType.entries.map { "'${it.str}'" }.joinToString(" or ")
            }"
        )
        return when(matchType){
            JudgeType.EQUAL -> {
                judgeBaseRegex.containsMatchIn(judgeTargetStr)
            }
            JudgeType.NOT_EQUAL -> {
                !judgeBaseRegex.containsMatchIn(judgeTargetStr)
            }
        } to null

    }

    class IfCheckErr(
        val errMessage: String
    )

    private fun checkArgs(
        argsPairList: List<Pair<String, String>>
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
                    return IfCheckErr(
                        "'if' method all args not exist: args list: ${spanArgsNameListCon}"
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
                return IfCheckErr("'if' method args not exist: name: ${spanArgName}, index: ${spanArgIndex}")
            }
        }
//        argsNameList.forEachIndexed {
//                index, argName ->
//            val argPair = argsPairList.getOrNull(index)
//                ?: return true
//            if(
//                argPair.first.isEmpty()
//            ) return true
//        }
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

}