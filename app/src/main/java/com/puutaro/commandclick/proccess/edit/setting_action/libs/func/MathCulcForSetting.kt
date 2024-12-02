package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.func.MathCulc


object MathCulcForSetting {
    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ): Pair<String?, FuncCheckerForSetting.FuncCheckErr?> {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameList,
            argsPairList
        )?.let {
                argsCheckErr ->
            return null to argsCheckErr
        }
        val argsList = argsPairList.map {
            it.second
        }
        val firstArg = argsList.get(0)
        val result = try {
            when (methodNameClass) {
                MethodNameClass.FLOAT -> {
                    MathCulc.float(firstArg).toString()
                }

                MethodNameClass.INT -> {
                    MathCulc.int(firstArg).toString()
                }
            }
        } catch (e: Exception){
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            val spanFirstArgStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                firstArg
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Formula err: ${spanFuncTypeStr}.${spanMethodNameStr}, arg: ${spanFirstArgStr}")
        }
        return result to null
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameList: List<String>,
    ){
        INT("int", listOf("formula")),
        FLOAT("float", listOf("formula")),
    }
}
