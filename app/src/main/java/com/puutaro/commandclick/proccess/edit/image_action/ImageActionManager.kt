package com.puutaro.commandclick.proccess.edit.image_action

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionData
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionErrLogger
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionImport
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionReturnErrManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionVarErrManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageFuncManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageReturnExecutor
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.StrToMapListTool
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
import java.lang.ref.WeakReference

class ImageActionManager {
    companion object {

        private val globalVarNameRegex = ImageActionKeyManager.globalVarNameRegex
        private const val awaitWaitTimes =
            ImageActionKeyManager.awaitWaitTimes
        private const val mapRoopKeyUnit =
            ImageActionKeyManager.LoopKeyManager.mapRoopKeyUnit

        fun init(){
            ImageActionImport.BeforeActionImportMapManager.init()
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
        val loopClasses = imageActionExecutor.makeResultLoopKeyToVarNameValueMap(
            topVarNameToVarNameBitmapMap,
            imageActionAsyncCoroutine,
            editConstraintListAdapterArg,
            keyToSubKeyConList,
            mapRoopKeyUnit,
            null,
            keyToSubKeyConWhere,
            null,
            null,
            topLevelBitmapStrKeyList,
        )
        return ImageActionKeyManager.LoopKeyManager.getResultLoopKeyToVarNameValueMap(
            loopClasses?.first
        )
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
        val keyToSubKeyConList = ImageActionKeyManager.KeyToSubKeyMapListMaker.make(
            keyToSubKeyConWithReflectRepValDefalt,
        )
        if (
            keyToSubKeyConList.isEmpty()
        ) return null
        return keyToSubKeyConList
    }


    private class ImageActionExecutor(
        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: Map<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
        private val topLevelBitmapStrKeyList: List<String>?,
    ) {

//        private val loopKeyToVarNameBitmapMap = ImageActionData.LoopKeyToVarNameBitmapMap()
////        mutableMapOf<String, MutableMap<String, Bitmap?>>()
//        private val privateLoopKeyVarNameBitmapMap = ImageActionData.PrivateLoopKeyVarNameBitmapMap()
//        private val loopKeyToAsyncDeferredVarNameBitmapMap = ImageActionData.LoopKeyToAsyncDeferredVarNameBitmapMap()
        private var imageActionExitManager = ImageActionData.ImageActionExitManager()
        private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
        private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
        private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix


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
        ): Pair<
                ImageActionData.LoopKeyToVarNameBitmapMap,
                ImageActionData.PrivateLoopKeyVarNameBitmapMap,
        >? {
            val fragment = fragmentRef.get()
                ?: return null
            val context = fragment.context
                ?: return null
            val loopKeyToVarNameBitmapMap =
                ImageActionData.LoopKeyToVarNameBitmapMap()
            val privateLoopKeyVarNameBitmapMap =
                ImageActionData.PrivateLoopKeyVarNameBitmapMap()
            val loopKeyToAsyncDeferredVarNameBitmapMap =
                ImageActionData.LoopKeyToAsyncDeferredVarNameBitmapMap()
//            loopKeyToVarNameBitmapMap
//                .initPrivateLoopKeyVarNameBitmapMapMutex(curMapLoopKey)
//            privateLoopKeyVarNameBitmapMap
//                .initPrivateLoopKeyVarNameBitmapMapMutex(curMapLoopKey)
            if(
                keyToSubKeyConList.isNullOrEmpty()
            ) return null
            val isErr = withContext(Dispatchers.IO) {
                val settingKeyAndAsyncVarNameToAwaitVarNameList =
                    ImageActionVarErrManager.makeSettingKeyAndAsyncVarNameToAwaitVarNameList(
                        keyToSubKeyConList
                    )
                val isAwaitNotAsyncVarErrJob = async {
                    ImageActionVarErrManager.isAwaitNotAsyncVarErr(
                        context,
                        settingKeyAndAsyncVarNameToAwaitVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isAwaitDuplicateAsyncVarErrJob = async {
                    ImageActionVarErrManager.isAwaitDuplicateAsyncVarErr(
                        context,
                        settingKeyAndAsyncVarNameToAwaitVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isAwaitNotDefiniteVarErrJob = async {
                    ImageActionVarErrManager.isAwaitNotDefiniteAsyncVarErr(
                        context,
                        settingKeyAndAsyncVarNameToAwaitVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotAwaitAsyncVarErrOrAwaitInAsyncVarErrJob = async {
                    ImageActionVarErrManager.isNotAwaitAsyncVarErrOrAwaitInAsyncVarErr(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToPrivateVarNameList =
                    ImageActionVarErrManager.makeSettingKeyToPrivateVarNameList(
                        keyToSubKeyConList
                    )
                val isBlankIVarOrIAcVarErrJob = async {
                    ImageActionVarErrManager.isBlankIVarOrIAcVarErr(
                        context,
                        settingKeyToPrivateVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotUseVarErrJob = async {
                    ImageActionVarErrManager.isNotUseVarErr(
                        context,
                        settingKeyToPrivateVarNameList,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToNoRunVarNameList = ImageActionVarErrManager.makeSettingKeyToNoRunVarNameList(
                    keyToSubKeyConList
                )
                val isShadowTopLevelVarErrJob = async {
                    ImageActionVarErrManager.isShadowTopLevelVarErr(
                        context,
                        settingKeyToNoRunVarNameList,
                        topLevelBitmapStrKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotDefinitionVarErr = async {
                    ImageActionVarErrManager.isNotDefinitionVarErr(
                        context,
                        settingKeyToNoRunVarNameList,
                        keyToSubKeyConList,
                        bitmapVarKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotReplaceStringVarErrJob = async {
                    ImageActionVarErrManager.isNotReplaceStringVarErr(
                        context,
                        keyToSubKeyConList.map {
                            it.second
                        }.joinToString("\n"),
                        keyToSubKeyConWhere,
                    )
                }
                val isRunPrefixUseErrJob = async {
                    ImageActionVarErrManager.isRunPrefixUseErr(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToBitmapVarKeyListForReturn =
                    ImageActionKeyManager.makeSettingKeyToBitmapVarKeyListForReturn(keyToSubKeyConList)
                val settingKeyToBitmapVarKeyListWithIrregular = settingKeyToBitmapVarKeyListForReturn.filter {
                    it.second.isNotEmpty()
                }
                val isIrregularVarNameErrJob = async {
                    ImageActionVarErrManager.isIrregularVarNameErr(
                        context,
                        settingKeyToBitmapVarKeyListWithIrregular,
                        keyToSubKeyConWhere,
                    )
                }
                val settingKeyToVarNameList = ImageActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(
                    settingKeyToBitmapVarKeyListWithIrregular
                )
                val isSameVarNameErrJob = async {
                    ImageActionVarErrManager.isSameVarNameErr(
                        context,
                        settingKeyToVarNameList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotBeforeDefinitionInReturnErrJob = async {
                    ImageActionReturnErrManager.isNotBeforeDefinitionInReturnErr(
                        context,
                        settingKeyToVarNameList,
                        bitmapVarKeyList,
                        keyToSubKeyConWhere,
                    )
                }
                val isNotExistReturnOrFuncJob = async {
                    ImageActionVarErrManager.isNotExistReturnOrFunc(
                        context,
                        keyToSubKeyConList,
                        keyToSubKeyConWhere,
                    )
                }
                val returnKeyToVarNameList = ImageActionReturnErrManager.makeReturnKeyToBitmapVarMarkList(
                    keyToSubKeyConList
                )
                val isBlankReturnErrWithoutRunPrefixJob =
                    async {
                        ImageActionReturnErrManager.isBlankReturnErrWithoutRunPrefix(
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
            if(isErr) {
                imageActionExitManager.setExit()
                return null
            }
            keyToSubKeyConList.forEach { keyToSubKeyConSrc ->
                if (
                    imageActionExitManager.get()
                ) return null
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
//                ImageActionVarErrManager.isNotReplaceVarErr(
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
                            ImageActionImport.makeImportPathAndRenewalVarNameToImportCon(
                                context,
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                curMapLoopKey,
                                topVarNameToVarNameBitmapMap,
                                loopKeyToAsyncDeferredVarNameBitmapMap,
                                loopKeyToVarNameBitmapMap,
                                privateLoopKeyVarNameBitmapMap,
                                importedVarNameToBitmapMap,
                                imageActionExitManager,
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
//                        val curImportedVarNameToBitmapMap =
//                            renewalVarNameToImportConToVarNameToBitmapMap.third
                        val importVarNameToBitmapMap =
                            importPathAndRenewalVarNameToImportConToVarNameBitmapMap.third
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
                        val addedLoopKey = ImageActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey)
                        if(isAsync){
                            val asyncJob = CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.IO) {
                                    val deferred = async {
//                                        val varNameToBitmapMap =
//                                            makeValueToBitmapMap(
//                                                curMapLoopKey,
//                                                topVarNameToVarNameBitmapMap,
//                                                importedVarNameToBitmapMap,
//                                                loopKeyToVarNameBitmapMap,
//                                                privateLoopKeyVarNameBitmapMap,
//                                                curImportedVarNameToBitmapMap,
//                                                null,
//                                                )
                                        val curBitmapVarKeyList =
                                            ((topVarNameToVarNameBitmapMap ?: emptyMap()) +
                                                    (importVarNameToBitmapMap ?: emptyMap())).map {
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
                                        val loopMapClasses = makeResultLoopKeyToVarNameValueMap(
                                            topVarNameToVarNameBitmapMap,
                                            imageActionAsyncCoroutine,
                                            editConstraintListAdapterArg,
                                            importedKeyToSubKeyConList,
                                            addedLoopKey,
                                            (originImportPathList ?: emptyList()) + listOf(importPath),
                                            "${importPath} by imported",
                                            acIVarName,
                                            importVarNameToBitmapMap,
                                            curBitmapVarKeyList,
                                        )
                                        val downLoopKeyVarNameBitmapMap = loopMapClasses?.first
                                        val downPrivateLoopKeyVarNameBitmapMap = loopMapClasses?.second
                                        val proposalRenewalVarNameSrcInnerMapBitmap =
                                            downPrivateLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmap(
                                                curMapLoopKey,
                                            )?.get(
                                                acIVarName
                                            )
                                        if (
                                            proposalRenewalVarNameSrcInnerMapBitmap != null
                                        ) {
                                            privateLoopKeyVarNameBitmapMap.put(
                                                curMapLoopKey,
                                                acIVarName,
                                                proposalRenewalVarNameSrcInnerMapBitmap
                                            )
                                        }
                                        if(
                                            !globalVarNameRegex.matches(acIVarName)
                                            || mapRoopKeyUnit != curMapLoopKey
                                        ) return@async null
                                        val proposalRenewalVarNameSrcMapBitmap =
                                            downLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmap(
                                                curMapLoopKey,
                                            )?.get(acIVarName)
                                        if (
                                            proposalRenewalVarNameSrcMapBitmap != null
                                        ) {
                                            loopKeyToVarNameBitmapMap.put(
                                                mapRoopKeyUnit,
                                                acIVarName,
                                                proposalRenewalVarNameSrcMapBitmap
                                            )
                                        }
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
                        val curBitmapVarKeyList =
                            ((topVarNameToVarNameBitmapMap ?: emptyMap()) +
                                    (importVarNameToBitmapMap ?: emptyMap())).map {
                                it.key
                            }
                        val loopMapClasses = makeResultLoopKeyToVarNameValueMap(
                            topVarNameToVarNameBitmapMap,
                            imageActionAsyncCoroutine,
                            editConstraintListAdapterArg,
                            importedKeyToSubKeyConList,
                            addedLoopKey,
                            (originImportPathList ?: emptyList()) + listOf(importPath),
                            "${importPath} by imported",
                            acIVarName,
                            importVarNameToBitmapMap,
                            curBitmapVarKeyList,
                        )
                        val downLoopKeyVarNameBitmapMap = loopMapClasses?.first
                        val downPrivateLoopKeyVarNameBitmapMap = loopMapClasses?.second
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
                        val proposalRenewalVarNameSrcInnerMapBitmap =
                            downPrivateLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmap(
                                curMapLoopKey,
                            )?.get(
                                acIVarName
                            )
                        if (
                            proposalRenewalVarNameSrcInnerMapBitmap != null
                        ) {
                            privateLoopKeyVarNameBitmapMap.put(
                                curMapLoopKey,
                                acIVarName,
                                proposalRenewalVarNameSrcInnerMapBitmap
                            )
                        }
                        if(
                            !globalVarNameRegex.matches(acIVarName)
                            || mapRoopKeyUnit != curMapLoopKey
                        ) return@forEach
                        val proposalRenewalVarNameSrcMapBitmap =
                            downLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmap(
                                curMapLoopKey,
                            )?.get(acIVarName)
                        if (
                            proposalRenewalVarNameSrcMapBitmap != null
                        ) {
                            loopKeyToVarNameBitmapMap.put(
                                mapRoopKeyUnit,
                                acIVarName,
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
                        val settingVarName = StrToMapListTool.getValue(
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
                                        imageActionExitManager,
                                        settingVarName,
                                        topAcIVarName,
                                        keyToSubKeyConWhere,
                                    )
                                }
                                loopKeyToAsyncDeferredVarNameBitmapMap.put(
                                    curMapLoopKey,
                                    settingVarName,
                                    deferred
                                )
//                                val removedLoopKey =
//                                    ImageActionKeyManager.LoopKeyManager.removeLoopKey(curMapLoopKey)
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
//                                if (
//                                    removedLoopKey == curMapLoopKey
//                                ) return@launch
//                                val isGlobalForRawVar =
//                                    globalVarNameRegex.matches(settingVarName)
//                                if (
//                                    !isGlobalForRawVar
//                                ) return@launch
//                                if (
//                                    topAcIVarName.isNullOrEmpty()
//                                ) return@launch
//                                if (
//                                    topAcIVarName.startsWith(escapeRunPrefix)
//                                ) return@launch
//                                loopKeyToAsyncDeferredVarNameBitmapMap.put(
//                                    removedLoopKey,
//                                    topAcIVarName,
//                                    deferred
//                                )
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
                            imageActionExitManager,
                            settingVarName,
                            topAcIVarName,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToBitmapAndExitSignal ->
                            val exitSignalClass = varNameToBitmapAndExitSignal.second
                            if (
                                exitSignalClass == ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) {
                                imageActionExitManager.setExit()
                                return Pair(
                                    loopKeyToVarNameBitmapMap,
                                    privateLoopKeyVarNameBitmapMap,
                                )
                            }
                            val varNameToBitmap = varNameToBitmapAndExitSignal.first
                            privateLoopKeyVarNameBitmapMap.put(
                                curMapLoopKey,
                                varNameToBitmap.first,
                                varNameToBitmap.second
                            )
                            if(
                                globalVarNameRegex.matches(varNameToBitmap.first)
                                && curMapLoopKey == mapRoopKeyUnit
                            ) {
                                loopKeyToVarNameBitmapMap.put(
                                    mapRoopKeyUnit,
                                    varNameToBitmap.first,
                                    varNameToBitmap.second
                                )
                            }
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
                        val bitmapVarMark = StrToMapListTool.getValue(
                            mainSubKeyPairList,
                            settingReturnKey
                        )?.get(settingReturnKey)
                            ?: String()
                        val returnBitmap = let {
                            val varNameToBitmapMap =
                                ImageActionKeyManager.makeValueToBitmapMap(
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
                            imageActionExitManager,
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
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultIDebugAppDirPath,
//                                    "lSReturn_${bitmapVarMark}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "subKeyCon: ${subKeyCon}",
//                                    "keyToSubKeyCon.second: ${keyToSubKeyCon.second}",
//                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "varNameToBitmapAndExitSignal: ${varNameToBitmapAndExitSignal}",
//                                    "isRegisterToTop: ${isRegisterToTop}",
//                                    "exitSignalClass: ${breakSignalClass}",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "curMapLoopKey makeValueToBitmapMap: ${makeValueToBitmapMap(
//                                        curMapLoopKey,
//                                        topVarNameToVarNameBitmapMap,
//                                        importedVarNameToBitmapMap,
//                                        loopKeyToVarNameBitmapMap,
//                                        privateLoopKeyVarNameBitmapMap,
//                                        null,
//                                        null,
//                                    )}",
//                                    "removeLoopKey(curMapLoopKey): ${removeLoopKey(curMapLoopKey)}",
//                                    "removeLoopKey(curMapLoopKey) makeValueToBitmapMap: ${makeValueToBitmapMap(
//                                        removeLoopKey(curMapLoopKey),
//                                        topVarNameToVarNameBitmapMap,
//                                        importedVarNameToBitmapMap,
//                                        loopKeyToVarNameBitmapMap,
//                                        privateLoopKeyVarNameBitmapMap,
//                                        null,
//                                        null,
//                                    )}",
//                                ).joinToString("\n\n\n")
//                            )
                            if(isRegisterToTop) {
                                ImageActionReturnErrManager.isReturnBitmapNullResultErr(
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
                                    return Pair(
                                        loopKeyToVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                    )
                                }
                            }
                            val removedLoopKey =
                                ImageActionKeyManager.LoopKeyManager.removeLoopKey(curMapLoopKey)
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
                                    imageActionExitManager.setExit()
                                    return Pair(
                                        loopKeyToVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                    )
                                }
                                ImageActionKeyManager.BreakSignal.RETURN_SIGNAL -> {
                                    return Pair(
                                        loopKeyToVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
            return Pair(
                loopKeyToVarNameBitmapMap,
                privateLoopKeyVarNameBitmapMap,
            )
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
                mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
                busyboxExecutor: BusyboxExecutor?,
                editConstraintListAdapterArg: EditConstraintListAdapter?,
                topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
                curMapLoopKey: String,
                loopKeyToAsyncDeferredVarNameBitmapMap: ImageActionData.LoopKeyToAsyncDeferredVarNameBitmapMap?,
                privateLoopKeyVarNameBitmapMapClass: ImageActionData.PrivateLoopKeyVarNameBitmapMap,
                loopKeyToVarNameBitmapMapClass: ImageActionData.LoopKeyToVarNameBitmapMap,
                importedVarNameToBitmapMap: Map<String, Bitmap?>?,
                imageActionExitManager: ImageActionData.ImageActionExitManager,
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
                                            CheckTool.lightBlue,
                                            ImageActionKeyManager.ImageActionsKey.IMAGE_VAR.key
                                        )
                                    runBlocking {
                                        ImageActionErrLogger.sendErrLog(
                                            context,
                                            ImageActionErrLogger.ImageActionErrType.AWAIT,
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
                                ImageActionKeyManager.makeValueToBitmapMap(
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
                            ImageActionVarErrManager.isGlobalVarNullResultErr(
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
                                ImageActionKeyManager.makeValueToBitmapMap(
                                    curMapLoopKey,
                                    topVarNameToVarNameBitmapMap,
                                    importedVarNameToBitmapMap,
                                    loopKeyToVarNameBitmapMapClass,
                                    privateLoopKeyVarNameBitmapMapClass,
                                    null,
                                    mapOf(itPronoun to itPronounBitmapToBreakSignal?.first),
                                )
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
                            )
                            val checkErr = resultBitmapToExitMacroAndCheckErr?.second
                            if(checkErr != null){
                                runBlocking {
                                    ImageActionErrLogger.sendErrLog(
                                        context,
                                        ImageActionErrLogger.ImageActionErrType.FUNC,
                                        checkErr.errMessage,
                                        keyToSubKeyConWhere,
                                    )
                                }
                                itPronounBitmapToBreakSignal = null
                                isNext = false
                                return@forEach
                            }
                            val resultBitmapToExitMacro = resultBitmapToExitMacroAndCheckErr?.first
                            ImageActionVarErrManager.isGlobalVarNullResultErr(
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
                                    ImageActionErrLogger.sendErrLog(
                                        context,
                                        ImageActionErrLogger.ImageActionErrType.I_IF,
                                        errType.errMessage,
                                        keyToSubKeyConWhere
                                    )
                                }
                                imageActionExitManager.setExit()
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
        }
    }
}

