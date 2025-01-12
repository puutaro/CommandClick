package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object RndForSetting {
    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
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
                is RndMethodArgClass.RangeArgs -> {
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
                    val minInt = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.minIntKeyToIndex,
                        where
                    ).let { minIntToErr ->
                        val funcErr = minIntToErr.second
                            ?: return@let minIntToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val maxInt = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.maxIntKeyToIndex,
                        where
                    ).let { maxIntToErr ->
                        val funcErr = maxIntToErr.second
                            ?: return@let maxIntToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    if(
                        minInt > maxInt
                    ) {
                        val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errBrown,
                            funcName
                        )
                        val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            methodNameStr
                        )
                        val spanMinIntStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            "${args.minIntKeyToIndex.first}(${minInt})"
                        )
                        val spanMaxIntStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            "${args.maxIntKeyToIndex.first}(${maxInt})"
                        )
                        Pair(
                            null,
                            FuncCheckerForSetting.FuncCheckErr("[ARG ERR] ${spanMinIntStr} must be lower than ${spanMaxIntStr}: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
                        )
                    }
                    Pair(
                        (minInt..maxInt).random().toString(),
                        null,
                    ) to null
                }
            }
        }
    }


    private enum class MethodNameClass(
        val str: String,
        val args: RndMethodArgClass,
    ){
        RANGE(
            "range",
            RndMethodArgClass.RangeArgs,
        ),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class RndMethodArgClass {
        data object RangeArgs : RndMethodArgClass(), ArgType {
            override val entries = RangeEnumArgs.entries
            val minIntKeyToIndex = Pair(
                RangeEnumArgs.MIN_INT.key,
                RangeEnumArgs.MIN_INT.index
            )
            val maxIntKeyToIndex = Pair(
                RangeEnumArgs.MAX_INT.key,
                RangeEnumArgs.MAX_INT.index
            )

            enum class RangeEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                MIN_INT("minInt", 0, FuncCheckerForSetting.ArgType.INT),
                MAX_INT("maxInt", 0, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}
