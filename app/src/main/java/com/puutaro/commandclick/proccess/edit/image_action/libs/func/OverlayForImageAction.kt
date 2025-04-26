package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ImageOverlay
import kotlin.enums.EnumEntries

object OverlayForImageAction {
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
            is OverlayMethodArgClass.ByCenterArgs -> {
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
                val bkBitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bkBitmapKeyToIndex,
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
                val returnBitmap = Overlay.byCenter(
                    bkBitmap,
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
            is OverlayMethodArgClass.ByOffsetArgs -> {
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
                val bkBitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bkBitmapKeyToIndex,
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
                val offsetX = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.offsetXKeyToIndex,
                    where
                ).let { offsetXToErr ->
                    val funcErr = offsetXToErr.second
                        ?: return@let offsetXToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val offsetY = FuncCheckerForSetting.Getter.getFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.offsetYKeyToIndex,
                    where
                ).let { offsetYToErr ->
                    val funcErr = offsetYToErr.second
                        ?: return@let offsetYToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = Overlay.byOffset(
                    bkBitmap,
                    bitmap,
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
            is OverlayMethodArgClass.ByRndArgs -> {
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
                val bkBitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bkBitmapKeyToIndex,
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
                val returnBitmap = Overlay.byRnd(
                    bkBitmap,
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
        }
    }

    private object Overlay {
        fun byCenter(
            bkBitmap: Bitmap,
            bitmap: Bitmap,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val overlayBitmap = ImageOverlay.overlayOnBkBitmapCenter(
                    bkBitmap,
                    bitmap,
                )
                Pair(
                    overlayBitmap,
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

        fun byOffset(
            bkBitmap: Bitmap,
            bitmap: Bitmap,
            offsetX: Float,
            offsetY: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "loffset.txt").absolutePath,
//                    listOf(
//                        "offsetX: ${offsetX}",
//                        "offsetY: ${offsetY}",
//                    ).joinToString("\n\n") + "\n\n============\n\n"
//                )
                val overlayBitmap = ImageOverlay.overlayOnBkBitmapByPivot(
                    bkBitmap,
                    bitmap,
                    offsetX,
                    offsetY,
                )
                Pair(
                    overlayBitmap,
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

        fun byRnd(
            bkBitmap: Bitmap,
            bitmap: Bitmap,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val overlayBitmap = ImageOverlay.overlayOnBkBitmap(
                    bkBitmap,
                    bitmap,
                )
                Pair(
                    overlayBitmap,
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
        val args: OverlayMethodArgClass,
    ){
        BY_CENTER("byCenter", OverlayMethodArgClass.ByCenterArgs),
        BY_OFFSET("byOffset", OverlayMethodArgClass.ByOffsetArgs),
        BY_RND("byRnd", OverlayMethodArgClass.ByRndArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class OverlayMethodArgClass {
        data object ByCenterArgs : OverlayMethodArgClass(), ArgType {
            override val entries = ByCenterArgs.entries
            val bkBitmapKeyToIndex = Pair(
                ByCenterArgs.BK_BITMAP.key,
                ByCenterArgs.BK_BITMAP.index
            )
            val bitmapKeyToIndex = Pair(
                ByCenterArgs.BITMAP.key,
                ByCenterArgs.BITMAP.index
            )
            enum class ByCenterArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BK_BITMAP("bkBitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                BITMAP("bitmap", 1, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
        data object ByOffsetArgs : OverlayMethodArgClass(), ArgType {
            override val entries = ByOffsetArgs.entries
            val bkBitmapKeyToIndex = Pair(
                ByOffsetArgs.BK_BITMAP.key,
                ByOffsetArgs.BK_BITMAP.index
            )
            val bitmapKeyToIndex = Pair(
                ByOffsetArgs.BITMAP.key,
                ByOffsetArgs.BITMAP.index
            )
            val offsetXKeyToIndex = Pair(
                ByOffsetArgs.OFFSET_X.key,
                ByOffsetArgs.OFFSET_X.index
            )
            val offsetYKeyToIndex = Pair(
                ByOffsetArgs.OFFSET_Y.key,
                ByOffsetArgs.OFFSET_Y.index
            )
            enum class ByOffsetArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BK_BITMAP("bkBitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                BITMAP("bitmap", 1, FuncCheckerForSetting.ArgType.BITMAP),
                OFFSET_X("offsetX", 2, FuncCheckerForSetting.ArgType.FLOAT),
                OFFSET_Y("offsetY", 3, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        data object ByRndArgs : OverlayMethodArgClass(), ArgType {
            override val entries = ByRndArgs.entries
            val bkBitmapKeyToIndex = Pair(
                ByRndArgs.BK_BITMAP.key,
                ByRndArgs.BK_BITMAP.index
            )
            val bitmapKeyToIndex = Pair(
                ByRndArgs.BITMAP.key,
                ByRndArgs.BITMAP.index
            )
            enum class ByRndArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BK_BITMAP("bkBitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                BITMAP("bitmap", 1, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
    }
}
