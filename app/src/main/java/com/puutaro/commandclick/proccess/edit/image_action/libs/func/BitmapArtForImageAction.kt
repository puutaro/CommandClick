package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.fragment_lib.command_index_fragment.UrlImageDownloader
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ImageFile
import com.puutaro.commandclick.util.image_tools.BitmapArt
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.CcDotArt
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File
import kotlin.enums.EnumEntries
import androidx.core.graphics.scale
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.util.image_tools.SpecialArt
import com.puutaro.commandclick.util.str.AltRegexTool

object BitmapArtForImageAction {
    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>?,
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
            is BitmapArtMethodArgClass.MatrixStormArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val width = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.widthKeyToDefaultValueStr,
                    where
                ).let { widthStrToErr ->
                    val funcErr = widthStrToErr.second
                        ?: return@let widthStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val height = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.heightKeyToDefaultValueStr,
                    where
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val xMulti = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.xMultiKeyToDefaultValueStr,
                    where
                ).let { xMultiToErr ->
                    val funcErr = xMultiToErr.second
                        ?: return@let xMultiToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val yMulti = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.yMultiKeyToDefaultValueStr,
                    where
                ).let { yMultiToErr ->
                    val funcErr = yMultiToErr.second
                        ?: return@let yMultiToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.pieceKeyToDefaultValueStr,
                    where
                ).let { pieceToErr ->
                    val funcErr = pieceToErr.second
                        ?: return@let pieceToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val pieceMap =
                    BitmapPieceManager.makePieceMap(argsPairList)
                val xDup = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.xDupKeyToDefaultValueStr,
                    where
                ).let { xDupToErr ->
                    val funcErr = xDupToErr.second
                        ?: return@let xDupToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val yDup = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.yDupKeyToDefaultValueStr,
                    where
                ).let { yDupToErr ->
                    val funcErr = yDupToErr.second
                        ?: return@let yDupToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = InnerBitmapArt.matrixStorm(
                    pieceMap,
                    bitmap,
                    width,
                    height,
                    xMulti,
                    yMulti,
                    xDup,
                    yDup,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.BitmapPuzzleArgs -> {
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
                val bkBitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bkBitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
               FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val bitmapKeyName = args.bitmapKeyToDefaultValueStr.first
                val bitmapToErrList = argsPairList.filter {
                    (argName, _) ->
                    argName == bitmapKeyName
                }.map {
                    (argName, valueStr) ->
                    FuncCheckerForSetting.Getter.getBitmapArg(
                        argName,
                        valueStr,
                        null,
                        null,
                        varNameToBitmapMap,
                        where
                    )
                }
                                bitmapToErrList.forEach { (_, err) ->
                    if (err != null) {
                        return Pair(
                            null,
                            ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to err
                    }
                }
                val bitmapList = bitmapToErrList.filter{
                    it.first != null
                }.map {
                    it.first as Bitmap
                }
//                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.pieceKeyToDefaultValueStr,
//                    where
//                ).let { pieceToErr ->
//                    val funcErr = pieceToErr.second
//                        ?: return@let pieceToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
//                    ) to funcErr
//                }
//                val pieceMapList =
//                    argsPairList.filter {
//                            (argName, _) ->
//                        argName == args.pieceKeyToDefaultValueStr.first
//                    }.map {
//                            argNameToValueStr ->
//                        BitmapPieceManager.makePieceMap(
//                            listOf(argNameToValueStr)
//                        )
//                    }
                val rate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.rateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val minOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minOpacityRateKeyToDefaultValueStr,
                    where
                ).let { minOpacityRateToErr ->
                    val funcErr = minOpacityRateToErr.second
                        ?: return@let minOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
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
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val sizeCenterX = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.sizeCenterXKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.EQUAL_LARGER,
                    sizeCenterX,
                    args.sizeCenterXKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val sizeCenterY = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.sizeCenterYKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.EQUAL_LARGER,
                    sizeCenterY,
                    args.sizeCenterYKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val sizeIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeInclineKeyToDefaultValueStr,
                    where
                ).let { sizeInclineToErr ->
                    val funcErr = sizeInclineToErr.second
                        ?: return@let sizeInclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val sizeOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeOffsetKeyToDefaultValueStr,
                    where
                ).let { sizeOffsetToErr ->
                    val funcErr = sizeOffsetToErr.second
                        ?: return@let sizeOffsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
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
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val passionColorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.passionColorListKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val passionInt = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.passionRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let (it * 10).toInt()
                    0
                }
                val isOverlay = FuncCheckerForSetting.Getter.getBoolFromArgMapByName(
                    mapArgMapList,
                    args.isOverlayKeyToDefaultValueStr,
                    where
                ).let { isOverlayToErr ->
                    val funcErr = isOverlayToErr.second
                        ?: return@let isOverlayToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val returnBitmap = InnerBitmapArt.bitmapPuzzle(
                    bkBitmap,
                    bitmapList,
                    rate,
                    times,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    sizeCenterX,
                    sizeCenterY,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    passionColorList,
                    passionInt,
                    isOverlay,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.RectPuzzleAjustSizeArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val rate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.rateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val minOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minOpacityRateKeyToDefaultValueStr,
                    where
                ).let { minOpacityRateToErr ->
                    val funcErr = minOpacityRateToErr.second
                        ?: return@let minOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
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
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val sizeCenterX = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.sizeCenterXKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.EQUAL_LARGER,
                    sizeCenterX,
                    args.sizeCenterXKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val sizeCenterY = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.sizeCenterYKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.EQUAL_LARGER,
                    sizeCenterY,
                    args.sizeCenterYKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val sizeIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeInclineKeyToDefaultValueStr,
                    where
                ).let { sizeInclineToErr ->
                    val funcErr = sizeInclineToErr.second
                        ?: return@let sizeInclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val sizeOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeOffsetKeyToDefaultValueStr,
                    where
                ).let { sizeOffsetToErr ->
                    val funcErr = sizeOffsetToErr.second
                        ?: return@let sizeOffsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { colorListConToErr ->
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lrectPuzzle.txt").absolutePath,
//                        listOf(
//                            "colorListConToErr: ${colorListConToErr.first}"
//                        ).joinToString("\n")
//                    )
                    val funcErr = colorListConToErr.second
                        ?: return@let colorListConToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val passionColorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.passionColorListKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val passionInt = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.passionRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let (it * 10).toInt()
                    0
                }
                val isOverlay = FuncCheckerForSetting.Getter.getBoolFromArgMapByName(
                    mapArgMapList,
                    args.isOverlayKeyToDefaultValueStr,
                    where
                ).let { isOverlayToErr ->
                    val funcErr = isOverlayToErr.second
                        ?: return@let isOverlayToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val returnBitmap = InnerBitmapArt.rectPuzzleAjustSize(
                    bitmap,
                    rate,
                    times,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    sizeCenterX,
                    sizeCenterY,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    passionColorList,
                    passionInt,
                    isOverlay,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.RectPuzzleArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val rate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.rateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val minOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minOpacityRateKeyToDefaultValueStr,
                    where
                ).let { minOpacityRateToErr ->
                    val funcErr = minOpacityRateToErr.second
                        ?: return@let minOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
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
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { colorListConToErr ->
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lrectPuzzle.txt").absolutePath,
//                        listOf(
//                            "colorListConToErr: ${colorListConToErr.first}"
//                        ).joinToString("\n")
//                    )
                    val funcErr = colorListConToErr.second
                        ?: return@let colorListConToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val passionColorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.passionColorListKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val passionInt = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.passionRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let (it * 10).toInt()
                    0
                }
                val isOverlay = FuncCheckerForSetting.Getter.getBoolFromArgMapByName(
                    mapArgMapList,
                    args.isOverlayKeyToDefaultValueStr,
                    where
                ).let { isOverlayToErr ->
                    val funcErr = isOverlayToErr.second
                        ?: return@let isOverlayToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val returnBitmap = InnerBitmapArt.rectPuzzle(
                    bitmap,
                    rate,
                    times,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    colorList,
                    passionColorList,
                    passionInt,
                    isOverlay,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.ByArcArgs -> {
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
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val centerX = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.centerXToDefaultValueStr,
                    where
                ).let { centerXToErr ->
                    val funcErr = centerXToErr.second
                        ?: return@let centerXToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val centerY = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.centerYToDefaultValueStr,
                    where
                ).let { centerYToErr ->
                    val funcErr = centerYToErr.second
                        ?: return@let centerYToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val radius = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.radiusKeyToDefaultValueStr,
                    where
                ).let { radiusToErr ->
                    val funcErr = radiusToErr.second
                        ?: return@let radiusToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val bkColor = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.bkColorKeyToDefaultValueStr,
                    where
                ).let { bkColorToErr ->
                    val funcErr = bkColorToErr.second
                        ?: return@let bkColorToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.bkColorKeyToDefaultValueStr.first,
                        where
                    )
                }
                FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val bitmapKeyName = args.bitmapKeyToDefaultValueStr.first
                val bitmapToErrList = argsPairList.filter {
                    (argName, _) ->
                    argName == bitmapKeyName
                }.map {
                    (argName, valueStr) ->
                    FuncCheckerForSetting.Getter.getBitmapArg(
                        argName,
                        valueStr,
                        null,
                        null,
                        varNameToBitmapMap,
                        where
                    )
                }
                bitmapToErrList.forEach { (_, err) ->
                    if (err != null) {
                        return Pair(
                            null,
                            ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to err
                    }
                }
                val bitmapList = bitmapToErrList.filter{
                    it.first != null
                }.map {
                    it.first as Bitmap
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
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
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val sizeIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeInclineKeyToDefaultValueStr,
                    where
                ).let { sizeInclineToErr ->
                    val funcErr = sizeInclineToErr.second
                        ?: return@let sizeInclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val sizeOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeOffsetKeyToDefaultValueStr,
                    where
                ).let { sizeOffsetToErr ->
                    val funcErr = sizeOffsetToErr.second
                        ?: return@let sizeOffsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val startAngle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.startAngleKeyToDefaultValueStr,
                    where
                ).let { startAngleToErr ->
                    val funcErr = startAngleToErr.second
                        ?: return@let startAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val sweepAngle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sweepAngleKeyToDefaultValueStr,
                    where
                ).let { speepAngleToErr ->
                    val funcErr = speepAngleToErr.second
                        ?: return@let speepAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }

                val returnBitmap = InnerBitmapArt.byArc(
                    width,
                    height,
                    bitmapList,
                    centerX,
                    centerY,
                    radius,
                    startAngle,
                    sweepAngle,
                    opacityIncline,
                    opacityOffset,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    times,
                    bkColor,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.ByLineArgs -> {
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
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val centerX = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.centerXToDefaultValueStr,
                    where
                ).let { centerXToErr ->
                    val funcErr = centerXToErr.second
                        ?: return@let centerXToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val centerY = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.centerYToDefaultValueStr,
                    where
                ).let { centerYToErr ->
                    val funcErr = centerYToErr.second
                        ?: return@let centerYToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxRadius = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxRadiusKeyToDefaultValueStr,
                    where
                ).let { radiusToErr ->
                    val funcErr = radiusToErr.second
                        ?: return@let radiusToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val bkColor = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.bkColorKeyToDefaultValueStr,
                    where
                ).let { bkColorToErr ->
                    val funcErr = bkColorToErr.second
                        ?: return@let bkColorToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.bkColorKeyToDefaultValueStr.first,
                        where
                    )
                }
                FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val bitmapKeyName = args.bitmapKeyToDefaultValueStr.first
                val bitmapToErrList = argsPairList.filter {
                        (argName, _) ->
                    argName == bitmapKeyName
                }.map {
                        (argName, valueStr) ->
                    FuncCheckerForSetting.Getter.getBitmapArg(
                        argName,
                        valueStr,
                        null,
                        null,
                        varNameToBitmapMap,
                        where
                    )
                }
                bitmapToErrList.forEach { (_, err) ->
                    if (err != null) {
                        return Pair(
                            null,
                            ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to err
                    }
                }
                val bitmapList = bitmapToErrList.filter{
                    it.first != null
                }.map {
                    it.first as Bitmap
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
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
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val sizeIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeInclineKeyToDefaultValueStr,
                    where
                ).let { sizeInclineToErr ->
                    val funcErr = sizeInclineToErr.second
                        ?: return@let sizeInclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val sizeOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.sizeOffsetKeyToDefaultValueStr,
                    where
                ).let { sizeOffsetToErr ->
                    val funcErr = sizeOffsetToErr.second
                        ?: return@let sizeOffsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val angle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.angleKeyToDefaultValueStr,
                    where
                ).let { startAngleToErr ->
                    val funcErr = startAngleToErr.second
                        ?: return@let startAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }

                val returnBitmap = InnerBitmapArt.byLine(
                    width,
                    height,
                    bitmapList,
                    centerX,
                    centerY,
                    maxRadius,
                    angle,
                    opacityIncline,
                    opacityOffset,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    times,
                    bkColor,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.WaveArgs -> {
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
                val fanAngle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.fanAngleKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { colorLitConToErr ->
                    val funcErr = colorLitConToErr.second
                        ?: return@let colorLitConToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val returnBitmap = InnerBitmapArt.wave(
                    bitmap,
                    fanAngle,
                    times,
                    opacityIncline,
                    opacityOffset,
                    colorList,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.ShakeArgs -> {
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

                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val zoomRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.zoomRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    rate ->
                    if(rate > 1f) return@let rate
                    val spanZoomRateKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.zoomRateKeyToDefaultValueStr.first.toString()
                    )
                    val spanZoomRateValue = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        rate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "${spanZoomRateKey}(${spanZoomRateValue}) must be > 1: ${spanWhere}"
                    )
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { colorLitConToErr ->
                    val funcErr = colorLitConToErr.second
                        ?: return@let colorLitConToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
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
                ).let { minOpacityRateToErr ->
                    val funcErr = minOpacityRateToErr.second
                        ?: return@let minOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
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
                val direction = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.directionKeyToDefaultValueStr,
                    where
                ).let { directionToErr ->
                    val funcErr = directionToErr.second
                        ?: return@let directionToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    directionStr ->
                    args.directionEntries.first {
                        it.name == directionStr
                    }
                }
                val returnBitmap = InnerBitmapArt.shake(
                    bitmap,
                    zoomRate,
                    times,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    colorList,
                    direction,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is BitmapArtMethodArgClass.IncSplashArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val targetColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.targetColorKeyToDefaultValueStr,
                    where
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        args.targetColorKeyToDefaultValueStr.first,
                        where,
                    )
                } ?: return null
                val radiusRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.radiusRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    0f
                }
                val minPointNum = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.minPpintNumKeyToDefaultValueStr,
                    where
                ).let { numToErr ->
                    val funcErr = numToErr.second
                        ?: return@let numToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val maxPointNum = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.maxPointNumKeyToDefaultValueStr,
                    where
                ).let { numToErr ->
                    val funcErr = numToErr.second
                        ?: return@let numToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                if(minPointNum > maxPointNum) {
                    val spanMinPointNumKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.minPpintNumKeyToDefaultValueStr.first
                    )
                    val spanMaxPointNumKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.maxPointNumKeyToDefaultValueStr.first
                    )
                    val spanMinAngle = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minPointNum.toString()
                    )
                    val spanMaxAngle = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxPointNum.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be ${spanMinPointNumKey}(${spanMinAngle}) <= ${spanMaxPointNumKey}(${spanMaxAngle}): ${spanWhere}"
                    )
                }
                val minAngle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minAngleKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val maxAngle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxAngleKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                if(minAngle > maxAngle) {
                    val spanMinAngleKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.minAngleKeyToDefaultValueStr.first
                    )
                    val spanMaxAngleKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        args.maxAngleKeyToDefaultValueStr.first
                    )
                    val spanMinAngle = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minAngle.toString()
                    )
                    val spanMaxAngle = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxAngle.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be ${spanMinAngleKey}(${spanMinAngle}) <= ${spanMaxAngleKey}(${spanMaxAngle}): ${spanWhere}"
                    )
                }
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityInclineKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val opacityOffset = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val colorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorListKeyToDefaultValueStr,
                    where
                ).let { colorLitConToErr ->
                    val funcErr = colorLitConToErr.second
                        ?: return@let colorLitConToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { AltRegexTool.trim(it).isNotEmpty() }.map {
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
                ).let { minOpacityRateToErr ->
                    val funcErr = minOpacityRateToErr.second
                        ?: return@let minOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
                    1f
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }.let {
                    if(
                        0 <= it
                        && it <= 1f
                    ) return@let it
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
                val returnBitmap = InnerBitmapArt.drawInkSplashOnColor(
                    bitmap,
                    targetColorStr,
                    radiusRate,
                    times,
                    minPointNum,
                    maxPointNum,
                    colorList,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
        }
    }

    private object InnerBitmapArt {

        fun drawInkSplashOnColor(
            srcBitmap: Bitmap,
            targetColorStr: String,
            radiusRate: Float,
            times: Int,
            minPointNum: Int,
            maxPointNum: Int,
            colorList: List<String>,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                SpecialArt.drawInkSplashOnColor(
                    srcBitmap,
                    targetColorStr,
                    radiusRate,
                    times,
                    minPointNum,
                    maxPointNum,
                    colorList,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        fun execScaleBitmap(
            bitmap: Bitmap,
            width: Int,
            height: Int,
        ): Bitmap {
            val returnBitmap =
                bitmap.scale(width, height)
//                BitmapTool.ImageTransformer.cutCenter(
//                    it,
//                    width,
//                    height
//                )
            return returnBitmap
        }

        private fun get(
            wallRelativePath: String,
        ): String? {
            val fannelWallDirPath = UrlImageDownloader.fannelWallDirPath
            val fannelWallDirName = File(fannelWallDirPath).name
            if(
                !wallRelativePath
                    .startsWith(fannelWallDirName)
            ) return null
            val wallPathObj = File(
                UrlImageDownloader.imageDirObj.absolutePath,
                wallRelativePath
            )
            val wallPathOrDirPath = wallPathObj.absolutePath
            return when(true){
                wallPathObj.isFile -> {
                    if(
                        !ImageFile.isImageFile(wallPathOrDirPath)
                    ) return null
                    wallPathOrDirPath
                }
                else -> {
                    File(fannelWallDirPath).walk().filter {
                            wallImageFileEntry ->
                        if(
                            !wallImageFileEntry.isFile
                        ) return@filter false
                        val wallImageFilePath =
                            wallImageFileEntry.absolutePath
                        wallImageFilePath.startsWith(
                            wallPathOrDirPath
                        ) && ImageFile.isImageFile(
                            wallImageFilePath
                        )
                    }.shuffled().firstOrNull()?.absolutePath
                        ?: return null
                }
            }
        }

        private fun getBkImageFilePathFromDirPath(
            bkImageDirPath: String,
        ): String {
            return FileSystems.sortedFiles(
                bkImageDirPath
            ).random().let {
                File(bkImageDirPath, it).absolutePath
            }
        }

        suspend fun matrixStorm(
            pieceMap: Map<String, String>?,
            bitmap: Bitmap,
            goalWidth: Int,
            goalHeight: Int,
            xMulti: Int,
            yMulti: Int,
            xDup: Int,
            yDup: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val pieceWidth = goalWidth / xMulti
                val pieceHeight = goalHeight / yMulti
                val scaledBitmap = let scaleBitmap@ {
                    val pieceRotate = BitmapPieceManager.getRotate(
                        pieceMap ?: emptyMap(),
                    )
                    execScaleBitmap(
                        bitmap,
                        pieceWidth,
                        pieceHeight,
                    ).let { piece ->
                        if (
                            pieceRotate == 0f
                        ) return@let piece
                        BitmapTool.rotate(
                            piece,
                            pieceRotate,
                        )
                    }
                }
                CcDotArt.makeMatrixStorm(
                    arrayOf(scaledBitmap),
                    xMulti,
                    yMulti,
                    xDup,
                    yDup,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun byArc(
            width: Int,
            height: Int,
            peaceBitmapList: List<Bitmap>,
            centerX: Float,
            centerY: Float,
            radius: Float,
            startAngle: Float,
            sweepAngle: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            sizeIncline: Float,
            sizeOffset: Float,
            colorList: List<String>,
            times: Int,
            bkColor: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                BitmapArt.byArc(
                    width,
                    height,
                    peaceBitmapList,
                    centerX,
                    centerY,
                    radius,
                    startAngle,
                    sweepAngle,
                    opacityIncline,
                    opacityOffset,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    times,
                    bkColor,
                    ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun byLine(
            width: Int,
            height: Int,
            peaceBitmapList: List<Bitmap>,
            centerX: Float,
            centerY: Float,
            maxRadius: Float,
            angle: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            sizeIncline: Float,
            sizeOffset: Float,
            colorList: List<String>,
            times: Int,
            bkColor: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                BitmapArt.byLine(
                    width,
                    height,
                    peaceBitmapList,
                    centerX,
                    centerY,
                    maxRadius,
                    angle,
                    opacityIncline,
                    opacityOffset,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    times,
                    bkColor,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }

        suspend fun rectPuzzleAjustSize(
            bitmap: Bitmap,
            rate: Float,
            times: Int,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            sizeCenterX: Int,
            sizeCenterY: Int,
            sizeIncline: Float,
            sizeOffset: Float,
            colorList: List<String>,
            passionColorList: List<String>,
            passionInt: Int,
            isOverlay: Boolean,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                BitmapArt.rectPuzzleAjustSize(
                    bitmap,
                    rate,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    sizeCenterX,
                    sizeCenterY,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    passionColorList,
                    passionInt,
                    times,
                    isOverlay,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun rectPuzzle(
            bitmap: Bitmap,
            rate: Float,
            times: Int,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            colorList: List<String>,
            passionColorList: List<String>,
            passionInt: Int,
            isOverlay: Boolean,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                BitmapArt.rectPuzzle(
                    bitmap,
                    rate,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    colorList,
                    passionColorList,
                    passionInt,
                    times,
                    isOverlay,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun bitmapPuzzle(
            bitmap: Bitmap,
            bitmapList: List<Bitmap>,
//            pieceMapList: List<Map<String, String>?>,
            rate: Float,
            times: Int,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            sizeCenterX: Int,
            sizeCenterY: Int,
            sizeIncline: Float,
            sizeOffset: Float,
            colorList: List<String>,
            passionColorList: List<String>,
            passionInt: Int,
            isOverlay: Boolean,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
//                val scaledBitmapList = let scaleBitmapList@{
//                    pieceMapList.asSequence().map { pieceMap ->
//                        val icon = BitmapPieceManager.getIcon(
//                            pieceMap ?: emptyMap(),
//                        )
//                        val width = BitmapPieceManager.getWidth(
//                            pieceMap ?: emptyMap(),
//                        )
//                        val height = BitmapPieceManager.getHeight(
//                            pieceMap ?: emptyMap(),
//                        )
////                        FileSystems.updateFile(
////                            File(UsePath.cmdclickDefaultAppDirPath, "lBitmapPuzzle.txt").absolutePath,
////                            listOf(
////                                "pieceMap: ${pieceMap}",
////                                "icon: ${icon.str}",
////                            ).joinToString("\n")
////                        )
//                        val srcPieceBitmap = AppCompatResources.getDrawable(
//                            context,
//                            icon.id,
//                        )?.let {
//                            if (
//                                width != null
//                                && height != null
//                            ) {
//                                return@let it.toBitmap(
//                                    width,
//                                    height,
//                                )
//                            }
//                            it.toBitmap()
//                        } ?: return@map null
//                        val pieceRotate = BitmapPieceManager.getRotate(
//                            pieceMap ?: emptyMap(),
//                        )
//                        if(
//                            pieceRotate == 0f
//                        ) return@map srcPieceBitmap
//                        BitmapTool.rotate(
//                            srcPieceBitmap,
//                            pieceRotate,
//                        )
//                    }.filter {
//                        it != null
//                    }.map {
//                        it as Bitmap
//                    }
//                }.toList()
                BitmapArt.bitmapPuzzle(
                    bitmap,
                    bitmapList,
//                    scaledBitmapList,
                    rate,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    sizeCenterX ,
                    sizeCenterY,
                    sizeIncline,
                    sizeOffset,
                    colorList,
                    passionColorList,
                    passionInt,
                    times,
                    isOverlay,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun wave(
            bitmap: Bitmap,
            fanAngle: Float,
            times: Int,
            opacityIncline: Float,
            opacityOffset: Float,
            colorList: List<String>,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                BitmapArt.createFanShapedBitmap(
                    bitmap,
                    times,
                    bitmap.height,
                    fanAngle,
                    opacityIncline,
                    opacityOffset,
                    colorList,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }

        suspend fun shake(
            bitmap: Bitmap,
            zoomRate: Float,
            times: Int,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            colorList: List<String>,
            direction: BitmapArt.ShakeDirection,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                BitmapArt.shake(
                    bitmap,
                    zoomRate,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    colorList,
                    times,
                    direction,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: BitmapArtMethodArgClass,
    ){
        MATRIX_STORM("matrixStorm", BitmapArtMethodArgClass.MatrixStormArgs),
        RECT_PUZZLE("rectPuzzle", BitmapArtMethodArgClass.RectPuzzleArgs),
        RECT_PUZZLE_AJUST_SIZE("rectPuzzleAjustSize", BitmapArtMethodArgClass.RectPuzzleAjustSizeArgs),
        BITMAP_PUZZLE("bitmapPuzzle", BitmapArtMethodArgClass.BitmapPuzzleArgs),
        WAVE("wave", BitmapArtMethodArgClass.WaveArgs),
        BY_ARC("byArc", BitmapArtMethodArgClass.ByArcArgs),
        BY_LINE("byLine", BitmapArtMethodArgClass.ByLineArgs),
        SHAKE("shake", BitmapArtMethodArgClass.ShakeArgs),
        INC_SPLASH("incSplash", BitmapArtMethodArgClass.IncSplashArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class BitmapArtMethodArgClass {
        data object ByArcArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = ByArcEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.BITMAP.key,
                ByArcEnumArgs.BITMAP.defaultValueStr,
            )
            val timesKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.TIMES.key,
                ByArcEnumArgs.TIMES.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.OPACITY_INCLINE.key,
                ByArcEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.OPACITY_OFFSET.key,
                ByArcEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.COLOR_LIST.key,
                ByArcEnumArgs.COLOR_LIST.defaultValueStr
            )
            val sizeInclineKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.SIZE_INCLINE.key,
                ByArcEnumArgs.SIZE_INCLINE.defaultValueStr
            )
            val sizeOffsetKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.SIZE_OFFSET.key,
                ByArcEnumArgs.SIZE_OFFSET.defaultValueStr
            )
            val startAngleKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.START_ANGLE.key,
                ByArcEnumArgs.START_ANGLE.defaultValueStr
            )
            val sweepAngleKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.SWEEP_ANGLE.key,
                ByArcEnumArgs.SWEEP_ANGLE.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.WIDTH.key,
                ByArcEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.HEIGHT.key,
                ByArcEnumArgs.HEIGHT.defaultValueStr
            )
            val centerXToDefaultValueStr = Pair(
                ByArcEnumArgs.CENTER_X.key,
                ByArcEnumArgs.CENTER_X.defaultValueStr
            )
            val centerYToDefaultValueStr = Pair(
                ByArcEnumArgs.CENTER_Y.key,
                ByArcEnumArgs.CENTER_Y.defaultValueStr
            )
            val radiusKeyToDefaultValueStr = Pair(
                ByArcEnumArgs.RADIUS.key,
                ByArcEnumArgs.RADIUS.defaultValueStr
            )
            val bkColorKeyToDefaultValueStr =  Pair(
                ByArcEnumArgs.BK_COLOR.key,
                ByArcEnumArgs.BK_COLOR.defaultValueStr
            )
            enum class ByArcEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                CENTER_X("centerX", null, FuncCheckerForSetting.ArgType.INT),
                CENTER_Y("centerY", null, FuncCheckerForSetting.ArgType.INT),
                RADIUS("radius", null, FuncCheckerForSetting.ArgType.INT),
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                TIMES("times", 1000.toString(), FuncCheckerForSetting.ArgType.INT),
                SIZE_INCLINE(
                    "sizeIncline",
                    0.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                SIZE_OFFSET("sizeOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
//                PASSION_COLOR_LIST("passionColorList", String(), FuncCheckerForSetting.ArgType.STRING),
//                PASSION_RATE("passionRate", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                START_ANGLE("startAngle", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                SWEEP_ANGLE("sweepAngle", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                BK_COLOR("bkColor", "#00000000", FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object ByLineArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = ByLineEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.BITMAP.key,
                ByLineEnumArgs.BITMAP.defaultValueStr,
            )
            val timesKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.TIMES.key,
                ByLineEnumArgs.TIMES.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.OPACITY_INCLINE.key,
                ByLineEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.OPACITY_OFFSET.key,
                ByLineEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.COLOR_LIST.key,
                ByLineEnumArgs.COLOR_LIST.defaultValueStr
            )
            val sizeInclineKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.SIZE_INCLINE.key,
                ByLineEnumArgs.SIZE_INCLINE.defaultValueStr
            )
            val sizeOffsetKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.SIZE_OFFSET.key,
                ByLineEnumArgs.SIZE_OFFSET.defaultValueStr
            )
            val angleKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.ANGLE.key,
                ByLineEnumArgs.ANGLE.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.WIDTH.key,
                ByLineEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.HEIGHT.key,
                ByLineEnumArgs.HEIGHT.defaultValueStr
            )
            val centerXToDefaultValueStr = Pair(
                ByLineEnumArgs.CENTER_X.key,
                ByLineEnumArgs.CENTER_X.defaultValueStr
            )
            val centerYToDefaultValueStr = Pair(
                ByLineEnumArgs.CENTER_Y.key,
                ByLineEnumArgs.CENTER_Y.defaultValueStr
            )
            val maxRadiusKeyToDefaultValueStr = Pair(
                ByLineEnumArgs.MAX_RADIUS.key,
                ByLineEnumArgs.MAX_RADIUS.defaultValueStr
            )
            val bkColorKeyToDefaultValueStr =  Pair(
                ByLineEnumArgs.BK_COLOR.key,
                ByLineEnumArgs.BK_COLOR.defaultValueStr
            )
            enum class ByLineEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                CENTER_X("centerX", null, FuncCheckerForSetting.ArgType.INT),
                CENTER_Y("centerY", null, FuncCheckerForSetting.ArgType.INT),
                MAX_RADIUS("maxRadius", null, FuncCheckerForSetting.ArgType.INT),
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                TIMES("times", 1000.toString(), FuncCheckerForSetting.ArgType.INT),
                SIZE_INCLINE(
                    "sizeIncline",
                    0.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                SIZE_OFFSET("sizeOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                ANGLE("angle", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                BK_COLOR("bkColor", "#00000000", FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object RectPuzzleArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = RectPuzzleEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.BITMAP.key,
                RectPuzzleEnumArgs.BITMAP.defaultValueStr,
            )
            val rateKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.RATE.key,
                RectPuzzleEnumArgs.RATE.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.TIMES.key,
                RectPuzzleEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.MIN_OPACITY_RATE.key,
                RectPuzzleEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.MAX_OPACITY_RATE.key,
                RectPuzzleEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.OPACITY_INCLINE.key,
                RectPuzzleEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.OPACITY_OFFSET.key,
                RectPuzzleEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.COLOR_LIST.key,
                RectPuzzleEnumArgs.COLOR_LIST.defaultValueStr
            )
            val passionColorListKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.PASSION_COLOR_LIST.key,
                RectPuzzleEnumArgs.PASSION_COLOR_LIST.defaultValueStr
            )
            val passionRateKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.PASSION_RATE.key,
                RectPuzzleEnumArgs.PASSION_RATE.defaultValueStr
            )
            val isOverlayKeyToDefaultValueStr = Pair(
                RectPuzzleEnumArgs.IS_OVERLAY.key,
                RectPuzzleEnumArgs.IS_OVERLAY.defaultValueStr
            )
            enum class RectPuzzleEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                RATE("rate", (0.9).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                TIMES("times", 1000.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    (0.5).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                PASSION_COLOR_LIST("passionColorList", String(), FuncCheckerForSetting.ArgType.STRING),
                PASSION_RATE("passionRate", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                IS_OVERLAY("isOverlay", false.toString(), FuncCheckerForSetting.ArgType.BOOL),
            }
        }
        data object RectPuzzleAjustSizeArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = RectPuzzleAjustSizeEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.BITMAP.key,
                RectPuzzleAjustSizeEnumArgs.BITMAP.defaultValueStr,
            )
            val rateKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.RATE.key,
                RectPuzzleAjustSizeEnumArgs.RATE.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.TIMES.key,
                RectPuzzleAjustSizeEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.MIN_OPACITY_RATE.key,
                RectPuzzleAjustSizeEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.MAX_OPACITY_RATE.key,
                RectPuzzleAjustSizeEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.OPACITY_INCLINE.key,
                RectPuzzleAjustSizeEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.OPACITY_OFFSET.key,
                RectPuzzleAjustSizeEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val sizeCenterXKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.SIZE_CENTER_X.key,
                RectPuzzleAjustSizeEnumArgs.SIZE_CENTER_X.defaultValueStr
            )
            val sizeCenterYKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.SIZE_CENTER_Y.key,
                RectPuzzleAjustSizeEnumArgs.SIZE_CENTER_Y.defaultValueStr
            )
            val sizeInclineKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.SIZE_INCLINE.key,
                RectPuzzleAjustSizeEnumArgs.SIZE_INCLINE.defaultValueStr,
            )
            val sizeOffsetKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.SIZE_OFFSET.key,
                RectPuzzleAjustSizeEnumArgs.SIZE_OFFSET.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.COLOR_LIST.key,
                RectPuzzleAjustSizeEnumArgs.COLOR_LIST.defaultValueStr
            )
            val passionColorListKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.PASSION_COLOR_LIST.key,
                RectPuzzleAjustSizeEnumArgs.PASSION_COLOR_LIST.defaultValueStr
            )
            val passionRateKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.PASSION_RATE.key,
                RectPuzzleAjustSizeEnumArgs.PASSION_RATE.defaultValueStr
            )
            val isOverlayKeyToDefaultValueStr = Pair(
                RectPuzzleAjustSizeEnumArgs.IS_OVERLAY.key,
                RectPuzzleAjustSizeEnumArgs.IS_OVERLAY.defaultValueStr
            )
            enum class RectPuzzleAjustSizeEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                RATE("rate", (0.9).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                TIMES("times", 1000.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    (0.5).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                SIZE_CENTER_X("sizeCenterX", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                SIZE_CENTER_Y("sizeCenterY", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                SIZE_INCLINE("sizeIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                SIZE_OFFSET("sizeOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                PASSION_COLOR_LIST("passionColorList", String(), FuncCheckerForSetting.ArgType.STRING),
                PASSION_RATE("passionRate", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                IS_OVERLAY("isOverlay", false.toString(), FuncCheckerForSetting.ArgType.BOOL),
            }
        }
        data object BitmapPuzzleArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = BitmapPuzzleEnumArgs.entries
            val bkBitmapKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.BK_BITMAP.key,
                BitmapPuzzleEnumArgs.BK_BITMAP.defaultValueStr,
            )
            val bitmapKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.BITMAP.key,
                BitmapPuzzleEnumArgs.BITMAP.defaultValueStr,
            )
//            val pieceKeyToDefaultValueStr = Pair(
//                BitmapPuzzleEnumArgs.PIECE.key,
//                BitmapPuzzleEnumArgs.PIECE.defaultValueStr
//            )
            val rateKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.RATE.key,
                BitmapPuzzleEnumArgs.RATE.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.TIMES.key,
                BitmapPuzzleEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.MIN_OPACITY_RATE.key,
                BitmapPuzzleEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.MAX_OPACITY_RATE.key,
                BitmapPuzzleEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.OPACITY_INCLINE.key,
                BitmapPuzzleEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.OPACITY_OFFSET.key,
                BitmapPuzzleEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val sizeCenterXKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.SIZE_CENTER_X.key,
                BitmapPuzzleEnumArgs.SIZE_CENTER_X.defaultValueStr
            )
            val sizeCenterYKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.SIZE_CENTER_Y.key,
                BitmapPuzzleEnumArgs.SIZE_CENTER_Y.defaultValueStr
            )
            val sizeInclineKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.SIZE_INCLINE.key,
                BitmapPuzzleEnumArgs.SIZE_INCLINE.defaultValueStr,
            )
            val sizeOffsetKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.SIZE_OFFSET.key,
                BitmapPuzzleEnumArgs.SIZE_OFFSET.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.COLOR_LIST.key,
                BitmapPuzzleEnumArgs.COLOR_LIST.defaultValueStr
            )
            val passionColorListKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.PASSION_COLOR_LIST.key,
                BitmapPuzzleEnumArgs.PASSION_COLOR_LIST.defaultValueStr
            )
            val passionRateKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.PASSION_RATE.key,
                BitmapPuzzleEnumArgs.PASSION_RATE.defaultValueStr
            )
            val isOverlayKeyToDefaultValueStr = Pair(
                BitmapPuzzleEnumArgs.IS_OVERLAY.key,
                BitmapPuzzleEnumArgs.IS_OVERLAY.defaultValueStr
            )
            enum class BitmapPuzzleEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BK_BITMAP("bkBitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
//                PIECE("piece", null, FuncCheckerForSetting.ArgType.STRING),
                RATE("rate", (0.9).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                TIMES("times", 1000.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    (0.5).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                PASSION_COLOR_LIST("passionColorList", String(), FuncCheckerForSetting.ArgType.STRING),
                PASSION_RATE("passionRate", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                IS_OVERLAY("isOverlay", false.toString(), FuncCheckerForSetting.ArgType.BOOL),
                SIZE_CENTER_X("sizeCenterX", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                SIZE_CENTER_Y("sizeCenterY", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                SIZE_INCLINE("sizeIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                SIZE_OFFSET("sizeOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object WaveArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = WaveEnumArgs.entries
            val fanAngleKeyToDefaultValueStr = Pair(
                WaveEnumArgs.FAN_ANGLE.key,
                WaveEnumArgs.FAN_ANGLE.defaultValueStr,
            )
            val bitmapKeyToDefaultValueStr = Pair(
                WaveEnumArgs.BITMAP.key,
                WaveEnumArgs.BITMAP.defaultValueStr,
            )
            val timesKeyToDefaultValueStr = Pair(
                WaveEnumArgs.TIMES.key,
                WaveEnumArgs.TIMES.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                WaveEnumArgs.OPACITY_INCLINE.key,
                WaveEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                WaveEnumArgs.OPACITY_OFFSET.key,
                WaveEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                WaveEnumArgs.COLOR_LIST.key,
                WaveEnumArgs.COLOR_LIST.defaultValueStr
            )
            enum class WaveEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                TIMES("timestimes", 1000.toString(), FuncCheckerForSetting.ArgType.INT),
                FAN_ANGLE("fanAngle", 90.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object ShakeArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = ShakeEnumArgs.entries
            val directionEntries = BitmapArt.ShakeDirection.entries
            val zoomRateKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.ZOOM_RATE.key,
                ShakeEnumArgs.ZOOM_RATE.defaultValueStr,
            )
            val bitmapKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.BITMAP.key,
                ShakeEnumArgs.BITMAP.defaultValueStr,
            )
            val timesKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.TIMES.key,
                ShakeEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.MIN_OPACITY_RATE.key,
                ShakeEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.MAX_OPACITY_RATE.key,
                ShakeEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.OPACITY_INCLINE.key,
                ShakeEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.OPACITY_OFFSET.key,
                ShakeEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.COLOR_LIST.key,
                ShakeEnumArgs.COLOR_LIST.defaultValueStr
            )
            val directionKeyToDefaultValueStr = Pair(
                ShakeEnumArgs.DIRECTION.key,
                ShakeEnumArgs.DIRECTION.defaultValueStr
            )

            enum class ShakeEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                ZOOM_RATE("zoomRate", null, FuncCheckerForSetting.ArgType.FLOAT),
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                TIMES("times", 1000.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    (0.5).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                DIRECTION("direction", BitmapArt.ShakeDirection.RND.name, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object MatrixStormArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = MatrixStormEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.BITMAP.key,
                MatrixStormEnumArgs.BITMAP.defaultValueStr,
            )
            val widthKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.WIDTH.key,
                MatrixStormEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.HEIGHT.key,
                MatrixStormEnumArgs.HEIGHT.defaultValueStr
            )
            val xMultiKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.X_MULTI.key,
                MatrixStormEnumArgs.X_MULTI.defaultValueStr
            )
            val yMultiKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.Y_MULTI.key,
                MatrixStormEnumArgs.Y_MULTI.defaultValueStr
            )
            val xDupKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.X_DUP.key,
                MatrixStormEnumArgs.X_DUP.defaultValueStr
            )
            val yDupKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.Y_DUP.key,
                MatrixStormEnumArgs.Y_DUP.defaultValueStr
            )
            val pieceKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.PIECE.key,
                MatrixStormEnumArgs.PIECE.defaultValueStr
            )
            private const val widthSrc = 300
            private const val heightSrc = widthSrc * 2
            private const val xMultiSrc = 60
            private const val yMultiSrc = xMultiSrc * 2
            enum class MatrixStormEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                WIDTH("width", widthSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", heightSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                X_MULTI("xMulti", xMultiSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                Y_MULTI("yMulti", yMultiSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                PIECE("piece", String(), FuncCheckerForSetting.ArgType.STRING),
                X_DUP("xDup", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                Y_DUP("yDup", 0.toString(), FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object IncSplashArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = IncSplashEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.BITMAP.key,
                IncSplashEnumArgs.BITMAP.defaultValueStr,
            )
            val targetColorKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.TARGET_COLOR.key,
                IncSplashEnumArgs.TARGET_COLOR.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.TIMES.key,
                IncSplashEnumArgs.TIMES.defaultValueStr
            )
            val radiusRateKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.RADIUS_RATE.key,
                IncSplashEnumArgs.RADIUS_RATE.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.MIN_OPACITY_RATE.key,
                IncSplashEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.MAX_OPACITY_RATE.key,
                IncSplashEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val minAngleKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.MIN_ANGLE.key,
                IncSplashEnumArgs.MIN_ANGLE.defaultValueStr
            )
            val maxAngleKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.MAX_ANGLE.key,
                IncSplashEnumArgs.MAX_ANGLE.defaultValueStr
            )
            val minPpintNumKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.MIN_POINT_NUM.key,
                IncSplashEnumArgs.MIN_POINT_NUM.defaultValueStr
            )
            val maxPointNumKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.MAX_POINT_NUM.key,
                IncSplashEnumArgs.MAX_POINT_NUM.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.COLOR_LIST.key,
                IncSplashEnumArgs.COLOR_LIST.defaultValueStr
            )
            val opacityOffsetKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.OPACITY_OFFSET.key,
                IncSplashEnumArgs.OPACITY_OFFSET.defaultValueStr
            )
            val opacityInclineKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.OPACITY_INCLINE.key,
                IncSplashEnumArgs.OPACITY_INCLINE.defaultValueStr
            )
            enum class IncSplashEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                TARGET_COLOR("targetColor", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                TIMES("times", 100.toString(), FuncCheckerForSetting.ArgType.INT),
                RADIUS_RATE("radiusRate", (0.3).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    (0.5).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                OPACITY_INCLINE("opacityIncline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OPACITY_OFFSET("opacityOffset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_ANGLE("minAngle", (0).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_ANGLE("maxAngle", (1).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_POINT_NUM("minPointNum", (3).toString(), FuncCheckerForSetting.ArgType.INT),
                MAX_POINT_NUM("maxPointNum", (5).toString(), FuncCheckerForSetting.ArgType.INT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }

    private object BitmapPieceManager {

        private const val keySeparator = '|'

        enum class PieceKey(val key: String) {
            WIDTH("width"),
            HEIGHT("height"),
            ICON("icon"),
            ROTATE("rotate"),
        }
        fun makePieceMap(
            argsPairList: List<Pair<String, String>>,
        ): Map<String, String>? {
            val pieceKey = BitmapArtMethodArgClass.MatrixStormArgs.MatrixStormEnumArgs.PIECE.key
            return argsPairList.asSequence().filter {
                    (argKey, _) ->
                argKey == pieceKey
            }.map {
                    (_, pieceMapCon) ->
                CmdClickMap.createMap(
                    pieceMapCon,
                    keySeparator
                ).toMap()
            }.firstOrNull()
        }
        fun getRotate(pieceMap: Map<String, String>): Float {
            return try {
                pieceMap.get(
                    PieceKey.ROTATE.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: 0f
        }
        fun getWidth(pieceMap: Map<String, String>): Int? {
            return try {
                pieceMap.get(
                    PieceKey.WIDTH.key
                )?.toInt()
            } catch (e: Exception){
                null
            }
        }
        fun getHeight(pieceMap: Map<String, String>): Int? {
            return try {
                pieceMap.get(
                    PieceKey.HEIGHT.key
                )?.toInt()
            } catch (e: Exception){
                null
            }
        }
        fun getIcon(pieceMap: Map<String, String>): CmdClickIcons {
            return pieceMap.get(
                PieceKey.ICON.key
            )?.let {
                shapeStr ->
                CmdClickIcons.entries.firstOrNull {
                    it.str == shapeStr
                }
            } ?: CmdClickIcons.RECT
        }
    }
}