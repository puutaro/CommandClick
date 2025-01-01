package com.puutaro.commandclick.proccess.edit.image_action

import android.content.Context
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageFuncManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageIfManager
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.lang.ref.WeakReference

class ImageActionManager {
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
                ErrLogger.AlreadyErr.init()
//                mutex.withLock {
//                    beforeActionImportMap.clear()
//                }
            }
        }

        private object ErrLogger {

            private val durationSec = 5

            enum class ImageActionErrType {
                I_VAR,
                I_AC_VAR,
                FUNC,
                S_IF,
            }

            object AlreadyErr {
                private var isAlreadyErr = false
                private val mutex = Mutex()

                suspend fun init(){
                    mutex.withLock {
                        isAlreadyErr = false
                    }
                }

                suspend fun enable() {
                    mutex.withLock {
                        isAlreadyErr = true
                    }
                }

                suspend fun get(): Boolean {
                    mutex.withLock {
                        return isAlreadyErr
                    }
                }
            }

//            object LogDatetime {
//                private var beforeOutputTime = LocalDateTime.parse("2020-02-15T21:30:50")
//                private val mutex = Mutex()
//
//                suspend fun update(
//                    datetime: LocalDateTime
//                ) {
//                    mutex.withLock {
//                        beforeOutputTime = datetime
//                    }
//                }
//
//                suspend fun get(): LocalDateTime {
//                    mutex.withLock {
//                        return beforeOutputTime
//                    }
//                }
//            }

            suspend fun sendErrLog(
                context: Context?,
                errType: ImageActionErrType,
                errMessage: String,
                keyToSubKeyConWhere: String,
            ) {
//                val currentDatetime =
//                    withContext(Dispatchers.IO) {
//                        LocalDateTime.now()
//                    }
//                val diffSec = withContext(Dispatchers.IO) {
//                    val beforeOutputTime = LogDatetime.get()
//                    LocalDatetimeTool.getDurationSec(
//                        beforeOutputTime,
//                        currentDatetime
//                    )
//                }
//                if(diffSec < durationSec) return
                if(AlreadyErr.get()) return
                withContext(Dispatchers.IO) {
//                    LogDatetime.update(
//                        currentDatetime
//                    )
                    AlreadyErr.enable()
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
        )
        imageActionExecutor.makeResultLoopKeyToVarNameValueMap(
            editConstraintListAdapterArg,
            keyToSubKeyConList,
            ImageActionExecutor.mapRoopKeyUnit,
            keyToSubKeyConWhere,
            null,
            null,
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
                                    ImageActionKeyManager.ExitSignal?
                                    >?
                            >
                    >
                >()
        private val asyncLoopKeyToVarNameBitmapMapMutex = Mutex()
        suspend fun getAsyncVarNameToBitmapAndExitSignal(loopKey: String):  MutableMap <
                String,
                Deferred<
                        Pair<
                                Pair<String, Bitmap?>,
                                ImageActionKeyManager.ExitSignal?
                                >?
                        >
                >? {
            return asyncLoopKeyToVarNameBitmapMapMutex.withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
                    loopKey
                )
            }
        }

        suspend fun put(
            loopKey: String,
            varName: String,
            deferredVarNameBitmapMapAndExitSignal: Deferred<
                    Pair<
                            Pair<String, Bitmap?>,
                            ImageActionKeyManager.ExitSignal?
                            >?
                    >
        ){
            asyncLoopKeyToVarNameBitmapMapMutex.withLock {
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
                                deferredVarNameBitmapMapAndExitSignal
                            )
                        }

                        else -> loopKeyToAsyncDeferredVarNameBitmapMap.put(
                            loopKey,
                            mutableMapOf(varName to deferredVarNameBitmapMapAndExitSignal)
                        )
                    }
                }
            }
        }

        suspend fun clearVarName(
            loopKey: String,
            varName: String,
        ) {
            asyncLoopKeyToVarNameBitmapMapMutex.withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.get(
                    loopKey
                )?.remove(varName)
            }
        }

        suspend fun clearAsyncVarNameToBitmapAndExitSignal(loopKey: String) {
            asyncLoopKeyToVarNameBitmapMapMutex.withLock {
                loopKeyToAsyncDeferredVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    private class PrivateLoopKeyVarNameBitmapMap {
        private val privateLoopKeyVarNameBitmapMap =
            mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val privateLoopKeyVarNameBitmapMapMutex = Mutex()
        suspend fun getAsyncVarNameToBitmap(
            loopKey: String
        ): MutableMap<String, Bitmap?>? {
            return privateLoopKeyVarNameBitmapMapMutex.withLock {
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
            privateLoopKeyVarNameBitmapMapMutex.withLock {
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
            return privateLoopKeyVarNameBitmapMapMutex.withLock {
                privateLoopKeyVarNameBitmapMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
            privateLoopKeyVarNameBitmapMapMutex.withLock {
                privateLoopKeyVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }

    private class LoopKeyToVarNameBitmapMap {
        private val loopKeyToVarNameBitmapMap =
            mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val loopKeyToVarNameBitmapMapMutex = Mutex()
        suspend fun getAsyncVarNameToBitmap(
            loopKey: String
        ): MutableMap<String, Bitmap?>? {
            return loopKeyToVarNameBitmapMapMutex.withLock {
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
            loopKeyToVarNameBitmapMapMutex.withLock {
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
            return loopKeyToVarNameBitmapMapMutex.withLock {
                loopKeyToVarNameBitmapMap.get(
                    loopKey
                )?.clear()
            }
        }

        suspend fun clearPrivateLoopKeyVarNameBitmapMapMutex(loopKey: String){
            loopKeyToVarNameBitmapMapMutex.withLock {
                loopKeyToVarNameBitmapMap.remove(
                    loopKey
                )
            }
        }
    }


    private class ImageActionExecutor(
        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: Map<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
    ) {

        private val loopKeyToVarNameBitmapMap = LoopKeyToVarNameBitmapMap()
//        mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val privateLoopKeyVarNameBitmapMap = PrivateLoopKeyVarNameBitmapMap()
        private val loopKeyToAsyncDeferredVarNameBitmapMap = LoopKeyToAsyncDeferredVarNameBitmapMap()
        private var exitSignal = false
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
            editConstraintListAdapterArg: EditConstraintListAdapter?,
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            keyToSubKeyConWhere: String,
            topAcIVarName: String?,
            importedVarNameToBitmapMap: Map<String, Bitmap?>?
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
                val isNotReplaceVarErrJob = async {
                    VarErrManager.isNotReplaceVarErr(
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
                val isNotExistReturnOrFuncJob = async {
                    VarErrManager.isNotExistReturnOrFunc(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                isAwaitNotAsyncVarErrJob.await()
                        || isAwaitDuplicateAsyncVarErrJob.await()
                        || isAwaitNotDefiniteVarErrJob.await()
                        || isNotAwaitAsyncVarErrOrAwaitInAsyncVarErrJob.await()
                        || isBlankIVarOrIAcVarErrJob.await()
                        || isNotUseVarErrJob.await()
                        || isNotReplaceVarErrJob.await()
                        || isRunPrefixUseErrJob.await()
                        || isSameVarNameErrJob.await()
                        || isNotExistReturnOrFuncJob.await()
            }
            if(isErr) return
            keyToSubKeyConList.forEach { keyToSubKeyConSrc ->
                if (
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
                                keyToSubKeyCon,
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
                        if(isAsync){
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.IO) {
                                    val job = async {
                                        makeResultLoopKeyToVarNameValueMap(
                                            editConstraintListAdapterArg,
                                            importedKeyToSubKeyConList,
                                            addLoopKey(curMapLoopKey),
                                            "${importPath} by imported",
                                            acIVarName,
                                            curImportedVarNameToBitmapMap,
                                        )
                                        null
                                    }
                                    loopKeyToAsyncDeferredVarNameBitmapMap.put(
                                        curMapLoopKey,
                                        acIVarName,
                                        job
                                    )
                                }
                            }
                            return@forEach
                        }
                        makeResultLoopKeyToVarNameValueMap(
                            editConstraintListAdapterArg,
                            importedKeyToSubKeyConList,
                            addLoopKey(curMapLoopKey),
                            "${importPath} by imported",
                            acIVarName,
                            importedVarNameToBitmapMap
                        )
                        FileSystems.updateFile(
                            File(
                                UsePath.cmdclickDefaultIDebugAppDirPath,
                                "image_acImport00_${acIVarName}.txt"
                            ).absolutePath,
                            listOf(
                                "acIVarName:${acIVarName}",
                                "topAcIVarName:${topAcIVarName}",
                                "curMapLoopKey: ${curMapLoopKey}",
                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
                                    curMapLoopKey
                                )?.map {
                                    it.key
                                }?.joinToString("\n")}",
                            ).joinToString("\n")  + "\n\n========\n\n"
                        )
                        if (
                            topAcIVarName.isNullOrEmpty()
                        ) return@forEach
                        if(
                            topAcIVarName.startsWith(escapeRunPrefix)
                        ) return@forEach
                        val proposalRenewalVarNameSrcInnnerMapBitmap =
                            privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
                                addLoopKey(curMapLoopKey)
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
                            addLoopKey(curMapLoopKey)
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
                        FileSystems.updateFile(
                            File(
                                UsePath.cmdclickDefaultIDebugAppDirPath,
                                "image_acImport_${acIVarName}.txt"
                            ).absolutePath,
                            listOf(
                                "acIVarName:${acIVarName}",
                                "topAcIVarName:${topAcIVarName}",
                                "curMapLoopKey: ${curMapLoopKey}",
                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
                                    curMapLoopKey
                                )?.map {
                                    it.key
                                }?.joinToString("\n")}",
                                "removedLoopKey:${removedLoopKey}",
                                "removed loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
                                    removedLoopKey
                                )?.map {
                                    it.key
                                }?.joinToString("\n")}",
//                    "varNameToBitmap: ${varNameToBitmap.first}",
//                    "bitmapWidth: ${varNameToBitmap.second?.width}",
//                    "bitmapHeight: ${varNameToBitmap.second?.height}",
                            ).joinToString("\n")  + "\n\n========\n\n"
                        )
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
                            CoroutineScope(Dispatchers.IO).launch {
                                val job = async {
                                    ImageVarExecutor().exec(
                                        fragment,
                                        mainSubKeyPairList,
                                        busyboxExecutor,
                                        editConstraintListAdapterArg,
                                        curMapLoopKey,
                                        loopKeyToAsyncDeferredVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                        loopKeyToVarNameBitmapMap,
                                        importedVarNameToBitmapMap,
                                        settingVarName,
                                        topAcIVarName,
                                        keyToSubKeyConWhere,
                                    )
                                }
//                                val removedLoopKey = removeLoopKey(curMapLoopKey)
                                loopKeyToAsyncDeferredVarNameBitmapMap.put(
                                    curMapLoopKey,
                                    settingVarName,
                                    job
                                )
                                val removedLoopKey = removeLoopKey(curMapLoopKey)
                                FileSystems.updateFile(
                                    File(
                                        UsePath.cmdclickDefaultIDebugAppDirPath,
                                        "image_async_00_${settingVarName}.txt"
                                    ).absolutePath,
                                    listOf(
                                        "keyToSubKeyConList: ${keyToSubKeyConList}",
                                        "imageVarKey: ${imageVarKey}",
                                        "mainSubKeyPairList: ${mainSubKeyPairList}",
                                        "settingVarName: $settingVarName",
                                        "isAsync: ${isAsync}",
                                        "curMapLoopKey: ${curMapLoopKey}",
                                        "removedLoopKey: ${removedLoopKey}",
                                        "loopKeyToAsyncDeferredVarNameBitmapMap: ${
                                            loopKeyToAsyncDeferredVarNameBitmapMap.getAsyncVarNameToBitmapAndExitSignal(
                                                curMapLoopKey
                                            )?.map {
                                                it.key
                                            }?.joinToString("\n")
                                        }",
                                        "privateLoopKeyVarNameBitmapMap: ${
                                            privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
                                                curMapLoopKey
                                            )?.map {
                                                it.key
                                            }?.joinToString("\n")
                                        }",
                                        "topAcIVarName: ${topAcIVarName}",
                                    ).joinToString("\n")
                                )
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
                                    job
                                )
                                FileSystems.updateFile(
                                    File(
                                        UsePath.cmdclickDefaultIDebugAppDirPath,
                                        "image_async_${settingVarName}.txt"
                                    ).absolutePath,
                                    listOf(
                                        "keyToSubKeyConList: ${keyToSubKeyConList}",
                                        "imageVarKey: ${imageVarKey}",
                                        "mainSubKeyPairList: ${mainSubKeyPairList}",
                                        "settingVarName: $settingVarName",
                                        "isAsync: ${isAsync}",
                                        "curMapLoopKey: ${curMapLoopKey}",
                                        "removedLoopKey: ${removedLoopKey}",
                                        "loopKeyToAsyncDeferredVarNameBitmapMap: ${
                                            loopKeyToAsyncDeferredVarNameBitmapMap.getAsyncVarNameToBitmapAndExitSignal(
                                                curMapLoopKey
                                            )?.map {
                                                it.key
                                            }?.joinToString("\n")
                                        }",
                                        "privateLoopKeyVarNameBitmapMap: ${
                                            privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
                                                curMapLoopKey
                                            )?.map {
                                                it.key
                                            }?.joinToString("\n")
                                        }",
                                        "loopKeyToVarNameBitmapMap: ${
                                            loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
                                                curMapLoopKey
                                            )?.map {
                                                it.key
                                            }?.joinToString("\n")
                                        }",
                                        "loopKeyToVarNameBitmapMap: ${
                                            loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
                                                removedLoopKey
                                            )?.map {
                                                it.key
                                            }?.joinToString("\n")
                                        }"
                                    ).joinToString("\n")
                                )
                            }
                            return@forEach
                        }
                        ImageVarExecutor().exec(
                            fragment,
                            mainSubKeyPairList,
                            busyboxExecutor,
                            editConstraintListAdapterArg,
                            curMapLoopKey,
                            loopKeyToAsyncDeferredVarNameBitmapMap,
                            privateLoopKeyVarNameBitmapMap,
                            loopKeyToVarNameBitmapMap,
                            importedVarNameToBitmapMap,
                            settingVarName,
                            topAcIVarName,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToBitmapAndExitSignal ->
                            val exitSignalClass = varNameToBitmapAndExitSignal.second
                            if (
                                exitSignalClass == ImageActionKeyManager.ExitSignal.EXIT_SIGNAL
                            ) {
                                exitSignal = true
                                return
                            }
                            val varNameToBitmap = varNameToBitmapAndExitSignal.first
                            privateLoopKeyVarNameBitmapMap.put(
                                curMapLoopKey,
                                varNameToBitmap.first,
                                varNameToBitmap.second
                            )

                            val isGlobalForRawVar =
                                globalVarNameRegex.matches(varNameToBitmap.first)
                            val isNotRunPrefix =
                                !topAcIVarName.isNullOrEmpty()
                                        && !topAcIVarName.startsWith(escapeRunPrefix)
                            val isRegisterToTopForPrivate =
                                isGlobalForRawVar && isNotRunPrefix
                            val removedLoopKey =
                                removeLoopKey(curMapLoopKey)
                            if (
                                isRegisterToTopForPrivate
                                && !topAcIVarName.isNullOrEmpty()
                            ) {
                                privateLoopKeyVarNameBitmapMap.put(
                                    removedLoopKey,
                                    topAcIVarName,
                                    varNameToBitmap.second
                                )
                            }
                            val varNameForPut = topAcIVarName
                                ?: varNameToBitmap.first
                            val isGlobalRegister =
                                isGlobalForRawVar
                                        && globalVarNameRegex.matches(varNameForPut)
                            if (isGlobalRegister) {
                                loopKeyToVarNameBitmapMap.put(
                                    removedLoopKey,
                                    varNameForPut,
                                    varNameToBitmap.second
                                )
                            }
                            FileSystems.updateFile(
                                File(
                                    UsePath.cmdclickDefaultIDebugAppDirPath,
                                    "image_${settingVarName}.txt"
                                ).absolutePath,
                                listOf(
                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
                                    "imageVarKey: ${imageVarKey}",
                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
                                    "settingVarName: $settingVarName",
                                    "isAsync: ${isAsync}",
                                    "curMapLoopKey: ${curMapLoopKey}",
                                    "removedLoopKey: ${removedLoopKey}",
                                    "varName: ${varNameToBitmap.first}",
                                    "topAcIVarName: ${topAcIVarName}",
                                    "varNameForPut: ${varNameForPut}",
                                    "curMapLoopKey privateLoopKeyVarNameBitmapMap: ${
                                        privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
                                            curMapLoopKey
                                        )?.map {
                                            it.key
                                        }?.joinToString("\n")
                                    }",
                                    "removedLoopKey privateLoopKeyVarNameBitmapMap: ${
                                        privateLoopKeyVarNameBitmapMap.getAsyncVarNameToBitmap(
                                            removedLoopKey
                                        )?.map {
                                            it.key
                                        }?.joinToString("\n")
                                    }",
                                    "curMapLoopKey loopKeyToVarNameBitmapMap: ${
                                        loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
                                            curMapLoopKey
                                        )?.map {
                                            it.key
                                        }?.joinToString("\n")
                                    }",
                                    "removedLoopKey loopKeyToVarNameBitmapMap: ${
                                        loopKeyToVarNameBitmapMap.getAsyncVarNameToBitmap(
                                            removedLoopKey
                                        )?.map {
                                            it.key
                                        }?.joinToString("\n")
                                    }",
                                ).joinToString("\n")
                            )
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
            private val asyncRunPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
            private val sAcVarKeyName =
                ImageActionKeyManager.ImageActionsKey.IMAGE_ACTION_VAR.key

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
                        ErrLogger.ImageActionErrType.I_AC_VAR,
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
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisGlobalVarNameExistErrWithRunPrefix.txt").absolutePath,
//                    listOf(
//                        "renewalVarName: ${renewalVarName}",
//                        "settingKeyToGlobalVarNameList: ${settingKeyToGlobalVarNameList}"
//                    ).joinToString("\n")
//                )
                if(
                    !isGlobalVarNameExistErr
                ) return false
                val spanIAcVarKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        sAcVarKeyName
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
                        ErrLogger.ImageActionErrType.I_AC_VAR,
                        "With ${escapeRunPrefix} prefix in ${spanIAcVarKeyName}, " +
                                "global (uppercase) var name must not exist: ${spanGlobalVarNameListCon}",
                        keyToSubKeyConWhere
                    )
                }
                return true
            }

            fun isGlobalVarNameMultipleErrWithoutRunPrefix(
                context: Context?,
                settingKeyToVarNameList: List<Pair<String, String>>,
                topIAcVarName: String,
                keyToSubKeyConWhere: String
            ): Boolean {
                if(
                    topIAcVarName.startsWith(escapeRunPrefix)
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
                        sAcVarKeyName
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
                        ErrLogger.ImageActionErrType.I_AC_VAR,
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
                        sAcVarKeyName
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
                        ErrLogger.ImageActionErrType.I_AC_VAR,
                        "Without ${escapeRunPrefix} prefix in ${spanSAdVarKeyName}, " +
                                "imported last var name must be global (uppercase): ${spanGlobalVarName}",
                        keyToSubKeyConWhere
                    )
                }
                return true
            }
        }

        private object VarErrManager {

            private val mainKeySeparator = ImageActionKeyManager.mainKeySeparator
            private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
            private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix

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
                        ErrLogger.ImageActionErrType.I_VAR,
                        "Var name must be unique: ${spanVarNameListCon}",
                        keyToSubKeyConWhere,
                    )
                }
                return true

            }

            fun isGlobalVarFuncNullResultErr(
                context: Context?,
                varName: String,
                returnBitmap: Bitmap?,
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
                runBlocking {
                    ErrLogger.sendErrLog(
                        context,
                        ErrLogger.ImageActionErrType.I_VAR,
                        "In global (uppercase), func result must be exist: ${spanVarName}",
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
                                "Not use ${spanAwaitKeyName}: ${spanAwaitVarName}, var name: settingKeyName: ${spanSettingKeyName}",
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
                val settingKeyToBlankVarNamePair = settingKeyToPrivateVarNameList.firstOrNull {
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

            fun isNotReplaceVarErr(
                context: Context?,
                keyToSubKeyConList: List<Pair<String, String>>,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val settingKeyToNoRunVarNameList = makeSettingKeyToNoRunVarNameList(
                    keyToSubKeyConList
                )
                val regexStrTemplate = "(#[{]%s[}])"
                val settingKeyListConRegex = let {
                    settingKeyToNoRunVarNameList.map {
                        regexStrTemplate.format(it.second)
                    }.joinToString("|") +
                            "|${regexStrTemplate.format(
                                ImageActionKeyManager.BitmapVar.itPronoun
                            )}"
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

            private fun execIsNotReplaceVarErr(
                context: Context?,
                subKeyListCon: String,
                keyToSubKeyConWhere: String,
            ): Boolean {
                val replaceKeyToSubKeyListCon = subKeyListCon.replace(
                    "#{${ImageActionKeyManager.BitmapVar}}",
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
                val dolVarMarkRegex = Regex("#[{][a-zA-Z0-9_]+[}]")
                val noReplaceVar = dolVarMarkRegex.find(replaceKeyToSubKeyListCon)?.value
                if(
                    noReplaceVar.isNullOrEmpty()
                    || noReplaceVar.startsWith("#{${escapeRunPrefix}")
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
                settingKeyToPrivateVarNameList.forEach {
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
                }
            }

            private fun makeSettingKeyToNoRunVarNameList(
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
                }
            }
        }


        private object SettingImport {

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
                keyToSubKeyCon: Pair<String, String>?,
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
                    var deferredVarNameToBitmapAndExitSignal: Deferred<
                            Pair<
                                    Pair<String, Bitmap?>,
                                    ImageActionKeyManager. ExitSignal?
                                    >?
                            >? = null
                    for (i in 1..50) {
                        if(
                            ErrLogger.AlreadyErr.get()
                        ) break
                        deferredVarNameToBitmapAndExitSignal =
                            loopKeyToAsyncDeferredVarNameBitmapMap
                                ?.getAsyncVarNameToBitmapAndExitSignal(
                                    curMapLoopKey
                                )?.get(awaitVarName)
                        if (
                            deferredVarNameToBitmapAndExitSignal != null
                        ) {
                            break
                        }
                        delay(100)
                    }
                    val varNameToBitmapAndExitSignal =
                        deferredVarNameToBitmapAndExitSignal?.await()
//                    loopKeyToAsyncDeferredVarNameBitmapMap?.clearVarName(
//                        curMapLoopKey,
//                        awaitVarName,
//                    )
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
                    else -> ImageIfManager.handle(
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
                        ErrLogger.ImageActionErrType.S_IF,
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
                        ImageActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
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
                ).replace(
                    Regex("[${SettingActionKeyManager.landSeparator}]+$"),
                    String(),
                )
                val repMap = makeRepValHolderMap(
                    actionImportMap.get(
                        ImageActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
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
                            topIAcVarName,
                            keyToSubKeyConWhere
                        )
                    }
                    val isGlobalVarNameMultipleExistErrWithoutRunPrefixJob = async {
                        ImportErrManager.isGlobalVarNameMultipleErrWithoutRunPrefix(
                            context,
                            settingKeyToVarNameList,
                            topIAcVarName,
                            keyToSubKeyConWhere
                        )
                    }
                    val isGlobalVarNameNotLastErrWithoutRunPrefixJob = async {
                        ImportErrManager.isGlobalVarNameNotLastErrWithoutRunPrefix(
                            context,
                            settingKeyToVarNameList,
                            topIAcVarName,
                            keyToSubKeyConWhere
                        )
                    }
                    isGlobalVarNameErrWithRunPrefixJob.await()
                            || isGlobalVarNameMultipleExistErrWithoutRunPrefixJob.await()
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

            private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
            private val asyncRunPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
            private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
            private val itReplaceVarStr = "${'$'}{${ImageActionKeyManager.BitmapVar}}"
            private var itPronounBitmapToExitSignal: Pair<
                    Bitmap?,
                    ImageActionKeyManager.ExitSignal?
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
                curMapLoopKey: String,
                loopKeyToAsyncDeferredVarNameBitmapMap: LoopKeyToAsyncDeferredVarNameBitmapMap?,
                privateLoopKeyVarNameBitmapMapClass: PrivateLoopKeyVarNameBitmapMap,
                loopKeyToVarNameBitmapMapClass: LoopKeyToVarNameBitmapMap,
                importedVarNameToBitmapMap: Map<String, Bitmap?>?,
                settingVarName: String,
                renewalVarName: String?,
                keyToSubKeyConWhere: String,
            ): Pair<Pair<String, Bitmap?>, ImageActionKeyManager.ExitSignal?>? {
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
                                var deferredVarNameToBitmapAndExitSignal: Deferred<
                                        Pair<
                                                Pair<String, Bitmap?>,
                                                ImageActionKeyManager. ExitSignal?
                                                >?
                                        >? = null
                                for (i in 1..50) {
                                    if(
                                        ErrLogger.AlreadyErr.get()
                                    ) break
                                    deferredVarNameToBitmapAndExitSignal =
                                        loopKeyToAsyncDeferredVarNameBitmapMap
                                            ?.getAsyncVarNameToBitmapAndExitSignal(
                                                curMapLoopKey
                                            )?.get(awaitVarName)
                                    FileSystems.updateFile(
                                        File(
                                            UsePath.cmdclickDefaultIDebugAppDirPath,
                                            "image_await_${settingVarName}.txt"
                                        ).absolutePath,
                                        listOf(
                                            "mainSubKeyPairList: ${mainSubKeyPairList}",
                                            "settingVarName: $settingVarName",
                                            "curMapLoopKey: ${curMapLoopKey}",
                                            "awaitVarNameList: ${awaitVarNameList}",
                                            "awaitVarName: ${awaitVarName}",
                                            "loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameBitmapMap?.getAsyncVarNameToBitmapAndExitSignal(
                                                curMapLoopKey
                                            )?.map {
                                                it.key
                                            }?.joinToString("\n")}"
//                    "varNameToBitmap: ${varNameToBitmap.first}",
//                    "bitmapWidth: ${varNameToBitmap.second?.width}",
//                    "bitmapHeight: ${varNameToBitmap.second?.height}",
                                        ).joinToString("\n")  + "\n\n========\n\n"
                                    )
                                    if (
                                        deferredVarNameToBitmapAndExitSignal != null
                                    ) {
                                        break
                                    }
                                    delay(100)
                                }
                                val varNameToBitmapAndExitSignal =
                                    deferredVarNameToBitmapAndExitSignal?.await()
//                                loopKeyToAsyncDeferredVarNameBitmapMap?.clearVarName(
//                                    curMapLoopKey,
//                                    awaitVarName,
//                                )
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
                            val curIVarKey =
                                mainSubKeyMap.get(mainSubKey)?.let {
                                    ImageActionKeyManager.BitmapVar.convertBitmapKey(it)
                                }
                            val returnBitmap = when(true) {
                                (curIVarKey == itPronoun) -> itPronounBitmapToExitSignal?.first
                                curIVarKey.isNullOrEmpty() -> null
                                else -> privateLoopKeyVarNameBitmapMapClass.getAsyncVarNameToBitmap(
                                    curMapLoopKey
                                )?.get(curIVarKey)
                                    ?: loopKeyToVarNameBitmapMapClass.getAsyncVarNameToBitmap(
                                        curMapLoopKey
                                    )?.get(curIVarKey)
                            }
                            VarErrManager.isGlobalVarFuncNullResultErr(
                                context,
                                renewalVarName ?: settingVarName,
                                returnBitmap,
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
                                (privateLoopKeyVarNameBitmapMapClass
                                    .getAsyncVarNameToBitmap(curMapLoopKey)?.toMap()
                                    ?: emptyMap()) +
                                        (loopKeyToVarNameBitmapMapClass
                                            .getAsyncVarNameToBitmap(curMapLoopKey)?.toMap()
                                            ?: emptyMap()) +
                                        (importedVarNameToBitmapMap ?: emptyMap()) +
                                        mapOf(itPronoun to itPronounBitmapToExitSignal?.first)
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
                                itPronounBitmapToExitSignal = null
                                return@forEach
                            }
                            val resultBitmapToExitMacro = resultBitmapToExitMacroAndCheckErr?.first
                            VarErrManager.isGlobalVarFuncNullResultErr(
                                context,
                                renewalVarName ?: settingVarName,
                                resultBitmapToExitMacro?.first,
                                keyToSubKeyConWhere,
                            ).let {
                                    isGlobalVarFuncNullResultErr ->
                                if(
                                    isGlobalVarFuncNullResultErr
                                ) return null
                            }
                            itPronounBitmapToExitSignal = resultBitmapToExitMacro

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
                            val isImportToErrType = ImageIfManager.handle(
                                judgeTargetStr,
                                argsPairList
                            )
                            val errType = isImportToErrType.second
                            if(errType != null){
                                runBlocking {
                                    ErrLogger.sendErrLog(
                                        context,
                                        ErrLogger.ImageActionErrType.S_IF,
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
                    if(privateSubKeyClass != ImageActionKeyManager.ImageSubKey.I_IF){
                        isNext = true
                    }
                }
                val isNoImageVar =
                    settingVarName.startsWith(escapeRunPrefix)
                            || settingVarName.startsWith(asyncRunPrefix)
                val isEscape =
                    isNoImageVar
                            && itPronounBitmapToExitSignal?.second != ImageActionKeyManager.ExitSignal.EXIT_SIGNAL
                return when(isEscape){
                    true -> null
                    else -> Pair(
                        Pair(
                            settingVarName,
                            itPronounBitmapToExitSignal?.first,
                            ),
                        itPronounBitmapToExitSignal?.second,
                        )
                }
            }
//            private fun replaceItPronoun(con: String): String {
//                return con.replace(itReplaceVarStr, itPronounValue)
//            }
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
