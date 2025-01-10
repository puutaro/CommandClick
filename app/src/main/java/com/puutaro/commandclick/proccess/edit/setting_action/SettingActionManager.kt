package com.puutaro.commandclick.proccess.edit.setting_action

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager.makeVarNameToValueStrMap
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.ReturnErrManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingActionData
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingActionErrLogger
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingActionImportManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingReturnExecutor
import com.puutaro.commandclick.proccess.edit.setting_action.libs.VarErrManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ColorForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.DebugForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.EditForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ExitForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.FileSystemsForSettingHandler
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ListForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.LocalDatetimeForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.MathCulcForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.PathForSettingHandler
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.RndForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ShellToolManagerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.SystemInfoForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ToastForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.TsvToolForSetting
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.StrToMapListTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import com.puutaro.commandclick.util.str.BackslashTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class SettingActionManager {
    companion object {

        private const val awaitWaitTimes =
            SettingActionKeyManager.AwaitManager.awaitWaitTimes
        private val globalVarNameRegex =
            SettingActionKeyManager.globalVarNameRegex

        fun init(){
            SettingActionImportManager.BeforeActionImportMapManager.init()
        }
    }

    suspend fun exec(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        topLevelValueStrKeyList: List<String>?,
        topVarNameToValueStrMap: Map<String, String?>?,
        keyToSubKeyCon: String?,
        keyToSubKeyConWhere: String,
        editConstraintListAdapterArg: EditConstraintListAdapter? = null,
        topAcSVarName: String? = null,
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
            topVarNameToValueStrMap,
            settingActionAsyncCoroutine,
            editConstraintListAdapterArg,
            keyToSubKeyConList,
            SettingActionExecutor.mapRoopKeyUnit,
            null,
            keyToSubKeyConWhere,
            topAcSVarName,
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
        val keyToSubKeyConList = SettingActionKeyManager.KeyToSubKeyMapListMaker.make(
            keyToSubKeyConWithReflectRepValDefalt,
        )
        if (
            keyToSubKeyConList.isEmpty()
        ) return null
        return keyToSubKeyConList
    }


    private class SettingActionExecutor(
        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: Map<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
        private val topLevelValueStrKeyList: List<String>?,
    ) {

        private val loopKeyToVarNameValueStrMap = SettingActionData.LoopKeyToVarNameValueStrMap()
//        mutableMapOf<String, MutableMap<String, Bitmap?>>()
        private val privateLoopKeyVarNameValueStrMap =
    SettingActionData.PrivateLoopKeyVarNameValueStrMap()
        private val loopKeyToAsyncDeferredVarNameValueStrMap =
            SettingActionData.LoopKeyToAsyncDeferredVarNameValueStrMap()
        private var settingActionExitManager = SettingActionData.SettingActionExitManager()
        private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
        private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
        private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
        private val returnTopAcVarNameMacro = SettingActionKeyManager.returnTopAcVarNameMacro

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
            topVarNameToValueStrMap: Map<String, String?>?,
            settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
            editConstraintListAdapterArg: EditConstraintListAdapter?,
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            originImportPathList: List<String>?,
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
                        (stringVarKeyList ?: emptyList()) + (topLevelValueStrKeyList ?: emptyList()),
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
                    SettingActionKeyManager.makeSettingKeyToVarNameListForReturn(keyToSubKeyConList)
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
                val settingKeyToVarNameList = SettingActionKeyManager
                    .filterSettingKeyToDefinitionListByValidVarDefinition(
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
                    settingActionExitManager.get()
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
                            SettingActionImportManager.makeImportPathAndRenewalVarNameToImportCon(
                                context,
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                curMapLoopKey,
                                topVarNameToValueStrMap,
                                loopKeyToAsyncDeferredVarNameValueStrMap,
                                privateLoopKeyVarNameValueStrMap,
                                loopKeyToVarNameValueStrMap,
                                importedVarNameToValueStrMap,
                                settingActionExitManager,
                                keyToSubKeyCon,
                                originImportPathList,
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
                                                topVarNameToValueStrMap,
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
                                            topVarNameToValueStrMap,
                                            settingActionAsyncCoroutine,
                                            editConstraintListAdapterArg,
                                            importedKeyToSubKeyConList,
                                            addLoopKey(curMapLoopKey),
                                            (originImportPathList?: emptyList()) + listOf(importPath),
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
                                topVarNameToValueStrMap,
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
                            topVarNameToValueStrMap,
                            settingActionAsyncCoroutine,
                            editConstraintListAdapterArg,
                            importedKeyToSubKeyConList,
                            addLoopKey(curMapLoopKey),
                            (originImportPathList ?: emptyList()) + listOf(importPath),
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
                        val settingVarName = StrToMapListTool.getValue(
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
                                        fannelInfoMap,
                                        setReplaceVariableMapSrc,
                                        mainSubKeyPairList,
                                        busyboxExecutor,
                                        editConstraintListAdapterArg,
                                        curMapLoopKey,
                                        topVarNameToValueStrMap,
                                        loopKeyToAsyncDeferredVarNameValueStrMap,
                                        privateLoopKeyVarNameValueStrMap,
                                        loopKeyToVarNameValueStrMap,
                                        importedVarNameToValueStrMap,
                                        settingActionExitManager,
                                        settingVarName,
                                        topAcSVarName,
                                        keyToSubKeyConWhere,
                                    )
                                }
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
                            fannelInfoMap,
                            setReplaceVariableMapSrc,
                            mainSubKeyPairList,
                            busyboxExecutor,
                            editConstraintListAdapterArg,
                            curMapLoopKey,
                            topVarNameToValueStrMap,
                            loopKeyToAsyncDeferredVarNameValueStrMap,
                            privateLoopKeyVarNameValueStrMap,
                            loopKeyToVarNameValueStrMap,
                            importedVarNameToValueStrMap,
                            settingActionExitManager,
                            settingVarName,
                            topAcSVarName,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToStrValueAndExitSignal ->
                            val exitSignalClass = varNameToStrValueAndExitSignal.second
                            if (
                                exitSignalClass == SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) {
                                settingActionExitManager.setExit()
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
                        val valueStrBeforeReplace = StrToMapListTool.getValue(
                            mainSubKeyPairList,
                            settingReturnKey
                        )?.get(settingReturnKey)
                            ?: String()
                        SettingReturnExecutor().exec(
                            fragment,
                            mainSubKeyPairList,
                            curMapLoopKey,
                            topVarNameToValueStrMap,
                            privateLoopKeyVarNameValueStrMap,
                            loopKeyToVarNameValueStrMap,
                            importedVarNameToValueStrMap,
                            settingActionExitManager,
                            valueStrBeforeReplace,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToStrValueAndExitSignal ->
                            val breakSignalClass = varNameToStrValueAndExitSignal.second
                            val varNameToValueStr = varNameToStrValueAndExitSignal.first
                            val returnSignal = varNameToValueStr?.first
                            val isRegisterToTop =
                                returnSignal == SettingActionKeyManager.SettingReturnManager.OutputReturn.OUTPUT_RETURN
                                        && !topAcSVarName.isNullOrEmpty()
                                        && varNameToValueStr != null
                                        && !topAcSVarName.startsWith(escapeRunPrefix)
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
                            if(
                                topAcSVarName == returnTopAcVarNameMacro
                                && varNameToValueStr != null
                            ){
                                loopKeyToVarNameValueStrMap.put(
                                    removedLoopKey,
                                    topAcSVarName,
                                    varNameToValueStr.second
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
                                SettingActionKeyManager.BreakSignal.EXIT_SIGNAL -> {
                                    settingActionExitManager.setExit()
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
                fannelInfoMap: Map<String, String>,
                setReplaceVariableMapSrc: Map<String, String>?,
                mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
                busyboxExecutor: BusyboxExecutor?,
                editConstraintListAdapterArg: EditConstraintListAdapter?,
                curMapLoopKey: String,
                topVarNameToValueStrMap: Map<String, String?>?,
                loopKeyToAsyncDeferredVarNameValueStrMap: SettingActionData.LoopKeyToAsyncDeferredVarNameValueStrMap?,
                privateLoopKeyVarNameValueStrMapClass: SettingActionData.PrivateLoopKeyVarNameValueStrMap,
                loopKeyToVarNameValueStrMapClass: SettingActionData.LoopKeyToVarNameValueStrMap,
                importedVarNameToValueStrMap: Map<String, String?>?,
                settingActionExitManager: SettingActionData.SettingActionExitManager,
                settingVarName: String,
                renewalVarName: String?,
                keyToSubKeyConWhere: String,
            ): Pair<Pair<String, String?>, SettingActionKeyManager.BreakSignal?>? {
                val context = fragment.context
                mainSubKeyPairList.forEach {
                        mainSubKeyPair ->
                    val varNameToValueStrMap = makeVarNameToValueStrMap(
                        curMapLoopKey,
                        topVarNameToValueStrMap,
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
                                    curIVarKey.isNullOrEmpty() -> null
                                    else -> varNameToValueStrMap.get(curIVarKey)
                                }
                            }
                            itPronounValueStrToBreakSignal = Pair(valueString, null)
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
                                        SettingActionErrLogger.sendErrLog(
                                            context,
                                            SettingActionErrLogger.SettingActionErrType.AWAIT,
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
                                    curIVarKey.isNullOrEmpty() -> null
                                    else -> varNameToValueStrMap.get(curIVarKey)
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
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                busyboxExecutor,
                                varNameToValueStrMap,
                                keyToSubKeyConWhere,
                                funcTypeDotMethod,
                                argsPairList,
                                editConstraintListAdapterArg,
                            )
                            val checkErr = resultValueStrToExitMacroAndCheckErr?.second
                            if(checkErr != null){
                                runBlocking {
                                    SettingActionErrLogger.sendErrLog(
                                        context,
                                        SettingActionErrLogger.SettingActionErrType.FUNC,
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
                            )
                            val errType = isNextToErrType.second
                            if(errType != null){
                                runBlocking {
                                    SettingActionErrLogger.sendErrLog(
                                        context,
                                        SettingActionErrLogger.SettingActionErrType.S_IF,
                                        errType.errMessage,
                                        keyToSubKeyConWhere
                                    )
                                }
                                itPronounValueStrToBreakSignal = null
                                isNext = false
                                settingActionExitManager.setExit()
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
    }
}

private object SettingFuncManager {

    private const val funcTypeAndMethodSeparatorDot = "."

    suspend fun handle(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        topVarNameToValueStrMap: Map<String, String?>?,
        keyToSubKeyConWhere: String,
        funcTypeDotMethod: String,
        baseArgsPairList: List<Pair<String, String>>,
        editConstraintListAdapter: EditConstraintListAdapter?,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
        val funcTypeAndMethodList =
            funcTypeDotMethod.split(funcTypeAndMethodSeparatorDot)
        val funcTypeStr = funcTypeAndMethodList.first()
        val funcType = FuncType.entries.firstOrNull {
            it.key == funcTypeStr
        } ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                funcTypeStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Irregular func name: ${spanFuncTypeStr}")
        }
        val methodName = funcTypeAndMethodList.getOrNull(1)
            ?: let {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    funcTypeStr
                )
                return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}")
            }
        return when(funcType){
            FuncType.FILE_SYSTEMS ->
                FileSystemsForSettingHandler.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.TOAST -> {
                ToastForSetting.handle(
                    fragment?.context,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            }
            FuncType.DEBUG -> {
                DebugForSetting.handle(
                    fragment?.context,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            }
            FuncType.EXIT ->
                ExitForSetting.handle(
                    funcTypeStr,
                    methodName
                )
            FuncType.PATH ->
                PathForSettingHandler.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.LOCAL_DATETIME ->
                LocalDatetimeForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.TSV_TOOL ->
                TsvToolForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.SHELL ->
                ShellToolManagerForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    busyboxExecutor,
//                    varNameToValueStrMap,
                )
            FuncType.EDIT ->
                EditForSetting.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    editConstraintListAdapter,
//                    varNameToValueStrMap,
                )
            FuncType.CULC ->
                MathCulcForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.COLOR -> {
                ColorForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            }
            FuncType.LIST ->
                ListForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            FuncType.RND ->
                RndForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.SYSTEM_INFO ->
                SystemInfoForSetting.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.EVAL ->
                EvalForSetting.handle(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMapSrc,
                    busyboxExecutor,
                    topVarNameToValueStrMap,
                    keyToSubKeyConWhere,
                    editConstraintListAdapter,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                )
        }

    }

    private enum class FuncType(
        val key: String,
    ) {
        FILE_SYSTEMS("fileSystems"),
        TOAST("toast"),
        DEBUG("debug"),
        EXIT("exit"),
        PATH("path"),
        LOCAL_DATETIME("localDatetime"),
        TSV_TOOL("tsvTool"),
        SHELL("shell"),
        EDIT("edit"),
        CULC("culc"),
        COLOR("color"),
        LIST("list"),
        RND("rnd"),
        SYSTEM_INFO("systemInfo"),
        EVAL("eval")
    }

}

object EvalForSetting {

    private const val joinStrIndex = 4

    suspend fun handle(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        topVarNameToValueStrMap: Map<String, String?>?,
        keyToSubKeyConWhere: String,
        editConstraintListAdapterArg: EditConstraintListAdapter? = null,
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameToTypeList,
            argsPairList,
        )?.let { argsCheckErr ->
            return null to argsCheckErr
        }
        val argsList = argsPairList.map {
            it.second
        }
        return withContext(Dispatchers.Main) {
            when (methodNameClass) {
                MethodNameClass.MAP -> {
                    val inputCon = argsList.get(0)
                    val separator = argsList.get(1)
                    val elVarName = argsList.get(2)
                    val settingActionCon = argsList.get(3)
                    val joinStr = argsList.get(joinStrIndex)
                    MapOperator.map(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        topVarNameToValueStrMap,
                        keyToSubKeyConWhere,
                        editConstraintListAdapterArg,
                        inputCon,
                        separator,
                        elVarName,
                        settingActionCon,
                        joinStr,
                    )
                }
            }
        }
    }

    private object MapOperator {

        const val returnTopAcVarNameMacro = SettingActionKeyManager.returnTopAcVarNameMacro

        suspend fun map(
            fragment: Fragment?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            topVarNameToValueStrMap: Map<String, String?>?,
            keyToSubKeyConWhere: String,
            editConstraintListAdapterArg: EditConstraintListAdapter? = null,
            inputCon: String,
            separator: String,
            elVarName: String,
            settingActionCon: String,
            joinStr: String,
        ): Pair<
                Pair<
                        String?,
                        SettingActionKeyManager.BreakSignal?
                        >?,
                FuncCheckerForSetting.FuncCheckErr?
                > {
            val info = listOf(
                "inputCon ${inputCon}",
                "separator: ${separator}",
                "elVarName: ${elVarName}",
                "settingActionCon $settingActionCon",
                "joinStr: ${joinStr}",
            ).joinToString(",")
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "leval_func.txt").absolutePath,
//                listOf(
//                    "separator: ${separator}",
//                    "inputCon: ${inputCon}",
//                    "inputCon.split(separator): ${inputCon.split(separator)}"
//                ).joinToString("\n\n")
//            )
            return withContext(Dispatchers.IO) {
                val evalMapJobList = when(separator == defaultMacroStr
                ) {
                    true -> listOf(inputCon)
                    else -> inputCon.split(separator)
                }.map {
                    el ->
                    async {
                        val curTopVarNameToValueStrMap = (topVarNameToValueStrMap ?: emptyMap()) + mapOf(
                            elVarName to el,
                        )
                        val outputVarNameToValueStrMap = SettingActionManager().exec(
                            fragment,
                            fannelInfoMap,
                            setReplaceVariableMapSrc,
                            busyboxExecutor,
                            SettingActionAsyncCoroutine(),
                            curTopVarNameToValueStrMap.map {
                                it.key
                            },
                            curTopVarNameToValueStrMap,
                            settingActionCon,
                            "eval: elVarName: ${elVarName}, ${keyToSubKeyConWhere}",
                            editConstraintListAdapterArg,
                            returnTopAcVarNameMacro
                        )
                        outputVarNameToValueStrMap.get(returnTopAcVarNameMacro)
                    }
                }
                val resultStrList = evalMapJobList.awaitAll()
                val isJoin = joinStr != defaultMacroStr
                if(!isJoin){
                   return@withContext null to null
                }
                val resultStr = when(
                    resultStrList.any {
                        result -> result == null
                    }
                ){
                    true -> {
                        val spanJoinStrArgName =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                mapArgsNameToTypeList.get(joinStrIndex).first
                            )
                        val spanDefaultMacroStr =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                defaultMacroStr
                            )
                        val spanInfo = let {
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errBrown,
                                info
                            )
                        }
                        return@withContext  Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL,
                        ) to  FuncCheckerForSetting.FuncCheckErr(
                            "When ${spanJoinStrArgName} is not ${spanDefaultMacroStr}, result must exist: ${spanInfo}, ${keyToSubKeyConWhere}"
                        )
                    }
                    else -> resultStrList.joinToString(joinStr)
                }
                Pair(
                    resultStr,
                    null,
                ) to null
            }
        }

        private const val defaultMacroStr = "NULL"
    }


    enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting.ArgType>>,
    ) {
        MAP("map", mapArgsNameToTypeList),
    }

    private val mapArgsNameToTypeList = listOf(
        Pair(
            "inputCon",
            FuncCheckerForSetting.ArgType.STRING,
        ),
        Pair(
            "separator",
            FuncCheckerForSetting.ArgType.STRING
        ),
        Pair(
            "elVarName",
            FuncCheckerForSetting.ArgType.STRING,
        ),
        Pair(
            "action",
            FuncCheckerForSetting.ArgType.STRING
        ),
        Pair(
            "joinStr",
            FuncCheckerForSetting.ArgType.STRING
        ),
    )
}
