package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.res.FannelIcons
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlin.enums.EnumEntries

object FannelIconForImageAction {

    private const val intDefaultNullMacroStr = 0.toString()

    fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
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
            is FannelIconMethodArgClass.SvgArgs -> {
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
                val iconMacroStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.typeStrKeyToDefaultValueStr,
                    where
                ).let { iconMacroStrToErr ->
                    val funcErr = iconMacroStrToErr.second
                        ?: return@let iconMacroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
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
                    where,
                ).let { heightToErr ->
                    val funcErr = heightToErr.second
                        ?: return@let heightToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val bitmap = FannelIcons.entries.firstOrNull {
                   it.str == iconMacroStr
               }?.let {
                   icon ->
                    val iconDrawable = AppCompatResources.getDrawable(
                        context,
                        icon.id,
                    )
                    when(
                           width == intDefaultNullMacroStr.toInt()
                                   || height == intDefaultNullMacroStr.toInt()
                       ){
                           true -> iconDrawable?.toBitmap()
                           false -> iconDrawable?.toBitmap(
                               width,
                               height,
                           )
                       }
//                   AppCompatResources.getDrawable(
//                       context,
//                       it.id,
//                   )?.let {
//                       val iconFile = ExecSetToolbarButtonImage.getImageFile(
//                           it.ass
//                       )
//                       BitmapTool.convertFileToBitmap(iconFile.absolutePath)
//                       when(
//                           width == intDefaultNullMacroStr.toInt()
//                                   || height == intDefaultNullMacroStr.toInt()
//                       ){
//                           true -> it.toBitmap()
//                           false -> it.toBitmap(
//                               width,
//                               height
//                           )
//                       }
//                   }
               }
                Pair(
                    bitmap,
                    null
                ) to null
            }
//            is FannelIconMethodArgClass.ImgArgs -> {
//                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
//                        index, formalArgsNameToType ->
//                    Triple(
//                        index,
//                        formalArgsNameToType.key,
//                        formalArgsNameToType.type,
//                    )
//                }
//                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
//                    formalArgIndexToNameToTypeList,
//                    argsPairList
//                )
//                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
//                    funcName,
//                    methodNameStr,
//                    argsPairList,
//                    formalArgIndexToNameToTypeList
//                )
//                val iconMacroStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.typeStrKeyToIndex,
//                    where
//                ).let { iconMacroStrToErr ->
//                    val funcErr = iconMacroStrToErr.second
//                        ?: return@let iconMacroStrToErr.first
//                    return Pair(
//                        null,
//                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
//                val bitmap = CmdClickIcons.entries.firstOrNull {
//                    it.str == iconMacroStr
//                }?.let {
//                    val iconFile = ExecSetToolbarButtonImage.getImageFile(
//                        it.assetsPath
//                    )
//                    BitmapTool.convertFileToBitmap(iconFile.absolutePath)
//                }
//                Pair(
//                    bitmap,
//                    null
//                ) to null
//            }
        }
    }
    private enum class MethodNameClass(
        val str: String,
        val args: FannelIconMethodArgClass
    ){
        SVG("svg", FannelIconMethodArgClass.SvgArgs),
//        IMG("img", FannelIconMethodArgClass.ImgArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class FannelIconMethodArgClass {
        data object SvgArgs : FannelIconMethodArgClass(), ArgType {
            override val entries = SvgEnumArgs.entries
            val typeStrKeyToDefaultValueStr = Pair(
                SvgEnumArgs.TYPE.key,
                SvgEnumArgs.TYPE.defaultValueStr
            )
            val widthKeyToDefaultValueStr = Pair(
                SvgEnumArgs.WIDTH.key,
                SvgEnumArgs.WIDTH.defaultValueStr
            )
            val heightKeyToDefaultValueStr = Pair(
                SvgEnumArgs.HEIGHT.key,
                SvgEnumArgs.HEIGHT.defaultValueStr
            )

            enum class SvgEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                TYPE("type", null, FuncCheckerForSetting.ArgType.STRING),
                WIDTH("width", intDefaultNullMacroStr, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", intDefaultNullMacroStr, FuncCheckerForSetting.ArgType.INT),
//                COLOR("color", ColorTool.convertColorToHex(Color.BLACK), FuncCheckerForSetting.ArgType.INT),
            }
        }
//        data object ImgArgs : FannelIconMethodArgClass(), ArgType {
//            override val entries = ImgEnumArgs.entries
//            val typeStrKeyToIndex = Pair(
//                ImgEnumArgs.TYPE.key,
//                ImgEnumArgs.TYPE.defaultValueStr
//            )
//
//            enum class ImgEnumArgs(
//                val key: String,
//                val defaultValueStr: String?,
//                val type: FuncCheckerForSetting.ArgType,
//            ){
//                TYPE("type", null, FuncCheckerForSetting.ArgType.STRING),
//            }
//        }
    }
}
