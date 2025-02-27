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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                val xMulti = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.xMultiKeyToDefaultValueStr,
                    where
                ).let { xMultiToErr ->
                    val funcErr = xMultiToErr.second
                        ?: return@let xMultiToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                val passionColorList = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.passionColorListKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                val passionInt = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.passionRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
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

        fun execScaleBitmap(
            bitmap: Bitmap,
            width: Int,
            height: Int,
        ): Bitmap {
            val returnBitmap =
                Bitmap.createScaledBitmap(
                    bitmap,
                    width,
                    height,
                    true,
                )
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
    }

    private enum class MethodNameClass(
        val str: String,
        val args: BitmapArtMethodArgClass,
    ){
        MATRIX_STORM("matrixStorm", BitmapArtMethodArgClass.MatrixStormArgs),
        RECT_PUZZLE("rectPuzzle", BitmapArtMethodArgClass.RectPuzzleArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class BitmapArtMethodArgClass {
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
    }

    private object BitmapPieceManager {

        private const val pieceOneSide = 100
        private const val keySeparator = '|'

        enum class PieceKey(val key: String) {
//            ONE_SIDE("oneSide"),
//            WIDTH("width"),
//            HEIGHT("height"),
//            MACRO("macro"),
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

//        fun getOneSide(pieceMap: Map<String, String>): Int {
//            return try {
//                pieceMap.get(
//                    PieceKey.ONE_SIDE.key
//                )?.toInt()
//            } catch (e: Exception){
//                null
//            } ?: pieceOneSide
//        }

        fun getRotate(pieceMap: Map<String, String>): Float {
            return try {
                pieceMap.get(
                    PieceKey.ROTATE.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: 0f
        }

//        fun getWidth(pieceMap: Map<String, String>): Int? {
//            return try {
//                pieceMap.get(
//                    PieceKey.WIDTH.key
//                )?.toInt()
//            } catch (e: Exception){
//                null
//            }
//        }
//
//        fun getHeight(pieceMap: Map<String, String>): Int? {
//            return try {
//                pieceMap.get(
//                    PieceKey.HEIGHT.key
//                )?.toInt()
//            } catch (e: Exception){
//                null
//            }
//        }


//        fun getMacro(pieceMap: Map<String, String>): String? {
//            val macroStr = pieceMap.get(
//                PieceKey.MACRO.key
//            )
//            return macroStr
//        }
    }
}