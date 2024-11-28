package com.puutaro.commandclick.proccess.edit.setting_action

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.broadcast.extra.ErrLogExtraForTerm
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.ErrLogBroadcastManagerForTerm.LogDatetime
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingFuncManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime

class SettingActionManager {

    companion object {

        val globalVarNameRegex = "[A-Z0-9_]+".toRegex()

        object BeforeActionImportMapManager {
            private val beforeActionImportMap = mutableMapOf<String, String>()
            private val mutex = Mutex()
            suspend fun get(
                importPath: String
            ): String? {
                mutex.withLock {
                    return beforeActionImportMap.get(importPath)
                }
            }
            suspend fun put(
                importPath: String,
                importCon: String
            ) {
                mutex.withLock {
                    beforeActionImportMap.put(importPath, importCon)
                }
            }

            suspend fun init() {
                mutex.withLock {
                    beforeActionImportMap.clear()
                }
            }
        }

        private object ErrLogger {

            private val durationSec = 5

            object LogDatetime {
                private var beforeOutputTime = LocalDateTime.parse("2020-02-15T21:30:50")
                private val mutex = Mutex()

                suspend fun update(
                    datetime: LocalDateTime
                ) {
                    mutex.withLock {
                        beforeOutputTime = datetime
                    }
                }

                suspend fun get(): LocalDateTime {
                    mutex.withLock {
                        return beforeOutputTime
                    }
                }
            }

            suspend fun sendErrLog(
                context: Context?,
                errMessage: String,
                keyToSubKeyConWhere: String,
            ) {
                val currentDatetime =
                    withContext(Dispatchers.IO) {
                    LocalDateTime.now()
                }
                val diffSec = withContext(Dispatchers.IO) {
                    val beforeOutputTime = LogDatetime.get()
                    LocalDatetimeTool.getDurationSec(
                        beforeOutputTime,
                        currentDatetime
                    )
                }
                if(diffSec < durationSec) return
                withContext(Dispatchers.IO) {
                    LogDatetime.update(
                        currentDatetime
                    )
                }
                val spanKeyToSubKeyConWhere =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        keyToSubKeyConWhere
                    )
                BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeTerm.ERR_LOG.action,
                    listOf(
                        ErrLogExtraForTerm.ERR_CONTENTS.schema to
                                "$errMessage about ${spanKeyToSubKeyConWhere}"
                    )
                )
            }
        }

        private fun makeSettingKeyToVarNameList(
            keyToSubKeyConList: List<Pair<String, String>>,
        ): List<Pair<String, String>> {
            val defaultReturnPair = String() to String()
            val subKeySeparator = SettingActionKeyManager.subKeySepartor
            val settingKeyList =
                SettingActionKeyManager.SettingActionsKey.entries.map {
                    it.key
                }
            return keyToSubKeyConList.map {
                    keyToSubKeyCon ->
                val settingKey = keyToSubKeyCon.first
                if(
                    !settingKeyList.contains(settingKey)
                ) return@map defaultReturnPair
                val varName = keyToSubKeyCon.second.split(subKeySeparator).firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@map defaultReturnPair
                settingKey to varName
            }.filter {
                it.first.isNotEmpty()
                        && it.second.isNotEmpty()
            }
        }

//        object GlobalExitManager {
//            private var globalEdit = false
//
//            suspend fun get(
//            ): Boolean {
//                mutex.withLock {
//                    return globalEdit
//                }
//            }
//            private val mutex = Mutex()
//            suspend fun exit(
//            ) {
//                mutex.withLock {
//                    globalEdit = true
//                }
//            }
//
//            suspend fun init() {
//                mutex.withLock {
//                    globalEdit = false
//                }
//            }
//        }
    }

    suspend fun exec(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        keyToSubKeyCon: String?,
        keyToSubKeyConWhere: String,
    ): Map<String, String> {
        val keyToSubKeyConList = makeSettingActionKeyToSubKeyList(
            fragment,
            fannelInfoMap,
            keyToSubKeyCon,
            setReplaceVariableMapSrc,
            null,
        )
        val settingActionExecutor = SettingActionExecutor(
            WeakReference(fragment),
            fannelInfoMap,
            setReplaceVariableMapSrc,
            busyboxExecutor,
        )
        settingActionExecutor.makeResultLoopKeyToVarNameValueMap(
            keyToSubKeyConList,
            SettingActionExecutor.mapRoopKeyUnit,
            keyToSubKeyConWhere,
            null,
        )
        return settingActionExecutor.getResultLoopKeyToVarNameValueMap()
    }

    private fun makeSetRepValMap(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        extraRepValMap: Map<String, String>?
    ): Map<String, String>? {
        val virtualSubFannelPath = VirtualSubFannel.makePath(
            fannelInfoMap
        )
        val setReplaceVariableMapSrc = FannelInfoTool.getReplaceVariableMap(
            fragment,
            virtualSubFannelPath
        )
        val jsRepValMapBeforeConcat = CmdVariableReplacer.replace(
            virtualSubFannelPath,
            setReplaceVariableMapSrc
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "repVal.txt").absolutePath,
//            listOf(
//                "virtualSubFannelPath: ${virtualSubFannelPath}",
//                "jsRepValMapBeforeConcat: ${jsRepValMapBeforeConcat}",
//                "jsRepValMap: ${CmdClickMap.concatRepValMap(
//                    jsRepValMapBeforeConcat,
//                    extraRepValMap
//                )}",
//            ).joinToString("\n")
//        )
        return CmdClickMap.concatRepValMap(
            jsRepValMapBeforeConcat,
            extraRepValMap
        )
    }

    private fun makeSettingActionKeyToSubKeyList(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        keyToSubKeyCon: String?,
        setReplaceVariableMapSrc: Map<String, String>?,
        repValsMap: Map<String, String>?
    ): List<Pair<String, String>>? {
        val setReplaceVariableMap = makeSetRepValMap(
            fragment,
            fannelInfoMap,
            setReplaceVariableMapSrc
        )
        val keyToSubKeyConWithReflectRepValDefalt = CmdClickMap.replaceHolderForJsAction(
            keyToSubKeyCon ?: String(),
            repValsMap
        )
        val keyToSubKeyConList = KeyToSubKeyMapListMaker.make(
            keyToSubKeyConWithReflectRepValDefalt,
        )
        if (
            keyToSubKeyConList.isEmpty()
        ) return null
        return keyToSubKeyConList
    }


    class SettingActionExecutor(
        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: Map<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
    ) {

        private val loopKeyToVarNameValueMap = mutableMapOf<String, MutableMap<String, String>>()
        private val innerLoopKeyVarNameValueMap = mutableMapOf<String, MutableMap<String, String>>()
        private var exitSignal = false
        private val escapeRunPrefix = SettingVar.escapeRunPrefix

        companion object {
            const val mapRoopKeyUnit = "loop"
            private const val mapLoopKeySeparator = "___"
        }


        private fun addLoopKey(
            curMapLoopKey: String
        ): String {
            return listOf(
                curMapLoopKey,
                mapRoopKeyUnit
            ).joinToString(mapLoopKeySeparator)
        }

        private fun removeLoopKey(
            curMapLoopKey: String
        ): String {
            return curMapLoopKey.removeSuffix(
                "${mapLoopKeySeparator}${mapRoopKeyUnit}"
            )
        }

        fun getResultLoopKeyToVarNameValueMap(): Map<String, String> {
            return loopKeyToVarNameValueMap.get(mapRoopKeyUnit)
                ?: emptyMap()
        }


        suspend fun makeResultLoopKeyToVarNameValueMap(
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            keyToSubKeyConWhere: String,
            renewalVarName: String?
        ) {
            val context = fragmentRef.get()?.context
                ?: return
            loopKeyToVarNameValueMap.get(curMapLoopKey)?.clear()
            innerLoopKeyVarNameValueMap.get(curMapLoopKey)?.clear()
            if(
                keyToSubKeyConList.isNullOrEmpty()
                ) return
            VarErrManager.isNotUseVarErr(
                context,
                keyToSubKeyConList,
                keyToSubKeyConWhere,
            ).let {
                isNotUseVarErr ->
                if(isNotUseVarErr) return
            }
            VarErrManager.isRunPrefixUseErr(
                context,
                keyToSubKeyConList,
                keyToSubKeyConWhere,
            ).let {
                    isRunPrefixUseErr ->
                if(isRunPrefixUseErr) return
            }
            VarErrManager.isSameVarNameErr(
                context,
                makeSettingKeyToVarNameList(keyToSubKeyConList),
                keyToSubKeyConWhere,
            ).let {
                    isSameVarNameErr ->
                if(isSameVarNameErr) return
            }
            keyToSubKeyConList.forEach {
                keyToSubKeyConSrc ->
//                val globalEdit = GlobalExitManager.get()
//                globalEdit
//                    ||
                if(
                    exitSignal
                ) return
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sMap.txt").absolutePath,
//                    listOf(
//                        "curMapLoopKey: ${curMapLoopKey}",
//                        "renewalVarName: ${renewalVarName}",
//                        "keyToSubKeyConSrc: ${keyToSubKeyConSrc}",
//                        "loopKeyToVarNameValueMap: ${loopKeyToVarNameValueMap}",
//                        "innerLoopKeyVarNameValueMap: ${innerLoopKeyVarNameValueMap}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                val keyToSubKeyCon = let {
                    val repMap =
                        (setReplaceVariableMapSrc ?: emptyMap()) +
                                (loopKeyToVarNameValueMap.get(curMapLoopKey) ?: emptyMap()) +
                                (innerLoopKeyVarNameValueMap.get(curMapLoopKey) ?: emptyMap())
                    val firstCon = CmdClickMap.replace(
                        keyToSubKeyConSrc.first,
                        repMap
                    )
                    val secondCon = CmdClickMap.replace(
                        keyToSubKeyConSrc.second,
                        repMap
                    )
                    firstCon to secondCon
                }
                val curSettingActionKeyStr = keyToSubKeyCon.first
                val curSettingActionKey = SettingActionKeyManager.SettingActionsKey.entries.firstOrNull {
                    it.key == curSettingActionKeyStr
                } ?: return@forEach
                val subKeyCon = keyToSubKeyCon.second
                VarErrManager.isNotReplaceVarErr(
                    context,
                    subKeyCon,
                    keyToSubKeyConWhere,
                ).let {
                        isNotReplaceVarErr ->
                    if(isNotReplaceVarErr) return
                }
                when(
                    curSettingActionKey
                ){
                    SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR -> {
                        val importPathAndRenewalVarNameToImportCon = SettingImport.makeImportPathAndRenewalVarNameToImportCon(
                            context,
                            fannelInfoMap,
                            setReplaceVariableMapSrc,
                            keyToSubKeyCon,
                            keyToSubKeyConWhere
                        )
                        val importPath = importPathAndRenewalVarNameToImportCon.first
                        if(
                            importPath.isEmpty()
                        ) return@forEach
                        val renewalVarNameToImportCon = importPathAndRenewalVarNameToImportCon.second
                        val renewalVarNameSrc =
                            renewalVarNameToImportCon.first
                        if(
                            renewalVarNameSrc.isEmpty()
                        ) return@forEach
                        val importedKeyToSubKeyConList =
                            renewalVarNameToImportCon.second
                        if(
                            importedKeyToSubKeyConList.isEmpty()
                        ) return@forEach
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "sImport.txt").absolutePath,
//                            listOf(
//                                "addLoopKey(curMapLoopKey): ${addLoopKey(curMapLoopKey)}",
//                                "renewalVarNameSrc: ${renewalVarNameSrc}",
//                                "importPath: ${importPath}",
//                                "importedKeyToSubKeyConList: ${importedKeyToSubKeyConList}",
//                            ).joinToString("\n") + "\n\n=============\n\n"
//                        )
                        makeResultLoopKeyToVarNameValueMap(
                            importedKeyToSubKeyConList,
                            addLoopKey(curMapLoopKey),
                            "${importPath} by imported",
                            renewalVarNameSrc,
                        )
                        if(
                            renewalVarName.isNullOrEmpty()
                        ) return@forEach
                        val isNotRunPrefix = !renewalVarName.startsWith(escapeRunPrefix)
                        val removedLoopKey = removeLoopKey(curMapLoopKey)
                        val proposalRenewalVarNameSrcInnnerMapValue = innerLoopKeyVarNameValueMap.get(
                            addLoopKey((curMapLoopKey))
                        )?.get(
                            renewalVarNameSrc
                        )
                        if(
                            isNotRunPrefix
                            && !proposalRenewalVarNameSrcInnnerMapValue.isNullOrEmpty()
                            ){
                            innerLoopKeyVarNameValueMap.get(
                                removedLoopKey
                            ).let {
                                    curInnerMapLoopKeyVarNameValueMap ->
                                when(curInnerMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                    false -> curInnerMapLoopKeyVarNameValueMap.put(
                                        renewalVarName,
                                        proposalRenewalVarNameSrcInnnerMapValue
                                    )
                                    else -> innerLoopKeyVarNameValueMap.put(
                                        removedLoopKey,
                                        mutableMapOf(
                                            renewalVarName to proposalRenewalVarNameSrcInnnerMapValue
                                        )
                                    )
                                }
                            }
                        }
                        val proposalRenewalVarNameSrcMapValue = loopKeyToVarNameValueMap.get(
                            addLoopKey((curMapLoopKey))
                        )?.get(
                            renewalVarNameSrc
                        )
                        if(
                            isNotRunPrefix
                            && !proposalRenewalVarNameSrcMapValue.isNullOrEmpty()
                        ) {
                            loopKeyToVarNameValueMap.get(
                                removedLoopKey
                            ).let {
                                    curMapLoopKeyVarNameValueMap ->
                                when(curMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                    false -> curMapLoopKeyVarNameValueMap.put(
                                        renewalVarName,
                                        proposalRenewalVarNameSrcMapValue
                                    )
                                    else -> loopKeyToVarNameValueMap.put(
                                        removedLoopKey,
                                        mutableMapOf(
                                            renewalVarName to proposalRenewalVarNameSrcMapValue
                                        )
                                    )
                                }
                            }
                        }
                    }
                    SettingActionKeyManager.SettingActionsKey.SETTING_VAR -> {
                        SettingVar().exec(
                            context,
                            curSettingActionKey.key,
                            subKeyCon,
                            busyboxExecutor,
                            renewalVarName,
                            keyToSubKeyConWhere,
                        )?.let {
                            val varValue = it.second
                            if(
                                varValue == SettingActionKeyManager.CommandMacro.EXIT_SIGNAL.name
                            ){
                                exitSignal = true
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "sExit.txt").absolutePath,
//                                    listOf(
//                                        "exitSignal: ${exitSignal}",
//                                    ).joinToString("\n") + "\n\n==========\n\n"
//                                )
                                return
                            }
                            val varName = it.first
                            innerLoopKeyVarNameValueMap.get(
                                curMapLoopKey
                            ).let {
                                curInnerMapLoopKeyVarNameValueMap ->
                                when(curInnerMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                    false -> curInnerMapLoopKeyVarNameValueMap.put(varName, varValue)
                                    else -> innerLoopKeyVarNameValueMap.put(
                                        curMapLoopKey,
                                        mutableMapOf(varName to varValue)
                                    )
                                }
                            }

                            val isGlobalForRawVar = globalVarNameRegex.matches(varName)
                            val isNotRunPrefix =
                                !renewalVarName.isNullOrEmpty()
                                        && !renewalVarName.startsWith(escapeRunPrefix)
                            val isRegisterToTopForInner = isGlobalForRawVar && isNotRunPrefix
                            val removedLoopKey = removeLoopKey(curMapLoopKey)
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "sinnerLoopKeyVarNameValueMap.txt").absolutePath,
//                                listOf(
//                                    "varName: ${varName}",
//                                    "isNotRunPrefix: ${isNotRunPrefix}",
//                                    "isRegisterToTopForInner: ${isRegisterToTopForInner}",
//                                    "removedLoopKey: ${removedLoopKey}",
//                                    "renewalVarName: ${renewalVarName}",
//                                    "varValue: ${varValue}",
//                                    "innerLoopKeyVarNameValueMap: ${innerLoopKeyVarNameValueMap}",
//                                ).joinToString("\n\n") + "\n\n=============\n\n"
//                            )
                            if(isRegisterToTopForInner && !renewalVarName.isNullOrEmpty()){
                                innerLoopKeyVarNameValueMap.get(
                                    removedLoopKey
                                ).let {
                                        curInnerMapLoopKeyVarNameValueMap ->
                                    when(curInnerMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                        false -> curInnerMapLoopKeyVarNameValueMap.put(renewalVarName, varValue)
                                        else -> innerLoopKeyVarNameValueMap.put(
                                            removedLoopKey,
                                            mutableMapOf(renewalVarName to varValue)
                                        )
                                    }
                                }
                            }

                            val isGlobalRegister = isGlobalForRawVar
                            if(isGlobalRegister) {
                                val varNameForPut = renewalVarName ?: varName
                                loopKeyToVarNameValueMap.get(
                                    removedLoopKey
                                ).let {
                                        curMapLoopKeyVarNameValueMap ->
                                    when(curMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                        false -> curMapLoopKeyVarNameValueMap.put(varNameForPut, varValue)
                                        else -> loopKeyToVarNameValueMap.put(
                                            removedLoopKey,
                                            mutableMapOf(varNameForPut to varValue)
                                        )
                                    }
                                }
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "sloopKeyToVarNameValueMap.txt").absolutePath,
//                                    listOf(
//                                        "curMapLoopKey: ${curMapLoopKey}",
//                                        "removedLoopKey: ${removedLoopKey}",
//                                        "varName: ${varName}",
//                                        "varNameForPut: ${varNameForPut}",
//                                        "isGlobalRegister: ${isGlobalRegister}",
//                                        "innerLoopKeyVarNameValueMap: ${innerLoopKeyVarNameValueMap}",
//                                        "loopKeyToVarNameValueMap: ${loopKeyToVarNameValueMap}",
//                                    ).joinToString("\n") + "\n\n==========\n\n"
//                                )
                            }
                        }
                    }
                }
            }
        }

        private object ImportErrManager {

            private val escapeRunPrefix = SettingVar.escapeRunPrefix
            private val sAdVarKeyName =
                SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key

            fun isNotExist(
                context: Context?,
                importPath: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    File(importPath).isFile
                ) {
                    return false
                }
                val spanSAdVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAdVarKeyName
                    )
                val spanImportPath =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        importPath
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        "import path not exist in ${spanSAdVarKeyName}: ${spanImportPath}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }

            fun isGlobalVarNameExistErrWithRunPrefix(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                renewalVarName: String,
                keyToSubKeyConWhere: String
            ): Boolean {
                if(
                    !renewalVarName.startsWith(escapeRunPrefix)
                ) return false
                val settingKeyToGlobalVarNameList = settingKeyToVarNameList.filter {
                        settingKeyToVarName ->
                    settingKeyToVarName.second.matches(globalVarNameRegex)
                }
                val isGlobalVarNameExistErr = settingKeyToGlobalVarNameList.isNotEmpty()
                if(
                    !isGlobalVarNameExistErr
                ) return false
                val spanSAdVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAdVarKeyName
                    )
                val spanGlobalVarNameListCon =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        settingKeyToGlobalVarNameList.map {
                            it.second
                        }.joinToString(". ")
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        "With ${escapeRunPrefix} prefix in ${spanSAdVarKeyName}, " +
                                "global (uppercase) var name must not exist: ${spanGlobalVarNameListCon}",
                        keyToSubKeyConWhere
                    )
                }
                return true
            }

            fun isGlobalVarNameMultipleErrWithoutRunPrefix(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                renewalVarName: String,
                keyToSubKeyConWhere: String
            ): Boolean {
                if(
                    renewalVarName.startsWith(escapeRunPrefix)
                ) return false
                val settingKeyToGlobalVarNameList = settingKeyToVarNameList.filter {
                        settingKeyToVarName ->
                    settingKeyToVarName.second.matches(globalVarNameRegex)
                }
                val isMultipleGlobalVarNameErr = settingKeyToGlobalVarNameList.size > 1
                if(
                    !isMultipleGlobalVarNameErr
                ) return false
                val spanSAdVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAdVarKeyName
                    )
                val spanGlobalVarNameListCon =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        settingKeyToGlobalVarNameList.map {
                            it.second
                        }.joinToString(", ")
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        "In ${spanSAdVarKeyName}, global (uppercase) var name must be one: ${spanGlobalVarNameListCon}",
                        keyToSubKeyConWhere
                    )
                }
                return true

            }

            fun isGlobalVarNameNotLastErrWithoutRunPrefix(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                renewalVarName: String,
                keyToSubKeyConWhere: String
            ): Boolean {
                if(
                    renewalVarName.startsWith(escapeRunPrefix)
                ) return false
                val lastSettingKeyToSubKeyCon = settingKeyToVarNameList.lastOrNull()
                    ?: return false
                val varName = lastSettingKeyToSubKeyCon.second
                val isGlobalVarName = globalVarNameRegex.matches(varName)
                if (
                    isGlobalVarName
                ) return false
                val spanSAdVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAdVarKeyName
                    )
                val spanGlobalVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        varName
                    )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisGlobalVarNameNotLastErrWithoutRunPrefix.txt").absolutePath,
//                    listOf(
//                        "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                        "lastSettingKeyToSubKeyCon: ${lastSettingKeyToSubKeyCon}",
//                    ).joinToString("\n")
//                )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        "Without ${escapeRunPrefix} prefix in ${spanSAdVarKeyName}, " +
                                "imported last var name must be global (uppercase): ${spanGlobalVarName}",
                        keyToSubKeyConWhere
                    )
                }
                return true
            }
        }

        private object VarErrManager {

            private val mainKeySeparator = SettingActionKeyManager.mainKeySeparator
            private val escapeRunPrefix = SettingVar.escapeRunPrefix

            private fun makeKeyToSubKeyListCon(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): String {
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val key = keyToSubKeyCon.first
                    val subKeyCon = keyToSubKeyCon.second
                    "${mainKeySeparator}${key}=${subKeyCon}"
                }.joinToString("\n")
            }

            fun isSameVarNameErr(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val varNameList = settingKeyToVarNameList.map {
                    it.second
                }
                val duplicateVarNameMap =
                    varNameList.groupingBy { it }.eachCount().filterValues { it > 1 }
                val duplicateVarNameList = duplicateVarNameMap.keys
                val isDuplicate = duplicateVarNameList.isNotEmpty()
                if(
                    !isDuplicate
                ) return false
                val spanVarNameListCon =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        duplicateVarNameList.joinToString(". ")
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        "Var name must be unique: ${spanVarNameListCon}",
                        keyToSubKeyConWhere,
                    )
                }
                return true

            }

            fun isGlobalVarFuncNullResultErr(
                context: Context?,
                varName: String,
                returnValue: String?,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    returnValue != null
                ) return false
                val isGlobal = globalVarNameRegex.matches(varName)
                if(
                    !isGlobal
                ) return false
                val spanVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        varName
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        "In global (uppercase), func result must be exist: ${spanVarName}",
                        keyToSubKeyConWhere,
                    )
                }
                return true

            }

            fun isRunPrefixUseErr(
                context: Context?,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val settingKeyToRunVarNameList = makeSettingKeyToInnerRunVarNameList(
                    keyToSubKeyConList
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyToRunVarNameList: ${settingKeyToRunVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                val keyToSubKeyListCon = makeKeyToSubKeyListCon(
                    keyToSubKeyConList,
                )
                settingKeyToRunVarNameList.forEach {
                        settingKeyToVarName ->
                    val runVarName = settingKeyToVarName.second
                    val varNameWithDoll = "${'$'}{${runVarName}}"
                    val isContain = keyToSubKeyListCon.contains(varNameWithDoll)
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "sContetn.txt").absolutePath,
//                        listOf(
//                            "settingKeyToRunVarNameList: ${settingKeyToRunVarNameList}",
//                            "varNameWithDoll: ${varNameWithDoll}",
//                            "keyToSubKeyListCon: ${keyToSubKeyListCon}",
//                            "isContain: ${isContain}",
//                        ).joinToString("\n\n") + "\n\n=============\n\n"
//                    )
                    if(
                        !isContain
                    ) return@forEach
                    val settingKeyName = settingKeyToVarName.first
                    val spanSettingKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            settingKeyName
                        )
                    val spanRunVarName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            runVarName
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            "Forbidden to use run prefix var as variable:  ${spanSettingKeyName}: ${spanRunVarName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun isNotUseVarErr(
                context: Context?,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val settingKeyToVarNameList = makeSettingKeyToInnerVarNameList(
                    keyToSubKeyConList
                )
                val keyToSubKeyListCon = makeKeyToSubKeyListCon(
                    keyToSubKeyConList,
                )
                execIsNotUseVarErr(
                    context,
                    settingKeyToVarNameList,
                    keyToSubKeyListCon,
                    keyToSubKeyConWhere,
                ).let {
                    isNotUseVarErr ->
                    if(isNotUseVarErr) return true
                }
                return false
            }

            fun isNotReplaceVarErr(
                context: Context?,
                subKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                execIsNotReplaceVarErr(
                    context,
                    subKeyListCon,
                    keyToSubKeyConWhere,
                ).let {
                        isNotReplaceVar ->
                    if(isNotReplaceVar) return true
                }
                return false
            }

            private fun execIsNotReplaceVarErr(
                context: Context?,
                subKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val replaceKeyToSubKeyListCon = subKeyListCon.replace(
                    "${'$'}{${SettingVar.itPronoun}}",
                    String()
                )
//                varNameList.forEach {
//                        varName ->
//                    if(
//                        varName.startsWith(escapeRunPrefix)
//                    ) return@forEach
//                    val varNameWithDoll = "${'$'}{${varName}}"
//                    replaceKeyToSubKeyListCon = replaceKeyToSubKeyListCon.replace(
//                        varNameWithDoll,
//                        String()
//                    )
//                }
                val dolVarMarkRegex = Regex("[$][{][a-zA-Z0-9_]+[}]")
                val noReplaceVar = dolVarMarkRegex.find(replaceKeyToSubKeyListCon)?.value
                if(
                    noReplaceVar.isNullOrEmpty()
                    || noReplaceVar.startsWith("${'$'}{${escapeRunPrefix}")
                ) return false
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sexecIsNotReplaceVarErr.txt").absolutePath,
//                    listOf(
//                        "subKeyListCon: ${subKeyListCon}",
//                        "replaceKeyToSubKeyListCon: ${replaceKeyToSubKeyListCon}",
//                        "noReplaceVar: ${noReplaceVar}",
//                    ).joinToString("\n")
//                )
                val spanNoReplaceVar =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        noReplaceVar
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        "Not replace var: ${spanNoReplaceVar}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }

            private fun execIsNotUseVarErr(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                keyToSubKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                settingKeyToVarNameList.forEach {
                        settingKeyToVarName ->
                    val varName = settingKeyToVarName.second
                    if(
                        varName.startsWith(escapeRunPrefix)
                    ) return@forEach
                    val varNameWithDoll = "${'$'}{${varName}}"
                    val isContain = keyToSubKeyListCon.contains(varNameWithDoll)
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "sLog.txt").absolutePath,
//                        listOf(
//                            "varNameRegex: ${varNameWithDoll}",
//                            "keyToSubKeyListCon: ${keyToSubKeyListCon}"
//                        ).joinToString("\n\n") + "\n\n===========n\n\n"
//                    )
                    if(isContain) return@forEach
                    val settingKeyName = settingKeyToVarName.first
                    val spanSettingKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            settingKeyName
                        )
                    val spanVarName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            varName
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            "Not use inner ${spanSettingKeyName}: ${spanVarName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            private fun makeSettingKeyToInnerVarNameList(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, String>> {
                val defaultReturnPair = String() to String()
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val settingKeyList =
                    SettingActionKeyManager.SettingActionsKey.entries.map {
                        it.key
                    }
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        !settingKeyList.contains(settingKey)
                    ) return@map defaultReturnPair
                    val varName = keyToSubKeyCon.second.split(subKeySeparator).firstOrNull()?.let {
                        QuoteTool.trimBothEdgeQuote(it)
                    } ?: return@map defaultReturnPair
                    val isInnerVarName = !globalVarNameRegex.matches(varName)
                    when(isInnerVarName){
                        false -> String() to String()
                        else -> settingKey to varName
                    }
                }.filter {
                    it.first.isNotEmpty()
                            && it.second.isNotEmpty()
                }
            }

            private fun makeSettingKeyToInnerRunVarNameList(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, String>> {
                val defaultReturnPair = String() to String()
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val settingKeyList =
                    SettingActionKeyManager.SettingActionsKey.entries.map {
                        it.key
                    }
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        !settingKeyList.contains(settingKey)
                    ) return@map defaultReturnPair
                    val varName = keyToSubKeyCon.second.split(subKeySeparator).firstOrNull()?.let {
                        QuoteTool.trimBothEdgeQuote(it)
                    } ?: return@map defaultReturnPair
                    val isRunVar = varName.startsWith(escapeRunPrefix)
                    when(isRunVar){
                        false -> String() to String()
                        else -> settingKey to varName
                    }
                }.filter {
                    it.first.isNotEmpty()
                            && it.second.isNotEmpty()
                }
            }
        }


        private object SettingImport {

            private val valueSeparator = SettingActionKeyManager.valueSeparator
            private val settingActionVarKey = SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
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
                context: Context?,
                fannelInfoMap: Map<String, String>,
                setReplaceVariableMap: Map<String, String>?,
                keyToSubKeyCon: Pair<String, String>?,
                keyToSubKeyConWhere: String,
            ): Pair<String,
                    Pair< String, List<Pair<String, String>> >
                    > {
                val keyToSubKeyContents = listOf(
                    settingActionVarKey,
                    keyToSubKeyCon?.second ?: String()
                ).joinToString("=")
                val actionImportMap = ImportMapMaker.comp(
                    keyToSubKeyContents,
                    "${settingActionVarKey}="
                )
                val renewalVarName = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        settingActionVarKey
                    )
                )
                val judgeTargetStr = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.S_IF.key
                    )
                )
                val argsPairList = CmdClickMap.createMap(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.ARGS.key
                    ),
                    valueSeparator
                )
                val isImport = when(judgeTargetStr.isEmpty()) {
                    true -> true
                    else -> SettingIfManager.handle(
                        judgeTargetStr,
                        argsPairList
                    )
                }
                val blankReturnValue = String() to Pair(String(), emptyList<Pair<String, String>>())
                if(
                    isImport != true
                ) return blankReturnValue
                val importPathSrc = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
                    )
                )
                ImportErrManager.isNotExist(
                    context,
                    importPathSrc,
                    keyToSubKeyConWhere
                ).let {
                    isNotExist ->
                    if(isNotExist) return blankReturnValue
                }
                val importSrcConBeforeReplace = makeActionImportSrcCon(
                    context,
                    importPathSrc,
                    fannelInfoMap,
                    setReplaceVariableMap,
                )
                val repMap = makeRepValHolderMap(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
                    )
                )
                val importSrcCon = CmdClickMap.replaceHolderForJsAction(
                    importSrcConBeforeReplace,
                    repMap
                )
                val importedKeyToSubKeyConList = KeyToSubKeyMapListMaker.make(importSrcCon)
                val settingKeyToVarNameList = makeSettingKeyToVarNameList(
                    importedKeyToSubKeyConList
                )
                ImportErrManager.isGlobalVarNameExistErrWithRunPrefix(
                    context,
                    settingKeyToVarNameList,
                    renewalVarName,
                    keyToSubKeyConWhere
                ).let {
                    isGlobalVarNameErrWithRunPrefix ->
                    if(isGlobalVarNameErrWithRunPrefix) return blankReturnValue
                }
                ImportErrManager.isGlobalVarNameMultipleErrWithoutRunPrefix(
                    context,
                    settingKeyToVarNameList,
                    renewalVarName,
                    keyToSubKeyConWhere
                ).let {
                        isGlobalVarNameMultipleExistErrWithoutRunPrefix ->
                    if(isGlobalVarNameMultipleExistErrWithoutRunPrefix) return blankReturnValue
                }
                ImportErrManager.isGlobalVarNameNotLastErrWithoutRunPrefix(
                    context,
                    settingKeyToVarNameList,
                    renewalVarName,
                    keyToSubKeyConWhere
                ).let {
                        isGlobalVarNameNotLastErrWithoutRunPrefix ->
                    if(isGlobalVarNameNotLastErrWithoutRunPrefix) return blankReturnValue
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "smakeImportPathAndRenewalVarNameToImportCon.txt").absolutePath,
//                    listOf(
//                        "renewalVarNameSrc: ${renewalVarName}",
//                        "importSrcCon: ${importSrcCon}",
//                        "importedKeyToSubKeyConList: ${importedKeyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                return importPathSrc to Pair(
                    renewalVarName,
                    importedKeyToSubKeyConList
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

        private class SettingVar {

            companion object {
                val escapeRunPrefix = "run"
                val itPronoun = "it"
            }
            private val itReplaceVarStr = "${'$'}{${itPronoun}}"
            private var itPronounValue = String()
            private var isNext = true
            private val valueSeparator = SettingActionKeyManager.valueSeparator

            fun exec(
                context: Context?,
                settingVarKey: String,
                subKeyCon: String,
                busyboxExecutor: BusyboxExecutor?,
                renewalVarName: String?,
                keyToSubKeyConWhere: String,
            ): Pair<String, String>? {
                val mainSubKeyPairList = makeMainSubKeyPairList(
                    settingVarKey,
                    subKeyCon,
                )
                val settingVarName = PairListTool.getValue(
                    mainSubKeyPairList,
                    settingVarKey
                )?.get(settingVarKey)
                    ?: return null
                mainSubKeyPairList.forEach {
                        mainSubKeyPair ->
                    val mainSubKey = mainSubKeyPair.first
                    val mainSubKeyMap = mainSubKeyPair.second.map {
                        replaceItPronoun(it.key) to replaceItPronoun(it.value)
                    }.toMap()
                    val innerSubKeyClass = SettingActionKeyManager.SettingSubKey.entries.firstOrNull {
                        it.key == mainSubKey
                    } ?: return@forEach
                    when(innerSubKeyClass) {
                        SettingActionKeyManager.SettingSubKey.SETTING_VAR,
                        SettingActionKeyManager.SettingSubKey.ARGS -> {}
                        SettingActionKeyManager.SettingSubKey.VALUE -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            itPronounValue = mainSubKeyMap.get(mainSubKey)
                                ?: String()
                            isNext = true
                        }
                        SettingActionKeyManager.SettingSubKey.RETURN -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            return settingVarName to
                                    (mainSubKeyMap.get(mainSubKey) ?: String())
                        }
                        SettingActionKeyManager.SettingSubKey.FUNC -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val funcTypeDotMethod = mainSubKeyMap.get(mainSubKey)
                                ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMap.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            )
                            val result = SettingFuncManager.handle(
                                context,
                                funcTypeDotMethod,
                                argsPairList,
                                busyboxExecutor
                            )
                            VarErrManager.isGlobalVarFuncNullResultErr(
                                context,
                            renewalVarName ?: settingVarName,
                                result,
                                keyToSubKeyConWhere,
                            ).let {
                                isGlobalVarFuncNullResultErr ->
                                if(
                                    isGlobalVarFuncNullResultErr
                                ) return null
                            }
                            itPronounValue = result ?: String()
                        }
                        SettingActionKeyManager.SettingSubKey.S_IF -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val judgeTargetStr = mainSubKeyMap.get(mainSubKey)
                                ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMap.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            )
                            SettingIfManager.handle(
                                judgeTargetStr,
                                argsPairList
                            )?.let {
                                isNext = it
                            }
                        }
                    }
                    if(innerSubKeyClass != SettingActionKeyManager.SettingSubKey.S_IF){
                        isNext = true
                    }
                }
                val isEscape =
                    settingVarName.startsWith(escapeRunPrefix)
                            && itPronounValue != SettingActionKeyManager.CommandMacro.EXIT_SIGNAL.name
                return when(isEscape){
                    true -> null
                    else -> settingVarName to itPronounValue
                }
            }

            fun makeMainSubKeyPairList(
                settingVarKey: String,
                subKeyCon: String,
            ): List<
                    Pair<
                            String,
                            Map<String, String>
                            >
                    > {
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val subKeyToConList = CmdClickMap.createMap(
                    "${settingVarKey}=${subKeyCon}",
                    subKeySeparator
                )
                val argsSubKey = SettingActionKeyManager.SettingSubKey.ARGS.key
                return subKeyToConList.mapIndexed {
                        index, subKeyToCon ->
                    val innerSubKeyName = subKeyToCon.first
                    val innerSubKeyClass = SettingActionKeyManager.SettingSubKey.entries.firstOrNull {
                        it.key == innerSubKeyName
                    } ?: return@mapIndexed Pair(String(), emptyMap())
                    val innerSubKeyCon = subKeyToCon.second
                    when(innerSubKeyClass) {
                        SettingActionKeyManager.SettingSubKey.SETTING_VAR,
                        SettingActionKeyManager.SettingSubKey.VALUE,
                        SettingActionKeyManager.SettingSubKey.RETURN -> {
                            val mainSubKeyMap = mapOf(
                                innerSubKeyName to innerSubKeyCon,
                            )
                            Pair(innerSubKeyName, mainSubKeyMap)
                        }
                        SettingActionKeyManager.SettingSubKey.FUNC,
                        SettingActionKeyManager.SettingSubKey.S_IF-> {
                            val funcPartMap = mapOf(
                                innerSubKeyName to innerSubKeyCon,
                            )
                            val argsMap = let {
                                val nextSubKeyToCon =
                                    subKeyToConList.getOrNull(index + 1)
                                        ?: return@let emptyMap()
                                val nextSubKeyName = nextSubKeyToCon.first
                                when (nextSubKeyName == argsSubKey) {
                                    false -> emptyMap()
                                    else -> mapOf(
                                        argsSubKey to nextSubKeyToCon.second
                                    )
                                }
                            }
                            Pair(innerSubKeyName, (funcPartMap + argsMap))
                        }
                        SettingActionKeyManager.SettingSubKey.ARGS
                            -> Pair(String(), emptyMap())
                    }
                }.filter {
                    it.first.isNotEmpty()
                }
            }

            private fun replaceItPronoun(con: String): String {
                return con.replace(itReplaceVarStr, itPronounValue)
            }
        }
    }



    private object PairListTool {

        fun getValue(
            pairList: List<Pair<String, Map<String, String>>>?,
            key: String
        ): Map<String, String>? {
            return getPair(
                pairList,
                key
            )
        }

        fun getPair(
            pairList: List<Pair<String, Map<String, String>>>?,
            key: String
        ): Map<String, String>? {
            if(
                pairList.isNullOrEmpty()
            ) return null
            return pairList.firstOrNull {
                it.first == key
            }?.second
        }
    }
}


private object KeyToSubKeyMapListMaker {

    private const val keySeparator = '|'
    private val settingActionsKeyPlusList =
        SettingActionKeyManager.SettingActionsKey.entries.map {
            it.key
        }

    fun make(
        keyToSubKeyCon: String?,
    ): List<Pair<String, String>> {
        val keyToSubKeyConListSrc = makeKeyToSubConPairListByValidKey(
            keyToSubKeyCon
        )
        val keyToSubKeyConListByValidKey =
            filterByValidKey(keyToSubKeyConListSrc)
        return keyToSubKeyConListByValidKey
    }

    private fun filterByValidKey(
        keyToSubKeyConList: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        return keyToSubKeyConList.filter {
            val mainKeyName = it.first
            settingActionsKeyPlusList.contains(mainKeyName)
        }
    }

    private fun makeKeyToSubConPairListByValidKey(
        keyToSubKeyCon: String?,
    ): List<Pair<String, String>> {
        return CmdClickMap.createMap(
            keyToSubKeyCon,
            keySeparator
        ).filter {
            val mainKey = it.first
            settingActionsKeyPlusList.contains(mainKey)
        }.map {
            val mainKey = it.first
            val subKeyAfterStr = it.second
            mainKey to subKeyAfterStr
        }
    }
}
