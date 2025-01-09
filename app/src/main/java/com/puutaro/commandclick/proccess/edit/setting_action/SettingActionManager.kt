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
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import com.puutaro.commandclick.util.str.BackslashTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
        private const val awaitWaitTimes = 5//10

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
//                ErrLogger.AlreadyErr.init()
                mutex.withLock {
                    beforeActionImportMap.clear()
                }
            }
        }

        private object ErrLogger {

            private val durationSec = 5

            enum class SettingActionErrType {
                S_RETURN,
                S_VAR,
                S_AC_VAR,
                FUNC,
                S_IF,
                AWAIT,
            }

//            object AlreadyErr {
//                private var isAlreadyErr = false
//                private val mutex = Mutex()
//
//                suspend fun init(){
//                    mutex.withLock {
//                        isAlreadyErr = false
//                    }
//                }
//
//                suspend fun enable() {
//                    mutex.withLock {
//                        isAlreadyErr = true
//                    }
//                }
//
//                suspend fun get(): Boolean {
//                    mutex.withLock {
//                        return isAlreadyErr
//                    }
//                }
//            }

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
//                if(AlreadyErr.get()) return
                withContext(Dispatchers.IO) {
                    LogDatetime.update(
                        currentDatetime
                    )
//                    AlreadyErr.enable()
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

        private fun filterSettingKeyToDefinitionListByValidVarDefinition(
            settingKeyToDefinitionList: List<Pair<String, String>>
        ): List<Pair<String, String>> {
            val settingReturnKey =
                SettingActionKeyManager.SettingActionsKey.SETTING_RETURN.key
            val varStrRegex = Regex("[a-zA-Z0-9_]+")
            return settingKeyToDefinitionList.filter {
                val settingKey = it.first
                if(
                    settingKey.isEmpty()
                ) return@filter false
                if(
                    settingKey == settingReturnKey
                ) return@filter true
                val definition = it.second
                varStrRegex.matches(definition)
            }
        }

        private fun makeSettingKeyToVarNameListForReturn(
            keyToSubKeyConList: List<Pair<String, String>>,
        ): List<Pair<String, String>> {
            val defaultReturnPair = String() to String()
            val subKeySeparator = SettingActionKeyManager.subKeySepartor
            val imageKeyList =
                SettingActionKeyManager.SettingActionsKey.entries.map {
                    it.key
                }
            return keyToSubKeyConList.map {
                    keyToSubKeyCon ->
                val settingKey = keyToSubKeyCon.first
                if(
                    !imageKeyList.contains(settingKey)
                ) return@map defaultReturnPair
                val varName = keyToSubKeyCon.second
                    .split(subKeySeparator)
                    .firstOrNull()?.let {
                        QuoteTool.trimBothEdgeQuote(it)
                    } ?: return@map defaultReturnPair
                settingKey to varName
            }
        }

        private suspend fun makeVarNameToValueStrMap(
            curMapLoopKey: String,
            importedVarNameToValueStrMap: Map<String, String?>?,
            loopKeyToVarNameValueStrMapClass: LoopKeyToVarNameValueStrMap,
            privateLoopKeyVarNameValueStrMapClass: PrivateLoopKeyVarNameValueStrMap,
            curImportedVarNameToValueStrMap: Map<String, String?>?,
            itToBitmapMap: Map<String, String?>?,
        ):  Map<String, String?> {
            val varNameToValueStrMap =
                (importedVarNameToValueStrMap ?: emptyMap()) +
                        (loopKeyToVarNameValueStrMapClass
                            .getAsyncVarNameToValueStr(curMapLoopKey)?.toMap()
                            ?: emptyMap()) +
                        (privateLoopKeyVarNameValueStrMapClass
                            .getAsyncVarNameToValueStr(curMapLoopKey)?.toMap()
                            ?: emptyMap()) +
                        (curImportedVarNameToValueStrMap ?: emptyMap()) +
                        (itToBitmapMap ?: emptyMap())
            return varNameToValueStrMap
        }
    }

    suspend fun exec(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        topLevelValueStrKeyList: List<String>?,
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
            topLevelValueStrKeyList
        )
        settingActionExecutor.makeResultLoopKeyToVarNameValueMap(
            settingActionAsyncCoroutine,
            editConstraintListAdapterArg,
            keyToSubKeyConList,
            SettingActionExecutor.mapRoopKeyUnit,
            null,
            keyToSubKeyConWhere,
            null,
            null,
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
//        val setReplaceVariableMap = makeSetRepValMap(
//            fragment,
//            fannelInfoMap,
//            setReplaceVariableMapSrc
//        )
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

    private class LoopKeyToAsyncDeferredVarNameValueStrMap {
        private val loopKeyToAsyncDeferredVarNameValueStrMap = mutableMapOf<
                String,
                MutableMap <
                        String,
                        Deferred<
                                Pair<
                                        Pair<String, String?>,
                                        SettingActionKeyManager.BreakSignal?
                                        >?
                                >
                    >
                >()
        private val asyncLoopKeyToVarNameValueStrMapMutex = Mutex()
        suspend fun getAsyncVarNameToValueStrAndExitSignal(loopKey: String):  MutableMap <
                String,
                Deferred<
                        Pair<
                                Pair<String, String?>,
                                SettingActionKeyManager.BreakSignal?
                                >?
                        >
                >? {
            return asyncLoopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.get(
                    loopKey
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            deferredVarNameValueStrMapAndBreakSignal: Deferred<
                    Pair<
                            Pair<String, String?>,
                            SettingActionKeyManager.BreakSignal?
                            >?
                    >
        ){
            asyncLoopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.get(
                    loopKey
                ).let { curPrivateMapLoopKeyVarNameValueMap ->
                    when (curPrivateMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                        false -> {
                            val isAlreadyExist =
                                curPrivateMapLoopKeyVarNameValueMap
                                    .get(varName) != null
                            if(isAlreadyExist) return@let
                            curPrivateMapLoopKeyVarNameValueMap.put(
                                varName,
                                deferredVarNameValueStrMapAndBreakSignal
                            )
                        }

                        else -> loopKeyToAsyncDeferredVarNameValueStrMap.put(
                            loopKey,
                            mutableMapOf(varName to deferredVarNameValueStrMapAndBreakSignal)
                        )
                    }
                }
            }
        }

        suspend fun clearVarName(
            loopKey: String,
            varName: String,
        ) {
            asyncLoopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.get(
                    loopKey
                )?.remove(varName)
            }
        }

        suspend fun clearAsyncVarNameToValueStrAndExitSignal(loopKey: String) {
            asyncLoopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToAsyncDeferredVarNameValueStrMap.remove(
                    loopKey
                )
            }
        }
    }

    private class PrivateLoopKeyVarNameValueStrMap {
        private val privateLoopKeyVarNameValueStrMap =
            mutableMapOf<String, MutableMap<String, String?>>()
        private val privateLoopKeyVarNameValueStrMapMutex = Mutex()
        suspend fun getAsyncVarNameToValueStr(
            loopKey: String
        ): MutableMap<String, String?>? {
            return privateLoopKeyVarNameValueStrMapMutex.withLock {
                privateLoopKeyVarNameValueStrMap.get(
                    loopKey
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            valueStr: String?,
        ){
            privateLoopKeyVarNameValueStrMapMutex.withLock {
                privateLoopKeyVarNameValueStrMap.get(
                    loopKey
                ).let { curPrivateVarNameValueMap ->
                    when (curPrivateVarNameValueMap.isNullOrEmpty()) {
                        false -> curPrivateVarNameValueMap.put(
                            varName,
                            valueStr
                        )

                        else -> privateLoopKeyVarNameValueStrMap.put(
                            loopKey,
                            mutableMapOf(varName to valueStr)
                        )
                    }
                }
            }
        }

        suspend fun initPrivateLoopKeyVarNameValueStrMapMutex(
            loopKey: String
        ) {
            return privateLoopKeyVarNameValueStrMapMutex.withLock {
                privateLoopKeyVarNameValueStrMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameValueStrMapMutex(loopKey: String){
            privateLoopKeyVarNameValueStrMapMutex.withLock {
                privateLoopKeyVarNameValueStrMap.remove(
                    loopKey
                )
            }
        }
    }

    private class LoopKeyToVarNameValueStrMap {
        private val loopKeyToVarNameValueStrMap =
            mutableMapOf<String, MutableMap<String, String?>>()
        private val loopKeyToVarNameValueStrMapMutex = Mutex()
        suspend fun getAsyncVarNameToValueStr(
            loopKey: String
        ): MutableMap<String, String?>? {
            return loopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToVarNameValueStrMap.get(
                    loopKey
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            valueStr: String?,
        ){
            loopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToVarNameValueStrMap.get(
                    loopKey
                ).let { curVarNameValueStrMap ->
                    when (curVarNameValueStrMap.isNullOrEmpty()) {
                        false -> curVarNameValueStrMap.put(
                            varName,
                            valueStr
                        )

                        else -> loopKeyToVarNameValueStrMap.put(
                            loopKey,
                            mutableMapOf(varName to valueStr)
                        )
                    }
                }
            }
        }

        suspend fun initPrivateLoopKeyVarNameValueStrMapMutex(
            loopKey: String
        ) {
            return loopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToVarNameValueStrMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameValueStrMapMutex(loopKey: String){
            loopKeyToVarNameValueStrMapMutex.withLock {
                loopKeyToVarNameValueStrMap.remove(
                    loopKey
                )
            }
        }
    }

    private class ExitSignalManager {
        private var exitSignal = false
        private val exitSignalMutex = Mutex()
        suspend fun setExit(){
            exitSignalMutex.withLock {
                exitSignal = true
            }
        }

        suspend fun get(): Boolean {
            return exitSignalMutex.withLock {
                exitSignal
            }
        }
    }


    private class SettingActionExecutor(
        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: Map<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
        private val topLevelValueStrKeyList: List<String>?,
    ) {

        private val loopKeyToVarNameValueStrMap = LoopKeyToVarNameValueStrMap()
//        mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val privateLoopKeyVarNameValueStrMap = PrivateLoopKeyVarNameValueStrMap()
        private val loopKeyToAsyncDeferredVarNameValueStrMap = LoopKeyToAsyncDeferredVarNameValueStrMap()
        private var exitSignalManager = ExitSignalManager()
        private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
        private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
        private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix

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

        suspend fun getResultLoopKeyToVarNameValueMap(): Map<String, String> {
            return loopKeyToVarNameValueStrMap
                .getAsyncVarNameToValueStr(mapRoopKeyUnit)
                ?.map {
                    it.key to (it.value ?: String())
                }?.toMap() ?: emptyMap()
        }


        suspend fun makeResultLoopKeyToVarNameValueMap(
            settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
            editConstraintListAdapterArg: EditConstraintListAdapter?,
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            originImportPath: String?,
            keyToSubKeyConWhere: String,
            topAcSVarName: String?,
            importedVarNameToValueStrMap: Map<String, String?>?,
            stringVarKeyList: List<String>?,
        ) {
            val fragment = fragmentRef.get()
                ?: return
            val context = fragment.context
                ?: return
            loopKeyToVarNameValueStrMap
                .initPrivateLoopKeyVarNameValueStrMapMutex(curMapLoopKey)
            privateLoopKeyVarNameValueStrMap
                .initPrivateLoopKeyVarNameValueStrMapMutex(curMapLoopKey)
            if(
                keyToSubKeyConList.isNullOrEmpty()
            ) return
            val isErr = withContext(Dispatchers.IO) {
                val settingKeyAndAsyncVarNameToAwaitVarNameList =
                    VarErrManager.makeSettingKeyAndAsyncVarNameToAwaitVarNameList(
                        keyToSubKeyConList
                    )
                val isAwaitNotAsyncVarErrJob = async {
                    VarErrManager.isAwaitNotAsyncVarErr(
                        context,
                        settingKeyAndAsyncVarNameToAwaitVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isAwaitDuplicateAsyncVarErrJob = async {
                    VarErrManager.isAwaitDuplicateAsyncVarErr(
                        context,
                        settingKeyAndAsyncVarNameToAwaitVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isAwaitNotDefiniteVarErrJob = async {
                    VarErrManager.isAwaitNotDefiniteAsyncVarErr(
                        context,
                        settingKeyAndAsyncVarNameToAwaitVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotAwaitAsyncVarErrOrAwaitInAsyncVarErrJob = async {
                    VarErrManager.isNotAwaitAsyncVarErrOrAwaitInAsyncVarErr(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToPrivateVarNameList =
                    VarErrManager.makeSettingKeyToPrivateVarNameList(
                        keyToSubKeyConList
                    )
                val isBlankSVarOrSAcVarErrJob = async {
                    VarErrManager.isBlankSVarOrSAcVarErr(
                        context,
                        settingKeyToPrivateVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotUseVarErrJob = async {
                    VarErrManager.isNotUseVarErr(
                        context,
                        settingKeyToPrivateVarNameList,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToNoRunVarNameList = VarErrManager.makeSettingKeyToNoRunVarNameList(
                    keyToSubKeyConList
                )
                val isShadowTopLevelVarErrJob = async {
                    VarErrManager.isShadowTopLevelVarErr(
                        context,
                        settingKeyToNoRunVarNameList,
                        topLevelValueStrKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotDefinitionVarErr = async {
                    VarErrManager.isNotDefinitionVarErr(
                        context,
                        settingKeyToNoRunVarNameList,
                        keyToSubKeyConList,
                        stringVarKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isRunPrefixVarUseErrJob = async {
                    VarErrManager.isRunPrefixVarUseErr(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToVarNameListForReturn =
                    makeSettingKeyToVarNameListForReturn(keyToSubKeyConList)
                val settingKeyToVarNameListWithIrregular = settingKeyToVarNameListForReturn.filter {
                    it.second.isNotEmpty()
                }
                val isIrregularVarNameErrJob = async {
                    VarErrManager.isIrregularVarNameErr(
                        context,
                        settingKeyToVarNameListWithIrregular,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToVarNameList = filterSettingKeyToDefinitionListByValidVarDefinition(
                        settingKeyToVarNameListWithIrregular
                    )
                val isSameVarNameErrJob = async {
                    VarErrManager.isSameVarNameErr(
                        context,
                        settingKeyToVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotBeforeDefinitionInReturnErrJob = async {
                    ReturnErrManager.isNotBeforeDefinitionInReturnErr(
                        context,
                        settingKeyToVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
//                val isAsyncVarOrRunPrefixVarSpecifyErrJob = async {
//                    ReturnErrManager.isAsyncVarOrRunPrefixVarSpecifyErr(
//                        context,
//                        settingKeyToVarNameList,
//                        keyToSubKeyConWhere,
//                    )
//                }
                val isNotExistReturnOrFuncJob = async {
                    VarErrManager.isNotExistReturnOrFuncOrValue(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val returnKeyToVarNameList = ReturnErrManager.makeReturnKeyToVarNameList(
                    keyToSubKeyConList
                )
                val isBlankReturnErrWithoutRunPrefixJob =
                    async {
                        ReturnErrManager.isBlankReturnErrWithoutRunPrefix(
                            context,
                            returnKeyToVarNameList,
                            keyToSubKeyConList,
                            keyToSubKeyConWhere,
                            topAcSVarName,
                        )
                    }
//                val isRunOrAsyncDifinitionErrInReturnJob = async {
//                    ReturnErrManager.isRunOrAsyncDefinitionErrInReturn(
//                        context,
//                        returnKeyToVarNameList,
//                        keyToSubKeyConList,
//                        keyToSubKeyConWhere,
//                        topAcSVarName,
//                        )
//                }
                isAwaitNotAsyncVarErrJob.await()
                        || isAwaitDuplicateAsyncVarErrJob.await()
                        || isAwaitNotDefiniteVarErrJob.await()
                        || isNotAwaitAsyncVarErrOrAwaitInAsyncVarErrJob.await()
                        || isBlankSVarOrSAcVarErrJob.await()
                        || isNotUseVarErrJob.await()
                        || isShadowTopLevelVarErrJob.await()
                        || isNotDefinitionVarErr.await()
                        || isRunPrefixVarUseErrJob.await()
                        || isIrregularVarNameErrJob.await()
                        || isSameVarNameErrJob.await()
//                        || isAsyncVarOrRunPrefixVarSpecifyErrJob.await()
                        || isNotBeforeDefinitionInReturnErrJob.await()
                        || isNotExistReturnOrFuncJob.await()
                        || isBlankReturnErrWithoutRunPrefixJob.await()
//                        || isRunOrAsyncDifinitionErrInReturnJob.await()
            }
            if(isErr) return
            keyToSubKeyConList.forEach { keyToSubKeyConSrc ->
                if (
                    exitSignalManager.get()
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
                val keyToSubKeyCon = keyToSubKeyConSrc
//                let {
//                    val repMap =
//                        (loopKeyToVarNameValueMap.get(curMapLoopKey) ?: emptyMap()) +
//                                (privateLoopKeyVarNameValueMap.get(curMapLoopKey) ?: emptyMap())
//                    val firstCon = CmdClickMap.replace(
//                        keyToSubKeyConSrc.first,
//                        repMap
//                    )
//                    val secondCon = CmdClickMap.replace(
//                        keyToSubKeyConSrc.second,
//                        repMap
//                    )
//                    firstCon to secondCon
//                }
                val curImageActionKeyStr = keyToSubKeyCon.first
                val curSettingActionKey =
                    SettingActionKeyManager.SettingActionsKey.entries.firstOrNull {
                        it.key == curImageActionKeyStr
                    } ?: return@forEach
                val subKeyCon = keyToSubKeyCon.second
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "isubkeycon.txt").absolutePath,
//                    listOf(
//                        "subKeyCon: ${subKeyCon}"
//                    ).joinToString("\n") + "\n======\n\n"
//                )
//                    QuoteTool.splitBySurroundedIgnore(
//                        keyToSubKeyCon.second,
//                        ImageActionKeyManager.landSeparator,
//                    ).firstOrNull()
//                        ?: return@forEach
//                VarErrManager.isNotReplaceVarErr(
//                    context,
//                    subKeyCon,
//                    keyToSubKeyConWhere,
//                ).let {
//                        isNotReplaceVarErr ->
//                    if(isNotReplaceVarErr) return
//                }
                when (curSettingActionKey) {
                    SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR -> {
                        val importPathAndRenewalVarNameToImportConToVarNameValueStrMapToImportRepMap =
                            SettingImport.makeImportPathAndRenewalVarNameToImportCon(
                                context,
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                curMapLoopKey,
                                loopKeyToAsyncDeferredVarNameValueStrMap,
                                privateLoopKeyVarNameValueStrMap,
                                loopKeyToVarNameValueStrMap,
                                importedVarNameToValueStrMap,
                                exitSignalManager,
                                keyToSubKeyCon,
                                originImportPath,
                                keyToSubKeyConWhere
                            )
                        val importPath =
                            importPathAndRenewalVarNameToImportConToVarNameValueStrMapToImportRepMap.first
                        if (
                            importPath.isEmpty()
                        ) return@forEach
                        val renewalVarNameToImportConToVarNameToValueStrMap =
                            importPathAndRenewalVarNameToImportConToVarNameValueStrMapToImportRepMap.second
                        val acIVarName =
                            renewalVarNameToImportConToVarNameToValueStrMap.first
                        if (
                            acIVarName.isEmpty()
                        ) return@forEach
                        val importedKeyToSubKeyConList =
                            renewalVarNameToImportConToVarNameToValueStrMap.second
                        if (
                            importedKeyToSubKeyConList.isEmpty()
                        ) return@forEach
                        val curImportedVarNameToValueStrMap =
                            renewalVarNameToImportConToVarNameToValueStrMap.third
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "sImport.txt").absolutePath,
//                            listOf(
//                                "addLoopKey(curMapLoopKey): ${addLoopKey(curMapLoopKey)}",
//                                "renewalVarNameSrc: ${renewalVarNameSrc}",
//                                "importPath: ${importPath}",
//                                "importedKeyToSubKeyConList: ${importedKeyToSubKeyConList}",
//                            ).joinToString("\n") + "\n\n=============\n\n"
//                        )
                        val isAsync =
                            acIVarName.startsWith(asyncPrefix)
                                    || acIVarName.startsWith(runAsyncPrefix)
                        if(isAsync){
                            val asyncJob = CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.IO) {
                                    val deferred = async {
                                        val varNameToBitmapMap =
                                            makeVarNameToValueStrMap(
                                                curMapLoopKey,
                                                importedVarNameToValueStrMap,
                                                loopKeyToVarNameValueStrMap,
                                                privateLoopKeyVarNameValueStrMap,
                                                curImportedVarNameToValueStrMap,
                                                null,
                                            )
                                        val curStringVarKeyList = varNameToBitmapMap.map {
                                            it.key
                                        }
                                        makeResultLoopKeyToVarNameValueMap(
                                            settingActionAsyncCoroutine,
                                            editConstraintListAdapterArg,
                                            importedKeyToSubKeyConList,
                                            addLoopKey(curMapLoopKey),
                                            importPath,
                                            "${importPath} by imported",
                                            acIVarName,
                                            varNameToBitmapMap,
                                            curStringVarKeyList
                                        )
                                        null
                                    }
                                    loopKeyToAsyncDeferredVarNameValueStrMap.put(
                                        curMapLoopKey,
                                        acIVarName,
                                        deferred
                                    )
                                }
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                settingActionAsyncCoroutine.put(asyncJob)
                            }
                            return@forEach
                        }
                        val varNameToBitmapMap =
                            makeVarNameToValueStrMap(
                                curMapLoopKey,
                                importedVarNameToValueStrMap,
                                loopKeyToVarNameValueStrMap,
                                privateLoopKeyVarNameValueStrMap,
                                curImportedVarNameToValueStrMap,
                                null,
                            )
                        val curStringVarKeyList = varNameToBitmapMap.map {
                            it.key
                        }
                        makeResultLoopKeyToVarNameValueMap(
                            settingActionAsyncCoroutine,
                            editConstraintListAdapterArg,
                            importedKeyToSubKeyConList,
                            addLoopKey(curMapLoopKey),
                            importPath,
                            "${importPath} by imported",
                            acIVarName,
                            varNameToBitmapMap,
                            curStringVarKeyList
                        )
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultSDebugAppDirPath,
//                                "image_acImport00_${acIVarName}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "acIVarName:${acIVarName}",
//                                "topAcIVarName:${topAcIVarName}",
//                                "curMapLoopKey: ${curMapLoopKey}",
//                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap?.getAsyncVarNameToValueStrAndExitSignal(
//                                    curMapLoopKey
//                                )}",
//                                "varNameToBitmapMap: ${varNameToBitmapMap}",
//                            ).joinToString("\n")  + "\n\n========\n\n"
//                        )
                        if (
                            topAcSVarName.isNullOrEmpty()
                        ) return@forEach
                        if(
                            topAcSVarName.startsWith(escapeRunPrefix)
                        ) return@forEach
                        val proposalRenewalVarNameSrcInnerMapValueStr =
                            privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
                                curMapLoopKey,
//                                addLoopKey(curMapLoopKey)
                            )?.get(
                                acIVarName
                            )
                        if(
                            !globalVarNameRegex.matches(acIVarName)
                        ) return@forEach
                        val removedLoopKey = removeLoopKey(curMapLoopKey)
                        if (
                            proposalRenewalVarNameSrcInnerMapValueStr != null
                        ) {
                            privateLoopKeyVarNameValueStrMap.put(
                                removedLoopKey,
                                topAcSVarName,
                                proposalRenewalVarNameSrcInnerMapValueStr
                            )
                        }
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultSDebugAppDirPath,
//                                "image_acImport01_${acIVarName}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "proposalRenewalVarNameSrcInnerMapValueStr: ${proposalRenewalVarNameSrcInnerMapValueStr}",
//                                "acIVarName:${acIVarName}",
//                                "topAcIVarName:${topAcIVarName}",
//                                "curMapLoopKey: ${curMapLoopKey}",
//                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "addLoopKey(curMapLoopKey): ${addLoopKey(curMapLoopKey)}",
//                                "addLoopKey(curMapLoopKey) privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    addLoopKey(curMapLoopKey)
//                                )}",
//                                "addLoopKey(curMapLoopKey) loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    addLoopKey(curMapLoopKey)
//                                )}",
//                                "removedLoopKey:${removedLoopKey}",
//                                "removed loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    removedLoopKey
//                                )}",
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                            ).joinToString("\n")  + "\n\n========\n\n"
//                        )
                        if(
                            !globalVarNameRegex.matches(topAcSVarName)
                        ) return@forEach
                        val proposalRenewalVarNameSrcMapValueStr =
                            loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
                            curMapLoopKey,
//                            addLoopKey(curMapLoopKey)
                        )?.get(acIVarName)
                        if (
                            proposalRenewalVarNameSrcMapValueStr != null
                        ) {
                            loopKeyToVarNameValueStrMap.put(
                                removedLoopKey,
                                topAcSVarName,
                                proposalRenewalVarNameSrcMapValueStr
                            )
                        }
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultSDebugAppDirPath,
//                                "image_acImport_${acIVarName}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "acIVarName:${acIVarName}",
//                                "topAcIVarName:${topAcIVarName}",
//                                "curMapLoopKey: ${curMapLoopKey}",
//                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "removedLoopKey:${removedLoopKey}",
//                                "removed loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    removedLoopKey
//                                )}",
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                            ).joinToString("\n")  + "\n\n========\n\n"
//                        )
                    }

                    SettingActionKeyManager.SettingActionsKey.SETTING_VAR -> {
                        val settingVarKey = curSettingActionKey.key
                        val mainSubKeyPairList = makeMainSubKeyPairList(
                            settingVarKey,
                            subKeyCon,
                        )
                        val settingVarName = PairListTool.getValue(
                            mainSubKeyPairList,
                            settingVarKey
                        )?.get(settingVarKey)
                            ?: return@forEach
                        val isAsync =
                            settingVarName.startsWith(asyncPrefix)
                                    || settingVarName.startsWith(runAsyncPrefix)
                        if(isAsync){
                            val asyncJob = CoroutineScope(Dispatchers.IO).launch {
                                val deferred = async {
                                    SettingVarExecutor().exec(
                                        fragment,
                                        mainSubKeyPairList,
                                        busyboxExecutor,
                                        editConstraintListAdapterArg,
                                        curMapLoopKey,
                                        loopKeyToAsyncDeferredVarNameValueStrMap,
                                        privateLoopKeyVarNameValueStrMap,
                                        loopKeyToVarNameValueStrMap,
                                        importedVarNameToValueStrMap,
                                        exitSignalManager,
                                        settingVarName,
                                        topAcSVarName,
                                        keyToSubKeyConWhere,
                                    )
                                }
//                                val removedLoopKey = removeLoopKey(curMapLoopKey)
                                loopKeyToAsyncDeferredVarNameValueStrMap.put(
                                    curMapLoopKey,
                                    settingVarName,
                                    deferred
                                )
                                val removedLoopKey = removeLoopKey(curMapLoopKey)
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultSDebugAppDirPath,
//                                        "image_async_00_${settingVarName}.txt"
//                                    ).absolutePath,
//                                    listOf(
//                                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                                        "imageVarKey: ${imageVarKey}",
//                                        "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                        "settingVarName: $settingVarName",
//                                        "isAsync: ${isAsync}",
//                                        "curMapLoopKey: ${curMapLoopKey}",
//                                        "removedLoopKey: ${removedLoopKey}",
//                                        "loopKeyToAsyncDeferredVarNameBitmapMap: ${
//                                            loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                                curMapLoopKey
//                                            )}",
//                                        "privateLoopKeyVarNameBitmapMap: ${
//                                            privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                                curMapLoopKey
//                                            )}",
//                                        "topAcIVarName: ${topAcIVarName}",
//                                    ).joinToString("\n")
//                                )
                                if (
                                    removedLoopKey == curMapLoopKey
                                ) return@launch
                                val isGlobalForRawVar =
                                    globalVarNameRegex.matches(settingVarName)
                                if (
                                    !isGlobalForRawVar
                                ) return@launch
                                if (
                                    topAcSVarName.isNullOrEmpty()
                                ) return@launch
                                if (
                                    topAcSVarName.startsWith(escapeRunPrefix)
                                ) return@launch
                                loopKeyToAsyncDeferredVarNameValueStrMap.put(
                                    removedLoopKey,
                                    topAcSVarName,
                                    deferred
                                )
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultSDebugAppDirPath,
//                                        "image_async_${settingVarName}.txt"
//                                    ).absolutePath,
//                                    listOf(
//                                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                                        "imageVarKey: ${imageVarKey}",
//                                        "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                        "settingVarName: $settingVarName",
//                                        "isAsync: ${isAsync}",
//                                        "curMapLoopKey: ${curMapLoopKey}",
//                                        "removedLoopKey: ${removedLoopKey}",
//                                        "loopKeyToAsyncDeferredVarNameBitmapMap: ${
//                                            loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                                curMapLoopKey
//                                            )}",
//                                        "privateLoopKeyVarNameBitmapMap: ${
//                                            privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                                curMapLoopKey
//                                            )}",
//                                        "loopKeyToVarNameBitmapMap: ${
//                                            loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                                curMapLoopKey
//                                            )}",
//                                        "loopKeyToVarNameBitmapMap: ${
//                                            loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                                removedLoopKey
//                                            )}"
//                                    ).joinToString("\n")
//                                )
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                settingActionAsyncCoroutine.put(asyncJob)
                            }
                            return@forEach
                        }
                        SettingVarExecutor().exec(
                            fragment,
                            mainSubKeyPairList,
                            busyboxExecutor,
                            editConstraintListAdapterArg,
                            curMapLoopKey,
                            loopKeyToAsyncDeferredVarNameValueStrMap,
                            privateLoopKeyVarNameValueStrMap,
                            loopKeyToVarNameValueStrMap,
                            importedVarNameToValueStrMap,
                            exitSignalManager,
                            settingVarName,
                            topAcSVarName,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToStrValueAndExitSignal ->
                            val exitSignalClass = varNameToStrValueAndExitSignal.second
                            if (
                                exitSignalClass == SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) {
                                exitSignalManager.setExit()
                                return
                            }
                            val varNameToValueStr = varNameToStrValueAndExitSignal.first
                            privateLoopKeyVarNameValueStrMap.put(
                                curMapLoopKey,
                                varNameToValueStr.first,
                                varNameToValueStr.second
                            )
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultSDebugAppDirPath,
//                                    "image_${settingVarName}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
//                                    "imageVarKey: ${imageVarKey}",
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "settingVarName: $settingVarName",
//                                    "isAsync: ${isAsync}",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "removedLoopKey: ${removedLoopKey}",
//                                    "varName: ${varNameToValueStr.first}",
//                                    "topAcIVarName: ${topAcIVarName}",
//                                    "varNameForPut: ${varNameForPut}",
//                                    "curMapLoopKey privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )}",
//                                    "removedLoopKey privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            removedLoopKey
//                                        )}",
//                                    "curMapLoopKey loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )}",
//                                    "removedLoopKey loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            removedLoopKey
//                                        )}",
//                                ).joinToString("\n")
//                            )
                        }
                    }
                    SettingActionKeyManager.SettingActionsKey.SETTING_RETURN -> {
                        val settingReturnKey = curSettingActionKey.key
                        val mainSubKeyPairList = makeMainSubKeyPairList(
                            settingReturnKey,
                            subKeyCon,
                        )
                        val valueStrBeforeReplace = PairListTool.getValue(
                            mainSubKeyPairList,
                            settingReturnKey
                        )?.get(settingReturnKey)
                            ?: String()
                        SettingReturnExecutor().exec(
                            fragment,
                            mainSubKeyPairList,
                            curMapLoopKey,
                            privateLoopKeyVarNameValueStrMap,
                            loopKeyToVarNameValueStrMap,
                            importedVarNameToValueStrMap,
                            exitSignalManager,
                            valueStrBeforeReplace,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToStrValueAndExitSignal ->
                            val breakSignalClass = varNameToStrValueAndExitSignal.second
                            val varNameToValueStr = varNameToStrValueAndExitSignal.first
                            val returnSignal = varNameToValueStr?.first
//                            privateLoopKeyVarNameValueStrMap.put(
//                                curMapLoopKey,
//                                varNameToValueStr.first,
//                                varNameToValueStr.second
//                            )

//                            val isGlobalForRawVar =
//                                globalVarNameRegex.matches(varNameToValueStr.first)
                            val isRegisterToTop =
                                returnSignal == SettingActionKeyManager.SettingReturnManager.OutputReturn.OUTPUT_RETURN
                                        && !topAcSVarName.isNullOrEmpty()
                                        && varNameToValueStr != null
                                        && !topAcSVarName.startsWith(escapeRunPrefix)
//                            val isRegisterToTopForPrivate =
//                                isGlobalForRawVar && isNotRunPrefix
                            ReturnErrManager.isReturnVarStrNullResultErr(
                                context,
                                valueStrBeforeReplace,
                                topAcSVarName,
                                varNameToValueStr?.second,
                                SettingActionKeyManager.SettingSubKey.SETTING_RETURN,
                                keyToSubKeyConWhere,
                            ).let isReturnVarStrNullResultErr@ {
                                    isReturnVarStrNullResultErr ->
                                if(
                                    !isReturnVarStrNullResultErr
                                ) return@isReturnVarStrNullResultErr
                                return
                            }
                            val removedLoopKey =
                                removeLoopKey(curMapLoopKey)
                            if (
                                isRegisterToTop
                                && !topAcSVarName.isNullOrEmpty()
                                && varNameToValueStr != null
                            ) {
                                privateLoopKeyVarNameValueStrMap.put(
                                    removedLoopKey,
                                    topAcSVarName,
                                    varNameToValueStr.second
                                )
                            }
//                            val varNameForPut = topAcIVarName
//                                ?: varNameToValueStr.first
//                            val isGlobalRegister =
//                                isRegisterToTop
//                            if (isGlobalRegister) {
//                                loopKeyToVarNameValueStrMap.put(
//                                    removedLoopKey,
//                                    varNameForPut,
//                                    varNameToValueStr.second
//                                )
//                            }
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultSDebugAppDirPath,
//                                    "lSReturn_${valueStrBeforeReplace}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "subKeyCon: ${subKeyCon}",
//                                    "keyToSubKeyCon.second: ${keyToSubKeyCon.second}",
//                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "exitSignalClass: ${breakSignalClass}",
//                                    "varNameToValueStr: ${varNameToValueStr}",
//                                    "settingVarName: $valueStrBeforeReplace",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "removedLoopKey: ${removedLoopKey}",
//                                    "varName: ${varNameToValueStr?.first}",
//                                    "curMapLoopKey privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )}",
//                                    "removedLoopKey privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            removedLoopKey
//                                        )}",
//                                    "curMapLoopKey loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )}",
//                                    "removedLoopKey loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            removedLoopKey
//                                        )}",
//                                    "makeVarNameToValueStrMap: ${makeVarNameToValueStrMap(
//                                        curMapLoopKey,
//                                        importedVarNameToValueStrMap,
//                                        loopKeyToVarNameValueStrMap,
//                                        privateLoopKeyVarNameValueStrMap,
//                                        null,
//                                        null,
////                        mapOf(
////                            itPronoun to itPronounValueStrToExitSignal?.first
////                        )
//                                    )}"
//                                ).joinToString("\n\n\n")
//                            )
                            when(breakSignalClass){
                                SettingActionKeyManager.BreakSignal.EXIT_SIGNAL -> {
                                    exitSignalManager.setExit()
                                    return
                                }
                                SettingActionKeyManager.BreakSignal.RETURN_SIGNAL -> {
                                    return
                                }
                                else -> {}
                            }
                        }

                    }
                }
            }
        }

        private fun makeMainSubKeyPairList(
            settingVarKey: String,
            subKeyCon: String,
        ): List<
                Pair<
                        String,
                        Map<String, String>
                        >
                > {
            val subKeySeparator = SettingActionKeyManager.subKeySepartor
            val landSeparator = SettingActionKeyManager.landSeparator
            val subKeyToConList = CmdClickMap.createMap(
                "${settingVarKey}=${
                    subKeyCon.replace(
                        Regex("[${landSeparator}]+$"),
                        String(),
                    )
                }",
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
                    SettingActionKeyManager.SettingSubKey.SETTING_RETURN,
                    SettingActionKeyManager.SettingSubKey.VALUE,
                    SettingActionKeyManager.SettingSubKey.AWAIT,
                    SettingActionKeyManager.SettingSubKey.ON_RETURN -> {
                        val mainSubKeyMap = mapOf(
                            innerSubKeyName to innerSubKeyCon,
                        )
                        Pair(innerSubKeyName, mainSubKeyMap)
                    }
                    SettingActionKeyManager.SettingSubKey.FUNC,
                    SettingActionKeyManager.SettingSubKey.S_IF -> {
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

        private object ImportErrManager {

            private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
            private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
            private val sAcVarKeyName =
                SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
            private val settingReturnKey =
                SettingActionKeyManager.SettingActionsKey.SETTING_RETURN.key

            fun isCircleImportErr(
                context: Context?,
                originImportPath: String?,
                importPath: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    originImportPath.isNullOrEmpty()
                ) {
                    return false
                }
                if(
                    importPath != originImportPath
                ) return false
                val spanSAcVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAcVarKeyName
                    )
                val spanOriginImportPath =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        originImportPath
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
                        "must not circle in ${spanSAcVarKeyName}: importPath: ${spanImportPath}, originImportPath: ${spanOriginImportPath}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }

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
                        sAcVarKeyName
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

            fun isImportShadowVarMarkErr(
                context: Context?,
                importPath: String,
                importSrcConBeforeReplace: String,
                importRepMap: Map<String, String>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val importVarDollMarkList = importRepMap.filter {
                    SettingActionKeyManager.ValueStrVar.matchStringVarName(it.value)
                }.map {
                    it.value
                }
                val importShadowVarDollMark = importVarDollMarkList.firstOrNull {
                    importSrcConBeforeReplace.contains(it)
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lisImportShadowVarMarkErr.txt").absolutePath,
//                    listOf(
//                        "importVarDollMarkList: ${importVarDollMarkList}",
//                        "importSrcConBeforeReplace: ${importSrcConBeforeReplace}",
//                    ).joinToString("\n\n") + "\n\n====\n\n"
//                )
                if(
                    importShadowVarDollMark.isNullOrEmpty()
                ) return false
                val spanSAcVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAcVarKeyName
                    )
                val spanImportShadowVar =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        importShadowVarDollMark
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
                        "must not shadow variable in ${spanSAcVarKeyName}: importPath: ${spanImportPath}, shadowVar: ${spanImportShadowVar}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }

//            fun isGlobalVarNameExistErrWithRunPrefix(
//                context: Context?,
//                settingKeyToVarNameList: List<Pair<String, String>>,
//                renewalVarName: String,
//                keyToSubKeyConWhere: String
//            ): Boolean {
//                if(
//                    !renewalVarName.startsWith(escapeRunPrefix)
//                ) return false
//                val settingKeyToGlobalVarNameList = settingKeyToVarNameList.filter {
//                        settingKeyToVarName ->
//                    settingKeyToVarName.second.matches(globalVarNameRegex)
//                }
//                val isGlobalVarNameExistErr = settingKeyToGlobalVarNameList.isNotEmpty()
////                FileSystems.updateFile(
////                    File(UsePath.cmdclickDefaultAppDirPath, "sisGlobalVarNameExistErrWithRunPrefix.txt").absolutePath,
////                    listOf(
////                        "renewalVarName: ${renewalVarName}",
////                        "settingKeyToGlobalVarNameList: ${settingKeyToGlobalVarNameList}"
////                    ).joinToString("\n")
////                )
//                if(
//                    !isGlobalVarNameExistErr
//                ) return false
//                val spanIAcVarKeyName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.ligthBlue,
//                        sAcVarKeyName
//                    )
//                val spanGlobalVarNameListCon =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        settingKeyToGlobalVarNameList.map {
//                            it.second
//                        }.joinToString(". ")
//                    )
//                runBlocking {
//                    ErrLogger.sendErrLog(
//                        context,
//                        ErrLogger.SettingActionErrType.S_AC_VAR,
//                        "With ${escapeRunPrefix} prefix in ${spanIAcVarKeyName}, " +
//                                "global (uppercase) var name must not exist: ${spanGlobalVarNameListCon}",
//                        keyToSubKeyConWhere
//                    )
//                }
//                return true
//            }

//            fun isGlobalVarNameMultipleErrWithoutRunPrefix(
//                context: Context?,
//                settingKeyToVarNameList: List<Pair<String, String>>,
//                topIAcVarName: String,
//                keyToSubKeyConWhere: String
//            ): Boolean {
//                if(
//                    topIAcVarName.startsWith(escapeRunPrefix)
//                ) return false
//                val settingKeyToGlobalVarNameList = settingKeyToVarNameList.filter {
//                        settingKeyToVarName ->
//                    settingKeyToVarName.second.matches(globalVarNameRegex)
//                }
//                val isMultipleGlobalVarNameErr = settingKeyToGlobalVarNameList.size > 1
//                if(
//                    !isMultipleGlobalVarNameErr
//                ) return false
//                val spanSAdVarKeyName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.ligthBlue,
//                        sAcVarKeyName
//                    )
//                val spanGlobalVarNameListCon =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        settingKeyToGlobalVarNameList.map {
//                            it.second
//                        }.joinToString(", ")
//                    )
//                runBlocking {
//                    ErrLogger.sendErrLog(
//                        context,
//                        ErrLogger.SettingActionErrType.S_AC_VAR,
//                        "In ${spanSAdVarKeyName}, global (uppercase) var name must be one: ${spanGlobalVarNameListCon}",
//                        keyToSubKeyConWhere
//                    )
//                }
//                return true
//
//            }

            fun isGlobalVarNameNotLastErrWithoutRunPrefix(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                keyToSubKeyConList: List<Pair<String, String>>,
                renewalVarName: String,
                keyToSubKeyConWhere: String
            ): Boolean {
                if(
                    renewalVarName.startsWith(escapeRunPrefix)
                ) return false
                val sIfKey =
                    SettingActionKeyManager.SettingSubKey.S_IF.key
                val sIfKeyRegex = Regex(
                    "[?]${sIfKey}="
                )
                val lastSubKeyCon = keyToSubKeyConList.lastOrNull {
                    it.first.isNotEmpty()
                }?.second
                    ?: return false
                val lastSettingKeyToSubKeyCon = settingKeyToVarNameList.lastOrNull()
                    ?: return false
                val isSettingReturnKey =
                    lastSettingKeyToSubKeyCon.first == settingReturnKey
                val varName = lastSettingKeyToSubKeyCon.second
                val isIrregularVarName = varName.startsWith(
                    escapeRunPrefix
                ) || varName.startsWith(
                    runAsyncPrefix
                ) || varName.startsWith(
                    asyncPrefix
                )
                val containIIfKey = sIfKeyRegex.containsMatchIn(lastSubKeyCon)
                if (
                    isSettingReturnKey
                    && !isIrregularVarName
                    && !containIIfKey
                ) return false
                val spanEscapeRunPrefix =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        escapeRunPrefix
                    )
                val spanRunAsyncPrefix =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        runAsyncPrefix
                    )
                val spanAsyncPrefix =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        asyncPrefix
                    )
                val spanSAdVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAcVarKeyName
                    )
                val spanSettingReturnKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        settingReturnKey
                    )
                val spanGlobalVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        varName
                    )
                val spanSIfKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sIfKey
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
                        "Without ${spanEscapeRunPrefix} prefix in ${spanSAdVarKeyName}, " +
                                "imported last setting key must be ${spanSettingReturnKey} without ${spanSIfKey}: ${spanGlobalVarName}",
                        keyToSubKeyConWhere
                    )
                }
                return true
            }
        }

        private object ReturnErrManager {

            private val mainKeySeparator = SettingActionKeyManager.mainKeySeparator
            private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
            private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
            private val settingReturnKey =
                SettingActionKeyManager.SettingActionsKey.SETTING_RETURN.key

            fun isReturnVarStrNullResultErr(
                context: Context?,
                varName: String?,
                topAcVarName: String?,
                returnValueStr: String?,
                settingSubKey: SettingActionKeyManager.SettingSubKey,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    returnValueStr != null
                ) return false
               if(
                   topAcVarName.isNullOrEmpty()
                   || topAcVarName.startsWith(escapeRunPrefix)
               ){
                   return false
               }
                val spanVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        varName.toString()
                    )
                val spanSettingReturnKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        settingReturnKey
                    )
                val spanSubKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        settingSubKey.key
                    )
                val spanTopAcVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        topAcVarName
                    )
                val spanEscapeRunPrefix =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        escapeRunPrefix
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_RETURN,
                        "When topAcVar(${spanTopAcVarName}) don't have ${spanEscapeRunPrefix} prefix, ${spanSettingReturnKey} value must be exist: varName: ${spanVarName}, setting sub key: ${spanSubKeyName}",
                        keyToSubKeyConWhere,
                    )
                }
                return true

            }

            fun isBlankReturnErrWithoutRunPrefix(
                context: Context?,
                returnKeyToVarNameList: List<Pair<String, String>>,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
                topAcVarName: String?,
            ): Boolean {
                if(
                    topAcVarName.isNullOrEmpty()
                    || topAcVarName.startsWith(escapeRunPrefix)
                ) return false
               if(
                   returnKeyToVarNameList.isEmpty()
               ) return false
                val errReturnKeyToVarName = returnKeyToVarNameList.firstOrNull {
                    returnKeyToVarName ->
                    returnKeyToVarName.second.isEmpty()
                } ?: return false
                val spanBlankSettingKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        errReturnKeyToVarName.first
                    )
                val spanEscapeRunPrefix =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        escapeRunPrefix
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_VAR,
                        "${spanBlankSettingKeyName} must not blank in ${spanEscapeRunPrefix}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }

//            fun isAsyncVarOrRunPrefixVarSpecifyErr(
//                context: Context?,
//                settingKeyToVarNameList: List<Pair<String, String>>,
//                keyToSubKeyConWhere: String,
//            ): Boolean {
//                val runPrefixVarRegex =
//                    Regex("[$][{][${escapeRunPrefix}][a-zA-Z0-9_]*[}]")
//                val asyncVarRegex = Regex("[$][{][${asyncPrefix}][a-zA-Z0-9_]*[}]")
//                settingKeyToVarNameList.forEach {
//                        settingKeyToValueStr ->
//                    val settingKey = settingKeyToValueStr.first
//                    if(
//                        settingKey != settingReturnKey
//                    ) return@forEach
//                    val valueStr = settingKeyToValueStr.second
//                    if(
//                        !runPrefixVarRegex.matches(valueStr)
//                        && !asyncVarRegex.matches(valueStr)
//                    ) return@forEach
//                    val spanSettingKey =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errRedCode,
//                            settingKey
//                        )
//                    val spanValueStr =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errRedCode,
//                            valueStr
//                        )
//                    val spanEscapeRunPrefix =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.ligthBlue,
//                            escapeRunPrefix
//                        )
//                    val spanAsyncPrefix =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.ligthBlue,
//                            asyncPrefix
//                        )
//                    runBlocking {
//                        ErrLogger.sendErrLog(
//                            context,
//                            ErrLogger.SettingActionErrType.S_VAR,
//                            "${spanSettingKey} key must not use ${spanEscapeRunPrefix} and ${spanAsyncPrefix} prefix: ${spanValueStr}",
//                            keyToSubKeyConWhere,
//                        )
//                    }
//                    return true
//                }
//                return false
//            }

//            fun isRunOrAsyncDefinitionErrInReturn(
//                context: Context?,
//                returnKeyToVarNameList: List<Pair<String, String>>,
//                keyToSubKeyConList: List<Pair<String, String>>,
//                keyToSubKeyConWhere: String,
//                topAcVarName: String?,
//            ): Boolean {
//                if(
//                    topAcVarName.isNullOrEmpty()
//                    || topAcVarName.startsWith(escapeRunPrefix)
//                ) return false
//                if(
//                    returnKeyToVarNameList.isEmpty()
//                ) return false
//                val errKeyToVarName = returnKeyToVarNameList.firstOrNull {
//                        returnKeyToVarName ->
//                    val varName = returnKeyToVarName.second
//                    varName.startsWith(escapeRunPrefix)
//                            || varName.startsWith(asyncPrefix)
//                            || varName.startsWith(runAsyncPrefix)
//                } ?: return false
//                val spanBlankSettingKeyName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        errKeyToVarName.first
//                    )
//                val spanVarName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        errKeyToVarName.second
//                    )
//                val spanEscapeRunPrefix =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.ligthBlue,
//                        escapeRunPrefix
//                    )
//                val spanAsyncPrefix =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        asyncPrefix
//                    )
//                val spanRunAsyncPrefix =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        runAsyncPrefix
//                    )
//                runBlocking {
//                    ErrLogger.sendErrLog(
//                        context,
//                        ErrLogger.SettingActionErrType.S_VAR,
//                        "${spanBlankSettingKeyName} must not ${spanEscapeRunPrefix}, ${spanAsyncPrefix}, and ${spanRunAsyncPrefix}: ${spanVarName}",
//                        keyToSubKeyConWhere,
//                    )
//                }
//                return true
//            }

            fun isNotBeforeDefinitionInReturnErr(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val varMarkRegex = Regex("[$][{][a-zA-Z0-9_]+[}]")
                val regexStrTemplate = "([$][{]%s[}])"
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lisNotBeforeDefinitionInReturnErr.txt").absolutePath,
//                    listOf(
//                        "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                    ).joinToString("\n\n") + "\n\n==========\n\n"
//                )
                settingKeyToVarNameList.forEachIndexed { index, settingKeyToVarName ->
                    val settingKey = settingKeyToVarName.first
                    if(
                        settingKey != settingReturnKey
                    ) return@forEachIndexed
                    val alreadyVarNameList = settingKeyToVarNameList.filterIndexed { innerIndex, innerSettingKeyToVarName ->
                        innerSettingKeyToVarName.first != settingReturnKey
                                && innerIndex < index
                    }.map {
                        innerSettingKeyToVarName ->
                        innerSettingKeyToVarName.second
                    }
                    val returnVarName = settingKeyToVarName.second
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultSDebugAppDirPath, "lisNotBeforeDefinitionInReturnErr_for.txt").absolutePath,
//                        listOf(
//                            "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                            "returnVarName:${returnVarName}",
//                            "alreadyVarNameList:${alreadyVarNameList}",
//                        ).joinToString("\n\n") + "\n\n==========\n\n"
//                    )
                    if(
                        !varMarkRegex.containsMatchIn(returnVarName)
                    ) return@forEachIndexed
                    val alreadyVarNameRegex = alreadyVarNameList.map {
                        regexStrTemplate.format(it)
                    }.joinToString("|").toRegex()
                    if(
                        alreadyVarNameList.isNotEmpty()
                        && alreadyVarNameRegex.containsMatchIn(returnVarName)
                    ) return@forEachIndexed
                    val spanSettingReturnKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            settingReturnKey
                        )
                    val spanReturnVarName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            returnVarName
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.SettingActionErrType.S_VAR,
                            "Not before definition ${spanSettingReturnKey} var: ${spanReturnVarName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun makeReturnKeyToVarNameList(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, String>> {
                val defaultReturnPair = String() to String()
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        settingKey != settingReturnKey
                    ) return@map defaultReturnPair
                    val varName = keyToSubKeyCon.second
                        .split(subKeySeparator)
                        .firstOrNull()?.let {
                            QuoteTool.trimBothEdgeQuote(it)
                        } ?: return@map defaultReturnPair
                    settingKey to varName
                }.filter {
                    it.first.isNotEmpty()
                }
            }
        }

        private object VarErrManager {

            private val mainKeySeparator = SettingActionKeyManager.mainKeySeparator
            private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
            private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
            private val settingReturnKey =
                SettingActionKeyManager.SettingActionsKey.SETTING_RETURN.key

            private fun makeKeyToSubKeyListCon(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): String {
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val imageKey = keyToSubKeyCon.first
                    val subKeyCon = keyToSubKeyCon.second
                    "${mainKeySeparator}${imageKey}=${subKeyCon}"
                }.joinToString("\n")
            }

            fun isSameVarNameErr(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val varNameList = settingKeyToVarNameList.filter {
                    settingKeyToVarName ->
                    settingKeyToVarName.first !=
                            settingReturnKey
                }.map {
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

            fun isIrregularVarNameErr(
                context: Context?,
                settingKeyToVarNameListSrc: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val varMarkRegex = Regex("^[a-zA-Z0-9_]+$")
                val settingKeyToVarNameList = settingKeyToVarNameListSrc.filter {
                        settingKeyToVarName ->
                    settingKeyToVarName.first !=
                            settingReturnKey
                }
                settingKeyToVarNameList.forEach {
                        settingKeyToVarName ->
                    val varMarkEntry = settingKeyToVarName.second
                    if(
                        varMarkRegex.matches(varMarkEntry)
                    ) return@forEach
                    val settingKey = settingKeyToVarName.first
                    val spanSettingKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            settingKey
                        )
                    val spanVarMarkEntry =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            varMarkEntry
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.SettingActionErrType.S_VAR,
                            "${spanSettingKey} key value must be variable( [a-zA-Z0-9_]+ ): ${spanVarMarkEntry}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun isGlobalVarNullResultErr(
                context: Context?,
                varName: String,
                returnValueStr: String?,
                settingSubKey: SettingActionKeyManager.SettingSubKey,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    returnValueStr != null
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
                val spanSubKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        settingSubKey.key
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_VAR,
                        "In global (uppercase), ${spanSubKeyName} result must be exist: ${spanVarName}",
                        keyToSubKeyConWhere,
                    )
                }
                return true

            }

            fun isAwaitNotDefiniteAsyncVarErr(
                context: Context?,
                settingKeyAndAsyncVarNameToAwaitVarNameList: List<
                        Pair<
                                String,
                                Pair<
                                        String,
                                        List<String>?
                                        >
                                >
                        >,
                keyToSubKeyConWhere: String,
            ): Boolean {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyAndAsyncVarNameToAwaitVarNameList: ${settingKeyAndAsyncVarNameToAwaitVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
                settingKeyAndAsyncVarNameToAwaitVarNameList.forEachIndexed {
                        index, settingKeyToAsyncVarNames ->
                    val beforeSettingKeyAndAsyncVarNameToAwaitVarNameList =
                        settingKeyAndAsyncVarNameToAwaitVarNameList.filterIndexed { innerIndex, pair ->
                            innerIndex < index
                        }
                    val beforeAsyncVarNameCon = beforeSettingKeyAndAsyncVarNameToAwaitVarNameList.map {
                        val asyncVarName = it.second.first
                        asyncVarName
                    }.joinToString("\n")
                    val curSettingKeyToAwaitVarNameList =
                        settingKeyToAsyncVarNames.second
                    val settingKey = curSettingKeyToAwaitVarNameList.first
                    val awaitVarNameList =
                        curSettingKeyToAwaitVarNameList.second
                    awaitVarNameList?.forEach {
                            awaitVarName ->
                        if(
                            beforeAsyncVarNameCon.contains(awaitVarName)
                        ) return@forEach
                        val spanSettingKeyName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                settingKey
                            )
                        val spanAwaitKeyName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                awaitKey
                            )
                        val spanAwaitVarName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errRedCode,
                                awaitVarName
                            )
                        runBlocking {
                            ErrLogger.sendErrLog(
                                context,
                                ErrLogger.SettingActionErrType.S_VAR,
                                "Not before definite ${spanAwaitKeyName} var name: ${spanAwaitVarName}, var name: settingKeyName: ${spanSettingKeyName}",
                                keyToSubKeyConWhere,
                            )
                        }
                        return true
                    }
                }
                return false
            }

            fun isNotAwaitAsyncVarErrOrAwaitInAsyncVarErr(
                context: Context?,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {

                val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
                val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
                val subKeySeparator =
                    SettingActionKeyManager.subKeySepartor

                keyToSubKeyConList.forEach {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        settingKey == settingReturnKey
                    ) return@forEach
                    val asyncVarNameEntry =
                        keyToSubKeyCon.second
                            .split(subKeySeparator)
                            .firstOrNull()
                            ?: return@forEach
                    if(
                        !asyncVarNameEntry.startsWith(asyncPrefix)
                    ) return@forEach
                    val asyncVarMark = "${'$'}{${asyncVarNameEntry}}"
                    val indexToSubKeyConWithVarMark = let indexToSubKeyConWithVarMark@ {
                        keyToSubKeyConList.forEachIndexed findindexToSubKeyConWithVarMark@ { index, innerKeyToSubKeyCon ->
                            val subKeyCon = innerKeyToSubKeyCon.second
                            if (
                                !subKeyCon.contains(asyncVarMark)
                            ) return@findindexToSubKeyConWithVarMark
                            return@indexToSubKeyConWithVarMark index to innerKeyToSubKeyCon
                        }
                        null
                    } ?: return@forEach
                    val indexToSubKeyPairListWithAwait = let indexToSubKeyPairListWithAwait@ {
                        keyToSubKeyConList.forEachIndexed findIndexToSubKeyPairListWithAwait@ { index, innerKeyToSubKeyCon ->
                            val subKeyCon = innerKeyToSubKeyCon.second
                            val subKeyPairList = CmdClickMap.createMap(
                                subKeyCon,
                                subKeySeparator
                            )
                            subKeyPairList.forEach execIndexToSubKeyPairListWithAwait@ {
                                    subKeyToCon ->
                                if(
                                    subKeyToCon.first != awaitKey
                                ) return@execIndexToSubKeyPairListWithAwait
                                val awaitVarNameList =
                                    SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                                        subKeyToCon.second
                                    )
                                if(
                                    !awaitVarNameList.contains(asyncVarNameEntry)
                                ) return@execIndexToSubKeyPairListWithAwait
                                return@indexToSubKeyPairListWithAwait index to subKeyPairList
                            }
//                            val awaitVarNameList = subKeyPairList.firstOrNull {
//                                subKeyToCon ->
//                                subKeyToCon.first == awaitKey
//                            }?.second?.let {
//                                ImageActionKeyManager.AwaitManager.getAwaitVarNameList(it)
//                            } ?: return@findIndexToSubKeyPairListWithAwait
//                            if(
//                                !awaitVarNameList.contains(asyncVarNameEntry)
//                            ) return@findIndexToSubKeyPairListWithAwait
//                            return@indexToSubKeyPairListWithAwait index to subKeyPairList
                        }
                        null
                    }
                    if(indexToSubKeyPairListWithAwait == null){
                        val spanSettingKeyName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                settingKey
                            )
                        val spanAwaitKeyName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                awaitKey
                            )
                        val spanAwaitAsyncVarName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errRedCode,
                                asyncVarNameEntry
                            )
                        val spanSettingReturnKey =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                settingReturnKey
                            )
                        val spanAsyncPrefix =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                asyncPrefix
                            )
                        runBlocking {
                            ErrLogger.sendErrLog(
                                context,
                                ErrLogger.SettingActionErrType.S_VAR,
                                "Not ${spanAwaitKeyName} async var: ${spanAwaitAsyncVarName} or forbidden ${spanAsyncPrefix} prefix var name in ${spanSettingReturnKey}, settingKeyName: ${spanSettingKeyName}",
                                keyToSubKeyConWhere,
                            )
                        }
                        return true
                    }
                    val varMarkIndex = indexToSubKeyConWithVarMark.first
                    val awaitVarNameIndex = indexToSubKeyPairListWithAwait.first
                    if(
                        varMarkIndex != awaitVarNameIndex
                    ) {
                        val spanSettingKeyName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                settingKey
                            )
                        val spanAwaitKeyName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                awaitKey
                            )
                        val spanAwaitAsyncVarName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errRedCode,
                                asyncVarNameEntry
                            )
                        runBlocking {
                            ErrLogger.sendErrLog(
                                context,
                                ErrLogger.SettingActionErrType.S_VAR,
                                "Must ${spanAwaitKeyName} in using async var: ${spanAwaitAsyncVarName}, settingKeyName: ${spanSettingKeyName}",
                                keyToSubKeyConWhere,
                            )
                        }
                        return true
                    }
                    val isNotAwaitBeforeAsyncVarErr = let isNotAwaitBeforeAsyncVarErr@ {
                        val subKeyPairList = indexToSubKeyPairListWithAwait.second
                        val varMarkIndexInEqual = subKeyPairList.indexOfFirst {
                                subKeyPair ->
                                subKeyPair.second.contains(asyncVarMark)
                            }
                        val awaitVarNameIndexInEqual = subKeyPairList.indexOfFirst {
                                subKeyPair ->
                            if(
                                subKeyPair.first != awaitKey
                            ) return@indexOfFirst false
                            SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                                subKeyPair.second
                            ).contains(asyncVarNameEntry)
                        }
                        varMarkIndexInEqual <= awaitVarNameIndexInEqual
                    }
                    if(!isNotAwaitBeforeAsyncVarErr) return@forEach
                    val spanSettingKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            settingKey
                        )
                    val spanAwaitKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            awaitKey
                        )
                    val spanAwaitAsyncVarName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            asyncVarNameEntry
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.SettingActionErrType.S_VAR,
                            "Must not ${spanAwaitKeyName} after in using async var: ${spanAwaitAsyncVarName}, settingKeyName: ${spanSettingKeyName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun isAwaitNotAsyncVarErr(
                context: Context?,
                settingKeyAndAsyncVarNameToAwaitVarNameList: List<
                        Pair<
                                String,
                                Pair<
                                        String,
                                        List<String>?
                                        >
                                >
                        >,
                keyToSubKeyConWhere: String,
            ): Boolean {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyAndAsyncVarNameToAwaitVarNameList: ${settingKeyAndAsyncVarNameToAwaitVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
                val asyncPrefixList = listOf(
                    asyncPrefix,
                    runAsyncPrefix,
                )
                settingKeyAndAsyncVarNameToAwaitVarNameList.forEachIndexed {
                        index, settingKeyToAsyncVarNames ->
                    val curSettingKeyToAwaitVarNameList =
                        settingKeyToAsyncVarNames.second
                    val settingKey = curSettingKeyToAwaitVarNameList.first
                    val awaitVarNameList = let {
                        curSettingKeyToAwaitVarNameList.second
                    }?: return@forEachIndexed
                    val notAsyncVarNameList = awaitVarNameList.filter {
                        awaitVarName ->
                        !asyncPrefixList.any{
                            awaitVarName.startsWith(it)
                        }
                    }
                    if(
                        notAsyncVarNameList.isEmpty()
                    ) return@forEachIndexed
                    val spanSettingKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            settingKey
                        )
                    val spanAwaitKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            awaitKey
                        )
                    val spanAwaitVarNameListCon =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            notAsyncVarNameList.joinToString(",")
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.SettingActionErrType.S_VAR,
                            "In ${spanAwaitKeyName}, must async var name: ${spanAwaitVarNameListCon}: settingKeyName: ${spanSettingKeyName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun isAwaitDuplicateAsyncVarErr(
                context: Context?,
                settingKeyAndAsyncVarNameToAwaitVarNameList: List<
                        Pair<
                                String,
                                Pair<
                                        String,
                                        List<String>?
                                        >
                                >
                        >,
                keyToSubKeyConWhere: String,
            ): Boolean {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyAndAsyncVarNameToAwaitVarNameList: ${settingKeyAndAsyncVarNameToAwaitVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
                settingKeyAndAsyncVarNameToAwaitVarNameList.forEachIndexed {
                        index, settingKeyToAsyncVarNames ->
                    val curSettingKeyToAwaitVarNameList =
                        settingKeyToAsyncVarNames.second
                    val settingKey = curSettingKeyToAwaitVarNameList.first
                    val awaitVarNameList = let {
                        curSettingKeyToAwaitVarNameList.second
                    }?: return@forEachIndexed
                    val duplicateAsyncVarNameList = awaitVarNameList.groupBy { it }
                        .mapValues { it.value.size }
                        .filterKeys { it.isNotEmpty() }
                        .filter {
                            it.value > 1
                        }.map {
                            it.key
                        }
                    if(
                        duplicateAsyncVarNameList.isEmpty()
                    ) return@forEachIndexed
                    val spanSettingKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            settingKey
                        )
                    val spanAwaitKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            awaitKey
                        )
                    val spanAwaitVarNameListCon =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            duplicateAsyncVarNameList.joinToString(",")
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.SettingActionErrType.S_VAR,
                            "Duplicate ${spanAwaitKeyName} var name: ${spanAwaitVarNameListCon}: settingKeyName: ${spanSettingKeyName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun isNotExistReturnOrFuncOrValue(
                context: Context?,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val settingVarMainKey = SettingActionKeyManager.SettingActionsKey.SETTING_VAR.key
                val funcKey = SettingActionKeyManager.SettingSubKey.FUNC.key
                val onReturnKey = SettingActionKeyManager.SettingSubKey.ON_RETURN.key
                val valueKey = SettingActionKeyManager.SettingSubKey.VALUE.key
                val funcKeyRegex = Regex("\\?${funcKey}=")
                val returnKeyRegex = Regex("\\?${onReturnKey}=")
                val valueKeyRegex = Regex("\\?${valueKey}=")
                keyToSubKeyConList.forEach {
                    settingKeyToSubKeyCon ->
                    val settingKeyName = settingKeyToSubKeyCon.first
                    if(
                        settingKeyName != settingVarMainKey
                    ) return@forEach
                    val subKeyCon = settingKeyToSubKeyCon.second
                    val isContainFuncOrReturnOrValue = funcKeyRegex.containsMatchIn(subKeyCon)
                            || returnKeyRegex.containsMatchIn(subKeyCon)
                            || valueKeyRegex.containsMatchIn(subKeyCon)
                    if(
                        isContainFuncOrReturnOrValue
                    ) return@forEach
//                    val spanSettingKeyName =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errRedCode,
//                            settingKeyName
//                        )
                    val spanSubKeyCon =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            subKeyCon
                        )
                    val spanImageVarMainKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            settingVarMainKey
                        )
                    val spanFuncKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            funcKey
                        )
                    val spanOnReturnKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            onReturnKey
                        )
                    val spanOnValueKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            valueKey
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.SettingActionErrType.S_VAR,
                            "${spanFuncKey} or ${spanOnReturnKey} or ${spanOnValueKey} sub key must use in ${spanImageVarMainKey} settingKey: subKeyCon: ${spanSubKeyCon}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun isRunPrefixVarUseErr(
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
//                            "varNameWithDoll: ${varNameWithSharp}",
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
                settingKeyToPrivateVarNameList: List<Pair<String, String>>,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val keyToSubKeyListCon = makeKeyToSubKeyListCon(
                    keyToSubKeyConList,
                )
                execIsNotUseVarErr(
                    context,
                    settingKeyToPrivateVarNameList,
                    keyToSubKeyListCon,
                    keyToSubKeyConWhere,
                ).let {
                        isNotUseVarErr ->
                    if(isNotUseVarErr) return true
                }
                return false
            }


            fun isBlankSVarOrSAcVarErr(
                context: Context?,
                settingKeyToPrivateVarNameList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val settingKeyToBlankVarNamePair = settingKeyToPrivateVarNameList.filter {
                    settingKeyToPrivateVarName ->
                    settingKeyToPrivateVarName.first != settingReturnKey
                }.firstOrNull {
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

            fun isNotDefinitionVarErr(
                context: Context?,
                settingKeyToNoRunVarNameList: List<Pair<String, String>>,
                keyToSubKeyConList: List<Pair<String, String>>,
                stringVarKeyList: List<String>?,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val regexStrTemplate = "([$][{]%s[}])"
//                val returnUseStringKeyList = settingKeyToNoRunVarNameList.filter {
//                        settingKeyToNoRunVarName ->
//                    settingKeyToNoRunVarName.first == settingReturnKey
//                }.map {
//                    it.second
//                }
                val stringKeyList = let {
                    val stringKeyListInCode = settingKeyToNoRunVarNameList.filter {
                        settingKeyToNoRunVarName ->
                        settingKeyToNoRunVarName.first !=
                                settingReturnKey
                    }.map {
                        it.second
                    }
                    val plusStringKeyList = stringVarKeyList?.filter {
                        !stringKeyListInCode.contains(it)
                    } ?: emptyList()
                    stringKeyListInCode +
                            plusStringKeyList +
                            listOf(SettingActionKeyManager.ValueStrVar.itPronoun)
                }
                val settingKeyListConRegex = let {
                    stringKeyList.map {
                        regexStrTemplate.format(it)
                    }.joinToString("|")
                }.toRegex()
                val keyToSubKeyListCon = makeKeyToSubKeyListCon(
                    keyToSubKeyConList,
                )
                val keyToSubKeyListConWithRemoveVar = keyToSubKeyListCon.replace(
                    settingKeyListConRegex,
                    String()
                )
//                val notDefinitionStringKeyInReturn = returnUseStringKeyList.firstOrNull {
//                    !stringKeyList.contains(it)
//                }
                val findVarMarkRegex = Regex(".[$][{][a-zA-Z0-9_]+[}]")
                val leaveVarMarkList =
                    findVarMarkRegex
                        .findAll(keyToSubKeyListConWithRemoveVar).filter {
                            varMarkResult ->
                            val varMark = varMarkResult.value
                            !varMark.startsWith("\\")
                        }.map {
                            it.value.replace(
                                Regex("^[^$]"),
                                String()
                            )
                        }
                if(
                    !leaveVarMarkList.any()
                ) return false
//                        ?: return false
//                    notDefinitionStringKeyInReturn
//                        ?: findVarMarkRegex
//                            .find(keyToSubKeyListConWithRemoveVar)
//                            ?.value
//                        ?: return false
                val spanLeaveVarMark =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        leaveVarMarkList.first()
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_VAR,
                        "Not difinition var: ${spanLeaveVarMark}",
                        keyToSubKeyConWhere,
                    )
                }
//
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisNotReplaceVarErr.txt").absolutePath,
//                    listOf(
//                        "settingKeyToNoRunVarNameList: ${settingKeyToNoRunVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                        "keyToSubKeyListConWithRemoveVar: ${keyToSubKeyListConWithRemoveVar}",
//                        "settingKeyListConRegex: ${settingKeyListConRegex}",
//                        "leaveVarMark: ${leaveVarMark}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                return true
            }

            fun isShadowTopLevelVarErr(
                context: Context?,
                settingKeyToNoRunVarNameList: List<Pair<String, String>>,
                topLevelValueStrKeyList: List<String>?,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val shadowSettingKeyToNoRunVarName = settingKeyToNoRunVarNameList.filter {
                    settingKeyToNoRunVarName ->
                    settingKeyToNoRunVarName.first != settingReturnKey
                }.firstOrNull {
                    settingKeyToNoRunVarName ->
                    topLevelValueStrKeyList?.contains(
                        settingKeyToNoRunVarName.second
                    ) == true
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisShadowTopLevelVarErr.txt").absolutePath,
//                    listOf(
//                        "settingKeyToNoRunVarNameList: ${settingKeyToNoRunVarNameList}",
//                        "shadowSettingKeyToNoRunVarName: ${shadowSettingKeyToNoRunVarName}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
                if(
                    shadowSettingKeyToNoRunVarName == null
                ) return false
                val spanSettingKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    shadowSettingKeyToNoRunVarName.first
                )
                val spanShadowTopLevelVarKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        shadowSettingKeyToNoRunVarName.second
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_VAR,
                        "shaddow top level var: ${spanShadowTopLevelVarKey} setting key ${spanSettingKey}",
                        keyToSubKeyConWhere,
                    )
                }

                return true
            }

            private fun execIsNotReplaceVarErr(
                context: Context?,
                subKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val replaceKeyToSubKeyListCon = subKeyListCon.replace(
                    "${'$'}{${SettingActionKeyManager.ValueStrVar}}",
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
                settingKeyToPrivateVarNameList: List<Pair<String, String>>,
                keyToSubKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                settingKeyToPrivateVarNameList.filter {
                    settingKeyToPrivateVarName ->
                    settingKeyToPrivateVarName.first != settingReturnKey
                }.forEach {
                        settingKeyToVarName ->
                    val varName = settingKeyToVarName.second
                    if(
                        varName.startsWith(escapeRunPrefix)
                    ) return@forEach
                    val varNameWithDoll = "${'$'}{${varName}}"
                    val isContain =
                        keyToSubKeyListCon.contains(varNameWithDoll)
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
//                        FileSystems.writeFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lsVar.txt").absolutePath,
//                            listOf(
//                                "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                                "keyToSubKeyListCon: ${keyToSubKeyListCon}",
//                            ).joinToString("\n")
//                        )
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

            fun makeSettingKeyToPrivateVarNameList(
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
                    val varName = keyToSubKeyCon.second
                        .split(subKeySeparator)
                        .firstOrNull()?.let {
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
                }.let {
                    filterSettingKeyToDefinitionListByValidVarDefinition(it)
                }
            }

            fun makeSettingKeyToNoRunVarNameList(
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
                    val varName = keyToSubKeyCon.second
                        .split(subKeySeparator)
                        .firstOrNull()?.let {
                            QuoteTool.trimBothEdgeQuote(it)
                        } ?: return@map defaultReturnPair
                    if(
                        varName.startsWith(escapeRunPrefix)
                    ){
                        return@map String() to String()
                    }
                    settingKey to varName
                }.filter {
                    it.first.isNotEmpty()
                            && it.second.isNotEmpty()
                }.let {
                    filterSettingKeyToDefinitionListByValidVarDefinition(
                        it
                    )
                }
            }

            private fun makeSettingKeyToPrivateVarNameListWithBlank(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, String>> {
                val defaultReturnPair = String() to String()
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    SettingActionKeyManager.SettingActionsKey.entries.map {
                        it.key
                    }
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        !imageActionKeyList.contains(settingKey)
                    ) return@map defaultReturnPair
                    val varName = keyToSubKeyCon.second.split(subKeySeparator).firstOrNull()?.let {
                        QuoteTool.trimBothEdgeQuote(it)
                    } ?: String()
                    settingKey to varName
                }.filter {
                    it.first.isNotEmpty()
                }
            }

            fun makeSettingKeyToSubKeyMap(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, Pair<String, List<String>?>>> {
                val defaultReturnPair =
                    String() to Pair(String(), null)
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    SettingActionKeyManager.SettingActionsKey.entries.map {
                        it.key
                    }
                val asyncVarPrefixList = listOf(
                    asyncPrefix,
                    runAsyncPrefix,
                )
                val awaitSubKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        !imageActionKeyList.contains(settingKey)
                    ) return@map defaultReturnPair
                    val varNameAndSubKeyConList =
                        keyToSubKeyCon.second
                            .split(subKeySeparator)
                    val varName = varNameAndSubKeyConList
                        .firstOrNull()?.let {
                            QuoteTool.trimBothEdgeQuote(it)
                        } ?: String()
                    val isAsyncVarName = asyncVarPrefixList.any {
                        varName.startsWith(it)
                    }
                    val awaitVarNameList = varNameAndSubKeyConList
                        .filterIndexed { index, s ->
                            index > 0
                        }.joinToString(subKeySeparator.toString()).let {
                            CmdClickMap.createMap(
                                it,
                                subKeySeparator
                            ).toMap().get(awaitSubKey)
                        }?.let {
                            SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                                it,
                            )
                        }
                    if(
                        !isAsyncVarName
                        && awaitVarNameList.isNullOrEmpty()
                    ) return@map defaultReturnPair
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "iaa.txt").absolutePath,
//                        listOf(
//                            "keyToSubKeyCon: ${keyToSubKeyCon}",
//                            "varName: ${varName}",
//                            "isAsyncVarName: ${isAsyncVarName}",
//                            "awaitVarNameList: ${awaitVarNameList}",
//                        ).joinToString("\n") + "\n\n=========\n\n"
//                    )
                    settingKey to Pair(
                        varName,
                        awaitVarNameList
                    )
                }.filter {
                    val settingKey = it.first
                    val varNameToAwaitVarList = it.second
                    val varName = varNameToAwaitVarList.first
                    settingKey.isNotEmpty()
                            && varName.isNotEmpty()

                }
            }

            fun makeSettingKeyAndAsyncVarNameToAwaitVarNameList(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, Pair<String, List<String>?>>> {
                val defaultReturnPair =
                    String() to Pair(String(), null)
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    SettingActionKeyManager.SettingActionsKey.entries.map {
                        it.key
                    }
                val asyncVarPrefixList = listOf(
                    asyncPrefix,
                    runAsyncPrefix,
                )
                val awaitSubKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        !imageActionKeyList.contains(settingKey)
                    ) return@map defaultReturnPair
                    val varNameAndSubKeyConList =
                        keyToSubKeyCon.second
                            .split(subKeySeparator)
                    val varName = varNameAndSubKeyConList
                        .firstOrNull()?.let {
                            QuoteTool.trimBothEdgeQuote(it)
                        } ?: String()
                    val isAsyncVarName = asyncVarPrefixList.any {
                        varName.startsWith(it)
                    }
                    val awaitVarNameList = varNameAndSubKeyConList
                        .filterIndexed { index, s ->
                            index > 0
                        }.joinToString(
                            subKeySeparator.toString()
                        ).let {
                           CmdClickMap.createMap(
                               it,
                               subKeySeparator
                           ).map awaitVarNameList@ {
                                   subKeyToCon ->
                               if(
                                   subKeyToCon.first != awaitSubKey
                               ) return@awaitVarNameList emptyList()
                               SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                                   subKeyToCon.second,
                               )
                           }.flatten()
                            //..get(awaitSubKey)
                        }
                    if(
                        !isAsyncVarName
                        && awaitVarNameList.isEmpty()
                    ) return@map defaultReturnPair
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "iaa.txt").absolutePath,
//                        listOf(
//                            "keyToSubKeyCon: ${keyToSubKeyCon}",
//                            "varName: ${varName}",
//                            "isAsyncVarName: ${isAsyncVarName}",
//                            "awaitVarNameList: ${awaitVarNameList}",
//                        ).joinToString("\n") + "\n\n=========\n\n"
//                    )
                    settingKey to Pair(
                        varName,
                        awaitVarNameList
                    )
                }.filter {
                    val settingKey = it.first
                    val varNameToAwaitVarList = it.second
                    val varName = varNameToAwaitVarList.first
                    settingKey.isNotEmpty()
                            && varName.isNotEmpty()

                }
            }

            private fun makeSettingKeyToPrivateRunVarNameList(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, String>> {
                val defaultReturnPair = String() to String()
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    SettingActionKeyManager.SettingActionsKey.entries.map {
                        it.key
                    }
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        !imageActionKeyList.contains(settingKey)
                    ) return@map defaultReturnPair
                    val varName = keyToSubKeyCon.second
                        .split(subKeySeparator)
                        .firstOrNull()?.let {
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
                }.let {
                    filterSettingKeyToDefinitionListByValidVarDefinition(
                        it
                    )
                }
            }
        }


        private object SettingImport {

            private val sIfKeyName = SettingActionKeyManager.SettingSubKey.S_IF.key
            private val valueSeparator = SettingActionKeyManager.valueSeparator
            private val imageActionVarKey = SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
            private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
            private val asyncRunPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
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
                curMapLoopKey: String,
                loopKeyToAsyncDeferredVarNameValueStrMap: LoopKeyToAsyncDeferredVarNameValueStrMap?,
                privateLoopKeyVarNameValueStrMapClass: PrivateLoopKeyVarNameValueStrMap,
                loopKeyToVarNameValueStrMapClass: LoopKeyToVarNameValueStrMap,
                importedVarNameToValueStrMap: Map<String, String?>?,
                exitSignalManager: ExitSignalManager,
                keyToSubKeyCon: Pair<String, String>?,
                originImportPath: String?,
                keyToSubKeyConWhere: String,
            ): Pair<String,
                    Triple<
                            String,
                            List<Pair<String, String>>,
                            Map<String, String?>,
                            >,
                    > {
                val keyToSubKeyContents = listOf(
                    imageActionVarKey,
                    keyToSubKeyCon?.second ?: String()
                ).joinToString("=")
                val varNameToValueStrMap =
                    makeVarNameToValueStrMap(
                        curMapLoopKey,
                        importedVarNameToValueStrMap,
                        loopKeyToVarNameValueStrMapClass,
                        privateLoopKeyVarNameValueStrMapClass,
                        null,
                        null,
                    )
                val actionImportMapBeforeReplace = ImportMapMaker.comp(
                    keyToSubKeyContents,
                    "${imageActionVarKey}="
                )
                val actionImportMap =
                    CmdClickMap.MapReplacer.replaceToPairList(
                        actionImportMapBeforeReplace,
                        varNameToValueStrMap
                    ).toMap()
                val topIAcVarName = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        imageActionVarKey
                    )
                )
                val awaitVarNameList = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
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
                                ?.getAsyncVarNameToValueStrAndExitSignal(
                                    curMapLoopKey
                                )?.get(awaitVarName)
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
                                CheckTool.ligthBlue,
                                SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
                            )
                        val importPath = actionImportMap.get(
                            SettingActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
                        )
                        val spanImportPath =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errRedCode,
                                importPath ?: String()
                            )
                        runBlocking {
                            ErrLogger.sendErrLog(
                                context,
                                ErrLogger.SettingActionErrType.AWAIT,
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
                val judgeTargetStr = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
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
                ).filter {
                    it.first.isNotEmpty()
                }.map {
                    argNameToValueStr ->
                    argNameToValueStr.first to CmdClickMap.replaceByBackslashToNormal(
                        argNameToValueStr.second,
                        varNameToValueStrMap
                    )
                }
                val isImportToErrType = when(judgeTargetStr.isEmpty()) {
                    true -> true to null
                    else -> {
                        SettingIfManager.handle(
                            sIfKeyName,
                            judgeTargetStr,
                            argsPairList,
                        )
                    }
                }
                val blankReturnValueStr =
                    Pair (
                        String(),
                            Triple(
                                String(),
                                emptyList<Pair<String, String>>(),
                                emptyMap<String, String?>(),
                            ),
                    )
                val errType = isImportToErrType.second
                if(errType != null){
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.SettingActionErrType.S_IF,
                        errType.errMessage,
                        keyToSubKeyConWhere
                    )
                    exitSignalManager.setExit()
                    return blankReturnValueStr
                }
                val isImport = isImportToErrType.first ?: false
                if(
                    !isImport
                ) return blankReturnValueStr
                val importPathSrc = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
                    )
                )
                val importRepMapBeroreReplace = makeRepValHolderMap(
                    actionImportMapBeforeReplace.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
                    )
                )
                val importRepMap =
                    CmdClickMap.MapReplacer.replaceToPairList(
                        importRepMapBeroreReplace,
                        varNameToValueStrMap
                    ).toMap()
                val isBeforeImportErr = withContext(Dispatchers.IO) {
                    val isCircleImportErrJob = async {
                        ImportErrManager.isCircleImportErr(
                            context,
                            originImportPath,
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
                    importPathSrc,
                    fannelInfoMap,
                    setReplaceVariableMap,
                ).replace(
                    Regex("[${SettingActionKeyManager.landSeparator}]+$"),
                    String(),
                )
                val importSrcCon = CmdClickMap.replaceHolderForJsAction(
                    importSrcConBeforeReplace,
                    importRepMap
                )
                val importedKeyToSubKeyConList = KeyToSubKeyMapListMaker.make(importSrcCon)
                val settingKeyToVarNameListForReturn = makeSettingKeyToVarNameListForReturn(
                    importedKeyToSubKeyConList
                ).let {
                    filterSettingKeyToDefinitionListByValidVarDefinition(
                        it
                    )
                }
                val isErr = withContext(Dispatchers.IO) {
                    val keyToSubKeyConWhereInImportPath = listOf(
                        keyToSubKeyConWhere,
                        "by import path $importPathSrc"
                    ).joinToString("\n")
                    val isImportShadowVarMarkErrJob = async {
                        ImportErrManager.isImportShadowVarMarkErr(
                            context,
                            importPathSrc,
                            importSrcConBeforeReplace,
                            importRepMapBeroreReplace,
                            keyToSubKeyConWhere,
                        )
                    }
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
                            settingKeyToVarNameListForReturn,
                            importedKeyToSubKeyConList,
                            topIAcVarName,
                            keyToSubKeyConWhereInImportPath
                        )
                    }
                    isImportShadowVarMarkErrJob.await()
//                            || isGlobalVarNameErrWithRunPrefixJob.await()
//                            || isGlobalVarNameMultipleExistErrWithoutRunPrefixJob.await()
                            || isSettingReturnNotLastErrWithoutRunPrefixJob.await()
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
                return Pair (
                    importPathSrc,
                    Triple(
                        topIAcVarName,
                        importedKeyToSubKeyConList,
                        awaitVarNameValueStrMap
                    ),
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

        private class SettingVarExecutor {

            private val sIfKeyName = SettingActionKeyManager.SettingSubKey.S_IF.key
            private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
            private val asyncRunPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
            private var itPronounValueStrToBreakSignal: Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >? = null
            private var isNext = true
            private val valueSeparator = SettingActionKeyManager.valueSeparator
            private val itPronoun = SettingActionKeyManager.ValueStrVar.itPronoun

            suspend fun exec(
                fragment: Fragment,
//                settingVarKey: String,
//                subKeyCon: String,
                mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
                busyboxExecutor: BusyboxExecutor?,
                editConstraintListAdapterArg: EditConstraintListAdapter?,
                curMapLoopKey: String,
                loopKeyToAsyncDeferredVarNameValueStrMap: LoopKeyToAsyncDeferredVarNameValueStrMap?,
                privateLoopKeyVarNameValueStrMapClass: PrivateLoopKeyVarNameValueStrMap,
                loopKeyToVarNameValueStrMapClass: LoopKeyToVarNameValueStrMap,
                importedVarNameToValueStrMap: Map<String, String?>?,
                exitSignalManager: ExitSignalManager,
                settingVarName: String,
                renewalVarName: String?,
                keyToSubKeyConWhere: String,
            ): Pair<Pair<String, String?>, SettingActionKeyManager.BreakSignal?>? {
                val context = fragment.context
                mainSubKeyPairList.forEach {
                        mainSubKeyPair ->
                    val varNameToValueStrMap = makeVarNameToValueStrMap(
                        curMapLoopKey,
                        importedVarNameToValueStrMap,
                        loopKeyToVarNameValueStrMapClass,
                        privateLoopKeyVarNameValueStrMapClass,
                        null,
                        mapOf(
                            itPronoun to itPronounValueStrToBreakSignal?.first
                        )
                    )
                    val mainSubKey = mainSubKeyPair.first
                    val mainSubKeyMapSrc = mainSubKeyPair.second
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "iargsPairList_${settingVarName}.txt").absolutePath,
//                        listOf(
//                            "mainSubKeyMap: ${mainSubKeyMap}",
//
//                        ).joinToString("\n")
//                    )
//                        .map {
//                        replaceItPronoun(it.key) to replaceItPronoun(it.value)
//                    }.toMap()
                    val privateSubKeyClass = SettingActionKeyManager.SettingSubKey.entries.firstOrNull {
                        it.key == mainSubKey
                    } ?: return@forEach
                    when(privateSubKeyClass) {
                        SettingActionKeyManager.SettingSubKey.SETTING_VAR,
                        SettingActionKeyManager.SettingSubKey.SETTING_RETURN,
                        SettingActionKeyManager.SettingSubKey.ARGS -> {}
                        SettingActionKeyManager.SettingSubKey.VALUE -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val valueString = let {
                                val mainSubKeyMap = CmdClickMap.MapReplacer.replaceToPairList(
                                    mainSubKeyMapSrc,
                                    varNameToValueStrMap
                                ).toMap()
                                val rawValue = mainSubKeyMap.get(mainSubKey)?.let {
                                    BackslashTool.toNormal(it)
                                } ?: return@let null
                                if(
                                    !SettingActionKeyManager.ValueStrVar.matchStringVarName(rawValue)
                                ) return@let rawValue
                                val curIVarKey =
                                    mainSubKeyMap.get(mainSubKey)?.let {
                                        SettingActionKeyManager.ValueStrVar.convertStrKey(it)
                                    }
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "l_value_varNameToValueStrMap_${settingVarName}.txt").absolutePath,
//                                    listOf(
//                                        "curIVarKey: ${curIVarKey}",
//                                        "rawValue: ${rawValue}",
//                                        "varNameToValueStrMap: ${varNameToValueStrMap}"
//                                    ).joinToString("\n")
//                                )
                                when (true) {
//                                    (curIVarKey == itPronoun) -> itPronounValueStrToExitSignal?.first
                                    curIVarKey.isNullOrEmpty() -> null
                                    else -> varNameToValueStrMap.get(curIVarKey)
//                                    privateLoopKeyVarNameValueStrMapClass.getAsyncVarNameToValueStr(
//                                        curMapLoopKey
//                                    )?.get(curIVarKey)
//                                        ?: loopKeyToVarNameValueStrMapClass.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )?.get(curIVarKey)
                                }
                            }
                            itPronounValueStrToBreakSignal = Pair(valueString, null)
//                            itPronounValue = mainSubKeyMap.get(mainSubKey)
//                                ?: String()
                            isNext = true
                        }
                        SettingActionKeyManager.SettingSubKey.AWAIT -> {
                            if(!isNext){
                                isNext = true
                                return@forEach
                            }
                            val mainSubKeyMap = CmdClickMap.MapReplacer.replaceToPairList(
                                mainSubKeyMapSrc,
                                varNameToValueStrMap
                            ).toMap()
                            val awaitVarNameList = mainSubKeyMap.get(
                                privateSubKeyClass.key
                            )?.let {
                                SettingActionKeyManager.AwaitManager.getAwaitVarNameList(it)
                            }

                            awaitVarNameList?.forEach awaitVarNameList@ {
                                awaitVarName ->
                                val isAsyncVar =
                                    awaitVarName.startsWith(asyncRunPrefix)
                                            || awaitVarName.startsWith(asyncPrefix)
                                if(
                                    awaitVarName.startsWith(escapeRunPrefix)
                                    || !isAsyncVar
                                ) return@awaitVarNameList
                                var deferredVarNameToValueStrAndBreakSignal: Deferred<
                                        Pair<
                                                Pair<String, String?>,
                                                SettingActionKeyManager.BreakSignal?
                                                >?
                                        >? = null
                                for (i in 1..awaitWaitTimes) {
                                    deferredVarNameToValueStrAndBreakSignal =
                                        loopKeyToAsyncDeferredVarNameValueStrMap
                                            ?.getAsyncVarNameToValueStrAndExitSignal(
                                                curMapLoopKey
                                            )?.get(awaitVarName)
//                                    FileSystems.updateFile(
//                                        File(
//                                            UsePath.cmdclickDefaultSDebugAppDirPath,
//                                            "image_await_${settingVarName}.txt"
//                                        ).absolutePath,
//                                        listOf(
//                                            "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                            "settingVarName: $settingVarName",
//                                            "curMapLoopKey: ${curMapLoopKey}",
//                                            "awaitVarNameList: ${awaitVarNameList}",
//                                            "awaitVarName: ${awaitVarName}",
//                                            "loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap?.getAsyncVarNameToValueStrAndExitSignal(
//                                                curMapLoopKey
//                                            )}",
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                                        ).joinToString("\n")  + "\n\n========\n\n"
//                                    )
                                    if (
                                        deferredVarNameToValueStrAndBreakSignal != null
                                    ) {
                                        break
                                    }
                                    delay(100)
                                }
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
                                    val spanIVarKeyName =
                                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                            CheckTool.ligthBlue,
                                            SettingActionKeyManager.SettingActionsKey.SETTING_VAR.key
                                        )
                                    runBlocking {
                                        ErrLogger.sendErrLog(
                                            context,
                                            ErrLogger.SettingActionErrType.AWAIT,
                                            "await var name not exist: ${spanAwaitVarName}, setting key: ${spanIVarKeyName}",
                                            keyToSubKeyConWhere,
                                        )
                                    }
                                    return@awaitVarNameList
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
                                    ?: return@awaitVarNameList
                                if(
                                    varName.startsWith(asyncRunPrefix)
                                ) return@awaitVarNameList
                                val valueStr = varNameToValueStr.second
                                privateLoopKeyVarNameValueStrMapClass.put(
                                    curMapLoopKey,
                                    varName,
                                    valueStr
                                )
                            }
                        }
                        SettingActionKeyManager.SettingSubKey.ON_RETURN -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val returnString = let {
                                val mainSubKeyMap = CmdClickMap.MapReplacer.replaceToPairList(
                                    mainSubKeyMapSrc,
                                    varNameToValueStrMap
                                ).toMap()
                                val rawValue = mainSubKeyMap.get(mainSubKey)?.let {
                                    BackslashTool.toNormal(it)
                                } ?: return@let null
                                if(
                                    !SettingActionKeyManager.ValueStrVar.matchStringVarName(rawValue)
                                ) return@let rawValue
                                val curIVarKey =
                                    mainSubKeyMap.get(mainSubKey)?.let {
                                        SettingActionKeyManager.ValueStrVar.convertStrKey(it)
                                    }
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "l_onReturn_varNameToValueStrMap_${settingVarName}.txt").absolutePath,
//                                    listOf(
//                                        "curIVarKey: ${curIVarKey}",
//                                        "rawValue: ${rawValue}",
//                                        "varNameToValueStrMap: ${varNameToValueStrMap}"
//                                    ).joinToString("\n")
//                                )
                                when (true) {
//                                    (curIVarKey == itPronoun) -> itPronounValueStrToExitSignal?.first
                                    curIVarKey.isNullOrEmpty() -> null
                                    else -> varNameToValueStrMap.get(curIVarKey)
//                                    privateLoopKeyVarNameValueStrMapClass.getAsyncVarNameToValueStr(
//                                        curMapLoopKey
//                                    )?.get(curIVarKey)
//                                        ?: loopKeyToVarNameValueStrMapClass.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )?.get(curIVarKey)
                                }
                            }
                            VarErrManager.isGlobalVarNullResultErr(
                                context,
                                renewalVarName ?: settingVarName,
                                returnString,
                                privateSubKeyClass,
                                keyToSubKeyConWhere,
                            ).let {
                                    isGlobalVarFuncNullResultErr ->
                                if(
                                    isGlobalVarFuncNullResultErr
                                ) return null
                            }
                            return Pair(
                                Pair(
                                    settingVarName,
                                    returnString
                                ),
                                null,
                            )
                        }
                        SettingActionKeyManager.SettingSubKey.FUNC -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val funcTypeDotMethod = mainSubKeyMapSrc.get(mainSubKey)?.let {
                                funcTypeDotMethodSrc ->
                                    CmdClickMap.replaceByBackslashToNormal(
                                        funcTypeDotMethodSrc,
                                        varNameToValueStrMap,
                                    )
                            } ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMapSrc.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            ).let {
                                pair ->

                                pair.filter {
                                    it.first.isNotEmpty()
                                }
                            }.map {
                                argNameToValueStr ->
                                argNameToValueStr.first to
                                        CmdClickMap.replaceByBackslashToNormal(
                                            argNameToValueStr.second,
                                            varNameToValueStrMap,
                                        )
                            }
//                            val quoteStr = """"aaa\"""""
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "limport.txt").absolutePath,
//                                listOf(
////                                    "keyToSubKeyCon: ${keyToSubKeyCon}",
////                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
////                                    "subKeyCon: ${subKeyCon}",
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "argsPairList: ${argsPairList}",
//                                    "quoteStr: ${quoteStr}",
//                                    "QuoteTool.trimBothEdgeQuote(quoteStr): ${QuoteTool.trimBothEdgeQuote(quoteStr)}",
//                                    "BackslashTool.toNormal(quoteStr): ${BackslashTool.toNormal(quoteStr)}",
//                                ).joinToString("\n") + "\n\n==========\n\n"
//                            )
                            val resultValueStrToExitMacroAndCheckErr = SettingFuncManager.handle(
                                fragment,
                                funcTypeDotMethod,
                                argsPairList,
                                busyboxExecutor,
                                editConstraintListAdapterArg,
//                                varNameToValueStrMap
//                                        (privateLoopKeyVarNameBitmapMapClass.getAsyncVarNameToBitmap(curMapLoopKey) ?: emptyMap<>()),
                            )
                            val checkErr = resultValueStrToExitMacroAndCheckErr?.second
                            if(checkErr != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.SettingActionErrType.FUNC,
                                        checkErr.errMessage,
                                        keyToSubKeyConWhere,
                                    )
                                }
                                itPronounValueStrToBreakSignal = null
                                return@forEach
                            }
                            val resultValueStrToExitMacro = resultValueStrToExitMacroAndCheckErr?.first
                            VarErrManager.isGlobalVarNullResultErr(
                                context,
                                renewalVarName ?: settingVarName,
                                resultValueStrToExitMacro?.first,
                                privateSubKeyClass,
                                keyToSubKeyConWhere,
                            ).let {
                                    isGlobalVarFuncNullResultErr ->
                                if(
                                    isGlobalVarFuncNullResultErr
                                ) return null
                            }
                            itPronounValueStrToBreakSignal = resultValueStrToExitMacro

                        }
                        SettingActionKeyManager.SettingSubKey.S_IF -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val judgeTargetStr = mainSubKeyMapSrc.get(mainSubKey)?.let {
                                    judgeTargetStrSrc ->
                                CmdClickMap.replaceByBackslashToNormal(
                                    judgeTargetStrSrc,
                                    varNameToValueStrMap,
                                )
                            } ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMapSrc.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            ).filter {
                                it.first.isNotEmpty()
                            }.map {
                                    argNameToValueStr ->
                                argNameToValueStr.first to
                                        CmdClickMap.replaceByBackslashToNormal(
                                            argNameToValueStr.second,
                                            varNameToValueStrMap,
                                        )
                            }
                            val isNextToErrType = SettingIfManager.handle(
                                sIfKeyName,
                                judgeTargetStr,
                                argsPairList,
//                                varNameToValueStrMap,
                            )
                            val errType = isNextToErrType.second
                            if(errType != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.SettingActionErrType.S_IF,
                                        errType.errMessage,
                                        keyToSubKeyConWhere
                                    )
                                }
                                itPronounValueStrToBreakSignal = null
                                isNext = false
                                exitSignalManager.setExit()
                                return@forEach
                            }
                            val isNextBool = isNextToErrType.first ?: false
                            isNext = isNextBool
                        }
                    }
                    if(privateSubKeyClass != SettingActionKeyManager.SettingSubKey.S_IF){
                        isNext = true
                    }
                }
                val isNoImageVar =
                    settingVarName.startsWith(escapeRunPrefix)
                            || settingVarName.startsWith(asyncRunPrefix)
                val isEscape =
                    isNoImageVar
                            && itPronounValueStrToBreakSignal?.second != SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                return when(isEscape){
                    true -> null
                    else -> Pair(
                        Pair(
                            settingVarName,
                            itPronounValueStrToBreakSignal?.first,
                            ),
                        itPronounValueStrToBreakSignal?.second,
                        )
                }
            }
//            private fun replaceItPronoun(con: String): String {
//                return con.replace(itReplaceVarStr, itPronounValue)
//            }
        }

        private class SettingReturnExecutor {

            private var isNext = true
            private val valueSeparator = SettingActionKeyManager.valueSeparator
            private val outputReturnSignal =
                SettingActionKeyManager.SettingReturnManager.OutputReturn.OUTPUT_RETURN
            private val returnSignal = SettingActionKeyManager.BreakSignal.RETURN_SIGNAL
            private val sIf = SettingActionKeyManager.SettingReturnManager.SettingReturnKey.S_IF

            suspend fun exec(
                fragment: Fragment,
                mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
                curMapLoopKey: String,
                privateLoopKeyVarNameValueStrMapClass: PrivateLoopKeyVarNameValueStrMap,
                loopKeyToVarNameValueStrMapClass: LoopKeyToVarNameValueStrMap,
                importedVarNameToValueStrMap: Map<String, String?>?,
                exitSignalManager: ExitSignalManager,
                valueStrBeforeReplace: String,
                keyToSubKeyConWhere: String,
            ): Pair<
                    Pair<
                            SettingActionKeyManager.SettingReturnManager.OutputReturn,
                            String
                            >?,
                    SettingActionKeyManager.BreakSignal?
                    >? {
                val context = fragment.context
                val varNameToValueStrMap = makeVarNameToValueStrMap(
                    curMapLoopKey,
                    importedVarNameToValueStrMap,
                    loopKeyToVarNameValueStrMapClass,
                    privateLoopKeyVarNameValueStrMapClass,
                    null,
                    null,
//                        mapOf(
//                            itPronoun to itPronounValueStrToExitSignal?.first
//                        )
                )
                val valueStr = CmdClickMap.replaceByBackslashToNormal(
                    valueStrBeforeReplace,
                    varNameToValueStrMap,
                )
                val isSIf = mainSubKeyPairList.any {
                    val mainSubKey = it.first
                    mainSubKey == sIf.key
                }
                val returnKeyValueStrPair = when(valueStrBeforeReplace.isEmpty()) {
                    true -> null
                    else -> Pair(
                        outputReturnSignal,
                        valueStr,
                    )
                }
                if(!isSIf){
                    return Pair(
                        returnKeyValueStrPair,
                        returnSignal
                    )
                }
                mainSubKeyPairList.forEach {
                        mainSubKeyPair ->

                    val mainSubKey = mainSubKeyPair.first
                    val mainSubKeyMapSrc = mainSubKeyPair.second
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "iargsPairList_${settingVarName}.txt").absolutePath,
//                        listOf(
//                            "mainSubKeyMap: ${mainSubKeyMap}",
//
//                        ).joinToString("\n")
//                    )
//                        .map {
//                        replaceItPronoun(it.key) to replaceItPronoun(it.value)
//                    }.toMap()
                    val privateSubKeyClass = SettingActionKeyManager.SettingReturnManager.SettingReturnKey.entries.firstOrNull {
                        it.key == mainSubKey
                    } ?: return@forEach
                    when(privateSubKeyClass) {
                        SettingActionKeyManager.SettingReturnManager.SettingReturnKey.ARGS -> {}
                        SettingActionKeyManager.SettingReturnManager.SettingReturnKey.S_IF -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val judgeTargetStr = mainSubKeyMapSrc.get(mainSubKey)?.let {
                                    judgeTargetStrSrc ->
                                CmdClickMap.replaceByBackslashToNormal(
                                    judgeTargetStrSrc,
                                    varNameToValueStrMap,
                                )
                            } ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMapSrc.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            ).filter {
                                it.first.isNotEmpty()
                            }.map {
                                    argNameToValueStr ->
                                argNameToValueStr.first to
                                        CmdClickMap.replaceByBackslashToNormal(
                                            argNameToValueStr.second,
                                            varNameToValueStrMap,
                                        )
                            }
                            val isReturnToErrType = SettingIfManager.handle(
                                sIf.key,
                                judgeTargetStr,
                                argsPairList,
                            )
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultSDebugAppDirPath,
//                                    "lsetting_isReturnToErrType_${valueStrBeforeReplace}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "valueStrBeforeReplace: $valueStrBeforeReplace",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "judgeTargetStr: ${judgeTargetStr}",
//                                    "argsPairList: ${argsPairList}",
//                                    "isReturnToErrType: ${isReturnToErrType}"
//                                ).joinToString("\n\n\n")
//                            )
                            val errType = isReturnToErrType.second
                            if(errType != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.SettingActionErrType.S_IF,
                                        errType.errMessage,
                                        keyToSubKeyConWhere
                                    )
                                }
                                isNext = false
                                exitSignalManager.setExit()
                                return null to SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                            }
                            val isReturnBool = isReturnToErrType.first ?: false
                            if(isReturnBool){
                                return returnKeyValueStrPair to returnSignal
                            }
                            isNext = true
                        }
                    }
                    if(privateSubKeyClass != sIf){
                        isNext = true
                    }
                }
                return Pair(
                        null,
                        null
                    )
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
}