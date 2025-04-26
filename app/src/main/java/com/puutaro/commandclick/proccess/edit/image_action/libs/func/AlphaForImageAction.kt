package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.AlphaManager
import kotlin.enums.EnumEntries

object AlphaForImageAction {
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
            is AlphaMethodArgClass.HorizonToArgs -> {
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
                val inclineFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.inclineKeyToIndex,
                    where
                ).let { inclineToErr ->
                    val funcErr = inclineToErr.second
                        ?: return@let inclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.offsetKeyToIndex,
                    where
                ).let { offsetToErr ->
                    val funcErr = offsetToErr.second
                        ?: return@let offsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = InnerAlpha.horizon(
                    bitmap,
                    inclineFloat,
                    offsetFloat,
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
            is AlphaMethodArgClass.HorizonToLowArgs -> {
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
                val inclineFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.inclineKeyToIndex,
                    where
                ).let { inclineToErr ->
                    val funcErr = inclineToErr.second
                        ?: return@let inclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.offsetKeyToIndex,
                    where
                ).let { offsetToErr ->
                    val funcErr = offsetToErr.second
                        ?: return@let offsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = InnerAlpha.horizonToLow(
                    bitmap,
                    inclineFloat,
                    offsetFloat,
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
            is AlphaMethodArgClass.RadianToArgs -> {
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
                val centerX = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.centerXKeyToIndex,
                    where
                ).let { centerXToErr ->
                    val funcErr = centerXToErr.second
                        ?: return@let centerXToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val centerY = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.centerYKeyToIndex,
                    where
                ).let { centerYToErr ->
                    val funcErr = centerYToErr.second
                        ?: return@let centerYToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val inclineFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.inclineKeyToIndex,
                    where
                ).let { inclineToErr ->
                    val funcErr = inclineToErr.second
                        ?: return@let inclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.offsetKeyToIndex,
                    where
                ).let { offsetToErr ->
                    val funcErr = offsetToErr.second
                        ?: return@let offsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = InnerAlpha.radian(
                    bitmap,
                    centerX,
                    centerY,
                    inclineFloat,
                    offsetFloat,
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
            is AlphaMethodArgClass.OverrideHorizonToArgs -> {
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
                val inclineFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.inclineKeyToIndex,
                    where
                ).let { inclineToErr ->
                    val funcErr = inclineToErr.second
                        ?: return@let inclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.offsetKeyToIndex,
                    where
                ).let { offsetToErr ->
                    val funcErr = offsetToErr.second
                        ?: return@let offsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = InnerAlpha.overrideHorizon(
                    bitmap,
                    inclineFloat,
                    offsetFloat,
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
            is AlphaMethodArgClass.OverrideRadianToArgs -> {
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
                val centerX = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.centerXKeyToIndex,
                    where
                ).let { centerXToErr ->
                    val funcErr = centerXToErr.second
                        ?: return@let centerXToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val centerY = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.centerYKeyToIndex,
                    where
                ).let { centerYToErr ->
                    val funcErr = centerYToErr.second
                        ?: return@let centerYToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val inclineFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.inclineKeyToIndex,
                    where
                ).let { inclineToErr ->
                    val funcErr = inclineToErr.second
                        ?: return@let inclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.offsetKeyToIndex,
                    where
                ).let { offsetToErr ->
                    val funcErr = offsetToErr.second
                        ?: return@let offsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = InnerAlpha.overrideRadian(
                    bitmap,
                    centerX,
                    centerY,
                    inclineFloat,
                    offsetFloat,
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
            is AlphaMethodArgClass.HorizonByWaveToArgs -> {
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
                val centerX = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                val centerY = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
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
                val inclineFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.inclineKeyToDefaultValueStr,
                    where
                ).let { inclineToErr ->
                    val funcErr = inclineToErr.second
                        ?: return@let inclineToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetFloat = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.offsetKeyToDefaultValueStr,
                    where
                ).let { offsetToErr ->
                    val funcErr = offsetToErr.second
                        ?: return@let offsetToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val waveAmp = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.waveAmpKeyToDefaultValueStr,
                    where
                ).let { waveAmpToErr ->
                    val funcErr = waveAmpToErr.second
                        ?: return@let waveAmpToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    waveAmp,
                    args.waveAmpKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val waveLength = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.waveLengthKeyToDefaultValueStr,
                    where
                ).let { waveLengthToErr ->
                    val funcErr = waveLengthToErr.second
                        ?: return@let waveLengthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0f,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    waveLength,
                    args.waveLengthKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }

                val lengthDivider = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    args.lengthDividerKeyToDefaultValueStr,
                    where
                ).let { lengthDividerToErr ->
                    val funcErr = lengthDividerToErr.second
                        ?: return@let lengthDividerToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.compare(
                    0f,
                    FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                    lengthDivider,
                    args.waveLengthKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                FuncCheckerForSetting.NumChecker.compare(
                    waveLength,
                    FuncCheckerForSetting.NumChecker.CompareSignal.SMALLER,
                    lengthDivider,
                    args.waveLengthKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val returnBitmap = InnerAlpha.horizonByWave(
                    bitmap,
                    centerX,
                    centerY,
                    inclineFloat,
                    offsetFloat,
                    waveAmp,
                    waveLength,
                    lengthDivider,
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
        }
    }

    private object InnerAlpha {
        suspend fun horizon(
            bitmap: Bitmap,
            inclineFloat: Float,
            offset: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val alphaBitmap = AlphaManager.fadeBitmapLeftToRight(
                    bitmap,
                    inclineFloat,
                    offset,
                )
                Pair(
                    alphaBitmap,
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
        suspend fun horizonToLow(
            bitmap: Bitmap,
            inclineFloat: Float,
            offset: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val alphaBitmap = AlphaManager.fadeBitmapLeftToRightToLow(
                    bitmap,
                    inclineFloat,
                    offset,
                )
                Pair(
                    alphaBitmap,
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
        suspend fun radian(
            bitmap: Bitmap,
            centerX: Int,
            centerY: Int,
            inclineFloat: Float,
            offset: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val alphaBitmap = AlphaManager.fadeBitmapFromCenter(
                    bitmap,
                    centerX,
                    centerY,
                    inclineFloat,
                    offset,
                )
                Pair(
                    alphaBitmap,
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
        suspend fun overrideHorizon(
            bitmap: Bitmap,
            inclineFloat: Float,
            offset: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val alphaBitmap = AlphaManager.overrideLeftToRight(
                    bitmap,
                    inclineFloat,
                    offset,
                )
                Pair(
                    alphaBitmap,
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
        suspend fun overrideRadian(
            bitmap: Bitmap,
            centerX: Int,
            centerY: Int,
            inclineFloat: Float,
            offset: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val alphaBitmap = AlphaManager.overrideFromCenter(
                    bitmap,
                    centerX,
                    centerY,
                    inclineFloat,
                    offset,
                )
                Pair(
                    alphaBitmap,
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
        suspend fun horizonByWave(
            bitmap: Bitmap,
            centerX: Int,
            centerY: Int,
            waveAmplitude: Float,
            waveLength: Float,
            lengthDivider: Int,
            alphaIncline: Float,
            alphaOffset: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val alphaBitmap = AlphaManager.drawWavyLineWithAlphaDecayByHorizon(
                    bitmap,
                    centerX,
                    centerY,
                    waveAmplitude,
                    waveLength,
                    lengthDivider,
                    alphaIncline,
                    alphaOffset
                )
                Pair(
                    alphaBitmap,
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
        val args: AlphaMethodArgClass,
    ){
        HORIZON("horizon", AlphaMethodArgClass.HorizonToLowArgs),
        RAD("rad", AlphaMethodArgClass.RadianToArgs),
        OVERRIDE_HORIZON("overHorizon", AlphaMethodArgClass.OverrideHorizonToArgs),
        OVERRIDE_RAD("overRad", AlphaMethodArgClass.OverrideRadianToArgs),
        HORIZON_BY_WAVE("horizonByWave", AlphaMethodArgClass.HorizonByWaveToArgs),
        HORIZON_TO_LOW("horizonToLow", AlphaMethodArgClass.HorizonToLowArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class AlphaMethodArgClass {
        data object HorizonToArgs : AlphaMethodArgClass(), ArgType {
            override val entries = HorizonToArgs.entries
            val bitmapKeyToIndex = Pair(
                HorizonToArgs.BITMAP.key,
                HorizonToArgs.BITMAP.index
            )
            val inclineKeyToIndex = Pair(
                HorizonToArgs.INCLINE.key,
                HorizonToArgs.INCLINE.index
            )
            val offsetKeyToIndex = Pair(
                HorizonToArgs.OFFSET.key,
                HorizonToArgs.OFFSET.index
            )
            enum class HorizonToArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                INCLINE("incline", 1, FuncCheckerForSetting.ArgType.FLOAT),
                OFFSET("offset", 2, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object HorizonToLowArgs : AlphaMethodArgClass(), ArgType {
            override val entries = HorizonToLowArgs.entries
            val bitmapKeyToIndex = Pair(
                HorizonToLowArgs.BITMAP.key,
                HorizonToLowArgs.BITMAP.index
            )
            val inclineKeyToIndex = Pair(
                HorizonToLowArgs.INCLINE.key,
                HorizonToLowArgs.INCLINE.index
            )
            val offsetKeyToIndex = Pair(
                HorizonToLowArgs.OFFSET.key,
                HorizonToLowArgs.OFFSET.index
            )
            enum class HorizonToLowArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                INCLINE("incline", 1, FuncCheckerForSetting.ArgType.FLOAT),
                OFFSET("offset", 2, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object RadianToArgs : AlphaMethodArgClass(), ArgType {
            override val entries = RadianToArgs.entries
            val bitmapKeyToIndex = Pair(
                RadianToArgs.BITMAP.key,
                RadianToArgs.BITMAP.index
            )
            val centerXKeyToIndex = Pair(
                RadianToArgs.CENTER_X.key,
                RadianToArgs.CENTER_X.index
            )
            val centerYKeyToIndex = Pair(
                RadianToArgs.CENTER_Y.key,
                RadianToArgs.CENTER_Y.index
            )
            val inclineKeyToIndex = Pair(
                RadianToArgs.INCLINE.key,
                RadianToArgs.INCLINE.index
            )
            val offsetKeyToIndex = Pair(
                RadianToArgs.OFFSET.key,
                RadianToArgs.OFFSET.index
            )
            enum class RadianToArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                CENTER_X("centerX", 1, FuncCheckerForSetting.ArgType.INT),
                CENTER_Y("centerY", 2, FuncCheckerForSetting.ArgType.INT),
                INCLINE("incline", 3, FuncCheckerForSetting.ArgType.FLOAT),
                OFFSET("offset", 4, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object OverrideHorizonToArgs : AlphaMethodArgClass(), ArgType {
            override val entries = OveerrideHorizonToArgs.entries
            val bitmapKeyToIndex = Pair(
                OveerrideHorizonToArgs.BITMAP.key,
                OveerrideHorizonToArgs.BITMAP.index
            )
            val inclineKeyToIndex = Pair(
                OveerrideHorizonToArgs.INCLINE.key,
                OveerrideHorizonToArgs.INCLINE.index
            )
            val offsetKeyToIndex = Pair(
                OveerrideHorizonToArgs.OFFSET.key,
                OveerrideHorizonToArgs.OFFSET.index
            )
            enum class OveerrideHorizonToArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                INCLINE("incline", 1, FuncCheckerForSetting.ArgType.FLOAT),
                OFFSET("offset", 2, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object OverrideRadianToArgs : AlphaMethodArgClass(), ArgType {
            override val entries = OverrideRadianToArgs.entries
            val bitmapKeyToIndex = Pair(
                OverrideRadianToArgs.BITMAP.key,
                OverrideRadianToArgs.BITMAP.index
            )
            val centerXKeyToIndex = Pair(
                OverrideRadianToArgs.CENTER_X.key,
                OverrideRadianToArgs.CENTER_X.index
            )
            val centerYKeyToIndex = Pair(
                OverrideRadianToArgs.CENTER_Y.key,
                OverrideRadianToArgs.CENTER_Y.index
            )
            val inclineKeyToIndex = Pair(
                OverrideRadianToArgs.INCLINE.key,
                OverrideRadianToArgs.INCLINE.index
            )
            val offsetKeyToIndex = Pair(
                OverrideRadianToArgs.OFFSET.key,
                OverrideRadianToArgs.OFFSET.index
            )
            enum class OverrideRadianToArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                CENTER_X("centerX", 1, FuncCheckerForSetting.ArgType.INT),
                CENTER_Y("centerY", 2, FuncCheckerForSetting.ArgType.INT),
                INCLINE("incline", 3, FuncCheckerForSetting.ArgType.FLOAT),
                OFFSET("offset", 4, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }

        data object HorizonByWaveToArgs : AlphaMethodArgClass(), ArgType {
            override val entries = HorizonByWaveToArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.BITMAP.key,
                HorizonByWaveToArgs.BITMAP.defaultValueStr
            )
            val centerXKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.CENTER_X.key,
                HorizonByWaveToArgs.CENTER_X.defaultValueStr
            )
            val centerYKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.CENTER_Y.key,
                HorizonByWaveToArgs.CENTER_Y.defaultValueStr
            )
            val inclineKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.INCLINE.key,
                HorizonByWaveToArgs.INCLINE.defaultValueStr
            )
            val offsetKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.OFFSET.key,
                HorizonByWaveToArgs.OFFSET.defaultValueStr
            )
            val waveAmpKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.WAVE_AMP.key,
                HorizonByWaveToArgs.WAVE_AMP.defaultValueStr
            )
            val waveLengthKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.WAVE_LENGTH.key,
                HorizonByWaveToArgs.WAVE_LENGTH.defaultValueStr
            )
            val lengthDividerKeyToDefaultValueStr = Pair(
                HorizonByWaveToArgs.LENGTH_DIVIDER.key,
                HorizonByWaveToArgs.LENGTH_DIVIDER.defaultValueStr
            )

            enum class HorizonByWaveToArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                CENTER_X("centerX", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                CENTER_Y("centerY", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                INCLINE("incline", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                OFFSET("offset", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                WAVE_AMP("waveAmp", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                WAVE_LENGTH("waveLength", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
                LENGTH_DIVIDER("lengthDivider", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
    }
}