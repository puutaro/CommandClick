package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RndForSetting {
    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
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
        )?.let { argsCheckErr ->
            return null to argsCheckErr
        }
        val argsList = argsPairList.map {
            it.second
        }
        val settingValueStr = withContext(Dispatchers.Main) {
            when (methodNameClass) {
                MethodNameClass.RANGE -> {
                    val minInt = argsList.get(0).toInt()
                    val maxInt = argsList.get(1).toInt()
                    if(
                        minInt > maxInt
                    ) {
                        val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errBrown,
                            funcName
                        )
                        val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            methodNameStr
                        )
                        val spanMinIntStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            "${MethodNameClass.RANGE.argsNameToTypeList.first().first}(${minInt})"
                        )
                        val spanMaxIntStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            "${MethodNameClass.RANGE.argsNameToTypeList.get(1).first}(${maxInt})"
                        )
                        Pair(
                            null,
                            FuncCheckerForSetting.FuncCheckErr("[ARG ERR] ${spanMinIntStr} must be lower than ${spanMaxIntStr}: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
                        )
                    }
                    (minInt..maxInt).random().toString()
                }
            }
        }
        return settingValueStr to null
    }

    enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting.ArgType>>,
    ) {
        RANGE("range", rangeArgsNameToTypeList),
    }

    private val rangeArgsNameToTypeList = listOf(
        Pair(
            "minInt",
            FuncCheckerForSetting.ArgType.INT,
        ),
        Pair(
            "maxInt",
            FuncCheckerForSetting.ArgType.INT
        )
    )
}
