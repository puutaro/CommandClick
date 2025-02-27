package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object InclineForSetting {
    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
//        FuncCheckerForSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.argsNameToTypeList,
//            argsPairList,
////            varNameToValueStrMap,
//        )?.let { argsCheckErr ->
//            return null to argsCheckErr
//        }
//        val argsList = argsPairList.map {
//            it.second
//        }
        val args =
            methodNameClass.args
        return withContext(Dispatchers.Main) {
            when (args) {
                is InclineMethodArgClass.LinearArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val x1 = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.x1KeyToIndex,
                        where
                    ).let { minIntToErr ->
                        val funcErr = minIntToErr.second
                            ?: return@let minIntToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    val y1 = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.y1KeyToIndex,
                        where
                    ).let { maxIntToErr ->
                        val funcErr = maxIntToErr.second
                            ?: return@let maxIntToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    val x2 = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.x2KeyToIndex,
                        where
                    ).let { x2ToErr ->
                        val funcErr = x2ToErr.second
                            ?: return@let x2ToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    val y2 = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.y2KeyToIndex,
                        where
                    ).let { y2ToErr ->
                        val funcErr = y2ToErr.second
                            ?: return@let y2ToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    val x = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.xKeyToIndex,
                        where
                    ).let { xToErr ->
                        val funcErr = xToErr.second
                            ?: return@let xToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    val y = let {
                        val culcSize = (
                                ((y2 - y1) * (x - x1)) / (x2 - x1)
                                ) + y1
//                        val culcSize = incline * (x - x1) + y1
                        if (
                            culcSize <= 0
                        ) return@let 0
                        culcSize
                    }.toInt().toString()
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lincline.txt").absolutePath,
//                        listOf(
//                            "x1: ${x1}",
//                            "y1: ${y1}",
//                            "x2: ${x2}",
//                            "y2: ${y2}",
//                            "x: ${x}",
//                            "y: ${y}",
//                        ).joinToString("\n") + "\n\n========\n\n"
//                    )
                    Pair(
                        y,
                        null
                    ) to null
                }
            }
        }
    }


    private enum class MethodNameClass(
        val str: String,
        val args: InclineMethodArgClass,
    ){
        LINEAR(
            "linear",
            InclineMethodArgClass.LinearArgs,
        ),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class InclineMethodArgClass {
        data object LinearArgs : InclineMethodArgClass(), ArgType {
            override val entries = LinearEnumArgs.entries
            val x1KeyToIndex = Pair(
                LinearEnumArgs.X1.key,
                LinearEnumArgs.X1.index
            )
            val y1KeyToIndex = Pair(
                LinearEnumArgs.Y1.key,
                LinearEnumArgs.Y1.index
            )
            val x2KeyToIndex = Pair(
                LinearEnumArgs.X2.key,
                LinearEnumArgs.X2.index
            )
            val y2KeyToIndex = Pair(
                LinearEnumArgs.Y2.key,
                LinearEnumArgs.Y2.index
            )
            val xKeyToIndex = Pair(
                LinearEnumArgs.X.key,
                LinearEnumArgs.X.index
            )

            enum class LinearEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                X1("x1", 0, FuncCheckerForSetting.ArgType.INT),
                Y1("y1", 1, FuncCheckerForSetting.ArgType.INT),
                X2("x2", 2, FuncCheckerForSetting.ArgType.INT),
                Y2("y2", 3, FuncCheckerForSetting.ArgType.INT),
                X("x", 4, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}
