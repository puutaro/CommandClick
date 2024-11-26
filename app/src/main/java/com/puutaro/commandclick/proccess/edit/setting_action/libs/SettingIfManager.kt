package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object SettingIfManager {

    fun handle(
        judgeTargetStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Boolean? {
        val isErr = checkArgs(
            argsPairList
        )
        if(isErr) return null
        val judgeBaseRegex = try {
            argsPairList.get(0).second.toRegex()
        } catch (e: Exception){
            return null
        }
        val matchTypeStr = argsPairList.get(1).second
        val matchType = JudgeType.entries.firstOrNull {
            it.str == matchTypeStr
        } ?: return null
        return when(matchType){
            JudgeType.EQUAL -> {
                judgeBaseRegex.containsMatchIn(judgeTargetStr)
            }
            JudgeType.NOT_EQUAL -> {
                !judgeBaseRegex.containsMatchIn(judgeTargetStr)
            }
        }

    }

    private fun checkArgs(
        argsPairList: List<Pair<String, String>>
    ): Boolean {
        argsNameList.forEachIndexed {
                index, argName ->
            val argPair = argsPairList.getOrNull(index)
                ?: return true
            if(
                argPair.first.isEmpty()
            ) return true
        }
        return false
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