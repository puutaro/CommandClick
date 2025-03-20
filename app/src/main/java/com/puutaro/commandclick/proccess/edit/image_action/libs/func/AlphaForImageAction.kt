package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
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
                val alphaBitmap = BitmapTool.AlphaManager.fadeBitmapLeftToRight(
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
                val alphaBitmap = BitmapTool.AlphaManager.fadeBitmapFromCenter(
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
                val alphaBitmap = BitmapTool.AlphaManager.overrideLeftToRight(
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
                val alphaBitmap = BitmapTool.AlphaManager.overrideFromCenter(
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

    }

    private enum class MethodNameClass(
        val str: String,
        val args: AlphaMethodArgClass,
    ){
        HORIZON("horizon", AlphaMethodArgClass.HorizonToArgs),
        RAD("rad", AlphaMethodArgClass.RadianToArgs),
        OVERRIDE_HORIZON("overHorizon", AlphaMethodArgClass.OverrideHorizonToArgs),
        OVERRIDE_RAD("overRad", AlphaMethodArgClass.OverrideRadianToArgs),
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
    }
}
