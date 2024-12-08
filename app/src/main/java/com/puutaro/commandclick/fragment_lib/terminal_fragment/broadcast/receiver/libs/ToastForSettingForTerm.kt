package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.libs

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ToastForSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ToastForSettingForTerm {
    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ): FuncCheckerForSetting.FuncCheckErr? {
        val methodNameClass = ToastForSetting.MethodNameClass.entries.firstOrNull {
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
            return FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.readArgsNameList,
            argsPairList
        )?.let {
                argsCheckErr ->
            return argsCheckErr
        }
        val argsList = argsPairList.map {
            it.second
        }
        val firstArg = argsList.get(0)
        CoroutineScope(Dispatchers.Main).launch {
            when (methodNameClass) {
                ToastForSetting.MethodNameClass.SHORT -> {
                    ToastUtils.showShort(
                        firstArg
                    )

                }

                ToastForSetting.MethodNameClass.LONG -> {
                    ToastUtils.showLong(
                        firstArg
                    )
                }
            }
        }
        return null
    }
}