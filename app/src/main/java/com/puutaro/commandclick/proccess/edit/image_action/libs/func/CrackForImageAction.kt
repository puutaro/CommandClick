package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.image_tools.CurveArt
import com.puutaro.commandclick.util.image_tools.LineArt
import kotlin.enums.EnumEntries

object CrackForImageAction {

    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
       if(
           context == null
       ) return null
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
        val args =
            methodNameClass.args
        return when(args){
            is CrackArtMethodArgClass.RndArgs -> {
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

                val width = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.widthKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val height = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.heightKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minDurationRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minDurationKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxDurationRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxDurationKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minDurationRate,
                    maxDurationRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minDurationKeyToDefaultValueStr.first,
                    args.maxDurationKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val minStrokeWidth = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.minStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { minStrokeWidthToErr ->
                    val funcErr = minStrokeWidthToErr.second
                        ?: return@let minStrokeWidthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxStrokeWidth = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { maxStrokeWidthToErr ->
                    val funcErr = maxStrokeWidthToErr.second
                        ?: return@let maxStrokeWidthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minStrokeWidth,
                    maxStrokeWidth,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minStrokeWidthKeyToDefaultValueStr.first,
                    args.maxStrokeWidthKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val times = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { it.trim().isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val minOpacityRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minOpacityRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minOpacityRate,
                    maxOpacityRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minOpacityRateKeyToDefaultValueStr.first,
                    args.maxOpacityRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val minSeg = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.minSegKeyToDefaultValueStr,
                    where
                ).let { minSegToErr ->
                    val funcErr = minSegToErr.second
                        ?: return@let minSegToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxSeg = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.maxSegKeyToDefaultValueStr,
                    where
                ).let { maxSegToErr ->
                    val funcErr = maxSegToErr.second
                        ?: return@let maxSegToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minSeg,
                    maxSeg,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minSegKeyToDefaultValueStr.first,
                    args.maxSegKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val bitmapArt = InnerLineArt.rnd(
                    width,
                    height,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minSeg,
                    maxSeg,
                    minDurationRate,
                    maxDurationRate,
                    minOpacityRate,
                    maxOpacityRate,
                    colorList,
                    times,
                    where,
                ).let {
                        (bitmapArtSrc, err) ->
                    if(
                        err == null
                    ) return@let bitmapArtSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    bitmapArt,
                    null
                ) to null
            }
            is CrackArtMethodArgClass.RadArgs -> {
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

                val width = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.widthKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val height = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.heightKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minRadius = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.minRadiusKeyToDefaultValueStr,
                    where
                ).let { minRadiusToErr ->
                    val funcErr = minRadiusToErr.second
                        ?: return@let minRadiusToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxRadius = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.maxRadiusKeyToDefaultValueStr,
                    where
                ).let { manRadiusToErr ->
                    val funcErr = manRadiusToErr.second
                        ?: return@let manRadiusToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minRadius,
                    maxRadius,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minRadiusKeyToDefaultValueStr.first,
                    args.maxRadiusKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val radAngle = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.radAngleKeyToDefaultValueStr,
                    where
                ).let { radAngleToErr ->
                    val funcErr = radAngleToErr.second
                        ?: return@let radAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minStrokeWidth = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.minStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { minStrokeWidthToErr ->
                    val funcErr = minStrokeWidthToErr.second
                        ?: return@let minStrokeWidthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxStrokeWidth = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { maxStrokeWidthToErr ->
                    val funcErr = maxStrokeWidthToErr.second
                        ?: return@let maxStrokeWidthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minStrokeWidth,
                    maxStrokeWidth,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minStrokeWidthKeyToDefaultValueStr.first,
                    args.maxStrokeWidthKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val centerX = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.centerXKeyToDefaultValueStr,
                    where
                ).let { centerXToErr ->
                    val funcErr = centerXToErr.second
                        ?: return@let centerXToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val centerY = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.centerYKeyToDefaultValueStr,
                    where
                ).let { centerYToErr ->
                    val funcErr = centerYToErr.second
                        ?: return@let centerYToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val times = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { it.trim().isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val minOpacityRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minOpacityRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minOpacityRate,
                    maxOpacityRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minOpacityRateKeyToDefaultValueStr.first,
                    args.maxOpacityRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val minSeg = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.minSegKeyToDefaultValueStr,
                    where
                ).let { minSegToErr ->
                    val funcErr = minSegToErr.second
                        ?: return@let minSegToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxSeg = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.maxSegKeyToDefaultValueStr,
                    where
                ).let { maxSegToErr ->
                    val funcErr = maxSegToErr.second
                        ?: return@let maxSegToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minSeg,
                    maxSeg,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minSegKeyToDefaultValueStr.first,
                    args.maxSegKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val bitmapArt = InnerLineArt.rad(
                    width,
                    height,
                    centerX,
                    centerY,
                    minRadius,
                    maxRadius,
                    minSeg,
                    maxSeg,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minOpacityRate,
                    maxOpacityRate,
                    radAngle,
                    colorList,
                    times,
                    where,
                ).let {
                        (bitmapArtSrc, err) ->
                    if(
                        err == null
                    ) return@let bitmapArtSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    bitmapArt,
                    null
                ) to null
            }
            is CrackArtMethodArgClass.RndCurveArgs -> {
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
                val width = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.widthKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val height = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    args.heightKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minDurationRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minDurationKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxDurationRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxDurationKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minDurationRate,
                    maxDurationRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minDurationKeyToDefaultValueStr.first,
                    args.maxDurationKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val minStrokeWidth = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { minStrokeWidthToErr ->
                    val funcErr = minStrokeWidthToErr.second
                        ?: return@let minStrokeWidthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0f,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    minStrokeWidth,
                    args.minStrokeWidthKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val maxStrokeWidth = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { maxStrokeWidthToErr ->
                    val funcErr = maxStrokeWidthToErr.second
                        ?: return@let maxStrokeWidthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0f,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    maxStrokeWidth,
                    args.maxStrokeWidthKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minStrokeWidth,
                    maxStrokeWidth,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minStrokeWidthKeyToDefaultValueStr.first,
                    args.maxOpacityRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val minCurveFactor = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minCurveFactorKeyToDefaultValueStr,
                    where
                ).let { minCurveFactorToErr ->
                    val funcErr = minCurveFactorToErr.second
                        ?: return@let minCurveFactorToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0f,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    minCurveFactor,
                    args.minCurveFactorKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val maxCurveFactor = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxCurveFactorKeyToDefaultValueStr,
                    where
                ).let { maxCurveFactorToErr ->
                    val funcErr = maxCurveFactorToErr.second
                        ?: return@let maxCurveFactorToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0f,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    maxCurveFactor,
                    args.maxCurveFactorKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minCurveFactor,
                    maxCurveFactor,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minCurveFactorKeyToDefaultValueStr.first,
                    args.maxCurveFactorKeyToDefaultValueStr.first,
                    where,
                ).let { err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val rndNum = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.rndNumKeyToDefaultValueStr,
                    where
                ).let { randomNumToErr ->
                    val funcErr = randomNumToErr.second
                        ?: return@let randomNumToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0f,
                    FuncCheckerForSetting.NumChecker.CompareSignal.EQUAL_LARGER,
                    rndNum,
                    args.rndNumKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    times,
                    args.timesKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { it.trim().isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val minOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minOpacityRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.range(
                    0f,
                    1f,
                    FuncCheckerForSetting.NumChecker.RangeCompareSignal.EQUAL,
                    minOpacityRate,
                    args.minOpacityRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.range(
                    0f,
                    1f,
                    FuncCheckerForSetting.NumChecker.RangeCompareSignal.EQUAL,
                    maxOpacityRate,
                    args.maxOpacityRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minOpacityRate,
                    maxOpacityRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minOpacityRateKeyToDefaultValueStr.first,
                    args.maxOpacityRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val minSeg = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.minSegKeyToDefaultValueStr,
                    where
                ).let { minSegToErr ->
                    val funcErr = minSegToErr.second
                        ?: return@let minSegToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    minSeg,
                    args.minSegKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val maxSeg = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.maxSegKeyToDefaultValueStr,
                    where
                ).let { maxSegToErr ->
                    val funcErr = maxSegToErr.second
                        ?: return@let maxSegToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    maxSeg,
                    args.maxSegKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minSeg,
                    maxSeg,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minSegKeyToDefaultValueStr.first,
                    args.maxSegKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val bitmapArt = InnerLineArt.rndCurve(
                    width,
                    height,
                    rndNum,
                    minCurveFactor,
                    maxCurveFactor,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minSeg,
                    maxSeg,
                    minDurationRate,
                    maxDurationRate,
                    minOpacityRate,
                    maxOpacityRate,
                    colorList,
                    times,
                    where,
                ).let {
                        (bitmapArtSrc, err) ->
                    if(
                        err == null
                    ) return@let bitmapArtSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    bitmapArt,
                    null
                ) to null
            }
        }
    }


    private object InnerLineArt {
        suspend fun rnd(
            width: Int,
            height: Int,
            minStrokeWidth: Float,
            maxStrokeWidth: Float,
            minSeg: Int,
            maxSeg: Int,
            minDurationRate:Float,
            maxDurationRate: Float,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            colorList: List<String>,
            times: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                LineArt.drawCrackOnBitmap(
                    width,
                    height,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minSeg,
                    maxSeg,
                    minDurationRate,
                    maxDurationRate,
                    minOpacityRate,
                    maxOpacityRate,
                    colorList,
                    times
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun rad(
            width: Int,
            height: Int,
            centerX: Float,
            centerY: Float,
            minRadius: Int,
            maxRadius: Int,
            minSeg: Int,
            maxSeg: Int,
            minStrokeWidth: Float,
            maxStrokeWidth: Float,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            radAngle: Int,
            colorList: List<String>,
            times: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                LineArt.drawCrackFromCenter(
                    width,
                    height,
                    centerX,
                    centerY,
                    minRadius,
                    maxRadius,
                    minSeg,
                    maxSeg,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minOpacityRate,
                    maxOpacityRate,
                    radAngle,
                    colorList,
                    times
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        fun rndCurve(
            width: Int,
            height: Int,
            rndNum: Float,
            minCurveFactor: Float,
            maxCurveFactor: Float,
            minStrokeWidth: Float,
            maxStrokeWidth: Float,
            minSeg: Int,
            maxSeg: Int,
            minDurationRate:Float,
            maxDurationRate: Float,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            colorList: List<String>,
            times: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                CurveArt.drawCurvedCrack(
                    width,
                    height,
                    rndNum,
                    minCurveFactor,
                    maxCurveFactor,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minSeg,
                    maxSeg,
                    minDurationRate,
                    maxDurationRate,
                    minOpacityRate,
                    maxOpacityRate,
                    colorList,
                    times
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
    }
    private enum class MethodNameClass(
        val str: String,
        val args: CrackArtMethodArgClass
    ){
        RND("rnd", CrackArtMethodArgClass.RndArgs),
        RAD("rad", CrackArtMethodArgClass.RadArgs),
        RND_CURVE("rndCurve", CrackArtMethodArgClass.RndCurveArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class CrackArtMethodArgClass {

        data object RndArgs : CrackArtMethodArgClass(), ArgType {
            override val entries = RndEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                RndEnumArgs.WIDTH.key,
                RndEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                RndEnumArgs.HEIGHT.key,
                RndEnumArgs.HEIGHT.defaultValueStr
            )
            val minSegKeyToDefaultValueStr = Pair(
                RndEnumArgs.MIN_SEG.key,
                RndEnumArgs.MIN_SEG.defaultValueStr
            )
            val maxSegKeyToDefaultValueStr = Pair(
                RndEnumArgs.MAX_SEG.key,
                RndEnumArgs.MAX_SEG.defaultValueStr
            )
            val minDurationKeyToDefaultValueStr = Pair(
                RndEnumArgs.MIN_DURATION_RATE.key,
                RndEnumArgs.MIN_DURATION_RATE.defaultValueStr
            )
            val maxDurationKeyToDefaultValueStr = Pair(
                RndEnumArgs.MAX_DURATION_RATE.key,
                RndEnumArgs.MAX_DURATION_RATE.defaultValueStr
            )
            val minStrokeWidthKeyToDefaultValueStr = Pair(
                RndEnumArgs.MIN_STROKE_WIDTH.key,
                RndEnumArgs.MIN_STROKE_WIDTH.defaultValueStr
            )
            val maxStrokeWidthKeyToDefaultValueStr = Pair(
                RndEnumArgs.MAX_STROKE_WIDTH.key,
                RndEnumArgs.MAX_STROKE_WIDTH.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                RndEnumArgs.TIMES.key,
                RndEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                RndEnumArgs.MIN_OPACITY_RATE.key,
                RndEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                RndEnumArgs.MAX_OPACITY_RATE.key,
                RndEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                RndEnumArgs.COLOR_LIST.key,
                RndEnumArgs.COLOR_LIST.defaultValueStr
            )
            enum class RndEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                MIN_SEG("minSeg", 1.toString(), FuncCheckerForSetting.ArgType.INT),
                MAX_SEG("maxSeg", 10.toString(), FuncCheckerForSetting.ArgType.INT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                TIMES("times", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MIN_DURATION_RATE("minDurationRate", 0.5.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_DURATION_RATE("maxDurationRate", 0.9.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_STROKE_WIDTH("minStrokeWidth", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_STROKE_WIDTH("maxStrokeWidth", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),

            }
        }
        data object RndCurveArgs : CrackArtMethodArgClass(), ArgType {
            override val entries = RndCurveEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.WIDTH.key,
                RndCurveEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.HEIGHT.key,
                RndCurveEnumArgs.HEIGHT.defaultValueStr
            )
            val minSegKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MIN_SEG.key,
                RndCurveEnumArgs.MIN_SEG.defaultValueStr
            )
            val maxSegKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MAX_SEG.key,
                RndCurveEnumArgs.MAX_SEG.defaultValueStr
            )
            val minDurationKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MIN_DURATION_RATE.key,
                RndCurveEnumArgs.MIN_DURATION_RATE.defaultValueStr
            )
            val maxDurationKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MAX_DURATION_RATE.key,
                RndCurveEnumArgs.MAX_DURATION_RATE.defaultValueStr
            )
            val minStrokeWidthKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MIN_STROKE_WIDTH.key,
                RndCurveEnumArgs.MIN_STROKE_WIDTH.defaultValueStr
            )
            val maxStrokeWidthKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MAX_STROKE_WIDTH.key,
                RndCurveEnumArgs.MAX_STROKE_WIDTH.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.TIMES.key,
                RndCurveEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MIN_OPACITY_RATE.key,
                RndCurveEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MAX_OPACITY_RATE.key,
                RndCurveEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val minCurveFactorKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MIN_CURVE_FACTOR.key,
                RndCurveEnumArgs.MIN_CURVE_FACTOR.defaultValueStr
            )
            val maxCurveFactorKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.MAX_CURVE_FACTOR.key,
                RndCurveEnumArgs.MAX_CURVE_FACTOR.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.COLOR_LIST.key,
                RndCurveEnumArgs.COLOR_LIST.defaultValueStr
            )
            val rndNumKeyToDefaultValueStr = Pair(
                RndCurveEnumArgs.RND_NUM.key,
                RndCurveEnumArgs.RND_NUM.defaultValueStr
            )
            enum class RndCurveEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                MIN_SEG("minSeg", 1.toString(), FuncCheckerForSetting.ArgType.INT),
                MAX_SEG("maxSeg", 10.toString(), FuncCheckerForSetting.ArgType.INT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                TIMES("times", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MIN_DURATION_RATE("minDurationRate", 0.5.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_DURATION_RATE("maxDurationRate", 0.9.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_STROKE_WIDTH("minStrokeWidth", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_STROKE_WIDTH("maxStrokeWidth", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_CURVE_FACTOR("minCurveFactor", 0.2.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_CURVE_FACTOR("maxCurveFactor", 0.5.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                RND_NUM("randNum", 60.toString(), FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object RadArgs : CrackArtMethodArgClass(), ArgType {
            override val entries = RadEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                RadEnumArgs.WIDTH.key,
                RadEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                RadEnumArgs.HEIGHT.key,
                RadEnumArgs.HEIGHT.defaultValueStr
            )
            val minSegKeyToDefaultValueStr = Pair(
                RadEnumArgs.MIN_SEG.key,
                RadEnumArgs.MIN_SEG.defaultValueStr
            )
            val maxSegKeyToDefaultValueStr = Pair(
                RadEnumArgs.MAX_SEG.key,
                RadEnumArgs.MAX_SEG.defaultValueStr
            )
            val minStrokeWidthKeyToDefaultValueStr = Pair(
                RadEnumArgs.MIN_STROKE_WIDTH.key,
                RadEnumArgs.MIN_STROKE_WIDTH.defaultValueStr
            )
            val maxStrokeWidthKeyToDefaultValueStr = Pair(
                RadEnumArgs.MAX_STROKE_WIDTH.key,
                RadEnumArgs.MAX_STROKE_WIDTH.defaultValueStr
            )
            val centerXKeyToDefaultValueStr = Pair(
                RadEnumArgs.CENTER_X.key,
                RadEnumArgs.CENTER_X.defaultValueStr
            )
            val centerYKeyToDefaultValueStr = Pair(
                RadEnumArgs.CENTER_Y.key,
                RadEnumArgs.CENTER_Y.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                RadEnumArgs.TIMES.key,
                RadEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                RadEnumArgs.MIN_OPACITY_RATE.key,
                RadEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                RadEnumArgs.MAX_OPACITY_RATE.key,
                RadEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                RadEnumArgs.COLOR_LIST.key,
                RadEnumArgs.COLOR_LIST.defaultValueStr
            )
            val minRadiusKeyToDefaultValueStr = Pair(
                RadEnumArgs.MIN_RADIUS.key,
                RadEnumArgs.MIN_RADIUS.defaultValueStr
            )
            val maxRadiusKeyToDefaultValueStr = Pair(
                RadEnumArgs.MAX_RADIUS.key,
                RadEnumArgs.MAX_RADIUS.defaultValueStr
            )
            val radAngleKeyToDefaultValueStr = Pair(
                RadEnumArgs.RAD_ANGLE.key,
                RadEnumArgs.RAD_ANGLE.defaultValueStr
            )
            enum class RadEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                TIMES("times", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_SEG("minSeg", 1.toString(), FuncCheckerForSetting.ArgType.INT),
                MAX_SEG("maxSeg", 10.toString(), FuncCheckerForSetting.ArgType.INT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                CENTER_X("centerX", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                CENTER_Y("centerY", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MIN_RADIUS("minRadius", 100.toString(), FuncCheckerForSetting.ArgType.INT),
                MAX_RADIUS("maxRadius", 100.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_STROKE_WIDTH("minStrokeWidth", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_STROKE_WIDTH("maxStrokeWidth", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                RAD_ANGLE("radAngle", 90.toString(), FuncCheckerForSetting.ArgType.INT),

            }
        }
    }
}
