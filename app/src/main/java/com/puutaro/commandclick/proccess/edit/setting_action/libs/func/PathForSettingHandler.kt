package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting2
import com.puutaro.commandclick.util.CcPathTool
import java.io.File

object PathForSettingHandler {
    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.ExitSignal?
                    >?,
            FuncCheckerForSetting2.FuncCheckErr?
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
        val argsList = argsPairList.map {
            it.second
        }
        return when(methodNameClass){
            MethodNameClass.MAKE_FANNEL_DIR_NAME -> {
                val firstArg = argsList.get(0)
                Pair(
                    CcPathTool.makeFannelDirName(firstArg),
                    null,
                ) to null
            }
            MethodNameClass.TRIM_ALL_EXTEND -> {
                val firstArg = argsList.get(0)
                Pair(
                    CcPathTool.trimAllExtend(firstArg),
                    null
                ) to null
            }
            MethodNameClass.MAKE_FANNEL_RAW_NAME -> {
                val firstArg = argsList.get(0)
                Pair(
                    CcPathTool.makeFannelRawName(firstArg),
                    null
                ) to null
            }
            MethodNameClass.GET_MAIN_APP_DIR_PATH -> {
                val firstArg = argsList.get(0)
                Pair(
                    CcPathTool.getMainAppDirPath(firstArg),
                    null
                ) to null
            }
            MethodNameClass.GET_MAIN_FANNEL_FILE_PATH -> {
                val firstArg = argsList.get(0)
                Pair(
                    CcPathTool.getMainFannelFilePath(firstArg),
                    null,
                ) to null
            }
            MethodNameClass.GET_MAIN_FANNEL_DIR_PATH -> {
                val firstArg = argsList.get(0)
                Pair(
                    CcPathTool.getMainFannelDirPath(firstArg),
                    null,
                ) to null
            }
            MethodNameClass.GET_PARENT_DIR_PATH -> {
                val firstArg = argsList.get(0)
                Pair(
                    File(firstArg).parent,
                    null,
                ) to null
            }
            MethodNameClass.GET_FILE_NAME -> {
                val firstArg = argsList.get(0)
                Pair(
                    File(firstArg).name,
                    null,
                ) to null
            }
            MethodNameClass.IS_FILE -> {
                val firstArg = argsList.get(0)
                Pair(
                    File(firstArg).isFile.toString(),
                    null,
                    ) to null
            }
            MethodNameClass.IS_DIR -> {
                val firstArg = argsList.get(0)
                Pair(
                    File(firstArg).isDirectory.toString(),
                    null,
                    ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting2.ArgType>>,
    ){
        MAKE_FANNEL_DIR_NAME(
            "makeFannelDirName",
            listOf(
                Pair(
                    "fannelNameSrc",
                    FuncCheckerForSetting2.ArgType.STRING,
                )
            )
        ),
        TRIM_ALL_EXTEND(
            "trimAllExtend",
            listOf(
                Pair(
                    "fileName",
                    FuncCheckerForSetting2.ArgType.STRING,
                )
            )
        ),
        MAKE_FANNEL_RAW_NAME(
            "makeFannelRawName",
            listOf(
                Pair(
                    "fannelNameSrc",
                    FuncCheckerForSetting2.ArgType.STRING,
                    )
            )
        ),
        GET_MAIN_APP_DIR_PATH(
            "getMainAppDirPath",
            listOf(
                Pair(
                    "currentSubFannelPath",
                    FuncCheckerForSetting2.ArgType.STRING,
                    )
            )
        ),
        GET_MAIN_FANNEL_FILE_PATH(
            "getMainFannelFilePath",
            listOf(
                Pair(
                    "currentSubFannelPath",
                    FuncCheckerForSetting2.ArgType.STRING,
                    )
            )
        ),
        GET_MAIN_FANNEL_DIR_PATH(
            "getMainFannelDirPath",
            listOf(
                Pair(
                    "currentSubFannelPath",
                    FuncCheckerForSetting2.ArgType.STRING,
                    )
            )
        ),
        GET_PARENT_DIR_PATH(
            "getParentDirPath",
            listOf(
                Pair(
                    "filePath",
                    FuncCheckerForSetting2.ArgType.STRING,
                    )
            )
        ),
        GET_FILE_NAME(
            "getFileName",
            listOf(
                Pair(
                    "filePath",
                    FuncCheckerForSetting2.ArgType.STRING,
                )
            )
        ),
        IS_FILE(
            "isFile",
            listOf(
                Pair(
                    "filePath",
                    FuncCheckerForSetting2.ArgType.STRING
                )
            )
        ),
        IS_DIR(
            "isDir",
            listOf(
                Pair(
                    "isDir",
                    FuncCheckerForSetting2.ArgType.STRING,
                    )
            )
        ),
    }
}
