package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionBitmapData
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlin.enums.EnumEntries

object ImportDataForImageAction {
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
            is ImportDataMethodArgClass.GetArgs -> {
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
                val importPath = FuncCheckerForSetting.Getter.getFileFromArgMapByIndex(
                    mapArgMapList,
                    args.importPathToKey,
                    where
                ).let { importPathToErr ->
                    val funcErr = importPathToErr.second
                        ?: return@let importPathToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val key = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.keyToIndex,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val waitMill = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByIndex(
                    mapArgMapList,
                    args.waitMillToIndex,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
//                    .let {
//                    if(it <= 0) return@let args.defaultWaitMill
//                    it
//                }
                val delayMill = 50L
                val waitLoopNum = waitMill / delayMill
                var returnBitmap: Bitmap? = null
                for(i in 0..waitLoopNum) {
                    returnBitmap = ImageActionBitmapData.get(
                        importPath,
                        key,
                    )
                    if(returnBitmap != null) break
                    delay(delayMill)
                }
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "ldata_get.txt").absolutePath,
//                    listOf(
//                        "importPath: ${importPath}",
//                        "key: ${key}",
//                        "bitmap: ${returnBitmap}",
//                        "mapArgMapList: ${mapArgMapList}",
//                        "varNameToBitmapMap: ${varNameToBitmapMap}",
//                        "Data: ${Data.get(
//                            importPath,
//                            key,
//                        )}"
//                    ).joinToString("\n")
//                )
                Pair(
                    returnBitmap,
                    null
                ) to null
            }
            is ImportDataMethodArgClass.PutArgs -> {
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
                val importPath = FuncCheckerForSetting.Getter.getFileFromArgMapByIndex(
                    mapArgMapList,
                    args.importPathToIndex,
                    where
                ).let { importPathToErr ->
                    val funcErr = importPathToErr.second
                        ?: return@let importPathToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val key = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.keyToIndex,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "ldata_put.txt").absolutePath,
//                    listOf(
//                        "importPath: ${importPath}",
//                        "key: ${key}",
//                        "mapArgMapList: ${mapArgMapList}",
//                        "varNameToBitmapMap: ${varNameToBitmapMap}",
////                        "bitmap: ${bitmap}",
////                        "Data: ${Data.get(
////                            importPath,
////                            key,
////                        )}"
//                    ).joinToString("\n")
//                )
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bitmapKeyToIndex,
                    varNameToBitmapMap,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                ImageActionBitmapData.put(
                    importPath,
                    key,
                    bitmap
                )
                Pair(
                    bitmap,
                    null
                ) to null
            }
        }
    }
    private enum class MethodNameClass(
        val str: String,
        val args: ImportDataMethodArgClass,
    ){
        GET("get", ImportDataMethodArgClass.GetArgs),
        PUT("put", ImportDataMethodArgClass.PutArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class ImportDataMethodArgClass {
        data object GetArgs : ImportDataMethodArgClass(), ArgType {
            override val entries = GetArgs.entries
            val defaultWaitMill = 1000
            val importPathToKey = Pair(
                GetArgs.IMPORT_PATH.key,
                GetArgs.IMPORT_PATH.index
            )
            val keyToIndex = Pair(
                GetArgs.KEY.key,
                GetArgs.KEY.index
            )
            val waitMillToIndex = Pair(
                GetArgs.WAIT_TIME.key,
                GetArgs.WAIT_TIME.index
            )
            enum class GetArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                IMPORT_PATH("importPath", 0, FuncCheckerForSetting.ArgType.FILE),
                KEY("key", 1, FuncCheckerForSetting.ArgType.STRING),
                WAIT_TIME("waitMill", 2, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object PutArgs : ImportDataMethodArgClass(), ArgType {
            override val entries = PutArgs.entries
           val importPathToIndex = Pair(
               PutArgs.IMPORT_PATH.key,
               PutArgs.IMPORT_PATH.index
           )
            val keyToIndex = Pair(
                PutArgs.KEY.key,
                PutArgs.KEY.index
            )
            val bitmapKeyToIndex = Pair(
                PutArgs.BITMAP.key,
                PutArgs.BITMAP.index
            )
            enum class PutArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                IMPORT_PATH("importPath", 0, FuncCheckerForSetting.ArgType.FILE),
                KEY("key", 1, FuncCheckerForSetting.ArgType.STRING),
                BITMAP("bitmap", 2, FuncCheckerForSetting.ArgType.BITMAP),
            }
        }
    }

    suspend fun clearImportData(){
        ImageActionBitmapData.clear()
    }
}
