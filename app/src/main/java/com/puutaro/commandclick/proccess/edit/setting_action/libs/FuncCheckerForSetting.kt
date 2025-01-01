package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import java.io.File

object FuncCheckerForSetting {

    class FuncCheckErr(
        val errMessage: String
    )


    fun checkArgs(
        funcName: String,
        methodName: String,
        baseArgsNameToTypeList: List<Pair<String, ArgType>>?,
        argsPairList: List<Pair<String, String>>
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
        ): FuncCheckErr? {
            return try {
                when (argType) {
                    ArgType.STRING ->
                        null
                    ArgType.PATH -> {
                        if(
                            File(argStr).isFile
                        ) null
                        else launchTypeCheckErr(
                            funcName,
                            methodName,
                            argName,
                            index,
                            argType,
                            argStr,
                        )
                    }
                    ArgType.INT -> {
                        argStr.toInt()
                        null
                    }
                    ArgType.FLOAT -> {
                        argStr.toFloat()
                        null
                    }
                    ArgType.LONG -> {
                            argStr.toLong()
                            null
                    }
                    ArgType.BOOL -> {
                        argStr.toBoolean()
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
                )
            }

        }

        private fun launchTypeCheckErr(
            funcName: String,
            methodName: String,
            argName: String,
            index: Int,
            argType: ArgType,
            argStr: String,
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
                else -> FuncCheckErr(
                    "Arg ${spanArgName} not ${spanArgType} type: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}"
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