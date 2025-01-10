package com.puutaro.commandclick.proccess.edit.image_action

import android.content.Context
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageFuncManager
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

class ImageActionManager {
    companion object {

        val globalVarNameRegex = "[A-Z0-9_]+".toRegex()
        private const val awaitWaitTimes = 5//10

        object BeforeActionImportMapManager {
            private val beforeActionImportMap = mutableMapOf<String, String>()
            private val mutex = ReentrantReadWriteLock()
            suspend fun get(
                importPath: String
            ): String? {
                mutex.readLock().withLock {
                    return beforeActionImportMap.get(importPath)
                }
            }
            suspend fun put(
                importPath: String,
                importCon: String
            ) {
                mutex.writeLock().withLock {
                    beforeActionImportMap.put(importPath, importCon)
                }
            }

            suspend fun init() {
//                ErrLogger.AlreadyErr.init()
                mutex.writeLock().withLock {
                    beforeActionImportMap.clear()
                }
            }
        }

        private object ErrLogger {

            private val durationSec = 5

            enum class ImageActionErrType {
                I_VAR,
                I_AC_VAR,
                I_RETURN,
                FUNC,
                I_IF,
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
                private val mutex = ReentrantReadWriteLock()

                suspend fun update(
                    datetime: LocalDateTime
                ) {
                    mutex.writeLock().withLock {
                        beforeOutputTime = datetime
                    }
                }

                suspend fun get(): LocalDateTime {
                    mutex.readLock().withLock {
                        return beforeOutputTime
                    }
                }
            }

            suspend fun sendErrLog(
                context: Context?,
                errType: ImageActionErrType,
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
                ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN.key
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

        private fun makeSettingKeyToBitmapVarKeyListForReturn(
            keyToSubKeyConList: List<Pair<String, String>>,
        ): List<Pair<String, String>> {
            val defaultReturnPair = String() to String()
            val subKeySeparator = SettingActionKeyManager.subKeySepartor
            val imageKeyList =
                ImageActionKeyManager.ImageActionsKey.entries.map {
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

        private suspend fun makeValueToBitmapMap(
            curMapLoopKey: String,
            topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
            importedVarNameToBitmapMap: Map<String, Bitmap?>?,
            loopKeyToVarNameBitmapMapClass: LoopKeyToVarNameBitmapMap,
            privateLoopKeyVarNameBitmapMapClass: PrivateLoopKeyVarNameBitmapMap,
            curImportedVarNameToBitmapMap: Map<String, Bitmap?>?,
            itToBitmapMap: Map<String, Bitmap?>?,
        ): Map<String, Bitmap?> {
            return (topVarNameToVarNameBitmapMap ?: emptyMap()) +
                    (importedVarNameToBitmapMap ?: emptyMap()) +
                    (loopKeyToVarNameBitmapMapClass
                        .getAsyncVarNameToBitmap(
                            curMapLoopKey
                        )?.toMap() ?: emptyMap()) +
                    (privateLoopKeyVarNameBitmapMapClass
                        .getAsyncVarNameToBitmap(
                            curMapLoopKey
                        )?.toMap() ?: emptyMap()) +
                    (curImportedVarNameToBitmapMap ?: emptyMap()) +
                    (itToBitmapMap ?: emptyMap())
        }

        private fun makeSettingKeyToVarNameList(
            keyToSubKeyConList: List<Pair<String, String>>,
        ): List<Pair<String, String>> {
            val defaultReturnPair = String() to String()
            val subKeySeparator = ImageActionKeyManager.subKeySepartor
            val imageKeyList =
                ImageActionKeyManager.ImageActionsKey.entries.map {
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
            }.filter {
                it.first.isNotEmpty()
                        && it.second.isNotEmpty()
            }
        }
    }

    suspend fun exec(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        topLevelBitmapStrKeyList: List<String>?,
        topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
        keyToSubKeyCon: String?,
        keyToSubKeyConWhere: String,
        editConstraintListAdapterArg: EditConstraintListAdapter? = null,
    ): Map<String, Bitmap?> {
        if(
            fragment == null
            || keyToSubKeyCon.isNullOrEmpty()
        ) return emptyMap()
        val keyToSubKeyConList = makeImageActionKeyToSubKeyList(
            fragment,
            fannelInfoMap,
            keyToSubKeyCon,
            setReplaceVariableMapSrc,
            null,
        )
        if(
            keyToSubKeyConList.isNullOrEmpty()
        ) return emptyMap()
        val imageActionExecutor = ImageActionExecutor(
            WeakReference(fragment),
            fannelInfoMap,
            setReplaceVariableMapSrc,
            busyboxExecutor,
            topLevelBitmapStrKeyList,
        )
        imageActionExecutor.makeResultLoopKeyToVarNameValueMap(
            topVarNameToVarNameBitmapMap,
            imageActionAsyncCoroutine,
            editConstraintListAdapterArg,
            keyToSubKeyConList,
            ImageActionExecutor.mapRoopKeyUnit,
            null,
            keyToSubKeyConWhere,
            null,
            null,
            topLevelBitmapStrKeyList,
        )
        return imageActionExecutor.getResultLoopKeyToVarNameValueMap()
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

    private fun makeImageActionKeyToSubKeyList(
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

    private class LoopKeyToAsyncDeferredVarNameBitmapMap {
        private val loopKeyToAsyncDeferredVarNameBitmapMap = mutableMapOf<
                String,
                MutableMap <
                        String,
                    Deferred<
                            Pair<
                                    Pair<String, Bitmap?>,
                                    ImageActionKeyManager.BreakSignal?
                                    >?
                            >
                    >
                >()
        private val asyncLoopKeyToVarNameBitmapMapMutex = ReentrantReadWriteLock()
        suspend fun getAsyncVarNameToBitmapAndExitSignal(loopKey: String):  MutableMap <
                String,
                Deferred<
                        Pair<
                                Pair<String, Bitmap?>,
                                ImageActionKeyManager.BreakSignal?
                                >?
                        >
                >? {
            return asyncLoopKeyToVarNameBitmapMapMutex.readLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
                    loopKey
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            deferredVarNameBitmapMapAndBreakSignal: Deferred<
                    Pair<
                            Pair<String, Bitmap?>,
                            ImageActionKeyManager.BreakSignal?
                            >?
                    >
        ){
            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
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
                                deferredVarNameBitmapMapAndBreakSignal
                            )
                        }

                        else -> loopKeyToAsyncDeferredVarNameBitmapMap.put(
                            loopKey,
                            mutableMapOf(varName to deferredVarNameBitmapMapAndBreakSignal)
                        )
                    }
                }
            }
        }

        suspend fun clearVarName(
            loopKey: String,
            varName: String,
        ) {
            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
                    loopKey
                )?.remove(varName)
            }
        }

        suspend fun clearAsyncVarNameToBitmapAndExitSignal(loopKey: String) {
            asyncLoopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    private class PrivateLoopKeyVarNameBitmapMap {
        private val privateLoopKeyVarNameBitmapMap =
            mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val privateLoopKeyVarNameBitmapMapMutex = ReentrantReadWriteLock()
        suspend fun getAsyncVarNameToBitmap(
            loopKey: String
        ): MutableMap<String, Bitmap?>? {
            return privateLoopKeyVarNameBitmapMapMutex.readLock().withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            bitmap: Bitmap?,
        ){
            privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                ).let { curPrivateVarNameValueMap ->
                    when (curPrivateVarNameValueMap.isNullOrEmpty()) {
                        false -> curPrivateVarNameValueMap.put(
                            varName,
                            bitmap
                        )

                        else -> privateLoopKeyVarNameBitmapMap.put(
                            loopKey,
                            mutableMapOf(varName to bitmap)
                        )
                    }
                }
            }
        }

        suspend fun initPrivateLoopKeyVarNameBitmapMapMutex(
            loopKey: String
        ) {
            return privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
            privateLoopKeyVarNameBitmapMapMutex.writeLock().withLock {
                privateLoopKeyVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    private class LoopKeyToVarNameBitmapMap {
        private val loopKeyToVarNameBitmapMap =
            mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val loopKeyToVarNameBitmapMapMutex = ReentrantReadWriteLock()
        suspend fun getAsyncVarNameToBitmap(
            loopKey: String
        ): MutableMap<String, Bitmap?>? {
            return loopKeyToVarNameBitmapMapMutex.readLock().withLock {
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            bitmap: Bitmap?,
        ){
            loopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                ).let { curVarNameBitmapMap ->
                    when (curVarNameBitmapMap.isNullOrEmpty()) {
                        false -> curVarNameBitmapMap.put(
                            varName,
                            bitmap
                        )

                        else -> loopKeyToVarNameBitmapMap.put(
                            loopKey,
                            mutableMapOf(varName to bitmap)
                        )
                    }
                }
            }
        }

        suspend fun initPrivateLoopKeyVarNameBitmapMapMutex(
            loopKey: String
        ) {
            return loopKeyToVarNameBitmapMapMutex.writeLock().withLock{
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
            loopKeyToVarNameBitmapMapMutex.writeLock().withLock {
                loopKeyToVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    private class ExitSignalManager {
        private var exitSignal = AtomicBoolean(false)
//        private val exitSignalMutex = Mutex()
        suspend fun setExit(){
//            exitSignalMutex.withLock {
                exitSignal = AtomicBoolean(true)
//            }
        }

        suspend fun get(): Boolean {
            return exitSignal.get()
//            exitSignalMutex.withLock {
//                exitSignal
//            }
        }
    }


    private class ImageActionExecutor(
        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: Map<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
        private val topLevelBitmapStrKeyList: List<String>?,
    ) {

        private val loopKeyToVarNameBitmapMap = LoopKeyToVarNameBitmapMap()
//        mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val privateLoopKeyVarNameBitmapMap = PrivateLoopKeyVarNameBitmapMap()
        private val loopKeyToAsyncDeferredVarNameBitmapMap = LoopKeyToAsyncDeferredVarNameBitmapMap()
        private var exitSignalManager = ExitSignalManager()
        private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
        private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
        private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix

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

        suspend fun getResultLoopKeyToVarNameValueMap(): Map<String, Bitmap?> {
            return loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(mapRoopKeyUnit)
                ?: emptyMap()
        }


        suspend fun makeResultLoopKeyToVarNameValueMap(
            topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
            imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
            editConstraintListAdapterArg: EditConstraintListAdapter?,
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            originImportPathList: List<String>?,
            keyToSubKeyConWhere: String,
            topAcIVarName: String?,
            importedVarNameToBitmapMap: Map<String, Bitmap?>?,
            bitmapVarKeyList: List<String>?,
        ) {
            val fragment = fragmentRef.get()
                ?: return
            val context = fragment.context
                ?: return
            loopKeyToVarNameBitmapMap
                .initPrivateLoopKeyVarNameBitmapMapMutex(curMapLoopKey)
            privateLoopKeyVarNameBitmapMap
                .initPrivateLoopKeyVarNameBitmapMapMutex(curMapLoopKey)
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
                val isBlankIVarOrIAcVarErrJob = async {
                    VarErrManager.isBlankIVarOrIAcVarErr(
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
                        topLevelBitmapStrKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotDefinitionVarErr = async {
                    VarErrManager.isNotDefinitionVarErr(
                        context,
                        settingKeyToNoRunVarNameList,
                        keyToSubKeyConList,
                        bitmapVarKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotReplaceStringVarErrJob = async {
                    VarErrManager.isNotReplaceStringVarErr(
                        context,
                        keyToSubKeyConList.map {
                            it.second
                        }.joinToString("\n"),
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
                val settingKeyToBitmapVarKeyListForReturn =
                    makeSettingKeyToBitmapVarKeyListForReturn(keyToSubKeyConList)
                val settingKeyToBitmapVarKeyListWithIrregular = settingKeyToBitmapVarKeyListForReturn.filter {
                    it.second.isNotEmpty()
                }
                val isIrregularVarNameErrJob = async {
                    VarErrManager.isIrregularVarNameErr(
                        context,
                        settingKeyToBitmapVarKeyListWithIrregular,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToVarNameList = filterSettingKeyToDefinitionListByValidVarDefinition(
                    settingKeyToBitmapVarKeyListWithIrregular
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
                        bitmapVarKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotExistReturnOrFuncJob = async {
                    VarErrManager.isNotExistReturnOrFunc(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val returnKeyToVarNameList = ReturnErrManager.makeReturnKeyToBitmapVarMarkList(
                    keyToSubKeyConList
                )
                val isBlankReturnErrWithoutRunPrefixJob =
                    async {
                        ReturnErrManager.isBlankReturnErrWithoutRunPrefix(
                            context,
                            returnKeyToVarNameList,
                            keyToSubKeyConList,
                            keyToSubKeyConWhere,
                            topAcIVarName,
                        )
                    }
                isAwaitNotAsyncVarErrJob.await()
                        || isAwaitDuplicateAsyncVarErrJob.await()
                        || isAwaitNotDefiniteVarErrJob.await()
                        || isNotAwaitAsyncVarErrOrAwaitInAsyncVarErrJob.await()
                        || isBlankIVarOrIAcVarErrJob.await()
                        || isNotUseVarErrJob.await()
                        || isShadowTopLevelVarErrJob.await()
                        || isNotDefinitionVarErr.await()
                        || isNotReplaceStringVarErrJob.await()
                        || isRunPrefixUseErrJob.await()
                        || isIrregularVarNameErrJob.await()
                        || isSameVarNameErrJob.await()
                        || isNotBeforeDefinitionInReturnErrJob.await()
                        || isNotExistReturnOrFuncJob.await()
                        || isBlankReturnErrWithoutRunPrefixJob.await()
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
                val curImageActionKey =
                    ImageActionKeyManager.ImageActionsKey.entries.firstOrNull {
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
                when (curImageActionKey) {
                    ImageActionKeyManager.ImageActionsKey.IMAGE_ACTION_VAR -> {
                        val importPathAndRenewalVarNameToImportConToVarNameBitmapMap =
                            SettingImport.makeImportPathAndRenewalVarNameToImportCon(
                                context,
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                curMapLoopKey,
                                loopKeyToAsyncDeferredVarNameBitmapMap,
                                exitSignalManager,
                                keyToSubKeyCon,
                                originImportPathList,
                                keyToSubKeyConWhere
                            )
                        val importPath =
                            importPathAndRenewalVarNameToImportConToVarNameBitmapMap.first
                        if (
                            importPath.isEmpty()
                        ) return@forEach
                        val renewalVarNameToImportConToVarNameToBitmapMap =
                            importPathAndRenewalVarNameToImportConToVarNameBitmapMap.second
                        val acIVarName =
                            renewalVarNameToImportConToVarNameToBitmapMap.first
                        if (
                            acIVarName.isEmpty()
                        ) return@forEach
                        val importedKeyToSubKeyConList =
                            renewalVarNameToImportConToVarNameToBitmapMap.second
                        if (
                            importedKeyToSubKeyConList.isEmpty()
                        ) return@forEach
                        val curImportedVarNameToBitmapMap =
                            renewalVarNameToImportConToVarNameToBitmapMap.third
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
                        val addedLoopKey = addLoopKey(curMapLoopKey)
                        if(isAsync){
                            val asyncJob = CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.IO) {
                                    val deferred = async {
                                        val varNameToBitmapMap =
                                            makeValueToBitmapMap(
                                                curMapLoopKey,
                                                topVarNameToVarNameBitmapMap,
                                                importedVarNameToBitmapMap,
                                                loopKeyToVarNameBitmapMap,
                                                privateLoopKeyVarNameBitmapMap,
                                                curImportedVarNameToBitmapMap,
                                                null,
                                                )
                                        val curBitmapVarKeyList = varNameToBitmapMap.map {
                                            it.key
                                        }
//                                        FileSystems.updateFile(
//                                            File(
//                                                UsePath.cmdclickDefaultIDebugAppDirPath,
//                                                "image_acync_acImport00_${acIVarName}.txt"
//                                            ).absolutePath,
//                                            listOf(
//                                                "acIVarName:${acIVarName}",
//                                                "topAcIVarName:${topAcIVarName}",
//                                                "curMapLoopKey: ${curMapLoopKey}",
//                                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
//                                                    addedLoopKey
//                                                )?.map {
//                                                    it.key
//                                                }?.joinToString("\n")}",
//                                                "curMapLoopKey: varNameToBitmapMap: ${varNameToBitmapMap.map {
//                                                    it.key
//                                                }.joinToString("\n")}",
//                                                "curMapLoopKey: curBitmapVarKeyList: ${curBitmapVarKeyList.joinToString("\n")}",
//                                            ).joinToString("\n")  + "\n\n========\n\n"
//                                        )
                                        makeResultLoopKeyToVarNameValueMap(
                                            topVarNameToVarNameBitmapMap,
                                            imageActionAsyncCoroutine,
                                            editConstraintListAdapterArg,
                                            importedKeyToSubKeyConList,
                                            addedLoopKey,
                                            (originImportPathList ?: emptyList()) + listOf(importPath),
                                            "${importPath} by imported",
                                            acIVarName,
                                            varNameToBitmapMap,
                                            curBitmapVarKeyList,
                                        )
                                        null
                                    }
                                    loopKeyToAsyncDeferredVarNameBitmapMap.put(
                                        curMapLoopKey,
                                        acIVarName,
                                        deferred
                                    )
                                }
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                    imageActionAsyncCoroutine.put(asyncJob)
                            }
                            return@forEach
                        }
                        val varNameToBitmapMap =
                            makeValueToBitmapMap(
                                curMapLoopKey,
                                topVarNameToVarNameBitmapMap,
                                importedVarNameToBitmapMap,
                                loopKeyToVarNameBitmapMap,
                                privateLoopKeyVarNameBitmapMap,
                                curImportedVarNameToBitmapMap,
                                null,
                            )
                        val curBitmapVarKeyList = varNameToBitmapMap.map {
                            it.key
                        }
                        makeResultLoopKeyToVarNameValueMap(
                            topVarNameToVarNameBitmapMap,
                            imageActionAsyncCoroutine,
                            editConstraintListAdapterArg,
                            importedKeyToSubKeyConList,
                            addedLoopKey,
                            (originImportPathList ?: emptyList()) + listOf(importPath),
                            "${importPath} by imported",
                            acIVarName,
                            varNameToBitmapMap,
                            curBitmapVarKeyList,
                        )
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultIDebugAppDirPath,
//                                "image_acImport00_${acIVarName}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "acIVarName:${acIVarName}",
//                                "topAcIVarName:${topAcIVarName}",
//                                "curMapLoopKey: ${curMapLoopKey}",
//                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
//                                    curMapLoopKey
//                                )?.map {
//                                    it.key
//                                }?.joinToString("\n")}",
//                            ).joinToString("\n")  + "\n\n========\n\n"
//                        )
                        if (
                            topAcIVarName.isNullOrEmpty()
                        ) return@forEach
                        if(
                            topAcIVarName.startsWith(escapeRunPrefix)
                        ) return@forEach
                        val proposalRenewalVarNameSrcInnnerMapBitmap =
                            privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
                                curMapLoopKey
//                                addLoopKey(curMapLoopKey)
                            )?.get(
                                acIVarName
                            )
                        if(
                            !globalVarNameRegex.matches(acIVarName)
                        ) return@forEach
                        val removedLoopKey = removeLoopKey(curMapLoopKey)
                        if (
                            proposalRenewalVarNameSrcInnnerMapBitmap != null
                        ) {
                            privateLoopKeyVarNameBitmapMap.put(
                                removedLoopKey,
                                topAcIVarName,
                                proposalRenewalVarNameSrcInnnerMapBitmap
                            )
                        }
                        if(
                            !globalVarNameRegex.matches(topAcIVarName)
                        ) return@forEach
                        val proposalRenewalVarNameSrcMapBitmap =
                            loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
                                curMapLoopKey,
//                            addLoopKey(curMapLoopKey)
                        )?.get(acIVarName)
                        if (
                            proposalRenewalVarNameSrcMapBitmap != null
                        ) {
                            loopKeyToVarNameBitmapMap.put(
                                removedLoopKey,
                                topAcIVarName,
                                proposalRenewalVarNameSrcMapBitmap
                            )
                        }
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultIDebugAppDirPath,
//                                "image_acImport_${acIVarName}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "acIVarName:${acIVarName}",
//                                "topAcIVarName:${topAcIVarName}",
//                                "curMapLoopKey: ${curMapLoopKey}",
//                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
//                                    curMapLoopKey
//                                )?.map {
//                                    it.key
//                                }?.joinToString("\n")}",
//                                "removedLoopKey:${removedLoopKey}",
//                                "removed loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
//                                    removedLoopKey
//                                )?.map {
//                                    it.key
//                                }?.joinToString("\n")}",
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                            ).joinToString("\n")  + "\n\n========\n\n"
//                        )
                    }

                    ImageActionKeyManager.ImageActionsKey.IMAGE_VAR -> {
                        val imageVarKey = curImageActionKey.key
                        val mainSubKeyPairList = makeMainSubKeyPairList(
                            imageVarKey,
                            subKeyCon,
                        )
                        val settingVarName = PairListTool.getValue(
                            mainSubKeyPairList,
                            imageVarKey
                        )?.get(imageVarKey)
                            ?: return@forEach
                        val isAsync =
                            settingVarName.startsWith(asyncPrefix)
                                    || settingVarName.startsWith(runAsyncPrefix)
                        if(isAsync){
                            val asyncJob = CoroutineScope(Dispatchers.IO).launch {
                                val deferred = async {
                                    ImageVarExecutor().exec(
                                        fragment,
                                        mainSubKeyPairList,
                                        busyboxExecutor,
                                        editConstraintListAdapterArg,
                                        topVarNameToVarNameBitmapMap,
                                        curMapLoopKey,
                                        loopKeyToAsyncDeferredVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                        loopKeyToVarNameBitmapMap,
                                        importedVarNameToBitmapMap,
                                        exitSignalManager,
                                        settingVarName,
                                        topAcIVarName,
                                        keyToSubKeyConWhere,
                                    )
                                }
//                                val removedLoopKey = removeLoopKey(curMapLoopKey)
                                loopKeyToAsyncDeferredVarNameBitmapMap.put(
                                    curMapLoopKey,
                                    settingVarName,
                                    deferred
                                )
                                val removedLoopKey = removeLoopKey(curMapLoopKey)
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultIDebugAppDirPath,
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
//                                            loopKeyToAsyncDeferredVarNameBitmapMap.getAsyncVarNameToBitmapAndExitSignal(
//                                                curMapLoopKey
//                                            )?.map {
//                                                it.key
//                                            }?.joinToString("\n")
//                                        }",
//                                        "privateLoopKeyVarNameBitmapMap: ${
//                                            privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                                curMapLoopKey
//                                            )?.map {
//                                                it.key
//                                            }?.joinToString("\n")
//                                        }",
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
                                    topAcIVarName.isNullOrEmpty()
                                ) return@launch
                                if (
                                    topAcIVarName.startsWith(escapeRunPrefix)
                                ) return@launch
                                loopKeyToAsyncDeferredVarNameBitmapMap.put(
                                    removedLoopKey,
                                    topAcIVarName,
                                    deferred
                                )
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultIDebugAppDirPath,
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
//                                            loopKeyToAsyncDeferredVarNameBitmapMap.getAsyncVarNameToBitmapAndExitSignal(
//                                                curMapLoopKey
//                                            )?.map {
//                                                it.key
//                                            }?.joinToString("\n")
//                                        }",
//                                        "privateLoopKeyVarNameBitmapMap: ${
//                                            privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                                curMapLoopKey
//                                            )?.map {
//                                                it.key
//                                            }?.joinToString("\n")
//                                        }",
//                                        "loopKeyToVarNameBitmapMap: ${
//                                            loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                                curMapLoopKey
//                                            )?.map {
//                                                it.key
//                                            }?.joinToString("\n")
//                                        }",
//                                        "loopKeyToVarNameBitmapMap: ${
//                                            loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                                removedLoopKey
//                                            )?.map {
//                                                it.key
//                                            }?.joinToString("\n")
//                                        }"
//                                    ).joinToString("\n")
//                                )
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                imageActionAsyncCoroutine.put(asyncJob)
                            }
                            return@forEach
                        }
                        ImageVarExecutor().exec(
                            fragment,
                            mainSubKeyPairList,
                            busyboxExecutor,
                            editConstraintListAdapterArg,
                            topVarNameToVarNameBitmapMap,
                            curMapLoopKey,
                            loopKeyToAsyncDeferredVarNameBitmapMap,
                            privateLoopKeyVarNameBitmapMap,
                            loopKeyToVarNameBitmapMap,
                            importedVarNameToBitmapMap,
                            exitSignalManager,
                            settingVarName,
                            topAcIVarName,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToBitmapAndExitSignal ->
                            val exitSignalClass = varNameToBitmapAndExitSignal.second
                            if (
                                exitSignalClass == ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) {
                                exitSignalManager.setExit()
                                return
                            }
                            val varNameToBitmap = varNameToBitmapAndExitSignal.first
                            privateLoopKeyVarNameBitmapMap.put(
                                curMapLoopKey,
                                varNameToBitmap.first,
                                varNameToBitmap.second
                            )

//                            val isGlobalForRawVar =
//                                globalVarNameRegex.matches(varNameToBitmap.first)
//                            val isNotRunPrefix =
//                                !topAcIVarName.isNullOrEmpty()
//                                        && !topAcIVarName.startsWith(escapeRunPrefix)
//                            val isRegisterToTopForPrivate =
//                                isGlobalForRawVar && isNotRunPrefix
//                            val removedLoopKey =
//                                removeLoopKey(curMapLoopKey)
//                            if (
//                                isRegisterToTopForPrivate
//                                && !topAcIVarName.isNullOrEmpty()
//                            ) {
//                                privateLoopKeyVarNameBitmapMap.put(
//                                    removedLoopKey,
//                                    topAcIVarName,
//                                    varNameToBitmap.second
//                                )
//                            }
//                            val varNameForPut = topAcIVarName
//                                ?: varNameToBitmap.first
//                            val isGlobalRegister =
//                                isGlobalForRawVar
//                                        && globalVarNameRegex.matches(varNameForPut)
//                            if (isGlobalRegister) {
//                                loopKeyToVarNameBitmapMap.put(
//                                    removedLoopKey,
//                                    varNameForPut,
//                                    varNameToBitmap.second
//                                )
//                            }
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultIDebugAppDirPath,
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
//                                    "varName: ${varNameToBitmap.first}",
//                                    "topAcIVarName: ${topAcIVarName}",
//                                    "varNameForPut: ${varNameForPut}",
//                                    "curMapLoopKey privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                            curMapLoopKey
//                                        )?.map {
//                                            it.key
//                                        }?.joinToString("\n")
//                                    }",
//                                    "removedLoopKey privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                            removedLoopKey
//                                        )?.map {
//                                            it.key
//                                        }?.joinToString("\n")
//                                    }",
//                                    "curMapLoopKey loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                            curMapLoopKey
//                                        )?.map {
//                                            it.key
//                                        }?.joinToString("\n")
//                                    }",
//                                    "removedLoopKey loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
//                                            removedLoopKey
//                                        )?.map {
//                                            it.key
//                                        }?.joinToString("\n")
//                                    }",
//                                ).joinToString("\n")
//                            )
                        }
                    }
                    ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN-> {
                        val settingReturnKey = curImageActionKey.key
                        val mainSubKeyPairList = makeMainSubKeyPairList(
                            settingReturnKey,
                            subKeyCon,
                        )
                        val bitmapVarMark = PairListTool.getValue(
                            mainSubKeyPairList,
                            settingReturnKey
                        )?.get(settingReturnKey)
                            ?: String()
                        val returnBitmap = let {
                            val varNameToBitmapMap =
                                makeValueToBitmapMap(
                                    curMapLoopKey,
                                    topVarNameToVarNameBitmapMap,
                                    importedVarNameToBitmapMap,
                                    loopKeyToVarNameBitmapMap,
                                    privateLoopKeyVarNameBitmapMap,
                                    null,
                                    null,
                                )
                            varNameToBitmapMap.get (
                                ImageActionKeyManager.BitmapVar.convertBitmapKey(
                                        bitmapVarMark
                                    )
                                )

                        }
                        ImageReturnExecutor().exec(
                            fragment,
                            exitSignalManager,
                            mainSubKeyPairList,
                            returnBitmap,
                            keyToSubKeyConWhere
                        )?.let {
                                varNameToBitmapAndExitSignal ->
                            val breakSignalClass = varNameToBitmapAndExitSignal.second
                            val varNameToBitmap = varNameToBitmapAndExitSignal.first
                            val returnSignal = varNameToBitmap?.first
                            val isRegisterToTop =
                                returnSignal == ImageActionKeyManager.ImageReturnManager.OutputReturn.OUTPUT_RETURN
                                        && !topAcIVarName.isNullOrEmpty()
                                        && varNameToBitmap != null
                                        && !topAcIVarName.startsWith(escapeRunPrefix)
                            FileSystems.updateFile(
                                File(
                                    UsePath.cmdclickDefaultIDebugAppDirPath,
                                    "lSReturn_${bitmapVarMark}.txt"
                                ).absolutePath,
                                listOf(
                                    "subKeyCon: ${subKeyCon}",
                                    "keyToSubKeyCon.second: ${keyToSubKeyCon.second}",
                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
                                    "varNameToBitmapAndExitSignal: ${varNameToBitmapAndExitSignal}",
                                    "isRegisterToTop: ${isRegisterToTop}",
                                    "exitSignalClass: ${breakSignalClass}",
                                    "curMapLoopKey: ${curMapLoopKey}",
                                    "curMapLoopKey makeValueToBitmapMap: ${makeValueToBitmapMap(
                                        curMapLoopKey,
                                        topVarNameToVarNameBitmapMap,
                                        importedVarNameToBitmapMap,
                                        loopKeyToVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                        null,
                                        null,
                                    )}",
                                    "removeLoopKey(curMapLoopKey): ${removeLoopKey(curMapLoopKey)}",
                                    "removeLoopKey(curMapLoopKey) makeValueToBitmapMap: ${makeValueToBitmapMap(
                                        removeLoopKey(curMapLoopKey),
                                        topVarNameToVarNameBitmapMap,
                                        importedVarNameToBitmapMap,
                                        loopKeyToVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                        null,
                                        null,
                                    )}",
                                ).joinToString("\n\n\n")
                            )
                            if(isRegisterToTop) {
                                ReturnErrManager.isReturnBitmapNullResultErr(
                                    context,
                                    bitmapVarMark,
                                    topAcIVarName,
                                    varNameToBitmap?.second,
                                    ImageActionKeyManager.ImageSubKey.IMAGE_RETURN,
                                    keyToSubKeyConWhere,
                                ).let isReturnVarStrNullResultErr@{ isReturnBitmapNullResultErr ->
                                    if (
                                        !isReturnBitmapNullResultErr
                                    ) return@isReturnVarStrNullResultErr
                                    return
                                }
                            }
                            val removedLoopKey =
                                removeLoopKey(curMapLoopKey)
                            if (
                                isRegisterToTop
                                && !topAcIVarName.isNullOrEmpty()
                                && varNameToBitmap != null
                            ) {
                                privateLoopKeyVarNameBitmapMap.put(
                                    removedLoopKey,
                                    topAcIVarName,
                                    varNameToBitmap.second
                                )
                            }
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
                                ImageActionKeyManager.BreakSignal.EXIT_SIGNAL -> {
                                    exitSignalManager.setExit()
                                    return
                                }
                                ImageActionKeyManager.BreakSignal.RETURN_SIGNAL -> {
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
            val subKeySeparator = ImageActionKeyManager.subKeySepartor
            val landSeparator = ImageActionKeyManager.landSeparator
            val subKeyToConList = CmdClickMap.createMap(
                "${settingVarKey}=${subKeyCon.replace(
                    Regex("[${landSeparator}]+$"),
                    String(),
                )}",
                subKeySeparator
            )
            val argsSubKey = ImageActionKeyManager.ImageSubKey.ARGS.key
            return subKeyToConList.mapIndexed {
                    index, subKeyToCon ->
                val innerSubKeyName = subKeyToCon.first
                val innerSubKeyClass = ImageActionKeyManager.ImageSubKey.entries.firstOrNull {
                    it.key == innerSubKeyName
                } ?: return@mapIndexed Pair(String(), emptyMap())
                val innerSubKeyCon = subKeyToCon.second
                when(innerSubKeyClass) {
                    ImageActionKeyManager.ImageSubKey.IMAGE_VAR,
                    ImageActionKeyManager.ImageSubKey.IMAGE_RETURN,
                    ImageActionKeyManager.ImageSubKey.AWAIT,
                    ImageActionKeyManager.ImageSubKey.ON_RETURN -> {
                        val mainSubKeyMap = mapOf(
                            innerSubKeyName to innerSubKeyCon,
                        )
                        Pair(innerSubKeyName, mainSubKeyMap)
                    }
                    ImageActionKeyManager.ImageSubKey.FUNC,
                    ImageActionKeyManager.ImageSubKey.I_IF-> {
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
                    ImageActionKeyManager.ImageSubKey.ARGS
                        -> Pair(String(), emptyMap())
                }
            }.filter {
                it.first.isNotEmpty()
            }
        }

        private object ImportErrManager {

            private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
            private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
            private val iAcVarKeyName =
                ImageActionKeyManager.ImageActionsKey.IMAGE_ACTION_VAR.key
            private val imageReturnKey =
                ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN.key

            fun isCircleImportErr(
                context: Context?,
                originImportPathList: List<String>?,
                importPath: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    originImportPathList.isNullOrEmpty()
                ) {
                    return false
                }
                if(
                    !originImportPathList.contains(importPath)
                ) return false
                val spanSAcVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        iAcVarKeyName
                    )
                val spanOriginImportPathListCon =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        originImportPathList.joinToString(",")
                    )
                val spanImportPath =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        importPath
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.ImageActionErrType.I_AC_VAR,
                        "must not circle in ${spanSAcVarKeyName}: importPath: ${spanImportPath}, originImportPathListCon: ${spanOriginImportPathListCon}",
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
                        iAcVarKeyName
                    )
                val spanImportPath =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        importPath
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.ImageActionErrType.I_AC_VAR,
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
                val importVarSharpMarkList = importRepMap.filter {
                    ImageActionKeyManager.BitmapVar.matchBitmapVarName(it.value)
                }.map {
                    it.value
                }
                val importShadowVarSharpMark = importVarSharpMarkList.firstOrNull {
                    importSrcConBeforeReplace.contains(it)
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultIDebugAppDirPath, "idebug.txt").absolutePath,
//                    listOf(
//                        "importPath: ${importPath}",
//                        "importVarSharpMarkList: ${importVarSharpMarkList}",
//                        "importShadowVarSharpMark: ${importShadowVarSharpMark}",
//                        "importSrcConBeforeReplace: ${importSrcConBeforeReplace}",
//                    ).joinToString("\n")
//                )
                if(
                    importShadowVarSharpMark.isNullOrEmpty()
                ) return false
                val spanSAcVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        iAcVarKeyName
                    )
                val spanImportShadowVar =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        importShadowVarSharpMark
                    )
                val spanImportPath =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        importPath
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.ImageActionErrType.I_AC_VAR,
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
//                        iAcVarKeyName
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
//                        ErrLogger.ImageActionErrType.I_AC_VAR,
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
//                        iAcVarKeyName
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
//                        ErrLogger.ImageActionErrType.I_AC_VAR,
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
                val iIfKey =
                    ImageActionKeyManager.ImageSubKey.I_IF.key
                val iIfKeyRegex = Regex(
                    "[?]${iIfKey}="
                )
                val lastSubKeyCon = keyToSubKeyConList.lastOrNull {
                    it.first.isNotEmpty()
                }?.second
                    ?: return false
                val lastSettingKeyToVarName = settingKeyToVarNameList.lastOrNull()
                    ?: return false
                val isImageReturnKey =
                    lastSettingKeyToVarName.first == imageReturnKey
                val varName = lastSettingKeyToVarName.second
                val isIrregularVarName = varName.startsWith(
                    escapeRunPrefix
                ) || varName.startsWith(
                    runAsyncPrefix
                ) || varName.startsWith(
                    asyncPrefix
                )
                val containIIfKey = iIfKeyRegex.containsMatchIn(lastSubKeyCon)
                FileSystems.updateFile(
                    File(UsePath.cmdclickDefaultIDebugAppDirPath, "isGlobalVarNameNotLastErrWithoutRunPrefix.txt").absolutePath,
                    listOf(
                        "lastSettingKeyToVarName: ${lastSettingKeyToVarName}",
                        "isImageReturnKey: ${isImageReturnKey}",
                        "varName: ${varName}",
                        "isIrregularVarName: ${isIrregularVarName}",
                        "iIfKeyRegex: ${iIfKeyRegex}",
                        "keyToSubKeyConList: ${keyToSubKeyConList}",
                        "lastSubKeyCon: ${lastSubKeyCon}",
                        "containIIfKey: ${containIIfKey}",
                    ).joinToString("\n\n")
                )
                if (
                    isImageReturnKey
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
                val spanIAdVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        iAcVarKeyName
                    )
                val spanImageReturnKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        imageReturnKey
                    )
                val spanGlobalVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        varName
                    )
                val spanIIfKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        iIfKey
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
                        ErrLogger.ImageActionErrType.I_AC_VAR,
                        "Without ${spanEscapeRunPrefix} prefix in ${spanIAdVarKeyName}, " +
                                "imported last setting key must be ${spanImageReturnKey} without ${spanIIfKey}: ${spanGlobalVarName}",
                        keyToSubKeyConWhere
                    )
                }
                return true
            }
        }

        private object ReturnErrManager {

            private val mainKeySeparator = ImageActionKeyManager.mainKeySeparator
            private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
            private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
            private val imageReturnKey =
                ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN.key

            fun isReturnBitmapNullResultErr(
                context: Context?,
                varName: String?,
                topAcVarName: String?,
                returnBitmap: Bitmap?,
                settingSubKey: ImageActionKeyManager.ImageSubKey,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    returnBitmap != null
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
                        imageReturnKey
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
                        ErrLogger.ImageActionErrType.I_RETURN,
                        "When topAcVar(${spanTopAcVarName}) don't have ${spanEscapeRunPrefix} prefix, ${spanSettingReturnKey} value must be exist: bitmapVarName: ${spanVarName}, setting sub key: ${spanSubKeyName}",
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
                        ErrLogger.ImageActionErrType.I_VAR,
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
                bitmapVarKeyList: List<String>?,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val varMarkRegex = Regex("#[{][a-zA-Z0-9_]+[}]")
                val regexStrTemplate = "(#[{]%s[}])"
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lisNotBeforeDefinitionInReturnErr.txt").absolutePath,
//                    listOf(
//                        "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                    ).joinToString("\n\n") + "\n\n==========\n\n"
//                )
                settingKeyToVarNameList.forEachIndexed { index, settingKeyToVarName ->
                    val settingKey = settingKeyToVarName.first
                    if(
                        settingKey != imageReturnKey
                    ) return@forEachIndexed
                    val alreadyVarNameList = let {
                        settingKeyToVarNameList.filterIndexed { innerIndex, innerSettingKeyToVarName ->
                            innerSettingKeyToVarName.first != imageReturnKey
                                    && innerIndex < index
                        }.map { innerSettingKeyToVarName ->
                            innerSettingKeyToVarName.second
                        } + (bitmapVarKeyList ?: emptyList())
                    }.sorted().distinct()
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
                    val spanImageReturnKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            imageReturnKey
                        )
                    val spanReturnVarName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            returnVarName
                        )
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.ImageActionErrType.I_VAR,
                            "Not before definition ${spanImageReturnKey} var: ${spanReturnVarName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun makeReturnKeyToBitmapVarMarkList(
                keyToSubKeyConList: List<Pair<String, String>>,
            ): List<Pair<String, String>> {
                val defaultReturnPair = String() to String()
                val subKeySeparator = ImageActionKeyManager.subKeySepartor
                return keyToSubKeyConList.map {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        settingKey != imageReturnKey
                    ) return@map defaultReturnPair
                    val bitmapVarMark = keyToSubKeyCon.second
                        .split(subKeySeparator)
                        .firstOrNull()?.let {
                            QuoteTool.trimBothEdgeQuote(it)
                        } ?: return@map defaultReturnPair
                    settingKey to bitmapVarMark
                }.filter {
                    it.first.isNotEmpty()
                }
            }
        }

        private object VarErrManager {

            private val mainKeySeparator = ImageActionKeyManager.mainKeySeparator
            private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
            private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
            private val imageReturnKey =
                ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN.key

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
                        settingKeyToBitmapKey ->
                    settingKeyToBitmapKey.first !=
                            imageReturnKey
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
                        ErrLogger.ImageActionErrType.I_VAR,
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
                            imageReturnKey
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
                            ErrLogger.ImageActionErrType.I_VAR,
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
                returnBitmap: Bitmap?,
                imageAcSubKey: ImageActionKeyManager.ImageSubKey,
                keyToSubKeyConWhere: String,
            ): Boolean {
                if(
                    returnBitmap != null
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
                        imageAcSubKey.key
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.ImageActionErrType.I_VAR,
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
                val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                                ErrLogger.ImageActionErrType.I_VAR,
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

                val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
                val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
                val subKeySeparator =
                    ImageActionKeyManager.subKeySepartor

                keyToSubKeyConList.forEach {
                        keyToSubKeyCon ->
                    val settingKey = keyToSubKeyCon.first
                    if(
                        settingKey == imageReturnKey
                    ) return@forEach
                    val asyncVarNameEntry =
                        keyToSubKeyCon.second
                            .split(subKeySeparator)
                            .firstOrNull()
                            ?: return@forEach
                    if(
                        !asyncVarNameEntry.startsWith(asyncPrefix)
                    ) return@forEach
                    val asyncVarMark = "#{${asyncVarNameEntry}}"
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
                                val awaitVarNameList = ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
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
                        runBlocking {
                            ErrLogger.sendErrLog(
                                context,
                                ErrLogger.ImageActionErrType.I_VAR,
                                "Not ${spanAwaitKeyName} async var: ${spanAwaitAsyncVarName}, settingKeyName: ${spanSettingKeyName}",
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
                                ErrLogger.ImageActionErrType.I_VAR,
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
                            ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
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
                            ErrLogger.ImageActionErrType.I_VAR,
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
                val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                            ErrLogger.ImageActionErrType.I_VAR,
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
                val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                            ErrLogger.ImageActionErrType.I_VAR,
                            "Duplicate ${spanAwaitKeyName} var name: ${spanAwaitVarNameListCon}: settingKeyName: ${spanSettingKeyName}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
            }

            fun isNotExistReturnOrFunc(
                context: Context?,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val imageVarMainKey = ImageActionKeyManager.ImageActionsKey.IMAGE_VAR.key
                val funcKey = ImageActionKeyManager.ImageSubKey.FUNC.key
                val onReturnKey = ImageActionKeyManager.ImageSubKey.ON_RETURN.key
                val funcKeyRegex = Regex("\\?${funcKey}=")
                val returnKeyRegex = Regex("\\?${onReturnKey}=")
                keyToSubKeyConList.forEach {
                    settingKeyToSubKeyCon ->
                    val settingKeyName = settingKeyToSubKeyCon.first
                    if(
                        settingKeyName != imageVarMainKey
                    ) return@forEach
                    val subKeyCon = settingKeyToSubKeyCon.second
                    val isContainFuncOrReturn = funcKeyRegex.containsMatchIn(subKeyCon)
                            || returnKeyRegex.containsMatchIn(subKeyCon)
                    if(isContainFuncOrReturn) return@forEach
                    val spanSettingKeyName =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            settingKeyName
                        )
                    val spanSubKeyCon =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            subKeyCon
                        )
                    val spanImageVarMainKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            imageVarMainKey
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
                    runBlocking {
                        ErrLogger.sendErrLog(
                            context,
                            ErrLogger.ImageActionErrType.I_VAR,
                            "${spanFuncKey} or ${spanOnReturnKey} sub key must use in ${spanImageVarMainKey} settingKey: subKeyCon: ${spanSubKeyCon}",
                            keyToSubKeyConWhere,
                        )
                    }
                    return true
                }
                return false
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
                    val varNameWithSharp = "#{${runVarName}}"
                    val isContain = keyToSubKeyListCon.contains(varNameWithSharp)
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
                            ErrLogger.ImageActionErrType.I_VAR,
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


            fun isBlankIVarOrIAcVarErr(
                context: Context?,
                settingKeyToPrivateVarNameList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val settingKeyToBlankVarNamePair = settingKeyToPrivateVarNameList.filter {
                        settingKeyToPrivateBitmapVarKey ->
                    settingKeyToPrivateBitmapVarKey.first != imageReturnKey
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
                        ErrLogger.ImageActionErrType.I_VAR,
                        "${spanBlankSettingKeyName} must not blank",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }

            fun isShadowTopLevelVarErr(
                context: Context?,
                settingKeyToNoRunVarNameList: List<Pair<String, String>>,
                topLevelBitmapStrKeyList: List<String>?,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val shadowSettingKeyToNoRunVarName = settingKeyToNoRunVarNameList.filter {
                        settingKeyToNoRunVarName ->
                    settingKeyToNoRunVarName.first != imageReturnKey
                }.firstOrNull {
                        settingKeyToNoRunVarName ->
                    topLevelBitmapStrKeyList?.contains(
                        settingKeyToNoRunVarName.second
                    ) == true
                }
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
                        ErrLogger.ImageActionErrType.I_VAR,
                        "shaddow top level var: ${spanShadowTopLevelVarKey} setting key ${spanSettingKey}",
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

            fun isNotDefinitionVarErr(
                context: Context?,
                settingKeyToNoRunVarNameList: List<Pair<String, String>>,
                keyToSubKeyConList: List<Pair<String, String>>,
                bitmapVarKeyList: List<String>?,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val regexStrTemplate = "(#[{]%s[}])"
                val bitmapKeyList = let {
                    val bitmapKeyListInCode = settingKeyToNoRunVarNameList.filter {
                            settingKeyToNoRunVarName ->
                        settingKeyToNoRunVarName.first !=
                                imageReturnKey
                    }.map {
                        it.second
                    }
                    val plusBitmapKeyList = bitmapVarKeyList?.filter {
                        !bitmapKeyListInCode.contains(it)
                    } ?: emptyList()
                    bitmapKeyListInCode +
                            plusBitmapKeyList +
                            listOf(ImageActionKeyManager.BitmapVar.itPronoun)
                }
                val settingKeyListConRegex = let {
                    bitmapKeyList.map {
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
                val findVarMarkRegex = Regex("#[{][a-zA-Z0-9_]+[}]")
                val leaveVarMark =
                    findVarMarkRegex.find(keyToSubKeyListConWithRemoveVar)
                        ?.value
                        ?: return false
                val spanLeaveVarMark =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        leaveVarMark
                    )
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.ImageActionErrType.I_VAR,
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

            fun isNotReplaceStringVarErr(
                context: Context?,
                subKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                execIsNotReplaceStringVarErr(
                    context,
                    subKeyListCon,
                    keyToSubKeyConWhere,
                ).let {
                        isNotReplaceVar ->
                    if(isNotReplaceVar) return true
                }
                return false
            }

            private fun execIsNotReplaceStringVarErr(
                context: Context?,
                subKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val dolVarMarkRegex = Regex("[$][{][a-zA-Z0-9_]+[}]")
                val noReplaceVar = dolVarMarkRegex.find(subKeyListCon)?.value
                if(
                    noReplaceVar.isNullOrEmpty()
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
                        ErrLogger.ImageActionErrType.I_VAR,
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
                    settingKeyToPrivateVarName.first != imageReturnKey
                }.forEach {
                        settingKeyToVarName ->
                    val varName = settingKeyToVarName.second
                    if(
                        varName.startsWith(escapeRunPrefix)
                    ) return@forEach
                    val varNameWithSharp = "#{${varName}}"
                    val isContain =
                        keyToSubKeyListCon.contains(varNameWithSharp)
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
                            ErrLogger.ImageActionErrType.I_VAR,
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
                val subKeySeparator = ImageActionKeyManager.subKeySepartor
                val settingKeyList =
                    ImageActionKeyManager.ImageActionsKey.entries.map {
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
                val subKeySeparator = ImageActionKeyManager.subKeySepartor
                val settingKeyList =
                    ImageActionKeyManager.ImageActionsKey.entries.map {
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
                val subKeySeparator = ImageActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    ImageActionKeyManager.ImageActionsKey.entries.map {
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
                val subKeySeparator = ImageActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    ImageActionKeyManager.ImageActionsKey.entries.map {
                        it.key
                    }
                val asyncVarPrefixList = listOf(
                    asyncPrefix,
                    runAsyncPrefix,
                )
                val awaitSubKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                            ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
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
                val subKeySeparator = ImageActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    ImageActionKeyManager.ImageActionsKey.entries.map {
                        it.key
                    }
                val asyncVarPrefixList = listOf(
                    asyncPrefix,
                    runAsyncPrefix,
                )
                val awaitSubKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                               ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
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
                val subKeySeparator = ImageActionKeyManager.subKeySepartor
                val imageActionKeyList =
                    ImageActionKeyManager.ImageActionsKey.entries.map {
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

            private val iIfKeyName = ImageActionKeyManager.ImageSubKey.I_IF.key
            private val valueSeparator = ImageActionKeyManager.valueSeparator
            private val imageActionVarKey = ImageActionKeyManager.ImageActionsKey.IMAGE_ACTION_VAR.key
            private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
            private val asyncRunPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
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
                loopKeyToAsyncDeferredVarNameBitmapMap: LoopKeyToAsyncDeferredVarNameBitmapMap?,
                exitSignalManager: ExitSignalManager,
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
                                    ImageActionKeyManager. BreakSignal?
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
                            ErrLogger.sendErrLog(
                                context,
                                ErrLogger.ImageActionErrType.AWAIT,
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
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.ImageActionErrType.I_IF,
                        errType.errMessage,
                        keyToSubKeyConWhere
                    )
                    exitSignalManager.setExit()
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
                val importedKeyToSubKeyConList = KeyToSubKeyMapListMaker.make(importSrcCon)
                val settingKeyToVarNameList = makeSettingKeyToBitmapVarKeyListForReturn(
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
                            importRepMap,
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
                    val isGlobalVarNameNotLastErrWithoutRunPrefixJob = async {
                        ImportErrManager.isGlobalVarNameNotLastErrWithoutRunPrefix(
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

        private class ImageVarExecutor {

            private val iIfKeyName = ImageActionKeyManager.ImageSubKey.I_IF.key
            private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
            private val asyncRunPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
            private var itPronounBitmapToBreakSignal: Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >? = null
            private var isNext = true
            private val valueSeparator = ImageActionKeyManager.valueSeparator
            private val itPronoun = ImageActionKeyManager.BitmapVar.itPronoun

            suspend fun exec(
                fragment: Fragment,
//                settingVarKey: String,
//                subKeyCon: String,
                mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
                busyboxExecutor: BusyboxExecutor?,
                editConstraintListAdapterArg: EditConstraintListAdapter?,
                topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
                curMapLoopKey: String,
                loopKeyToAsyncDeferredVarNameBitmapMap: LoopKeyToAsyncDeferredVarNameBitmapMap?,
                privateLoopKeyVarNameBitmapMapClass: PrivateLoopKeyVarNameBitmapMap,
                loopKeyToVarNameBitmapMapClass: LoopKeyToVarNameBitmapMap,
                importedVarNameToBitmapMap: Map<String, Bitmap?>?,
                exitSignalManager: ExitSignalManager,
                settingVarName: String,
                renewalVarName: String?,
                keyToSubKeyConWhere: String,
            ): Pair<Pair<String, Bitmap?>, ImageActionKeyManager.BreakSignal?>? {
                val context = fragment.context
                mainSubKeyPairList.forEach {
                        mainSubKeyPair ->
                    val mainSubKey = mainSubKeyPair.first
                    val mainSubKeyMap = mainSubKeyPair.second
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
                    val privateSubKeyClass = ImageActionKeyManager.ImageSubKey.entries.firstOrNull {
                        it.key == mainSubKey
                    } ?: return@forEach
                    when(privateSubKeyClass) {
                        ImageActionKeyManager.ImageSubKey.IMAGE_VAR,
                        ImageActionKeyManager.ImageSubKey.IMAGE_RETURN,
                        ImageActionKeyManager.ImageSubKey.ARGS -> {}
                        ImageActionKeyManager.ImageSubKey.AWAIT -> {
                            if(!isNext){
                                isNext = true
                                return@forEach
                            }
                            val awaitVarNameList = mainSubKeyMap.get(
                                privateSubKeyClass.key
                            )?.let {
                                ImageActionKeyManager.AwaitManager.getAwaitVarNameList(it)
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
                                var deferredVarNameToBitmapAndBreakSignal: Deferred<
                                        Pair<
                                                Pair<String, Bitmap?>,
                                                ImageActionKeyManager. BreakSignal?
                                                >?
                                        >? = null
                                for (i in 1..awaitWaitTimes) {
                                    deferredVarNameToBitmapAndBreakSignal =
                                        loopKeyToAsyncDeferredVarNameBitmapMap
                                            ?.getAsyncVarNameToBitmapAndExitSignal(
                                                curMapLoopKey
                                            )?.get(awaitVarName)
//                                    FileSystems.updateFile(
//                                        File(
//                                            UsePath.cmdclickDefaultIDebugAppDirPath,
//                                            "image_await_${settingVarName}.txt"
//                                        ).absolutePath,
//                                        listOf(
//                                            "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                            "settingVarName: $settingVarName",
//                                            "curMapLoopKey: ${curMapLoopKey}",
//                                            "awaitVarNameList: ${awaitVarNameList}",
//                                            "awaitVarName: ${awaitVarName}",
//                                            "loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
//                                                curMapLoopKey
//                                            )?.map {
//                                                it.key
//                                            }?.joinToString("\n")}"
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                                        ).joinToString("\n")  + "\n\n========\n\n"
//                                    )
                                    if (
                                        deferredVarNameToBitmapAndBreakSignal != null
                                    ) {
                                        break
                                    }
                                    delay(100)
                                }
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
                                    val spanIVarKeyName =
                                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                            CheckTool.ligthBlue,
                                            ImageActionKeyManager.ImageActionsKey.IMAGE_VAR.key
                                        )
                                    runBlocking {
                                        ErrLogger.sendErrLog(
                                            context,
                                            ErrLogger.ImageActionErrType.AWAIT,
                                            "await var name not exist: ${spanAwaitVarName}, setting key: ${spanIVarKeyName}",
                                            keyToSubKeyConWhere,
                                        )
                                    }
                                    return@awaitVarNameList
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
                                    ?: return@awaitVarNameList
                                if(
                                    varName.startsWith(asyncRunPrefix)
                                ) return@awaitVarNameList
                                val bitmap = varNameToBitmap.second
                                privateLoopKeyVarNameBitmapMapClass.put(
                                    curMapLoopKey,
                                    varName,
                                    bitmap
                                )
                            }
                        }
                        ImageActionKeyManager.ImageSubKey.ON_RETURN -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val varNameToBitmapMap =
                                makeValueToBitmapMap(
                                    curMapLoopKey,
                                    topVarNameToVarNameBitmapMap,
                                    importedVarNameToBitmapMap,
                                    loopKeyToVarNameBitmapMapClass,
                                    privateLoopKeyVarNameBitmapMapClass,
                                    null,
                                    mapOf(itPronoun to itPronounBitmapToBreakSignal?.first),
                                )
                            val curIVarKey =
                                mainSubKeyMap.get(mainSubKey)?.let {
                                    ImageActionKeyManager.BitmapVar.convertBitmapKey(it)
                                }
                            val returnBitmap = when(true) {
//                                (curIVarKey == itPronoun) -> itPronounBitmapToExitSignal?.first
                                curIVarKey.isNullOrEmpty() -> null
                                else -> varNameToBitmapMap.get(curIVarKey)
//                                privateLoopKeyVarNameBitmapMapClass.getAsyncVarNameToBitmap(
//                                    curMapLoopKey
//                                )?.get(curIVarKey)
//                                    ?: loopKeyToVarNameBitmapMapClass.getAsyncVarNameToBitmap(
//                                        curMapLoopKey
//                                    )?.get(curIVarKey)
                            }
                            VarErrManager.isGlobalVarNullResultErr(
                                context,
                                renewalVarName ?: settingVarName,
                                returnBitmap,
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
                                    returnBitmap
                                ),
                                null,
                            )
//                                    (mainSubKeyMap.get(mainSubKey) ?: String())
                        }
                        ImageActionKeyManager.ImageSubKey.FUNC -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val funcTypeDotMethod = mainSubKeyMap.get(mainSubKey)
                                ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMap.get(
                                    ImageActionKeyManager.ImageSubKey.ARGS.key
                                ),
                                valueSeparator
                            ).filter {
                                it.first.isNotEmpty()
                            }
                            val varNameToBitmapMap =
                                makeValueToBitmapMap(
                                    curMapLoopKey,
                                    topVarNameToVarNameBitmapMap,
                                    importedVarNameToBitmapMap,
                                    loopKeyToVarNameBitmapMapClass,
                                    privateLoopKeyVarNameBitmapMapClass,
                                    null,
                                    mapOf(itPronoun to itPronounBitmapToBreakSignal?.first),
                                )
//                                (importedVarNameToBitmapMap ?: emptyMap()) +
//                                (topVarNameToVarNameBitmapMap ?: emptyMap()) +
//                                        (privateLoopKeyVarNameBitmapMapClass
//                                            .getAsyncVarNameToBitmap(
//                                                curMapLoopKey
//                                            )?.toMap() ?: emptyMap()) +
//                                        (loopKeyToVarNameBitmapMapClass
//                                            .getAsyncVarNameToBitmap(
//                                                curMapLoopKey
//                                            )?.toMap()
//                                            ?: emptyMap()) +
//                                        mapOf(itPronoun to itPronounBitmapToExitSignal?.first)
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultIDebugAppDirPath,
//                                    "image_func_${settingVarName}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "settingVarName: $settingVarName",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "varNameToBitmapMap: ${varNameToBitmapMap}",
//                                    "loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
//                                        curMapLoopKey
//                                    )?.map {
//                                        it.key
//                                    }?.joinToString("\n")}"
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                                ).joinToString("\n")  + "\n\n========\n\n"
//                            )
                            val resultBitmapToExitMacroAndCheckErr = ImageFuncManager.handle(
                                fragment,
                                funcTypeDotMethod,
                                argsPairList,
                                busyboxExecutor,
                                editConstraintListAdapterArg,
                                varNameToBitmapMap
//                                        (privateLoopKeyVarNameBitmapMapClass.getAsyncVarNameToBitmap(curMapLoopKey) ?: emptyMap<>()),
                            )
                            val checkErr = resultBitmapToExitMacroAndCheckErr?.second
                            if(checkErr != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.ImageActionErrType.FUNC,
                                        checkErr.errMessage,
                                        keyToSubKeyConWhere,
                                    )
                                }
                                itPronounBitmapToBreakSignal = null
                                isNext = false
                                return@forEach
                            }
                            val resultBitmapToExitMacro = resultBitmapToExitMacroAndCheckErr?.first
                            VarErrManager.isGlobalVarNullResultErr(
                                context,
                                renewalVarName ?: settingVarName,
                                resultBitmapToExitMacro?.first,
                                privateSubKeyClass,
                                keyToSubKeyConWhere,
                            ).let {
                                    isGlobalVarFuncNullResultErr ->
                                if(
                                    isGlobalVarFuncNullResultErr
                                ) return null
                            }
                            itPronounBitmapToBreakSignal = resultBitmapToExitMacro

                        }
                        ImageActionKeyManager.ImageSubKey.I_IF -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val judgeTargetStr = mainSubKeyMap.get(mainSubKey)
                                ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMap.get(
                                    ImageActionKeyManager.ImageSubKey.ARGS.key
                                ),
                                valueSeparator
                            ).filter {
                                it.first.isNotEmpty()
                            }
                            val isImportToErrType = SettingIfManager.handle(
                                iIfKeyName,
                                judgeTargetStr,
                                argsPairList
                            )
                            val errType = isImportToErrType.second
                            if(errType != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.ImageActionErrType.I_IF,
                                        errType.errMessage,
                                        keyToSubKeyConWhere
                                    )
                                }
                                exitSignalManager.setExit()
                                return Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                            }
                            val isImport = isImportToErrType.first ?: false
                            isNext = isImport
                        }
                    }
                    if(privateSubKeyClass != ImageActionKeyManager.ImageSubKey.I_IF){
                        isNext = true
                    }
                }
                val isNoImageVar =
                    settingVarName.startsWith(escapeRunPrefix)
                            || settingVarName.startsWith(asyncRunPrefix)
                val isEscape =
                    isNoImageVar
                            && itPronounBitmapToBreakSignal?.second != ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                return when(isEscape){
                    true -> null
                    else -> Pair(
                        Pair(
                            settingVarName,
                            itPronounBitmapToBreakSignal?.first,
                            ),
                        itPronounBitmapToBreakSignal?.second,
                        )
                }
            }
//            private fun replaceItPronoun(con: String): String {
//                return con.replace(itReplaceVarStr, itPronounValue)
//            }
        }
    }

    private class ImageReturnExecutor {

        private var isNext = true
        private val valueSeparator = ImageActionKeyManager.valueSeparator
        private val outputReturnSignal =
            ImageActionKeyManager.ImageReturnManager.OutputReturn.OUTPUT_RETURN
        private val returnSignal = ImageActionKeyManager.BreakSignal.RETURN_SIGNAL
        private val iIf = ImageActionKeyManager.ImageReturnManager.ImageReturnKey.I_IF

        suspend fun exec(
            fragment: Fragment,
            exitSignalManager: ExitSignalManager,
            mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
            returnBitmap: Bitmap?,
            keyToSubKeyConWhere: String,
        ): Pair<
                Pair<
                        ImageActionKeyManager.ImageReturnManager.OutputReturn,
                        Bitmap?
                        >?,
                ImageActionKeyManager.BreakSignal?
                >? {
            val context = fragment.context
            val isIIf = mainSubKeyPairList.any {
                val mainSubKey = it.first
                mainSubKey == iIf.key
            }
            val returnKeyValueStrPair = Pair(
                outputReturnSignal,
                returnBitmap,
            )
            if(!isIIf){
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
                val privateSubKeyClass = ImageActionKeyManager.ImageReturnManager.ImageReturnKey.entries.firstOrNull {
                    it.key == mainSubKey
                } ?: return@forEach
                when(privateSubKeyClass) {
                    ImageActionKeyManager.ImageReturnManager.ImageReturnKey.ARGS -> {}
                    ImageActionKeyManager.ImageReturnManager.ImageReturnKey.I_IF -> {
                        if(!isNext) {
                            isNext = true
                            return@forEach
                        }
                        val judgeTargetStr = mainSubKeyMapSrc.get(mainSubKey)
                            ?: return@forEach
                        val argsPairList = CmdClickMap.createMap(
                            mainSubKeyMapSrc.get(
                                ImageActionKeyManager.ImageSubKey.ARGS.key
                            ),
                            valueSeparator
                        ).filter {
                            it.first.isNotEmpty()
                        }
                        val isReturnToErrType = SettingIfManager.handle(
                            iIf.key,
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
                                    ErrLogger.ImageActionErrType.I_IF,
                                    errType.errMessage,
                                    keyToSubKeyConWhere
                                )
                            }
                            isNext = false
                            exitSignalManager.setExit()
                            return null to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                        }
                        val isReturnBool = isReturnToErrType.first ?: false
                        if(isReturnBool){
                            return returnKeyValueStrPair to returnSignal
                        }
                        isNext = true
                    }
                }
                if(privateSubKeyClass != iIf){
                    isNext = true
                }
            }
            return Pair(
                null,
                null
            )
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
    private val imageActionsKeyPlusList =
        ImageActionKeyManager.ImageActionsKey.entries.map {
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
            imageActionsKeyPlusList.contains(mainKeyName)
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
            imageActionsKeyPlusList.contains(mainKey)
        }.map {
            val mainKey = it.first
            val subKeyAfterStr = it.second
            mainKey to subKeyAfterStr
        }
    }
}
