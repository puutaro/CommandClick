package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting2
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText

object FileSystemsForSettingHandler {

    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting2.FuncCheckErr?
            >?
    //Pair<String?, FuncCheckerForSetting.FuncCheckErr?>
    {
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
            return null to FuncCheckerForSetting2.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting2.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameToTypeList,
            argsPairList,
//            varNameToValueStrMap,
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
                val filePath =
                    argsList.get(0)
//                SettingFuncTool.getValueStrFromMapOrIt(
//                    argsList.get(0),
//                    varNameToValueStrMap,
//                ) ?: return null
//                val valurStrKey =
//                    ImageActionKeyManager.BitmapVar.convertBitmapKey(
//                        argsList.get(0)
//                    )
//                val filePath = varNameToValueStrMap.get(valurStrKey)
//                    ?: return null
                Pair(
                    ReadText(filePath).readText(),
                    null
                ) to null
            }
            MethodNameClass.WRITE -> {
                val firstArg = argsList.get(0)
//                    SettingFuncTool.getValueStrFromMapOrIt(
//                    argsList.get(0),
//                    varNameToValueStrMap,
//                ) ?: return null
                val secondArg = argsList.get(1)
//                SettingFuncTool.getValueStrFromMapOrIt(
//                    argsList.get(1),
//                    varNameToValueStrMap,
//                ) ?: return null
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
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting2.ArgType>>,
    ){
        READ("read", readArgsNameToTypeList),
        WRITE("write", writeArgsNameToTypeList),
        UPDATE_WRITE("updateWrite", writeArgsNameToTypeList),
        REMOVE("remove", readArgsNameToTypeList),
    }

    private val readArgsNameToTypeList = listOf(
        Pair("filePath", FuncCheckerForSetting2.ArgType.PATH)
    )


    private val writeArgsNameToTypeList = listOf(
        Pair("filePath", FuncCheckerForSetting2.ArgType.STRING),
        Pair("contents", FuncCheckerForSetting2.ArgType.STRING),
    )

}