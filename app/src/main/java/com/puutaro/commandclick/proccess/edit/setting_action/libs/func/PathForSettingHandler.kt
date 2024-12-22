package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.util.CcPathTool
import java.io.File

object PathForSettingHandler {
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameToTypeList,
            argsPairList
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
                CcPathTool.makeFannelDirName(firstArg)
            }
            MethodNameClass.TRIM_ALL_EXTEND -> {
                val firstArg = argsList.get(0)
                CcPathTool.trimAllExtend(firstArg)
            }
            MethodNameClass.MAKE_FANNEL_RAW_NAME -> {
                val firstArg = argsList.get(0)
                CcPathTool.makeFannelRawName(firstArg)
            }
            MethodNameClass.GET_MAIN_APP_DIR_PATH -> {
                val firstArg = argsList.get(0)
                CcPathTool.getMainAppDirPath(firstArg)
            }
            MethodNameClass.GET_MAIN_FANNEL_FILE_PATH -> {
                val firstArg = argsList.get(0)
                CcPathTool.getMainFannelFilePath(firstArg)
            }
            MethodNameClass.GET_MAIN_FANNEL_DIR_PATH -> {
                val firstArg = argsList.get(0)
                CcPathTool.getMainFannelDirPath(firstArg)
            }
            MethodNameClass.GET_PARENT_DIR_PATH -> {
                val firstArg = argsList.get(0)
                File(firstArg).parent
            }
            MethodNameClass.GET_FILE_NAME -> {
                val firstArg = argsList.get(0)
                File(firstArg).name
            }
            MethodNameClass.IS_FILE -> {
                val firstArg = argsList.get(0)
                File(firstArg).isFile.toString()
            }
            MethodNameClass.IS_DIR -> {
                val firstArg = argsList.get(0)
                File(firstArg).isDirectory.toString()
            }
        } to null
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting.ArgType>>,
    ){
        MAKE_FANNEL_DIR_NAME(
            "makeFannelDirName",
            listOf(
                Pair(
                    "fannelNameSrc",
                    FuncCheckerForSetting.ArgType.STRING,
                )
            )
        ),
        TRIM_ALL_EXTEND(
            "trimAllExtend",
            listOf(
                Pair(
                    "fileName",
                    FuncCheckerForSetting.ArgType.STRING,
                )
            )
        ),
        MAKE_FANNEL_RAW_NAME(
            "makeFannelRawName",
            listOf(
                Pair(
                    "fannelNameSrc",
                    FuncCheckerForSetting.ArgType.STRING,
                    )
            )
        ),
        GET_MAIN_APP_DIR_PATH(
            "getMainAppDirPath",
            listOf(
                Pair(
                    "currentSubFannelPath",
                    FuncCheckerForSetting.ArgType.STRING,
                    )
            )
        ),
        GET_MAIN_FANNEL_FILE_PATH(
            "getMainFannelFilePath",
            listOf(
                Pair(
                    "currentSubFannelPath",
                    FuncCheckerForSetting.ArgType.STRING,
                    )
            )
        ),
        GET_MAIN_FANNEL_DIR_PATH(
            "getMainFannelDirPath",
            listOf(
                Pair(
                    "currentSubFannelPath",
                    FuncCheckerForSetting.ArgType.STRING,
                    )
            )
        ),
        GET_PARENT_DIR_PATH(
            "getParentDirPath",
            listOf(
                Pair(
                    "filePath",
                    FuncCheckerForSetting.ArgType.STRING,
                    )
            )
        ),
        GET_FILE_NAME(
            "getFileName",
            listOf(
                Pair(
                    "filePath",
                    FuncCheckerForSetting.ArgType.STRING,
                )
            )
        ),
        IS_FILE(
            "isFile",
            listOf(
                Pair(
                    "filePath",
                    FuncCheckerForSetting.ArgType.STRING
                )
            )
        ),
        IS_DIR(
            "isDir",
            listOf(
                Pair(
                    "isDir",
                    FuncCheckerForSetting.ArgType.STRING,
                    )
            )
        ),
    }
}
