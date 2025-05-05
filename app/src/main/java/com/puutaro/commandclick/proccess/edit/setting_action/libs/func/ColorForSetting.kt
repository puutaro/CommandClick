package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ColorTool
import kotlin.enums.EnumEntries

object ColorForSetting {
    suspend fun handle(
        context: Context?,
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
        val args = methodNameClass.args
        return when(args){
            is ColorMethodArgClass.RndArgs -> {
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
                val rndMacroStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.rndMacroKeyToIndex,
                    where
                ).let { (rndMacroStr, funcErr) ->
                    funcErr
                        ?: return@let rndMacroStr
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    ColorTool.parseColorMacro(rndMacroStr)
                        ?: CmdClickColorStr.entries.random().str,
                    null
                ) to null
            }
            is ColorMethodArgClass.OpacityArgs -> {
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
                val colorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.colorStrKeyToIndex,
                    where
                ).let { (rndMacroStr, funcErr) ->
                    funcErr
                        ?: return@let rndMacroStr
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorStrKeyToIndex.first,
                        where,
                    )
                }
                val opacity = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.opacityStrKeyToIndex,
                    where
                ).let { (rndMacroStr, funcErr) ->
                    funcErr
                        ?: return@let rndMacroStr
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val addOpacityColorStr = ColorTool.replaceOpacity(
                    colorStr,
                    opacity,
                )
                Pair(
                    addOpacityColorStr,
                    null
                ) to null
            }
            is ColorMethodArgClass.AvTwoArgs -> {
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
                val colorStr1 = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.color1StrKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.color1StrKeyToIndex.first,
                        where,
                    )
                }
                val colorStr2 = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.color2StrKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.color2StrKeyToIndex.first,
                        where,
                    )
                }
                Pair(
                    ColorTool.averageHexColors(
                        colorStr1,
                        colorStr2,
                    ),
                    null
                ) to null
            }
            is ColorMethodArgClass.WeightAvTwoArgs -> {
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
                val colorStr1 = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.color1StrKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.color1StrKeyToIndex.first,
                        where,
                    )
                }
                val colorStr2 = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.color2StrKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.color2StrKeyToIndex.first,
                        where,
                    )
                }
                val weight1 = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.weight1KeyToIndex,
                    where
                ).let { weightToErr ->
                    val funcErr = weightToErr.second
                        ?: return@let weightToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    if(it > 1) return@let it
                    val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        funcName
                    )
                    val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        methodNameStr
                    )
                    val weightKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.weight1KeyToIndex.first
                    )
                    val weightValueKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it.toString()
                    )
                    return null to
                            FuncCheckerForSetting.FuncCheckErr(
                                "${weightKey}(${weightValueKey}) must be > 1: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                            )
                }
                val weight2 = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.weight2KeyToIndex,
                    where
                ).let { weightToErr ->
                    val funcErr = weightToErr.second
                        ?: return@let weightToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    if(it > 1) return@let it
                    val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        funcName
                    )
                    val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        methodNameStr
                    )
                    val weightKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.weight2KeyToIndex.first
                    )
                    val weightValueKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it.toString()
                    )
                    return null to
                            FuncCheckerForSetting.FuncCheckErr(
                                "${weightKey}(${weightValueKey}) must be > 1: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                            )
                }
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lWeithColor.txt").absolutePath,
//                    listOf(
//                        "colorStr1: ${colorStr1}",
//                        "colorStr2: ${colorStr2}",
//                        "weight1: ${weight1}",
//                        "weight2: ${weight2}",
//                        "colorResult: ${ColorTool.weightAvHexColors(
//                            colorStr1,
//                            colorStr2,
//                            weight1,
//                            weight2,
//                        )}",
//                    ).joinToString("\n")
//                )
                Pair(
                    ColorTool.weightAvHexColors(
                        colorStr1,
                        colorStr2,
                        weight1,
                        weight2,
                    ),
                    null
                ) to null
            }
            is ColorMethodArgClass.VividArgs -> {
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
                val colorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.colorStrKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.colorStrKeyToIndex.first,
                        where,
                    )
                }
                val rate = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.rateKeyToIndex,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let { rate ->
                    if (rate >= 0 && rate <= 1) return@let rate
                    val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        funcName
                    )
                    val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        methodNameStr
                    )
                    val spanRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        args.rateKeyToIndex.first,
                        rate.toString()
                    )
                    val spanRateValue = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        rate.toString()
                    )
                    return null to FuncCheckerForSetting.FuncCheckErr(
                        "${spanRateKey}(${spanRateValue}) must be 0~1: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                    )
                }
                Pair(
                    ColorTool.makeColorVibrant(
                        colorStr,
                        rate,
                    ),
                    null
                ) to null
            }
            is ColorMethodArgClass.ClosestColorArgs -> {
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
                val colorKey = args.colorStrListKeyToIndex.first
                val colorStrList = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.colorStrListKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStrListStr ->
                    val colorListSrc = colorStrListStr.split(",").filter {
                        it.trim().isNotEmpty()
                    }
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lcolorClosest00.txt").absolutePath,
//                        listOf(
//                            "colorStrList: AA${colorListSrc.joinToString("--")}",
//                        ).joinToString("\n")
//                    )
                    colorListSrc.forEach {
                        colorStr ->
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lcolorClosestLoop.txt").absolutePath,
//                            listOf(
//                                "colorStr: AA${colorStr}--",
//                            ).joinToString("\n")
//                        )
                        ColorTool.parseColorStr(
                            context,
                            colorStr,
                            colorKey,
                            where,
                        )
                    }
                    colorListSrc
                }
                val targetColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.targetColorStrKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.targetColorStrKeyToIndex.first,
                        where,
                    )
                }
//               FileSystems.updateFile(
//                   File(UsePath.cmdclickDefaultAppDirPath, "lcolorClosest.txt").absolutePath,
//                   listOf(
//                       "colorStrList: ${colorStrList}",
//                       "closestColo: ${ColorTool.ClosestColor.findClosestColor(
//                           colorStrList,
//                           targetColorStr,
//                       )}",
//                       "targetColorStr: ${targetColorStr}"
//                   ).joinToString("\n")
//               )
                Pair(
                    ColorTool.ClosestColor.findClosestColor(
                        colorStrList,
                        targetColorStr,
                    )?.trim(),
                    null
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ColorMethodArgClass
    ) {
        RND("rnd", ColorMethodArgClass.RndArgs),
        OPACITY("opacity", ColorMethodArgClass.OpacityArgs),
        AV_TWO("avTwo", ColorMethodArgClass.AvTwoArgs),
        WEIGHT_AV_TWO("weightAvTwo", ColorMethodArgClass.WeightAvTwoArgs),
        VIVID("vivid", ColorMethodArgClass.VividArgs),
        CLOSEST("closest", ColorMethodArgClass.ClosestColorArgs)
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ColorMethodArgClass {
        data object RndArgs : ColorMethodArgClass(), ArgType {
            override val entries = RndEnumArgs.entries
            val rndMacroKeyToIndex = Pair(
                RndEnumArgs.RND_MACRO.key,
                RndEnumArgs.RND_MACRO.index
            )

            enum class RndEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                RND_MACRO("rndMacro", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object OpacityArgs : ColorMethodArgClass(), ArgType {
            override val entries = OpacityEnumArgs.entries
            val colorStrKeyToIndex = Pair(
                OpacityEnumArgs.COLOR_STR.key,
                OpacityEnumArgs.COLOR_STR.index,
            )
            val opacityStrKeyToIndex = Pair(
                OpacityEnumArgs.OPACITY.key,
                OpacityEnumArgs.OPACITY.index,
            )
            enum class OpacityEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                COLOR_STR("colorStr", 0, FuncCheckerForSetting.ArgType.STRING),
                OPACITY("opacity", 1, FuncCheckerForSetting.ArgType.FLOAT)
            }
        }
        data object AvTwoArgs : ColorMethodArgClass(), ArgType {
            override val entries = OpacityEnumArgs.entries
            val color1StrKeyToIndex = Pair(
                OpacityEnumArgs.COLOR_STR1.key,
                OpacityEnumArgs.COLOR_STR1.index,
            )
            val color2StrKeyToIndex = Pair(
                OpacityEnumArgs.COLOR_STR2.key,
                OpacityEnumArgs.COLOR_STR2.index,
            )
            enum class OpacityEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                COLOR_STR1("colorStr1", 0, FuncCheckerForSetting.ArgType.STRING),
                COLOR_STR2("colorStr2", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object WeightAvTwoArgs : ColorMethodArgClass(), ArgType {
            override val entries = OpacityEnumArgs.entries
            val color1StrKeyToIndex = Pair(
                OpacityEnumArgs.COLOR_STR1.key,
                OpacityEnumArgs.COLOR_STR1.index,
            )
            val color2StrKeyToIndex = Pair(
                OpacityEnumArgs.COLOR_STR2.key,
                OpacityEnumArgs.COLOR_STR2.index,
            )
            val weight1KeyToIndex = Pair(
                OpacityEnumArgs.WEIGHT1.key,
                OpacityEnumArgs.WEIGHT1.index,
            )
            val weight2KeyToIndex = Pair(
                OpacityEnumArgs.WEIGHT2.key,
                OpacityEnumArgs.WEIGHT2.index,
            )
            enum class OpacityEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                COLOR_STR1("colorStr1", 0, FuncCheckerForSetting.ArgType.STRING),
                COLOR_STR2("colorStr2", 1, FuncCheckerForSetting.ArgType.STRING),
                WEIGHT1("weight1", 2, FuncCheckerForSetting.ArgType.INT),
                WEIGHT2("weight2", 3, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object VividArgs : ColorMethodArgClass(), ArgType {
            override val entries = VividEnumArgs.entries
            val colorStrKeyToIndex = Pair(
                VividEnumArgs.COLOR_STR.key,
                VividEnumArgs.COLOR_STR.index,
            )
            val rateKeyToIndex = Pair(
                VividEnumArgs.RATE.key,
                VividEnumArgs.RATE.index,
            )


            enum class VividEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                COLOR_STR("colorStr", 0, FuncCheckerForSetting.ArgType.STRING),
                RATE("rate", 1, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }

        data object ClosestColorArgs : ColorMethodArgClass(), ArgType {
            override val entries = ClosestColorEnumArgs.entries

            val colorStrListKeyToIndex = Pair(
                ClosestColorEnumArgs.COLOR_STR_LIST.key,
                ClosestColorEnumArgs.COLOR_STR_LIST.index,
            )
            val targetColorStrKeyToIndex = Pair(
                ClosestColorEnumArgs.TARGET_COLOR_STR.key,
                ClosestColorEnumArgs.TARGET_COLOR_STR.index,
            )
            enum class ClosestColorEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                COLOR_STR_LIST("colorStrList", 0, FuncCheckerForSetting.ArgType.STRING),
                TARGET_COLOR_STR("targetColorStr", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }

}
