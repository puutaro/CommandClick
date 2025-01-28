package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent.IconType
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.CcDotArt
import com.puutaro.commandclick.util.image_tools.ColorTool
import java.io.File
import kotlin.enums.EnumEntries

object ArbForImageAction {
    suspend fun handle(
        fragment: Fragment,
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
        val context =
            fragment.context
                ?: return Pair(Pair(null, null), null)
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
            is ArbMethodArgClass.MatrixStormArgs -> {
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
                val width = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.widthKeyToDefaultValueStr,
                    where
                ).let { widthStrToErr ->
                    val funcErr = widthStrToErr.second
                        ?: return@let widthStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val xMulti = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.xMultiKeyToDefaultValueStr,
                    where
                ).let { xMultiToErr ->
                    val funcErr = xMultiToErr.second
                        ?: return@let xMultiToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val yMulti = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.yMultiKeyToDefaultValueStr,
                    where
                ).let { yMultiToErr ->
                    val funcErr = yMultiToErr.second
                        ?: return@let yMultiToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val shapeStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.shapeKeyToDefaultValueStr,
                    where
                ).let { shapeStrToErr ->
                    val funcErr = shapeStrToErr.second
                        ?: return@let shapeStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val iconType = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.iconTypeKeyToDefaultValueStr,
                    where
                ).let { iconTypeStrToErr ->
                    val funcErr = iconTypeStrToErr.second
                        ?: return@let iconTypeStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        iconTypeStr ->
                    IconType.entries.firstOrNull {
                        it.name == iconTypeStr
                    }?: IconType.SVG
                }
                val iconColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.iconColorKeyToDefaultValueStr,
                    where
                ).let {
                    colorStrToErr ->
                    val funcErr = colorStrToErr.second
                        ?: return@let colorStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = MatrixStorm.make(
                    context,
                    width,
                    height,
                    xMulti,
                    yMulti,
                    shapeStr,
                    iconType,
                    iconColorStr
                )
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
        }
    }

    private object MatrixStorm {

        suspend fun make(
            context: Context,
            goalWidth: Int,
            goalHeight: Int,
            xMulti: Int,
            yMulti: Int,
            shapeStr: String,
            iconType: IconType,
            iconColorStr: String
        ): Bitmap? {
            val pieceWidth = goalWidth / xMulti
            val pieceHeight = goalHeight / yMulti
            val pieceBitmap = makePieceBitmap(
                context,
                pieceWidth,
                pieceHeight,
                shapeStr,
                iconType,
                iconColorStr,
            )
            return CcDotArt.makeMatrixStorm(
                pieceBitmap,
                xMulti,
                yMulti,
            )
        }

        private fun makePieceBitmap(
            context: Context,
            pieceWidth: Int,
            pieceHeight: Int,
            shapeStr: String,
            iconType: EditComponent.IconType,
            iconColorStr: String,
        ): Bitmap {
            return let {
                if(
                    File(shapeStr).isFile
                ) return@let BitmapTool.convertFileToBitmap(shapeStr)?.let {
                    Bitmap.createScaledBitmap(
                        it,
                        pieceWidth,
                        pieceHeight,
                        true,
                    )
                }
                val shape = CmdClickIcons.entries.firstOrNull {
                    it.str == shapeStr
                } ?: CmdClickIcons.RECT
                return@let when(iconType){
                    EditComponent.IconType.IMG -> {
                        val iconFile = ExecSetToolbarButtonImage.getImageFile(
                            shape.assetsPath
                        )
                        BitmapTool.convertFileToBitmap(iconFile.absolutePath)?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                pieceWidth,
                                pieceHeight,
                                true,
                            )
                        }
                    }
                    EditComponent.IconType.SVG -> {
                        AppCompatResources.getDrawable(
                            context,
                            shape.id,
                        )?.toBitmap(
                            pieceWidth,
                            pieceHeight
                        )?.let convertBlack@ {
                            val bitmap = BitmapTool.ImageTransformer.convertBlackToColor(
                                it,
                                iconColorStr
                            )
                            bitmap
                        }
                    }
                }
            } ?: BitmapTool.ImageTransformer.makeRect(
                iconColorStr,
                pieceWidth,
                pieceHeight
            )
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ArbMethodArgClass,
    ){
        MATRIX_STORM("matrixStorm", ArbMethodArgClass.MatrixStormArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ArbMethodArgClass {
        data object MatrixStormArgs : ArbMethodArgClass(), ArgType {
            override val entries = MatrixStormEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.WIDTH.key,
                MatrixStormEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.HEIGHT.key,
                MatrixStormEnumArgs.HEIGHT.defaultValueStr
            )
            val xMultiKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.X_MULTI.key,
                MatrixStormEnumArgs.X_MULTI.defaultValueStr
            )
            val yMultiKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.Y_MULTI.key,
                MatrixStormEnumArgs.Y_MULTI.defaultValueStr
            )
            val shapeKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.SHAPE.key,
                MatrixStormEnumArgs.SHAPE.defaultValueStr
            )
            val iconColorKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.ICON_COLOR.key,
                MatrixStormEnumArgs.ICON_COLOR.defaultValueStr
            )
            val iconTypeKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.ICON_TYPE.key,
                MatrixStormEnumArgs.ICON_TYPE.defaultValueStr
            )
            private const val widthSrc = 300
            private const val heightSrc = widthSrc * 2
            private const val xMultiSrc = 60
            private const val yMultiSrc = xMultiSrc * 2
            enum class MatrixStormEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                WIDTH("width", widthSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", heightSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                X_MULTI("xMulti", xMultiSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                Y_MULTI("yMulti", yMultiSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                SHAPE("shape", CmdClickIcons.RECT.str, FuncCheckerForSetting.ArgType.STRING),
                ICON_TYPE("iconType", IconType.SVG.name, FuncCheckerForSetting.ArgType.STRING),
                ICON_COLOR("iconColor", ColorTool.convertColorToHex(Color.BLACK), FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}