package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object MonoArtForImageAction {

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
            is MonoArtMethodArgClass.RndRectArgs -> {
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val borderRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.borderRateKeyToDefaultValueStr,
                    where
                ).let { borderRateToErr ->
                    val funcErr = borderRateToErr.second
                        ?: return@let borderRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val minWidthRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minWidthRateKeyToDefaultValueStr,
                    where
                ).let { widthRateToErr ->
                    val funcErr = widthRateToErr.second
                        ?: return@let widthRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val maxWidthRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxWidthRateKeyToDefaultValueStr,
                    where
                ).let { widthRateToErr ->
                    val funcErr = widthRateToErr.second
                        ?: return@let widthRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(minWidthRate > maxWidthRate) {
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minWidthRate.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxWidthRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minWidthRate(${spanMinWidthRate}) <= maxWidthRate(${spanMaxWidthRate}): ${spanWhere}"
                    )
                }
                val minHeightRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minHeightRateKeyToDefaultValueStr,
                    where
                ).let { heightRateToErr ->
                    val funcErr = heightRateToErr.second
                        ?: return@let heightRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val maxHeightRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxHeightRateKeyToDefaultValueStr,
                    where
                ).let { heightRateToErr ->
                    val funcErr = heightRateToErr.second
                        ?: return@let heightRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(minHeightRate > maxHeightRate) {
                    val spanMinHeightRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minHeightRate.toString()
                    )
                    val spanMaxHeightRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxHeightRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minWidthRate(${spanMinHeightRate}) <= maxWidthRate(${spanMaxHeightRate}): ${spanWhere}"
                    )
                }
                val bitmapArt = MonoArt.rndRect(
                    width,
                    height,
                    times,
                    borderRate,
                    minWidthRate,
                    maxWidthRate,
                    minHeightRate,
                    maxHeightRate,
                    where,
                ).let {
                        (bitmapArtSrc, err) ->
                    if(
                        err == null
                    ) return@let bitmapArtSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    bitmapArt,
                    null
                ) to null
            }
            is MonoArtMethodArgClass.RndRectByGrayArgs -> {
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
                ).let { iconMacroStrToErr ->
                    val funcErr = iconMacroStrToErr.second
                        ?: return@let iconMacroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val borderRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.borderRateKeyToDefaultValueStr,
                    where
                ).let { borderRateToErr ->
                    val funcErr = borderRateToErr.second
                        ?: return@let borderRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val minWidthRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minWidthRateKeyToDefaultValueStr,
                    where
                ).let { widthRateToErr ->
                    val funcErr = widthRateToErr.second
                        ?: return@let widthRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val maxWidthRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxWidthRateKeyToDefaultValueStr,
                    where
                ).let { widthRateToErr ->
                    val funcErr = widthRateToErr.second
                        ?: return@let widthRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(minWidthRate > maxWidthRate) {
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minWidthRate.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxWidthRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minWidthRate(${spanMinWidthRate}) <= maxWidthRate(${spanMaxWidthRate}): ${spanWhere}"
                    )
                }
                val minHeightRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minHeightRateKeyToDefaultValueStr,
                    where
                ).let { heightRateToErr ->
                    val funcErr = heightRateToErr.second
                        ?: return@let heightRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val maxHeightRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxHeightRateKeyToDefaultValueStr,
                    where
                ).let { heightRateToErr ->
                    val funcErr = heightRateToErr.second
                        ?: return@let heightRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(minHeightRate > maxHeightRate) {
                    val spanMinHeightRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minHeightRate.toString()
                    )
                    val spanMaxHeightRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxHeightRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minWidthRate(${spanMinHeightRate}) <= maxWidthRate(${spanMaxHeightRate}): ${spanWhere}"
                    )
                }
                val bitmapArt = MonoArt.rndRectByGray(
                    bitmap,
                    times,
                    borderRate,
                    minWidthRate,
                    maxWidthRate,
                    minHeightRate,
                    maxHeightRate,
                    where,
                ).let {
                        (bitmapArtSrc, err) ->
                    if(
                        err == null
                    ) return@let bitmapArtSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    bitmapArt,
                    null
                ) to null
            }
            is MonoArtMethodArgClass.RndColorRectArgs -> {
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
                ).let { iconMacroStrToErr ->
                    val funcErr = iconMacroStrToErr.second
                        ?: return@let iconMacroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val borderRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.borderRateKeyToDefaultValueStr,
                    where
                ).let { borderRateToErr ->
                    val funcErr = borderRateToErr.second
                        ?: return@let borderRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val minWidthRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minWidthRateKeyToDefaultValueStr,
                    where
                ).let { widthRateToErr ->
                    val funcErr = widthRateToErr.second
                        ?: return@let widthRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val maxWidthRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxWidthRateKeyToDefaultValueStr,
                    where
                ).let { widthRateToErr ->
                    val funcErr = widthRateToErr.second
                        ?: return@let widthRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(minWidthRate > maxWidthRate) {
                    val spanMinWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minWidthRate.toString()
                    )
                    val spanMaxWidthRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxWidthRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minWidthRate(${spanMinWidthRate}) <= maxWidthRate(${spanMaxWidthRate}): ${spanWhere}"
                    )
                }
                val minHeightRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.minHeightRateKeyToDefaultValueStr,
                    where
                ).let { heightRateToErr ->
                    val funcErr = heightRateToErr.second
                        ?: return@let heightRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val maxHeightRate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.maxHeightRateKeyToDefaultValueStr,
                    where
                ).let { heightRateToErr ->
                    val funcErr = heightRateToErr.second
                        ?: return@let heightRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(minHeightRate > maxHeightRate) {
                    val spanMinHeightRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        minHeightRate.toString()
                    )
                    val spanMaxHeightRate = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        maxHeightRate.toString()
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Must be minWidthRate(${spanMinHeightRate}) <= maxWidthRate(${spanMaxHeightRate}): ${spanWhere}"
                    )
                }
                val bitmapArt = MonoArt.rndColorRect(
                    bitmap,
                    times,
                    borderRate,
                    minWidthRate,
                    maxWidthRate,
                    minHeightRate,
                    maxHeightRate,
                    where,
                ).let {
                        (bitmapArtSrc, err) ->
                    if(
                        err == null
                    ) return@let bitmapArtSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    bitmapArt,
                    null
                ) to null
            }
        }
    }


    private object MonoArt {
        suspend fun rndRectByGray(
            baseBitmap: Bitmap,
            times: Int,
            borderRate: Float,
            minWidthRate: Float,
            maxWidthRate: Float,
            minHeightRate: Float,
            maxHeightRate: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                execRndRectByGray(
                    baseBitmap,
                    times,
                    borderRate,
                    minWidthRate,
                    maxWidthRate,
                    minHeightRate,
                    maxHeightRate,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        private suspend fun execRndRectByGray(
            baseBitmap: Bitmap,
            times: Int,
            borderRate: Float,
            minWidthRate: Float,
            maxWidthRate: Float,
            minHeightRate: Float,
            maxHeightRate: Float,
        ): Bitmap {
            val baseWidth = baseBitmap.width
//            val halfBaseWidth = baseWidth / 2
            val baseHeight = baseBitmap.height
//            val halfBaseHeight = baseHeight / 2
            return withContext(Dispatchers.IO) {
                val bitmapToOffsetPairListJob = (0..times).map {
                    async {
                        val cutWidth =
                            ((baseWidth * minWidthRate).toInt()
                                    ..
                                    (baseWidth * maxWidthRate).toInt()
                                    ).random()
                        val cutHeight =
                            ((baseHeight * minHeightRate).toInt()
                                    ..
                                    (baseHeight * maxHeightRate).toInt()
                                    ).random()
                        val offsetX = (0..(baseWidth - cutWidth)).random()
                        val offsetY = (0..(baseHeight - cutHeight)).random()
                        BitmapTool.ImageTransformer.cutByTarget(
                            baseBitmap,
                            cutWidth,
                            cutHeight,
                            offsetX,
                            offsetY,
                        ).let {
                            val bkRect = BitmapTool.ImageTransformer.makeRect(
                                "#000000",
                                it.width,
                                it.height
                            )
                            val shrinkBitmap = Bitmap.createScaledBitmap(
                                it,
                                (it.width * borderRate).toInt().let { if(it <= 0) 1 else it },
                                (it.height * borderRate).toInt().let { if(it <= 0) 1 else it },
                                true,
                            )
                            val grayBitmap = BitmapTool.ImageTransformer.convertGrayScaleBitmap(
                                shrinkBitmap
                            )
                            val invertedBitmap = BitmapTool.ImageTransformer.invertMonoBitmap(
                                grayBitmap
                            )
                            val concatInvertedBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapCenter(
                                bkRect,
                                invertedBitmap,
                            )
                            Pair(
                                concatInvertedBitmap,
                                offsetX to offsetY
                            )
                        }
                    }
                }
                val bitmapToOffsetPairList = bitmapToOffsetPairListJob.awaitAll()
                var resultBitmap = baseBitmap
                bitmapToOffsetPairList.forEach {
                        (cutBitmap, offsetPair) ->
                    resultBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapByPivot(
                        resultBitmap,
                        cutBitmap,
                        offsetPair.first.toFloat(),
                        offsetPair.second.toFloat(),
                    )
                }
                resultBitmap
            }

        }
        suspend fun rndRect(
            baseWidth: Int,
            baseHeight: Int,
//            baseBitmap: Bitmap,
            times: Int,
            borderRate: Float,
            minWidthRate: Float,
            maxWidthRate: Float,
            minHeightRate: Float,
            maxHeightRate: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                execRndRect(
                    baseWidth,
                    baseHeight,
//                    baseBitmap,
                    times,
                    borderRate,
                    minWidthRate,
                    maxWidthRate,
                    minHeightRate,
                    maxHeightRate,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        private suspend fun execRndRect(
            baseWidth: Int,
            baseHeight: Int,
//            baseBitmap: Bitmap,
            times: Int,
            borderRate: Float,
            minWidthRate: Float,
            maxWidthRate: Float,
            minHeightRate: Float,
            maxHeightRate: Float,
        ): Bitmap {
            val baseBitmap =
                BitmapTool.ImageTransformer.makeRect(
                "#ffffff",
                baseWidth,
                baseHeight,
            )
//            val baseWidth = baseBitmap.width
////            val halfBaseWidth = baseWidth / 2
//            val baseHeight = baseBitmap.height
//            val halfBaseHeight = baseHeight / 2
            return withContext(Dispatchers.IO) {
                val bitmapToOffsetPairListJob = (0..times).map {
                    async {
                        val cutWidth =
                            ((baseWidth * minWidthRate).toInt()
                                    ..
                                    (baseWidth * maxWidthRate).toInt()
                                    ).random()
                        val cutHeight =
                            ((baseHeight * minHeightRate).toInt()
                                    ..
                                    (baseHeight * maxHeightRate).toInt()
                                    ).random()
                        val offsetX = (0..(baseWidth - cutWidth)).random()
                        val offsetY = (0..(baseHeight - cutHeight)).random()
                        BitmapTool.ImageTransformer.cutByTarget(
                            baseBitmap,
                            cutWidth,
                            cutHeight,
                            offsetX,
                            offsetY,
                        ).let {
                            baseRect ->
                            val bkRect = BitmapTool.ImageTransformer.makeRect(
                                "#000000",
                                baseRect.width,
                                baseRect.height
                            )
                            val shrinkBitmap = Bitmap.createScaledBitmap(
                                baseRect,
                                (baseRect.width * borderRate).toInt().let { if(it <= 0) 1 else it },
                                (baseRect.height * borderRate).toInt().let { if(it <= 0) 1 else it },
                                true,
                            )
//                            val grayBitmap = BitmapTool.ImageTransformer.convertGrayScaleBitmap(
//                                shrinkBitmap
//                            )
//                            val invertedBitmap = BitmapTool.ImageTransformer.invertMonoBitmap(
//                                grayBitmap
//                            )
                            val concatInvertedBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapCenter(
                                bkRect,
                                shrinkBitmap,
                            )
                            Pair(
                                concatInvertedBitmap,
                                offsetX to offsetY
                            )
                        }
                    }
                }
                val bitmapToOffsetPairList = bitmapToOffsetPairListJob.awaitAll()
                var resultBitmap = baseBitmap
                bitmapToOffsetPairList.forEach {
                    (cutBitmap, offsetPair) ->
                    resultBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapByPivot(
                        resultBitmap,
                        cutBitmap,
                        offsetPair.first.toFloat(),
                        offsetPair.second.toFloat(),
                    )
                }
                resultBitmap
            }

        }
        suspend fun rndColorRect(
            baseBitmap: Bitmap,
            times: Int,
            borderRate: Float,
            minWidthRate: Float,
            maxWidthRate: Float,
            minHeightRate: Float,
            maxHeightRate: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                execRndColorRect(
                    baseBitmap,
                    times,
                    borderRate,
                    minWidthRate,
                    maxWidthRate,
                    minHeightRate,
                    maxHeightRate,
                ) to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        private suspend fun execRndColorRect(
            baseBitmap: Bitmap,
            times: Int,
            borderRate: Float,
            minWidthRate: Float,
            maxWidthRate: Float,
            minHeightRate: Float,
            maxHeightRate: Float,
        ): Bitmap {
            val baseWidth = baseBitmap.width
//            val halfBaseWidth = baseWidth / 2
            val baseHeight = baseBitmap.height
//            val halfBaseHeight = baseHeight / 2
            val grayBaseBitmap = BitmapTool.ImageTransformer.convertGrayScaleBitmap(
                baseBitmap
            )
            return withContext(Dispatchers.IO) {
                val bitmapToOffsetPairListJob = (0..times).map {
                    async {
                        val cutWidth =
                            ((baseWidth * minWidthRate).toInt()
                                    ..
                                    (baseWidth * maxWidthRate).toInt()
                                    ).random()
                        val cutHeight =
                            ((baseHeight * minHeightRate).toInt()
                                    ..
                                    (baseHeight * maxHeightRate).toInt()
                                    ).random()
                        val offsetX = (0..(baseWidth - cutWidth)).random()
                        val offsetY = (0..(baseHeight - cutHeight)).random()
                        BitmapTool.ImageTransformer.cutByTarget(
                            baseBitmap,
                            cutWidth,
                            cutHeight,
                            offsetX,
                            offsetY,
                        ).let {
                            val bkRect = BitmapTool.ImageTransformer.makeRect(
                                "#000000",
                                it.width,
                                it.height
                            )

                            val shrinkBitmap = BitmapTool.ImageTransformer.cutCenter2(
                                it,
                                (it.width * borderRate).toInt().let { if(it <= 0) 1 else it },
                                (it.height * borderRate).toInt().let { if(it <= 0) 1 else it },
                            )
//                                Bitmap.createScaledBitmap(
//                                it,
//                                (it.width * borderRate).toInt().let { if(it <= 0) 1 else it },
//                                (it.height * borderRate).toInt().let { if(it <= 0) 1 else it },
//                                true,
//                            )
//                            val grayBitmap = BitmapTool.ImageTransformer.convertGrayScaleBitmap(
//                                shrinkBitmap
//                            )
//                            val invertedBitmap = BitmapTool.ImageTransformer.invertMonoBitmap(
//                                grayBitmap
//                            )
                            val concatInvertedBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapCenter(
                                bkRect,
                                shrinkBitmap,
                            )
                            Pair(
                                concatInvertedBitmap,
                                offsetX to offsetY
                            )
                        }
                    }
                }
                val bitmapToOffsetPairList = bitmapToOffsetPairListJob.awaitAll()
                var resultBitmap = grayBaseBitmap
                bitmapToOffsetPairList.forEach {
                        (cutBitmap, offsetPair) ->
                    resultBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapByPivot(
                        resultBitmap,
                        cutBitmap,
                        offsetPair.first.toFloat(),
                        offsetPair.second.toFloat(),
                    )
                }
                resultBitmap
            }

        }
    }
    private enum class MethodNameClass(
        val str: String,
        val args: MonoArtMethodArgClass
    ){
        RND_RECT_BY_RECT("rndRectByRect", MonoArtMethodArgClass.RndRectByGrayArgs),
        RND_RECT("rndRect", MonoArtMethodArgClass.RndRectArgs),
        RND_COLOR_RECT("rndColorRect", MonoArtMethodArgClass.RndColorRectArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class MonoArtMethodArgClass {
        data object RndRectArgs : MonoArtMethodArgClass(), ArgType {
            override val entries = RndRectEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.WIDTH.key,
                RndRectEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.HEIGHT.key,
                RndRectEnumArgs.HEIGHT.defaultValueStr
            )
//            val shapeKeyToDefaultValueStr = Pair(
//                RndRectEnumArgs.SHAPE.key,
//                RndRectEnumArgs.SHAPE.defaultValueStr
//            )
            val timesKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.TIMES.key,
                RndRectEnumArgs.TIMES.defaultValueStr
            )
            val borderRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.BORDER_RATE.key,
                RndRectEnumArgs.BORDER_RATE.defaultValueStr
            )
            val minWidthRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MIN_WIDTH_RATE.key,
                RndRectEnumArgs.MIN_WIDTH_RATE.defaultValueStr
            )
            val maxWidthRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MAX_WIDTH_RATE.key,
                RndRectEnumArgs.MAX_WIDTH_RATE.defaultValueStr
            )
            val minHeightRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MIN_HEIGHT_RATE.key,
                RndRectEnumArgs.MIN_HEIGHT_RATE.defaultValueStr
            )
            val maxHeightRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MAX_HEIGHT_RATE.key,
                RndRectEnumArgs.MAX_HEIGHT_RATE.defaultValueStr
            )


            enum class RndRectEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
//                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
//                SHAPE("shape", CmdClickIcons.RECT.str, FuncCheckerForSetting.ArgType.STRING),
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                TIMES("times", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                BORDER_RATE("borderRate", 0.9.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_WIDTH_RATE("minWidthRate", (0.1).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_WIDTH_RATE("maxWidthRate", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_HEIGHT_RATE("minHeightRate", (0.1).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_HEIGHT_RATE("maxHeightRate", (0.5).toString(), FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object RndRectByGrayArgs : MonoArtMethodArgClass(), ArgType {
            override val entries = RndRectEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.BITMAP.key,
                RndRectEnumArgs.BITMAP.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.TIMES.key,
                RndRectEnumArgs.TIMES.defaultValueStr
            )
            val borderRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.BORDER_RATE.key,
                RndRectEnumArgs.BORDER_RATE.defaultValueStr
            )
            val minWidthRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MIN_WIDTH_RATE.key,
                RndRectEnumArgs.MIN_WIDTH_RATE.defaultValueStr
            )
            val maxWidthRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MAX_WIDTH_RATE.key,
                RndRectEnumArgs.MAX_WIDTH_RATE.defaultValueStr
            )
            val minHeightRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MIN_HEIGHT_RATE.key,
                RndRectEnumArgs.MIN_HEIGHT_RATE.defaultValueStr
            )
            val maxHeightRateKeyToDefaultValueStr = Pair(
                RndRectEnumArgs.MAX_HEIGHT_RATE.key,
                RndRectEnumArgs.MAX_HEIGHT_RATE.defaultValueStr
            )


            enum class RndRectEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                TIMES("times", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                BORDER_RATE("borderRate", 0.9.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_WIDTH_RATE("minWidthRate", (0.1).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_WIDTH_RATE("maxWidthRate", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_HEIGHT_RATE("minHeightRate", (0.1).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_HEIGHT_RATE("maxHeightRate", (0.5).toString(), FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object RndColorRectArgs : MonoArtMethodArgClass(), ArgType {
            override val entries = RndColorEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                RndColorEnumArgs.BITMAP.key,
                RndColorEnumArgs.BITMAP.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                RndColorEnumArgs.TIMES.key,
                RndColorEnumArgs.TIMES.defaultValueStr
            )
            val borderRateKeyToDefaultValueStr = Pair(
                RndColorEnumArgs.BORDER_RATE.key,
                RndColorEnumArgs.BORDER_RATE.defaultValueStr
            )
            val minWidthRateKeyToDefaultValueStr = Pair(
                RndColorEnumArgs.MIN_WIDTH_RATE.key,
                RndColorEnumArgs.MIN_WIDTH_RATE.defaultValueStr
            )
            val maxWidthRateKeyToDefaultValueStr = Pair(
                RndColorEnumArgs.MAX_WIDTH_RATE.key,
                RndColorEnumArgs.MAX_WIDTH_RATE.defaultValueStr
            )
            val minHeightRateKeyToDefaultValueStr = Pair(
                RndColorEnumArgs.MIN_HEIGHT_RATE.key,
                RndColorEnumArgs.MIN_HEIGHT_RATE.defaultValueStr
            )
            val maxHeightRateKeyToDefaultValueStr = Pair(
                RndColorEnumArgs.MAX_HEIGHT_RATE.key,
                RndColorEnumArgs.MAX_HEIGHT_RATE.defaultValueStr
            )


            enum class RndColorEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                TIMES("times", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                BORDER_RATE("borderRate", 0.9.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_WIDTH_RATE("minWidthRate", (0.1).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_WIDTH_RATE("maxWidthRate", 1.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MIN_HEIGHT_RATE("minHeightRate", (0.1).toString(), FuncCheckerForSetting.ArgType.FLOAT),
                MAX_HEIGHT_RATE("maxHeightRate", (0.5).toString(), FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
    }
}
