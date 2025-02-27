package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.ShapeOverlayAssetsImages
import kotlin.enums.EnumEntries

object ShapeOverlayForImageAction {

    private const val defaultZeroMacroStr = 0

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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        val args =
            methodNameClass.args
        return when(args){
            is StrBkMethodArgClass.MakeArgs -> {
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
                val shapeAssetsPath = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.shapeKeyToDefaultValueStr,
                    where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    shapeStr ->
                    StrBkMethodArgClass.MakeArgs.Shape.entries.firstOrNull {
                        it.name == shapeStr
                    } ?: StrBkMethodArgClass.MakeArgs.Shape.HORIZON
                }.path
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
                val returnBitmap = AssetsFileManager.assetsByteArray(
                    context,
                    shapeAssetsPath
                )?.let {
                  val innerBitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    val innerBitmapWidth = innerBitmap.width
                    val isDefaultWidth = innerBitmapWidth == defaultZeroMacroStr
                            || innerBitmapWidth == width
                    val innerBitmapHeight = innerBitmap.height
                    val isDefaultHeight = innerBitmapHeight == defaultZeroMacroStr
                            || innerBitmapHeight == height
                    if(
                        isDefaultWidth
                        && isDefaultHeight
                    ) return@let innerBitmap
                   val resizeBitmapWidth = if (isDefaultWidth) {
                       innerBitmapWidth
                       } else width
                    val resizeBitmapHeight = if (isDefaultHeight) {
                        innerBitmapHeight
                    } else height
                    Bitmap.createScaledBitmap(
                        innerBitmap,
                        resizeBitmapWidth,
                        resizeBitmapHeight,
                        true,
                    )
                }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: StrBkMethodArgClass,
    ){
        MAKE("make", StrBkMethodArgClass.MakeArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class StrBkMethodArgClass {

        val defaultZeroMacroStr = 0.toString()
        data object MakeArgs : StrBkMethodArgClass(), ArgType {
            override val entries = MakeArgs.entries
            val shapeKeyToDefaultValueStr = Pair(
                MakeArgs.SHAPE.key,
                MakeArgs.SHAPE.defaultValueStr
            )

            val widthKeyToDefaultValueStr = Pair(
                MakeArgs.WIDTH.key,
                MakeArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                MakeArgs.HEIGHT.key,
                MakeArgs.HEIGHT.defaultValueStr
            )
            enum class MakeArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                SHAPE("shape", Shape.HORIZON.name, FuncCheckerForSetting.ArgType.STRING),
                WIDTH("width", defaultZeroMacroStr, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", defaultZeroMacroStr, FuncCheckerForSetting.ArgType.INT),
            }

            enum class Shape(val path: String){
                HORIZON(ShapeOverlayAssetsImages.fannelLineHorizonBkPngPath),
                VERTICAL(ShapeOverlayAssetsImages.fannelLineVerticalPngPath),
                ORTHOGONAL(ShapeOverlayAssetsImages.fannelLineOrthogonalPngPath),
                SF_RECT(ShapeOverlayAssetsImages.fannelSfRectPngPath),
                DIAMOND(ShapeOverlayAssetsImages.fannelDiamondPngPath),
                RECT_ROTATE_GRAD(ShapeOverlayAssetsImages.fannelRectRotateGradPngPath),
            }
        }

    }
}