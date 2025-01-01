package com.puutaro.commandclick.proccess.edit.setting_action

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingFuncManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
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

            enum class SettingActionErrType {
                S_VAR,
                S_AC_VAR,
                FUNC,
                S_IF,
            }

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
                errType: SettingActionErrType,
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
                val errToastMessage =
                    Jsoup.parse(errMessage).text()
                val logErrMessage =
                    "[SETTING ACTION] (${errType.name}) $errMessage about ${spanKeyToSubKeyConWhere}"
                LogSystems.broadErrLog(
                    context,
                    errToastMessage,
                    logErrMessage,
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
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        keyToSubKeyCon: String?,
        keyToSubKeyConWhere: String,
        editConstraintListAdapterArg: EditConstraintListAdapter? = null,
    ): Map<String, String> {
        if(
            fragment == null
            || keyToSubKeyCon.isNullOrEmpty()
        ) return emptyMap()
        val keyToSubKeyConList = makeSettingActionKeyToSubKeyList(
            fragment,
            fannelInfoMap,
            keyToSubKeyCon,
            setReplaceVariableMapSrc,
            null,
        )
        if(
            keyToSubKeyConList.isNullOrEmpty()
        ) return emptyMap()
        val settingActionExecutor = SettingActionExecutor(
            WeakReference(fragment),
            fannelInfoMap,
            setReplaceVariableMapSrc,
            busyboxExecutor,
        )
        settingActionExecutor.makeResultLoopKeyToVarNameValueMap(
            editConstraintListAdapterArg,
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
        private val privateLoopKeyVarNameValueMap = mutableMapOf<String, MutableMap<String, String>>()
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
            editConstraintListAdapterArg: EditConstraintListAdapter?,
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            keyToSubKeyConWhere: String,
            topAcIVarName: String?
        ) {
            val fragment = fragmentRef.get()
                ?: return
            val context = fragment.context
                ?: return
            loopKeyToVarNameValueMap.get(curMapLoopKey)?.clear()
            privateLoopKeyVarNameValueMap.get(curMapLoopKey)?.clear()
            if(
                keyToSubKeyConList.isNullOrEmpty()
                ) return
            val isErr = withContext(Dispatchers.IO) {
                val isBlankSVarOrSAcVArErrJob = async {
                    VarErrManager.isBlankSVarOrSAcVArErr(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotUseVarErrJob = async {
                    VarErrManager.isNotUseVarErr(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val isRunPrefixUseErrJob = async {
                    VarErrManager.isRunPrefixUseErr(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val isSameVarNameErrJob = async {
                    VarErrManager.isSameVarNameErr(
                        context,
                        makeSettingKeyToVarNameList(keyToSubKeyConList),
                        keyToSubKeyConWhere,
                    )
                }
                isBlankSVarOrSAcVArErrJob.await()
                        || isNotUseVarErrJob.await()
                        || isRunPrefixUseErrJob.await()
                        || isSameVarNameErrJob.await()
            }
            if(isErr) return
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
                                (privateLoopKeyVarNameValueMap.get(curMapLoopKey) ?: emptyMap())
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
                        val acIVarName =
                            renewalVarNameToImportCon.first
                        if(
                            acIVarName.isEmpty()
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
                            editConstraintListAdapterArg,
                            importedKeyToSubKeyConList,
                            addLoopKey(curMapLoopKey),
                            "${importPath} by imported",
                            acIVarName,
                        )
                        if(
                            topAcIVarName.isNullOrEmpty()
                        ) return@forEach
                        val isNotRunPrefix = !topAcIVarName.startsWith(escapeRunPrefix)
                        val removedLoopKey = removeLoopKey(curMapLoopKey)
                        val proposalRenewalVarNameSrcInnnerMapValue = privateLoopKeyVarNameValueMap.get(
                            addLoopKey(curMapLoopKey)
                        )?.get(acIVarName)
                        if(
                            isNotRunPrefix
                            && !proposalRenewalVarNameSrcInnnerMapValue.isNullOrEmpty()
                            ){
                            privateLoopKeyVarNameValueMap.get(
                                removedLoopKey
                            ).let {
                                    curPrivateMapLoopKeyVarNameValueMap ->
                                when(curPrivateMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                    false -> curPrivateMapLoopKeyVarNameValueMap.put(
                                        topAcIVarName,
                                        proposalRenewalVarNameSrcInnnerMapValue
                                    )
                                    else -> privateLoopKeyVarNameValueMap.put(
                                        removedLoopKey,
                                        mutableMapOf(
                                            topAcIVarName to proposalRenewalVarNameSrcInnnerMapValue
                                        )
                                    )
                                }
                            }
                        }
                        val proposalRenewalVarNameSrcMapValue = loopKeyToVarNameValueMap.get(
                            addLoopKey(curMapLoopKey)
                        )?.get(acIVarName)
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
                                        topAcIVarName,
                                        proposalRenewalVarNameSrcMapValue
                                    )
                                    else -> loopKeyToVarNameValueMap.put(
                                        removedLoopKey,
                                        mutableMapOf(
                                            topAcIVarName to proposalRenewalVarNameSrcMapValue
                                        )
                                    )
                                }
                            }
                        }
                    }
                    SettingActionKeyManager.SettingActionsKey.SETTING_VAR -> {
                        SettingVar().exec(
                            fragment,
                            curSettingActionKey.key,
                            subKeyCon,
                            busyboxExecutor,
                            editConstraintListAdapterArg,
                            topAcIVarName,
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
                            privateLoopKeyVarNameValueMap.get(
                                curMapLoopKey
                            ).let {
                                curPrivateMapLoopKeyVarNameValueMap ->
                                when(curPrivateMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                    false -> curPrivateMapLoopKeyVarNameValueMap.put(varName, varValue)
                                    else -> privateLoopKeyVarNameValueMap.put(
                                        curMapLoopKey,
                                        mutableMapOf(varName to varValue)
                                    )
                                }
                            }

                            val isGlobalForRawVar = globalVarNameRegex.matches(varName)
                            val isNotRunPrefix =
                                !topAcIVarName.isNullOrEmpty()
                                        && !topAcIVarName.startsWith(escapeRunPrefix)
                            val isRegisterToTopForPrivate = isGlobalForRawVar && isNotRunPrefix
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
                            if(isRegisterToTopForPrivate && !topAcIVarName.isNullOrEmpty()){
                                privateLoopKeyVarNameValueMap.get(
                                    removedLoopKey
                                ).let {
                                        curPrivateMapLoopKeyVarNameValueMap ->
                                    when(curPrivateMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                        false -> curPrivateMapLoopKeyVarNameValueMap.put(topAcIVarName, varValue)
                                        else -> privateLoopKeyVarNameValueMap.put(
                                            removedLoopKey,
                                            mutableMapOf(topAcIVarName to varValue)
                                        )
                                    }
                                }
                            }

                            val varNameForPut =
                                topAcIVarName
                                    ?: varName
                            val isGlobalRegister =
                                isGlobalForRawVar
                                    && globalVarNameRegex.matches(varNameForPut)
                            if(isGlobalRegister) {
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
                        ErrLogger.SettingActionErrType.S_AC_VAR,
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
                        ErrLogger.SettingActionErrType.S_AC_VAR,
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
                        ErrLogger.SettingActionErrType.S_AC_VAR,
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
                        ErrLogger.SettingActionErrType.S_AC_VAR,
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
                        ErrLogger.SettingActionErrType.S_VAR,
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
                        ErrLogger.SettingActionErrType.S_VAR,
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
                val settingKeyToRunVarNameList = makeSettingKeyToPrivateRunVarNameList(
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
                            ErrLogger.SettingActionErrType.S_VAR,
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
                val settingKeyToVarNameList = makeSettingKeyToPrivateVarNameList(
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


            fun isBlankSVarOrSAcVArErr(
                context: Context?,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val settingKeyToVarNameList = makeSettingKeyToPrivateVarNameListWithBlank(
                    keyToSubKeyConList
                )
                val settingKeyToBlankVarNamePair = settingKeyToVarNameList.firstOrNull {
                    val varName = it.second
                    varName.isEmpty()
                }
                if(
                    settingKeyToBlankVarNamePair == null
                ) return false
                val spanBlankSettingKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        settingKeyToBlankVarNamePair.first
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_VAR,
                        "${spanBlankSettingKeyName} must not blank",
                        keyToSubKeyConWhere,
                    )
                }
                return true
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
                        ErrLogger.SettingActionErrType.S_VAR,
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
                        FileSystems.writeFile(
                            File(UsePath.cmdclickDefaultAppDirPath, "lsVar.txt").absolutePath,
                            listOf(
                                "settingKeyToVarNameList: ${settingKeyToVarNameList}",
                                "keyToSubKeyListCon: ${keyToSubKeyListCon}",
                            ).joinToString("\n")
                        )
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.SettingActionErrType.S_VAR,
                            "Not use private ${spanSettingKeyName}: ${spanVarName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            private fun makeSettingKeyToPrivateVarNameList(
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
                    val isPrivateVarName = !globalVarNameRegex.matches(varName)
                    when(isPrivateVarName){
                        false -> String() to String()
                        else -> settingKey to varName
                    }
                }.filter {
                    it.first.isNotEmpty()
                            && it.second.isNotEmpty()
                }
            }

            private fun makeSettingKeyToPrivateVarNameListWithBlank(
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
                    } ?: String()
                    settingKey to varName
                }.filter {
                    it.first.isNotEmpty()
                }
            }

            private fun makeSettingKeyToPrivateRunVarNameList(
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
                ).filter {
                    it.first.isNotEmpty()
                }
                val isImportToErrType = when(judgeTargetStr.isEmpty()) {
                    true -> true to null
                    else -> SettingIfManager.handle(
                        judgeTargetStr,
                        argsPairList
                    )
                }
                val blankReturnValue = String() to Pair(String(), emptyList<Pair<String, String>>())
                val errType = isImportToErrType.second
                if(errType != null){
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_IF,
                        errType.errMessage,
                        keyToSubKeyConWhere
                    )
                    return blankReturnValue
                }
                val isImport = isImportToErrType.first
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
                val isErr = withContext(Dispatchers.IO) {
                    val isGlobalVarNameErrWithRunPrefixJob = async {
                        ImportErrManager.isGlobalVarNameExistErrWithRunPrefix(
                            context,
                            settingKeyToVarNameList,
                            renewalVarName,
                            keyToSubKeyConWhere
                        )
                    }
                    val isGlobalVarNameMultipleExistErrWithoutRunPrefixJob = async {
                        ImportErrManager.isGlobalVarNameMultipleErrWithoutRunPrefix(
                            context,
                            settingKeyToVarNameList,
                            renewalVarName,
                            keyToSubKeyConWhere
                        )
                    }
                    val isGlobalVarNameNotLastErrWithoutRunPrefixJob = async {
                        ImportErrManager.isGlobalVarNameNotLastErrWithoutRunPrefix(
                            context,
                            settingKeyToVarNameList,
                            renewalVarName,
                            keyToSubKeyConWhere
                        )
                    }
                    isGlobalVarNameErrWithRunPrefixJob.await()
                            || isGlobalVarNameMultipleExistErrWithoutRunPrefixJob.await()
                            || isGlobalVarNameNotLastErrWithoutRunPrefixJob.await()
                }
                if(isErr) blankReturnValue
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

            suspend fun exec(
                fragment: Fragment,
                settingVarKey: String,
                subKeyCon: String,
                busyboxExecutor: BusyboxExecutor?,
                editConstraintListAdapterArg: EditConstraintListAdapter?,
                renewalVarName: String?,
                keyToSubKeyConWhere: String,
            ): Pair<String, String>? {
                val context = fragment.context
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
                    val privateSubKeyClass = SettingActionKeyManager.SettingSubKey.entries.firstOrNull {
                        it.key == mainSubKey
                    } ?: return@forEach
                    when(privateSubKeyClass) {
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
                        SettingActionKeyManager.SettingSubKey.ON_RETURN -> {
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
                            ).filter {
                                it.first.isNotEmpty()
                            }
                            val resultStrToCheckErr = SettingFuncManager.handle(
                                fragment,
                                funcTypeDotMethod,
                                argsPairList,
                                busyboxExecutor,
                                editConstraintListAdapterArg,
                            )
                            val checkErr = resultStrToCheckErr.second
                            if(checkErr != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.SettingActionErrType.FUNC,
                                        checkErr.errMessage,
                                        keyToSubKeyConWhere,
                                    )
                                }
                                itPronounValue = String()
                                return@forEach
                            }
                            val resultStr = resultStrToCheckErr.first
                            VarErrManager.isGlobalVarFuncNullResultErr(
                                context,
                            renewalVarName ?: settingVarName,
                                resultStr,
                                keyToSubKeyConWhere,
                            ).let {
                                isGlobalVarFuncNullResultErr ->
                                if(
                                    isGlobalVarFuncNullResultErr
                                ) return null
                            }
                            itPronounValue = resultStr ?: String()
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
                            ).filter {
                                it.first.isNotEmpty()
                            }
                            val isImportToErrType = SettingIfManager.handle(
                                judgeTargetStr,
                                argsPairList
                            )
                            val errType = isImportToErrType.second
                            if(errType != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.SettingActionErrType.S_IF,
                                        errType.errMessage,
                                        keyToSubKeyConWhere
                                    )
                                }
                                return@forEach
                            }
                            val isImport = isImportToErrType.first
                            isImport?.let {
                                isNext = it
                            }
                        }
                    }
                    if(privateSubKeyClass != SettingActionKeyManager.SettingSubKey.S_IF){
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
                        SettingActionKeyManager.SettingSubKey.ON_RETURN -> {
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
