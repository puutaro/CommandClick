package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlin.enums.EnumEntries

object ConcatForImageAction {
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
            is ConcatMethodArgClass.HorizonArgs -> {
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
                val leftBitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.leftBitmapKeyToIndex,
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
                val rightBitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.rightBitmapKeyToIndex,
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
                val dupInt = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.dupKeyToIndex,
                    where
                ).let { dupToErr ->
                    val funcErr = dupToErr.second
                        ?: return@let dupToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    dupInt ->
                    if(dupInt <= 0) return@let 0
                    dupInt
                }
                val returnBitmap = Concat.horizon(
                    leftBitmap,
                    rightBitmap,
                    dupInt,
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

    private object Concat {
        fun horizon(
            leftBitmap: Bitmap,
            rightBitmap: Bitmap,
            dup: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val overlayBitmap = BitmapTool.concatByHorizon(
                    leftBitmap,
                    rightBitmap,
                    dup,
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
        val args: ConcatMethodArgClass,
    ){
        HORIZON("horizon", ConcatMethodArgClass.HorizonArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class ConcatMethodArgClass {
        data object HorizonArgs : ConcatMethodArgClass(), ArgType {
            override val entries = HorizonArgs.entries
            val leftBitmapKeyToIndex = Pair(
                HorizonArgs.LEFT_BITMAP.key,
                HorizonArgs.LEFT_BITMAP.index
            )
            val rightBitmapKeyToIndex = Pair(
                HorizonArgs.RIGHT_BITMAP.key,
                HorizonArgs.RIGHT_BITMAP.index
            )
            val dupKeyToIndex = Pair(
                HorizonArgs.DUP.key,
                HorizonArgs.DUP.index
            )
            enum class HorizonArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                LEFT_BITMAP("leftBitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                RIGHT_BITMAP("rightBitmap", 1, FuncCheckerForSetting.ArgType.BITMAP),
                DUP("dup", 2, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}
