package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool

object FuncCheckerForSetting {

    class FuncCheckErr(
        val errMessage: String
    )


    fun checkArgs(
        funcName: String,
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
                    val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        argName
                    )
                    return FuncCheckErr("Method args not exist: ${spanFuncName}.${spanArgName}")
                }
            if(
                argPair.first.isEmpty()
            ) {
                val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    funcName
                )
                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    argName
                )
                return FuncCheckErr("Method args name must not be blank: ${spanFuncName}.${spanArgName}")
            }
        }
        return null
    }
}