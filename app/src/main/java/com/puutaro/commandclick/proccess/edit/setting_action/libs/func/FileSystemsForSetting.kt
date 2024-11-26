package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText

object FileSystemsForSettingHandler {

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
        }
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