package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ColorTool
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

                val width = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                val height = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                val minDurationRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
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
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    0f
                }
                val maxDurationRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
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
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                if(minDurationRate > maxDurationRate) {
                    val spanMinDurationRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.minDurationKeyToDefaultValueStr.first,
                    )
                    val spanMaxDurationRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.maxDurationKeyToDefaultValueStr.first,
                    )
                    val spanMinDurationRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minDurationRate.toString()
                    )
                    val spanMaxDurationRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxDurationRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be ${spanMinDurationRateKey}(${spanMinDurationRate}) <= ${spanMaxDurationRateKey}(${spanMaxDurationRate}): ${spanWhere}"
                    )
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
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
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
                }.let {
                    if(it <= 0) return@let 1f
                    it
                }
                if(minStrokeWidth > maxStrokeWidth) {
                    val spanMinWidthRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.minStrokeWidthKeyToDefaultValueStr.first
                    )
                    val spanMaxWidthRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.maxStrokeWidthKeyToDefaultValueStr.first
                    )
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minStrokeWidth.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxStrokeWidth.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be ${spanMinWidthRateKey}(${spanMinWidthRate}) <= ${spanMaxWidthRateKey}(${spanMaxWidthRate}): ${spanWhere}"
                    )
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
                }.let {
                    if(it <= 1f) return@let it
                    1f
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
                }.let {
                    if(it <= 1f) return@let it
                    1f
                }
                if(minOpacityRate > maxOpacityRate) {
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minOpacityRate.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxOpacityRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minOpacityRate(${spanMinWidthRate}) <= maxOpacityRate(${spanMaxWidthRate}): ${spanWhere}"
                    )
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
                }.let {
                    if(it >= 0) return@let it
                    0
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
                }.let {
                    if(it >= 0) return@let it
                    0
                }
                if(minSeg > maxSeg) {
                    val spanMinSegRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minOpacityRate.toString()
                    )
                    val spanMaxSegRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxOpacityRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minSeg(${spanMinSegRate}) <= maxSeg(${spanMaxSegRate}): ${spanWhere}"
                    )
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

                val width = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                val height = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                val minRadius = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                }.let {
                    if(it > 0) return@let it
                    val spanMinRadiusKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.minRadiusKeyToDefaultValueStr.first
                    )
                    val spanMinRadius = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "${spanMinRadiusKey}(${spanMinRadius}) must be > 0 : ${spanWhere}"
                    )
                }
                val maxRadius = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                }.let {
                    if(it > 0) return@let it
                    val spanMaxRadiusKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.maxRadiusKeyToDefaultValueStr.first
                    )
                    val spanMaxRadius = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "${spanMaxRadiusKey}(${spanMaxRadius}) must be > 0 : ${spanWhere}"
                    )
                }
                if(minRadius > maxRadius) {
                    val spanMinRadiusKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.minRadiusKeyToDefaultValueStr.first
                    )
                    val spanMaxRadiusKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.maxRadiusKeyToDefaultValueStr.first
                    )
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minRadius.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxRadius.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be ${spanMinRadiusKey}(${spanMinWidthRate}) <= ${spanMaxRadiusKey}(${spanMaxWidthRate}): ${spanWhere}"
                    )
                }
                val radAngle = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                }.let {
                    if(it > 0) return@let it
                    val spanRadAngleKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.radAngleKeyToDefaultValueStr.first
                    )
                    val spanRadAngle = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "${spanRadAngleKey}(${spanRadAngle}) must be > 0 : ${spanWhere}"
                    )
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
                }.let {
                    if(it <= 0) return@let 1f
                    it
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
                }.let {
                    if(it <= 0) return@let 1f
                    it
                }
                if(minStrokeWidth > maxStrokeWidth) {
                    val spanMinWidthRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.minStrokeWidthKeyToDefaultValueStr.first
                    )
                    val spanMaxWidthRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.maxStrokeWidthKeyToDefaultValueStr.first
                    )
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minStrokeWidth.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxStrokeWidth.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be ${spanMinWidthRateKey}(${spanMinWidthRate}) <= ${spanMaxWidthRateKey}(${spanMaxWidthRate}): ${spanWhere}"
                    )
                }
                val centerX = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
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
                }.let {
                    if(it > 0) return@let it
                    val spanCenterXKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.centerXKeyToDefaultValueStr.first
                    )
                    val spanCenterX = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "${spanCenterXKey}(${spanCenterX}) must be > 0 : ${spanWhere}"
                    )
                }
                val centerY = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
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
                }.let {
                    if(it > 0) return@let it
                    val spanCenterXKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.centerYKeyToDefaultValueStr.first
                    )
                    val spanCenterY = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "${spanCenterXKey}(${spanCenterY}) must be > 0 : ${spanWhere}"
                    )
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
                }.let {
                    if(it <= 1f) return@let it
                    1f
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
                }.let {
                    if(it <= 1f) return@let it
                    1f
                }
                if(minOpacityRate > maxOpacityRate) {
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minOpacityRate.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxOpacityRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minOpacityRate(${spanMinWidthRate}) <= maxOpacityRate(${spanMaxWidthRate}): ${spanWhere}"
                    )
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
                }.let {
                    if(it >= 0) return@let it
                    0
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
                }.let {
                    if(it >= 0) return@let it
                    0
                }
                if(minSeg > maxSeg) {
                    val spanMinSegRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minOpacityRate.toString()
                    )
                    val spanMaxSegRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxOpacityRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minSeg(${spanMinSegRate}) <= maxSeg(${spanMaxSegRate}): ${spanWhere}"
                    )
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
    }
    private enum class MethodNameClass(
        val str: String,
        val args: CrackArtMethodArgClass
    ){
        RND("rnd", CrackArtMethodArgClass.RndArgs),
        RAD("rad", CrackArtMethodArgClass.RadArgs),
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
