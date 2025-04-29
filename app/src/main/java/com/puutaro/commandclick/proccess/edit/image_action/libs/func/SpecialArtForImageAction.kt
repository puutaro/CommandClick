package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ColorTool
import kotlin.enums.EnumEntries
import com.puutaro.commandclick.util.image_tools.SpecialArt

object SpecialArtForImageAction {
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
            is BitmapArtMethodArgClass.IncSplashArgs -> {
                val formalArgIndexToNameToTypeList = BitmapArtMethodArgClass.IncSplashArgs.entries.mapIndexed {
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
                    BitmapArtMethodArgClass.IncSplashArgs.widthKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val height = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.heightKeyToDefaultValueStr,
                    where
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val radiusRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.radiusRateKeyToDefaultValueStr,
                    where
                ).let { rateToErr ->
                    val funcErr = rateToErr.second
                        ?: return@let rateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val minPointNum = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.minPpintNumKeyToDefaultValueStr,
                    where
                ).let { numToErr ->
                    val funcErr = numToErr.second
                        ?: return@let numToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val maxPointNum = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.maxPointNumKeyToDefaultValueStr,
                    where
                ).let { numToErr ->
                    val funcErr = numToErr.second
                        ?: return@let numToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minPointNum,
                    maxPointNum,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
                    args.minPpintNumKeyToDefaultValueStr.first,
                    args.maxPointNumKeyToDefaultValueStr.first,
                    where,
                ).let {
                        err ->
                    if(err == null) return@let
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to err
                }
                val minAngle = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.minAngleKeyToDefaultValueStr,
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
                    BitmapArtMethodArgClass.IncSplashArgs.maxAngleKeyToDefaultValueStr,
                    where
                ).let { angleToErr ->
                    val funcErr = angleToErr.second
                        ?: return@let angleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                FuncCheckerForSetting.NumChecker.minMaxTwoFloatErr(
                    minAngle,
                    maxAngle,
                    FuncCheckerForSetting.NumChecker.MinMaxCompare.NOT_EQUAL,
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
                val opacityIncline = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.opacityInclineKeyToDefaultValueStr,
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
                    BitmapArtMethodArgClass.IncSplashArgs.opacityOffsetKeyToDefaultValueStr,
                    where
                ).let { opacityOffsetRateToErr ->
                    val funcErr = opacityOffsetRateToErr.second
                        ?: return@let opacityOffsetRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val times = FuncCheckerForSetting.Getter.getZeroELargerIntFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.timesKeyToDefaultValueStr,
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
                    BitmapArtMethodArgClass.IncSplashArgs.colorListKeyToDefaultValueStr,
                    where
                ).let { colorLitConToErr ->
                    val funcErr = colorLitConToErr.second
                        ?: return@let colorLitConToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.split(",").asSequence().filter { it.trim().isNotEmpty() }.map {
                    ColorTool.parseColorStr(
                        context,
                        it,
                        BitmapArtMethodArgClass.IncSplashArgs.colorListKeyToDefaultValueStr.first,
                        where,
                    ).let {
                        ColorTool.removeAlpha(it)
                    }
                }.toList()
                val minOpacityRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.minOpacityRateKeyToDefaultValueStr,
                    where
                ).let { minOpacityRateToErr ->
                    val funcErr = minOpacityRateToErr.second
                        ?: return@let minOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val maxOpacityRate = FuncCheckerForSetting.Getter.getRateFloatFromArgMapByName(
                    mapArgMapList,
                    BitmapArtMethodArgClass.IncSplashArgs.maxOpacityRateKeyToDefaultValueStr,
                    where
                ).let { maxOpacityRateToErr ->
                    val funcErr = maxOpacityRateToErr.second
                        ?: return@let maxOpacityRateToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
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
                val returnBitmap = InnerSpecialArt.incSplash(
                    width,
                    height,
                    radiusRate,
                    times,
                    minPointNum,
                    maxPointNum,
                    colorList,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    minAngle,
                    maxAngle,
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

    private object InnerSpecialArt {

        suspend fun incSplash(
            width: Int,
            height: Int,
            radiusRate: Float,
            times: Int,
            minPointNum: Int,
            maxPointNum: Int,
            colorList: List<String>,
            minOpacityRate: Float,
            maxOpacityRate: Float,
            opacityIncline: Float,
            opacityOffset: Float,
            minAngle: Float,
            maxAngle: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                SpecialArt.drawInkSplash(
                    width,
                    height,
                    radiusRate,
                    times,
                    minPointNum,
                    maxPointNum,
                    colorList,
                    minOpacityRate,
                    maxOpacityRate,
                    opacityIncline,
                    opacityOffset,
                    minAngle,
                    maxAngle
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
        INC_SPLASH("incSplash", BitmapArtMethodArgClass.IncSplashArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class BitmapArtMethodArgClass {
        data object IncSplashArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = IncSplashEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.WIDTH.key,
                IncSplashEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                IncSplashEnumArgs.HEIGHT.key,
                IncSplashEnumArgs.HEIGHT.defaultValueStr
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
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
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

}