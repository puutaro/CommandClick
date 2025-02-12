package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.libs.IfErrManager
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

object ImageActionImportManager {

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
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        curMapLoopKey: String,
        topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
        loopKeyToAsyncDeferredVarNameBitmapMap: ImageActionData.LoopKeyToAsyncDeferredVarNameBitmapMap?,
        loopKeyToVarNameBitmapMap: ImageActionData.LoopKeyToVarNameBitmapMap?,
        privateLoopKeyVarNameBitmapMapClass: ImageActionData.PrivateLoopKeyVarNameBitmapMap,
        importedVarNameToBitmapMap: Map<String, Bitmap?>?,
        imageActionExitManager: ImageActionData.ImageActionExitManager,
        keyToSubKeyCon: Pair<String, String>?,
        originImportPathList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Triple<
            String,
            Triple<
                    String,
                    List<Pair<String, String>>,
                    Map<String, Bitmap?>,
                    >,
            Map<String, Bitmap?>?
            > {
        val keyToSubKeyContents = listOf(
            imageActionVarKey,
            keyToSubKeyCon?.second ?: String()
        ).joinToString("=")
        val varNameToBitmapMap =
            ImageActionKeyManager.makeValueToBitmapMap(
                curMapLoopKey,
                topVarNameToVarNameBitmapMap,
                importedVarNameToBitmapMap,
                loopKeyToVarNameBitmapMap,
                privateLoopKeyVarNameBitmapMapClass,
                null,
                null,
            )
        val actionImportPairList = ImportMapMaker.comp(
            keyToSubKeyContents,
            "${imageActionVarKey}="
        )
        val actionImportMap = actionImportPairList.toMap()
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
                        ?.getAsyncVarNameToBitmapAndExitSignalFromMap(
                            curMapLoopKey,
                            awaitVarName
                        )
//                        ?.get(awaitVarName)
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
                        CheckTool.lightBlue,
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
        awaitVarNameBitmapMap.forEach {
            (varName, bitmap) ->
            privateLoopKeyVarNameBitmapMapClass.put(
                curMapLoopKey,
                varName,
                bitmap
            )
        }
        val blankReturnValue =
            Triple(
                String(),
                Triple(
                    String(),
                    emptyList<Pair<String, String>>(),
                    emptyMap<String, Bitmap?>(),
                ),
                null,
            )
        val iIfKey =
            ImageActionKeyManager.ActionImportManager.ActionImportKey.I_IF.key
        val ifMapList = actionImportPairList.filter {
                mainSubKeyPair ->
            val mainSubKey = mainSubKeyPair.first
//            val mainSubKeyMapSrc = mainSubKeyPair.second
            mainSubKey == iIfKey
        }
        IfErrManager.isMultipleSpecifyErr(
            context,
            ifMapList.size,
            iIfKeyName,
            keyToSubKeyConWhere,
        ).let {
                isMultipleSpecifyErr ->
            if(
                !isMultipleSpecifyErr
            ) return@let
            imageActionExitManager.setExit()
            return blankReturnValue
        }
        val ifProcName = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(iIfKey)
        )
        val argsPairList = CmdClickMap.createMap(
            actionImportMap.get(
                ImageActionKeyManager.ActionImportManager.ActionImportKey.ARGS.key
            ),
            valueSeparator
        ).filter {
            it.first.isNotEmpty()
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lsmakeImportPathAndRenewalVarNameToImportCon.txt").absolutePath,
//            listOf(
//                "actionImportMap: ${actionImportMap}",
//                "ifProcName: ${ifProcName}",
//                "argsPairList: ${argsPairList}",
//            ).joinToString("\n\n")
//        )
        val isImportToErrType = when(ifProcName.isEmpty()) {
            true -> true to null
            else -> SettingIfManager.handle(
                iIfKeyName,
//                judgeTargetStr,
                argsPairList,
                null
            )
        }
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
        val varNameToBitmapMapPlusAwait =
            varNameToBitmapMap + awaitVarNameBitmapMap
        val importVarNameToBitmapMap =
            importRepMap.map {
                    (_, bitmapVarMark) ->
                val blankMapPair =
                    String() to null
                if(
                    !ImageActionKeyManager.BitmapVar.matchBitmapVarName(
                        bitmapVarMark
                    )
                ) return@map blankMapPair
                val importKey = ImageActionKeyManager.BitmapVar.convertBitmapKey(bitmapVarMark)
                val importBitmap = varNameToBitmapMapPlusAwait.get(importKey)
                    ?: return@map  blankMapPair
                importKey to importBitmap
            }.filter {
                    (importKey, _) ->
                importKey.isNotEmpty()
            }.toMap()
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
//            val isImportShadowVarMarkErrJob = async {
//                ImageActionImportErrManager.isImportShadowVarMarkErr(
//                    context,
//                    importPathSrc,
//                    importSrcConBeforeReplace,
//                    importRepMap,
//                    keyToSubKeyConWhere,
//                )
//            }
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
//            isImportShadowVarMarkErrJob.await()
//                            || isGlobalVarNameErrWithRunPrefixJob.await()
//                            || isGlobalVarNameMultipleExistErrWithoutRunPrefixJob.await()
                    isGlobalVarNameNotLastErrWithoutRunPrefixJob.await()
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
        return Triple (
            importPathSrc,
            Triple(
                topIAcVarName,
                importedKeyToSubKeyConList,
                awaitVarNameBitmapMap
            ),
            importVarNameToBitmapMap
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
        fannelInfoMap: HashMap<String, String>,
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