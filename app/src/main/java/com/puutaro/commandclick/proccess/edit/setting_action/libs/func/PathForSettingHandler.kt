package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.util.CcPathTool
import java.io.File

object PathForSettingHandler {
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
        if(isErr) return null
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
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameList: List<String>,
    ){
        MAKE_FANNEL_DIR_NAME("makeFannelDirName", listOf("fannelNameSrc")),
        TRIM_ALL_EXTEND("trimAllExtend", listOf("fileName")),
        MAKE_FANNEL_RAW_NAME("makeFannelRawName", listOf("fannelNameSrc")),
        GET_MAIN_APP_DIR_PATH("getMainAppDirPath", listOf("currentSubFannelPath")),
        GET_MAIN_FANNEL_FILE_PATH("getMainFannelFilePath", listOf("currentSubFannelPath")),
        GET_MAIN_FANNEL_DIR_PATH("getMainFannelDirPath", listOf("currentSubFannelPath")),
        GET_PARENT_DIR_PATH("getParentDirPath", listOf("filePath")),
        GET_FILE_NAME("getFileName", listOf("filePath")),
        IS_FILE("isFile", listOf("filePath")),
        IS_DIR("isDir", listOf("isDir")),
    }
}
