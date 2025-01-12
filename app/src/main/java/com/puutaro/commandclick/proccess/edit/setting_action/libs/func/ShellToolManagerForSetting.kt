package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.str.QuoteTool
import kotlin.enums.EnumEntries

object ShellToolManagerForSetting {

    private val cmdSeparator = '|'

    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        busyboxExecutor: BusyboxExecutor?,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
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
        val funcCheckerForSetting = FuncCheckerForSetting(
            funcName,
            methodNameStr,
        )
        val args =
            methodNameClass.args
        when(args){
            is ShellMethodArgClass.ExecArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
                    Triple(
                        index,
                        formalArgsNameToType.key,
                        formalArgsNameToType.type,
                    )
                }
                val mapArgMapList = FuncCheckerForSetting.Companion.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.makeWhereFromList(
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val cmdStr =
                    funcCheckerForSetting.getStringFromArgMapByIndex(
                        funcCheckerForSetting,
                        mapArgMapList,
                        args.cmdListKeyToIndex,
                        where
                    ).let { cmdStrToErr ->
                    val funcErr = cmdStrToErr.second
                        ?: return@let cmdStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val cmd = QuoteTool.splitBySurroundedIgnore(
                    cmdStr,
                    cmdSeparator
                ).map {
                    "${'$'}{b} ${it} "
                }.joinToString(cmdSeparator.toString())
                return Pair (
                    busyboxExecutor?.getCmdOutput(
                        cmd,
                        null
                    ),
                    null
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ShellMethodArgClass,
    ){
        EXEC("exec", ShellMethodArgClass.ExecArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ShellMethodArgClass {
        data object ExecArgs : ShellMethodArgClass(), ArgType {
            override val entries = RangeEnumArgs.entries
            val cmdListKeyToIndex = Pair(
                RangeEnumArgs.CMD_LIST.key,
                RangeEnumArgs.CMD_LIST.index
            )

            enum class RangeEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.Companion.ArgType,
            ){
                CMD_LIST("cmdList", 0, FuncCheckerForSetting.Companion.ArgType.STRING),

            }
        }
    }
}