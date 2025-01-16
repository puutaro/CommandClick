package com.puutaro.commandclick.proccess.edit.setting_action

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
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
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ReplaceForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.RndForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.SettingFuncTool
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ShellToolManagerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.SystemInfoForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ToastForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.TsvToolForSetting
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
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
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.enums.EnumEntries

class SettingActionManager {
    companion object {

        private const val awaitWaitTimes =
            SettingActionKeyManager.AwaitManager.awaitWaitTimes
        private val globalVarNameRegex =
            SettingActionKeyManager.globalVarNameRegex

        fun init(){
            SettingActionImportManager.BeforeActionImportMapManager.init()
        }
        private val mapRoopKeyUnit =
            SettingActionKeyManager.LoopKeyManager.mapRoopKeyUnit
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
        val loopClasses = settingActionExecutor.makeResultLoopKeyToVarNameValueMap(
            topVarNameToValueStrMap,
            settingActionAsyncCoroutine,
            editConstraintListAdapterArg,
//            privateLoopKeyVarNameValueStrMap,
//            loopKeyToAsyncDeferredVarNameValueStrMap,
            keyToSubKeyConList,
            mapRoopKeyUnit,
            null,
            keyToSubKeyConWhere,
            topAcSVarName,
            null,
            null,
        )
        val returnLoopKeyToVarNameValueStrMap = loopClasses?.first
        return SettingActionKeyManager.LoopKeyManager.getResultLoopKeyToVarNameValueMap(
            returnLoopKeyToVarNameValueStrMap,
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
//        private val privateLoopKeyVarNameValueStrMap =
//    SettingActionData.PrivateLoopKeyVarNameValueStrMap()
//        private val loopKeyToAsyncDeferredVarNameValueStrMap =
//            SettingActionData.LoopKeyToAsyncDeferredVarNameValueStrMap()
        private var settingActionExitManager = SettingActionData.SettingActionExitManager()
        private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
        private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
        private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
        private val returnTopAcVarNameMacro = SettingActionKeyManager.returnTopAcVarNameMacro
        private val mapRoopKeyUnit = SettingActionKeyManager.LoopKeyManager.mapRoopKeyUnit


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
        ): Pair<
            SettingActionData.LoopKeyToVarNameValueStrMap?,
            SettingActionData.PrivateLoopKeyVarNameValueStrMap,
//            SettingActionData.LoopKeyToAsyncDeferredVarNameValueStrMap,
        >? {
            val fragment = fragmentRef.get()
                ?: return null
            val context = fragment.context
                ?: return null
            val loopKeyToVarNameValueStrMap =
                SettingActionData.LoopKeyToVarNameValueStrMap()
            val privateLoopKeyVarNameValueStrMap =
                SettingActionData.PrivateLoopKeyVarNameValueStrMap()
            val loopKeyToAsyncDeferredVarNameValueStrMap =
                SettingActionData.LoopKeyToAsyncDeferredVarNameValueStrMap()
//            loopKeyToVarNameValueStrMap
//                ?.initPrivateLoopKeyVarNameValueStrMapMutex(curMapLoopKey)
//            privateLoopKeyVarNameValueStrMap
//                .initPrivateLoopKeyVarNameValueStrMapMutex(curMapLoopKey)
            if(
                keyToSubKeyConList.isNullOrEmpty()
            ) return null
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
            if(isErr) {
                settingActionExitManager.setExit()
                return null
            }
            keyToSubKeyConList.forEach { keyToSubKeyConSrc ->
                if (
                    settingActionExitManager.get()
                ) return null
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sMap.txt").absolutePath,
//                    listOf(
//                        "curMapLoopKey: ${curMapLoopKey}",
//                        "topAcSVarName: ${topAcSVarName}",
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
//                        val curImportedVarNameToValueStrMap =
//                            renewalVarNameToImportConToVarNameToValueStrMap.third
                        val importVarNameToValueStrMap =
                            importPathAndRenewalVarNameToImportConToVarNameValueStrMapToImportRepMap.third
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
//                                        val varNameToBitmapMap =
//                                            makeVarNameToValueStrMap(
//                                                curMapLoopKey,
//                                                topVarNameToValueStrMap,
//                                                importedVarNameToValueStrMap,
//                                                loopKeyToVarNameValueStrMap,
//                                                privateLoopKeyVarNameValueStrMap,
//                                                curImportedVarNameToValueStrMap,
//                                                null,
//                                            )
                                        val curStringVarKeyList =
                                            ((topVarNameToValueStrMap ?: emptyMap()) + importVarNameToValueStrMap).map {
                                                it.key
                                            }
                                        val loopMapClasses = makeResultLoopKeyToVarNameValueMap(
                                            topVarNameToValueStrMap,
                                            settingActionAsyncCoroutine,
                                            editConstraintListAdapterArg,
                                            importedKeyToSubKeyConList,
                                            SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey),
                                            (originImportPathList?: emptyList()) + listOf(importPath),
                                            "${importPath} by imported",
                                            acIVarName,
                                            importVarNameToValueStrMap,
//                                            varNameToBitmapMap,
                                            curStringVarKeyList
                                        )
                                        val downLoopKeyVarNameValueStrMap = loopMapClasses?.first
                                        val downPrivateLoopKeyVarNameValueStrMap = loopMapClasses?.second
//                                        if(
//                                            !globalVarNameRegex.matches(acIVarName)
//                                        ) return@async null
                                        val proposalRenewalVarNameSrcInnerMapValueStr =
                                            downPrivateLoopKeyVarNameValueStrMap?.getAsyncVarNameToValueStr(
                                                curMapLoopKey,
                                            )?.get(
                                                acIVarName
                                            )
//                                        val removedLoopKey =
//                                            SettingActionKeyManager.LoopKeyManager.removeLoopKey(curMapLoopKey)
                                        if (
                                            proposalRenewalVarNameSrcInnerMapValueStr != null
                                        ) {
                                            privateLoopKeyVarNameValueStrMap.put(
                                                curMapLoopKey,
                                                acIVarName,
                                                proposalRenewalVarNameSrcInnerMapValueStr
                                            )
//                                            privateLoopKeyVarNameValueStrMap.put(
//                                                removedLoopKey,
//                                                topAcSVarName,
//                                                proposalRenewalVarNameSrcInnerMapValueStr
//                                            )
                                        }

//                                        FileSystems.updateFile(
//                                            File(
//                                                UsePath.cmdclickDefaultSDebugAppDirPath,
//                                                "image_async_acImport01_${acIVarName}.txt"
//                                            ).absolutePath,
//                                            listOf(
//                                                "proposalRenewalVarNameSrcInnerMapValueStr: ${proposalRenewalVarNameSrcInnerMapValueStr}",
//                                                "acIVarName:${acIVarName}",
//                                                "topAcSVarName:${topAcSVarName}",
//                                                "importedVarNameToValueStrMap: ${importedVarNameToValueStrMap}",
//                                                "topVarNameToValueStrMap: ${topVarNameToValueStrMap}",
//                                                "curMapLoopKey: ${curMapLoopKey}",
//                                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                                    curMapLoopKey
//                                                )}",
//                                                "curMapLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                                    curMapLoopKey
//                                                )}",
//                                                "curMapLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                                    curMapLoopKey
//                                                )}",
//                                                "addLoopKey(curMapLoopKey): ${SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey)}",
//                                                "addLoopKey(curMapLoopKey) privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                                    SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey)
//                                                )}",
//                                                "addLoopKey(curMapLoopKey) loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                                    SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey)
//                                                )}",
//                                                "mapRoopKeyUnit:${mapRoopKeyUnit}",
//                                                "removed loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                                    mapRoopKeyUnit
//                                                )}",
//                                                "removedLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                                    mapRoopKeyUnit
//                                                )}",
//                                                "removedLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                                    mapRoopKeyUnit
//                                                )}",
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                                            ).joinToString("\n")  + "\n\n========\n\n"
//                                        )
//                                        if(
//                                            topAcSVarName.isNullOrEmpty()
//                                            || topAcSVarName.startsWith(escapeRunPrefix)
//                                        ) return@async null
                                        if(
//                                            !globalVarNameRegex.matches(topAcSVarName)
                                            !globalVarNameRegex.matches(curMapLoopKey)
                                            || mapRoopKeyUnit != curMapLoopKey
                                        ) return@async null
                                        val proposalRenewalVarNameSrcMapValueStr =
                                            downLoopKeyVarNameValueStrMap?.getAsyncVarNameToValueStr(
                                                curMapLoopKey,
                                            )?.get(acIVarName)
                                        if (
                                            proposalRenewalVarNameSrcMapValueStr != null
                                        ) {
                                            loopKeyToVarNameValueStrMap.put(
                                                mapRoopKeyUnit,
                                                acIVarName,
                                                proposalRenewalVarNameSrcMapValueStr
                                            )
                                        }
//                                        if(
//                                            !globalVarNameRegex.matches(topAcSVarName)
//                                            || mapRoopKeyUnit != removedLoopKey
//                                        ) return@async null
//                                        val proposalRenewalVarNameSrcMapValueStr =
//                                            downLoopKeyVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                                curMapLoopKey,
//                                            )?.get(acIVarName)
//                                        if (
//                                            proposalRenewalVarNameSrcMapValueStr != null
//                                        ) {
//                                            loopKeyToVarNameValueStrMap?.put(
//                                                removedLoopKey,
//                                                topAcSVarName,
//                                                proposalRenewalVarNameSrcMapValueStr
//                                            )
//                                        }
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
                        val curStringVarKeyList = ((topVarNameToValueStrMap ?: emptyMap()) + importVarNameToValueStrMap).map {
                            it.key
                        }
                        val loopMapClasses = makeResultLoopKeyToVarNameValueMap(
                            topVarNameToValueStrMap,
                            settingActionAsyncCoroutine,
                            editConstraintListAdapterArg,
                            importedKeyToSubKeyConList,
                            SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey),
                            (originImportPathList ?: emptyList()) + listOf(importPath),
                            "${importPath} by imported",
                            acIVarName,
                            importVarNameToValueStrMap,
//                            varNameToBitmapMap,
                            curStringVarKeyList
                        )
                        val downLoopKeyVarNameValueStrMap = loopMapClasses?.first
                        val downPrivateLoopKeyVarNameValueStrMap = loopMapClasses?.second
//                        val downLoopKeyToAsyncDeferredVarNameValueStrMap = loopMapClasses?.third
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
                        val proposalRenewalVarNameSrcInnerMapValueStr =
                            downPrivateLoopKeyVarNameValueStrMap?.getAsyncVarNameToValueStr(
                                curMapLoopKey,
//                                addLoopKey(curMapLoopKey)
                            )?.get(
                                acIVarName
                            )
//                        if(
//                            !globalVarNameRegex.matches(acIVarName)
//                        ) return@forEach
                        if (
                            proposalRenewalVarNameSrcInnerMapValueStr != null
                        ) {
                            privateLoopKeyVarNameValueStrMap.put(
                                curMapLoopKey,
                                acIVarName,
                                proposalRenewalVarNameSrcInnerMapValueStr
                            )
//                            privateLoopKeyVarNameValueStrMap.put(
//                                removedLoopKey,
//                                topAcSVarName,
//                                proposalRenewalVarNameSrcInnerMapValueStr
//                            )
                        }
                        val removedLoopKey =
                            SettingActionKeyManager.LoopKeyManager.removeLoopKey(curMapLoopKey)
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultSDebugAppDirPath,
//                                "image_acImport01_${acIVarName}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "proposalRenewalVarNameSrcInnerMapValueStr: ${proposalRenewalVarNameSrcInnerMapValueStr}",
//                                "acIVarName:${acIVarName}",
//                                "topAcSVarName:${topAcSVarName}",
//                                "curMapLoopKey: ${curMapLoopKey}",
//                                "importedVarNameToValueStrMap: ${importedVarNameToValueStrMap}",
//                                "topVarNameToValueStrMap: ${topVarNameToValueStrMap}",
//                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "addLoopKey(curMapLoopKey): ${SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey)}",
//                                "addLoopKey(curMapLoopKey) privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey)
//                                )}",
//                                "addLoopKey(curMapLoopKey) loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                    SettingActionKeyManager.LoopKeyManager.addLoopKey(curMapLoopKey)
//                                )}",
//                                "removedLoopKey:${removedLoopKey}",
//                                "removed loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                    removedLoopKey
//                                )}",
////                    "varNameToBitmap: ${varNameToBitmap.first}",
////                    "bitmapWidth: ${varNameToBitmap.second?.width}",
////                    "bitmapHeight: ${varNameToBitmap.second?.height}",
//                            ).joinToString("\n")  + "\n\n========\n\n"
//                        )
//                        if (
//                            topAcSVarName.isNullOrEmpty()
//                            || topAcSVarName.startsWith(escapeRunPrefix)
//                        ) return@forEach
                        if(
//                            !globalVarNameRegex.matches(topAcSVarName)
                            !globalVarNameRegex.matches(acIVarName)
                            || mapRoopKeyUnit != curMapLoopKey
//                            || mapRoopKeyUnit != removedLoopKey
                        ) return@forEach
                        val proposalRenewalVarNameSrcMapValueStr =
                            downLoopKeyVarNameValueStrMap?.getAsyncVarNameToValueStr(
                            curMapLoopKey,
                        )?.get(acIVarName)
                        if (
                            proposalRenewalVarNameSrcMapValueStr != null
                        ) {
                            loopKeyToVarNameValueStrMap.put(
                                mapRoopKeyUnit,
                                acIVarName,
                                proposalRenewalVarNameSrcMapValueStr
                            )
//                            loopKeyToVarNameValueStrMap?.put(
//                                removedLoopKey,
//                                topAcSVarName,
//                                proposalRenewalVarNameSrcMapValueStr
//                            )
                        }
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultSDebugAppDirPath,
//                                "image_acImport_${acIVarName}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "acIVarName:${acIVarName}",
//                                "topAcSVarName:${topAcSVarName}",
//                                "curMapLoopKey: ${curMapLoopKey}",
//                                "curMapLoopKey: loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "curMapLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                    curMapLoopKey
//                                )}",
//                                "removedLoopKey:${removedLoopKey}",
//                                "removed loopKeyToAsyncDeferredVarNameBitmapMap: ${loopKeyToAsyncDeferredVarNameValueStrMap.getAsyncVarNameToValueStrAndExitSignal(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: privateLoopKeyVarNameValueStrMap: ${privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                    removedLoopKey
//                                )}",
//                                "removedLoopKey: loopKeyToVarNameValueStrMap: ${loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
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
//                                val removedLoopKey = SettingActionKeyManager.LoopKeyManager.removeLoopKey(curMapLoopKey)
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultSDebugAppDirPath,
//                                        "image_async_00_${settingVarName}.txt"
//                                    ).absolutePath,
//                                    listOf(
//                                        "keyToSubKeyConList: ${keyToSubKeyConList}",
////                                        "imageVarKey: ${imageVarKey}",
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
//                                        "topAcSVarName: ${topAcSVarName}",
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
//                                    topAcSVarName.isNullOrEmpty()
//                                ) return@launch
//                                if (
//                                    topAcSVarName.startsWith(escapeRunPrefix)
//                                ) return@launch
//                                loopKeyToAsyncDeferredVarNameValueStrMap.put(
//                                    removedLoopKey,
//                                    topAcSVarName,
//                                    deferred
//                                )
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultSDebugAppDirPath,
//                                        "image_async_${settingVarName}.txt"
//                                    ).absolutePath,
//                                    listOf(
//                                        "keyToSubKeyConList: ${keyToSubKeyConList}",
////                                        "imageVarKey: ${imageVarKey}",
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
//                                            loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                                curMapLoopKey
//                                            )}",
//                                        "loopKeyToVarNameBitmapMap: ${
//                                            loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
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
                                return Pair(
                                    loopKeyToVarNameValueStrMap,
                                    privateLoopKeyVarNameValueStrMap,
//                                    loopKeyToAsyncDeferredVarNameValueStrMap,
                                )
                            }
                            val varNameToValueStr = varNameToStrValueAndExitSignal.first
                            privateLoopKeyVarNameValueStrMap.put(
                                curMapLoopKey,
                                varNameToValueStr.first,
                                varNameToValueStr.second
                            )
                            if(
                                globalVarNameRegex.matches(varNameToValueStr.first)
                                && curMapLoopKey == mapRoopKeyUnit
                                ) {
                                loopKeyToVarNameValueStrMap.put(
                                    mapRoopKeyUnit,
                                    varNameToValueStr.first,
                                    varNameToValueStr.second
                                )
                            }
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultSDebugAppDirPath,
//                                    "image_${settingVarName}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
////                                    "imageVarKey: ${imageVarKey}",
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "settingVarName: $settingVarName",
//                                    "isAsync: ${isAsync}",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "mapRoopKeyUnit: ${mapRoopKeyUnit}",
//                                    "varName: ${varNameToValueStr.first}",
//                                    "topAcIVarName: ${topAcSVarName}",
////                                    "varNameForPut: ${varNameForPut}",
//                                    "curMapLoopKey privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )}",
//                                    "mapRoopKeyUnit privateLoopKeyVarNameBitmapMap: ${
//                                        privateLoopKeyVarNameValueStrMap.getAsyncVarNameToValueStr(
//                                            mapRoopKeyUnit
//                                        )}",
//                                    "curMapLoopKey loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                            curMapLoopKey
//                                        )}",
//                                    "mapRoopKeyUnit loopKeyToVarNameBitmapMap: ${
//                                        loopKeyToVarNameValueStrMap?.getAsyncVarNameToValueStr(
//                                            mapRoopKeyUnit
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
                                return Pair(
                                    loopKeyToVarNameValueStrMap,
                                    privateLoopKeyVarNameValueStrMap,
//                                    loopKeyToAsyncDeferredVarNameValueStrMap,
                                )
                            }
                            val removedLoopKey =
                                SettingActionKeyManager.LoopKeyManager.removeLoopKey(curMapLoopKey)
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
                                    return Pair(
                                        loopKeyToVarNameValueStrMap,
                                        privateLoopKeyVarNameValueStrMap,
//                                        loopKeyToAsyncDeferredVarNameValueStrMap,
                                    )
                                }
                                SettingActionKeyManager.BreakSignal.RETURN_SIGNAL -> {
                                    return Pair(
                                        loopKeyToVarNameValueStrMap,
                                        privateLoopKeyVarNameValueStrMap,
//                                        loopKeyToAsyncDeferredVarNameValueStrMap,
                                    )
                                }
                                else -> {}
                            }
                        }

                    }
                }
            }
            return Pair(
                loopKeyToVarNameValueStrMap,
                privateLoopKeyVarNameValueStrMap,
//                loopKeyToAsyncDeferredVarNameValueStrMap,
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
                loopKeyToVarNameValueStrMapClass: SettingActionData.LoopKeyToVarNameValueStrMap?,
                importedVarNameToValueStrMap: Map<String, String?>?,
                settingActionExitManager: SettingActionData.SettingActionExitManager,
                settingVarName: String,
                renewalVarName: String?,
                keyToSubKeyConWhere: String,
            ): Pair<Pair<String, String?>, SettingActionKeyManager.BreakSignal?>? {
                val context = fragment.context
                var itPronounValueStrToBreakSignal: Pair<
                        String?,
                        SettingActionKeyManager.BreakSignal?
                        >? = null
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
                                    settingActionExitManager.setExit()
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
                                ) {
                                    settingActionExitManager.setExit()
                                    return null
                                }
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
                            val argsPairListBeforeBsEscape = CmdClickMap.createMap(
                                mainSubKeyMapSrc.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            ).let {
                                pair ->
                                pair.filter {
                                    it.first.isNotEmpty()
                                }
                            }
                            val argsPairList = SettingFuncTool.makeArgsPairList(
                                argsPairListBeforeBsEscape,
                                varNameToValueStrMap
                            )
                            val quoteStr = """"aaa\"""""
//                            if(funcTypeDotMethod.contains("eval")) {
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultAppDirPath,
//                                        "limport.txt"
//                                    ).absolutePath,
//                                    listOf(
////                                    "keyToSubKeyCon: ${keyToSubKeyCon}",
////                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
////                                    "subKeyCon: ${subKeyCon}",
//                                        "funcTypeDotMethod: ${funcTypeDotMethod}",
//                                        "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                        "mainSubKeyMapSrc: ${mainSubKeyMapSrc}",
//                                        "argsPairList: ${argsPairList}",
//                                        "quoteStr: ${quoteStr}",
//                                        "BackslashTool.toNormal(quoteStr): ${
//                                            BackslashTool.toNormal(
//                                                quoteStr
//                                            )
//                                        }",
//                                    ).joinToString("\n") + "\n\n==========\n\n"
//                                )
//                            }
                            val resultValueStrToExitMacroAndCheckErr = SettingFuncManager.handle(
                                fragment,
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                busyboxExecutor,
                                varNameToValueStrMap,
                                keyToSubKeyConWhere,
                                funcTypeDotMethod,
                                argsPairList,
                                argsPairListBeforeBsEscape,
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
                                settingActionExitManager.setExit()
                                return null
                            }
                            val resultValueStrToExitMacro =
                                resultValueStrToExitMacroAndCheckErr?.first
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
                                ) {
                                    settingActionExitManager.setExit()
                                    return null
                                }
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
        argsPairListBeforeBsEscape: List<Pair<String, String>>,
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
            FuncType.FILE ->
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
                    argsPairListBeforeBsEscape,
                    busyboxExecutor,
                    topVarNameToValueStrMap,
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
            FuncType.REPLACE ->
                ReplaceForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                )

        }

    }

    private enum class FuncType(
        val key: String,
    ) {
        FILE("file"),
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
        EVAL("eval"),
        REPLACE("replace"),
    }

}

object EvalForSetting {

    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

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

        val args = methodNameClass.args
        return withContext(Dispatchers.Main) {
            when (args) {
                is EvalArgClass.EvalEvalArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val inputCon = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.inputKeyToDefaultValueStr,
                        where
                    ).let { inputConToErr ->
                            val funcErr = inputConToErr.second
                                ?: return@let inputConToErr.first
                            return@withContext Pair(
                                null,
                                SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) to funcErr
                        }
                    val fieldVarPrefix = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.fieldVarPrefixKeyToDefaultValueStr,
                        where
                    ).let { fieldVarPrefixToErr ->
                        val funcErr = fieldVarPrefixToErr.second
                            ?: return@let fieldVarPrefixToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
//                    val elVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                        mapArgMapList,
//                        args.elVarNameKeyToDefaultValueStr,
//                        where
//                    ).let { elVarNameToErr ->
//                        val funcErr = elVarNameToErr.second
//                            ?: return@let elVarNameToErr.first
//                        return@withContext Pair(
//                            null,
//                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
//                        ) to funcErr
//                    }
                    val settingActionCon = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.actionKeyToDefaultValueStr,
                        where
                    ).let { settingActionConToErr ->
                        val funcErr = settingActionConToErr.second
                            ?: return@let settingActionConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val indexVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.indexVarNameKeyToDefaultValueStr,
                        where
                    ).let { indexVarNameToErr ->
                        val funcErr = indexVarNameToErr.second
                            ?: return@let indexVarNameToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.separatorKeyToDefaultValueStr,
                        where
                    ).let { inputConToErr ->
                        val funcErr = inputConToErr.second
                            ?: return@let inputConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val joinStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.joinStrKeyToDefaultValueStr,
                        where
                    ).let { joinStrToErr ->
                        val funcErr = joinStrToErr.second
                            ?: return@let joinStrToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val semaphoreLimit = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                        mapArgMapList,
                        args.semaphoreKeyToDefaultValueStr,
                        where
                    ).let { semaphoreToErr ->
                        val funcErr = semaphoreToErr.second
                            ?: return@let semaphoreToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val delimiter = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.delimiterKeyToDefaultValueStr,
                        where
                    ).let { delimiterToErr ->
                        val funcErr = delimiterToErr.second
                            ?: return@let delimiterToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val alreadyUseVarNameList = listOf(
//                        elVarName,
                        fieldVarPrefix,
                        indexVarName,
                    ).filter {
                        it != defaultNullMacroStr
                    }
                    val isDuplicate =
                        let {
                            val sortedAlreadyUseVarNameList =
                                alreadyUseVarNameList.sortedBy { it }
                            sortedAlreadyUseVarNameList !=
                                    sortedAlreadyUseVarNameList.distinct()
                        }
                    if(isDuplicate){
//                        val spanElVarNameKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.ligthBlue,
//                            args.elVarNameKeyToDefaultValueStr.first
//                        )
                        val spanIndexVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            args.indexVarNameKeyToDefaultValueStr.first
                        )
                        val spanFieldVarPrefix = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            args.fieldVarPrefixKeyToDefaultValueStr.first
                        )
                        val alreadyUseVarListCon = alreadyUseVarNameList.joinToString(", ")
                        val spanAlreadyUseVarListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            alreadyUseVarListCon
                        )
                        val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errBrown,
                            where
                        )
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to  FuncCheckerForSetting. FuncCheckErr(
                            "Must be different from ${spanIndexVarName} and ${spanFieldVarPrefix}: ${spanAlreadyUseVarListCon}, ${spanWhere} "
                        )
                    }
                    MapOperator.map(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        topVarNameToValueStrMap,
                        keyToSubKeyConWhere,
                        editConstraintListAdapterArg,
                        args,
                        inputCon,
                        separator,
                        indexVarName,
//                        elVarName,
                        settingActionCon,
                        joinStr,
                        semaphoreLimit,
                        delimiter,
                        fieldVarPrefix,
                    )
                }
            }
        }
    }

    private object MapOperator {

        private const val returnTopAcVarNameMacro =
            SettingActionKeyManager.returnTopAcVarNameMacro
        private val defaultNullMacroStr =
            FuncCheckerForSetting.defaultNullMacroStr
        private val itPronoun = SettingActionKeyManager.ValueStrVar.itPronoun

        suspend fun map(
            fragment: Fragment?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            topVarNameToValueStrMapSrc: Map<String, String?>?,
            keyToSubKeyConWhere: String,
            editConstraintListAdapterArg: EditConstraintListAdapter? = null,
            args: EvalArgClass.EvalEvalArgs,
            inputCon: String,
            separator: String,
            indexVarName: String,
//            elVarName: String,
            settingActionCon: String,
            joinStr: String,
            semaphoreLimit: Int,
            delimiter: String,
            fieldVarPrefix: String,
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
                "indexVarName: ${indexVarName}",
//                "elVarName: ${elVarName}",
                "settingActionCon $settingActionCon",
                "joinStr: ${joinStr}",
                "semaphoreLimit: ${semaphoreLimit}",
                "delimiter: ${delimiter}",
                "fieldVarPrefix: ${fieldVarPrefix}",
            ).joinToString(",")
            val topVarNameToValueStrMap = topVarNameToValueStrMapSrc?.filterKeys{
                it != itPronoun
            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "leval_func.txt").absolutePath,
//                listOf(
//                    "separator: ${separator}",
//                    "inputCon: ${inputCon}",
//                    "inputCon.split(separator): ${inputCon.split(separator)}"
//                ).joinToString("\n\n")
//            )
            return withContext(Dispatchers.IO) {
                val semaphore = when(semaphoreLimit > 0) {
                    false -> null
                    else -> Semaphore(semaphoreLimit)
                }
                val indexToResultStrJobList = when(
                    separator == defaultNullMacroStr
                ) {
                    true -> listOf(inputCon)
                    else -> inputCon.split(separator)
                }.mapIndexed {
                    index, el ->
                    async {
                        val fieldVarNameToValueStrMap = SettingFuncTool.FieldVarPrefix.makeFieldVarNameToValueStrList(
                            el,
                            delimiter,
                            fieldVarPrefix,
                        )?.map {
                                (fieldVarName, valueStr) ->
                            fieldVarName to valueStr
                        }?.toMap() ?: emptyMap()
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "leval_replace.txt").absolutePath,
//                            listOf(
//                                "fieldVarMarkToValueStrMap: ${fieldVarMarkToValueStrMap}",
//                            ).joinToString("\n\n")
//                        )
                        when(semaphore == null) {
                            true -> execAction(
                                fragment,
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                busyboxExecutor,
                                topVarNameToValueStrMap,
                                keyToSubKeyConWhere,
                                editConstraintListAdapterArg,
                                index,
                                indexVarName,
//                                el,
//                                elVarName,
                                settingActionCon,
                                fieldVarNameToValueStrMap,
                            )
                            else -> semaphore.withPermit {
                                execAction(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMapSrc,
                                    busyboxExecutor,
                                    topVarNameToValueStrMap,
                                    keyToSubKeyConWhere,
                                    editConstraintListAdapterArg,
                                    index,
                                    indexVarName,
//                                    el,
//                                    elVarName,
                                    settingActionCon,
                                    fieldVarNameToValueStrMap,
                                )
                            }
                        }
                    }
                }
                val resultStrList = indexToResultStrJobList.awaitAll().sortedBy {
                    indexToResultStr ->
                    indexToResultStr.first
                }.map {
                    indexToResultStr ->
                    indexToResultStr.second
                }
                val isJoin =
                    joinStr != defaultNullMacroStr
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
                                args.joinStrKeyToDefaultValueStr.first,
                            )
                        val spanDefaultMacroStr =
                            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                defaultNullMacroStr
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

        private suspend fun execAction(
            fragment: Fragment?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            topVarNameToValueStrMap: Map<String, String?>?,
            keyToSubKeyConWhere: String,
            editConstraintListAdapterArg: EditConstraintListAdapter? = null,
            index: Int,
            indexVarName: String,
//            el: String,
//            elVarName: String,
            settingActionCon: String,
            fieldVarMarkToValueStrMap: Map<String, String>,
        ): Pair<Int, String?> {
            val curTopVarNameToValueStrMap = (topVarNameToValueStrMap ?: emptyMap()) + mapOf(
//                elVarName to el,
                indexVarName to index.toString(),
            ) + fieldVarMarkToValueStrMap
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
                "eval: indexVarName: ${indexVarName}, ${keyToSubKeyConWhere}",
                editConstraintListAdapterArg,
                returnTopAcVarNameMacro
            )
            return index to outputVarNameToValueStrMap.get(returnTopAcVarNameMacro)
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: EvalArgClass
    ) {
        MAP("map", EvalArgClass.EvalEvalArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class EvalArgClass {
        data object EvalEvalArgs : EvalArgClass(), ArgType {
            override val entries = MapEnumArgs.entries
            val inputKeyToDefaultValueStr = Pair(
                MapEnumArgs.INPUT.key,
                MapEnumArgs.INPUT.defaultValueStr
            )
//            val elVarNameKeyToDefaultValueStr = Pair(
//                MapEnumArgs.EL_VAR_NAME.key,
//                MapEnumArgs.EL_VAR_NAME.defaultValueStr
//            )
            val fieldVarPrefixKeyToDefaultValueStr = Pair(
                MapEnumArgs.FIELD_VAR_PREFIX.key,
                MapEnumArgs.FIELD_VAR_PREFIX.defaultValueStr
            )
            val actionKeyToDefaultValueStr = Pair(
                MapEnumArgs.ACTION.key,
                MapEnumArgs.ACTION.defaultValueStr
            )
            val separatorKeyToDefaultValueStr = Pair(
                MapEnumArgs.SEPARATOR.key,
                MapEnumArgs.SEPARATOR.defaultValueStr
            )
            val indexVarNameKeyToDefaultValueStr = Pair(
                MapEnumArgs.INDEX_VAR_NAME.key,
                MapEnumArgs.INDEX_VAR_NAME.defaultValueStr
            )
            val joinStrKeyToDefaultValueStr = Pair(
                MapEnumArgs.JOIN_STR.key,
                MapEnumArgs.JOIN_STR.defaultValueStr
            )
            val semaphoreKeyToDefaultValueStr = Pair(
                MapEnumArgs.SEMAPHORE.key,
                MapEnumArgs.SEMAPHORE.defaultValueStr
            )
            val delimiterKeyToDefaultValueStr = Pair(
                MapEnumArgs.DELIMITER.key,
                MapEnumArgs.DELIMITER.defaultValueStr
            )

            enum class MapEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                INPUT("inputCon", String(), FuncCheckerForSetting.ArgType.STRING),
//                EL_VAR_NAME("elVarName", null, FuncCheckerForSetting.ArgType.STRING),
                FIELD_VAR_PREFIX("fieldVarPrefix", null, FuncCheckerForSetting.ArgType.STRING),
                ACTION("action", null, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                INDEX_VAR_NAME("indexVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEMAPHORE("semaphore", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                DELIMITER("delimiter", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}
