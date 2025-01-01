package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ListForSetting {
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
                MethodNameClass.RND -> {
                    val listCon = argsList.get(0)
                    val separator = argsList.get(1)
                    listCon.split(separator).filter{
                        it.trim().isNotEmpty()
                    }.random()
                }
                MethodNameClass.SHUF -> {
                    val listCon = argsList.get(0)
                    val separator = argsList.get(1)
                    listCon.split(separator).filter{
                        it.trim().isNotEmpty()
                    }.shuffled().joinToString(separator)
                }
                MethodNameClass.TAKE -> {
                    val listCon = argsList.get(0)
                    val separator = argsList.get(1)
                    val takeNum = argsList.get(2).toInt()
                    listCon.split(separator).filter{
                        it.trim().isNotEmpty()
                    }.take(takeNum).joinToString(separator)
                }
                MethodNameClass.TAKE_LAST -> {
                    val listCon = argsList.get(0)
                    val separator = argsList.get(1)
                    val takeLastNum = argsList.get(2).toInt()
                    listCon.split(separator).filter{
                        it.trim().isNotEmpty()
                    }.takeLast(takeLastNum).joinToString(separator)
                }
            }
        }
        return settingValueStr to null
    }

    enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting.ArgType>>,
    ) {
        RND("rnd", rndArgsNameToTypeList),
        SHUF("shuf", rndArgsNameToTypeList),
        TAKE("take", takeArgsNameToTypeList),
        TAKE_LAST("takeLast", takeArgsNameToTypeList),
    }

    private val rndArgsNameToTypeList = listOf(
        Pair(
            "strs",
            FuncCheckerForSetting.ArgType.STRING,
        ),
        Pair(
            "separator",
            FuncCheckerForSetting.ArgType.STRING
        )
    )

    private val takeArgsNameToTypeList = listOf(
        Pair(
            "strs",
            FuncCheckerForSetting.ArgType.STRING,
            ),
        Pair(
            "separator",
            FuncCheckerForSetting.ArgType.STRING,
            ),
        Pair(
            "num",
            FuncCheckerForSetting.ArgType.INT,
            )
    )
}
