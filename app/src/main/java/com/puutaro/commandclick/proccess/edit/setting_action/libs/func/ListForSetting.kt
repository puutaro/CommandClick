package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ListForSetting {
    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
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
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameToTypeList,
            argsPairList,
//            varNameToValueStrMap
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
                MethodNameClass.JOIN -> {
                    val con = argsList.get(0)
                    val separator = argsList.get(1)
                    val joinStr = argsList.get(2)
                    con.split(separator).joinToString(joinStr)
                }
            }
        }
        return Pair(
            settingValueStr,
            null
        ) to null
    }

    enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting.ArgType>>,
    ) {
        RND("rnd", rndArgsNameToTypeList),
        SHUF("shuf", rndArgsNameToTypeList),
        TAKE("take", takeArgsNameToTypeList),
        TAKE_LAST("takeLast", takeArgsNameToTypeList),
        JOIN("join", joinArgsNameToTypeList)
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
    private val joinArgsNameToTypeList = listOf(
        Pair(
            "strs",
            FuncCheckerForSetting.ArgType.STRING,
        ),
        Pair(
            "separator",
            FuncCheckerForSetting.ArgType.STRING,
        ),
        Pair(
            "joinStr",
            FuncCheckerForSetting.ArgType.STRING,
        )
    )
}
