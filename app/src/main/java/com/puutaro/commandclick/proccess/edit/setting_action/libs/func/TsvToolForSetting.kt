package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.util.tsv.TsvTool

object TsvToolForSetting {
    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ): Pair<String?, FuncCheckerForSetting.FuncCheckErr?> {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        }  ?: let {
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
            methodNameClass.argsNameList,
            argsPairList
        )?.let {
                argsCheckErr ->
            return null to argsCheckErr
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "settingCheck.txt").absolutePath,
//            listOf(
//                "isErr: ${isErr}",
//            ).joinToString("\n")
//        )
        val argsList = argsPairList.map {
            it.second
        }
        return when(methodNameClass){
            MethodNameClass.GET_KEY_VALUE_FROM_FILE -> {
                val firstArg = argsList.get(0)
                val secondArg = argsList.get(1)
                TsvTool.getKeyValueFromFile(
                    firstArg,
                    secondArg,
                )
            }
            MethodNameClass.GET_KEY_VALUE -> {
                val firstArg = argsList.get(0)
                val secondArg = argsList.get(1)
                TsvTool.getKeyValue(
                    firstArg,
                    secondArg,
                )
            }
        } to null
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameList: List<String>,
    ){
        GET_KEY_VALUE_FROM_FILE("getKeyValueFromFile", filePathAndKeyArgsNameList),
        GET_KEY_VALUE("getKeyValue", conAndKeyArgsNameList),
    }

    private val filePathAndKeyArgsNameList = listOf(
        "filePath",
        "key",

    )


    private val conAndKeyArgsNameList = listOf(
        "tsvCon",
        "key",
    )

}