package com.puutaro.commandclick.proccess.edit.image_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager


object ImageIfManager {

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
                    .replace("%", "％    ")
            )
            val spanIIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                ImageActionKeyManager.ImageSubKey.I_IF.key
            )
            return null to IfCheckErr("Failure to compile '${spanIIfKey}' method args regex: ${spanJudgeBaseRegexStr}")
        }
        val matchTypeStr = argsPairList.get(1).second
        val matchType = JudgeType.entries.firstOrNull {
            it.str == matchTypeStr
        } ?: let {
            val spanIIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                ImageActionKeyManager.ImageSubKey.I_IF.key
            )
            return null to IfCheckErr(
                "'${spanIIfKey}' Match type must be ${
                    JudgeType.entries.map { "'${it.str}'" }.joinToString(" or ")
                }"
            )
        }
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
                    val spanIIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        ImageActionKeyManager.ImageSubKey.I_IF.key
                    )
                    return IfCheckErr(
                        "'${spanIIfKey}' method all args not exist: args list: ${spanArgsNameListCon}"
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
                val spanIIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    ImageActionKeyManager.ImageSubKey.I_IF.key
                )
                return IfCheckErr("'${spanIIfKey}' method args not exist: name: ${spanArgName}, index: ${spanArgIndex}")
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

}