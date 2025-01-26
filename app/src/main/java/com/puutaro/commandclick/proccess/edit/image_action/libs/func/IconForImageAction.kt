package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlin.enums.EnumEntries

object IconForImageAction {
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
            is IconMethodArgClass.MakeArgs -> {
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
                val iconMacroStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.iconMacroStrKeyToIndex
                    ,where
                ).let { iconMacroStrToErr ->
                    val funcErr = iconMacroStrToErr.second
                        ?: return@let iconMacroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val width = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.widthKeyToIndex
                    ,where
                ).let { widthToErr ->
                    val funcErr = widthToErr.second
                        ?: return@let widthToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val height = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.heightKeyToIndex
                    ,where
                ).let { heightToErriconMacroStrToErr ->
                    val funcErr = heightToErriconMacroStrToErr.second
                        ?: return@let heightToErriconMacroStrToErr.first
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
                   )?.toBitmap(
                       width,
                       height
                   )
               }
                Pair(
                    bitmap,
                    null
                ) to null
            }
        }
//        ImageFuncCheckerForImageSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.argsNameToTypeList,
//            argsPairList,
//            null,
//        )?.let {
//                argsCheckErr ->
//            return null to argsCheckErr
//        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "settingCheck.txt").absolutePath,
//            listOf(
//                "isErr: ${isErr}",
//            ).joinToString("\n")
//        )
//        val argsList = argsPairList.map {
//            it.second
//        }
//        return when(methodNameClass){
//            MethodNameClass.MAKE -> {
//                val iconMacroStr = argsList.get(0)
//                val width = argsList.get(1).toInt()
//                val height = argsList.get(2).toInt()
//               val bitmap = CmdClickIcons.entries.firstOrNull {
//                   it.str == iconMacroStr
//               }?.let {
//                   AppCompatResources.getDrawable(
//                       context,
//                       it.id,
//                   )?.toBitmap(
//                       width,
//                       height
//                   )
//               }
//                Pair(
//                    bitmap,
//                    null
//                ) to null
//            }
//        }
    }

//    private enum class MethodNameClass(
//        val str: String,
//        val argsNameToTypeList: List<Pair<String, ImageFuncCheckerForImageSetting.ArgType>>,
//    ){
//        MAKE("make", makeArgsNameToTypeList),
//    }

    private enum class MethodNameClass(
        val str: String,
        val args: IconMethodArgClass
    ){
        MAKE("make", IconMethodArgClass.MakeArgs),
    }

//    private val makeArgsNameToTypeList = listOf(
//        Pair("iconMacroStr", ImageFuncCheckerForImageSetting.ArgType.STRING),
//        Pair("width", ImageFuncCheckerForImageSetting.ArgType.INT),
//        Pair("height", ImageFuncCheckerForImageSetting.ArgType.INT),
//    )

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class IconMethodArgClass {
        data object MakeArgs : IconMethodArgClass(), ArgType {
            override val entries = MakeEnumArgs.entries
            val iconMacroStrKeyToIndex = Pair(
                MakeEnumArgs.ICON_MACRO_STR.key,
                MakeEnumArgs.ICON_MACRO_STR.index
            )
            val widthKeyToIndex = Pair(
                MakeEnumArgs.WIDTH.key,
                MakeEnumArgs.WIDTH.index
            )
            val heightKeyToIndex = Pair(
                MakeEnumArgs.HEIGHT.key,
                MakeEnumArgs.HEIGHT.index
            )

            enum class MakeEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                ICON_MACRO_STR("iconMacroStr", 0, FuncCheckerForSetting.ArgType.STRING),
                WIDTH("width", 1, FuncCheckerForSetting.ArgType.INT),
                HEIGHT("height", 2, FuncCheckerForSetting.ArgType.INT),

            }
        }
    }
}
