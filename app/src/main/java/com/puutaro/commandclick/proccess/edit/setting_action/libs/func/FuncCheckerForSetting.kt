package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool

object FuncCheckerForSetting {

    class FuncCheckErr(
        val errMessage: String
    )


    fun checkArgs(
        funcName: String,
        methodName: String,
        baseArgsNameList: List<String>?,
        argsPairList: List<Pair<String, String>>
    ): FuncCheckErr? {
        if(
            baseArgsNameList.isNullOrEmpty()
        ) return null
        baseArgsNameList.forEachIndexed {
                index, argName ->
            val argPair = argsPairList.getOrNull(index)
                ?: let {
                    val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        funcName
                    )
                    val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        methodName
                    )
                    val spanBaseArgsNameListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        baseArgsNameList.joinToString(", ")
                    )
                    return FuncCheckErr(
                        "Method all args not exist: ${spanFuncName}.${spanMethodName}: " +
                                "args list: ${spanBaseArgsNameListCon}"
                    )
                }
            if(
                argPair.first.isEmpty()
            ) {
                val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    funcName
                )
                val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    methodName
                )
                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    argName
                )
                val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    (index + 1).toString()
                )
                return FuncCheckErr(
                    "Method args not exist: ${spanFuncName}.${spanMethodName}: name: ${spanArgName}, index: ${spanArgIndex}"
                )
            }
        }
        return null
    }
}