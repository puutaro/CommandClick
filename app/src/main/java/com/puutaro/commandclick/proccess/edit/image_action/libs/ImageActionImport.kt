package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object ImageActionImport {

    private val iIfKeyName = ImageActionKeyManager.ImageSubKey.I_IF.key
    private val valueSeparator = ImageActionKeyManager.valueSeparator
    private val imageActionVarKey = ImageActionKeyManager.ImageActionsKey.IMAGE_ACTION_VAR.key
    private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
    private val asyncRunPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
    private const val awaitWaitTimes =
        ImageActionKeyManager.awaitWaitTimes
//            private var replaceVariableMapCon = String()

//            fun initBeforeActionImportMap(
//                setReplaceVariableMap: Map<String, String>?
//            ){
////                beforeActionImportMap.clear()
//                replaceVariableMapCon =
//                    makeSetRepValeMapCon(setReplaceVariableMap)
//            }

    object BeforeActionImportMapManager {
        private val beforeActionImportMap = mutableMapOf<String, String>()
        private val mutex = ReentrantReadWriteLock()
        fun get(
            importPath: String
        ): String? {
            mutex.readLock().withLock {
                return beforeActionImportMap.get(importPath)
            }
        }
        fun put(
            importPath: String,
            importCon: String
        ) {
            mutex.writeLock().withLock {
                beforeActionImportMap.put(importPath, importCon)
            }
        }

        fun init() {
//                ImageActionErrLogger.AlreadyErr.init()
            mutex.writeLock().withLock {
                beforeActionImportMap.clear()
            }
        }
    }

    private fun makeSetRepValeMapCon(
        setReplaceVariableMap: Map<String, String>?
    ): String {
        if(
            setReplaceVariableMap.isNullOrEmpty()
        ) return String()
        return setReplaceVariableMap.map {
            "${it.key}\t${it.value}"
        }.joinToString("\n") + "\n"
    }


    suspend fun makeImportPathAndRenewalVarNameToImportCon(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        curMapLoopKey: String,
        loopKeyToAsyncDeferredVarNameBitmapMap: ImageActionData.LoopKeyToAsyncDeferredVarNameBitmapMap?,
        imageActionExitManager: ImageActionData.ImageActionExitManager,
        keyToSubKeyCon: Pair<String, String>?,
        originImportPathList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Pair<String,
            Triple<
                    String,
                    List<Pair<String, String>>,
                    Map<String, Bitmap?>,
                    >
            > {
        val keyToSubKeyContents = listOf(
            imageActionVarKey,
            keyToSubKeyCon?.second ?: String()
        ).joinToString("=")
        val actionImportMap = ImportMapMaker.comp(
            keyToSubKeyContents,
            "${imageActionVarKey}="
        )
        val topIAcVarName = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                imageActionVarKey
            )
        )
        val awaitVarNameList = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                ImageActionKeyManager.ActionImportManager.ActionImportKey.AWAIT.key
            )
        ).let {
            ImageActionKeyManager.AwaitManager.getAwaitVarNameList(it)
        }
        val awaitVarNameBitmapMap = awaitVarNameList.map awaitVarNameList@ {
                awaitVarName ->
            var deferredVarNameToBitmapAndBreakSignal: Deferred<
                    Pair<
                            Pair<String, Bitmap?>,
                            ImageActionKeyManager.BreakSignal?
                            >?
                    >? = null
            for (i in 1..awaitWaitTimes) {
                deferredVarNameToBitmapAndBreakSignal =
                    loopKeyToAsyncDeferredVarNameBitmapMap
                        ?.getAsyncVarNameToBitmapAndExitSignal(
                            curMapLoopKey
                        )?.get(awaitVarName)
                if (
                    deferredVarNameToBitmapAndBreakSignal != null
                ) {
                    break
                }
                delay(100)
            }
            val spanKeyToSubKeyConWhere =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    keyToSubKeyConWhere
                )
            if(
                deferredVarNameToBitmapAndBreakSignal == null
                && curMapLoopKey.isNotEmpty()
                && awaitVarName.isNotEmpty()
            ) {
                val spanAwaitVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        awaitVarName
                    )
                val spanIAcVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        ImageActionKeyManager.ImageActionsKey.IMAGE_ACTION_VAR.key
                    )
                val importPath = actionImportMap.get(
                    ImageActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
                )
                val spanImportPath =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        importPath ?: String()
                    )
                runBlocking {
                    ImageActionErrLogger.sendErrLog(
                        context,
                        ImageActionErrLogger.ImageActionErrType.AWAIT,
                        "await var name not exist: ${spanAwaitVarName}, by import path ${spanImportPath}, setting key: ${spanIAcVarKeyName}",
                        spanKeyToSubKeyConWhere,
                    )
                }
                return@awaitVarNameList String() to null
            }
            val varNameToBitmapAndExitSignal =
                deferredVarNameToBitmapAndBreakSignal?.await()
            loopKeyToAsyncDeferredVarNameBitmapMap?.clearVarName(
                curMapLoopKey,
                awaitVarName,
            )
            val varNameToBitmap = varNameToBitmapAndExitSignal?.first
            val exitSignal = varNameToBitmapAndExitSignal?.second
            val varName = varNameToBitmap?.first
                ?: return@awaitVarNameList String() to null
            if(
                varName.startsWith(asyncRunPrefix)
            ) return@awaitVarNameList String() to null
            val bitmap = varNameToBitmap.second
            varName to bitmap
        }.toMap()
        val judgeTargetStr = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                ImageActionKeyManager.ActionImportManager.ActionImportKey.I_IF.key
            )
        )
        val argsPairList = CmdClickMap.createMap(
            actionImportMap.get(
                ImageActionKeyManager.ActionImportManager.ActionImportKey.ARGS.key
            ),
            valueSeparator
        ).filter {
            it.first.isNotEmpty()
        }
        val isImportToErrType = when(judgeTargetStr.isEmpty()) {
            true -> true to null
            else -> SettingIfManager.handle(
                iIfKeyName,
                judgeTargetStr,
                argsPairList
            )
        }
        val blankReturnValue =
            String() to
                    Triple(
                        String(),
                        emptyList<Pair<String, String>>(),
                        emptyMap<String, Bitmap?>(),
                    )
        val errType = isImportToErrType.second
        if(errType != null){
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_IF,
                errType.errMessage,
                keyToSubKeyConWhere
            )
            imageActionExitManager.setExit()
            return blankReturnValue
        }
        val isImport = isImportToErrType.first ?: false
        if(
            !isImport
        ) return blankReturnValue
        val importPathSrc = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                ImageActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
            )
        )
        val isBeforeImportErr = withContext(Dispatchers.IO) {
            val isCircleImportErrJob = async {
                ImageActionImportErrManager.isCircleImportErr(
                    context,
                    originImportPathList,
                    importPathSrc,
                    keyToSubKeyConWhere,
                )
            }
            val isNotExistJob = async {
                ImageActionImportErrManager.isNotExist(
                    context,
                    importPathSrc,
                    keyToSubKeyConWhere
                )
            }
            isCircleImportErrJob.await()
                    || isNotExistJob.await()
        }
        if(
            isBeforeImportErr
        ) return blankReturnValue
        val importSrcConBeforeReplace = makeActionImportSrcCon(
            context,
            importPathSrc,
            fannelInfoMap,
            setReplaceVariableMap,
        ).replace(
            Regex("[${ImageActionKeyManager.landSeparator}]+$"),
            String(),
        )
        val importRepMap = makeRepValHolderMap(
            actionImportMap.get(
                ImageActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
            )
        )
        val importSrcCon = CmdClickMap.replaceHolderForJsAction(
            importSrcConBeforeReplace,
            importRepMap
        )
        val importedKeyToSubKeyConList = ImageActionKeyManager.KeyToSubKeyMapListMaker.make(importSrcCon)
        val settingKeyToVarNameList = ImageActionKeyManager.makeSettingKeyToBitmapVarKeyListForReturn(
            importedKeyToSubKeyConList
        ).let {
            ImageActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(
                it
            )
        }
        val isErr = withContext(Dispatchers.IO) {
            val keyToSubKeyConWhereInImportPath = listOf(
                keyToSubKeyConWhere,
                "by import path $importPathSrc"
            ).joinToString("\n")
            val isImportShadowVarMarkErrJob = async {
                ImageActionImportErrManager.isImportShadowVarMarkErr(
                    context,
                    importPathSrc,
                    importSrcConBeforeReplace,
                    importRepMap,
                    keyToSubKeyConWhere,
                )
            }
//                    val isGlobalVarNameErrWithRunPrefixJob = async {
//                        ImageActionImportErrManager.isGlobalVarNameExistErrWithRunPrefix(
//                            context,
//                            settingKeyToVarNameList,
//                            topIAcVarName,
//                            keyToSubKeyConWhereInImportPath
//                        )
//                    }
//                    val isGlobalVarNameMultipleExistErrWithoutRunPrefixJob = async {
//                        ImageActionImportErrManager.isGlobalVarNameMultipleErrWithoutRunPrefix(
//                            context,
//                            settingKeyToVarNameList,
//                            topIAcVarName,
//                            keyToSubKeyConWhereInImportPath
//                        )
//                    }
            val isGlobalVarNameNotLastErrWithoutRunPrefixJob = async {
                ImageActionImportErrManager.isGlobalVarNameNotLastErrWithoutRunPrefix(
                    context,
                    settingKeyToVarNameList,
                    importedKeyToSubKeyConList,
                    topIAcVarName,
                    keyToSubKeyConWhereInImportPath
                )
            }
            isImportShadowVarMarkErrJob.await()
//                            || isGlobalVarNameErrWithRunPrefixJob.await()
//                            || isGlobalVarNameMultipleExistErrWithoutRunPrefixJob.await()
                    || isGlobalVarNameNotLastErrWithoutRunPrefixJob.await()
        }
        if(
            isErr
        ) return blankReturnValue
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "smakeImportPathAndRenewalVarNameToImportCon.txt").absolutePath,
//                    listOf(
//                        "renewalVarNameSrc: ${renewalVarName}",
//                        "importSrcCon: ${importSrcCon}",
//                        "importedKeyToSubKeyConList: ${importedKeyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
        return importPathSrc to Triple(
            topIAcVarName,
            importedKeyToSubKeyConList,
            awaitVarNameBitmapMap
        )
    }

    private fun makeRepValHolderMap(
        replaceKeyConWithQuote: String?,
    ): Map<String, String> {
        if(
            replaceKeyConWithQuote.isNullOrEmpty()
        ) return emptyMap()
        val replaceKeyCon = QuoteTool.trimBothEdgeQuote(
            replaceKeyConWithQuote
        )
        val replaceSeparator = '&'
        return CmdClickMap.createMap(
            replaceKeyCon,
            replaceSeparator
        ).toMap().filterKeys { it.isNotEmpty() }
    }

    private suspend fun makeActionImportSrcCon(
        context: Context?,
        importPath: String,
//        currentAppDirPath: String,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
        val beforeActionImportSrcCon = withContext(Dispatchers.IO) {
            BeforeActionImportMapManager.get(importPath)
        }
        if(
            !beforeActionImportSrcCon.isNullOrEmpty()
        ) {
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport_first_makeActionImportSrcCon.txt").absolutePath,
//                        listOf(
//                            "importPath: ${importPath}",
//                            "beforeActionImportSrcCon: ${beforeActionImportSrcCon}",
//                        ).joinToString("\n\n") + "\n\n========\n\n"
//                    )
            return beforeActionImportSrcCon
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport_first_makeActionImportSrcCon.txt").absolutePath,
//            listOf(
//                "importPath: ${importPath}",
//                "beforeActionImportSrcCon: ${beforeActionImportSrcCon}",
//            ).joinToString("\n\n") + "\n-----\n"
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
        val actionImportSrcCon = SettingFile.read(
            context,
            importPath,
            File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath,
            setReplaceVariableMap,
        )
        CoroutineScope(Dispatchers.IO).launch {
            BeforeActionImportMapManager.put(
                importPath,
                actionImportSrcCon,
            )
        }
        return actionImportSrcCon
    }
}