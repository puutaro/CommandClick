package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object ImageFuncCheckerForImageSetting {

    class FuncCheckErr(
        val errMessage: String
    )


    fun checkArgs(
        funcName: String,
        methodName: String,
        baseArgsNameToTypeList: List<Pair<String, ArgType>>?,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>?,
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
                varNameToBitmapMap,
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
            varNameToBitmapMap: Map<String, Bitmap?>?,
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
                            String()
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
                    ArgType.BITMAP_VAR_NAME -> {
                        ImageActionKeyManager.BitmapVar.matchBitmapVarName(argStr).let {
                            isBitmapRegex ->
                            if(isBitmapRegex) return@let
                            return launchTypeCheckErr(
                                funcName,
                                methodName,
                                argName,
                                index,
                                argType,
                                argStr,
                                "not bitmap str (#{(^run)a-zA-Z0-9+})"
                            )
                        }
                        val bitmapKey = ImageActionKeyManager.BitmapVar.convertBitmapKey(argStr)
                        val runPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
                        (bitmapKey.startsWith(runPrefix)).let {
                                isRunPrefix ->
                            if(!isRunPrefix) return@let
                            return launchTypeCheckErr(
                                funcName,
                                methodName,
                                argName,
                                index,
                                argType,
                                argStr,
                                "disables ${runPrefix} prefix"
                            )
                        }
                        (varNameToBitmapMap?.get(
                            bitmapKey
                        ) is Bitmap).let {
                                isBitmapRegex ->
                            if(isBitmapRegex) return@let
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "iarg.txt").absolutePath,
//                                listOf(
//                                    "argStr: ${argStr}",
//                                    "bitmapKey: ${bitmapKey}"
//                                ).joinToString("\n")
//                            )
                            return launchTypeCheckErr(
                                funcName,
                                methodName,
                                argName,
                                index,
                                argType,
                                argStr,
                                "not exist bitmap var name"
                            )
                        }
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
                ArgType.BITMAP_VAR_NAME ->
                    FuncCheckErr(
                        "Arg ${spanArgName} ${bitmapErrBody}: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}"
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
        BITMAP_VAR_NAME,
    }
}