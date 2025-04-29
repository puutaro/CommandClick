package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Path
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.image_tools.GraphicPathTool
import com.puutaro.commandclick.util.image_tools.LineArt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set

object LineArtForImageAction {

    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

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
            is MonoArtMethodArgClass.TangentArgs -> {
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
                    where,
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
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
                }.let {
                    widthSrc ->
                    if(widthSrc <= 0) return@let bitmap.width
                    widthSrc
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
                }.let {
                        heightSrc ->
                    if(heightSrc <= 0) return@let bitmap.height
                    heightSrc
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
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
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
                val strokeWidth = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.strokeWidthKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minStrokeWidthRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxStrokeWidthRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val angle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.angleKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minAngleShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minAngleShrinkRateKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxAngleShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxAngleShrinkRateKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minAngleShrinkRate,
                    maxAngleShrinkRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minAngleShrinkRateKeyToDefaultValueStr.first,
                    args.maxAngleShrinkRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val length = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.lengthKeyToDefaultValueStr,
                    where
                ).let { lengthToErr ->
                    val funcErr = lengthToErr.second
                        ?: return@let lengthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minLengthShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minLengthShrinkRateKeyToDefaultValueStr,
                    where
                ).let { lengthToErr ->
                    val funcErr = lengthToErr.second
                        ?: return@let lengthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxLengthShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxLengthShrinkRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minLengthShrinkRateKeyToDefaultValueStr.first,
                    args.maxLengthShrinkRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val bitmapArt = InnerLineArt.tan(
                    bitmap,
                    width,
                    height,
                    times,
                    colorList,
                    strokeWidth,
                    minStrokeWidthRate,
                    maxStrokeWidthRate,
                    minOpacityRate,
                    maxOpacityRate,
                    angle,
                    minAngleShrinkRate,
                    maxAngleShrinkRate,
                    length,
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
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
            is MonoArtMethodArgClass.FromPathArgs -> {
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
                val pathDataStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.pathDataKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val times = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
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
                val opacity = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.opacityKeyToDefaultValueStr,
                    where
                ).let { opacityToErr ->
                    val funcErr = opacityToErr.second
                        ?: return@let opacityToErr.first
                    return  Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    if(it <= 1f) return@let it
                    1f
                }
                val minOpacityRate = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
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
                val maxOpacityRate = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
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
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
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
                val angle = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.angleKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val marginRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.marginRateKeyToDefaultValueStr,
                    where
                ).let { marginRateToErr ->
                    val funcErr = marginRateToErr.second
                        ?: return@let marginRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minAngleShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minAngleShrinkRateKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxAngleShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxAngleShrinkRateKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minAngleShrinkRate,
                    maxAngleShrinkRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minAngleShrinkRateKeyToDefaultValueStr.first,
                    args.maxAngleShrinkRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val length = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    args.lengthKeyToDefaultValueStr,
                    where
                ).let { lengthToErr ->
                    val funcErr = lengthToErr.second
                        ?: return@let lengthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minLengthShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minLengthShrinkRateKeyToDefaultValueStr,
                    where
                ).let { lengthToErr ->
                    val funcErr = lengthToErr.second
                        ?: return@let lengthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxLengthShrinkRate = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxLengthShrinkRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minLengthShrinkRateKeyToDefaultValueStr.first,
                    args.maxLengthShrinkRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val bitmapArt = InnerLineArt.fromPath(
                    width,
                    height,
                    pathDataStr,
                    times,
                    colorList,
                    marginRate,
                    opacity,
                    minOpacityRate,
                    maxOpacityRate,
                    angle,
                    minAngleShrinkRate,
                    maxAngleShrinkRate,
                    length,
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
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
            is MonoArtMethodArgClass.ByAngleArgs -> {
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
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val times = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
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
                val minStrokeWidth = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.minStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxStrokeWidth = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxStrokeWidthKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
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
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
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
                val minAngle = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.minAngleKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxAngle = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.maxAngleKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minAngle,
                    maxAngle,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.EQUAL,
                    args.minAngleKeyToDefaultValueStr.first,
                    args.maxAngleKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val length = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByName(
                    mapArgMapList,
                    args.lengthKeyToDefaultValueStr,
                    where
                ).let { lengthToErr ->
                    val funcErr = lengthToErr.second
                        ?: return@let lengthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val minLengthShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.minLengthShrinkRateKeyToDefaultValueStr,
                    where
                ).let { lengthToErr ->
                    val funcErr = lengthToErr.second
                        ?: return@let lengthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val maxLengthShrinkRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxLengthShrinkRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minLengthShrinkRateKeyToDefaultValueStr.first,
                    args.maxLengthShrinkRateKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }

                val bitmapArt = InnerLineArt.drawAngleLine(
                    bitmap,
                    length,
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minOpacityRate,
                    maxOpacityRate,
                    minAngle,
                    maxAngle,
                    colorList,
                    times,
                    where
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
        fun tan(
            bitmap: Bitmap,
            width: Int,
            height: Int,
            times:Int,
            colorList: List<String>,
            strokeWidth: Float,
            minStrokeWidthRate: Float,
            maxStrokeWidthRate: Float,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            angleSrc: Float,
            minAngleShrinkRate: Float,
            maxAngleShrinkRate: Float,
            length: Int,
            minLengthShrinkRate: Float,
            maxLengthShrinkRate: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                LineArt.drawTangents(
                    bitmap,
                    width,
                    height,
                    times,
                    colorList,
                    strokeWidth,
                    minStrokeWidthRate,
                    maxStrokeWidthRate,
                    minOpacityRate,
                    maxOpacityRate,
                    angleSrc,
                    minAngleShrinkRate,
                    maxAngleShrinkRate,
                    length,
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun drawAngleLine(
            bitmap: Bitmap,
            baseLength: Float,
            minLengthRate: Float,
            maxLengthRate: Float,
            minStrokeWidth: Float,
            maxStrokeWidth: Float,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            minAngle: Int,
            maxAngle: Int,
            colorList: List<String>,
            times: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                LineArt.drawAngleyLine(
                    bitmap,
                    baseLength,
                    minLengthRate,
                    maxLengthRate,
                    minStrokeWidth,
                    maxStrokeWidth,
                    minOpacityRate,
                    maxOpacityRate,
                    minAngle,
                    maxAngle,
                    colorList,
                    times,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        suspend fun fromPath(
            baseWidth: Int,
            baseHeight: Int,
            pathDataStr: String,
            times: Int,
            colorList: List<String>,
            marginRate: Float,
            opacity: Float,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            angle: Float,
            minAngleShrinkRate: Float,
            maxAngleShrinkRate: Float,
            length: Int,
            minLengthShrinkRate: Float,
            maxLengthShrinkRate: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val scaledPath = let {
                    val path = LineArt.convertStrToPath(pathDataStr)
                    val pathWidth = GraphicPathTool.getPathWidth(path)
                    val pathHeight = GraphicPathTool.getPathHeight(path)
                    val zoomRate = (pathWidth + length) / pathWidth
                    GraphicPathTool.scalePath(
                        path,
                        null, //pathWidth / 2f,
                        null, //pathHeight / 2f,
                        baseWidth / (pathWidth * marginRate),
                        baseHeight / (pathHeight * marginRate),
                    )
                    path
                }
                execFromPath(
//                    shape,
                    baseWidth,
                    baseHeight,
                    scaledPath,
//                    baseBitmap,
                    times,
                    colorList,
                    opacity,
                    minOpacityRate,
                    maxOpacityRate,
                    angle,
                    minAngleShrinkRate,
                    maxAngleShrinkRate,
                    length,
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        private suspend fun execFromPath(
            baseWidth: Int,
            baseHeight: Int,
            path: Path,
            times: Int,
            colorList: List<String>,
            opacity: Float,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            angle: Float,
            minAngleShrinkRate: Float,
            maxAngleShrinkRate: Float,
            length: Int,
            minLengthShrinkRate: Float,
            maxLengthShrinkRate: Float,
        ): Bitmap {
            val baseBitmap =
                BitmapTool.ImageTransformer.makeRect(
                    "#00000000",
                    baseWidth,
                    baseHeight,
                )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "tclow.txt").absolutePath,
//                listOf(
//                    "direction: ${direction}"
//                ).joinToString("\n")
//            )
            return withContext(Dispatchers.IO) {
                LineArt.drawRandomLinesAroundPath(
                    baseBitmap,
                    path,
                    times,
                    colorList,
                    opacity,
                    minOpacityRate,
                    maxOpacityRate,
                    angle,
                    minAngleShrinkRate,
                    maxAngleShrinkRate,
                    length,
                    minLengthShrinkRate,
                    maxLengthShrinkRate,
                )
            }

        }

        fun generateLineArt(bitmap: Bitmap): Bitmap {
            val width = bitmap.width
            val height = bitmap.height

            val resultBitmap = createBitmap(width, height, bitmap.config!!)

            val matrix = ColorMatrix()
            matrix.setSaturation(0f) // 彩度を0にしてグレースケールにする

            val filter = ColorMatrixColorFilter(matrix)

            // Find Edgesフィルター（簡略版）
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = bitmap[x, y]
                    val gray = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3

                    val newColor = if (gray > 128) Color.BLACK else Color.WHITE
                    resultBitmap[x, y] = newColor
                }
            }

            return resultBitmap
        }
    }
    private enum class MethodNameClass(
        val str: String,
        val args: MonoArtMethodArgClass
    ){
        RND_ORTHOGONAL_RECT("fromPath", MonoArtMethodArgClass.FromPathArgs),
        TAN("tan", MonoArtMethodArgClass.TangentArgs),
        BY_ANGLE("byAngle", MonoArtMethodArgClass.ByAngleArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class MonoArtMethodArgClass {
        data object FromPathArgs : MonoArtMethodArgClass(), ArgType {
            override val entries = FromPathEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.WIDTH.key,
                FromPathEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.HEIGHT.key,
                FromPathEnumArgs.HEIGHT.defaultValueStr
            )
            val pathDataKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.PATH_DATA.key,
                FromPathEnumArgs.PATH_DATA.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.COLOR_LIST.key,
                FromPathEnumArgs.COLOR_LIST.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.TIMES.key,
                FromPathEnumArgs.TIMES.defaultValueStr
            )
            val opacityKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.OPACITY.key,
                FromPathEnumArgs.OPACITY.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.MIN_OPACITY_RATE.key,
                FromPathEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.MAX_OPACITY_RATE.key,
                FromPathEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val angleKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.ANGLE.key,
                FromPathEnumArgs.ANGLE.defaultValueStr
            )
            val marginRateKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.MARGIN_RATE.key,
                FromPathEnumArgs.MARGIN_RATE.defaultValueStr
            )
            val minAngleShrinkRateKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.MIN_ANGLE_SHRINK_RATE.key,
                FromPathEnumArgs.MIN_ANGLE_SHRINK_RATE.defaultValueStr
            )
            val maxAngleShrinkRateKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.MAX_ANGLE_SHRINK_RATE.key,
                FromPathEnumArgs.MAX_ANGLE_SHRINK_RATE.defaultValueStr
            )
            val lengthKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.LENGTH.key,
                FromPathEnumArgs.LENGTH.defaultValueStr
            )
            val minLengthShrinkRateKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.MIN_LENGTH_SHRINK_RATE.key,
                FromPathEnumArgs.MIN_LENGTH_SHRINK_RATE.defaultValueStr
            )
            val maxLengthShrinkRateKeyToDefaultValueStr = Pair(
                FromPathEnumArgs.MAX_LENGTH_SHRINK_RATE.key,
                FromPathEnumArgs.MAX_LENGTH_SHRINK_RATE.defaultValueStr
            )

            enum class FromPathEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                PATH_DATA("pathData", null, FuncCheckerForSetting.ArgType.STRING),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                TIMES("times", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                OPACITY(
                    "opacity",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
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
                ANGLE(
                    "angle",
                    360.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MIN_ANGLE_SHRINK_RATE(
                    "minAngleShrinkRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_ANGLE_SHRINK_RATE(
                    "maxAngleShrinkRate",
                    (0.5).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MARGIN_RATE(
                    "marginRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                LENGTH(
                    "length",
                    (100).toString(),
                    FuncCheckerForSetting.ArgType.INT
                ),
                MIN_LENGTH_SHRINK_RATE(
                    "minLengthShrinkRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_LENGTH_SHRINK_RATE(
                    "maxLengthShrinkRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
            }
        }

        data object TangentArgs : MonoArtMethodArgClass(), ArgType {
            override val entries = TanEnumArgs.entries
            const val defaultMinusGeo = -1
            val bitmapKeyToDefaultValueStr = Pair(
                TanEnumArgs.BITMAP.key,
                TanEnumArgs.BITMAP.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                TanEnumArgs.WIDTH.key,
                TanEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                TanEnumArgs.HEIGHT.key,
                TanEnumArgs.HEIGHT.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                TanEnumArgs.COLOR_LIST.key,
                TanEnumArgs.COLOR_LIST.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                TanEnumArgs.TIMES.key,
                TanEnumArgs.TIMES.defaultValueStr
            )
            val opacityKeyToDefaultValueStr = Pair(
                TanEnumArgs.OPACITY.key,
                TanEnumArgs.OPACITY.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                TanEnumArgs.MIN_OPACITY_RATE.key,
                TanEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                TanEnumArgs.MAX_OPACITY_RATE.key,
                TanEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val angleKeyToDefaultValueStr = Pair(
                TanEnumArgs.ANGLE.key,
                TanEnumArgs.ANGLE.defaultValueStr
            )
            val minAngleShrinkRateKeyToDefaultValueStr = Pair(
                TanEnumArgs.MIN_ANGLE_SHRINK_RATE.key,
                TanEnumArgs.MIN_ANGLE_SHRINK_RATE.defaultValueStr
            )
            val maxAngleShrinkRateKeyToDefaultValueStr = Pair(
                TanEnumArgs.MAX_ANGLE_SHRINK_RATE.key,
                TanEnumArgs.MAX_ANGLE_SHRINK_RATE.defaultValueStr
            )
            val lengthKeyToDefaultValueStr = Pair(
                TanEnumArgs.LENGTH.key,
                TanEnumArgs.LENGTH.defaultValueStr
            )
            val minLengthShrinkRateKeyToDefaultValueStr = Pair(
                TanEnumArgs.MIN_LENGTH_SHRINK_RATE.key,
                TanEnumArgs.MIN_LENGTH_SHRINK_RATE.defaultValueStr
            )
            val maxLengthShrinkRateKeyToDefaultValueStr = Pair(
                TanEnumArgs.MAX_LENGTH_SHRINK_RATE.key,
                TanEnumArgs.MAX_LENGTH_SHRINK_RATE.defaultValueStr
            )
            val strokeWidthKeyToDefaultValueStr = Pair(
                TanEnumArgs.STROKE_WIDTH.key,
                TanEnumArgs.STROKE_WIDTH.defaultValueStr
            )
            val minStrokeWidthKeyToDefaultValueStr = Pair(
                TanEnumArgs.MIN_STROKE_WIDTH.key,
                TanEnumArgs.MIN_STROKE_WIDTH.defaultValueStr
            )
            val maxStrokeWidthKeyToDefaultValueStr = Pair(
                TanEnumArgs.MAX_STROKE_WIDTH.key,
                TanEnumArgs.MAX_STROKE_WIDTH.defaultValueStr
            )

            enum class TanEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                WIDTH("width", defaultMinusGeo.toString(), FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", defaultMinusGeo.toString(), FuncCheckerForSetting.ArgType.INT),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                TIMES("times", (-1).toString(), FuncCheckerForSetting.ArgType.INT),
                OPACITY(
                    "opacity",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
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
                ANGLE(
                    "angle",
                    360.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MIN_ANGLE_SHRINK_RATE(
                    "minAngleShrinkRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_ANGLE_SHRINK_RATE(
                    "maxAngleShrinkRate",
                    (0.5).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                LENGTH(
                    "length",
                    (100).toString(),
                    FuncCheckerForSetting.ArgType.INT
                ),
                MIN_LENGTH_SHRINK_RATE(
                    "minLengthShrinkRate",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_LENGTH_SHRINK_RATE(
                    "maxLengthShrinkRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                STROKE_WIDTH(
                    "strokeWidth",
                    (1).toString(),
                    FuncCheckerForSetting.ArgType.INT
                ),
                MIN_STROKE_WIDTH(
                    "minStrokeWidth",
                    (0.1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_STROKE_WIDTH(
                    "maxStrokeWidth",
                    (1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT,
                )
            }
        }
        data object ByAngleArgs : MonoArtMethodArgClass(), ArgType {
            override val entries = ByAngleEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.BITMAP.key,
                ByAngleEnumArgs.BITMAP.defaultValueStr
            )
            val colorListKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.COLOR_LIST.key,
                ByAngleEnumArgs.COLOR_LIST.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.TIMES.key,
                ByAngleEnumArgs.TIMES.defaultValueStr
            )
            val minOpacityRateKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MIN_OPACITY_RATE.key,
                ByAngleEnumArgs.MIN_OPACITY_RATE.defaultValueStr
            )
            val maxOpacityRateKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MAX_OPACITY_RATE.key,
                ByAngleEnumArgs.MAX_OPACITY_RATE.defaultValueStr
            )
            val minAngleKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MIN_ANGLE.key,
                ByAngleEnumArgs.MIN_ANGLE.defaultValueStr
            )
            val maxAngleKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MAX_ANGLE.key,
                ByAngleEnumArgs.MAX_ANGLE.defaultValueStr
            )
            val lengthKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.LENGTH.key,
                ByAngleEnumArgs.LENGTH.defaultValueStr
            )
            val minLengthShrinkRateKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MIN_LENGTH_SHRINK_RATE.key,
                ByAngleEnumArgs.MIN_LENGTH_SHRINK_RATE.defaultValueStr
            )
            val maxLengthShrinkRateKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MAX_LENGTH_SHRINK_RATE.key,
                ByAngleEnumArgs.MAX_LENGTH_SHRINK_RATE.defaultValueStr
            )
            val minStrokeWidthKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MIN_STROKE_WIDTH.key,
                ByAngleEnumArgs.MIN_STROKE_WIDTH.defaultValueStr
            )
            val maxStrokeWidthKeyToDefaultValueStr = Pair(
                ByAngleEnumArgs.MAX_STROKE_WIDTH.key,
                ByAngleEnumArgs.MAX_STROKE_WIDTH.defaultValueStr
            )

            enum class ByAngleEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                COLOR_LIST("colorList", CmdClickColor.BLACK.str, FuncCheckerForSetting.ArgType.STRING),
                TIMES("times", 1.toString(), FuncCheckerForSetting.ArgType.INT),
                MIN_OPACITY_RATE(
                    "minOpacityRate",
                    (0.9).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_OPACITY_RATE(
                    "maxOpacityRate",
                    (1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MIN_ANGLE(
                    "minAngle",
                    0.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_ANGLE(
                    "maxAngle",
                    360.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                LENGTH(
                    "length",
                    (100).toString(),
                    FuncCheckerForSetting.ArgType.INT
                ),
                MIN_LENGTH_SHRINK_RATE(
                    "minLengthShrinkRate",
                    (0.9).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_LENGTH_SHRINK_RATE(
                    "maxLengthShrinkRate",
                    1.toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MIN_STROKE_WIDTH(
                    "minStrokeWidth",
                    (0.9).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT
                ),
                MAX_STROKE_WIDTH(
                    "maxStrokeWidth",
                    (1).toString(),
                    FuncCheckerForSetting.ArgType.FLOAT,
                )
            }
        }
    }
}
