package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlin.enums.EnumEntries

object ImageToolForImageAction {
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
            is BitmapArtMethodArgClass.TrimEdgeArgs -> {
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
                ).let { bitmapStrToErr ->
                    val funcErr = bitmapStrToErr.second
                        ?: return@let bitmapStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val trimSize = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.trimSizeKeyToIndex,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL,
                    ) to funcErr
                }

                val returnBitmap = InnerImageTool.trimEdge(
                    bitmap,
                    trimSize,
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
                    null,
                ) to null
            }
        }
    }

    private object InnerImageTool {
        suspend fun trimEdge(
            bitmap: Bitmap,
            trimSize: Int,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                BitmapTool.ImageTransformer.trimEdge(
                    bitmap,
                    trimSize
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
        TRIM_EDGE("trimEdge", BitmapArtMethodArgClass.TrimEdgeArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class BitmapArtMethodArgClass {
        data object TrimEdgeArgs : BitmapArtMethodArgClass(), ArgType {
            override val entries = MatrixStormEnumArgs.entries
            val bitmapKeyToIndex = Pair(
                MatrixStormEnumArgs.BITMAP.key,
                MatrixStormEnumArgs.BITMAP.index,
            )
            val trimSizeKeyToIndex = Pair(
                MatrixStormEnumArgs.TRIM_SIZE.key,
                MatrixStormEnumArgs.TRIM_SIZE.index
            )
            enum class MatrixStormEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                TRIM_SIZE("trimSize", 1, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}