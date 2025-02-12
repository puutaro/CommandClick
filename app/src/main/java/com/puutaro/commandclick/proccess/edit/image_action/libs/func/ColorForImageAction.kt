package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ColorTool
import java.io.File
import kotlin.enums.EnumEntries

object ColorForImageAction {
    suspend fun handle(
        fragment: Fragment,
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
        val context =
            fragment.context
                ?: return null
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
            return null to
                    FuncCheckerForSetting.FuncCheckErr(
                        "Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                    )
        }
        val args =
            methodNameClass.args
        return when(args) {
            is ColorMethodArgClass.BlackToArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bitmapKeyToIndex,
                    varNameToBitmapMap,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val toColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.toKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.toKeyToIndex.first,
                        where,
                    )
                }
                val returnBitmap = ColorManager.blackTo(
                    bitmap,
                    toColorStr,
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
                    null
                ) to null
            }
            is ColorMethodArgClass.ColorToArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bitmapKeyToIndex,
                    varNameToBitmapMap,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val toColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.toKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.toKeyToIndex.first,
                        where,
                    )
                }
                val fromColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.fromKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.fromKeyToIndex.first,
                        where,
                    )
                }
                val returnBitmap = ColorManager.colorTo(
                    bitmap,
                    fromColorStr,
                    toColorStr,
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
                    null
                ) to null
            }
            is ColorMethodArgClass.AllToInTarns -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bitmapKeyToIndex,
                    varNameToBitmapMap,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val toColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.toKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.toKeyToIndex.first,
                        where,
                    )
                }
                val returnBitmap = ColorManager.allToColorInTrans(
                    bitmap,
                    toColorStr,
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
                    null
                ) to null
            }
            is ColorMethodArgClass.SwapBlackAndTransArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bitmapKeyToIndex,
                    varNameToBitmapMap,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val returnBitmap = ColorManager.swapTransAndBlack(
                    bitmap,
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
                    null
                ) to null
            }
            is ColorMethodArgClass.SwapArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bitmapKeyToIndex,
                    varNameToBitmapMap,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val color1Str = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.color1KeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.color1KeyToIndex.first,
                        where,
                    )
                }
                val color2Str = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.color2KeyToIndex,
                    where
                ).let { colorToErr ->
                    val funcErr = colorToErr.second
                        ?: return@let colorToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.color2KeyToIndex.first,
                        where,
                    )
                }
                val returnBitmap = ColorManager.colorTo(
                    bitmap,
                    color1Str,
                    color2Str,
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
                    null
                ) to null
            }
        }
    }

    private object ColorManager {
        suspend fun blackTo(
            bitmap: Bitmap,
            toColorStr: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val toBitmap = BitmapTool.ImageTransformer.convertBlackToColor(
                    bitmap,
                    toColorStr,
                )
                Pair(
                    toBitmap,
                    null
                )
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }

        suspend fun colorTo(
            bitmap: Bitmap,
            fromColorStr: String,
            toColorStr: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val toBitmap = BitmapTool.ImageTransformer.convertColorTo(
                    bitmap,
                    toColorStr,
                    fromColorStr,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lcolor.txt").absolutePath,
//                    listOf(
//                        "toColorStr: ${toColorStr}",
//                        "fromColorStr: ${fromColorStr}",
//                    ).joinToString("\n")
//                )
                Pair(
                    toBitmap,
                    null
                )
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }

        suspend fun allToColorInTrans(
            bitmap: Bitmap,
            toColorStr: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val toBitmap = BitmapTool.ImageTransformer.convertAllToColorInTrans(
                    bitmap,
                    toColorStr,
                )
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lcolor.txt").absolutePath,
//                        listOf(
//                            "toColorStr: ${toColorStr}",
//                            "fromColorStr: ${fromColorStr}",
//                        ).joinToString("\n")
//                    )
                Pair(
                    toBitmap,
                    null
                )
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }

        suspend fun swapTransAndBlack(
            bitmap: Bitmap,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val exchangeBitmap = BitmapTool.ImageTransformer.swapTransparentAndBlack(
                    bitmap,
                )
                Pair(
                    exchangeBitmap,
                    null
                )
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
        fun swap(
            bitmap: Bitmap,
            color1Str: String,
            color2Str: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val exchangeBitmap = BitmapTool.ImageTransformer.swap(
                    bitmap,
                    color1Str,
                    color2Str,
                )
                Pair(
                    exchangeBitmap,
                    null
                )
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
        val args: ColorMethodArgClass,
    ){
        BLACK_TO("blackTo", ColorMethodArgClass.BlackToArgs),
        COLOR_TO("colorTo", ColorMethodArgClass.ColorToArgs),
        ALL_TO("allToInTrans", ColorMethodArgClass.AllToInTarns),
        SWAP_TRANS_AND_BLACK("swapTransAndBlack", ColorMethodArgClass.SwapBlackAndTransArgs),
        SWAP("swap", ColorMethodArgClass.SwapArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class ColorMethodArgClass {
        data object BlackToArgs : ColorMethodArgClass(), ArgType {
            override val entries = BlackToArgs.entries
            val bitmapKeyToIndex = Pair(
                BlackToArgs.BITMAP.key,
                BlackToArgs.BITMAP.index
            )
            val toKeyToIndex = Pair(
                BlackToArgs.TO.key,
                BlackToArgs.TO.index
            )
            enum class BlackToArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                TO("to", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object ColorToArgs : ColorMethodArgClass(), ArgType {
            override val entries = ColorToArgs.entries
            val bitmapKeyToIndex = Pair(
                ColorToArgs.BITMAP.key,
                ColorToArgs.BITMAP.index
            )
            val toKeyToIndex = Pair(
                ColorToArgs.TO.key,
                ColorToArgs.TO.index
            )
            val fromKeyToIndex = Pair(
                ColorToArgs.FROM.key,
                ColorToArgs.FROM.index
            )
            enum class ColorToArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                FROM("from", 1, FuncCheckerForSetting.ArgType.STRING),
                TO("to", 2, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object AllToInTarns : ColorMethodArgClass(), ArgType {
            override val entries = AllToInTransArgs.entries
            val bitmapKeyToIndex = Pair(
                AllToInTransArgs.BITMAP.key,
                AllToInTransArgs.BITMAP.index
            )
            val toKeyToIndex = Pair(
                AllToInTransArgs.TO.key,
                AllToInTransArgs.TO.index
            )
            enum class AllToInTransArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                TO("to", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object SwapBlackAndTransArgs : ColorMethodArgClass(), ArgType {
            override val entries = SwapTransAndBlackArgs.entries
            val bitmapKeyToIndex = Pair(
                SwapTransAndBlackArgs.BITMAP.key,
                SwapTransAndBlackArgs.BITMAP.index
            )
            enum class SwapTransAndBlackArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
        data object SwapArgs : ColorMethodArgClass(), ArgType {
            override val entries = SwapArgs.entries
            val bitmapKeyToIndex = Pair(
                SwapArgs.BITMAP.key,
                SwapArgs.BITMAP.index
            )
            val color1KeyToIndex = Pair(
                SwapArgs.COLOR1.key,
                SwapArgs.COLOR1.index
            )
            val color2KeyToIndex = Pair(
                SwapArgs.COLOR2.key,
                SwapArgs.COLOR2.index
            )
            enum class SwapArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                COLOR1("color1", 1, FuncCheckerForSetting.ArgType.STRING),
                COLOR2("color2", 2, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}
