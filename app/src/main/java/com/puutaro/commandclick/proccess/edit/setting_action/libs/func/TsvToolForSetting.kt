package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.tsv.TsvTool

object TsvToolForSetting {
    fun handle(
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ): String? {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: return null
        val isErr = ArgsChecker.checkArgs(
            methodNameClass.argsNameList,
            argsPairList
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "settingCheck.txt").absolutePath,
//            listOf(
//                "isErr: ${isErr}",
//            ).joinToString("\n")
//        )
        if(isErr) return null
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
        }
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