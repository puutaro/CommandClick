package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlin.enums.EnumEntries

object DebugForImageAction {
    fun handle(
        fragment: Fragment,
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
            return null to
                    FuncCheckerForSetting.FuncCheckErr(
                        "Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                    )
        }
        val args =
            methodNameClass.args
        return when(args) {
            is DebugMethodArgClass.ReflectArgs -> {
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
                ).let { savePathToErr ->
                    val funcErr = savePathToErr.second
                        ?: return@let savePathToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                Pair(
                    bitmap,
                    null
                ) to null
            }
            is DebugMethodArgClass.NullArgs -> {
                Pair(
                    null,
                    null
                ) to null
            }
        }

//        ImageFuncCheckerForImageSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.argsNameToTypeList,
//            argsPairList,
//            varNameToBitmapMap,
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
//                val bitmapKey =
//                    ImageActionKeyManager.BitmapVar.convertBitmapKey(
//                        argsList.get(0)
//                    )
//                val bitmap = varNameToBitmapMap?.get(bitmapKey)
//                    ?: return null
//                Pair(
//                    bitmap,
//                    null
//                ) to null
//            }
//            MethodNameClass.NULL -> {
//                Pair(
//                    null,
//                    null
//                ) to null
//            }
//        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: DebugMethodArgClass,
    ){
        MAKE("reflect", DebugMethodArgClass.ReflectArgs),
        NULL("null", DebugMethodArgClass.NullArgs)
    }

//    private val makeArgsNameToTypeList = listOf(
//        Pair("bitmapVarName", ImageFuncCheckerForImageSetting.ArgType.BITMAP_VAR_NAME),
//    )
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class DebugMethodArgClass {
        data object ReflectArgs : DebugMethodArgClass(), ArgType {
            override val entries = SaveEnumArgs.entries
            val bitmapKeyToIndex = Pair(
                SaveEnumArgs.BITMAP.key,
                SaveEnumArgs.BITMAP.index
            )

            enum class SaveEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
        data object NullArgs : DebugMethodArgClass() {}
    }
}
