package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlin.enums.EnumEntries

object IconForImageAction {

    private const val intDefaultNullMacroStr = 0.toString()

    fun handle(
        fragment: Fragment,
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
        val context =
            fragment.context
                ?: return null
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
            is IconMethodArgClass.SvgArgs -> {
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
                    args.typeStrKeyToIndex,
                    where
                ).let { iconMacroStrToErr ->
                    val funcErr = iconMacroStrToErr.second
                        ?: return@let iconMacroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val oneSide = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.oneSideKeyToIndex,
                    where
                ).let { oneSideToErr ->
                    val funcErr = oneSideToErr.second
                        ?: return@let oneSideToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val bitmap = CmdClickIcons.entries.firstOrNull {
                   it.str == iconMacroStr
               }?.let {
                   AppCompatResources.getDrawable(
                       context,
                       it.id,
                   )?.let {
                       when(oneSide == intDefaultNullMacroStr.toInt()){
                           true -> it.toBitmap()
                           false -> it.toBitmap(
                               oneSide,
                               oneSide
                           )
                       }
                   }
               }
                Pair(
                    bitmap,
                    null
                ) to null
            }
            is IconMethodArgClass.ImgArgs -> {
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
                    args.typeStrKeyToIndex,
                    where
                ).let { iconMacroStrToErr ->
                    val funcErr = iconMacroStrToErr.second
                        ?: return@let iconMacroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val bitmap = CmdClickIcons.entries.firstOrNull {
                    it.str == iconMacroStr
                }?.let {
                    val iconFile = ExecSetToolbarButtonImage.getImageFile(
                        it.assetsPath
                    )
                    BitmapTool.convertFileToBitmap(iconFile.absolutePath)
                }
                Pair(
                    bitmap,
                    null
                ) to null
            }
        }
    }
    private enum class MethodNameClass(
        val str: String,
        val args: IconMethodArgClass
    ){
        SVG("svg", IconMethodArgClass.SvgArgs),
        IMG("img", IconMethodArgClass.ImgArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class IconMethodArgClass {
        data object SvgArgs : IconMethodArgClass(), ArgType {
            override val entries = SvgEnumArgs.entries
            val typeStrKeyToIndex = Pair(
                SvgEnumArgs.TYPE.key,
                SvgEnumArgs.TYPE.defaultValueStr
            )
            val oneSideKeyToIndex = Pair(
                SvgEnumArgs.ONE_SIDE.key,
                SvgEnumArgs.ONE_SIDE.defaultValueStr
            )

            enum class SvgEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                TYPE("type", null, FuncCheckerForSetting.ArgType.STRING),
                ONE_SIDE("oneSide", intDefaultNullMacroStr, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object ImgArgs : IconMethodArgClass(), ArgType {
            override val entries = ImgEnumArgs.entries
            val typeStrKeyToIndex = Pair(
                ImgEnumArgs.TYPE.key,
                ImgEnumArgs.TYPE.defaultValueStr
            )

            enum class ImgEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                TYPE("type", null, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}
