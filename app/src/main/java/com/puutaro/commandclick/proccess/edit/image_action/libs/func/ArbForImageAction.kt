package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ArbForImageAction.ArbMethodArgClass.IconsArgs.IconsEnumArgs
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.CcDotArt
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.enums.EnumEntries

object ArbForImageAction {

    private const val transparentColorStr = "#00000000"
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
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.pieceKeyToDefaultValueStr,
                    where
                ).let { pieceToErr ->
                    val funcErr = pieceToErr.second
                        ?: return@let pieceToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val pieceMapArray =
                    PieceShapeManager.makePieceMapArray(argsPairList)

//                val shapeStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.shapeKeyToDefaultValueStr,
//                    where
//                ).let { shapeStrToErr ->
//                    val funcErr = shapeStrToErr.second
//                        ?: return@let shapeStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
//                val iconType = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.iconTypeKeyToDefaultValueStr,
//                    where
//                ).let { iconTypeStrToErr ->
//                    val funcErr = iconTypeStrToErr.second
//                        ?: return@let iconTypeStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                        iconTypeStr ->
//                    IconType.entries.firstOrNull {
//                        it.name == iconTypeStr
//                    }?: IconType.SVG
//                }
//                val iconColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.iconColorKeyToDefaultValueStr,
//                    where
//                ).let {
//                    colorStrToErr ->
//                    val funcErr = colorStrToErr.second
//                        ?: return@let colorStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                        colorStr ->
//                    ColorTool.parseColorStr(
//                        context,
//                        colorStr,
//                        args.iconColorKeyToDefaultValueStr.first,
//                        where,
//                    )
//                }
                val xDup = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.xDupKeyToDefaultValueStr,
                    where
                ).let { xDupToErr ->
                    val funcErr = xDupToErr.second
                        ?: return@let xDupToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val yDup = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.yDupKeyToDefaultValueStr,
                    where
                ).let { yDupToErr ->
                    val funcErr = yDupToErr.second
                        ?: return@let yDupToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
//                val pieceRotate = FuncCheckerForSetting.Getter.getFloatFromArgMapByName(
//                    mapArgMapList,
//                    args.pieceRotateKeyToDefaultValueStr,
//                    where
//                ).let { pieceRotateToErr ->
//                    val funcErr = pieceRotateToErr.second
//                        ?: return@let pieceRotateToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
                val returnBitmap = MatrixStorm.make(
                    context,
                    pieceMapArray,
                    width,
                    height,
                    xMulti,
                    yMulti,
//                    shapeStr,
//                    iconType,
//                    iconColorStr,
                    xDup,
                    yDup,
//                    pieceRotate,
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
            is ArbMethodArgClass.IconsArgs -> {
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
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.pieceKeyToDefaultValueStr,
                    where
                ).let { pieceToErr ->
                    val funcErr = pieceToErr.second
                        ?: return@let pieceToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val pieceMapArray =
                    PieceShapeManager.makePieceMapArray(argsPairList)

//                val pieceOneSide = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
//                    mapArgMapList,
//                    args.pieceOneSideKeyToDefaultValueStr,
//                    where
//                ).let { pieceOneSideToErr ->
//                    val funcErr = pieceOneSideToErr.second
//                        ?: return@let pieceOneSideToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
                val startAngle = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.startAngleKeyToDefaultValueStr,
                    where
                ).let { startAngleToErr ->
                    val funcErr = startAngleToErr.second
                        ?: return@let startAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val endAngle = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.endAngleKeyToDefaultValueStr,
                    where
                ).let { endAngleToErr ->
                    val funcErr = endAngleToErr.second
                        ?: return@let endAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
//                val shapeStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.shapeKeyToDefaultValueStr,
//                    where
//                ).let { shapeStrToErr ->
//                    val funcErr = shapeStrToErr.second
//                        ?: return@let shapeStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
//                val iconType = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.iconTypeKeyToDefaultValueStr,
//                    where
//                ).let { iconTypeStrToErr ->
//                    val funcErr = iconTypeStrToErr.second
//                        ?: return@let iconTypeStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                        iconTypeStr ->
//                    IconType.entries.firstOrNull {
//                        it.name == iconTypeStr
//                    }?: IconType.SVG
//                }
//                val iconColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.iconColorKeyToDefaultValueStr,
//                    where
//                ).let {
//                        colorStrToErr ->
//                    val funcErr = colorStrToErr.second
//                        ?: return@let colorStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                        colorStr ->
//                    ColorTool.parseColorStr(
//                        context,
//                        colorStr,
//                        args.iconColorKeyToDefaultValueStr.first,
//                        where,
//                    )
//                }
                val bkColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.bkColorKeyToDefaultValueStr,
                    where
                ).let {
                        colorStrToErr ->
                    val funcErr = colorStrToErr.second
                        ?: return@let colorStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                        colorStr ->
                    if(
                        colorStr == transparentColorStr
                    ) return@let transparentColorStr
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.bkColorKeyToDefaultValueStr.first,
                        where,
                    )
                }
                val layout = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.layoutKeyToDefaultValueStr,
                    where
                ).let { layoutStrToErr ->
                    val funcErr = layoutStrToErr.second
                        ?: return@let layoutStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    layoutStr ->
                    ArbMethodArgClass.IconsArgs.Layout.entries.firstOrNull {
                        it.name == layoutStr
                    }?: ArbMethodArgClass.IconsArgs.Layout.LEFT
                }
                val returnBitmap = RndIcons.make(
                    context,
                    args,
                    argsPairList,
                    pieceMapArray,
                    width,
                    height,
//                    pieceOneSide,
                    startAngle,
                    endAngle,
                    times,
//                    shapeStr,
//                    iconType,
//                    iconColorStr,
                    bkColorStr,
                    layout,
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
            is ArbMethodArgClass.StringsArgs -> {
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
//                val pieceWidthFloat = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
//                    mapArgMapList,
//                    args.pieceWidthKeyToDefaultValueStr,
//                    where
//                ).let { pieceWidthToErr ->
//                    val funcErr = pieceWidthToErr.second
//                        ?: return@let pieceWidthToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.toFloat()
//                val pieceHeightFloat = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
//                    mapArgMapList,
//                    args.pieceHeightKeyToDefaultValueStr,
//                    where
//                ).let { pieceHeightToErr ->
//                    val funcErr = pieceHeightToErr.second
//                        ?: return@let pieceHeightToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.toFloat()
                val startAngle = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.startAngleKeyToDefaultValueStr,
                    where
                ).let { startAngleToErr ->
                    val funcErr = startAngleToErr.second
                        ?: return@let startAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val endAngle = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.endAngleKeyToDefaultValueStr,
                    where
                ).let { endAngleToErr ->
                    val funcErr = endAngleToErr.second
                        ?: return@let endAngleToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val times = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timesKeyToDefaultValueStr,
                    where
                ).let { timesToErr ->
                    val funcErr = timesToErr.second
                        ?: return@let timesToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
//                val string = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.stringKeyToDefaultValueStr,
//                    where
//                ).let { stringToErr ->
//                    val funcErr = stringToErr.second
//                        ?: return@let stringToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
//                val fontSizeFloat = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
//                    mapArgMapList,
//                    args.fontSizeKeyToDefaultValueStr,
//                    where
//                ).let { fontSizeToErr ->
//                    val funcErr = fontSizeToErr.second
//                        ?: return@let fontSizeToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.toFloat()
//                val fontType = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.fontTypeKeyToDefaultValueStr,
//                    where
//                ).let { fontTypeToErr ->
//                    val funcErr = fontTypeToErr.second
//                        ?: return@let fontTypeToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                    fontTypeStr ->
//                    EditComponent.Font.entries.firstOrNull {
//                        it.key == fontTypeStr
//                    }?.typeface ?: EditComponent.Font.SANS_SERIF.typeface
//                }
//                val fontStyle = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.fontStyleKeyToDefaultValueStr,
//                    where
//                ).let { fontStyleToErr ->
//                    val funcErr = fontStyleToErr.second
//                        ?: return@let fontStyleToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                    fontStyleStr ->
//                    EditComponent.Template.TextManager.TextStyle.entries.firstOrNull {
//                        it.key == fontStyleStr
//                    }?.style ?: EditComponent.Template.TextManager.TextStyle.NORMAL.style
//                }
//                val fontColor = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.fontColorKeyToDefaultValueStr,
//                    where
//                ).let { colorStrToErr ->
//                    val funcErr = colorStrToErr.second
//                        ?: return@let colorStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                        colorStr ->
//                    ColorTool.parseColorStr(
//                        context,
//                        colorStr,
//                        args.fontColorKeyToDefaultValueStr.first,
//                        where,
//                    )
//                }.let {
//                    Color.parseColor(it)
//                }
                val bkColorStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.bkColorKeyToDefaultValueStr,
                    where
                ).let { colorStrToErr ->
                    val funcErr = colorStrToErr.second
                        ?: return@let colorStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    colorStr ->
                    if(
                        colorStr == transparentColorStr
                    ) return@let transparentColorStr
                    ColorTool.parseColorStr(
                        context,
                        colorStr,
                        args.bkColorKeyToDefaultValueStr.first,
                        where,
                    )
                }
//                val strokeWidthFloat = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
//                    mapArgMapList,
//                    args.strokeWidthKeyToDefaultValueStr,
//                    where
//                ).let { strokeWidthToErr ->
//                    val funcErr = strokeWidthToErr.second
//                        ?: return@let strokeWidthToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.toFloat()
//                val strokeColor = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.strokeColorKeyToDefaultValueStr,
//                    where
//                ).let { colorStrToErr ->
//                    val funcErr = colorStrToErr.second
//                        ?: return@let colorStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.let {
//                        colorStr ->
//                    ColorTool.parseColorStr(
//                        context,
//                        colorStr,
//                        args.strokeColorKeyToDefaultValueStr.first,
//                        where,
//                    )
//                }.let {
//                    Color.parseColor(it)
//                }
//                val letterSpacingFloat = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
//                    mapArgMapList,
//                    args.letterSpacingKeyToDefaultValueStr,
//                    where
//                ).let { letterSpacingToErr ->
//                    val funcErr = letterSpacingToErr.second
//                        ?: return@let letterSpacingToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }.toFloat()
                val layout = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.layoutKeyToDefaultValueStr,
                    where
                ).let { layoutToErr ->
                    val funcErr = layoutToErr.second
                        ?: return@let layoutToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    layoutStr ->
                    ArbMethodArgClass.StringsArgs.Layout.entries.firstOrNull {
                        it.name == layoutStr
                    } ?: ArbMethodArgClass.StringsArgs.Layout.LEFT
                }
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.pieceKeyToDefaultValueStr,
                    where
                ).let { pieceToErr ->
                    val funcErr = pieceToErr.second
                        ?: return@let pieceToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val pieceStrMapArray =
                    PieceStringManager.makePieceMapArray(argsPairList)

                val returnBitmap = RndStrings.make(
                    context,
                    pieceStrMapArray,
                    width,
                    height,
//                    pieceWidthFloat,
//                    pieceHeightFloat,
                    startAngle,
                    endAngle,
                    times,
//                    string,
//                    fontSizeFloat,
//                    fontType,
//                    fontStyle,
//                    fontColor,
                    bkColorStr,
//                    strokeColor,
//                    strokeWidthFloat,
//                    letterSpacingFloat,
                    layout,
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

    private object RndStrings {
        fun make(
            context: Context,
            pieceStrMapArray: Array<Map<String, String>>,
            baseWidth: Int,
            baseHeight: Int,
//            pieceWidthFloat: Float,
//            pieceHeightFloat: Float,
            startAngle: Int,
            endAngle: Int,
            times: Int,
//            string: String,
//            fontSizeFloat: Float,
//            fontType: Typeface,
//            fontStyle: Int,
//            fontColor: Int,
            bkColorStr: String,
//            strokeColor: Int,
//            strokeWidthFloat: Float,
//            letterSpacingFloat: Float,
            layout: ArbMethodArgClass.StringsArgs.Layout,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lstringsp.txt").absolutePath,
//                listOf(
//                    "string: $string",
//                    "pieceWidthFloat: $pieceWidthFloat",
//                    "pieceHeightFloat: $pieceHeightFloat",
//                    null,
//                    "fontSizeFloat: $fontSizeFloat",
//                    "fontColor: $fontColor",
//                    "strokeColor: $strokeColor",
//                    "strokeWidthFloat: $strokeWidthFloat",
//                    null,
//                    "letterSpacingFloat: $letterSpacingFloat",
//                    "fontType: $fontType",
//                    "fontStyle: $fontStyle",
//                    "baseWidth: $baseWidth",
//                    "baseHeight: $baseHeight",
//                    "bkColorStr: $bkColorStr",
//                    "times: $times",
//                    "layout: ${layout}",
//                ).joinToString("\n"),
//            )
            return try {
                val stringBitmapList = pieceStrMapArray.map {
                    pieceMap ->
                    val string = PieceStringManager.getString(pieceMap)
                    val pieceWidthFloat = PieceStringManager.getWidth(pieceMap)
                    val pieceHeightFloat = PieceStringManager.getHeight(pieceMap)
                    val fontSizeFloat = PieceStringManager.getFontSize(pieceMap)
                    val fontColor = PieceStringManager.getFontColor(
                        context,
                        pieceMap,
                        where
                    ).let {
                        Color.parseColor(it)
                    }
                    val strokeColor = PieceStringManager.getStrokeColor(
                        context,
                        pieceMap,
                        where
                    ).let {
                        Color.parseColor(it)
                    }
                    val strokeWidthFloat = PieceStringManager.getStrokeWidth(pieceMap)
                    val letterSpacingFloat = PieceStringManager.getLetterSpacingWidth(pieceMap)
                    val fontType = PieceStringManager.getFontType(pieceMap)
                    val fontStyle = PieceStringManager.getFontStyle(pieceMap)
                    BitmapTool.DrawText.drawTextToBitmap(
                        string,
                        pieceWidthFloat,
                        pieceHeightFloat,
                        null,
                        fontSizeFloat,
                        fontColor,
                        strokeColor,
                        strokeWidthFloat,
                        null,
                        letterSpacingFloat,
                        font = Typeface.create(
                            fontType,
                            fontStyle
                        ),
                        isAntiAlias = true,
                    ).let {
                        val cutWidth = (pieceWidthFloat * 0.8).toInt()
                        val cutHeight = (pieceHeightFloat * 0.8).toInt()
                        BitmapTool.ImageTransformer.cutCenter2(
                            it,
                            cutWidth,
                            cutHeight
                        )
                    }
                }.toTypedArray()
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "lstring.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(
//                    stringBitmap
//                )
//            )
                val autoRndStringsBitmap = when (layout) {
                    ArbMethodArgClass.StringsArgs.Layout.LEFT -> {
                        CcDotArt.MistMaker.makeLeftRndBitmaps(
                            baseWidth,
                            baseHeight,
                            stringBitmapList,
                            startAngle,
                            endAngle,
                            times
                        ).let {
                            val cutWidth = (baseWidth * 0.8).toInt()
                            val cutHeight = (baseHeight * 0.8).toInt()
                            BitmapTool.ImageTransformer.cutByTarget(
                                it,
                                cutWidth,
                                cutHeight,
                                it.width - cutWidth,
                                (it.height - cutHeight) / 2
                            )
                        }
                    }

                    ArbMethodArgClass.StringsArgs.Layout.RND -> {
                        CcDotArt.MistMaker.makeRndBitmap(
                            baseWidth,
                            baseHeight,
                            bkColorStr,
                            stringBitmapList,
                            times
                        )
//                        .let {
//                        val cutWidth = (baseWidth * 0.8).toInt()
//                        val cutHeight = (baseHeight * 0.8).toInt()
//                        BitmapTool.ImageTransformer.cutCenter2(
//                            it,
//                            cutWidth,
//                            cutHeight,
//                        )
                    }
                }
                autoRndStringsBitmap to null
            } catch (e: Exception){
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
    }

    private object RndIcons {
        suspend fun make(
            context: Context,
            args: ArbMethodArgClass.IconsArgs,
            argsPairList: List<Pair<String, String>>,
            pieceMapList:Array<Map<String, String>>,
            width: Int,
            height: Int,
//            pieceOneSide: Int,
            startAngle: Int,
            endAngle: Int,
            times: Int,
//            shapeStr: String,
//            iconType: IconType,
//            iconColorStr: String,
            bkColorStr: String,
            layout: ArbMethodArgClass.IconsArgs.Layout,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return  try {
                val pieceBitmapArray = withContext(Dispatchers.IO) {
                    val pieceBitmapArrayJob = pieceMapList.mapIndexed { index, pieceMap ->
                        async {
                            val pieceOneSide = PieceShapeManager.getOneSide(
                                pieceMap
                            )
                            val pieceWidth = PieceShapeManager.getWidth(
                                pieceMap
                            ) ?: pieceOneSide
                            val pieceHeight = PieceShapeManager.getHeight(
                                pieceMap
                            ) ?: pieceOneSide
                            val shapeStr = PieceShapeManager.getShape(
                                pieceMap
                            )
                            val iconType = PieceShapeManager.getType(
                                pieceMap
                            )
                            val iconColorStr = PieceShapeManager.getColor(
                                context,
                                pieceMap,
                                where
                            )
                            val pieceBitmap = makePieceBitmap(
                                context,
                                pieceWidth,
                                pieceHeight,
                                shapeStr,
                                iconType,
                                iconColorStr,
                            )
                            index to pieceBitmap
                        }
                    }
                    pieceBitmapArrayJob.awaitAll()
                        .sortedBy { it.first }
                        .map {
                            it.second
                        }.toTypedArray()
                }
//            if(endAngle == 0)
//            FileSystems.writeFromByteArray(
//                File(UsePath.cmdclickDefaultAppDirPath, "rpiece.png").absolutePath,
//                BitmapTool.convertBitmapToByteArray(
//                    pieceBitmap
//                )
//            )

                val autoRndIcons = when (layout) {
                    ArbForImageAction.ArbMethodArgClass.IconsArgs.Layout.LEFT -> {
                        CcDotArt.MistMaker.makeLeftRndBitmaps(
                            width,
                            height,
                            pieceBitmapArray,
                            startAngle,
                            endAngle,
                            times
                        ).let {
                            val cutWidth = (width * 0.8).toInt()
                            val cutHeight = (height * 0.8).toInt()
                            BitmapTool.ImageTransformer.cutByTarget(
                                it,
                                cutWidth,
                                cutHeight,
                                it.width - cutWidth,
                                (it.height - cutHeight) / 2
                            )
                        }
                    }

                    ArbForImageAction.ArbMethodArgClass.IconsArgs.Layout.RND -> {
                        CcDotArt.MistMaker.makeRndBitmap(
                            width,
                            height,
                            bkColorStr,
                            pieceBitmapArray,
                            times
                        )
                    }

                    ArbForImageAction.ArbMethodArgClass.IconsArgs.Layout.DIAGONAL ->
                        CcDotArt.MistMaker.makeRndDiagonalBitmap(
                            width,
                            height,
                            bkColorStr,
                            pieceBitmapArray,
                            times
                        )

                    ArbForImageAction.ArbMethodArgClass.IconsArgs.Layout.PIVOT_VERTICAL -> {
                        val basePivotX = let basePivotX@{
                            val pivotXKey = args.pivotXKeyToDefaultValueStr.first
                            PairListTool.getValue(
                                argsPairList,
                                pivotXKey
                            ).let { pivotXSrc ->
                                if (
                                    pivotXSrc == null
                                    || pivotXSrc == args.pivotXDeafultValue.toString()
                                ) return@let null
                                pivotXSrc.toInt()
                            }
                        }
                        val xRndDeno = let xRndDeno@{
                            val xRndDenoKey = args.xRndDenoKeyToDefaultValueStr.first
                            PairListTool.getValue(
                                argsPairList,
                                xRndDenoKey
                            ).let { xRndDenoSrc ->
                                if (
                                    xRndDenoSrc == null
                                    || xRndDenoSrc == args.xRndDenoDeafultValue.toString()
                                ) return@let null
                                xRndDenoSrc.toInt()
                            }
                        }
                        val yRange = let basePivotX@{
                            val yRangeKey = args.yRangeKeyToDefaultValueStr.first
                            PairListTool.getValue(
                                argsPairList,
                                yRangeKey
                            ).let { yRangeSrc ->
                                if (
                                    yRangeSrc == null
                                    || yRangeSrc == args.yRangeDeafultValue.toString()
                                ) return@let null
                                yRangeSrc.toInt()
                            }
                        }
                        val pivotInWidthTimes = let basePivotX@{
                            val yRangeKey = args.pivotInWidthTimsKeyToDefaultValueStr.first
                            PairListTool.getValue(
                                argsPairList,
                                yRangeKey
                            )?.let { pivotInWidthTimesSrc ->
                                try {
                                    pivotInWidthTimesSrc.toInt()
                                } catch (e: Exception) {
                                    null
                                }
                            } ?: 0
                        }
                        FileSystems.updateFile(
                            File(UsePath.cmdclickDefaultAppDirPath, "larb.txt").absolutePath,
                            listOf(
                                "xRndDeno: ${xRndDeno}",
                                "yRange: ${yRange}",
                                "isPivotInWidth: ${pivotInWidthTimes}",
                            ).joinToString("\n\n") + "\n\n=====\n\n"
                        )
                        CcDotArt.MistMaker.makePivotVerticalBitmap(
                            width,
                            height,
                            bkColorStr,
                            pieceBitmapArray,
                            times,
                            basePivotX,
                            xRndDeno,
                            yRange,
                            pivotInWidthTimes,
                        )
                    }
                }
                autoRndIcons to null
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
    }

    private object MatrixStorm {

        suspend fun make(
            context: Context,
            pieceMapArray: Array<Map<String, String>>,
            goalWidth: Int,
            goalHeight: Int,
            xMulti: Int,
            yMulti: Int,
//            shapeStr: String,
//            iconType: IconType,
//            iconColorStr: String,
            xDup: Int,
            yDup: Int,
//            pieceRotate: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
                val pieceWidth = goalWidth / xMulti
                val pieceHeight = goalHeight / yMulti
                val pieceBitmapArray = withContext(Dispatchers.IO) {
                    val pieceBitmapArrayJob = pieceMapArray.mapIndexed { index, pieceMap ->
                        async {
                            val shapeStr = PieceShapeManager.getShape(
                                pieceMap
                            )
                            val iconType = PieceShapeManager.getType(
                                pieceMap
                            )
                            val iconColorStr = PieceShapeManager.getColor(
                                context,
                                pieceMap,
                                where
                            )
                            val pieceRotate = PieceShapeManager.getRotate(
                                pieceMap,
                            )
                            index to makePieceBitmap(
                                context,
                                pieceWidth,
                                pieceHeight,
                                shapeStr,
                                iconType,
                                iconColorStr,
                            ).let { piece ->
                                if (
                                    pieceRotate == 0f
                                ) return@let piece
                                BitmapTool.rotate(
                                    piece,
                                    pieceRotate,
                                )
                            }
                        }
                    }
                    pieceBitmapArrayJob.awaitAll()
                        .asSequence()
                        .sortedBy { it.first }
                        .map { it.second }
                        .toList()
                }.toTypedArray()
                CcDotArt.makeMatrixStorm(
                    pieceBitmapArray,
                    xMulti,
                    yMulti,
                    xDup,
                    yDup,
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


    private suspend fun makePieceBitmap(
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

    private enum class MethodNameClass(
        val str: String,
        val args: ArbMethodArgClass,
    ){
        MATRIX_STORM("matrixStorm", ArbMethodArgClass.MatrixStormArgs),
        ICONS("icons", ArbMethodArgClass.IconsArgs),
        STRINGS("strings", ArbMethodArgClass.StringsArgs),
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
//            val shapeKeyToDefaultValueStr = Pair(
//                MatrixStormEnumArgs.SHAPE.key,
//                MatrixStormEnumArgs.SHAPE.defaultValueStr
//            )
//            val iconColorKeyToDefaultValueStr = Pair(
//                MatrixStormEnumArgs.ICON_COLOR.key,
//                MatrixStormEnumArgs.ICON_COLOR.defaultValueStr
//            )
//            val iconTypeKeyToDefaultValueStr = Pair(
//                MatrixStormEnumArgs.ICON_TYPE.key,
//                MatrixStormEnumArgs.ICON_TYPE.defaultValueStr
//            )
            val xDupKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.X_DUP.key,
                MatrixStormEnumArgs.X_DUP.defaultValueStr
            )
            val yDupKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.Y_DUP.key,
                MatrixStormEnumArgs.Y_DUP.defaultValueStr
            )
//            val pieceRotateKeyToDefaultValueStr = Pair(
//                MatrixStormEnumArgs.PIECE_ROTATE.key,
//                MatrixStormEnumArgs.PIECE_ROTATE.defaultValueStr
//            )
            val pieceKeyToDefaultValueStr = Pair(
                MatrixStormEnumArgs.PIECE.key,
                MatrixStormEnumArgs.PIECE.defaultValueStr
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
                PIECE("piece", String(), FuncCheckerForSetting.ArgType.STRING),
//                SHAPE("shape", CmdClickIcons.RECT.str, FuncCheckerForSetting.ArgType.STRING),
//                ICON_TYPE("iconType", IconType.SVG.name, FuncCheckerForSetting.ArgType.STRING),
//                ICON_COLOR("iconColor", ColorTool.convertColorToHex(Color.BLACK), FuncCheckerForSetting.ArgType.STRING),
                X_DUP("xDup", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                Y_DUP("yDup", 0.toString(), FuncCheckerForSetting.ArgType.INT),
//                PIECE_ROTATE("pieceRotate", 0.toString(), FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
        object IconsArgs: ArbMethodArgClass(), ArgType {
            override val entries = IconsEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                IconsEnumArgs.WIDTH.key,
                IconsEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                IconsEnumArgs.HEIGHT.key,
                IconsEnumArgs.HEIGHT.defaultValueStr
            )
            val startAngleKeyToDefaultValueStr = Pair(
                IconsEnumArgs.START_ANGLE.key,
                IconsEnumArgs.START_ANGLE.defaultValueStr
            )
            val endAngleKeyToDefaultValueStr = Pair(
                IconsEnumArgs.END_ANGLE.key,
                IconsEnumArgs.END_ANGLE.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                IconsEnumArgs.TIMES.key,
                IconsEnumArgs.TIMES.defaultValueStr
            )
            val pieceKeyToDefaultValueStr = Pair(
                IconsEnumArgs.PIECE.key,
                IconsEnumArgs.PIECE.defaultValueStr
            )
//            val pieceOneSideKeyToDefaultValueStr = Pair(
//                IconsEnumArgs.PIECE_ONE_SIDE.key,
//                IconsEnumArgs.PIECE_ONE_SIDE.defaultValueStr
//            )
//            val shapeKeyToDefaultValueStr = Pair(
//                IconsEnumArgs.SHAPE.key,
//                IconsEnumArgs.SHAPE.defaultValueStr
//            )
//            val iconColorKeyToDefaultValueStr = Pair(
//                IconsEnumArgs.ICON_COLOR.key,
//                IconsEnumArgs.ICON_COLOR.defaultValueStr
//            )
//            val iconTypeKeyToDefaultValueStr = Pair(
//                IconsEnumArgs.ICON_TYPE.key,
//                IconsEnumArgs.ICON_TYPE.defaultValueStr
//            )
            val bkColorKeyToDefaultValueStr = Pair(
                IconsEnumArgs.BK_COLOR.key,
                IconsEnumArgs.BK_COLOR.defaultValueStr
            )
            val layoutKeyToDefaultValueStr = Pair(
                IconsEnumArgs.LAYOUT.key,
                IconsEnumArgs.LAYOUT.defaultValueStr
            )
            val pivotXKeyToDefaultValueStr = Pair(
                IconsEnumArgs.PIVOT_X.key,
                IconsEnumArgs.PIVOT_X.defaultValueStr
            )
            val xRndDenoKeyToDefaultValueStr = Pair(
                IconsEnumArgs.X_RND_DENO.key,
                IconsEnumArgs.X_RND_DENO.defaultValueStr
            )
            val yRangeKeyToDefaultValueStr = Pair(
                IconsEnumArgs.Y_RANGE.key,
                IconsEnumArgs.Y_RANGE.defaultValueStr
            )
            val pivotInWidthTimsKeyToDefaultValueStr = Pair(
                IconsEnumArgs.PIVOT_IN_WIDTH_TIMES.key,
                IconsEnumArgs.PIVOT_IN_WIDTH_TIMES.defaultValueStr
            )
            const val pivotXDeafultValue = -1
            const val xRndDenoDeafultValue = -1
            const val yRangeDeafultValue = -1
            const val pivotInWidthTimesDefaultValue = 0
            enum class Layout {
                LEFT,
                RND,
                DIAGONAL,
                PIVOT_VERTICAL,
            }
            private const val widthSrc = 300
            private const val heightSrc = widthSrc * 2
            enum class IconsEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                WIDTH("width", widthSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", heightSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                START_ANGLE("startAngle", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                END_ANGLE("endAngle", 180.toString(), FuncCheckerForSetting.ArgType.INT),
                TIMES("times", 10.toString(), FuncCheckerForSetting.ArgType.INT),
                BK_COLOR("bkColor", transparentColorStr, FuncCheckerForSetting.ArgType.STRING),
                LAYOUT("layout", Layout.LEFT.name, FuncCheckerForSetting.ArgType.STRING),
                PIECE("piece", String(), FuncCheckerForSetting.ArgType.STRING),
//                PIECE_ONE_SIDE("pieceOneSide", pieceOneSide.toString(), FuncCheckerForSetting.ArgType.INT),
//                SHAPE("shape", CmdClickIcons.RECT.str, FuncCheckerForSetting.ArgType.STRING),
//                ICON_TYPE("iconType", IconType.SVG.name, FuncCheckerForSetting.ArgType.STRING),
//                ICON_COLOR("iconColor", ColorTool.convertColorToHex(Color.BLACK), FuncCheckerForSetting.ArgType.STRING),
                PIVOT_X("pivotX", pivotXDeafultValue.toString(), FuncCheckerForSetting.ArgType.INT),
                X_RND_DENO("xRndDeno", xRndDenoDeafultValue.toString(), FuncCheckerForSetting.ArgType.INT),
                Y_RANGE("yRange", yRangeDeafultValue.toString(), FuncCheckerForSetting.ArgType.INT),
                PIVOT_IN_WIDTH_TIMES("pivotInWidthTimes", pivotInWidthTimesDefaultValue.toString(), FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object StringsArgs : ArbMethodArgClass(), ArgType {
            override val entries = StringsEnumArgs.entries
            val widthKeyToDefaultValueStr = Pair(
                StringsEnumArgs.WIDTH.key,
                StringsEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                StringsEnumArgs.HEIGHT.key,
                StringsEnumArgs.HEIGHT.defaultValueStr
            )
//            val pieceWidthKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.PIECE_WIDTH.key,
//                StringsEnumArgs.PIECE_WIDTH.defaultValueStr
//            )
//            val pieceHeightKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.PIECE_HEIGHT.key,
//                StringsEnumArgs.PIECE_HEIGHT.defaultValueStr
//            )
            val startAngleKeyToDefaultValueStr = Pair(
                StringsEnumArgs.START_ANGLE.key,
                StringsEnumArgs.START_ANGLE.defaultValueStr
            )
            val endAngleKeyToDefaultValueStr = Pair(
                StringsEnumArgs.END_ANGLE.key,
                StringsEnumArgs.END_ANGLE.defaultValueStr
            )
            val timesKeyToDefaultValueStr = Pair(
                StringsEnumArgs.TIMES.key,
                StringsEnumArgs.TIMES.defaultValueStr
            )
            val bkColorKeyToDefaultValueStr = Pair(
                StringsEnumArgs.BK_COLOR.key,
                StringsEnumArgs.BK_COLOR.defaultValueStr
            )
            val layoutKeyToDefaultValueStr = Pair(
                StringsEnumArgs.LAYOUT.key,
                StringsEnumArgs.LAYOUT.defaultValueStr
            )
            val pieceKeyToDefaultValueStr = Pair(
                StringsEnumArgs.PIECE.key,
                StringsEnumArgs.PIECE.defaultValueStr
            )
//            val stringKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.STRING.key,
//                StringsEnumArgs.STRING.defaultValueStr
//            )
//            val fontSizeKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.FONT_SIZE.key,
//                StringsEnumArgs.FONT_SIZE.defaultValueStr
//            )
//            val fontTypeKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.FONT_TYPE.key,
//                StringsEnumArgs.FONT_TYPE.defaultValueStr
//            )
//            val fontStyleKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.FONT_STYLE.key,
//                StringsEnumArgs.FONT_STYLE.defaultValueStr
//            )
//            val fontColorKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.FONT_COLOR.key,
//                StringsEnumArgs.FONT_COLOR.defaultValueStr
//            )
//            val strokeColorKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.STROKE_COLOR.key,
//                StringsEnumArgs.STROKE_COLOR.defaultValueStr
//            )
//           val strokeWidthKeyToDefaultValueStr = Pair(
//               StringsEnumArgs.STROKE_WIDTH.key,
//               StringsEnumArgs.STROKE_WIDTH.defaultValueStr
//           )
//            val letterSpacingKeyToDefaultValueStr = Pair(
//                StringsEnumArgs.LETTER_SPACING.key,
//                StringsEnumArgs.LETTER_SPACING.defaultValueStr
//            )
//
            private const val widthSrc = 300
            private const val heightSrc = widthSrc * 2
            enum class Layout {
                LEFT,
                RND,
            }
            enum class StringsEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                WIDTH("width", widthSrc.toString(), FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", heightSrc.toString(), FuncCheckerForSetting.ArgType.INT),
//                PIECE_WIDTH("pieceWidth", pieceOneSide.toString(), FuncCheckerForSetting.ArgType.INT),
//                PIECE_HEIGHT("pieceHeight", pieceOneSide.toString(), FuncCheckerForSetting.ArgType.INT),
                START_ANGLE("startAngle", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                END_ANGLE("endAngle", 180.toString(), FuncCheckerForSetting.ArgType.INT),
                TIMES("times", 10.toString(), FuncCheckerForSetting.ArgType.INT),
                PIECE("piece", String(), FuncCheckerForSetting.ArgType.STRING),
//                STRING("string", "C", FuncCheckerForSetting.ArgType.STRING),
//                FONT_SIZE("fontSize", 20.toString(), FuncCheckerForSetting.ArgType.STRING),
//                FONT_TYPE("fontType", Font.SANS_SERIF.key, FuncCheckerForSetting.ArgType.STRING),
//                FONT_STYLE("fontStyle", EditComponent.Template.TextManager.TextStyle.NORMAL.key, FuncCheckerForSetting.ArgType.STRING),
//                FONT_COLOR("fontColor", ColorTool.convertColorToHex(Color.BLACK), FuncCheckerForSetting.ArgType.STRING),
                BK_COLOR("bkColor", transparentColorStr, FuncCheckerForSetting.ArgType.STRING),
//                STROKE_COLOR("strokeColor", ColorTool.convertColorToHex(Color.BLACK), FuncCheckerForSetting.ArgType.STRING),
//                STROKE_WIDTH("strokeWidth", 0.toString(), FuncCheckerForSetting.ArgType.INT),
//                LETTER_SPACING("letterSpacing", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                LAYOUT("layout", Layout.LEFT.name, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }

    object PieceStringManager {

        private const val keySeparator = '|'
        private const val pieceOneSide = 40

        enum class PieceKey(val key: String) {
            STRING("string"),
            WIDTH("width"),
            HEIGHT("height"),
            FONT_SIZE("fontSize"),
            FONT_TYPE("fontType"),
            FONT_STYLE("fontStyle"),
            FONT_COLOR("fontColor"),
            STROKE_COLOR("strokeColor"),
            STROKE_WIDTH("strokeWidth"),
            LETTER_SPACING("letterSpacing"),
        }

        fun makePieceMapArray(
            argsPairList: List<Pair<String, String>>,
        ): Array<Map<String, String>> {
            val pieceKey = ArbMethodArgClass.StringsArgs.StringsEnumArgs.PIECE.key
            return argsPairList.asSequence().filter {
                    (argKey, _) ->
                argKey == pieceKey
            }.map {
                    (_, pieceMapCon) ->
                CmdClickMap.createMap(
                    pieceMapCon,
                    keySeparator
                ).toMap()
            }.toList().toTypedArray()
        }

        fun getWidth(pieceMap: Map<String, String>): Float {
            return try {
                pieceMap.get(
                    PieceKey.WIDTH.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: pieceOneSide.toFloat()
        }
        fun getHeight(pieceMap: Map<String, String>): Float {
            return try {
                pieceMap.get(
                    PieceKey.HEIGHT.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: pieceOneSide.toFloat()
        }
        fun getFontSize(pieceMap: Map<String, String>): Float {
            return try {
                pieceMap.get(
                    PieceKey.FONT_SIZE.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: 20f
        }


        fun getString(pieceMap: Map<String, String>): String {
            return pieceMap.get(
                PieceKey.STRING.key
            ) ?: "C"
        }

        fun getFontStyle(pieceMap: Map<String, String>): Int {
            val fontStyleStr = pieceMap.get(
                PieceKey.FONT_STYLE.key
            )
            return EditComponent.Template.TextManager.TextStyle.entries.firstOrNull {
                it.key == fontStyleStr
            }?.style ?: EditComponent.Template.TextManager.TextStyle.NORMAL.style
        }

        fun getFontType(pieceMap: Map<String, String>): Typeface {
            val fontTypeStr = pieceMap.get(
                PieceKey.FONT_TYPE.key
            )
            return EditComponent.Font.entries.firstOrNull {
                it.key == fontTypeStr
            }?.typeface ?: EditComponent.Font.SANS_SERIF.typeface
        }

        fun getFontColor(
            context: Context,
            pieceMap: Map<String, String>,
            where: String,
        ): String {
            val colorKey = PieceKey.FONT_COLOR.key
            val colorStr = pieceMap.get(
                colorKey
            ) ?: "#000000"
            return ColorTool.parseColorStr(
                context,
                colorStr,
                colorKey,
                where,
            )
        }
        fun getStrokeColor(
            context: Context,
            pieceMap: Map<String, String>,
            where: String,
        ): String {
            val colorKey = PieceKey.STROKE_COLOR.key
            val colorStr = pieceMap.get(
                colorKey
            ) ?: "#000000"
            return ColorTool.parseColorStr(
                context,
                colorStr,
                colorKey,
                where,
            )
        }

        fun getStrokeWidth(
            pieceMap: Map<String, String>,
        ): Float {
            return try {
                pieceMap.get(
                    PieceKey.STROKE_WIDTH.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: 20f
        }

        fun getLetterSpacingWidth(
            pieceMap: Map<String, String>,
        ): Float {
            return try {
                pieceMap.get(
                    PieceKey.LETTER_SPACING.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: 20f
        }
    }

    object PieceShapeManager {

        private const val pieceOneSide = 100
        private const val keySeparator = '|'

        enum class PieceKey(val key: String) {
            ONE_SIDE("oneSide"),
            WIDTH("width"),
            HEIGHT("height"),
            SHAPE("shape"),
            TYPE("type"),
            COLOR("color"),
            ROTATE("rotate"),
        }

        fun makePieceMapArray(
            argsPairList: List<Pair<String, String>>,
        ): Array<Map<String, String>> {
            val pieceKey = IconsEnumArgs.PIECE.key
            return argsPairList.asSequence().filter {
                    (argKey, _) ->
                argKey == pieceKey
            }.map {
                    (_, pieceMapCon) ->
                CmdClickMap.createMap(
                    pieceMapCon,
                    keySeparator
                ).toMap()
            }.toList().toTypedArray()
        }

        fun getOneSide(pieceMap: Map<String, String>): Int {
            return try {
                pieceMap.get(
                    PieceKey.ONE_SIDE.key
                )?.toInt()
            } catch (e: Exception){
                null
            } ?: pieceOneSide
        }

        fun getRotate(pieceMap: Map<String, String>): Float {
            return try {
                pieceMap.get(
                    PieceKey.ROTATE.key
                )?.toFloat()
            } catch (e: Exception){
                null
            } ?: 0f
        }

        fun getWidth(pieceMap: Map<String, String>): Int? {
            return try {
                pieceMap.get(
                    PieceKey.WIDTH.key
                )?.toInt()
            } catch (e: Exception){
                null
            }
        }

        fun getHeight(pieceMap: Map<String, String>): Int? {
            return try {
                pieceMap.get(
                    PieceKey.HEIGHT.key
                )?.toInt()
            } catch (e: Exception){
                null
            }
        }


        fun getShape(pieceMap: Map<String, String>): String {
            val shapeStr = pieceMap.get(
                PieceKey.SHAPE.key
            )
            if(
                !shapeStr.isNullOrEmpty()
                && File(shapeStr).isFile
                ) return shapeStr
            return CmdClickIcons.entries.firstOrNull {
                it.str == shapeStr
            }?.str ?: CmdClickIcons.RECT.str
        }

        fun getType(pieceMap: Map<String, String>): EditComponent.IconType {

            val typeStr = pieceMap.get(
                PieceKey.TYPE.key
            )
            return EditComponent.IconType.entries.firstOrNull {
                it.name == typeStr
            } ?: EditComponent.IconType.SVG
        }

        fun getColor(
            context: Context,
            pieceMap: Map<String, String>,
            where: String,
        ): String {
            val colorKey = PieceKey.COLOR.key
            val colorStr = pieceMap.get(
                PieceKey.COLOR.key
            ) ?: "#000000"
            return ColorTool.parseColorStr(
                context,
                colorStr,
                colorKey,
                where,
            )
        }
    }
}