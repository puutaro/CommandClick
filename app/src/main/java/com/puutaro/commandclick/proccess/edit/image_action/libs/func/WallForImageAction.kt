package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment_lib.command_index_fragment.UrlImageDownloader
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ImageFile
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.CcDotArt
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.enums.EnumEntries
import androidx.core.graphics.scale

object WallForImageAction {
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
            is WallMethodArgClass.MakeArgs -> {
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
                val macroStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.macroKeyToIndex,
                    where
                ).let { macroStrToErr ->
                    val funcErr = macroStrToErr.second
                        ?: return@let macroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
//                val cmdClickBkImageFilePath = BkWallPath.get(
//                    macroStr
//                ) ?: return null
//                BitmapTool.convertFileToBitmap(cmdClickBkImageFilePath)?.let {
//                    BitmapTool.ImageTransformer.cutCenter(
//                        it,
//                        400,
//                        800
//                    )
//                }
                val returnBitmap = BkWallPath.makeWallBitmap(
                    macroStr,
                    400,
                    800,
                )
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is WallMethodArgClass.MatrixStormArgs -> {
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
                ).let { widthStrToErr ->
                    val funcErr = widthStrToErr.second
                        ?: return@let widthStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val height = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
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
                val xMulti = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
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
                val yMulti = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
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
                val pieceMapArray =
                    WallPieceManager.makePieceMapArray(argsPairList)
                val xDup = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
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
                val yDup = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
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
                val returnBitmap = BkWallPath.makeStorm(
                    pieceMapArray,
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
        }
    }

    private object BkWallPath {

        fun makeWallBitmap(
            macroStr: String,
            width: Int,
            height: Int,
        ): Bitmap? {
            val cmdClickBkImageFilePath = get(
                macroStr
            ) ?: return null
            val returnBitmap = BitmapTool.convertFileToBitmap(cmdClickBkImageFilePath)?.let {
                it.scale(width, height)
//                BitmapTool.ImageTransformer.cutCenter(
//                    it,
//                    width,
//                    height
//                )
            }
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

        suspend fun makeStorm(
            pieceMapArray: Array<Map<String, String>>,
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
                val pieceBitmapArray = withContext(Dispatchers.IO) {
                    val pieceBitmapArrayJob = pieceMapArray.mapIndexed { index, pieceMap ->
                        async {
                            val macroStr = WallPieceManager.getMacro(
                                pieceMap
                            ) ?: return@async index to null
                            val pieceRotate = WallPieceManager.getRotate(
                                pieceMap,
                            )
                            index to makeWallBitmap(
                                macroStr,
                                pieceWidth,
                                pieceHeight,
                            )?.let { piece ->
                                if (
                                    pieceRotate == 0f
                                ) return@let piece
                                BitmapTool.rotate(
                                    piece,
                                    pieceRotate,
                                )
                            }
                        }
                    }
                    pieceBitmapArrayJob.awaitAll()
                        .asSequence()
                        .sortedBy { it.first }
                        .filter { it.second != null }
                        .map { it.second }
                        .toList()
                }.toTypedArray()
                if(
                    pieceBitmapArray.isEmpty()
                ) return null to null
                CcDotArt.makeMatrixStorm(
                    pieceBitmapArray as Array<Bitmap>,
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
    }

    private enum class MethodNameClass(
        val str: String,
        val args: WallMethodArgClass,
    ){
        MAKE("make", WallMethodArgClass.MakeArgs),
        MATRIX_STORM("matrixStorm", WallMethodArgClass.MatrixStormArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class WallMethodArgClass {
        data object MakeArgs : WallMethodArgClass(), ArgType {
            override val entries = MakeEnumArgs.entries
            val macroKeyToIndex = Pair(
                MakeEnumArgs.MACRO.key,
                MakeEnumArgs.MACRO.defaultValueStr
            )
            enum class MakeEnumArgs(
                val key: String,
                val defaultValueStr: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                MACRO("macro", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object MatrixStormArgs : WallMethodArgClass(), ArgType {
            override val entries = MatrixStormEnumArgs.entries
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

    object WallPieceManager {

        private const val pieceOneSide = 100
        private const val keySeparator = '|'

        enum class PieceKey(val key: String) {
//            ONE_SIDE("oneSide"),
//            WIDTH("width"),
//            HEIGHT("height"),
            MACRO("macro"),
            ROTATE("rotate"),
        }

        fun makePieceMapArray(
            argsPairList: List<Pair<String, String>>,
        ): Array<Map<String, String>> {
            val pieceKey = WallMethodArgClass.MatrixStormArgs.MatrixStormEnumArgs.PIECE.key
            return argsPairList.asSequence().filter {
                    (argKey, _) ->
                argKey == pieceKey
            }.map {
                    (_, pieceMapCon) ->
                CmdClickMap.createMap(
                    pieceMapCon,
                    keySeparator
                ).toMap()
            }.toList().toTypedArray()
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


        fun getMacro(pieceMap: Map<String, String>): String? {
            val macroStr = pieceMap.get(
                PieceKey.MACRO.key
            )
            return macroStr
        }
    }
}