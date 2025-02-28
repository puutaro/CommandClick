package com.puutaro.commandclick.proccess.edit.setting_action.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.VarMarkTool
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

object SettingActionImportManager {

    private val sIfKeyName = SettingActionKeyManager.SettingSubKey.S_IF.key
    private val valueSeparator = SettingActionKeyManager.valueSeparator
    private val imageActionVarKey = SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
    private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
    private val asyncRunPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
    private const val awaitWaitTimes =
        SettingActionKeyManager.AwaitManager.awaitWaitTimes

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
//                ErrLogger.AlreadyErr.init()
            mutex.writeLock().withLock {
                beforeActionImportMap.clear()
            }
        }
    }
//            private var replaceVariableMapCon = String()

//            fun initBeforeActionImportMap(
//                setReplaceVariableMap: Map<String, String>?
//            ){
////                beforeActionImportMap.clear()
//                replaceVariableMapCon =
//                    makeSetRepValeMapCon(setReplaceVariableMap)
//            }

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
        context: Context,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        curMapLoopKey: String,
        topVarNameToValueStrMap: Map<String, String?>?,
        loopKeyToAsyncDeferredVarNameValueStrMap: SettingActionData.LoopKeyToAsyncDeferredVarNameValueStrMap?,
        privateLoopKeyVarNameValueStrMapClass: SettingActionData.PrivateLoopKeyVarNameValueStrMap,
        loopKeyToVarNameValueStrMapClass: SettingActionData.LoopKeyToVarNameValueStrMap?,
        importedVarNameToValueStrMap: Map<String, String?>?,
        settingActionExitManager: SettingActionData.SettingActionExitManager,
        keyToSubKeyCon: Pair<String, String>?,
        originImportPathList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Triple<String,
            Triple<
                    String,
                    List<Pair<String, String>>,
                    Map<String, String?>,
                    >,
            Map<String,String?>,
            > {
//        val context = fragment.context
        val keyToSubKeyContents = listOf(
            imageActionVarKey,
            keyToSubKeyCon?.second ?: String()
        ).joinToString("=")
        val blankReturnValueStr =
            Triple (
                String(),
                Triple(
                    String(),
                    emptyList<Pair<String, String>>(),
                    emptyMap<String, String?>(),
                ),
                emptyMap<String, String?>(),
            )
        val varNameToValueStrMap =
            SettingActionMapTool.makeVarNameToValueStrMap(
                curMapLoopKey,
                topVarNameToValueStrMap,
                importedVarNameToValueStrMap,
                loopKeyToVarNameValueStrMapClass,
                privateLoopKeyVarNameValueStrMapClass,
                null,
                null,
            )
        val actionImportPairListBeforeReplace = ImportMapMaker.comp(
            keyToSubKeyContents,
            "${imageActionVarKey}="
        )
        val actionImportMapBeforeReplace = actionImportPairListBeforeReplace.toMap()
//        val actionImportMap =
//            CmdClickMap.MapReplacer.replaceToPairList(
//                actionImportMapBeforeReplace,
//                varNameToValueStrMap
//            ).toMap()
        val topIAcVarName = QuoteTool.trimBothEdgeQuote(
            actionImportMapBeforeReplace.get(
                imageActionVarKey
            )
        )
        val awaitVarNameList = QuoteTool.trimBothEdgeQuote(
            actionImportMapBeforeReplace.get(
                SettingActionKeyManager.ActionImportManager.ActionImportKey.AWAIT.key
            )
        ).let {
            SettingActionKeyManager.AwaitManager.getAwaitVarNameList(it)
        }
        val awaitVarNameValueStrMap = awaitVarNameList.map awaitVarNameList@ {
                awaitVarName ->
            var deferredVarNameToValueStrAndBreakSignal: Deferred<
                    Pair<
                            Pair<String, String?>,
                            SettingActionKeyManager.BreakSignal?
                            >?
                    >? = null
            for (i in 1..awaitWaitTimes) {
//                        if(
//                            ErrLogger.AlreadyErr.get()
//                        ) break
                deferredVarNameToValueStrAndBreakSignal =
                    loopKeyToAsyncDeferredVarNameValueStrMap
                        ?.getAsyncVarNameToValueStrAndExitSignalFromMap(
                            curMapLoopKey,
                            awaitVarName,
                        )
//                        ?.get(awaitVarName)
                if (
                    deferredVarNameToValueStrAndBreakSignal != null
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
                deferredVarNameToValueStrAndBreakSignal == null
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
                        SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
                    )
                val importPath = actionImportMapBeforeReplace.get(
                    SettingActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
                )?.let {
                    CmdClickMap.replaceByBackslashToNormal(
                        it,
                        varNameToValueStrMap
                    )
                }
                val spanImportPath =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        importPath ?: String()
                    )
                runBlocking {
                    SettingActionErrLogger.sendErrLog(
                        context,
                        SettingActionErrLogger.SettingActionErrType.AWAIT,
                        "await var name not exist: ${spanAwaitVarName}, by import path ${spanImportPath}, setting key: ${spanIAcVarKeyName}",
                        spanKeyToSubKeyConWhere,
                    )
                }
                return@awaitVarNameList String() to null
            }
            val varNameToValueStrAndExitSignal =
                deferredVarNameToValueStrAndBreakSignal?.await()
            loopKeyToAsyncDeferredVarNameValueStrMap?.clearVarName(
                curMapLoopKey,
                awaitVarName,
            )
            val varNameToValueStr = varNameToValueStrAndExitSignal?.first
            val exitSignal = varNameToValueStrAndExitSignal?.second
            val varName = varNameToValueStr?.first
                ?: return@awaitVarNameList String() to null
            if(
                varName.startsWith(asyncRunPrefix)
            ) return@awaitVarNameList String() to null
            val valueStr = varNameToValueStr.second
            varName to valueStr
        }.toMap()
        awaitVarNameValueStrMap.forEach {
                (varName, bitmap) ->
            privateLoopKeyVarNameValueStrMapClass.put(
                curMapLoopKey,
                varName,
                bitmap
            )
        }
        val sIfKey = SettingActionKeyManager.SettingReturnManager.SettingReturnKey.S_IF.key
        val ifMapList = actionImportPairListBeforeReplace.filter {
                mainSubKeyPair ->
            val mainSubKey = mainSubKeyPair.first
//            val mainSubKeyMapSrc = mainSubKeyPair.second
            mainSubKey == sIfKey
        }
        IfErrManager.isMultipleSpecifyErr(
            context,
            ifMapList.size,
            sIfKeyName,
            keyToSubKeyConWhere,
        ).let {
                isMultipleSpecifyErr ->
            if(
                !isMultipleSpecifyErr
            ) return@let
            settingActionExitManager.setExitSignal(
                SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
            )
            return blankReturnValueStr
        }
        val ifProcName = QuoteTool.trimBothEdgeQuote(
            actionImportMapBeforeReplace.get(
                SettingActionKeyManager.ActionImportManager.ActionImportKey.S_IF.key
            )
        ).let {
            CmdClickMap.replaceByBackslashToNormal(
                it,
                varNameToValueStrMap,
            )
        }
        val argsPairList = CmdClickMap.createMap(
            actionImportMapBeforeReplace.get(
                SettingActionKeyManager.ActionImportManager.ActionImportKey.ARGS.key
            ),
            valueSeparator
        ).asSequence().filter {
            it.first.isNotEmpty()
        }.map {
                argNameToValueStr ->
            argNameToValueStr.first to CmdClickMap.replaceByBackslashToNormal(
                argNameToValueStr.second,
                varNameToValueStrMap
            )
        }.toList()
        val isImportToErrType = when(ifProcName.isEmpty()) {
            true -> true to null
            else -> {
                SettingIfManager.handle(
                    sIfKeyName,
//                    judgeTargetStr,
                    argsPairList,
                    varNameToValueStrMap
                )
            }
        }
        val errType = isImportToErrType.second
        if(errType != null){
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_IF,
                errType.errMessage,
                keyToSubKeyConWhere
            )
            settingActionExitManager.setExitSignal(
                SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
            )
            return blankReturnValueStr
        }
        val isImport = isImportToErrType.first ?: false
        if(
            !isImport
        ) return blankReturnValueStr
        val importPathSrc = QuoteTool.trimBothEdgeQuote(
            actionImportMapBeforeReplace.get(
                SettingActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
            )?.let {
                importPathSrc ->
                CmdClickMap.replaceByBackslashToNormal(
                    importPathSrc,
                    varNameToValueStrMap
                )
            }
        )
        val importRepMapBeforeReplace = makeRepValHolderMap(
            actionImportMapBeforeReplace.get(
                SettingActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
            )
        )
        val varNameToValueStrMapPlusAwait =
            varNameToValueStrMap + awaitVarNameValueStrMap
        val importVarNameToValueStrMap =
            importRepMapBeforeReplace.asSequence().map {
                (_, valueStr) ->
                val blankMapPair =
                    String() to String()
                if(
                    !VarMarkTool.matchStringVarName(
                        valueStr
                    )
                ) return@map blankMapPair
                val importKey = VarMarkTool.convertVarName(valueStr)
                val importValueStr = varNameToValueStrMapPlusAwait.get(importKey)
                    ?: return@map blankMapPair
                importKey to importValueStr
            }.filter {
                (importKey, _) ->
                importKey.isNotEmpty()
            }.toMap()
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "limpot.txt").absolutePath,
//            listOf(
//                "curMapLoopKey: ${curMapLoopKey}",
//                "importMapBeforeReplace: ${actionImportMapBeforeReplace}",
//                "importVarNameToValueStrMap: ${importVarNameToValueStrMap}"
//            ).joinToString("\n\n") + "\n\n===========\n\n"
//        )
//        val importRepMap =
//            CmdClickMap.MapReplacer.replaceToPairList(
//                importRepMapBeroreReplace,
//                varNameToValueStrMap
//            ).toMap()
        val isBeforeImportErr = withContext(Dispatchers.IO) {
            val isCircleImportErrJob = async {
                ImportErrManager.isCircleImportErr(
                    context,
                    originImportPathList,
                    importPathSrc,
                    keyToSubKeyConWhere,
                )
            }
            val isNotExistJob = async {
                ImportErrManager.isNotExist(
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
        ) return blankReturnValueStr
        val importSrcConBeforeReplace = makeActionImportSrcCon(
            context,
            fannelInfoMap,
            setReplaceVariableMap,
            null,
            null,
            null,
            importPathSrc,
        ).replace(
            Regex("[${SettingActionKeyManager.landSeparator}]+$"),
            String(),
        )
        val importSrcCon = CmdClickMap.replaceHolderForJsAction(
            importSrcConBeforeReplace,
            importRepMapBeforeReplace
        )
        val importedKeyToSubKeyConList =
            SettingActionKeyManager.KeyToSubKeyMapListMaker.make(importSrcCon)
        val settingKeyToVarNameListForReturn =
            SettingActionKeyManager.makeSettingKeyToVarNameListForReturn(
                importedKeyToSubKeyConList
            ).let {
                SettingActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(
                    it.asSequence()
                )
            }
        val isErr = withContext(Dispatchers.IO) {
            val keyToSubKeyConWhereInImportPath = listOf(
                keyToSubKeyConWhere,
                "by import path $importPathSrc"
            ).joinToString("\n")
//            val isImportShadowVarMarkErrJob = async {
//                ImportErrManager.isImportShadowVarMarkErr(
//                    context,
//                    importPathSrc,
//                    importSrcConBeforeReplace,
//                    importRepMapBeforeReplace,
//                    keyToSubKeyConWhere,
//                )
//            }
//                    val isGlobalVarNameErrWithRunPrefixJob = async {
//                        ImportErrManager.isGlobalVarNameExistErrWithRunPrefix(
//                            context,
//                            settingKeyToVarNameList,
//                            topIAcVarName,
//                            keyToSubKeyConWhereInImportPath
//                        )
//                    }
//                    val isGlobalVarNameMultipleExistErrWithoutRunPrefixJob = async {
//                        ImportErrManager.isGlobalVarNameMultipleErrWithoutRunPrefix(
//                            context,
//                            settingKeyToVarNameList,
//                            topIAcVarName,
//                            keyToSubKeyConWhereInImportPath
//                        )
//                    }
            val isSettingReturnNotLastErrWithoutRunPrefixJob = async {
                ImportErrManager.isGlobalVarNameNotLastErrWithoutRunPrefix(
                    context,
                    settingKeyToVarNameListForReturn.toList(),
                    importedKeyToSubKeyConList,
                    topIAcVarName,
                    keyToSubKeyConWhereInImportPath
                )
            }
//            isImportShadowVarMarkErrJob.await()
//                            || isGlobalVarNameErrWithRunPrefixJob.await()
//                            || isGlobalVarNameMultipleExistErrWithoutRunPrefixJob.await()
                    isSettingReturnNotLastErrWithoutRunPrefixJob.await()
        }
        if(
            isErr
        ) return blankReturnValueStr
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
                awaitVarNameValueStrMap
            ),
            importVarNameToValueStrMap,
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
        context: Context,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
        importPath: String,
//        currentAppDirPath: String,
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
            File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath,
            setReplaceVariableMap,
            busyboxExecutor,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            importPath,
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