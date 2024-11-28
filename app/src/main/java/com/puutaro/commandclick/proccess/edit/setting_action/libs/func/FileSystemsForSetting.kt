package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText

object FileSystemsForSettingHandler {

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
            MethodNameClass.READ -> {
                val firstArg = argsList.get(0)
                ReadText(
                    firstArg
                ).readText()
            }
            MethodNameClass.WRITE -> {
                val firstArg = argsList.get(0)
                val secondArg = argsList.get(1)
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "setting2.txt").absolutePath,
//                    listOf(
//                        "firstArg: ${firstArg}",
//                        "secondArg: ${secondArg}",
//                    ).joinToString("\n")
//                )
                FileSystems.writeFile(
                    firstArg,
                    secondArg
                )
                null
            }
            MethodNameClass.UPDATE_WRITE -> {
                val firstArg = argsList.get(0)
                val secondArg = argsList.get(1)
                FileSystems.updateFile(
                    firstArg,
                    secondArg
                )
                null
            }
            MethodNameClass.REMOVE -> {
                val firstArg = argsList.get(0)
                FileSystems.removeFiles(
                    firstArg
                )
                null
            }
        } to null
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameList: List<String>,
    ){
        READ("read", readArgsNameList),
        WRITE("write", writeArgsNameList),
        UPDATE_WRITE("updateWrite", writeArgsNameList),
        REMOVE("remove", readArgsNameList),
    }

    private val readArgsNameList = listOf(
        "filePath"
    )


    private val writeArgsNameList = listOf(
        "filePath",
        "contents",
    )

}