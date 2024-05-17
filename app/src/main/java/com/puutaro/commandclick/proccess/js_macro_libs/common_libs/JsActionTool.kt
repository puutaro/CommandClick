package com.puutaro.commandclick.proccess.js_macro_libs.common_libs


import TsvImportManager
import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.LogTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.import.JsImportManager
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object JsActionTool {

    private val jsActionShiban =
        CommandClickScriptVariable.jsActionShiban
    private val jsKeyName = JsActionKeyManager.JsActionsKey.JS.key
//    private val replaceMainKeyName = JsActionKeyManager.JsActionsKey.REPLACE.key
    private val overrideMainKeyName = JsActionKeyManager.JsActionsKey.OVERRIDE.key
    private val jsPathKeyName = JsActionKeyManager.JsActionsKey.JS_FUNC.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key


    fun judgeJsAction(
        jsList: List<String>
    ): Boolean {
        val firstLine = jsList
            .firstOrNull()
            ?: return false
        val isExistJsAcShiban = firstLine
            .trim()
            .removePrefix("//")
            .trim() == jsActionShiban
        return isExistJsAcShiban
    }

    private fun makeSetRepValMap(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        extraRepValMap: Map<String, String>?
    ): Map<String, String>? {
        val virtualSubFannelPath = VirtualSubFannel.makePath(
            readSharePreferenceMap
        )
        val setReplaceVariableMapSrc = SharePrefTool.getReplaceVariableMap(
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

    fun makeJsActionMap(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        keyToSubKeyCon: String?,
        setReplaceVariableMapSrc: Map<String, String>?,
        mainOrSubFannelPath: String,
    ): Map<String, String>? {
        val setReplaceVariableMap = makeSetRepValMap(
            fragment,
            readSharePreferenceMap,
            setReplaceVariableMapSrc
        )
        makeReplaceVariableTsv(
            setReplaceVariableMap,
            mainOrSubFannelPath,
        )
        val keyToSubMapTypeMapToKeyToSubKeyConListByValidKey = createKeyToSubMapTypeMap(
            readSharePreferenceMap,
            keyToSubKeyCon,
            setReplaceVariableMap,
        ) ?: return null
        val keyToSubMapTypeMap = keyToSubMapTypeMapToKeyToSubKeyConListByValidKey.first
        val actionImportedKeyToSubKeyConList = keyToSubMapTypeMapToKeyToSubKeyConListByValidKey.second
//        val keyToSubKeyMapListWithReplace = keyToSubMapTypeMap.get(
//            KeyToSubConType.WITH_REPLACE
//        )
//        val jsRepValHolderMap = makeRepValHolderMap(
//            keyToSubKeyMapListWithReplace
//        )

        val keyToSubKeyMapListWithAfterSubKey = keyToSubMapTypeMap.get(
            KeyToSubConType.WITH_AFTER
        )

        val keyToSubKeyMapListWithoutAfterSubKey =
            keyToSubMapTypeMap.get(
                KeyToSubConType.WITH_OTHER
            ) ?: emptyList()

        val macroDataMap =
            extractMacroDataMap(keyToSubKeyMapListWithoutAfterSubKey)
//        FileSystems.writeFile(
//            File(
//                UsePath.cmdclickDefaultAppDirPath,
//                "js_makeJsActionMap.txt").absolutePath,
//            listOf(
//                "keyToSubKeyCon: ${keyToSubKeyCon}",
//                "setReplaceVariableMap: ${setReplaceVariableMap}",
//                "keyToSubMapTypeMap: ${keyToSubMapTypeMap}",
//                "jsRepValHolderMap: ${jsRepValHolderMap}",
//                "keyToSubKeyMapListWithAfterSubKey: ${keyToSubKeyMapListWithAfterSubKey}",
//                "keyToSubKeyMapListWithoutAfterSubKey: ${keyToSubKeyMapListWithoutAfterSubKey}",
//                "macroDataMap: ${macroDataMap}",
//                "onExtract: ${!macroDataMap.isNullOrEmpty()}",
//                "extractJsDataMap: ${
//                    extractJsDataMap(
//                        fragment,
//                        readSharePreferenceMap,
//                        setReplaceVariableMap,
//                        keyToSubKeyMapListWithoutAfterSubKey,
//                        keyToSubKeyMapListWithAfterSubKey,
//                        jsRepValHolderMap,
//                    )
//                }",
//            ).joinToString("\n\n")
//        )
        val displayActionImportedAcCon = LogTool.DisplayActionImportedJsAcSrc.make(
            actionImportedKeyToSubKeyConList,
        )
        if(
            !macroDataMap.isNullOrEmpty()
        ) {
            LogTool.FirstJsActionLogSaver.save(
                keyToSubKeyCon,
                displayActionImportedAcCon,
                null,
                null,
                macroDataMap to null,
            )
            return macroDataMap
        }
        val jsActionMapToJsConOnlyReplace = extractJsDataMap(
            fragment,
            readSharePreferenceMap,
            setReplaceVariableMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
//            jsRepValHolderMap,
        )
        val jsActionMap = jsActionMapToJsConOnlyReplace?.first
        LogTool.FirstJsActionLogSaver.save(
            keyToSubKeyCon,
            displayActionImportedAcCon,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
            jsActionMapToJsConOnlyReplace
        )
        val context = fragment.context
//        val checkJsCon = jsActionMap?.get(JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key)
//        val blankActionMap = mapOf(
//            JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
//                    to JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON.key,
//            JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key to String()
//        )
        jsAcChecker(
            context,
            jsActionMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
//            keyToSubKeyMapListWithReplace,
//            jsRepValHolderMap,
            actionImportedKeyToSubKeyConList,
            displayActionImportedAcCon,
        ).let {
            isErr ->
            if(
                isErr
            ) return mapOf(
                JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
                    to JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON.key,
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
                        to String()
            )
        }
        return jsActionMap
    }

    private fun jsAcChecker(
        context: Context?,
        jsActionMap: Map<String, String>?,
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
//        keyToSubKeyMapListWithReplace: List<Pair<String, Map<String, String>>>?,
//        jsRepValHolderMap: Map<String, String>?,
        actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
        displayActionImportedAcCon: String,
    ): Boolean {
        val checkJsCon =
            jsActionMap?.get(JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key)
        LogTool.QuoteNumCheck.check(
            context,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
//            keyToSubKeyMapListWithReplace
        ).let {
                isQuoteErr ->
            if(
                isQuoteErr
            ) return true
        }
        val evaluateGeneCon =
            LogTool.KeyToSubKeyConTool.makeEvaluateAcCon(
                keyToSubKeyMapListWithoutAfterSubKey,
                keyToSubKeyMapListWithAfterSubKey,
//                jsRepValHolderMap
            )
        LogTool.NotCorrespondToUseAfter.check(
            context,
            evaluateGeneCon
        )
        LogTool.PathNotFound.check(
            context,
            evaluateGeneCon,
            actionImportedKeyToSubKeyConList,
//            keyToSubKeyCon,
//            jsRepValHolderMap,
        ).let {
                isErrPath ->
            if(
                isErrPath
            ) return true
        }
        LogTool.VarNotInit.check(
            context,
            evaluateGeneCon
        ).let {
                isVarNotInit ->
            if(
                isVarNotInit
            ) return true
        }

        LogTool.IrregularFuncValue.check(
            context,
            evaluateGeneCon,
        ).let {
                isIrregularFuncValue ->
            if(
                isIrregularFuncValue
            ) return true
        }
        LogTool.LoopMethodOrArgsNotExist.check(
            context,
            checkJsCon
        ).let {
                isLoopMethodOrArgsNotExist ->
            if(
                isLoopMethodOrArgsNotExist
            ) return true
        }
        LogTool.PrevNotExist.check(
            context,
            checkJsCon
        ).let {
                isPrevNotExist ->
            if(
                isPrevNotExist
            ) return true
        }
        LogTool.SyntaxCheck.checkJsAcSyntax(
            context,
            checkJsCon
        ).let {
                isSyntaxErr ->
            if(
                isSyntaxErr
            ) return true
        }
        LogTool.VarNotUse.checkJsAsSyntaxForVarNotUse(
            context,
            checkJsCon
        ).let {
                isVarNotUseErr ->
            if(
                isVarNotUseErr
            ) return true
        }
//        LogTool.MisMatchCollectionMethodStartAndEnd.check(
//            context,
//            displayActionImportedAcCon
//        ).let {
//                isMisMatchCollMethodStartAndEnd ->
//            if(
//                isMisMatchCollMethodStartAndEnd
//            ) return true
//        }
        return false
    }

    private fun extractJsDataMap(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
//        jsRepValHolderMap: Map<String, String>?,
    ): Pair<Map<String, String>, String?>? {
        val jsConToJsConOnlyReplace = convertJsCon(
            fragment,
            readSharePreferenceMap,
            setReplaceVariableMap,
//            jsRepValHolderMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        )
        val jsCon = jsConToJsConOnlyReplace.first
        val jsConOnlyReplace = jsConToJsConOnlyReplace.second
        if(
            jsCon.isNullOrEmpty()
        ) return null
        return makeJsDataMap(
            jsCon,
        ) to jsConOnlyReplace
    }

    private fun extractMacroDataMap(
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>
    ): Map<String, String>? {
        if(
            keyToSubKeyMapListWithoutAfterSubKey.size != 1
        ) return null
        val keyToSubKeyCon = keyToSubKeyMapListWithoutAfterSubKey.first()
        val mainKeyName = keyToSubKeyCon.first
        if (
            mainKeyName != jsKeyName
        ) return null
        val jsMap= keyToSubKeyCon.second
        if(
            jsMap.isEmpty()
        ) return null
        val macroName = jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
            ?: return null
        if(
            !howMacroFunc(
                macroName,
            )
        ) return null
        val argsStr = jsMap.get(JsActionKeyManager.JsSubKey.ARGS.key)
            ?: String()
        return makeMacroDataMap(
            macroName,
            argsStr,
        )
    }

    fun compJsActionMacro(
        jsActionMapSrc: Map<String, String>?,
        defaultButtonMacroStr: String,
    ): Map<String, String> {
        if(
            jsActionMapSrc.isNullOrEmpty()
        ) return makeMacroDataMap(
            defaultButtonMacroStr,
            String()
        )
        val isMacro =
            jsActionMapSrc.get(JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key) ==
                    JsActionDataMapKeyObj.JsActionDataTypeKey.MACRO.key
        if(
            !isMacro
        ) return jsActionMapSrc
        val isBlank = jsActionMapSrc.get(
            JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
        ).isNullOrEmpty()
        if(!isBlank) return jsActionMapSrc
        return makeMacroDataMap(
            defaultButtonMacroStr,
            String()
        )
    }

    private fun makeMacroDataMap(
        macroName: String,
        argsStr: String,
    ): Map<String, String> {
        return mapOf(
            JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
                    to JsActionDataMapKeyObj.JsActionDataTypeKey.MACRO.key,
            JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
                    to macroName,
            JsActionDataMapKeyObj.JsActionDataMapKey.MACRO_ARGS.key
                    to argsStr,
        )
    }

    private fun makeJsDataMap(
        jsCon: String,
    ): Map<String, String> {
        return mapOf(
            JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
                    to JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON.key,
            JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
                    to jsCon,
        )
    }

    private fun createKeyToSubMapTypeMap(
        readSharePreferenceMap: Map<String, String>,
        keyToSubKeyCon: String?,
        setReplaceVariableMap: Map<String, String>?,
    ): Pair<
            Map<KeyToSubConType, List<Pair<String, Map<String, String>>>?>,
            List<Pair<String, String>>
            >? {
        val keyToSubKeyMapListToKeyToSubKeyConListByValidKey = KeyToSubKeyMapListMaker.make(
            keyToSubKeyCon,
            readSharePreferenceMap,
            setReplaceVariableMap,
        )
        val keyToSubKeyMapList =
            keyToSubKeyMapListToKeyToSubKeyConListByValidKey.first
        val actionImportedKeyToSubKeyConList =
            keyToSubKeyMapListToKeyToSubKeyConListByValidKey.second
        if (
            keyToSubKeyMapList.isEmpty()
        ) return null
        val overrideKeyToSubKeyMapList = filterByMainKey(
            keyToSubKeyMapList,
            overrideMainKeyName
        )
        val overrideMapList = makeOverrideMapList(
            overrideKeyToSubKeyMapList
        )
        val keyToSubKeyConListWithoutOverride = keyToSubKeyMapList.filter {
            !overrideKeyToSubKeyMapList.contains(it)
        }
        val keyToSubKeyMapListByOverride = makeKeyToSubKeyMapByOverride(
            keyToSubKeyConListWithoutOverride,
            overrideKeyToSubKeyMapList,
            overrideMapList
        )

//        val replaceKeyToSubKeyMapList = filterByMainKey(
//            keyToSubKeyMapList,
//            replaceMainKeyName
//        )
//        val keyToSubKeyMapListWithoutReplaceKey =  keyToSubKeyMapListByOverride
//            .filter {
//            !replaceKeyToSubKeyMapList.contains(it)
//        }
        val afterKeyToSubKeyMapList =
            filterByAfterJsSubKey(
                keyToSubKeyMapListByOverride,
            )

        val keyToSubKeyMapListWithoutAfterSubKey = keyToSubKeyMapListByOverride.filter {
            !afterKeyToSubKeyMapList.contains(it)

        }
        val keyToSubKeyMapListForMacro =
            makeKeyToSubKeyMapListForMacro(
                keyToSubKeyMapListWithoutAfterSubKey
            )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jcreateJsPairList.txt").absolutePath,
//            listOf(
//                "keyToSubKeyMapList: ${keyToSubKeyMapList}",
//                "overrideMapList: ${overrideMapList}",
//                "keyToSubKeyMapListByOverride: ${keyToSubKeyMapListByOverride}",
//                "keyToSubKeyConListWithoutOverride: ${keyToSubKeyConListWithoutOverride}",
//                "replaceKeyToSubKeyMapList: ${replaceKeyToSubKeyMapList}",
//                "keyToSubKeyMapListWithoutReplaceKey: ${keyToSubKeyMapListWithoutReplaceKey}",
//                "afterKeyToSubKeyMapList: ${afterKeyToSubKeyMapList}",
//                "keyToSubKeyMapListWithoutAfterSubKey: ${keyToSubKeyMapListWithoutAfterSubKey}",
//                "keyToSubKeyMapListForMacro: ${keyToSubKeyMapListForMacro}",
//                "keyToSubKeyConListUntilFirstUnPipAble: ${
//                    makeKeyToSubKeyConListUntilFirstUnPipAble(
//                        keyToSubKeyMapListWithoutAfterSubKey
//                )
//                }"
//            ).joinToString("\n\n")
//        )
        if(
            keyToSubKeyMapListForMacro != null
        ) return mapOf(
//            KeyToSubConType.WITH_REPLACE
//                    to null,
            KeyToSubConType.WITH_AFTER
                    to null,
            KeyToSubConType.WITH_OTHER
                    to listOf(keyToSubKeyMapListForMacro),
        ) to actionImportedKeyToSubKeyConList
        return mapOf(
//            KeyToSubConType.WITH_REPLACE
//                    to replaceKeyToSubKeyMapList,
            KeyToSubConType.WITH_AFTER
                    to afterKeyToSubKeyMapList,
            KeyToSubConType.WITH_OTHER
                    to makeKeyToSubKeyConListUntilFirstUnPipAble(
                keyToSubKeyMapListWithoutAfterSubKey
            ),
        ) to actionImportedKeyToSubKeyConList
    }

    private fun makeOverrideMapList(
        overrideKeyToSubKeyConList: List<Pair<String, Map<String, String>>>
    ): List<Map<String, String>> {
        return overrideKeyToSubKeyConList.map {
            it.second
        }
    }

    private fun makeKeyToSubKeyMapByOverride(
        keyToSubKeyMapList: List<Pair<String, Map<String, String>>>,
        overrideKeyToSubKeyConList: List<Pair<String, Map<String, String>>>,
        overrideMapList: List<Map<String, String>>
    ): List<Pair<String, Map<String, String>>> {
        val keyToSubKeyMapListWithoutOverride = keyToSubKeyMapList.filter {
            !overrideKeyToSubKeyConList.contains(it)
        }
        val idSubKeyName =
            JsActionKeyManager.JsSubKey.ID.key
        return keyToSubKeyMapListWithoutOverride.map {
            mainKeyToSubKeyMap ->
            val mainKeyName = mainKeyToSubKeyMap.first
            val subKeyMap = mainKeyToSubKeyMap.second
            val id = subKeyMap.get(
                idSubKeyName
            ) ?: return@map mainKeyName to subKeyMap
            val overrideMapSrc = JsActionKeyManager.OverrideManager.makeOverrideMap(
                overrideMapList,
                id,
            )
            mainKeyName to subKeyMap.plus(overrideMapSrc)
        }
    }


    private fun makeKeyToSubKeyMapListForMacro(
        keyToSubKeyConList: List<Pair<String, Map<String, String>>>?
    ): Pair<String, Map<String, String>>? {
        if(
            keyToSubKeyConList.isNullOrEmpty()
        ) return null
        return keyToSubKeyConList.firstOrNull {
                mainKeyNameToSubKeyCon ->
            howFuncMacroComponent(
                mainKeyNameToSubKeyCon
            )
        }
    }

    private fun makeKeyToSubKeyConListUntilFirstUnPipAble(
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?
    ): List<Pair<String, Map<String, String>>> {
        if(
            keyToSubKeyConListWithoutAfterSubKey.isNullOrEmpty()
        ) return emptyList()
        val sizeByUnPipAble =
            keyToSubKeyConListWithoutAfterSubKey.indexOfFirst {
                howPipUnableComponent(it)
            }.let {
                val isExistUnPipAble = it > -1
                when(isExistUnPipAble) {
                    false -> keyToSubKeyConListWithoutAfterSubKey.size
                    else -> it + 1
                }
            }
        return keyToSubKeyConListWithoutAfterSubKey.take(sizeByUnPipAble)
    }

    private fun filterByMainKey(
        keyToSubKeyMapList: List<Pair<String, Map<String, String>>>,
        filterMainKeyName: String,
    ): List<Pair<String, Map<String, String>>> {
        return keyToSubKeyMapList.filter {
            val mainKeyName = it.first
            return@filter mainKeyName == filterMainKeyName
        }
    }

    private fun filterByAfterJsSubKey(
        keyToSubKeyMapList: List<Pair<String, Map<String, String>>>
    ): List<Pair<String, Map<String, String>>> {
        val afterKeyName = JsActionKeyManager.JsSubKey.AFTER.key
        return keyToSubKeyMapList.filter {
            val mainKeyName = it.first
            if(
                mainKeyName != jsKeyName
            ) return@filter false
            val subKeyMap = it.second
            subKeyMap.keys.contains(afterKeyName)
        }
    }

    private fun howPipUnableComponent(
        mainKeyNameToSubKeyCon: Pair<String, Map<String, String>>
    ): Boolean {
        val mainKeyName = mainKeyNameToSubKeyCon.first
        return mainKeyName == jsPathKeyName
    }

    private fun howFuncMacroComponent(
        mainKeyNameToSubKeyCon: Pair<String, Map<String, String>>
    ): Boolean {
        val mainKeyName = mainKeyNameToSubKeyCon.first
        if(
            mainKeyName == jsPathKeyName
        ) return false
        if(
            mainKeyName != jsKeyName
        ) return false
        val subKeyMap = mainKeyNameToSubKeyCon.second
        val macroSrc = subKeyMap.get(funcSubKeyName)?.trim()
        return howMacroFunc(
            macroSrc,
        )
    }


    private fun convertJsCon(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
//        jsRepValHolderMap: Map<String, String>?,
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
    ): Pair<String?, String?> {
        if(
            keyToSubKeyMapListWithoutAfterSubKey.isNullOrEmpty()
        ) return null to null
        val tsvImportCon = TsvImportConMaker.make(
            keyToSubKeyMapListWithoutAfterSubKey,
        )
        val jsImportCon = ImportConMaker.make(
            JsActionKeyManager.JsActionsKey.JS_IMPORT.key
                    to JsImportManager.jsImportPreWord,
            keyToSubKeyMapListWithoutAfterSubKey
        )
        val jsMapListWithoutAfterSubKey = makeJsMapList(
            keyToSubKeyMapListWithoutAfterSubKey,
        )
        val jsMapListWithAfterSubKey = makeJsMapList(
            keyToSubKeyMapListWithAfterSubKey,
        )

        val jsFuncCon = JsConPutter.make(
            jsMapListWithoutAfterSubKey,
            jsMapListWithAfterSubKey
        )

        val jsConBeforeJsImport = listOf(
            tsvImportCon,
            jsImportCon,
            jsFuncCon,
        ).joinToString("\n")
//            .let {
//            CmdClickMap.replaceHolderForJsAction(
//                it,
//                jsRepValHolderMap,
//            )
//        }

//        FileSystems.writeFile(
//            File(
//                UsePath.cmdclickDefaultAppDirPath,
//                "js_makeJsActionMap_convert.txt").absolutePath,
//            listOf(
//                "tsvImportCon: ${tsvImportCon}",
//                "jsImportCon: ${jsImportCon}",
//                "keyToSubKeyMapListWithoutAfterSubKey: ${keyToSubKeyMapListWithoutAfterSubKey}",
//                "jsMapListWithAfterSubKey: ${jsMapListWithAfterSubKey}",
//                "jsMapListWithoutAfterSubKey: ${jsMapListWithoutAfterSubKey}",
//                "jsMapListWithoutAfterSubKey: ${jsMapListWithoutAfterSubKey}",
//                "jsMapListWithAfterSubKey: ${jsMapListWithAfterSubKey}",
//                "jsFuncCon: ${jsFuncCon}",
//                "jsConBeforeJsImport: ${jsConBeforeJsImport}",
//                "resultJsCon ${ JavaScriptLoadUrl.makeRawJsConFromContents(
//                    fragment,
//                    readSharePreferenceMap,
//                    jsConBeforeJsImport,
//                    setReplaceVariableMap,
//                )}"
//            ).joinToString("\n\n\n")
//        )
        val jsCon = JavaScriptLoadUrl.makeRawJsConFromContents(
            fragment,
            readSharePreferenceMap,
            jsConBeforeJsImport,
            setReplaceVariableMap,
        )
        val jsConOnlyReplace = SetReplaceVariabler.execReplaceByReplaceVariables(
            jsConBeforeJsImport,
            setReplaceVariableMap,
            SharePrefTool.getCurrentAppDirPath(readSharePreferenceMap),
            SharePrefTool.getCurrentFannelName(readSharePreferenceMap),
        )
        return jsCon to jsConOnlyReplace
    }

    private fun makeJsMapList(
        keyToSubMapList: List<Pair<String, Map<String, String>>>?,
    ): List<Map<String, String>> {
        if(
            keyToSubMapList.isNullOrEmpty()
        ) return emptyList()
        return keyToSubMapList.map {
            val mainKeyName = it.first
            if(
                mainKeyName != jsKeyName
            ) return@map emptyMap()
            it.second
        }.filter { it.isNotEmpty() }
    }

    private enum class KeyToSubConType {
//        WITH_REPLACE,
        WITH_AFTER,
        WITH_OTHER,
    }
}


private object KeyToSubKeyMapListMaker {

    private val jsMainKey = JsActionKeyManager.JsActionsKey.JS.key
    private val actionImportVirtualSubKey =
        JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
    private const val keySeparator = '|'
    private const val jsActionEndComma = ','
    val jsActionsKeyPlusList =
        JsActionKeyManager.JsActionsKey.values().map {
            it.key
        }
    val beforeActionImportMap = mutableMapOf<String, String>()

    fun make(
        keyToSubKeyCon: String?,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): Pair<List<Pair<String, Map<String, String>>>, List<Pair<String, String>>> {
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val keyToSubKeyConListSrc = makeKeyToSubConPairListByValidKey(
            keyToSubKeyCon
        )
        val importRoopLimit = 5
        var containImport = false
        val jsActionImportKeyName = JsActionKeyManager.JsActionsKey.ACTION_IMPORT.key
        val jsActionImportSignal = "${jsActionImportKeyName}="
        var keyToSubKeyConList = keyToSubKeyConListSrc
        beforeActionImportMap.clear()
        for( i in 1..importRoopLimit) {
            keyToSubKeyConList = keyToSubKeyConList.map {
                    keyToSubKeyPair ->
                val mainKeyName = keyToSubKeyPair.first
                if(
                    mainKeyName != jsActionImportKeyName
                ) return@map listOf(keyToSubKeyPair)
                val putKeyToSubKeyCon =
                    putJsActionsImport(
                        currentAppDirPath,
                        currentFannelName,
                        setReplaceVariableMap,
                        keyToSubKeyPair
                    )
                containImport =
                    putKeyToSubKeyCon.contains(jsActionImportSignal)
                makeKeyToSubConPairListByValidKey(
                    putKeyToSubKeyCon
                )
            }.flatten()
            if(!containImport) break
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsKeyToSubKeyConListMaker.make.txt").absolutePath,
//            listOf(
//                "keyToSubKeyConListSrc: ${keyToSubKeyConListSrc}",
//                "keyToSubKeyConList: ${keyToSubKeyConList}",
//            ).joinToString("\n\n")
//        )
        val keyToSubKeyConListByValidKey =
            filterByValidKey(keyToSubKeyConList)
        return PairToMapInList.convert(
            keyToSubKeyConListByValidKey
        ) to keyToSubKeyConListByValidKey
    }

    private fun filterByValidKey(
        keyToSubKeyConList: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        return keyToSubKeyConList.filter {
            val mainKeyName = it.first
            jsActionsKeyPlusList.contains(mainKeyName)
        }
    }

    private fun putJsActionsImport(
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyPair: Pair<String, String>,
    ): String {
        val subKeyCon = listOf(
            JsActionKeyManager.CommonPathKey.IMPORT_PATH.key,
            keyToSubKeyPair.second
        ).joinToString("=")
        val actionImportMap = makeActionImportMap(
            subKeyCon,
        )
        val importPathSrc = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                JsActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
            )
        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport.txt").absolutePath,
//            listOf(
//                "keyToSubKeyPair.second: ${keyToSubKeyPair.second}",
//                "subKeyCon: ${subKeyCon}",
//                "actionImportMap: ${actionImportMap}",
//                "importPathSrc: ${importPathSrc}",
//            ).joinToString("\n\n") + "\n-----\n"
//        )
        val importPath = JsActionKeyManager.PathExistChecker.makeCodeOrPath(importPathSrc)
        val isNotFoundPrefix =
            importPath.startsWith(JsActionKeyManager.PathExistChecker.notFoundCodePrefix)

        val importConSrc = when(isNotFoundPrefix){
            true ->
                "${jsMainKey}=${actionImportVirtualSubKey}=${importPath}"
            else -> {
                val importSrcConBeforeReplace = makeActionImportSrcCon(
                    importPath,
                    currentAppDirPath,
                    currentFannelName,
                    setReplaceVariableMap,
                )
                val repMap = makeRepValHolderMap(
                    actionImportMap.get(
                        JsActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
                    )
                )
                val importSrcCon = CmdClickMap.replaceHolderForJsAction(
                    importSrcConBeforeReplace,
                    repMap
                )
                val importConWithFormatList = makeActionImportFormatList (
                    importSrcCon,
                )
                val whenCondition = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        JsActionKeyManager.ActionImportManager.ActionImportKey.WHEN.key
                    )
                )
                val useAfterValue = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key
                    )
                )
                putIfBracketByWhen(
                    importConWithFormatList,
                    whenCondition,
                    useAfterValue,
                ).joinToString("\n")
            }
        }
        return importConSrc.let {
            ListSettingVariableListMaker.execRemoveMultipleNewLinesAndReplace(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
        }
    }

    private fun makeActionImportMap(
        subKeyCon: String,
    ): Map<String, String> {
        val subKeySeparator = '?'
        val subKeyConList = subKeyCon.split(subKeySeparator)
        val importPathKeyCon = subKeyConList.firstOrNull()
            ?: String()
        val importPathKeyPrefix =
            "${JsActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key}="
        val endsQuote = extractEndsQuote(
            importPathKeyCon.removePrefix(importPathKeyPrefix),
        )
        if(
            endsQuote.isNullOrEmpty()
        ) {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_makeActionImportMap1.txt").absolutePath,
//                listOf(
//                    "subKeyCon: ${subKeyCon}",
//                    "importPathKeyCon: ${importPathKeyCon}",
//                ).joinToString("\n\n")
//            )
            return CmdClickMap.createMap(
                subKeyCon,
                subKeySeparator
            ).toMap()
        }
        val otherKeyCon = subKeyConList.filterIndexed { index, _ ->
            index > 0
        }.joinToString(subKeySeparator.toString())
        val compImportPathKeyCon = listOf(
            importPathKeyPrefix,
            endsQuote,
            importPathKeyCon.removePrefix(importPathKeyPrefix),
        ).joinToString(String())
        val compOtherKeyCon = listOf(
            otherKeyCon,
            endsQuote
        ).joinToString(String())
        val compQuoteSubKeyCon = listOf(
            compImportPathKeyCon,
            subKeySeparator.toString(),
            compOtherKeyCon
        ).joinToString(String())
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_makeActionImportMap2.txt").absolutePath,
//            listOf(
//                "subKeyCon: ${subKeyCon}",
//                "importPathKeyCon: ${importPathKeyCon}",
//                "compImportPathKeyCon: ${compImportPathKeyCon}",
//                "importPathKeyPrefix: ${importPathKeyPrefix}",
//                "otherKeyCon: ${otherKeyCon}",
//                "compOtherKeyCon: ${compOtherKeyCon}",
//                "compQuoteSubKeyCon: ${compQuoteSubKeyCon}",
//                "map: ${CmdClickMap.createMap(
//                    compQuoteSubKeyCon,
//                    subKeySeparator
//                ).toMap()}"
//            ).joinToString("\n\n")
//        )
        return CmdClickMap.createMap(
            compQuoteSubKeyCon,
            subKeySeparator
        ).toMap()
    }

    private fun extractEndsQuote(
        importPathKeyCon: String,
    ): String? {
        val quoteList = listOf("`", "\"")
        quoteList.forEach {
            val isOnlyEndQuote = importPathKeyCon.endsWith(it)
                    && !importPathKeyCon.startsWith(it)
            if(
                isOnlyEndQuote
            ) return it
        }
        return null
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
        ).toMap()
    }

//    private fun makeRepValHolderMap(
//        keyToSubKeyMapListWithReplace: List<Pair<String, Map<String, String>>>?
//    ): Map<String, String>? {
//        return keyToSubKeyMapListWithReplace?.map {
//            it.second.map{
//                    repValMapSrc ->
//                repValMapSrc.key to repValMapSrc.value
//            }
//        }?.flatten()?.toMap()
//    }

    private fun makeActionImportSrcCon(
        importPath: String,
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
        val beforeActionImportSrcCon = beforeActionImportMap.get(importPath)
        if(
            !beforeActionImportSrcCon.isNullOrEmpty()
        ) {
            return beforeActionImportSrcCon
        }
        val actionImportSrcCon = SettingFile.read(
            importPath,
            File(currentAppDirPath, currentFannelName).absolutePath,
            setReplaceVariableMap,
        )
        beforeActionImportMap.put(
            importPath,
            actionImportSrcCon,
        )
        return actionImportSrcCon
    }

    private fun makeActionImportFormatList (
        importSrcCon: String,
    ): List<String> {
        val cmdclickNewlineMark = "CMDDCLICK_NEW_LINE_MARK"
        return importSrcCon.let {
            QuoteTool.replaceBySurroundedIgnore(
                it,
                jsActionEndComma,
                cmdclickNewlineMark
            ).split(
                cmdclickNewlineMark
            ).mapIndexed {
                    index, line ->
                val trimLine = line.trim()
                if(
                    trimLine.isEmpty()
                ) return@mapIndexed String()
                val updateLine =
                    if(index == 0) "|${trimLine}".replace(
                        Regex("^\\|\\|"),
                        "|"
                    )
                    else trimLine
                JsActionKeyManager.ActionImportManager.putActionImportSubKey(updateLine)
            }
        }
    }

    private fun putIfBracketByWhen(
        importConWithFormatList: List<String>,
        whenCondition: String?,
        useAfterValue: String?,
    ): List<String> {
        if(
            whenCondition.isNullOrEmpty()
        ) return importConWithFormatList
        val useAfterKeyCon = makeUseAfterKeyConToSrcAfterKeyCon(
            importConWithFormatList,
            useAfterValue
        )
        val afterKeyConRegex = Regex("\\?${JsActionKeyManager.JsSubKey.AFTER.key}=[^?\n]+")
        val importConWithFormatListByUpdateAfter = importConWithFormatList.map{
            it.replace(
                afterKeyConRegex,
                useAfterKeyCon
            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsImoprtafterPhrase.txt").absolutePath,
//            listOf(
//                "firstorNull: ${importConWithFormatList.firstOrNull()}",
//                "importConWithFormatList: ${importConWithFormatList}",
//                "useAfterValue: ${useAfterValue}",
//                "useAfterKeyCon: ${useAfterKeyCon}",
//                "importConWithFormatList: ${importConWithFormatList}",
//                "importConWithFormatListByUpdateAfter: ${importConWithFormatListByUpdateAfter}"
//            ).joinToString("\n\n") + "\n-------\n"
//        )
        return listOf(
            listOf(
                "|${JsActionKeyManager.JsActionsKey.JS.key}=",
                "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
                "?${JsActionKeyManager.JsSubKey.IF_BRACKET_START.key}=",
                useAfterKeyCon,
                "?${JsActionKeyManager.JsSubKey.IF.key}=`${whenCondition}`"
            ).joinToString(String())
        ) + importConWithFormatListByUpdateAfter +
            listOf(
                listOf(
                    "|${JsActionKeyManager.JsActionsKey.JS.key}=",
                    "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
                    "?${JsActionKeyManager.JsSubKey.IF_BRACKET_END.key}=",
                    useAfterKeyCon,
                ).joinToString(String())
            )
    }

    private fun makeUseAfterKeyConToSrcAfterKeyCon(
        importConWithFormatList: List<String>,
        useAfterValue: String?,
    ): String {
        val afterIdWithQuote =
            Regex("\\?${JsActionKeyManager.JsSubKey.AFTER.key}=[^?\n]+").find(
                importConWithFormatList.firstOrNull() ?: String()
            )?.value?.removePrefix(
                "?${JsActionKeyManager.JsSubKey.AFTER.key}="
            )
        val afterId = QuoteTool.trimBothEdgeQuote(afterIdWithQuote)
        if(
            afterId.isEmpty()
        ) return String()
        val errConSeparator = JsActionKeyManager.ActionImportManager.errConSeparator
        val useAfterMap = makeUseAfterMap(useAfterValue)
            ?: let {
                val notCorrespondSrcAfterToUseAfterKeyCon =
                    "?${JsActionKeyManager.ActionImportManager.ActionImportKey.NOT_CORRESPOND_SRC_AFTER_TO_USE_AFTER.key}=${afterId}${errConSeparator}"
                return listOf(
                    notCorrespondSrcAfterToUseAfterKeyCon,
                    "?${JsActionKeyManager.JsSubKey.AFTER.key}=${afterId}",
                    "?${JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key}=${useAfterValue}"
                ).joinToString(String())
//                to
//                        notCorrespondSrcAfterToUseAfterKeyCon
            }

        val useCurAfterId = useAfterMap.keys.joinToString(String())
        val useDestiAfterId = useAfterMap.get(afterId)
            ?: let {
                val notCorrespondSrcAfterToUseAfterKeyCon =
                    "?${JsActionKeyManager.ActionImportManager.ActionImportKey.NOT_CORRESPOND_SRC_AFTER_TO_USE_AFTER.key}=${afterId}${errConSeparator}${useCurAfterId}"
                return listOf(
                    notCorrespondSrcAfterToUseAfterKeyCon,
                    "?${JsActionKeyManager.JsSubKey.AFTER.key}=${afterId}",
                    "?${JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key}=${useAfterValue}"
                ).joinToString(String())
//                to listOf(
//                    notCorrespondSrcAfterToUseAfterKeyCon,
//                    "?${JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key}=`${useAfterValue}`"
//                ).joinToString(String())
            }
        return "?${JsActionKeyManager.JsSubKey.AFTER.key}=${useDestiAfterId}"
//        to
//                "?${JsActionKeyManager.JsSubKey.AFTER.key}=${useDestiAfterId}"


    }

    private fun makeUseAfterMap(
        useAfterValue: String?
    ): Map<String, String>? {
        val useAfterSeparator =
                JsActionKeyManager.ActionImportManager.useAfterAllow
        return useAfterValue
            ?.split(useAfterSeparator)
            ?.map {
                it.trim()
            }?.let {
                    keyValueList ->
                val key = keyValueList.firstOrNull()
                    ?: return@let emptyMap<String, String>()
                val value = keyValueList.getOrNull(1)
                    ?: key
                mapOf(key to value)
            }
    }


    private fun makeKeyToSubConPairListByValidKey(
        keyToSubKeyCon: String?,
    ): List<Pair<String, String>> {
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsMakePaier.txt").absolutePath,
//            listOf(
//                "keyToSubKeyCon: ${keyToSubKeyCon}",
//                "map: ${CmdClickMap.createMap(
//                    keyToSubKeyCon,
//                    keySeparator
//                )}",
//                "filterMap: ${CmdClickMap.createMap(
//                    keyToSubKeyCon,
//                    keySeparator
//                ).filter {
//                    val mainKey = it.first
//                    jsActionsKeyPlusList.contains(mainKey)
//                }.map {
//                    val mainKey = it.first
//                    val subKeyAfterStr = it.second
//                    mainKey to subKeyAfterStr
//                }}"
//            ).joinToString("\n\n")
//        )
        return CmdClickMap.createMap(
            keyToSubKeyCon,
            keySeparator
        ).filter {
            val mainKey = it.first
            jsActionsKeyPlusList.contains(mainKey)
        }.map {
            val mainKey = it.first
            val subKeyAfterStr = it.second
            mainKey to subKeyAfterStr
        }
    }
}

private object PairToMapInList {

    private val jsMainKeyName = JsActionKeyManager.JsActionsKey.JS.key
    private val jsFuncMainKeyName = JsActionKeyManager.JsActionsKey.JS_FUNC.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key
    private val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
    private val afterSubKeyName = JsActionKeyManager.JsSubKey.AFTER.key
    private const val jsSubKeySeparator = '?'
    fun convert(
        keyToSubKeyConListByValidKey: List<Pair<String, String>>
    ): List<Pair<String, Map<String, String>>> {
        var beforeVarName = JsActionKeyManager.noDefinitionBeforeVarNameByPrev
        return keyToSubKeyConListByValidKey.mapIndexed { index, el ->
            val mainJsKeyName = el.first
            val mapConSrc = el.second
            val mainKey = JsActionKeyManager.JsActionsKey.values().firstOrNull {
                it.key == mainJsKeyName
            } ?: return@mapIndexed String() to emptyMap()
            when (mainKey) {
                JsActionKeyManager.JsActionsKey.ACTION_IMPORT,
                -> String() to emptyMap()

                JsActionKeyManager.JsActionsKey.JS,
                JsActionKeyManager.JsActionsKey.OVERRIDE,
//                JsActionKeyManager.JsActionsKey.REPLACE,
                -> mainKey.key to CmdClickMap.createMap(
                    QuoteTool.trimBothEdgeQuote(mapConSrc),
                    jsSubKeySeparator
                ).toMap()

                JsActionKeyManager.JsActionsKey.JS_VAR
                -> {
                    val mainKeyToSubKeyConToVarName = VarShortSyntaxToJsFunc.toJsFunc(
                        mapConSrc,
                        beforeVarName
                    )
                    val mainKeyToSubKeyCon = mainKeyToSubKeyConToVarName.first
                    val jsMap = mainKeyToSubKeyCon.second
                    jsMap.get(
                        afterSubKeyName
                    ).isNullOrEmpty().let {
                        hasNotAfterValue ->
                        val hasAfterValue = !hasNotAfterValue
                        if(hasAfterValue) return@let
                        beforeVarName = mainKeyToSubKeyConToVarName.second
                    }
                    mainKeyToSubKeyCon
                }

                JsActionKeyManager.JsActionsKey.JS_FUNC
                -> convertFuncToJsFunc(
                    mapConSrc,
                )
                JsActionKeyManager.JsActionsKey.JS_PATH
                -> convertJsPathToJsFunc(
                    mapConSrc
                )

                JsActionKeyManager.JsActionsKey.TSV_IMPORT -> {
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "tsvComp.txt").absolutePath,
//                        listOf(
//                            "mapConSrc: ${mapConSrc}",
//                            "comp: ${QuoteTool.trimBothEdgeQuote(mapConSrc)}"
//                        ).joinToString("\n\n") + "\n\n-------\n\n"
//                    )
                    convertToTsvImportMap(
                        QuoteTool.trimBothEdgeQuote(mapConSrc),
                    )
                }
                JsActionKeyManager.JsActionsKey.JS_IMPORT ->
                    convertToImportMap(
                        mainKey,
                        QuoteTool.trimBothEdgeQuote(mapConSrc),
                    )
            }
        }
    }
    private fun convertToTsvImportMap(
        mapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val commonKeysRegex =
            JsActionKeyManager.CommonPathKey.values().map {
                it.key
            }.joinToString("|")
        val regexStr = "[?|&](${commonKeysRegex})="
        val compMapConSrc = compSrcMapCon(
            mapConSrc,
            regexStr,
        )
        val importPathKey = JsActionKeyManager.CommonPathKey.IMPORT_PATH.key
        val tsvImportKey =
            JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
        val tsvImportMapCon = listOf(
            importPathKey,
            compMapConSrc
        ).joinToString("=")

//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "tsvImpotJsac.txt").absolutePath,
//            listOf(
//                "mapConSrc: ${mapConSrc}",
//                "compMapConSrc: ${compMapConSrc}",
//                "tsvImportMap: ${CmdClickMap.createMap(
//                    tsvImportMapCon,
//                    jsSubKeySeparator
//                ).toMap()}",
//            ).joinToString("\n\n") + "\n---\n"
//        )
        val tsvImportMap = CmdClickMap.createMap(
            tsvImportMapCon,
            jsSubKeySeparator
        ).toMap()
//        val pathCon = tsvImportMapSrc.get(
//            pathKey,
//        ).let {
//            JsActionKeyManager.PathExistChecker.makeCodeOrPath(it)
//        }
//        val tsvImportMap = tsvImportMapSrc.map {
//            val key = it.key
//            if(
//                key != pathKey
//            ) return@map key to it.value
//            key to pathCon
//        }.toMap()
        return tsvImportKey to tsvImportMap
    }

    private fun compSrcMapCon(
        mapConSrc: String,
        regexStr: String,
    ): String {
        val checkQuote = listOf(
            "`",
            "\"",
            "'"
        )
        checkQuote.forEach {
            val curRegexStr = "${it}${regexStr}${it}"
            val curRegex = curRegexStr.toRegex()
            val isNotBoth = !mapConSrc.startsWith(it)
                    && !mapConSrc.endsWith(it)
            val isHit = curRegex.containsMatchIn(mapConSrc)
            val isCorrect = !(isHit && isNotBoth)
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "tsvImpotJsacComp.txt").absolutePath,
//                listOf(
//                    "it: ${it}",
//                    "mapConSrc: ${mapConSrc}",
//                    "curRegexStr: ${curRegexStr}",
//                    "isNotBoth: ${isNotBoth}",
//                    "isHit: ${isHit}",
//                    "isIrregular: ${isCorrect}"
//                ).joinToString("\n\n") + "\n---\n"
//            )
            if(
                isCorrect
            ) return@forEach
            return "${it}${mapConSrc}${it}"
        }
        return mapConSrc
    }

    private fun convertToImportMap(
        jsActionMainKey: JsActionKeyManager.JsActionsKey,
        importListCon: String,
    ): Pair<String, Map<String, String>> {
        val importMap = mapOf(
            JsActionKeyManager.CommonPathKey.IMPORT_PATH.key to importListCon
        )
        return jsActionMainKey.key to importMap
    }

    private fun convertFuncToJsFunc(
        jsPathMapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val funcConPairListSrcBeforeFilter = CmdClickMap.createMap(
            "${jsFuncMainKeyName}=${jsPathMapConSrc}",
            '?'
        )
        val whenOnlySubKeyName =
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.CommonOnlySubKey.WHEN.key
        val ifSubKeyName =
            JsActionKeyManager.JsSubKey.IF.key
        val funcConPairListSrc = JsActionKeyManager.OnlySubKeyMapForShortSyntax.filterForFunc(
            funcConPairListSrcBeforeFilter
        )?.map {
                subKeyToCon ->
            val key = subKeyToCon.first
            when(key == whenOnlySubKeyName){
                true -> ifSubKeyName to subKeyToCon.second
                else -> subKeyToCon.first to subKeyToCon.second
            }
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcFunc.txt").absolutePath,
//            listOf(
//                "jsPathMapConSrc: ${jsPathMapConSrc}",
//                "jsPathConPairListSrcBeforeFilter: ${jsPathConPairListSrcBeforeFilter}",
//                "jsPathConPairListSrc: ${jsPathConPairListSrc}",
//            ).joinToString("\n\n")
//        )
        val onlySubKeyMapToFuncConPairList =
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.extractForFunc(
                funcConPairListSrc
            )
        val onlySubKeyMapSrc = onlySubKeyMapToFuncConPairList.first
        val actionImportVirtualSubKey =
            JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
        val jsActionImportMarkMap = onlySubKeyMapSrc.filterKeys {
                subKey ->
            val isActionImportVirtualSubKey =
                subKey == actionImportVirtualSubKey
            isActionImportVirtualSubKey
        }
        val onlySubKeyMap = onlySubKeyMapSrc.filterKeys {
                subKey ->
            val isNotActionImportVirtualSubKey =
                subKey != actionImportVirtualSubKey
            isNotActionImportVirtualSubKey
        }
        val funcConPairList =
            onlySubKeyMapToFuncConPairList.second
                ?: emptyList()
        val funcCon = CmdClickMap.getFirst(
            funcConPairList,
            jsFuncMainKeyName
        ) ?: return String() to mapOf()
        val argsMapCon = CmdClickMap.getFirst(
            funcConPairList,
            argsSubKeyName
        ) ?: String()
        val jsFuncCon = QuoteTool.trimBothEdgeQuote(funcCon)
        return macroOrJsInterToJsFuncForFunc(
            jsFuncCon,
            argsMapCon,
            jsActionImportMarkMap,
            onlySubKeyMap
        )
//        val isJsPathCon = File(jsFuncCon).isFile
//        return when (true) {
//            isJsPathCon -> toJsFuncForPath(
//                jsFuncCon,
//                argsMapCon,
//                onlySubKeyMap
//            )
//            else ->
//        }
    }

    private fun convertJsPathToJsFunc(
        jsPathMapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val jsPathPairListSrcBeforeFilter = CmdClickMap.createMap(
            "${jsFuncMainKeyName}=${jsPathMapConSrc}",
            '?'
        )
        val jsPathConPairListSrc = JsActionKeyManager.OnlySubKeyMapForShortSyntax.filterForFunc(
            jsPathPairListSrcBeforeFilter
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcFunc.txt").absolutePath,
//            listOf(
//                "jsPathMapConSrc: ${jsPathMapConSrc}",
//                "jsPathConPairListSrcBeforeFilter: ${jsPathConPairListSrcBeforeFilter}",
//                "jsPathConPairListSrc: ${jsPathConPairListSrc}",
//            ).joinToString("\n\n")
//        )
        val onlySubKeyMapToJsPathPairList =
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.extractForFunc(
                jsPathConPairListSrc
            )
        val onlySubKeyMapSrc = onlySubKeyMapToJsPathPairList.first
        val actionImportVirtualSubKey =
            JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
        val jsActionImportMarkMap = onlySubKeyMapSrc.filterKeys {
                subKey ->
            val isActionImportVirtualSubKey =
                subKey == actionImportVirtualSubKey
            isActionImportVirtualSubKey
        }
        val onlySubKeyMap = onlySubKeyMapSrc.filterKeys {
                subKey ->
            val isNotActionImportVirtualSubKey =
                subKey != actionImportVirtualSubKey
            isNotActionImportVirtualSubKey
        }
        val jsPathConPairList =
            onlySubKeyMapToJsPathPairList.second
                ?: emptyList()
        val jsPathCon = CmdClickMap.getFirst(
            jsPathConPairList,
            jsFuncMainKeyName
        ) ?: return String() to mapOf()
        val argsMapCon = CmdClickMap.getFirst(
            jsPathConPairList,
            argsSubKeyName
        ) ?: String()
        val jsFuncCon = QuoteTool.trimBothEdgeQuote(jsPathCon)
        return toJsFuncForPath(
            jsFuncCon,
            argsMapCon,
            onlySubKeyMap,
            jsActionImportMarkMap

        )
    }

    private fun macroOrJsInterToJsFuncForFunc(
        jsPathCon: String,
        argsMapCon: String,
        jsActionImportMarkMap: Map<String, String>,
        onlySubKeyMap: Map<String, String>,
    ): Pair<String, Map<String, String>> {
        val jsKeyCon = jsActionImportMarkMap + mapOf(
            funcSubKeyName to jsPathCon,
            argsSubKeyName to argsMapCon,
        ) + onlySubKeyMap
        return jsMainKeyName to jsKeyCon
    }

    private fun toJsFuncForPath(
        jsPathCon: String,
        argsMapCon: String,
        onlySubKeyMap: Map<String, String>,
        jsActionImportMarkMap: Map<String, String>,
    ): Pair<String, Map<String, String>> {
        val repMapPrefix = "repMapCon"
        val argsSrc = argsMapCon.ifEmpty {
            "${repMapPrefix}=CMDCLICK_BLANK_ARGS=-"
        }
//        val jsPathStr = JsActionKeyManager.PathExistChecker.makeCodeOrPath(
//            jsPathCon
//        )
        val argsCon = listOf(
            "path=${jsPathCon}",
            "${repMapPrefix}=${argsSrc}"
        ).joinToString("&")
        val jsKeyCon = jsActionImportMarkMap + mapOf(
            funcSubKeyName to "jsUrl.loadJsPath",
            JsActionKeyManager.JsSubKey.ARGS.key to argsCon,
            JsActionKeyManager.JsSubKey.DESC.key to "path: ${jsPathCon}",
        ) + onlySubKeyMap
        return jsMainKeyName to jsKeyCon
    }
}

private object VarShortSyntaxToJsFunc {

    private val jsVarMainKeyName = JsActionKeyManager.JsActionsKey.JS_VAR.key
    private val varSubKeyName = JsActionKeyManager.JsSubKey.VAR.key
    private val jsMainKeyName = JsActionKeyManager.JsActionsKey.JS.key
    private val varValueSubKeyName = JsActionKeyManager.JsSubKey.VAR_VALUE.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key
    private val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
    private val ifSubKeyName = JsActionKeyManager.JsSubKey.IF.key
    private val whenOnlySubKeyName =
        JsActionKeyManager.OnlySubKeyMapForShortSyntax.CommonOnlySubKey.WHEN.key
    private const val jsSubKeySeparator = '?'
    private const val itPronoun = JsActionKeyManager.JsVarManager.itPronoun
    private const val prevPronoun = JsActionKeyManager.prevPronoun
    private val itSuggerVarRegex = Regex("([^a-zA-Z0-9_])${itPronoun}([^a-zA-Z0-9_])")
    private val itSuggerVarRegexEndVer = Regex("([^a-zA-Z0-9_])${itPronoun}$")
    fun toJsFunc(
        varMapConSrc: String,
        beforeVarName: String,
    ): Pair<Pair<String, Map<String, String>>,String> {
        val varNameSrc = QuoteTool.trimBothEdgeQuote(
            varMapConSrc.split(jsSubKeySeparator)
                .firstOrNull() ?: String()
        )
        val varName = when(varNameSrc == prevPronoun){
            true -> beforeVarName
            else -> varNameSrc
        }
        val varMapCon = varMapConSrc.split(jsSubKeySeparator).mapIndexed {
            index, subKeyCon ->
            if(
                index == 0
            ) return@mapIndexed varName
            subKeyCon
        }.joinToString(jsSubKeySeparator.toString())
            .replace(itSuggerVarRegex, "$1${varName}$2")
            .replace(itSuggerVarRegexEndVer, "$1${varName}")
        val varMapConPairListSrcBeforeFilter = CmdClickMap.createMap(
            "${jsVarMainKeyName}=${varMapCon}",
            jsSubKeySeparator
        )
        val varMapConPairListSrc = JsActionKeyManager.OnlySubKeyMapForShortSyntax.filterForVar(
            varMapConPairListSrcBeforeFilter
        )
        val onlySubKeyMapToVarMapConPairList =
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.extractForVar(
                varMapConPairListSrc
            )
        val onlySubKeyMapSrc = onlySubKeyMapToVarMapConPairList.first.map {
            onlyMapEntry ->
            val key = onlyMapEntry.key
            when(key == whenOnlySubKeyName){
                true -> ifSubKeyName to onlyMapEntry.value
                else -> onlyMapEntry.key to onlyMapEntry.value
            }
        }.toMap()
        val varMapConPairList = onlySubKeyMapToVarMapConPairList.second
            ?: emptyList()
//        val onlyIfSubKeyMapToVarMapConPairListExcludeIfSubKey = extractFirstInExcludePairList(
//            varMapConPairListBeforeExcludeFirstIf
//        )
//        val onlyIfMap =
//            onlyIfSubKeyMapToVarMapConPairListExcludeIfSubKey.first
//        val varMapConPairList =
//            onlyIfSubKeyMapToVarMapConPairListExcludeIfSubKey.second
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc.txt").absolutePath,
//            listOf(
//                "varMapConSrc: ${varMapConSrc}",
//                "varMapConPairListSrcBeforeFilter: ${varMapConPairListSrcBeforeFilter}",
//                "varMapConPairListSrc: ${varMapConPairListSrc}",
////                "varMapConPairListBeforeExcludeFirstIf: ${varMapConPairListBeforeExcludeFirstIf}",
////                "onlyIfMap: ${onlyIfMap}",
//                "onlySubKeyMapToVarMapConPairList: ${onlySubKeyMapToVarMapConPairList}",
//                "onlySubKeyMapSrc: ${onlySubKeyMapSrc}",
//                "varMapConPairList: ${varMapConPairList}"
//            ).joinToString("\n\n") + "\n--------\n"
//        )
        val jsVarName = CmdClickMap.getFirst(
            varMapConPairList,
            jsVarMainKeyName
        ) ?: return (String() to mapOf<String, String>()) to varName
        val nextIndex = 1
        val valueOrIfConList = makeVarKeyToConPairListForJsVarMacro(
            varMapConPairList,
            nextIndex
        )
        val valueOrFuncMapToSeedIndex = extractFirstValueOrFuncMap(
            valueOrIfConList,
        )
        val valueOrFuncMapSrc = valueOrFuncMapToSeedIndex.first
        val valueOrFuncMap = when(
            valueOrFuncMapSrc.isEmpty()
        ){
          true -> mapOf(
              JsActionKeyManager.VirtualSubKey.VAR_NOT_INIT.key to jsVarName
          )
          else -> valueOrFuncMapSrc
        }
        val seedIndex = valueOrFuncMapToSeedIndex.second
        val nextNextIndex =
            nextIndex + seedIndex

        val nextVarKeyToConPairList = makeVarKeyToConPairListForJsVarMacro(
            varMapConPairList,
            nextNextIndex
        )
        val actionImportVirtualSubKey =
            JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
        val jsActionImportMarkMap = onlySubKeyMapSrc.filterKeys {
            subKey ->
            val isActionImportVirtualSubKey =
                subKey == actionImportVirtualSubKey
            isActionImportVirtualSubKey
        }
        val onlySubKeyMap = onlySubKeyMapSrc.filterKeys {
                subKey ->
            val isNotActionImportVirtualSubKey =
                subKey != actionImportVirtualSubKey
            isNotActionImportVirtualSubKey
        }
        val jsKeyConMapSrc = jsActionImportMarkMap + mapOf(
            varSubKeyName to jsVarName,
        ) + valueOrFuncMap +
                onlySubKeyMap +
                extractAfterJsConForVar(
                    jsVarName,
                    nextVarKeyToConPairList
                )
        val jsKeyConMap = jsKeyConMapSrc.filterKeys { it.isNotEmpty() }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc2.txt").absolutePath,
//            listOf(
//                "valueOrFuncMap: ${valueOrFuncMap}",
//                "varMapConSrc: ${varMapConSrc}",
//                "varMapConPairListSrcBeforeFilter: ${varMapConPairListSrcBeforeFilter}",
//                "varMapConPairListSrc: ${varMapConPairListSrc}",
////                "varMapConPairListBeforeExcludeFirstIf: ${varMapConPairListBeforeExcludeFirstIf}",
////                "onlyIfMap: ${onlyIfMap}",
//                "onlySubKeyMapToVarMapConPairList: ${onlySubKeyMapToVarMapConPairList}",
//                "onlySubKeyMap: ${onlySubKeyMap}",
//                "varMapConPairList: ${varMapConPairList}",
//                "valueOrIfConList: ${valueOrIfConList}",
//                "nextVarKeyToConPairList: ${nextVarKeyToConPairList}",
//                "jsKeyConMapSrc: ${jsKeyConMapSrc}"
//            ).joinToString("\n\n") + "\n---\n"
//        )
        return (jsMainKeyName to jsKeyConMap) to varName
    }

//    private fun extractFirstInExcludePairList(
//        varMapConPairListSrc: List<Pair<String, String>>
//    ): Pair<
//            Map<String, String>,
//            List<Pair<String, String>>
//            >{
//        val untilSubKeyList = listOf(
//            varValueSubKeyName,
//            funcSubKeyName,
//        )
//        val untilSubKeyIndex = varMapConPairListSrc.indexOfFirst {
//            val subKeyName = it.first
//            untilSubKeyList.contains(subKeyName)
//        }
//        val defaultNoExistIfMap = emptyMap<String, String>()
//        if(
//            untilSubKeyIndex < 0
//        ) return defaultNoExistIfMap to varMapConPairListSrc
//        val ifSubKeyContainEntryPairList =
//            varMapConPairListSrc.take(untilSubKeyIndex)
//        val firstIfPair = ifSubKeyContainEntryPairList.firstOrNull {
//            val subKeyName = it.first
//            subKeyName == whenOnlySubKeyName
//        } ?: return defaultNoExistIfMap to varMapConPairListSrc
//        val firstIfCondition = firstIfPair.second
//        if(
//            firstIfCondition.isEmpty()
//        ) return defaultNoExistIfMap to varMapConPairListSrc
//        var firstMatch = true
//        val varMapConPairListExcludeFirstIf = varMapConPairListSrc.filter {
//            if(
//                firstIfPair == it
//                && firstMatch
//            ) {
//                firstMatch = false
//                return@filter false
//            }
//            true
//        }
//        return mapOf(
//            firstIfPair
//        ) to varMapConPairListExcludeFirstIf
//    }

    private fun extractFirstValueOrFuncMap(
        valueOrIfConList: List<Pair<String, String>>?,
    ): Pair<Map<String, String>, Int> {
        val seedIndex = 1
        val defaultBlankMap = emptyMap<String, String>()
        if(
            valueOrIfConList.isNullOrEmpty()
        ) return defaultBlankMap to seedIndex
        val firstPair =
            valueOrIfConList.firstOrNull()
                ?: return defaultBlankMap to seedIndex
        val subKey = firstPair.first
        val valOrFuncCon = firstPair.second
        return when(subKey){
            varValueSubKeyName ->
                mapOf(
                    varValueSubKeyName to QuoteTool.trimBothEdgeQuote(valOrFuncCon)
                ) to seedIndex
            funcSubKeyName ->
                extractFuncMap(
                    valueOrIfConList,
                    valOrFuncCon
                )
            else -> defaultBlankMap to seedIndex
        }
    }

    private fun extractFuncMap(
        valueOrIfConList: List<Pair<String, String>>?,
        funcCon: String
    ): Pair<Map<String, String>, Int> {
        val seedIndex = 1
        val seedX2Index = 2
        val funcSubKeyToConMap = mapOf(
            funcSubKeyName to QuoteTool.trimBothEdgeQuote(funcCon)
        )
        val argsPair = valueOrIfConList?.getOrNull(1)
        val argsSubKeyEntry = argsPair?.first
        if(
            argsSubKeyEntry != argsSubKeyName
        ) return funcSubKeyToConMap to seedIndex
        val argsArgsEntry = argsPair.second
        if(
            argsArgsEntry.isEmpty()
        ) return funcSubKeyToConMap to seedIndex
        return funcSubKeyToConMap + mapOf(
            argsSubKeyName to argsArgsEntry
        ) to seedX2Index

    }
    private fun makeVarKeyToConPairListForJsVarMacro(
        keyToSubKeyCon: List<Pair<String, String>>?,
        nextNextIndex: Int
    ): List<Pair<String, String>>? {
        if(
            keyToSubKeyCon.isNullOrEmpty()
            || keyToSubKeyCon.getOrNull(nextNextIndex) == null
        ) return null
        return keyToSubKeyCon.filterIndexed { index, _ -> index >= nextNextIndex  }
    }

    private fun extractAfterJsConForVar(
        varName: String,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?
    ): Map<String, String> {
        val defaultBlankMap = emptyMap<String, String>()
        if(
            nextValueOrFuncOrIfConList.isNullOrEmpty()
        ) return defaultBlankMap
        val jsAfterConSeparator =
            JsActionKeyManager.AfterJsConMaker.afterJsConSeparator
        var index = 0
        val lastIndex = nextValueOrFuncOrIfConList.lastIndex
        val afterJsConList = mutableListOf<String>()
        run loop@ {
            nextValueOrFuncOrIfConList.indices.forEach {
                    _ ->
                if(
                    index > lastIndex
                ) return@loop
                val keyToCon = nextValueOrFuncOrIfConList.getOrNull(index)
                    ?: return@loop
                val subKeyName = keyToCon.first
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsuseVarInex.txt").absolutePath,
//                    listOf(
//                        "index: $index",
//                        "keyToCon: ${keyToCon}",
//                        "subKeyName: ${subKeyName}",
//                    ).joinToString("\n\n") + "\n-------\n\n"
//                )
                val useKeyForAfterJsConForVar = JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.values().firstOrNull {
                    it.key == subKeyName
                }
                if(
                    useKeyForAfterJsConForVar == null
                ){
                    index++
                    return@forEach
                }
                val curAfterJsConToIndex = makeAfterJsConToIndex(
                    useKeyForAfterJsConForVar,
                    varName,
                    keyToCon,
                    nextValueOrFuncOrIfConList,
                    index,
                )
                val curAfterJsCon = curAfterJsConToIndex.first
                val futureIndex = curAfterJsConToIndex.second
                index = futureIndex
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsAcVar.txt").absolutePath,
//                    listOf(
//                        "index: $index",
//                        "curAfterJsCon: $curAfterJsCon",
//                        "afterJsConList: ${afterJsConList}"
//                    ).joinToString("\n\n") + "\n------\n\n"
//                )
                afterJsConList.add(curAfterJsCon)
            }
        }
        val afterJsCon = afterJsConList.filter { it.isNotEmpty() }
            .joinToString(
                jsAfterConSeparator.toString()
            ).replace("\n", "")
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcVar.txt").absolutePath,
//            listOf(
//                "afterJsConList: ${afterJsConList.joinToString("\n")}",
//                "afterJsCon: ${afterJsCon}"
//            ).joinToString("\n\n") + "\n------\n\n"
//        )
        return mapOf(
            JsActionKeyManager.JsSubKey.AFTER_JS_CON.key
                    to afterJsCon
        )
    }

    private fun makeAfterJsConToIndex(
        useKeyForAfterJsConForVar: JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar,
        varName: String,
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>,
        index: Int,
    ): Pair<String, Int> {
        return when (useKeyForAfterJsConForVar) {
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.VAR_VALUE
            -> makeVarSentence(
                varName,
                keyToCon,
                nextValueOrFuncOrIfConList,
                index,
            )
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.FUNC
            -> makeFuncSentence(
                varName,
                keyToCon,
                nextValueOrFuncOrIfConList,
                index,
            )
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.VAR_RETURN
            -> makeVarReturnSentence(
                keyToCon,
                nextValueOrFuncOrIfConList,
                index,
            )
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.EXIT
            -> makeExitSentence(
                keyToCon,
                nextValueOrFuncOrIfConList,
                index,
            )
//            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.USE_VAR
//            -> {
//                val bringVarName =
//                    QuoteTool.trimBothEdgeQuote(keyToCon.second)
//                makeUseVarSentence(
//                    nextValueOrFuncOrIfConList,
//                    bringVarName,
//                    index + 1,
//                )
//            }
//            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.COLLECTION_METHOD_START
//            -> makeCollectionMethodStartSentence(
//                varName,
//                keyToCon,
//                nextValueOrFuncOrIfConList,
//                index,
//            )
//            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.COLLECTION_METHOD_END_RETURN
//            -> makeCollectionMethodEndSentence(
//                index,
//                keyToCon,
//            )
        }
    }

//    private fun makeUseVarSentence(
//        nextValueOrFuncOrIfConList: List<Pair<String, String>>,
//        varName: String,
//        index: Int,
//    ): Pair<String, Int> {
//        val curKeyToCon = nextValueOrFuncOrIfConList.getOrNull(index)
//            ?: return String() to index + 1
//        val subKeyName = curKeyToCon.first
//        val useVarKeyForAfterJsConForVar = JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseVarKeyForAfterJsConForVar.values().firstOrNull {
//            it.key == subKeyName
//        }
//        val ifBaseIndex = index - 1
//        val curAfterJsConToIndex = when (useVarKeyForAfterJsConForVar) {
//            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseVarKeyForAfterJsConForVar.VAR_VALUE
//            -> makeVarSentence(
//                varName,
//                curKeyToCon,
//                nextValueOrFuncOrIfConList,
//                index,
//                ifBaseIndex
//            )
//            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseVarKeyForAfterJsConForVar.FUNC
//            -> makeFuncSentence(
//                varName,
//                curKeyToCon,
//                nextValueOrFuncOrIfConList,
//                index,
//                ifBaseIndex
//            )
////            JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseVarKeyForAfterJsConForVar.COLLECTION_METHOD_START
////            -> makeCollectionMethodStartSentence(
////                varName,
////                curKeyToCon,
////                nextValueOrFuncOrIfConList,
////                index,
////            )
//            else -> String() to index + 1
//        }
//        return curAfterJsConToIndex
//    }

    private fun addIfSentence(
        varOrFuncSentence: String,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
    ): String {
        val jsAfterConSeparator =
            JsActionKeyManager.AfterJsConMaker.afterJsConSeparator
        val ifIndexSrc = index - 1
        if(
            ifIndexSrc < 0
        ) return varOrFuncSentence
        val ifEntryKeyToCon =
            nextValueOrFuncOrIfConList?.getOrNull(ifIndexSrc)
                ?: return varOrFuncSentence
        val isNotIfKey = ifEntryKeyToCon.first != whenOnlySubKeyName
        if(
            isNotIfKey
        ) return varOrFuncSentence
        val ifAfterSentence = listOf(
            JsActionKeyManager.AfterJsConMaker.ifSentence,
            "`${ifEntryKeyToCon.second}`"
        ).joinToString("=")
        return listOf(
            ifAfterSentence,
            varOrFuncSentence,
        ).joinToString(jsAfterConSeparator.toString())
    }

    private fun makeFuncSentence(
        varName: String,
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
        ifBaseIndexSrc: Int? = null
    ): Pair<String, Int> {
        val funcConSrc =
            QuoteTool.trimBothEdgeQuote(keyToCon.second)
        if(
            funcConSrc.isEmpty()
        ) return String() to index + 1
        val argsIndex = index + 1
        val funcSentenceToInt = execMakeFuncSentence(
            varName,
            funcConSrc,
            nextValueOrFuncOrIfConList,
            argsIndex,
        )
        val funcSentence = funcSentenceToInt.first
        val futureIndex = funcSentenceToInt.second
        val ifBaseIndex = makeIfBaseIndex(
            ifBaseIndexSrc,
            index,
        )
        return addIfSentence(
            funcSentence,
            nextValueOrFuncOrIfConList,
            ifBaseIndex,
        ) to futureIndex
    }

//    private fun makeCollectionMethodEndSentence(
//        index: Int,
//        keyToCon: Pair<String, String>,
//    ): Pair<String, Int> {
//        val futureIndex = index + 1
//        val returnValue = JsActionKeyManager.NoQuoteHandler.make(
//            QuoteTool.trimBothEdgeQuote(keyToCon.second)
//        )
//        return listOf(
//            "end collection method",
//            "return ${returnValue}; });"
//        ).joinToString("=") to futureIndex
//    }

//    private fun makeCollectionMethodStartSentence(
//        varName: String,
//        keyToCon: Pair<String, String>,
//        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
//        index: Int,
//    ): Pair<String, Int> {
//        val editVarNameDotCollectionMethodNameSrc =
//            QuoteTool.trimBothEdgeQuote(keyToCon.second)
//        val collectionMethodNameSrc = editVarNameDotCollectionMethodNameSrc
//            .split(".")
//            .lastOrNull()
//            ?: return String() to index + 1
//        if(
//            editVarNameDotCollectionMethodNameSrc.isEmpty()
//        ) return String() to index + 1
//        val enableCollectionMethod =
//            JsActionKeyManager.CollectionMethodManager.EnableCollectionMethod.values()
//                .firstOrNull {
//                    it.method == collectionMethodNameSrc
//                } ?: return String() to index + 1
//
//        val argsIndex = index + 1
//        val collMethodStartSentenceToFutureIndex = execMakeCollectionMethodStartSentence(
//            varName,
//            enableCollectionMethod,
//            editVarNameDotCollectionMethodNameSrc,
//            nextValueOrFuncOrIfConList,
//            argsIndex,
//        )
//        return collMethodStartSentenceToFutureIndex
//    }


//    private fun execMakeCollectionMethodStartSentence(
//        varName: String,
//        enableCollectionMethod: JsActionKeyManager.CollectionMethodManager.EnableCollectionMethod,
//        editVarNameDotCollectionMethodName: String,
//        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
//        argsIndex: Int,
//    ): Pair<String, Int> {
//
//        val collMethodArgsNameListToFutureIndex = makeCollMethodArgsList(
//            nextValueOrFuncOrIfConList,
//            argsIndex,
//        )
//        val collMethodArgsNameList = collMethodArgsNameListToFutureIndex.first
//        val futureIndex = collMethodArgsNameListToFutureIndex.second
//
//        val elementValName = getCollMethodArgName(
//            collMethodArgsNameList,
//            0,
//            JsActionKeyManager.JsFuncManager.DefaultLoopArgsName.EL.default
//        )
//        val elementArgName = elementValName + "Src"
//        val indexArgName = getCollMethodArgName(
//            collMethodArgsNameList,
//            1,
//            JsActionKeyManager.JsFuncManager.DefaultLoopArgsName.INDEX.default
//        )
//        val collMethodStartCon = when(enableCollectionMethod) {
//            JsActionKeyManager.CollectionMethodManager.EnableCollectionMethod.FILTER -> {
//                val boolValName = getCollMethodArgName(
//                    collMethodArgsNameList,
//                    2,
//                    JsActionKeyManager.JsFuncManager.DefaultLoopArgsName.BOOL.default
//                )
//                listOf(
//                    listOf("${editVarNameDotCollectionMethodName}(function(${elementArgName}, ${indexArgName}){"),
//                    listOf(
//                        "var ${elementValName} = ${elementArgName};",
//                        "var ${boolValName} = true;",
//                    ).map { "\t${it}" },
////                    listOf("})${method};"),
//                ).flatten().joinToString("\n")
//            }
//            JsActionKeyManager.CollectionMethodManager.EnableCollectionMethod.MAP -> {
//                listOf(
//                    listOf("${editVarNameDotCollectionMethodName}(function(${elementArgName}, ${indexArgName}){"),
//                    listOf(
//                        "var ${elementValName} = ${elementArgName};",
//                    ).map { "\t${it}" },
////                    listOf("})${method};")
//                ).flatten().joinToString("\n")
//            }
//            JsActionKeyManager.CollectionMethodManager.EnableCollectionMethod.FOR_EACH -> {
//                listOf(
//                    listOf("${editVarNameDotCollectionMethodName}(function(${elementArgName}, ${indexArgName}){"),
//                    listOf(
//                        "var ${elementValName} = ${elementArgName};",
//                    ).map { "\t${it}" },
////                    listOf("});")
//                ).flatten().joinToString("\n")
//            }
//        }
//        val collMethodStartConWithVar = listOf(
//            varName,
//            collMethodStartCon
//        ).joinToString("=")
//        return collMethodStartConWithVar to futureIndex
//    }


//    private fun makeCollMethodArgsList(
//        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
//        argsIndex: Int,
//    ): Pair<List<String>, Int> {
//        val argsSeparator = '&'
//        val defaultCollMethodArgs =
//            listOf("el", "index", "bool")
//        val defaultCollMethodArgsToIndex = defaultCollMethodArgs to argsIndex
//        val argsNameToCon = nextValueOrFuncOrIfConList?.getOrNull(argsIndex)
//            ?: return defaultCollMethodArgsToIndex
//
//        val argsKeyEntry = argsNameToCon.first
//        if(
//            argsKeyEntry != JsActionKeyManager.OnlyVarSubKey.COLLECTION_METHOD_ARGS.key
//        ) return defaultCollMethodArgsToIndex
//        val argsCon = argsNameToCon.second
//        val collMethodArgsList = CmdClickMap.createMap(
//            argsCon,
//            argsSeparator,
//        ).map { it.second }
//        val futureIndex = argsIndex + 1
//        return defaultCollMethodArgs.mapIndexed {
//            index, arg ->
//            val curArg = collMethodArgsList.getOrNull(index)
//            when(curArg.isNullOrEmpty()){
//                true -> arg
//                else -> curArg
//            }
//        } to futureIndex
//    }

//    private fun getCollMethodArgName(
//        loopArgsNameList: List<String>,
//        index: Int,
//        defaultArgName: String
//    ): String {
//        return loopArgsNameList.getOrNull(index).let {
//            if(
//                it.isNullOrEmpty()
//            ) return@let defaultArgName
//            it
//        }
//    }


    private fun makeVarReturnSentence(
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
    ): Pair<String, Int> {
        val varReturnValue =
            QuoteTool.trimBothEdgeQuote(keyToCon.second)

        val returnSentenceToFutureIndex = execMakeReturnSentence(
            varReturnValue,
            index
        )
        val returnSentence = returnSentenceToFutureIndex.first
        val futureIndex = returnSentenceToFutureIndex.second
        return addIfSentence(
            returnSentence,
            nextValueOrFuncOrIfConList,
            index,
        ) to futureIndex
    }

    private fun execMakeReturnSentence(
        returnValueSrc: String,
        index: Int,
    ): Pair<String, Int> {
        val varReturnSentence =
            JsActionKeyManager.NoQuoteHandler.makeForVarReturn(returnValueSrc)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
//            listOf(
//                "returnValueSrc: ${returnValueSrc}",
//                "returnValue: ${varReturnSentence}"
//            ).joinToString("\n\n")
//        )
        val futureIndex = index + 1
        return listOf(
            "var return",
            "\"${varReturnSentence}\""
        ).joinToString("=") to futureIndex
    }

    private fun makeExitSentence(
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
    ): Pair<String, Int> {
        val exitMessage =
            QuoteTool.trimBothEdgeQuote(keyToCon.second)
        val funcSentence = execMakeExitSentence(exitMessage)
        val futureIndex = index + 1
        return addIfSentence(
            funcSentence,
            nextValueOrFuncOrIfConList,
            index,
        ) to futureIndex
    }

    private fun execMakeExitSentence(
        exitMessage: String
    ): String {
        val exitJs = when(exitMessage.isEmpty()){
            true -> "exitZero()"
            else -> listOf(
                "jsToast.short(`${exitMessage}`); exitZero()"
            ).joinToString("\n")
        }
        return listOf(
            "early exit",
            "`${exitJs}`",
        ).joinToString("=")
    }

    private fun execMakeFuncSentence(
        varName: String,
        funcConSrc: String,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        argsIndex: Int,
    ): Pair<String, Int> {
        val funcSentenceTemplate = listOf(
            varName,
            "`${funcConSrc}(%s)`"
        ).joinToString("=")
        val argsNameToCon = nextValueOrFuncOrIfConList?.getOrNull(argsIndex)
            ?: return funcSentenceTemplate.format(String()) to argsIndex
        val argsKeyEntry = argsNameToCon.first
        if(
            argsKeyEntry != argsSubKeyName
        ) return funcSentenceTemplate.format(String()) to argsIndex
        val argsCon = argsNameToCon.second
        val argsOnlyJsMap =  mapOf(
            argsSubKeyName to argsCon
        )
        val varargsStr =
            JsActionKeyManager.ArgsManager.makeVarArgs(
                argsOnlyJsMap,
                argsSubKeyName
            )
        return funcSentenceTemplate.format(varargsStr) to argsIndex + 1
    }

    private fun makeVarSentence(
        varName: String,
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
        ifBaseIndexSrc: Int? = null,
    ): Pair<String, Int> {
        val noQuotePrefix = JsActionKeyManager.noQuotePrefix
        val jsConSrc = QuoteTool.trimBothEdgeQuote(keyToCon.second)
        val isNoQuotePrefix = jsConSrc.startsWith(noQuotePrefix)
        val varValue = when(isNoQuotePrefix) {
            true ->
                jsConSrc.removePrefix(noQuotePrefix)
            else ->
                listOf(
                    JsActionKeyManager.AfterJsConMaker.SignalPrefix.QUOTE.signal,
                    jsConSrc,
                ).joinToString(String())
        }
        val varSentence = listOf(
            varName,
            "`${varValue}`"
        ).joinToString("=")
        val futureIndex = index + 1
        val ifBaseIndex = makeIfBaseIndex(
            ifBaseIndexSrc,
            index,
        )
        return addIfSentence(
            varSentence,
            nextValueOrFuncOrIfConList,
            ifBaseIndex,
        ) to futureIndex
    }

    private fun makeIfBaseIndex(
        ifBaseIndexSrc: Int?,
        index: Int,
    ): Int {
        return when(ifBaseIndexSrc == null){
            true -> index
            else -> ifBaseIndexSrc
        }
    }
}

private object TsvImportConMaker {

    fun make(
        keyToSubKeyMapList: List<Pair<String, Map<String, String>>>,
    ): String {
        val tsvImportKeyName = JsActionKeyManager.JsActionsKey.TSV_IMPORT.key

        val keyToSubKeyMapListOnlyImport = keyToSubKeyMapList.filter {
                keyToSubKeyPair ->
            val mainJsKeyName = keyToSubKeyPair.first
            mainJsKeyName == tsvImportKeyName
        }
        val tsvImportPreWord = TsvImportManager.tsvImportPreWord
        return  keyToSubKeyMapListOnlyImport.map {
                keyToSubKeyMap ->
            execPut(
                tsvImportPreWord,
                keyToSubKeyMap
            )
        }.joinToString("\n")
    }
    private fun execPut(
        importPreWord: String,
        keyToSubKeyMap: Pair<String, Map<String, String>>,
    ): String {
        val tsvImportMap =
            CmdClickMap.recreateMapWithoutQuoteInKey(
                keyToSubKeyMap.second
            )
        val importImportPath = tsvImportMap.get(
            JsActionKeyManager.CommonPathKey.IMPORT_PATH.key
        ) ?: return String()
        val importMainSentence = listOf(
            importPreWord,
            importImportPath
        ).joinToString(" ")
        val useMapCon = tsvImportMap.get(
            JsActionKeyManager.CommonPathKey.USE.key
        )?.replace("|", "\n")
        val changePhrase = TsvImportManager.changePhrase
        val useMap = TsvImportManager.createMapByStrSepa(
            useMapCon,
            changePhrase,
        ).toMap()
        val tsvImportUsePhrase = TsvImportManager.tsvImportUsePhrase
        val useSentence = useMap.map {
            val key = it.key.trim()
            if(
                key.isEmpty()
            ) return@map String()
            val changeKey = it.value.trim()
            "\t${key} ${changePhrase} ${changeKey}"
        }.joinToString(",\n")
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "tsvUseMap.txt").absolutePath,
//            listOf(
//                "useCon: ${tsvImportMap.get(
//                    JsActionKeyManager.CommonPathKey.USE.key
//                )}",
//                "useMap: ${useMap}",
//                "useSentence: ${useSentence}",
//            ).joinToString("\n\n")
//        )
        return listOf(
            importMainSentence,
            "${tsvImportUsePhrase} (",
            useSentence,
            ")"
        ).joinToString("\n")
    }
}

private object ImportConMaker {

    fun make(
        importKeyNameToImportPreWord: Pair<String, String>,
        keyToSubKeyMapList: List<Pair<String, Map<String, String>>>,
    ): String {
        val importKeyName = importKeyNameToImportPreWord.first
        val keyToSubKeyMapListOnlyImport = keyToSubKeyMapList.filter {
                keyToSubKeyPair ->
            val mainJsKeyName = keyToSubKeyPair.first
            mainJsKeyName == importKeyName
        }
        val importPreWord = importKeyNameToImportPreWord.second
        return  keyToSubKeyMapListOnlyImport.map {
                keyToSubKeyMap ->
            execPut(
                importPreWord,
                keyToSubKeyMap
            )
        }.joinToString("\n")
    }

    private fun execPut(
        importPreWord: String,
        keyToSubKeyMap: Pair<String, Map<String, String>>,
    ): String {
        val tsvImportPathSeparator = '?'
        val subKeyCon = keyToSubKeyMap.second.get(
            JsActionKeyManager.CommonPathKey.IMPORT_PATH.key
        ) ?: return String()
        return QuoteTool.splitBySurroundedIgnore(
            subKeyCon,
            tsvImportPathSeparator
        ).firstOrNull()?.let {
            val importPath = QuoteTool.trimBothEdgeQuote(it)
            listOf(
                importPreWord,
                importPath
            ).joinToString(" ")
        } ?: String()
    }
}

private object JsConPutter {

    private val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
    private val varSubKeyName = JsActionKeyManager.JsSubKey.VAR.key
    private val afterSrcPrevPronoun =
        JsActionKeyManager.prevPronoun.replaceFirstChar(Char::uppercaseChar)
    private const val afterItPrefix = "after"

    private enum class LogPrefix(
        val prefix: String
    ) {
        START("start"),
        OK("ok"),
        EXIT("exit")
    }

    fun make(
        jsMapListWithoutAfterSubKey: List<Map<String, String>>,
        jsMapListWithAfterSubKey: List<Map<String, String>>,
    ): String {
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "JsConPutter.make.txt").absolutePath,
//            listOf(
//                "jsMapListWithoutAfterSubKey: ${jsMapListWithoutAfterSubKey}",
//                "jsMapListWithAfterSubKey: ${jsMapListWithAfterSubKey}",
//            ).joinToString("\n\n")
//        )
        val loopIndex = 0
        return jsMapListWithoutAfterSubKey.map {
            execMake(
                it,
                jsMapListWithAfterSubKey,
                loopIndex,
            )
        }.joinToString("\n")
    }

    private fun execMake(
        jsMap: Map<String, String>?,
        jsMapListOnlyAfter: List<Map<String, String>>,
        loopIndex: Int,
    ): String {
        if(
            jsMap.isNullOrEmpty()
        ) return String()
        jsMap.get(JsActionKeyManager.JsSubKey.IF_BRACKET_START.key)?.let {
            val ifCondition = jsMap.get(JsActionKeyManager.JsSubKey.IF.key)
            if(ifCondition.isNullOrEmpty()) return@let
            return "if(${ifCondition}){"
        }
        jsMap.get(JsActionKeyManager.JsSubKey.IF_BRACKET_END.key)?.let {
            return "}"
        }

        val functionName = jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
        if(
            functionName.isNullOrEmpty()
        ) LogSystems.stdSys(
            "func name is null: ${jsMap}"
        )
        val funcTemplate = makeFuncTemplate(jsMap)
        val enableLoopMethodType = JsActionKeyManager.JsFuncManager.howLoopMethod(
            functionName,
        )
        val insertFuncCon = when(enableLoopMethodType == null){
            false -> {
                val loopMethodTemplate =
                    makeLoopMethodTemplate(
                        jsMap,
                        enableLoopMethodType,
                    )
                makeInsertLoopMethodCon(
                    jsMap,
                    jsMapListOnlyAfter,
                    loopMethodTemplate,
                    loopIndex,
                )
            }
            else -> makeInsertFuncCon(
                jsMap,
                jsMapListOnlyAfter,
                loopIndex,
            )
        }
        return funcTemplate.format(
            insertFuncCon
        )
    }

    private fun makeInsertLoopMethodCon(
        jsMap: Map<String, String>,
        jsMapListOnlyAfter: List<Map<String, String>>,
        loopMethodTemplate: String,
        loopIndex: Int,
    ): String {
        if(
            loopMethodTemplate.isEmpty()
        ) return String()
        val loopMethodTemplateWithVar = makeFuncConWithVarOrReturn(
            jsMap,
            loopMethodTemplate,
        )
        val afterJsCon = JsActionKeyManager.AfterJsConMaker.make(jsMap)
            .replace("\n", "\n\t")
        val afterFuncCon = putAfterFunc(
            jsMap,
            jsMapListOnlyAfter,
            loopIndex,
        )
        val compLoopMethodTemplateWithVar =
            loopMethodTemplateWithVar.format(
                listOf(
                    afterJsCon,
                    afterFuncCon
                ).joinToString("\n")
            )
        val exitJudgeCon = makeFuncConWithVarExitJudge(
            jsMap,
        )

        return listOf(
            makeStartToast(jsMap),
            compLoopMethodTemplateWithVar,
            exitJudgeCon,
            makeDeleteVarCon(jsMap),
            makeEndToast(jsMap),
        ).filter{
            it.trim().isNotEmpty()
        }.joinToString("\n")
    }

    private fun makeInsertFuncCon(
        jsMap: Map<String, String>,
        jsMapListOnlyAfter:  List<Map<String, String>>,
        loopIndex: Int,
    ): String {
        val varargsStr =
            JsActionKeyManager.ArgsManager.makeVarArgs(
                jsMap,
                argsSubKeyName
            )

        val funcCon = makeFuncCon(
            jsMap,
            varargsStr,
        )
        val funcConWithVar = makeFuncConWithVarOrReturn(
            jsMap,
            funcCon,
        )
        val afterJsCon = JsActionKeyManager.AfterJsConMaker.make(jsMap)
        val exitJudgeCon = makeFuncConWithVarExitJudge(
            jsMap,
        )

        val afterFuncCon = putAfterFunc(
            jsMap,
            jsMapListOnlyAfter,
            loopIndex
        )

//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "JsConPutter.execMake.txt").absolutePath,
//            listOf(
//                "jsMap: ${jsMap}",
//                "jsMapListOnlyAfter: ${jsMapListOnlyAfter}",
//                "funcCon: ${funcCon}",
//                "funcConWithVar: ${funcConWithVar}",
//            ).joinToString("\n\n")
//        )
        return listOf(
            makeStartToast(jsMap),
            funcConWithVar,
            afterJsCon,
            exitJudgeCon,
            afterFuncCon,
            makeDeleteVarCon(jsMap),
            makeEndToast(jsMap),
        ).filter {
            it.trim().isNotEmpty()
        }.joinToString("\n")
    }

    private fun makeStartToast(
        jsMap: Map<String, String>,
    ): String {
        val startToastMessage = jsMap.get(JsActionKeyManager.JsSubKey.START_TOAST.key)
        if(
            startToastMessage.isNullOrEmpty()
        ) return String()
        return makeToastMessage(startToastMessage)
    }

    private fun makeEndToast(
        jsMap: Map<String, String>,
    ): String {
        val endToastMessage = jsMap.get(JsActionKeyManager.JsSubKey.END_TOAST.key)
        if(
            endToastMessage.isNullOrEmpty()
        ) return String()
        return makeToastMessage(endToastMessage)
    }

    private fun putAfterFunc(
        jsMap: Map<String, String>,
        jsMapListOnlyAfter: List<Map<String, String>>,
        loopIndex: Int,
    ): String {
        val afterId = jsMap.get(JsActionKeyManager.JsSubKey.ID.key)
        val afterKeyName = JsActionKeyManager.JsSubKey.AFTER.key
        val afterJsMapList = jsMapListOnlyAfter.filter {
            val afterValueList =
                it.get(afterKeyName)
                    ?.split("&")
                    ?.map { it.trim() }
            if(
                afterValueList.isNullOrEmpty()
            ) return@filter false
            afterValueList.contains(afterId)
        }
        val parentBeforeVarName = QuoteTool.trimBothEdgeQuote(
            jsMap.get(varSubKeyName)
        ).ifEmpty {
            JsActionKeyManager.noDefinitionBeforeVarNameByPrev
        }

        var curBeforeVarName = parentBeforeVarName
        val curAfterIt = (0..loopIndex).map {
            afterItPrefix.replaceFirstChar(Char::uppercaseChar)
        }.joinToString(
            String()
        ).replaceFirstChar(Char::lowercaseChar) + afterSrcPrevPronoun
        return afterJsMapList.map {
            afterJsMapSrc ->
            val updatedAfterJsMapToBeforeVarName = updateAfterJsMap(
                afterJsMapSrc,
                curBeforeVarName,
                curAfterIt,
            )
            val updatedAfterJsMap = updatedAfterJsMapToBeforeVarName.first
            curBeforeVarName = updatedAfterJsMapToBeforeVarName.second
            execMake(
                updatedAfterJsMap,
                jsMapListOnlyAfter,
                loopIndex + 1
            ).let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    '\n'
                )
            }.map {
                "\t${it}"
            }.joinToString("\n")
        }.joinToString("\n")
    }


    private fun updateAfterJsMap(
        afterJsMapSrc: Map<String, String>,
        curBeforeVarName: String,
        curAfterIt: String,
    ): Pair<Map<String, String>, String> {
        val varName = QuoteTool.trimBothEdgeQuote(
            afterJsMapSrc.get(varSubKeyName)
        )
        val updateVarName =
            when(varName == curAfterIt) {
                true -> curBeforeVarName
                else -> varName
            }
        val returnBeforeVarName =
            when(
                updateVarName.isEmpty()
            ){
                true -> curBeforeVarName
                else -> updateVarName
            }
        val updateAfterJsMap = afterJsMapSrc.map {
            val key = it.key
            when(key == varSubKeyName) {
                true -> key to updateVarName
                else -> key to it.value
            }
        }.toMap()
        return updateAfterJsMap to returnBeforeVarName
    }

    private fun makeFuncConWithVarOrReturn(
        jsMap: Map<String, String>,
        funcCon: String,
    ): String {
        val onReturn =
            jsMap.get(JsActionKeyManager.JsSubKey.ON_RETURN.key) ==
                    JsActionKeyManager.OnReturnValue.ON.name
        if(onReturn){
            return "return ${funcCon}"
        }
        val varName = jsMap.get(JsActionKeyManager.JsSubKey.VAR.key)
        return when(
            varName.isNullOrEmpty()
        ){
            true -> funcCon
            else -> "var ${varName} = ${funcCon}"
        }
    }

    private fun makeToastMessage(
        message: String,
    ): String {
        return """
            jsToast.short(`${message}`);
        """.trimIndent()
    }

    private fun makeDeleteVarCon(
        jsMap: Map<String, String>
    ): String {
        val deleteVarName = jsMap.get(JsActionKeyManager.JsSubKey.DELETE_VAR.key)
        if(
            deleteVarName.isNullOrEmpty()
        ) return String()
        return "${deleteVarName} = null;"
    }

    private fun makeFuncConWithVarExitJudge(
        jsMap: Map<String, String>,
    ): String {
        val descCon = jsMap.get(JsActionKeyManager.JsSubKey.DESC.key)
            ?: String()
        val exitJsCon = stepOutputFunc(
            jsMap,
            LogPrefix.EXIT.prefix,
            descCon
        )
        val exitJudgeCondition =
            jsMap.get(JsActionKeyManager.JsSubKey.EXIT_JUDGE.key)
        if(
            exitJudgeCondition.isNullOrEmpty()
        ) return String()
        val exitToastMessage =
            jsMap.get(JsActionKeyManager.JsSubKey.EXIT_TOAST.key)
        val toastCon = when(exitToastMessage.isNullOrEmpty()) {
            true -> String()
            else -> makeToastMessage(exitToastMessage)
        }
        return """
        |if(${exitJudgeCondition}){ 
        |   ${toastCon}
        |   ${exitJsCon}
        |   exitZero();
        |}
        |""".trimMargin()
    }

    private fun makeLoopMethodTemplate(
        jsMap: Map<String, String>,
        enableLoopMethodType: JsActionKeyManager.JsFuncManager.EnableLoopMethodType,
    ): String {
        val functionName =
            jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
        val loopArgsNameList = makeVarArgsForLoopMethod(
            jsMap
        ).ifEmpty {
            return JsActionKeyManager.JsFuncManager.makeLoopArgsDefinitionErrMark(
                functionName,
                JsActionKeyManager.JsFuncManager.LoopMethodArgNameIndexToErrMsg.LOOP_ARG_NAMES.errMsg
            )
        }
        val elementValName =
            getLoopArgName(
                loopArgsNameList,
                JsActionKeyManager.JsFuncManager.LoopMethodArgNameIndexToErrMsg.ELEMENT.index,
        ).ifEmpty {
                return JsActionKeyManager.JsFuncManager.makeLoopArgsDefinitionErrMark(
                    functionName,
                    JsActionKeyManager.JsFuncManager.LoopMethodArgNameIndexToErrMsg.ELEMENT.errMsg
                )
            }

        val elementArgName = elementValName + "Src"
        val indexArgName = getLoopArgName(
            loopArgsNameList,
            JsActionKeyManager.JsFuncManager.LoopMethodArgNameIndexToErrMsg.INDEX.index,
        ).ifEmpty {
            return JsActionKeyManager.JsFuncManager.makeLoopArgsDefinitionErrMark(
                functionName,
                JsActionKeyManager.JsFuncManager.LoopMethodArgNameIndexToErrMsg.INDEX.errMsg
            )
        }
        return when(enableLoopMethodType) {
            JsActionKeyManager.JsFuncManager.EnableLoopMethodType.FILTER_METHOD_TYPE -> {
                val method =
                    JsActionKeyManager.MethodManager.makeMethod(jsMap)
                val boolValName = getLoopArgName(
                    loopArgsNameList,
                    JsActionKeyManager.JsFuncManager.LoopMethodArgNameIndexToErrMsg.BOOL.index,
                ).ifEmpty {
                    return JsActionKeyManager.JsFuncManager.makeLoopArgsDefinitionErrMark(
                        functionName,
                        JsActionKeyManager.JsFuncManager.LoopMethodArgNameIndexToErrMsg.BOOL.errMsg
                    )
                }
                listOf(
                    listOf("${functionName}(function(${elementArgName}, ${indexArgName}){"),
                    listOf(
                        "var ${elementValName} = ${elementArgName};",
                        "var ${boolValName} = true;",
                        "%s;",
                        "return ${boolValName};",
                    ).map { "\t${it}" },
                    listOf("})${method};"),
                ).flatten().joinToString("\n")
            }
            JsActionKeyManager.JsFuncManager.EnableLoopMethodType.MAP_METHOD_TYPE -> {
                val method =
                    JsActionKeyManager.MethodManager.makeMethod(jsMap)
                listOf(
                    listOf("${functionName}(function(${elementArgName}, ${indexArgName}){"),
                    listOf(
                        "var ${elementValName} = ${elementArgName};",
                        "%s;",
                        "return ${elementValName};"
                    ).map { "\t${it}" },
                    listOf("})${method};")
                ).flatten().joinToString("\n")
            }
            JsActionKeyManager.JsFuncManager.EnableLoopMethodType.FOR_EACH_METHOD_TYPE -> {
                listOf(
                    listOf("${functionName}(function(${elementArgName}, ${indexArgName}){"),
                    listOf(
                        "var ${elementValName} = ${elementArgName};",
                        "%s;"
                    ).map { "\t${it}" },
                    listOf("});")
                ).flatten().joinToString("\n")
            }
        }
    }

    private fun getLoopArgName(
        loopArgsNameList: List<String>,
        index: Int,
//        defaultArgName: String
    ): String {
        return loopArgsNameList.getOrNull(index)
            ?: String()
//            .let {
//            if(
//                it.isNullOrEmpty()
//            ) return@let defaultArgName
//            it
//        }
    }

    private fun makeFuncCon(
        jsMap: Map<String, String>,
        varargsStr: String,
    ): String {
        val functionName =
            jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
        val method =
            JsActionKeyManager.MethodManager.makeMethod(jsMap)
        val isOnlyVar = howVarOnly(jsMap)
        val funcConSrc = when(true){
            isOnlyVar ->
                JsActionKeyManager.JsVarManager.makeVarValue(jsMap)
            else -> makeFuncSignature(
                jsMap,
                functionName,
                varargsStr,
            )
        }
        return "${funcConSrc}${method};"
    }

    private fun howVarOnly(
        jsMap: Map<String, String>,
    ): Boolean {
        val isVarOnlyByVarValue =
            jsMap.containsKey(JsActionKeyManager.JsSubKey.VAR.key)
                    && jsMap.containsKey(JsActionKeyManager.JsSubKey.VAR_VALUE.key)
        return isVarOnlyByVarValue
    }

    private fun makeFuncSignature(
        jsMap: Map<String, String>,
        functionName: String?,
        varargsStr: String,
    ): String {
        if(
            functionName.isNullOrEmpty()
        ) return String()
        return "${functionName}(${varargsStr})"
    }

    private fun makeVarArgsForLoopMethod(
        jsMap: Map<String, String>
    ): List<String> {
        val argsSeparator = '&'
        val argList = CmdClickMap.createMap(
            jsMap.get(JsActionKeyManager.JsSubKey.LOOP_ARG_NAMES.key),
            argsSeparator
        ).map{ it.second }.filter { it.isNotEmpty() }
        return argList
    }

    private fun makeFuncTemplate(
        jsMap: Map<String, String>
    ): String {
        val ifCondition = jsMap.get(JsActionKeyManager.JsSubKey.IF.key)
        val descCon = jsMap.get(JsActionKeyManager.JsSubKey.DESC.key)
            ?: String()
        val funcName = jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
        val stepStartFunc = stepOutputFunc(
            jsMap,
            LogPrefix.START.prefix,
            descCon
        )
        val stepOkFunc = stepOutputFunc(
            jsMap,
            LogPrefix.OK.prefix,
            descCon
        )
        val funcTemplateSrc = JsActionKeyManager.makeFuncTemplateForIf(ifCondition)
        val funcNameAndDescLine = " ${funcName}: ${descCon}"
        val funcTemplate = listOf(
            "// _/_/_/ start ${funcNameAndDescLine}",
            funcTemplateSrc,
            "// _/_/_/ end ${funcNameAndDescLine}",
        ).joinToString("\n")
        return listOf(
            stepStartFunc,
            funcTemplate,
            stepOkFunc
        ).joinToString("\n")
    }

    private fun stepOutputFunc(
        jsMap: Map<String, String>,
        prefix: String,
        desc: String = String()
    ): String {
        val funcName =
            jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
                ?: return String()
        val isJsCon =
            JsActionKeyManager.JsFuncManager.isJsCon(
                funcName
            )
        if(isJsCon) return String()
        val isExitPrefix =
            prefix == LogPrefix.EXIT.prefix
        val disableLog =
            jsMap.get(JsActionKeyManager.JsSubKey.ON_LOG.key) ==
                    JsActionKeyManager.OnLogValue.OFF.name
                    && !isExitPrefix
        if(disableLog) return String()
        val symbolRegex =  Regex("[\\[\\]}{)(`\\\\\"]")
        val displayCon = listOf(
            "${prefix} ${funcName}",
            desc
        ).joinToString("\n")
            .replace(symbolRegex, "")
            .split("\n")
            .filter { it.isNotEmpty() }
            .joinToString("\\n# ")
        return "jsFileSystem.stdLog(`${displayCon}`);"
    }
}

//private fun makeJsCon(
//    funcName: String?,
//): String {
//    if(
//        funcName.isNullOrEmpty()
//    ) return String()
//    val jsConPrefix =
//        JsActionKeyManager.JsFuncManager.LoopMethodFlag.JS_CON_PREFIX.flag
//    val jsConSrc =
//        funcName.trim().removePrefix(jsConPrefix)
//    return QuoteTool.trimBothEdgeQuote(jsConSrc)
//}


private fun makeReplaceVariableTsv(
    setReplaceVariableMap: Map<String, String>?,
    execJsPath: String,
){
    if(
        setReplaceVariableMap.isNullOrEmpty()
        || execJsPath.isEmpty()
    ) return
    CoroutineScope(Dispatchers.IO).launch {
        val mainCurrentAppDirPath = CcPathTool.getMainAppDirPath(
            execJsPath
        )
        val mainFannelName = File(
            CcPathTool.getMainFannelFilePath(
                execJsPath
            )
        ).name
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "ubuntu.txt").absolutePath,
//            listOf(
//                "execJsPath: ${execJsPath}",
//                "mainCurrentAppDirPath: ${mainCurrentAppDirPath}",
//                "mainFannelName: ${mainFannelName}",
//                "setReplaceVariableMap: ${setReplaceVariableMap}"
//            ).joinToString("\n\n\n")
//        )
        JavaScriptLoadUrl.makeReplaceVariableTableTsv(
            setReplaceVariableMap,
            mainCurrentAppDirPath,
            mainFannelName,
        )
    }
}

//private fun trimBothEdgeQuoteForJsAc(
//    targetStr: String
//): String {
//    val trimCon = QuoteTool.trimBothEdgeQuote(targetStr)
//    val subKeyRegex = "[`\"']([|?&]as)['`\"]"
//    return trimCon.replace(
//        subKeyRegex,
//        "$1"
//    )
//}

private fun howMacroFunc(
    firstFuncName: String?,
): Boolean {
    if(
        firstFuncName.isNullOrEmpty()
    ) return false
    return macroValueList.any {
        firstFuncName == it
    }
}

private val macroValueList =
    MacroForToolbarButton.Macro.values()
        .map {
            it.name
} + JsPathMacroForListIndex.values()
        .map {
            it.name
} + JsMacroForQr.values()
        .map {
            it.name
        }
