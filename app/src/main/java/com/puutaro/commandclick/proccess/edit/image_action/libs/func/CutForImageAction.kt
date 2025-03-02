package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlin.enums.EnumEntries

object CutForImageAction {
    fun handle(
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
            is CutMethodArgClass.ByFreeArgs -> {
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
                }
                val offsetX = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.offsetXKeyToDefaultValueStr,
                    where
                ).let { offsetXToErr ->
                    val funcErr = offsetXToErr.second
                        ?: return@let offsetXToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetY = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.offsetYKeyToDefaultValueStr,
                    where
                ).let { offsetYToErr ->
                    val funcErr = offsetYToErr.second
                        ?: return@let offsetYToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = Cut.byOffset(
                    bitmap,
                    width,
                    height,
                    offsetX,
                    offsetY,
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
            is CutMethodArgClass.ByCenterArgs -> {
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
                }
                val returnBitmap = Cut.byCenter(
                    bitmap,
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

    private object Cut {
        fun byOffset(
            bitmap: Bitmap,
            limitWidthPx: Int,
            limitHeightPx: Int,
            offsetX: Int,
            offsetY: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val cutBitmap = BitmapTool.ImageTransformer.cutByTarget(
                    bitmap,
                    limitWidthPx,
                    limitHeightPx,
                    offsetX,
                    offsetY,
                )
                Pair(
                    cutBitmap,
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
        fun byCenter(
            bitmap: Bitmap,
            limitWidthPx: Int,
            limitHeightPx: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val cutBitmap = BitmapTool.ImageTransformer.cutByTarget(
                    bitmap,
                    limitWidthPx,
                    limitHeightPx,
                    (bitmap.width - limitWidthPx)/ 2,
                    (bitmap.height - limitHeightPx) / 2,
                )
                Pair(
                    cutBitmap,
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
        val args: CutMethodArgClass,
    ){
        BY_OFFSET("byOffset", CutMethodArgClass.ByFreeArgs),
        BY_CENTER("byCenter", CutMethodArgClass.ByCenterArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class CutMethodArgClass {
        data object ByFreeArgs : CutMethodArgClass(), ArgType {
            override val entries = ByFreeEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                ByFreeEnumArgs.BITMAP.key,
                ByFreeEnumArgs.BITMAP.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                ByFreeEnumArgs.WIDTH.key,
                ByFreeEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                ByFreeEnumArgs.HEIGHT.key,
                ByFreeEnumArgs.HEIGHT.defaultValueStr
            )
            val offsetXKeyToDefaultValueStr = Pair(
                ByFreeEnumArgs.OFFSET_X.key,
                ByFreeEnumArgs.OFFSET_X.defaultValueStr
            )
            val offsetYKeyToDefaultValueStr = Pair(
                ByFreeEnumArgs.OFFSET_Y.key,
                ByFreeEnumArgs.OFFSET_Y.defaultValueStr
            )
            enum class ByFreeEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
                OFFSET_X("offsetX", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                OFFSET_Y("offsetY", 0.toString(), FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object ByCenterArgs : CutMethodArgClass(), ArgType {
            override val entries = ByCenterEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                ByCenterEnumArgs.BITMAP.key,
                ByCenterEnumArgs.BITMAP.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                ByCenterEnumArgs.WIDTH.key,
                ByCenterEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                ByCenterEnumArgs.HEIGHT.key,
                ByCenterEnumArgs.HEIGHT.defaultValueStr
            )
            enum class ByCenterEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                WIDTH("width", null, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", null, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}
