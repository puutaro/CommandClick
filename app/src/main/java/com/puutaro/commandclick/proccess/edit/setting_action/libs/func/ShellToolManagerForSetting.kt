package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.str.QuoteTool

object ShellToolManagerForSetting {

    private val cmdSeparator = '|'

    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        busyboxExecutor: BusyboxExecutor?
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameToTypeList,
            argsPairList
        )?.let {
                argsCheckErr ->
            return null to argsCheckErr
        }
        val argsList = argsPairList.map {
            it.second
        }
        val firstArg = argsList.get(0)
        val cmd = QuoteTool.splitBySurroundedIgnore(
            firstArg,
            cmdSeparator
        ).map {
            "${'$'}{b} ${it} "
        }.joinToString(cmdSeparator.toString())
        return busyboxExecutor?.getCmdOutput(
            cmd,
            null
        ) to null
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting.ArgType>>,
    ){
        EXEC("exec", shellArgsNameToTypeList),
    }

    private val  shellArgsNameToTypeList = listOf(
        Pair("cmdList", FuncCheckerForSetting.ArgType.STRING)
    )
}