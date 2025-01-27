package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlin.enums.EnumEntries

object FileSystemsForImageAction {
    fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>,
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
            is FileMethodArgClass.SaveArgs -> {
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
                val savePath = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.savePathStrKeyToIndex,
                    where
                ).let { savePathToErr ->
                    val funcErr = savePathToErr.second
                        ?: return@let savePathToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
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
                FileSystems.writeFromByteArray(
                   savePath,
                   BitmapTool.convertBitmapToByteArray(bitmap)
                )
                Pair(
                    bitmap,
                    null,
                ) to null
                null
            }
            is FileMethodArgClass.ReadArgs -> {
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
                val path = FuncCheckerForSetting.Getter.getFileFromArgMapByIndex(
                    mapArgMapList,
                    args.pathStrKeyToIndex,
                    where
                ).let { pathToErr ->
                    val funcErr = pathToErr.second
                        ?: return@let pathToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap =
                    BitmapTool.convertFileToBitmap(path)
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: FileMethodArgClass,
    ){
        SAVE("save", FileMethodArgClass.SaveArgs),
        READ("read", FileMethodArgClass.ReadArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class FileMethodArgClass {
        data object SaveArgs : FileMethodArgClass(), ArgType {
            override val entries = SaveEnumArgs.entries
            val savePathStrKeyToIndex = Pair(
                SaveEnumArgs.SAVE_PATH.key,
                SaveEnumArgs.SAVE_PATH.index
            )
            val bitmapKeyToIndex = Pair(
                SaveEnumArgs.BITMAP.key,
                SaveEnumArgs.BITMAP.index
            )

            enum class SaveEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                SAVE_PATH("path", 0, FuncCheckerForSetting.ArgType.STRING),
                BITMAP("bitmap", 1, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
        data object ReadArgs : FileMethodArgClass(), ArgType {
            override val entries = ReadEnumArgs.entries
            val pathStrKeyToIndex = Pair(
                ReadEnumArgs.READ_PATH.key,
                ReadEnumArgs.READ_PATH.index
            )

            enum class ReadEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                READ_PATH("path", 0, FuncCheckerForSetting.ArgType.FILE),
            }
        }
    }
}
