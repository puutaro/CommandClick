package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.res.FannelIcons
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ColorTool
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
                is RndMethodArgClass.ColorArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val colorRnd = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.colorKeyToDefaultValueStr,
                        where
                    ).let { minIntToErr ->
                        val funcErr = minIntToErr.second
                            ?: return@let minIntToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }.let {
                        colorMacroStr ->
                        ColorTool.ColorRndStr.entries.firstOrNull {
                            it.name == colorMacroStr
                        } ?: ColorTool.ColorRndStr.RND
                    }
                    Pair(
                        ColorTool.parseColorMacro(colorRnd.name),
                        null,
                    ) to null
                }
                is RndMethodArgClass.IconArgs -> {
                    Pair(
                        CmdClickIcons.entries.random().str,
                        null,
                    ) to null
                }
                is RndMethodArgClass.FannelIconArgs -> {
                    Pair(
                        FannelIcons.entries.random().str,
                        null,
                    ) to null
                }
                is RndMethodArgClass.BkIconArgs -> {
                    Pair(
                        makeBkIconStr(),
                        null,
                    ) to null
                }
            }
        }
    }

    private fun makeBkIconStr(): String{
        return listOf(
            CmdClickIcons.ROUND_RECT,
            CmdClickIcons.DEBUG,
            CmdClickIcons.FILE,
            CmdClickIcons.EXTRA,
            CmdClickIcons.JS,
            CmdClickIcons.LIST,
            CmdClickIcons.PIN_LOCATION,
            CmdClickIcons.PREF,
            CmdClickIcons.STAR,
            CmdClickIcons.SPIDER,
            CmdClickIcons.BLACK_HISTORY,
        ).random().str
    }


    private enum class MethodNameClass(
        val str: String,
        val args: RndMethodArgClass,
    ){
        RANGE(
            "range",
            RndMethodArgClass.RangeArgs,
        ),
        ICON(
            "icon",
            RndMethodArgClass.IconArgs,
        ),
        FANNEL_ICON(
            "fannelIcon",
            RndMethodArgClass.FannelIconArgs,
        ),
        BK_ICON(
            "bkIcon",
            RndMethodArgClass.BkIconArgs,
        ),
        COLOR(
            "color",
            RndMethodArgClass.ColorArgs,
        )
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
        data object ColorArgs : RndMethodArgClass(), ArgType {
            override val entries = ColorEnumArgs.entries
            val colorKeyToDefaultValueStr = Pair(
                ColorEnumArgs.TYPE.key,
                ColorEnumArgs.TYPE.defaultValueStr
            )

            enum class ColorEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                TYPE("type", ColorTool.ColorRndStr.RND.name, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object IconArgs : RndMethodArgClass()
        data object FannelIconArgs : RndMethodArgClass()
        data object BkIconArgs : RndMethodArgClass()
    }
}
