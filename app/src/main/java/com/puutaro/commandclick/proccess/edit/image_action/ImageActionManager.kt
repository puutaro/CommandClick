package com.puutaro.commandclick.proccess.edit.image_action

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager.LoopKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionData
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionErrLogger
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionImportManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionMapTool
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionReturnErrManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionVarErrManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageReturnExecutor
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ArbForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.BitmapArtForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.BlurForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ColorForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ConcatForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.CutForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.DebugForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.DelayForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ExitForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.FannelIconForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.FileSystemsForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.FlipForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.GradForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.IconForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ImageToolForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ImportDataForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.LineArtForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.LoopResultForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.MaskForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.MonoArtForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.OpacityForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.OverlayForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.RotateForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ShapeOverlayForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.SizeForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.StrPngForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ViewForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.WallForImageAction
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingArgsTool
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.IfErrManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.SettingFuncTool
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.StrToMapListTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import com.puutaro.commandclick.util.str.BackslashTool
import com.puutaro.commandclick.util.str.ImageVarMarkTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.VarMarkTool
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
import java.io.File
import kotlin.enums.EnumEntries

class ImageActionManager {
    companion object {

//        private val globalVarNameRegex = ImageActionKeyManager.globalVarNameRegex
        private const val awaitWaitTimes =
            ImageActionKeyManager.awaitWaitTimes
        private const val mapRoopKeyUnit =
            ImageActionKeyManager.LoopKeyManager.mapRoopKeyUnit

        fun init(){
            ImageActionImportManager.BeforeActionImportMapManager.init()
        }

        suspend fun dataInit(){
            ImportDataForImageAction.clearImportData()
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
            return keyToSubKeyConList.asSequence().map {
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
            }.toList()
        }
    }

    suspend fun exec(
        context: Context?,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        imageView: AppCompatImageView?,
        requestBuilder: RequestBuilder<Drawable>?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
        topLevelBitmapStrKeyList: List<String>?,
        topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
        keyToSubKeyCon: String?,
        keyToSubKeyConWhere: String,
        editConstraintListAdapterArg: EditConstraintListAdapter? = null,
        topAcIVarName: String? = null,
        forVarNameBitmapMap: ImageActionData.ForVarNameBitmapMap? = null
    ): Pair<
            Map<String, Bitmap?>,
            ImageActionKeyManager. BreakSignal?,
            > {
        val blankReturnPair = emptyMap<String, Bitmap?>() to null
        if(
            keyToSubKeyCon.isNullOrEmpty()
        ) return blankReturnPair
        val keyToSubKeyConList = makeImageActionKeyToSubKeyList(
            keyToSubKeyCon,
            setReplaceVariableMapSrc,
        )
        if(
            keyToSubKeyConList.isNullOrEmpty()
        ) return blankReturnPair
        val imageActionExitManager =
            ImageActionData.ImageActionExitManager()
        val imageActionExecutor = ImageActionExecutor(
            fannelInfoMap,
            setReplaceVariableMapSrc,
            busyboxExecutor,
            topLevelBitmapStrKeyList,
            imageActionExitManager,
        )
        val loopClasses = imageActionExecutor.makeResultLoopKeyToVarNameValueMap(
            context,
            imageView,
            requestBuilder,
            topVarNameToVarNameBitmapMap,
            imageActionAsyncCoroutine,
            editConstraintListAdapterArg,
            keyToSubKeyConList,
            mapRoopKeyUnit,
            null,
            keyToSubKeyConWhere,
            topAcIVarName,
            null,
            topLevelBitmapStrKeyList,
            forVarNameBitmapMap,
        )
        val varNameToBitmapMap = getResultLoopKeyToVarNameValueMap(
            loopClasses?.first
        )
        val signal = imageActionExitManager.get()
        return varNameToBitmapMap to signal
    }

    suspend fun getResultLoopKeyToVarNameValueMap(
        loopKeyToVarNameBitmapMap: ImageActionData.LoopKeyToVarNameBitmapMap?
    ): Map<String, Bitmap?> {
        return loopKeyToVarNameBitmapMap?.convertAsyncVarNameToBitmapToMap(LoopKeyManager.mapRoopKeyUnit)
            ?: emptyMap()
    }

    private fun makeSetRepValMap(
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
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
        keyToSubKeyCon: String?,
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
//        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: HashMap<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
        private val topLevelBitmapStrKeyList: List<String>?,
        private val imageActionExitManager: ImageActionData.ImageActionExitManager,
    ) {

//        private val loopKeyToVarNameBitmapMap = ImageActionData.LoopKeyToVarNameBitmapMap()
////        mutableMapOf<String, MutableMap<String, Bitmap?>>()
//        private val privateLoopKeyVarNameBitmapMap = ImageActionData.PrivateLoopKeyVarNameBitmapMap()
//        private val loopKeyToAsyncDeferredVarNameBitmapMap = ImageActionData.LoopKeyToAsyncDeferredVarNameBitmapMap()
        private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
        private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
        private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
        private val returnTopAcVarNameMacro = ImageActionKeyManager.returnTopAcVarNameMacro


        suspend fun makeResultLoopKeyToVarNameValueMap(
            context: Context?,
            imageView: AppCompatImageView?,
            requestBuilder: RequestBuilder<Drawable>?,
            topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
            imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
            editConstraintListAdapterArg: EditConstraintListAdapter?,
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            originImportPathList: List<String>?,
            keyToSubKeyConWhere: String,
            topAcIVarName: String?,
            importedVarNameToBitmapMap: Map<String, Bitmap?>?,
            bitmapVarKeyList: List<String>?,
            forVarNameBitmapMap: ImageActionData.ForVarNameBitmapMap?
        ): Pair<
                ImageActionData.LoopKeyToVarNameBitmapMap,
                ImageActionData.PrivateLoopKeyVarNameBitmapMap,
        >? {
//            val fragment = fragmentRef.get()
//                ?: return null
//            val context = fragment.context
//                ?: return null
            if(
                context == null
            ) return null
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
                val isIfHoldErrJob = async {
                    IfErrManager.isIfHoldErr(
                        context,
                        keyToSubKeyConList,
                        Pair(
                            ImageActionKeyManager.ImageSubKey.I_IF.key,
                            ImageActionKeyManager.ImageSubKey.I_IF_END.key,
                        ),
                        keyToSubKeyConWhere,
                    )
                }
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
                isIfHoldErrJob.await()
                        || isAwaitNotAsyncVarErrJob.await()
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
                imageActionExitManager.setExitSignal(ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL)
                return Pair(
                    loopKeyToVarNameBitmapMap,
                    privateLoopKeyVarNameBitmapMap,
                )
            }
            keyToSubKeyConList.forEach { keyToSubKeyConSrc ->
                if (
                    imageActionExitManager.isExit()
                ) return Pair(
                    loopKeyToVarNameBitmapMap,
                    privateLoopKeyVarNameBitmapMap,
                )
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
                            ImageActionImportManager.makeImportPathAndRenewalVarNameToImportCon(
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
                                            context,
                                            imageView,
                                            requestBuilder,
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
                                            null
                                        )
                                        val downLoopKeyVarNameBitmapMap = loopMapClasses?.first
                                        val downPrivateLoopKeyVarNameBitmapMap = loopMapClasses?.second
                                        val proposalRenewalVarNameSrcInnerMapBitmap =
                                            downPrivateLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmapFromMap(
                                                curMapLoopKey,
                                                acIVarName
                                            )
//                                                ?.get(
//                                                acIVarName
//                                            )
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
//                                            !globalVarNameRegex.matches(acIVarName)
                                            !VarMarkTool.matchesUpperAlphNumOrUnderscore(acIVarName)
                                            || mapRoopKeyUnit != curMapLoopKey
                                        ) return@async null
                                        val proposalRenewalVarNameSrcMapBitmap =
                                            downLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmapFromMap(
                                                curMapLoopKey,
                                                acIVarName,
                                            )
//                                                ?.get(acIVarName)
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
                                    imageActionAsyncCoroutine?.put(asyncJob)
                            }
                            return@forEach
                        }
                        val curBitmapVarKeyList =
                            ((topVarNameToVarNameBitmapMap ?: emptyMap()) +
                                    (importVarNameToBitmapMap ?: emptyMap())).map {
                                it.key
                            }
                        val loopMapClasses = makeResultLoopKeyToVarNameValueMap(
                            context,
                            imageView,
                            requestBuilder,
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
                            forVarNameBitmapMap,
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
                            downPrivateLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmapFromMap(
                                curMapLoopKey,
                                acIVarName,
                            )
//                                ?.get(
//                                acIVarName
//                            )
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
//                            !globalVarNameRegex.matches(acIVarName)
                            !VarMarkTool.matchesUpperAlphNumOrUnderscore(acIVarName)
                            || mapRoopKeyUnit != curMapLoopKey
                        ) return@forEach
                        val proposalRenewalVarNameSrcMapBitmap =
                            downLoopKeyVarNameBitmapMap?.getAsyncVarNameToBitmapFromMap(
                                curMapLoopKey,
                                acIVarName,
                            )
                                //?.get(acIVarName)
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
                                        context,
                                        fannelInfoMap,
                                        setReplaceVariableMapSrc,
                                        busyboxExecutor,
                                        mainSubKeyPairList,
                                        imageView,
                                        requestBuilder,
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
                                        forVarNameBitmapMap,
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
                                imageActionAsyncCoroutine?.put(asyncJob)
                            }
                            return@forEach
                        }
                        ImageVarExecutor().exec(
                            context,
                            fannelInfoMap,
                            setReplaceVariableMapSrc,
                            busyboxExecutor,
                            mainSubKeyPairList,
                            imageView,
                            requestBuilder,
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
                            forVarNameBitmapMap,
                            keyToSubKeyConWhere,
                        )?.let {
                            varNameToBitmapAndExitSignal ->
                            val exitSignalClass = varNameToBitmapAndExitSignal.second
                            if (
                                imageActionExitManager.isExitBySignal(exitSignalClass)
//                                exitSignalClass == ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) {
                                imageActionExitManager.setExitSignal(exitSignalClass)
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
//                                globalVarNameRegex.matches(varNameToBitmap.first)
                                VarMarkTool.matchesUpperAlphNumOrUnderscore(varNameToBitmap.first)
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
                                ImageActionMapTool.makeValueToBitmapMap(
                                    curMapLoopKey,
                                    topVarNameToVarNameBitmapMap,
                                    importedVarNameToBitmapMap,
                                    loopKeyToVarNameBitmapMap,
                                    privateLoopKeyVarNameBitmapMap,
                                    null,
                                    null,
                                )
                            varNameToBitmapMap.get (
                                ImageVarMarkTool.convertBitmapKey(
                                        bitmapVarMark
                                    )
                                )

                        }
                        ImageReturnExecutor().exec(
                            context,
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
                            if(
                                topAcIVarName == returnTopAcVarNameMacro
                                && varNameToBitmap != null
                            ){
                                forVarNameBitmapMap?.put(
//                                    removedLoopKey,
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
                                ImageActionKeyManager.BreakSignal.RETURN_SIGNAL -> {
                                    return Pair(
                                        loopKeyToVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                    )
                                }
                                else -> {
                                    imageActionExitManager.setExitSignal(breakSignalClass)
                                    return Pair(
                                        loopKeyToVarNameBitmapMap,
                                        privateLoopKeyVarNameBitmapMap,
                                    )
                                }
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

        private suspend fun makeMainSubKeyPairList(
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

            return withContext(Dispatchers.IO) {
                val indexAndKeyToSubKeyJobList = subKeyToConList.asSequence().mapIndexed { index, subKeyToCon ->
                    async {
                        val innerSubKeyName = subKeyToCon.first
                        val innerSubKeyClass =
                            ImageActionKeyManager.ImageSubKey.entries.firstOrNull {
                                it.key == innerSubKeyName
                            } ?: return@async index to Pair(String(), emptyMap())
                        val innerSubKeyCon = subKeyToCon.second
                        when (innerSubKeyClass) {
                            ImageActionKeyManager.ImageSubKey.I_IF_END,
                            ImageActionKeyManager.ImageSubKey.IMAGE_VAR,
                            ImageActionKeyManager.ImageSubKey.IMAGE_RETURN,
                            ImageActionKeyManager.ImageSubKey.AWAIT,
                            ImageActionKeyManager.ImageSubKey.ON_RETURN -> {
                                val mainSubKeyMap = mapOf(
                                    innerSubKeyName to innerSubKeyCon,
                                )
                                index to Pair(innerSubKeyName, mainSubKeyMap)
                            }

                            ImageActionKeyManager.ImageSubKey.FUNC,
                            ImageActionKeyManager.ImageSubKey.I_IF -> {
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
                                index to Pair(innerSubKeyName, (funcPartMap + argsMap))
                            }
                            ImageActionKeyManager.ImageSubKey.ARGS
                                -> index to Pair(String(), emptyMap())
                        }
                    }
                }
                val indexAndKeyToSubKeyConList =
                    ArrayList<Pair<Int, Pair<String, Map<String, String>>>>(indexAndKeyToSubKeyJobList.count())
                indexAndKeyToSubKeyJobList.forEach {
                    indexAndKeyToSubKeyConList.add(it.await())
                }
                indexAndKeyToSubKeyConList.sortedBy { it.first }.map {
                    it.second
                }.filter {
                    it.first.isNotEmpty()
                }.toList()
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
//            private var isNext = true
            private val valueSeparator = ImageActionKeyManager.valueSeparator
            private val itPronoun = ImageActionKeyManager.BitmapVar.itPronoun

            suspend fun exec(
                context: Context?,
                fannelInfoMap: HashMap<String, String>,
                setReplaceVariableMapSrc: Map<String, String>?,
                busyboxExecutor: BusyboxExecutor?,
                mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
                imageView: AppCompatImageView?,
                requestBuilder: RequestBuilder<Drawable>?,
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
                forVarNameBitmapMap: ImageActionData.ForVarNameBitmapMap?,
                keyToSubKeyConWhere: String,
            ): Pair<Pair<String, Bitmap?>, ImageActionKeyManager.BreakSignal?>? {
                val ifStackList =
                    arrayListOf<SettingIfManager.IfStack>()
//                val varMarkSeq =
//                    ImageVarMarkTool.extractValNameList(mainSubKeyPairList.joinToString("\n"))
                val mainSubKeyPairListCon = mainSubKeyPairList.joinToString("\n")
                val filterTopVarNameToVarNameBitmapMap = ImageVarMarkTool.filterByUseVarMark(
                    topVarNameToVarNameBitmapMap,
                    mainSubKeyPairListCon,
                )
                val filterImportedVarNameToBitmapMap = ImageVarMarkTool.filterByUseVarMark(
                    importedVarNameToBitmapMap,
                    mainSubKeyPairListCon,
                )
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
                    val isNext = ifStackList.lastOrNull().let {
                            ifStack ->
                        if(
                            ifStack == null
                        ) return@let true
                        ifStack.bool
                    }
                    when(privateSubKeyClass) {
                        ImageActionKeyManager.ImageSubKey.IMAGE_VAR,
                        ImageActionKeyManager.ImageSubKey.IMAGE_RETURN,
                        ImageActionKeyManager.ImageSubKey.ARGS -> {}
                        ImageActionKeyManager.ImageSubKey.AWAIT -> {
                            if(!isNext){
//                                isNext = true
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
                                            ?.getAsyncVarNameToBitmapAndExitSignalFromMap(
                                                curMapLoopKey,
                                                awaitVarName
                                            )
//                                            ?.get(awaitVarName)
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
//                                isNext = true
                                return@forEach
                            }
                            val varNameToBitmapMap =
                                ImageActionMapTool.makeValueToBitmapMap(
                                    curMapLoopKey,
                                    filterTopVarNameToVarNameBitmapMap,
                                    filterImportedVarNameToBitmapMap,
                                    loopKeyToVarNameBitmapMapClass,
                                    privateLoopKeyVarNameBitmapMapClass,
                                    null,
                                    mapOf(itPronoun to itPronounBitmapToBreakSignal?.first),
                                )
                            val curIVarKey =
                                mainSubKeyMap.get(mainSubKey)?.let {
                                    ImageVarMarkTool.convertBitmapKey(it)
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
                                    !isGlobalVarFuncNullResultErr
                                ) return@let
                                imageActionExitManager.setExitSignal(
                                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                                )
                                return null
                                //Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                                return null
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
//                                isNext = true
                                return@forEach
                            }
                            val funcTypeDotMethod = mainSubKeyMap.get(mainSubKey)
                                ?: return@forEach
                            val argsPairList = SettingArgsTool.makeArgsPairList(
                                mainSubKeyMap,
                                ImageActionKeyManager.ImageSubKey.ARGS.key,
                                null,
                                valueSeparator
                            )
//                            CmdClickMap.createMap(
//                                mainSubKeyMap.get(
//                                    ImageActionKeyManager.ImageSubKey.ARGS.key
//                                ),
//                                valueSeparator
//                            ).filter {
//                                it.first.isNotEmpty()
//                            }
                            val varNameToBitmapMap =
                                ImageActionMapTool.makeValueToBitmapMap(
                                    curMapLoopKey,
                                    filterTopVarNameToVarNameBitmapMap,
                                    filterImportedVarNameToBitmapMap,
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
                                context,
                                fannelInfoMap,
                                setReplaceVariableMapSrc,
                                busyboxExecutor,
                                funcTypeDotMethod,
                                argsPairList,
                                editConstraintListAdapterArg,
                                varNameToBitmapMap,
                                imageView,
                                requestBuilder,
                                forVarNameBitmapMap,
                                keyToSubKeyConWhere,
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
                                imageActionExitManager.setExitSignal(
                                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                                )
                                return null
                                //Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                                isNext = false
//                                return@forEach
                            }
                            val resultBitmapToExitMacro =
                                resultBitmapToExitMacroAndCheckErr?.first
                            val resultBitmap =
                                resultBitmapToExitMacro?.first
                            val signal =
                                resultBitmapToExitMacro?.second
//                            val isExitSignal =
//                                signal == exitSignal
                            if(imageActionExitManager.isExitBySignal(signal)){
                                imageActionExitManager.setExitSignal(signal)
                                return null
                                //Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                            }
                            ImageActionVarErrManager.isGlobalVarNullResultErr(
                                context,
                                renewalVarName ?: settingVarName,
                                resultBitmap,
                                privateSubKeyClass,
                                "funcTypeDotMethod: ${funcTypeDotMethod} argsPairList: ${argsPairList}, $keyToSubKeyConWhere",
                            ).let {
                                    isGlobalVarFuncNullResultErr ->
                                if(
                                    !isGlobalVarFuncNullResultErr
                                ) return@let
                                imageActionExitManager.setExitSignal(
                                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                                )
                                return null
                                //Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                                    return null
//                                }
                            }
                            itPronounBitmapToBreakSignal = resultBitmapToExitMacro

                        }
                        ImageActionKeyManager.ImageSubKey.I_IF -> {
                            if(!isNext) {
//                                isNext = true
                                return@forEach
                            }
//                            val judgeTargetStr = mainSubKeyMap.get(mainSubKey)
//                                ?: return@forEach
                            val argsPairList = SettingArgsTool.makeArgsPairList(
                                mainSubKeyMap,
                                ImageActionKeyManager.ImageSubKey.ARGS.key,
                                null,
                                valueSeparator,
                            )
//                            CmdClickMap.createMap(
//                                mainSubKeyMap.get(
//                                    ImageActionKeyManager.ImageSubKey.ARGS.key
//                                ),
//                                valueSeparator
//                            ).filter {
//                                it.first.isNotEmpty()
//                            }
                            val isImportToErrType = SettingIfManager.handle(
                                iIfKeyName,
//                                judgeTargetStr,
                                argsPairList,
                                null,
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
                                imageActionExitManager.setExitSignal(
                                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                                )
                                return Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                            }
                            val sIfProcName = IfErrManager.makeIfProcNameNotExistInRuntime(
                                mainSubKey,
                                mainSubKeyMap.get(mainSubKey)
                            ).let {
                                    (ifProcName, errMsg) ->
                                if(
                                    errMsg == null
                                ) return@let ifProcName
                                runBlocking {
                                    ImageActionErrLogger.sendErrLog(
                                        context,
                                        ImageActionErrLogger.ImageActionErrType.I_IF,
                                        errMsg,
                                        keyToSubKeyConWhere
                                    )
                                }
                                imageActionExitManager.setExitSignal(
                                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                                )
                                return null
                                //Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                                return@forEach
                            }
                            val isImport = isImportToErrType.first ?: false
                            ifStackList.add(
                                SettingIfManager.IfStack(
                                    sIfProcName,
                                    isImport
                                )
                            )
//                            isNext = isImport
                        }
                        ImageActionKeyManager.ImageSubKey.I_IF_END -> {
                            val sIfProcName = IfErrManager.makeIfProcNameNotExistInRuntime(
                                mainSubKey,
                                mainSubKeyMap.get(mainSubKey)
                            ).let {
                                    (ifProcName, errMsg) ->
                                if(
                                    errMsg == null
                                ) return@let ifProcName
                                runBlocking {
                                    ImageActionErrLogger.sendErrLog(
                                        context,
                                        ImageActionErrLogger.ImageActionErrType.I_IF,
                                        errMsg,
                                        keyToSubKeyConWhere
                                    )
                                }
                                imageActionExitManager.setExitSignal(
                                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                                )
                                return null
                                //Pair(settingVarName, null) to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                                return@forEach
                            }
                            if(
                                ifStackList.lastOrNull()?.ifProcName != sIfProcName
                            ) {
                                return@forEach
                            }
                            if(
                                ifStackList.isNotEmpty()
                            ) {
                                ifStackList.removeAt(ifStackList.lastIndex)
                            }
                        }
                    }
//                    if(privateSubKeyClass != ImageActionKeyManager.ImageSubKey.I_IF){
//                        isNext = true
//                    }
                }
                val isNoImageVar =
                    settingVarName.startsWith(escapeRunPrefix)
                            || settingVarName.startsWith(asyncRunPrefix)
                val isEscape =
                    isNoImageVar
                            && imageActionExitManager.isExitBySignal(
                        itPronounBitmapToBreakSignal?.second
                            )
//                            && itPronounBitmapToBreakSignal?.second != ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
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

    private object ImageFuncManager {

        private const val funcTypeAndMethodSeparatorDot = "."

        suspend fun handle(
            context: Context?,
            fannelInfoMap: HashMap<String, String>,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            funcTypeDotMethod: String,
            baseArgsPairList: List<Pair<String, String>>,
            editConstraintListAdapter: EditConstraintListAdapter?,
            varNameToBitmapMap: Map<String, Bitmap?>,
            imageView: AppCompatImageView?,
            requestBuilder: RequestBuilder<Drawable>?,
            forVarNameBitmapMap: ImageActionData.ForVarNameBitmapMap?,
            keyToSubKeyConWhere: String,
        ): Pair<
                Pair<
                        Bitmap?,
                        ImageActionKeyManager.BreakSignal?
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
                FuncType.ICON ->
                    IconForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList
                    )
                FuncType.FANNEL_ICON ->
                    FannelIconForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList
                    )
                FuncType.FILE ->
                    FileSystemsForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.WALL ->
                    WallForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                    )
                FuncType.ARB ->
                    ArbForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                    )
                FuncType.SHAPE_OVERLAY ->
                    ShapeOverlayForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                    )
                FuncType.DEBUG ->
                    DebugForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.DELAY ->
                    DelayForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.VIEW ->
                    ViewForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                        imageView,
                        requestBuilder
                    )
                FuncType.CUT ->
                    CutForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.OVERLAY ->
                    OverlayForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.OPACITY ->
                    OpacityForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.ROTATE ->
                    RotateForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.BLUR ->
                    BlurForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.COLOR ->
                    ColorForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.GRAD ->
                    GradForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                    )
                FuncType.SIZE ->
                    SizeForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.MASK ->
                    MaskForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.FLIP ->
                    FlipForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.STR_PNG ->
                    StrPngForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                    )
                FuncType.IMPORT_DATE ->
                    ImportDataForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.MONO_ART ->
                    MonoArtForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.LOOP_RESULT ->
                    LoopResultForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        forVarNameBitmapMap,
                    )
                FuncType.EVAL ->
                    EvalForSetting.handle(
                        context,
                        fannelInfoMap,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        varNameToBitmapMap,
                        keyToSubKeyConWhere,
                        editConstraintListAdapter,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                    )
                FuncType.LINE_ART -> {
                    LineArtForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                }
                FuncType.CONCAT ->
                    ConcatForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.BITMAP_STORM ->
                    BitmapArtForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.IMAGE_TOOL ->
                    ImageToolForImageAction.handle(
                        context,
                        funcTypeStr,
                        methodName,
                        baseArgsPairList,
                        varNameToBitmapMap,
                    )
                FuncType.EXIT ->
                        ExitForImageAction.handle(
                            funcTypeStr,
                            methodName,
                        )
            }

        }

        private enum class FuncType(
            val key: String,
        ) {
            ICON("icon"),
            FANNEL_ICON("fannelIcon"),
            FILE("file"),
            WALL("wall"),
            DEBUG("debug"),
            VIEW("view"),
            DELAY("delay"),
            ARB("arb"),
            SHAPE_OVERLAY("shapeOverlay"),
            CUT("cut"),
            OVERLAY("overlay"),
            OPACITY("opacity"),
            ROTATE("rotate"),
            BLUR("blur"),
            COLOR("color"),
            GRAD("grad"),
            SIZE("size"),
            MASK("mask"),
            FLIP("flip"),
            STR_PNG("strPng"),
            IMPORT_DATE("importData"),
            LOOP_RESULT("loopResult"),
            MONO_ART("monoArt"),
            EVAL("eval"),
            LINE_ART("lineArt"),
            CONCAT("concat"),
            BITMAP_STORM("bitmapArt"),
            IMAGE_TOOL("imageTool"),
            EXIT("exit"),
        }
    }

    private object EvalForSetting {

        private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

        suspend fun handle(
            context: Context?,
            fannelInfoMap: HashMap<String, String>,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            topVarNameToBitmapMap: Map<String, Bitmap?>?,
            keyToSubKeyConWhere: String,
            editConstraintListAdapterArg: EditConstraintListAdapter? = null,
            funcName: String,
            methodNameStr: String,
            argsPairList: List<Pair<String, String>>,
        ): Pair<
                Pair<
                        Bitmap?,
                        ImageActionKeyManager.BreakSignal?
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
                        val formalArgIndexToNameToTypeList =
                            args.entries.mapIndexed { index, formalArgsNameToType ->
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
                                ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                                ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
//                            ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                        ) to funcErr
//                    }
                        val settingActionCon =
                            FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                                mapArgMapList,
                                args.actionKeyToDefaultValueStr,
                                where
                            ).let { settingActionConToErr ->
                                val funcErr = settingActionConToErr.second
                                    ?: return@let settingActionConToErr.first
                                return@withContext Pair(
                                    null,
                                    ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                                ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                                ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                            ) to funcErr
                        }
//                        val joinStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                            mapArgMapList,
//                            args.joinStrKeyToDefaultValueStr,
//                            where
//                        ).let { joinStrToErr ->
//                            val funcErr = joinStrToErr.second
//                                ?: return@let joinStrToErr.first
//                            return@withContext Pair(
//                                null,
//                                ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                            ) to funcErr
//                        }
                        val semaphoreLimit = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                            mapArgMapList,
                            args.semaphoreKeyToDefaultValueStr,
                            where
                        ).let { semaphoreToErr ->
                            val funcErr = semaphoreToErr.second
                                ?: return@let semaphoreToErr.first
                            return@withContext Pair(
                                null,
                                ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                                ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
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
                        if (isDuplicate) {
//                        val spanElVarNameKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.ligthBlue,
//                            args.elVarNameKeyToDefaultValueStr.first
//                        )
                            val spanIndexVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.lightBlue,
                                args.indexVarNameKeyToDefaultValueStr.first
                            )
                            val spanFieldVarPrefix =
                                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                    CheckTool.lightBlue,
                                    args.fieldVarPrefixKeyToDefaultValueStr.first
                                )
                            val alreadyUseVarListCon = alreadyUseVarNameList.joinToString(", ")
                            val spanAlreadyUseVarListCon =
                                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                    CheckTool.lightBlue,
                                    alreadyUseVarListCon
                                )
                            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errBrown,
                                where
                            )
                            return@withContext Pair(
                                null,
                                ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                            ) to FuncCheckerForSetting.FuncCheckErr(
                                "Must be different from ${spanIndexVarName} and ${spanFieldVarPrefix}: ${spanAlreadyUseVarListCon}, ${spanWhere} "
                            )
                        }
                        MapOperator.map(
                            context,
                            fannelInfoMap,
                            setReplaceVariableMapSrc,
                            busyboxExecutor,
                            topVarNameToBitmapMap,
                            keyToSubKeyConWhere,
                            editConstraintListAdapterArg,
                            args,
                            inputCon,
                            separator,
                            indexVarName,
//                        elVarName,
                            settingActionCon,
//                            joinStr,
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
                ImageActionKeyManager.returnTopAcVarNameMacro
            private const val defaultNullMacroStr =
                FuncCheckerForSetting.defaultNullMacroStr
            private const val itPronoun = ImageActionKeyManager.BitmapVar.itPronoun

            suspend fun map(
                context: Context?,
                fannelInfoMap: HashMap<String, String>,
                setReplaceVariableMapSrc: Map<String, String>?,
                busyboxExecutor: BusyboxExecutor?,
                topVarNameToBitmapMapSrc: Map<String, Bitmap?>?,
                keyToSubKeyConWhereSrc: String,
                editConstraintListAdapterArg: EditConstraintListAdapter? = null,
                args: EvalArgClass.EvalEvalArgs,
                inputCon: String,
                separator: String,
                indexVarName: String,
//            elVarName: String,
                imageActionCon: String,
//                joinStr: String,
                semaphoreLimit: Int,
                delimiter: String,
                fieldVarPrefix: String,
            ): Pair<
                    Pair<
                            Bitmap?,
                            ImageActionKeyManager.BreakSignal?
                            >?,
                    FuncCheckerForSetting.FuncCheckErr?
                    >
            {
                val info = sequenceOf(
                    "inputCon ${inputCon}",
                    "separator: ${separator}",
                    "indexVarName: ${indexVarName}",
//                "elVarName: ${elVarName}",
                    "settingActionCon $imageActionCon",
//                    "joinStr: ${joinStr}",
                    "semaphoreLimit: ${semaphoreLimit}",
                    "delimiter: ${delimiter}",
                    "fieldVarPrefix: ${fieldVarPrefix}",
                ).joinToString(",")
                val keyToSubKeyConWhere = listOf(
                    keyToSubKeyConWhereSrc,
                    info
                ).joinToString(",")
                val topVarNameToBitmapMap = topVarNameToBitmapMapSrc?.filterKeys {
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
                val forVarNameBitmapMap = ImageActionData.ForVarNameBitmapMap()
                return withContext(Dispatchers.IO) {
                    val semaphore = when (semaphoreLimit > 0) {
                        false -> null
                        else -> Semaphore(semaphoreLimit)
                    }
//                    val indexToResultBitmapJobList =
                    val indexToResultBitmapJobList = ArrayList<Deferred<Unit>>()
                    when (
                        separator == defaultNullMacroStr
                    ) {
                        true -> listOf(inputCon)
                        else -> inputCon.split(separator)
                    }.forEachIndexed { index, el ->
                        val asyncJob = async {
                            val fieldVarNameToValueStrMap =
                                SettingFuncTool.FieldVarPrefix.makeFieldVarNameToValueStrList(
                                    el,
                                    delimiter,
                                    fieldVarPrefix,
                                )?.map { (fieldVarName, valueStr) ->
                                    fieldVarName to valueStr
                                }?.toMap() ?: emptyMap()
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "leval_replace.txt").absolutePath,
//                            listOf(
//                                "fieldVarMarkToValueStrMap: ${fieldVarMarkToValueStrMap}",
//                            ).joinToString("\n\n")
//                        )
                            when (semaphore == null) {
                                true -> execAction(
                                    context,
                                    fannelInfoMap,
                                    setReplaceVariableMapSrc,
                                    busyboxExecutor,
                                    topVarNameToBitmapMap,
                                    keyToSubKeyConWhere,
                                    editConstraintListAdapterArg,
                                    index,
                                    indexVarName,
//                                el,
//                                elVarName,
                                    imageActionCon,
                                    fieldVarNameToValueStrMap,
                                    forVarNameBitmapMap,
                                )

                                else -> semaphore.withPermit {
                                    execAction(
                                        context,
                                        fannelInfoMap,
                                        setReplaceVariableMapSrc,
                                        busyboxExecutor,
                                        topVarNameToBitmapMap,
                                        keyToSubKeyConWhere,
                                        editConstraintListAdapterArg,
                                        index,
                                        indexVarName,
//                                    el,
//                                    elVarName,
                                        imageActionCon,
                                        fieldVarNameToValueStrMap,
                                        forVarNameBitmapMap,
                                    )
                                }
                            }
                        }
                        when(true) {
                            (semaphoreLimit > 1) -> indexToResultBitmapJobList.add(asyncJob)
                            (semaphoreLimit == 1) -> asyncJob.await()
                            else -> {}
                        }
                    }
                    indexToResultBitmapJobList.awaitAll()
//                    val resultStrList =
//                        indexToResultBitmapJobList.awaitAll().sortedBy { indexToResultStr ->
//                            indexToResultStr.first
//                        }.map { indexToResultStr ->
//                            indexToResultStr.second
//                        }
//                    val isJoin =
//                        joinStr != defaultNullMacroStr
//                    if (!isJoin) {
//                        return@withContext null to null
//                    }
                    val resultBitmap = forVarNameBitmapMap.get(
                        returnTopAcVarNameMacro
                    )
                    Pair(
                        resultBitmap,
                        null,
                    ) to null
                }
            }

            private suspend fun execAction(
                context: Context?,
                fannelInfoMap: HashMap<String, String>,
                setReplaceVariableMapSrc: Map<String, String>?,
                busyboxExecutor: BusyboxExecutor?,
                topVarNameToBitmapMap: Map<String, Bitmap?>?,
                keyToSubKeyConWhere: String,
                editConstraintListAdapterArg: EditConstraintListAdapter? = null,
                index: Int,
                indexVarName: String,
//            el: String,
//            elVarName: String,
                imageActionCon: String,
                fieldVarMarkToValueStrMap: Map<String, String>,
                forVarNameBitmapMap: ImageActionData.ForVarNameBitmapMap?,
            )
            //: Pair<Int, Bitmap?>
            {
//                val curTopVarNameToValueStrMap = (topVarNameToBitmapMap ?: emptyMap())
                val loopVarNameToValueStrMap = mapOf(
//                elVarName to el,
                    indexVarName to index.toString(),
                ) + fieldVarMarkToValueStrMap
                FileSystems.writeFile(
                    File(UsePath.cmdclickDefaultAppDirPath, "lexecAc.txt").absolutePath,
                    listOf(
                        "fieldVarMarkToValueStrMap: ${fieldVarMarkToValueStrMap}",
                        "imageActionCon: ${imageActionCon}",
                        "loopVarNameToValueStrMap: ${loopVarNameToValueStrMap}",
                        "rep imageActionCon: ${CmdClickMap.replace(
                            BackslashTool.toNormal(imageActionCon),
                            loopVarNameToValueStrMap,
                        )}"
                    ).joinToString("\n")
                )
//                val outputVarNameToBitmapMap =
                ImageActionManager().exec(
                    context,
                    fannelInfoMap,
                    setReplaceVariableMapSrc,
                    busyboxExecutor,
                    null,
                    null,
                    ImageActionAsyncCoroutine(),
                    topVarNameToBitmapMap?.map {
                        it.key
                    },
                    topVarNameToBitmapMap,
                    CmdClickMap.replace(
                        BackslashTool.toNormal(imageActionCon),
                        loopVarNameToValueStrMap,
                    ),
                    "index: ${index}, eval: indexVarName: ${indexVarName}, ${keyToSubKeyConWhere}",
                    editConstraintListAdapterArg,
                    returnTopAcVarNameMacro,
                    forVarNameBitmapMap,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "loop.txt").absolutePath,
//                    listOf(
//                        "index: ${index}",
//                        "forVarNameBitmapMap: ${forVarNameBitmapMap?.get(ImageActionKeyManager.returnTopAcVarNameMacro)}"
//                    ).joinToString("\n") + "\n\n==========-\n\n"
//                )
//                return index to outputVarNameToBitmapMap.get(returnTopAcVarNameMacro)
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
//                val joinStrKeyToDefaultValueStr = Pair(
//                    MapEnumArgs.JOIN_STR.key,
//                    MapEnumArgs.JOIN_STR.defaultValueStr
//                )
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
                    SEPARATOR(
                        "separator",
                        null,
                        FuncCheckerForSetting.ArgType.STRING
                    ),
                    INDEX_VAR_NAME(
                        "indexVarName",
                        defaultNullMacroStr,
                        FuncCheckerForSetting.ArgType.STRING
                    ),
//                    JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                    SEMAPHORE("semaphore", 1.toString(), FuncCheckerForSetting.ArgType.INT),
                    DELIMITER(
                        "delimiter",
                        defaultNullMacroStr,
                        FuncCheckerForSetting.ArgType.STRING
                    ),
                }
            }
        }
    }
}

