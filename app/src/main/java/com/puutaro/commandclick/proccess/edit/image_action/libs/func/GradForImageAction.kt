package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ColorTool
import kotlin.enums.EnumEntries
import androidx.core.graphics.toColorInt

object GradForImageAction {
    fun handle(
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
        if(context == null) return null
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
            is GradMethodArgClass.MakeArgs -> {
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
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorKeyToDefaultValueStr,
                    where
                ).let { colorStrToErr ->
                    val funcErr = colorStrToErr.second
                        ?: return@let colorStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val orientation = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.orientationKeyToDefaultValueStr,
                    where
                ).let { orientationToErr ->
                    val funcErr = orientationToErr.second
                        ?: return@let orientationToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    orientationStr ->
                    BitmapTool.GradientBitmap.GradOrient.entries.firstOrNull {
                        it.name == orientationStr
                    } ?: BitmapTool.GradientBitmap.GradOrient.LINEAR
                }
                val returnBitmap = Grad.make(
                    context,
                    args,
                    argsPairList,
                    width,
                    height,
                    orientation,
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
            is GradMethodArgClass.RadArgs -> {
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
                val centerX = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
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
                val centerY = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
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
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorKeyToDefaultValueStr,
                    where
                ).let { colorStrToErr ->
                    val funcErr = colorStrToErr.second
                        ?: return@let colorStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = Grad.rad(
                    context,
                    args,
                    argsPairList,
                    centerX,
                    centerY,
                    width,
                    height,
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
            is GradMethodArgClass.SweepArgs -> {
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
                val centerX = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
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
                val centerY = FuncCheckerForSetting.Getter.getZeroELargerFloatFromArgMapByName(
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
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.colorKeyToDefaultValueStr,
                    where
                ).let { colorStrToErr ->
                    val funcErr = colorStrToErr.second
                        ?: return@let colorStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = Grad.sweep(
                    context,
                    args,
                    argsPairList,
                    centerX,
                    centerY,
                    width,
                    height,
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

    private object Grad {
        fun sweep(
            context: Context,
            args: GradMethodArgClass.SweepArgs,
            argsPairList: List<Pair<String, String>>,
            centerX: Float,
            centerY: Float,
            width: Int,
            height: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val colorKey = args.colorKeyToDefaultValueStr.first
                val colorIntArray = argsPairList.filter {
                    it.first == colorKey
                }.mapIndexed {
                        index, (_, colorStr) ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        "${colorKey}${index}",
                        where,
                    ).toColorInt()
                }.toIntArray()
                val radGradBitmap = BitmapTool.GradientBitmap.createSweepGradientBitmap(
                    centerX,
                    centerY,
                    width,
                    height,
                    colorIntArray,
                )
                Pair(
                    radGradBitmap,
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
        fun rad(
            context: Context,
            args: GradMethodArgClass.RadArgs,
            argsPairList: List<Pair<String, String>>,
            centerX: Float,
            centerY: Float,
            width: Int,
            height: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val colorKey = args.colorKeyToDefaultValueStr.first
                val colorIntArray = argsPairList.filter {
                    it.first == colorKey
                }.mapIndexed {
                        index, (_, colorStr) ->
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        "${colorKey}${index}",
                        where,
                    ).toColorInt()
                }.toIntArray()
                val radGradBitmap = BitmapTool.GradientBitmap.createRadialGradientBitmap(
                    centerX,
                    centerY,
                    width,
                    height,
                    colorIntArray,
                )
                Pair(
                    radGradBitmap,
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
        fun make(
            context: Context,
            args: GradMethodArgClass.MakeArgs,
            argsPairList: List<Pair<String, String>>,
            width: Int,
            height: Int,
            orientation: BitmapTool. GradientBitmap. GradOrient,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val colorKey = args.colorKeyToDefaultValueStr.first
                val colorIntArray = argsPairList.filter {
                    it.first == colorKey
                }.mapIndexed {
                    index, (_, colorStr) ->
                    ColorTool.parseColorStr(
                    context,
                    colorStr,
                    "${colorKey}${index}",
                    where,
                ).toColorInt()
                }.toIntArray()
                val gradBitmap = BitmapTool.GradientBitmap.makeGradientBitmap2(
                    width,
                    height,
                    colorIntArray,
                    orientation
                )
                Pair(
                    gradBitmap,
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
        val args: GradMethodArgClass,
    ){
        MAKE("make", GradMethodArgClass.MakeArgs),
        RAD("rad", GradMethodArgClass.RadArgs),
        SWEEP("sweep", GradMethodArgClass.SweepArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class GradMethodArgClass {
        data object MakeArgs : GradMethodArgClass(), ArgType {
            override val entries = MakeArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                MakeArgs.WIDTH.key,
                MakeArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                MakeArgs.HEIGHT.key,
                MakeArgs.HEIGHT.defaultValueStr
            )
            val colorKeyToDefaultValueStr = Pair(
                MakeArgs.COLOR.key,
                MakeArgs.COLOR.defaultValueStr
            )
            val orientationKeyToDefaultValueStr = Pair(
                MakeArgs.ORIENTATION.key,
                MakeArgs.ORIENTATION.defaultValueStr
            )
            enum class MakeArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                COLOR("color", null, FuncCheckerForSetting.ArgType.STRING),
                ORIENTATION("orientation", BitmapTool.GradientBitmap.GradOrient.LINEAR.name, FuncCheckerForSetting.ArgType.STRING),
            }

        }
        data object RadArgs : GradMethodArgClass(), ArgType {
            override val entries = RadArgs.entries
            val centerXKeyToDefaultValueStr = Pair(
                RadArgs.CENTER_X.key,
                RadArgs.CENTER_X.defaultValueStr
            )
            val centerYKeyToDefaultValueStr = Pair(
                RadArgs.CENTER_Y.key,
                RadArgs.CENTER_Y.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                RadArgs.WIDTH.key,
                RadArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                RadArgs.HEIGHT.key,
                RadArgs.HEIGHT.defaultValueStr
            )
            val colorKeyToDefaultValueStr = Pair(
                RadArgs.COLOR.key,
                RadArgs.COLOR.defaultValueStr
            )
            enum class RadArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                CENTER_X("centerX", null, FuncCheckerForSetting.ArgType.FLOAT),
                CENTER_Y("centerY", null, FuncCheckerForSetting.ArgType.FLOAT),
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                COLOR("color", null, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object SweepArgs : GradMethodArgClass(), ArgType {
            override val entries = SweepEnumArgs.entries
            val centerXKeyToDefaultValueStr = Pair(
                SweepEnumArgs.CENTER_X.key,
                SweepEnumArgs.CENTER_X.defaultValueStr
            )
            val centerYKeyToDefaultValueStr = Pair(
                SweepEnumArgs.CENTER_Y.key,
                SweepEnumArgs.CENTER_Y.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                SweepEnumArgs.WIDTH.key,
                SweepEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                SweepEnumArgs.HEIGHT.key,
                SweepEnumArgs.HEIGHT.defaultValueStr
            )
            val colorKeyToDefaultValueStr = Pair(
                SweepEnumArgs.COLOR.key,
                SweepEnumArgs.COLOR.defaultValueStr
            )
            enum class SweepEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                CENTER_X("centerX", null, FuncCheckerForSetting.ArgType.FLOAT),
                CENTER_Y("centerY", null, FuncCheckerForSetting.ArgType.FLOAT),
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                COLOR("color", null, FuncCheckerForSetting.ArgType.STRING),
            }

        }

    }
}
