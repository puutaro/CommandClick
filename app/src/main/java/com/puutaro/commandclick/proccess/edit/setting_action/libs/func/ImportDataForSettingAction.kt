package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.delay
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlin.enums.EnumEntries

object ImportDataForSettingAction {
    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
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
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val waitMill = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.waitMillToIndex,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }.let {
                    if(it <= 0) return@let args.defaultWaitMill
                    it
                }
                val delayMill = 50L
                val waitLoopNum = waitMill / delayMill
                var returnStr: String? = null
                for(i in 0..waitLoopNum) {
                    returnStr = Data.get(
                        importPath,
                        key,
                    )
                    if(returnStr != null) break
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
                    returnStr,
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
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }

                val str = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.strKeyToIndex,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                Data.put(
                    importPath,
                    key,
                    str
                )

                Pair(
                    str,
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
            val strKeyToIndex = Pair(
                PutArgs.STR.key,
                PutArgs.STR.index
            )
            enum class PutArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                IMPORT_PATH("importPath", 0, FuncCheckerForSetting.ArgType.FILE),
                KEY("key", 1, FuncCheckerForSetting.ArgType.STRING),
                STR("str", 2, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }

    suspend fun clearImportData(){
        Data.clear()
    }

    private object Data {

        data class ImportData(
            val importPath: String,
            val key: String,
            val str: String?,
        )
        private val asyncImportKeyToValueStrMutex = ReentrantReadWriteLock()
        private val importKeyToValueStr: HashSet<ImportData> = hashSetOf()
        suspend fun get(
            importPath: String,
            key: String,
        ): String? {
            return asyncImportKeyToValueStrMutex.readLock().withLock {
                importKeyToValueStr.firstOrNull {
                    importData ->
                    importData.importPath == importPath
                            && importData.key == key
                }?.str
            }
        }

        suspend fun put(
            importPath: String,
            key: String,
            str: String?,
        ){
            asyncImportKeyToValueStrMutex.writeLock().withLock {
                val existData = importKeyToValueStr.filter {
                        importData ->
                    importData.importPath == importPath
                            && importData.key == key
                }
                existData.forEach {
                    importKeyToValueStr.remove(it)
                }
                importKeyToValueStr.add(
                    ImportData(
                        importPath,
                        key,
                        str,
                    )
                )
            }
        }

        suspend fun clear(){
            asyncImportKeyToValueStrMutex.writeLock().withLock {
                importKeyToValueStr.clear()
            }
        }
    }
}
