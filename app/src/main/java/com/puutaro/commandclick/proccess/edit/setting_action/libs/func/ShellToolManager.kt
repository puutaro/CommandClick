package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.str.QuoteTool

object ShellToolManagerForSetting {

    private val cmdSeparator = '|'

    fun handle(
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        busyboxExecutor: BusyboxExecutor?
    ): String? {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: return null
        val isErr = ArgsChecker.checkArgs(
            methodNameClass.argsNameList,
            argsPairList
        )
        if(isErr) return null
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
        )
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameList: List<String>,
    ){
        EXEC("exec", shellArgsList),
    }

    private val  shellArgsList = listOf(
        "cmdList"
    )
}