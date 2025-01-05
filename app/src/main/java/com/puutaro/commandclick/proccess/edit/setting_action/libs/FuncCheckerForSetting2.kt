package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import java.io.File

object FuncCheckerForSetting2 {

    class FuncCheckErr(
        val errMessage: String
    )


    fun checkArgs(
        funcName: String,
        methodName: String,
        baseArgsNameToTypeList: List<Pair<String, ArgType>>?,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>?,
    ): FuncCheckErr? {
        if(
            baseArgsNameToTypeList.isNullOrEmpty()
        ) return null
        baseArgsNameToTypeList.forEachIndexed {
                index, argNameToType ->
            val argName = argNameToType.first
            val argType = argNameToType.second
            val argPair = argsPairList.getOrNull(index)
                ?: let {
                    val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        funcName
                    )
                    val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        methodName
                    )
                    val spanBaseArgsNameListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        baseArgsNameToTypeList.joinToString(", ")
                    )
                    return FuncCheckErr(
                        "Method all args not exist: func.method: ${spanFuncName}.${spanMethodName}: " +
                                "args list: ${spanBaseArgsNameListCon}"
                    )
                }
            val userDefiniteArgName = argPair.first
            if(
                userDefiniteArgName.isEmpty()
            ) {
                val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    funcName
                )
                val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    methodName
                )
                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    argName
                )
                val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    (index + 1).toString()
                )
                val spanArgType = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    argType.name
                )
                return FuncCheckErr(
                    "Method args not exist: func.method: ${spanFuncName}.${spanMethodName}: name: ${spanArgName}, index: ${spanArgIndex}, type: ${spanArgType}"
                )
            }
            val userDefiniteArgStr = argPair.second
            ArgsTypeChecker.check(
                funcName,
                methodName,
                argName,
                index,
                userDefiniteArgStr,
                argType,
//                varNameToValueStrMap,
            ).let {
                funcCheckErr ->
                if(
                    funcCheckErr == null
                ) return@let
                return funcCheckErr
            }
        }
        return null
    }

    private object ArgsTypeChecker{
        fun check(
            funcName: String,
            methodName: String,
            argName: String,
            index: Int,
            argStr: String,
            argType: ArgType,
//            varNameToValueStrMap: Map<String, String?>?,
        ): FuncCheckErr? {
            val valueStrToErr = isNotExistStringVarName(
                funcName,
                methodName,
                argStr,
                argName,
                index,
                argType,
//                varNameToValueStrMap,
            )
            val funcCheckErr = valueStrToErr?.second
            if(
                funcCheckErr != null
            ) return funcCheckErr
            val argValueStr = valueStrToErr?.first
                ?: return null
            return try {
                when (argType) {
                    ArgType.STRING ->
                        null
                    ArgType.PATH -> {
                        if(
                            File(argValueStr).isFile
                        ) null
                        else launchTypeCheckErr(
                            funcName,
                            methodName,
                            argName,
                            index,
                            argType,
                            argStr,
                            String()
                        )
                    }
                    ArgType.INT -> {
                        argValueStr.toInt()
                        null
                    }
                    ArgType.FLOAT -> {
                        argValueStr.toFloat()
                        null
                    }
                    ArgType.LONG -> {
                        argValueStr.toLong()
                            null
                    }
                    ArgType.BOOL -> {
                        argValueStr.toBoolean()
                        null
                    }
                }
            } catch (e: Exception){
                launchTypeCheckErr(
                    funcName,
                    methodName,
                    argName,
                    index,
                    argType,
                    argStr,
                    String(),
                )
            }

        }

        private fun isNotExistStringVarName(
            funcName: String,
            methodName: String,
            argStr: String,
            argName: String,
            index: Int,
            argType: ArgType,
//            varNameToValueStrMap: Map<String, String?>?,
        ): Pair<String?, FuncCheckErr?>? {
            if(
                !SettingActionKeyManager.ValueStrVar.matchStringVarName(argStr)
            ) return argStr to null
            return null to launchTypeCheckErr(
                funcName,
                methodName,
                argName,
                index,
                argType,
                argStr,
                "not exist string var name"
            )
        }
////            SettingActionKeyManager.ValueStrVar.matchStringVarName(argStr).let {
////                    isStrVarRegex ->
////                if(isStrVarRegex) return@let
////            }
//            val strKey = SettingActionKeyManager.ValueStrVar.convertStrKey(argStr)
//            val runPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
//            (strKey.startsWith(runPrefix)).let {
//                    isRunPrefix ->
//                if(!isRunPrefix) return@let
//                return null to launchTypeCheckErr(
//                    funcName,
//                    methodName,
//                    argName,
//                    index,
//                    argType,
//                    argStr,
//                    "disables ${runPrefix} prefix"
//                )
//            }
//            val valueStr =
//                varNameToValueStrMap?.get(strKey)
//            if(
//                valueStr is String
//            ) return valueStr to null
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultSDebugAppDirPath, "lfuncarg.txt").absolutePath,
////                listOf(
////                    "argStr: ${argStr}",
////                    "valueStr: ${valueStr}",
////                    "varNameToValueStrMap: ${varNameToValueStrMap}",
////                ).joinToString("\n") + "\n\n====\n\n"
////            )
//            return null to launchTypeCheckErr(
//                    funcName,
//                    methodName,
//                    argName,
//                    index,
//                    argType,
//                    argStr,
//                    "not exist string var name"
//                )
//            }
        }

        private fun launchTypeCheckErr(
            funcName: String,
            methodName: String,
            argName: String,
            index: Int,
            argType: ArgType,
            argStr: String,
            bitmapErrBody: String,
        ): FuncCheckErr {
            val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                methodName
            )
            val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                argName
            )
            val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                (index + 1).toString()
            )
            val spanArgType = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                argType.name
            )
            val spanArgStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                argStr
            )
            return when(argType) {
                ArgType.PATH ->
                    FuncCheckErr(
                        "Arg ${spanArgName} not found path: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}"
                    )
                else -> {
                    val errBodyMsg = bitmapErrBody.ifEmpty {
                        "not ${spanArgType} type"
                    }
                    FuncCheckErr(
                        "Arg ${spanArgName} ${errBodyMsg}: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}"
                    )
                }
            }
        }

    enum class ArgType {
        PATH,
        STRING,
        INT,
        FLOAT,
        LONG,
        BOOL,
    }
}