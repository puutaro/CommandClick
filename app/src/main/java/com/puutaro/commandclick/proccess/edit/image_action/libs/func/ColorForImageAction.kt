package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ColorTool
import kotlin.enums.EnumEntries

object ColorForImageAction {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null
                ) to null
            }
            is ColorMethodArgClass.BlackToByTolArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                val tol = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByIndex(
                    mapArgMapList,
                    args.tolKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = ColorManager.blackToByTol(
                    bitmap,
                    toColorStr,
                    tol,
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
                    null
                ) to null
            }
            is ColorMethodArgClass.OtherToArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val saveColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.saveKeyToIndex,
                    where
                ).let { colorToErr ->
                    val funcErr = colorToErr.second
                        ?: return@let colorToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.saveKeyToIndex.first,
                        where,
                    )
                }
                val toColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.toKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                val returnBitmap = ColorManager.otherTo(
                    bitmap,
                    saveColorStr,
                    toColorStr,
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null
                ) to null
            }
            is ColorMethodArgClass.ColorToByTolArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                val tol = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByIndex(
                    mapArgMapList,
                    args.tolKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = ColorManager.colorToByTol(
                    bitmap,
                    fromColorStr,
                    toColorStr,
                    tol,
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null
                ) to null
            }
            is ColorMethodArgClass.SwapTransAndAllArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val colorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.colorKeyToIndex,
                    where
                ).let { colorToErr ->
                    val funcErr = colorToErr.second
                        ?: return@let colorToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.colorKeyToIndex.first,
                        where,
                    )
                }
                val returnBitmap = ColorManager.swapTransAndAll(
                    bitmap,
                    colorStr,
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
                    null
                ) to null
            }
            is ColorMethodArgClass.ToColorArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null

                val targetColor = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.targetColorKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.targetColorKeyToIndex.first,
                        where,
                    )
                }
                val rate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.rateKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val grayBitmap = ColorManager.toColor(
                    bitmap,
                    targetColor,
                    rate,
                    where,
                ).let {
                        (grayBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let grayBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    grayBitmap,
                    null
                ) to null
            }
            is ColorMethodArgClass.ToGrayArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null

                val grayBitmap = ColorManager.toGray(
                    bitmap,
                    where,
                ).let {
                        (grayBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let grayBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    grayBitmap,
                    null
                ) to null
            }
            is ColorMethodArgClass.InvertMonoArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null

                val inertedBitmap = ColorManager.invertMono(
                    bitmap,
                    where,
                ).let {
                        (grayBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let grayBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    inertedBitmap,
                    null
                ) to null
            }
            is ColorMethodArgClass.ReduceContrastArgs -> {
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
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null

                val reduceBitmap = ColorManager.reduceContrast(
                    bitmap,
                    where,
                ).let {
                        (grayBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let grayBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    reduceBitmap,
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
                val toBitmap = ColorTool.convertBlackToColor(
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
        suspend fun blackToByTol(
            bitmap: Bitmap,
            toColorStr: String,
            tol: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val toBitmap = ColorTool.convertColorToByTol(
                    bitmap,
                    Color.BLACK,
                    toColorStr.toColorInt(),
                    tol,
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

        suspend fun otherTo(
            bitmap: Bitmap,
            saveColorStr: String,
            toColorStr: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val toBitmap = ColorTool.otherToColor(
                    bitmap,
                    saveColorStr.toColorInt(),
                    toColorStr.toColorInt(),
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
                val toBitmap = ColorTool.convertColorTo(
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
        suspend fun colorToByTol(
            bitmap: Bitmap,
            fromColorStr: String,
            toColorStr: String,
            tol: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val toBitmap = ColorTool.convertColorToByTol(
                    bitmap,
                    fromColorStr.toColorInt(),
                    toColorStr.toColorInt(),
                    tol,
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

        suspend fun toColor(
            bitmap: Bitmap,
            targetColor: String,
            rate: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val toColorBitmap = ColorTool.adjustBitmapColor(
                    bitmap,
                    targetColor,
                    rate
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lcolor.txt").absolutePath,
//                    listOf(
//                        "toColorStr: ${toColorStr}",
//                        "fromColorStr: ${fromColorStr}",
//                    ).joinToString("\n")
//                )
                Pair(
                    toColorBitmap,
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
        suspend fun toGray(
            bitmap: Bitmap,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val glayBitmap = ColorTool.convertGrayScaleBitmap(
                    bitmap,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lcolor.txt").absolutePath,
//                    listOf(
//                        "toColorStr: ${toColorStr}",
//                        "fromColorStr: ${fromColorStr}",
//                    ).joinToString("\n")
//                )
                Pair(
                    glayBitmap,
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


        fun swapTransAndAll(
            bitmap: Bitmap,
            colorStr: String,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val changedBitmap = ColorTool.changeAllToTrans(
                    bitmap,
                    colorStr,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lcolor.txt").absolutePath,
//                    listOf(
//                        "toColorStr: ${toColorStr}",
//                        "fromColorStr: ${fromColorStr}",
//                    ).joinToString("\n")
//                )
                Pair(
                    changedBitmap,
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

        suspend fun invertMono(
            bitmap: Bitmap,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val inevrtedBitmap = BitmapTool.ImageTransformer.invertMonoBitmap(
                    bitmap,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lcolor.txt").absolutePath,
//                    listOf(
//                        "toColorStr: ${toColorStr}",
//                        "fromColorStr: ${fromColorStr}",
//                    ).joinToString("\n")
//                )
                Pair(
                    inevrtedBitmap,
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

        suspend fun reduceContrast(
            bitmap: Bitmap,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val reduceBitmap = BitmapTool.ImageTransformer.reduceContrast(
                    bitmap,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lcolor.txt").absolutePath,
//                    listOf(
//                        "toColorStr: ${toColorStr}",
//                        "fromColorStr: ${fromColorStr}",
//                    ).joinToString("\n")
//                )
                Pair(
                    reduceBitmap,
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
                val toBitmap = ColorTool.convertAllToColorInTrans(
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
                val exchangeBitmap = ColorTool.swapTransparentAndBlack(
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
                val exchangeBitmap = ColorTool.swap(
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
        BLACK_TO_BY_TOL("blackToByTol", ColorMethodArgClass.BlackToByTolArgs),
        OTHER_TO("otherTo", ColorMethodArgClass.BlackToArgs),
        COLOR_TO("colorTo", ColorMethodArgClass.ColorToArgs),
        COLOR_TO_BY_TOL("colorToByTol", ColorMethodArgClass.ColorToByTolArgs),
        ALL_TO("allToInTrans", ColorMethodArgClass.AllToInTarns),
        SWAP_TRANS_AND_BLACK("swapTransAndBlack", ColorMethodArgClass.SwapBlackAndTransArgs),
        SWAP("swap", ColorMethodArgClass.SwapArgs),
        SWAP_TRANS_AND_ALL("swapTransAndAll", ColorMethodArgClass.SwapTransAndAllArgs),
        TO_GRAY("toGray", ColorMethodArgClass.ToGrayArgs),
        TO_COLOR("toColor", ColorMethodArgClass.ToColorArgs),
        INVERT_MONO("invertMono", ColorMethodArgClass.InvertMonoArgs),
        REDUCE_CONTRAST("reduceContrast", ColorMethodArgClass.ReduceContrastArgs),
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
        data object BlackToByTolArgs : ColorMethodArgClass(), ArgType {
            override val entries = BlackToByTolArgs.entries
            val bitmapKeyToIndex = Pair(
                BlackToByTolArgs.BITMAP.key,
                BlackToByTolArgs.BITMAP.index
            )
            val toKeyToIndex = Pair(
                BlackToByTolArgs.TO.key,
                BlackToByTolArgs.TO.index
            )
            val tolKeyToIndex = Pair(
                BlackToByTolArgs.TOLERANCE.key,
                BlackToByTolArgs.TOLERANCE.index
            )
            enum class BlackToByTolArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                TO("to", 1, FuncCheckerForSetting.ArgType.STRING),
                TOLERANCE("tol", 2, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object OtherToArgs : ColorMethodArgClass(), ArgType {
            override val entries = OtherToArgs.entries
            val bitmapKeyToIndex = Pair(
                OtherToArgs.BITMAP.key,
                OtherToArgs.BITMAP.index
            )
            val saveKeyToIndex = Pair(
                OtherToArgs.SAVE.key,
                OtherToArgs.SAVE.index
            )
            val toKeyToIndex = Pair(
                OtherToArgs.TO.key,
                OtherToArgs.TO.index
            )
            enum class OtherToArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                SAVE("save", 1, FuncCheckerForSetting.ArgType.STRING),
                TO("to", 2, FuncCheckerForSetting.ArgType.STRING),
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
        data object ColorToByTolArgs : ColorMethodArgClass(), ArgType {
            override val entries = ColorToByTolArgs.entries
            val bitmapKeyToIndex = Pair(
                ColorToByTolArgs.BITMAP.key,
                ColorToByTolArgs.BITMAP.index
            )
            val toKeyToIndex = Pair(
                ColorToByTolArgs.TO.key,
                ColorToByTolArgs.TO.index
            )
            val fromKeyToIndex = Pair(
                ColorToByTolArgs.FROM.key,
                ColorToByTolArgs.FROM.index
            )
            val tolKeyToIndex = Pair(
                ColorToByTolArgs.TOLERANCE.key,
                ColorToByTolArgs.TOLERANCE.index
            )
            enum class ColorToByTolArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                FROM("from", 1, FuncCheckerForSetting.ArgType.STRING),
                TO("to", 2, FuncCheckerForSetting.ArgType.STRING),
                TOLERANCE("tol", 3, FuncCheckerForSetting.ArgType.INT),
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
        data object ToColorArgs : ColorMethodArgClass(), ArgType {
            override val entries = ToColorArgs.entries
            val bitmapKeyToIndex = Pair(
                ToColorArgs.BITMAP.key,
                ToColorArgs.BITMAP.index
            )
            val targetColorKeyToIndex = Pair(
                ToColorArgs.TARGET_COLOR.key,
                ToColorArgs.TARGET_COLOR.index
            )
            val rateKeyToIndex = Pair(
                ToColorArgs.RATE.key,
                ToColorArgs.RATE.index
            )
            enum class ToColorArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                TARGET_COLOR("targetColor", 1, FuncCheckerForSetting.ArgType.STRING),
                RATE("rate", 2, FuncCheckerForSetting.ArgType.FLOAT),
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

        data object ToGrayArgs : ColorMethodArgClass(), ArgType {
            override val entries = ToGrayTransAndBlackArgs.entries
            val bitmapKeyToIndex = Pair(
                ToGrayTransAndBlackArgs.BITMAP.key,
                ToGrayTransAndBlackArgs.BITMAP.index
            )
            enum class ToGrayTransAndBlackArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
        data object SwapTransAndAllArgs : ColorMethodArgClass(), ArgType {
            override val entries = SwapTransAndAllArgs.entries
            val bitmapKeyToIndex = Pair(
                SwapTransAndAllArgs.BITMAP.key,
                SwapTransAndAllArgs.BITMAP.index
            )
            val colorKeyToIndex = Pair(
                SwapTransAndAllArgs.COLOR.key,
                SwapTransAndAllArgs.COLOR.index
            )
            enum class SwapTransAndAllArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                COLOR("color", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }

        data object InvertMonoArgs : ColorMethodArgClass(), ArgType {
            override val entries = InvertMonoArgs.entries
            val bitmapKeyToIndex = Pair(
                InvertMonoArgs.BITMAP.key,
                InvertMonoArgs.BITMAP.index
            )
            enum class InvertMonoArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
        data object ReduceContrastArgs : ColorMethodArgClass(), ArgType {
            override val entries = ReduceContrastArgs.entries
            val bitmapKeyToIndex = Pair(
                ReduceContrastArgs.BITMAP.key,
                ReduceContrastArgs.BITMAP.index
            )
            enum class ReduceContrastArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
    }
}
