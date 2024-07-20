package com.puutaro.commandclick.proccess.js_macro_libs.common_libs


import TsvImportManager
import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
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
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object JsActionTool {

    private val jsActionShiban =
        CommandClickScriptVariable.jsActionShiban
    private val jsKeyName = JsActionKeyManager.JsActionsKey.JS.key
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

    fun makeJsActionMap(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        keyToSubKeyCon: String?,
        setReplaceVariableMapSrc: Map<String, String>?,
        mainOrSubFannelPath: String,
    ): Map<String, String>? {
        val setReplaceVariableMap = makeSetRepValMap(
            fragment,
            fannelInfoMap,
            setReplaceVariableMapSrc
        )
        makeReplaceVariableTsv(
            setReplaceVariableMap,
            mainOrSubFannelPath,
        )
        val keyToSubKeyConWithReflectRepValDefalt = CmdClickMap.replaceHolderForJsAction(
            keyToSubKeyCon ?: String(),
            null
        )
        val keyToSubMapTypeMapToKeyToSubKeyConListByValidKey = createKeyToSubMapTypeMap(
            fannelInfoMap,
            keyToSubKeyConWithReflectRepValDefalt,
            setReplaceVariableMap,
        ) ?: return null
        val keyToSubMapTypeMap =
            keyToSubMapTypeMapToKeyToSubKeyConListByValidKey.first
        val actionImportedKeyToSubKeyConList =
            keyToSubMapTypeMapToKeyToSubKeyConListByValidKey.second
        val keyToSubKeyMapListWithAfterSubKey =
            keyToSubMapTypeMap.get(
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
//                "keyToSubKeyConWithReflectRepValDefalt: ${keyToSubKeyConWithReflectRepValDefalt}",
//                "setReplaceVariableMap: ${setReplaceVariableMap}",
//                "keyToSubMapTypeMap: ${keyToSubMapTypeMap}",
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
//                    )
//                }",
//            ).joinToString("\n\n")
//        )
        val displayActionImportedAcCon = CheckTool.DisplayActionImportedJsAcSrc.make(
            actionImportedKeyToSubKeyConList,
        )
        if(
            !macroDataMap.isNullOrEmpty()
        ) {
            CheckTool.FirstJsActionLogSaver.save(
                keyToSubKeyConWithReflectRepValDefalt,
                displayActionImportedAcCon,
                null,
                null,
                macroDataMap to null,
            )
            return macroDataMap
        }
        val jsActionMapToJsConOnlyReplace = extractJsDataMap(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        )
        val jsActionMap = jsActionMapToJsConOnlyReplace?.first
        CheckTool.FirstJsActionLogSaver.save(
            keyToSubKeyConWithReflectRepValDefalt,
            displayActionImportedAcCon,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
            jsActionMapToJsConOnlyReplace
        )
        val context = fragment.context
        jsAcChecker(
            context,
            jsActionMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
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
        actionImportedKeyToSubKeyConList: List<Pair<String, String>>,
        displayActionImportedAcCon: String,
    ): Boolean {
        val checkJsCon =
            jsActionMap?.get(JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key)
        val evaluateGeneCon =
            CheckTool.KeyToSubKeyConTool.makeEvaluateAcCon(
                keyToSubKeyMapListWithoutAfterSubKey,
                keyToSubKeyMapListWithAfterSubKey,
            )
        val actionImportedCon = actionImportedKeyToSubKeyConList.map {
            val mainKey = it.first
            val subKeyCon = it.second
            "|${mainKey}=${subKeyCon}"
        }.joinToString("\n")
        CheckTool.FuncMainKeyTwoOverErr.check(
            context,
            actionImportedKeyToSubKeyConList,
            actionImportedCon
        ).let {
                isFuncMainKeyTwoOverErr ->
            if(
                isFuncMainKeyTwoOverErr
            ) return true
        }
        CheckTool.ForbiddenJsKeyDirectSpecifyErr.check(
            context,
            evaluateGeneCon
        ).let {
                isForbiddenJsKeyDirectSpecifyErr ->
            if(
                isForbiddenJsKeyDirectSpecifyErr
            ) return true
        }
        CheckTool.QuoteNumCheck.check(
            context,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        ).let {
                isQuoteErr ->
            if(
                isQuoteErr
            ) return true
        }
        CheckTool.IdDuplicateErr.check(
            context,
            evaluateGeneCon
        ).let {
                isIdDuplicateErr ->
            if(
                isIdDuplicateErr
            ) return true
        }
        CheckTool.NotStartVerticalVarMainKey.check(
            context,
            evaluateGeneCon
        ).let {
                isNotStartVerticalVarMainKey ->
            if(
                isNotStartVerticalVarMainKey
            ) return true
        }
        CheckTool.IrregularStrKeyCon.check(
            context,
            evaluateGeneCon
        ).let {
                isIrregularStrKeyCon ->
            if(
                isIrregularStrKeyCon
            ) return true
        }
        CheckTool.IrregularFuncValue.check(
            context,
            evaluateGeneCon,
        ).let {
                isIrregularFuncValue ->
            if(
                isIrregularFuncValue
            ) return true
        }
        CheckTool.VarNotInit.check(
            context,
            evaluateGeneCon
        ).let {
                isVarNotInit ->
            if(
                isVarNotInit
            ) return true
        }
        CheckTool.RunVarPrefixUsedAsArgErr.check(
            context,
            actionImportedKeyToSubKeyConList,
        ).let {
                isRunVarPrefixUsedErr ->
            if(
                isRunVarPrefixUsedErr
            ) return true
        }
        CheckTool.MissImportPathErr.check(
            context,
            actionImportedCon,
        ).let {
                isMissImportPathErr ->
            if(
                isMissImportPathErr
            ) return true
        }
        CheckTool.MissLastVarKeyErrForAcVar.check(
            context,
            actionImportedCon
        ).let {
                isMissLastVarKeyErrForAcVar ->
            if(
                isMissLastVarKeyErrForAcVar
            ) return true
        }
        CheckTool.MissLastReturnKeyErrForAcImport.check(
            context,
            actionImportedCon,
        ).let {
                isMissLastReturnKeyErrForAcImport ->
            if(
                isMissLastReturnKeyErrForAcImport
            ) return true
        }
        CheckTool.InvalidAfterIdInAcImportErr.check(
            context,
            actionImportedCon
        ).let {
                isInvalidAfterInAcImportErr ->
            if(
                isInvalidAfterInAcImportErr
            ) return true
        }
        CheckTool.PathNotFound.check(
            context,
            evaluateGeneCon,
            actionImportedKeyToSubKeyConList,
        ).let {
                isPathNotFound ->
            if(
                isPathNotFound
            ) {
                return true
            }
        }
        CheckTool.AcImportPathNotRegisterInRepValErr.check(
            context,
            evaluateGeneCon,
            actionImportedKeyToSubKeyConList,
        ).let {
                isPathNotRegisterInRepValErr ->
            if(
                isPathNotRegisterInRepValErr
            ) {
                return true
            }
        }
        CheckTool.IrregularFuncValue.check(
            context,
            evaluateGeneCon,
        ).let {
                isIrregularFuncValue ->
            if(
                isIrregularFuncValue
            ) return true
        }
        CheckTool.LoopMethodOrArgsNotExist.check(
            context,
            checkJsCon
        ).let {
                isLoopMethodOrArgsNotExist ->
            if(
                isLoopMethodOrArgsNotExist
            ) return true
        }
        CheckTool.SyntaxCheck.checkJsAcSyntax(
            context,
            checkJsCon
        ).let {
                isSyntaxErr ->
            if(
                isSyntaxErr
            ) return true
        }
        CheckTool.VarNotUse.checkJsAsSyntaxForVarNotUse(
            context,
            checkJsCon
        ).let {
                isVarNotUseErr ->
            if(
                isVarNotUseErr
            ) return true
        }
        return false
    }

    private fun extractJsDataMap(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
    ): Pair<Map<String, String>, String?>? {
        val jsConToJsConOnlyReplace = convertJsCon(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
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
        fannelInfoMap: Map<String, String>,
        keyToSubKeyCon: String?,
        setReplaceVariableMap: Map<String, String>?,
    ): Pair<
            Map<KeyToSubConType, List<Pair<String, Map<String, String>>>?>,
            List<Pair<String, String>>
            >? {
        val keyToSubKeyMapListToKeyToSubKeyConListByValidKey = KeyToSubKeyMapListMaker.make(
            keyToSubKeyCon,
            fannelInfoMap,
            setReplaceVariableMap,
        )
        val keyToSubKeyMapList =
            keyToSubKeyMapListToKeyToSubKeyConListByValidKey.first
        val actionImportedKeyToSubKeyConList =
            keyToSubKeyMapListToKeyToSubKeyConListByValidKey.second
        if (
            keyToSubKeyMapList.isEmpty()
        ) return null
        val afterKeyToSubKeyMapList =
            filterByAfterJsSubKey(
                keyToSubKeyMapList,
            )

        val keyToSubKeyMapListWithoutAfterSubKey = keyToSubKeyMapList.filter {
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
            KeyToSubConType.WITH_AFTER
                    to null,
            KeyToSubConType.WITH_OTHER
                    to listOf(keyToSubKeyMapListForMacro),
        ) to actionImportedKeyToSubKeyConList
        return mapOf(
            KeyToSubConType.WITH_AFTER
                    to afterKeyToSubKeyMapList,
            KeyToSubConType.WITH_OTHER
                    to makeKeyToSubKeyConListUntilFirstUnPipAble(
                keyToSubKeyMapListWithoutAfterSubKey
            ),
        ) to actionImportedKeyToSubKeyConList
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
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
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
            fannelInfoMap,
            jsConBeforeJsImport,
            setReplaceVariableMap,
        )
        val jsConOnlyReplace = SetReplaceVariabler.execReplaceByReplaceVariables(
            jsConBeforeJsImport,
            setReplaceVariableMap,
            FannelInfoTool.getCurrentAppDirPath(fannelInfoMap),
            FannelInfoTool.getCurrentFannelName(fannelInfoMap),
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
        WITH_AFTER,
        WITH_OTHER,
    }
}


private object KeyToSubKeyMapListMaker {

    private const val keySeparator = '|'
    val jsActionsKeyPlusList =
        JsActionKeyManager.JsActionsKey.values().map {
            it.key
        }

    fun make(
        keyToSubKeyCon: String?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): Pair<List<Pair<String, Map<String, String>>>, List<Pair<String, String>>> {
        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val keyToSubKeyConListSrc = makeKeyToSubConPairListByValidKey(
            keyToSubKeyCon
        )
        val importRoopLimit = 5
//        var containImport = false
        val jsActionVarKeyName = JsActionKeyManager.JsActionsKey.ACTION_VAR.key
        val jsActionImportSignal = "${jsActionVarKeyName}="
        var keyToSubKeyConList = keyToSubKeyConListSrc
        ActionImportPutter.initBeforeActionImportMap(
            setReplaceVariableMap
        )
        for( i in 1..importRoopLimit) {
            var errType: ActionImportPutter.ErrSignal =
                ActionImportPutter.ErrSignal.NO_ERR
            keyToSubKeyConList = keyToSubKeyConList.map {
                    keyToSubKeyPair ->
                val mainKeyName = keyToSubKeyPair.first
                if(
                    mainKeyName != jsActionVarKeyName
                ) return@map listOf(keyToSubKeyPair)
                val putKeyToSubKeyConToErrType =
                    ActionImportPutter.put(
                        currentAppDirPath,
                        currentFannelName,
                        setReplaceVariableMap,
                        keyToSubKeyPair
                    )
                val putKeyToSubKeyCon = putKeyToSubKeyConToErrType.first
                errType = putKeyToSubKeyConToErrType.second
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsAc_import.txt").absolutePath,
//                    listOf(
//                        "putKeyToSubKeyCon: ${putKeyToSubKeyCon}",
//                        "\n\nupdatePutKeyToSubKeyCon ${makeKeyToSubConPairListByValidKey(
//                            putKeyToSubKeyCon
//                        )}",
//                        "errType: ${errType.name}",
////                        "containImport: ${containImport}",
//                        "roopNum: ${i}",
//                    ).joinToString("\n\n") + "\n--------\n"
//                )
                makeKeyToSubConPairListByValidKey(
                    putKeyToSubKeyCon
                )
            }.flatten()
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_import_flattern_after.txt").absolutePath,
//                listOf(
//                    "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    "errType: ${errType.name}",
//                    "roopNum: ${i}",
////                    "containImport: ${containImport}"
//                ).joinToString("\n\n") + "\n--------\n"
//            )
            if(errType == ActionImportPutter.ErrSignal.ERR) break
            val containImport = keyToSubKeyConList.find{
                val key = it.first
                key == jsActionVarKeyName
            } != null
            if(!containImport) break
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_import_flattern__break_after.txt").absolutePath,
//                listOf(
//                    "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    "errType: ${errType.name}",
//                    "roopNum: ${i}",
//                    "containImport: ${containImport}",
//                ).joinToString("\n\n") + "\n--------\n"
//            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_impot_total.txt").absolutePath,
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

    private fun makeKeyToSubConPairListByValidKey(
        keyToSubKeyCon: String?,
    ): List<Pair<String, String>> {
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


private object ImportMapMaker {
    fun comp(
        subKeyCon: String,
        firstSubKeyWithEqualPrefix: String,
    ): Map<String, String> {
        val subKeySeparator = '?'
        val subKeyConList = subKeyCon.split(subKeySeparator)
        val importPathKeyCon = subKeyConList.firstOrNull()
            ?: String()
        val endsQuote = extractEndsQuote(
            importPathKeyCon.removePrefix(firstSubKeyWithEqualPrefix),
        )
        if (
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
            firstSubKeyWithEqualPrefix,
            endsQuote,
            importPathKeyCon.removePrefix(firstSubKeyWithEqualPrefix),
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
}
private object ActionImportPutter {

    private val jsMainKey = JsActionKeyManager.JsActionsKey.JS.key
    private const val jsActionEndComma = ','
    private val mainKeySeparator = '|'
    private val actionVarKey =
        JsActionKeyManager.JsActionsKey.ACTION_VAR.key
    private val actionImportVirtualSubKey =
        JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
    private val afterKey =
        JsActionKeyManager.ActionImportManager.ActionImportKey.AFTER.key
    private val funcKey =
        JsActionKeyManager.JsSubKey.FUNC.key
    private val varKey = JsActionKeyManager.JsSubKey.VAR.key
    private val delayKey = JsActionKeyManager.ActionImportManager.ActionImportKey.DELAY.key
    private val beforeActionImportMap = mutableMapOf<String, String>()
    private var replaceVariableMapCon = String()
    private val tsvVarsKey = JsActionKeyManager.JsActionsKey.TSV_VARS.key
    private val jsImportKey = JsActionKeyManager.JsActionsKey.JS_IMPORT.key
    private val idSubKey = JsActionKeyManager.JsSubKey.ID.key


    enum class ErrSignal {
        NO_ERR,
        ERR,
    }

    fun initBeforeActionImportMap(
        setReplaceVariableMap: Map<String, String>?
    ){
        beforeActionImportMap.clear()
        replaceVariableMapCon =
            makeSetRepValeMapCon(setReplaceVariableMap)
    }

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
    fun put(
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyPair: Pair<String, String>,
    ): Pair<String, ErrSignal> {
        val subKeyCon = listOf(
            actionVarKey,
            keyToSubKeyPair.second
        ).joinToString("=")
        val actionImportMap = ImportMapMaker.comp(
            subKeyCon,
            "${actionVarKey}="
        )
        val importPathSrc = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                JsActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
            )
        )
        if(
            importPathSrc.isEmpty()
        ){
            val missImportKey =
                "${JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_IMPORT_PATH.key}="
            val actionVarSec = subKeyCon.replace(
                Regex("(${actionVarKey}=[^?|]+)"),
                "|$1?${missImportKey}"
            )
            return actionVarSec to ErrSignal.ERR
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport.txt").absolutePath,
//            listOf(
//                "keyToSubKeyPair.second: ${keyToSubKeyPair.second}",
//                "subKeyCon: ${subKeyCon}",
//                "actionImportMap: ${actionImportMap}",
//                "importPathSrc: ${importPathSrc}",
//            ).joinToString("\n\n") + "\n-----\n"
//        )
        val importPath =
            JsActionKeyManager.PathExistChecker.makeCodeOrPath(importPathSrc)
        val isNotFoundPrefix =
            importPath.startsWith(JsActionKeyManager.PathExistChecker.notFoundCodePrefix)
        val pathNotRegisterInRepValErrSignal =
            JsActionKeyManager.PathNotRegisterInRepValChecker.echoErrSignal(
                importPath,
                beforeActionImportMap,
                replaceVariableMapCon
            )
        val importConSrcToErrType = when(true){
            isNotFoundPrefix ->
                "${jsMainKey}=?${actionImportVirtualSubKey}=${importPath}" to ErrSignal.NO_ERR
            !pathNotRegisterInRepValErrSignal.isNullOrEmpty() ->
                "${jsMainKey}=?${actionImportVirtualSubKey}=${pathNotRegisterInRepValErrSignal}" to ErrSignal.NO_ERR
            else -> {
                val importConListToErrType = makeImportConSrcToErrType(
                    actionImportMap,
                    importPath,
                    currentAppDirPath,
                    currentFannelName,
                    setReplaceVariableMap,
                )
                val importCon = importConListToErrType.first.joinToString("\n")
                val errType = importConListToErrType.second
                importCon to errType
            }
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcImport_put.txt").absolutePath,
//            listOf(
//                "importPath: ${importPath}",
//                "isNotFoundPrefix: ${isNotFoundPrefix}",
//                "pathNotRegisterInRepValErrSignal: ${pathNotRegisterInRepValErrSignal}",
//                "!pathNotRegisterInRepValErrSignal.isNullOrEmpty(): ${!pathNotRegisterInRepValErrSignal.isNullOrEmpty()}",
//                "importConSrcToErrType ${importConSrcToErrType}",
//            ).joinToString("\n") + "\n-----------------\n"
//        )
        val importConSrc = importConSrcToErrType.first
        val errType = importConSrcToErrType.second
        if(errType == ErrSignal.ERR){
            return importConSrc to ErrSignal.ERR
        }
        return importConSrc.let {
            ListSettingVariableListMaker.execRemoveMultipleNewLinesAndReplace(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            ) to errType
        }
    }

    private fun makeImportConSrcToErrType(
        actionImportMap: Map<String, String>,
        importPath: String,
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): Pair<List<String>, ErrSignal> {
        val importSrcConBeforeReplace = makeActionImportSrcCon(
            importPath,
            currentAppDirPath,
            currentFannelName,
            setReplaceVariableMap,
        )
//            .replace(
//            afterKeyConRegex,
//            "?${invalidAfterInAcImport}=$1"
//        )
        val repMap = makeRepValHolderMap(
            actionImportMap.get(
                JsActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
            )
        )
        val idSubKeyConRegex = Regex("\\?${idSubKey}=[^?|\n]+")
        val idSubKeyPrefix = "?${idSubKey}="
        val importSrcCon = CmdClickMap.replaceHolderForJsAction(
            importSrcConBeforeReplace,
            repMap
        )
        val idList = idSubKeyConRegex.findAll(
            importSrcCon
        ).map {
            QuoteTool.trimBothEdgeQuote(
                it.value.removePrefix(idSubKeyPrefix)
            )
        }.filter { it.isNotEmpty() }.toList()
        val importConWithFormatList = makeActionImportFormatList(
            importSrcCon,
        )
        val actionVarValue = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                actionVarKey
            )
        )
        val whenCondition = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(
                JsActionKeyManager.ActionImportManager.ActionImportKey.WHEN.key
            )
        )
        val afterId = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(afterKey)
        )
        val delayTime = QuoteTool.trimBothEdgeQuote(
            actionImportMap.get(delayKey)
        ).let {
            if(
                it.isEmpty()
            ) return@let null
            try {it.toInt()} catch(e: Exception){
                null
            }
        }
        return putIfVarFuncBracketToErrType(
            importConWithFormatList,
            whenCondition,
            actionVarValue,
            afterId,
            idList,
            delayTime,
        )
    }

    private fun putIfVarFuncBracketToErrType(
        importConWithFormatList: List<String>,
        whenCondition: String?,
        actionVarValue: String?,
        afterId: String?,
        idList: List<String>,
        delayTime: Int?,
    ): Pair<List<String>, ErrSignal> {
        val importConWithFormatListByAcVarToAcVarErr =
            ImportConWithFormatListForAcVar.validate(
                importConWithFormatList,
                actionVarValue,
            )
        val errSignalByAcVar =
            importConWithFormatListByAcVarToAcVarErr.second
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_errSignalByUseVar.txt").absolutePath,
//            listOf(
//                "errSignalByUseVar: ${errSignalByUseVar.name}",
//                "importConWithFormatListByUseVar: ${importConWithFormatListByUseVarToUseVarErr.first}",
//            ).joinToString("\n\n")
//        )
        if(
            errSignalByAcVar == ErrSignal.ERR)
        {
            val importConWithFormatListByAcVar =
                importConWithFormatListByAcVarToAcVarErr.first
            val updatedImportConByWhenCondition = updateImportConByWhenCondition(
                importConWithFormatListByAcVar,
                whenCondition,
                actionVarValue,
                afterId,
                delayTime,
            )
            return updatedImportConByWhenCondition to ErrSignal.ERR
        }
        val importConWithFormatListByAcVar =
            importConWithFormatListByAcVarToAcVarErr.first
        val updatedImportConWithFormatListByAfterToErrType =
            ImportConWithFormatListForUseAfter.update(
                importConWithFormatListByAcVar,
                idList,
                afterId,
            )
        val updatedImportConWithFormatListByAfter =
            updatedImportConWithFormatListByAfterToErrType.first
        val errType =
            updatedImportConWithFormatListByAfterToErrType.second
        return updateImportConByWhenCondition(
            updatedImportConWithFormatListByAfter,
            whenCondition,
            actionVarValue,
            afterId,
            delayTime,
        ) to errType
    }

    private object ImportConWithFormatListForAcVar {

        private const val escapeRunPrefix = JsActionKeyManager.JsVarManager.escapeRunPrefix

        fun validate(
            importConWithFormatList: List<String>,
            actionVarValue: String?,
        ): Pair<List<String>, ErrSignal> {
            if (
                actionVarValue.isNullOrEmpty()
            ) return importConWithFormatList to ErrSignal.NO_ERR
            if(
                actionVarValue.startsWith(escapeRunPrefix)
            ) return importConWithFormatList to ErrSignal.NO_ERR
            val validatedImportConWithFormatListToErrSignal =
                validateImportConWithFormatListToErrSignal(
                    importConWithFormatList,
                    actionVarValue,
                )
            val validatedImportConWithFormatList =
                validatedImportConWithFormatListToErrSignal.first
            val errSignal = validatedImportConWithFormatListToErrSignal.second
            return validatedImportConWithFormatList to errSignal
        }

        private fun validateImportConWithFormatListToErrSignal(
            importConWithFormatList: List<String>,
            varNameSrc: String?,
        ): Pair<List<String>, ErrSignal> {
            if(
                varNameSrc.isNullOrEmpty()
            ) return importConWithFormatList to ErrSignal.NO_ERR
            var errSignal: ErrSignal = ErrSignal.NO_ERR
            val findVarReturnSubKeyRegex = Regex(
                "\\?${JsActionKeyManager.OnlyVarSubKey.VAR_RETURN.key}=[^?|\n]*"
            )
            var isChange = false
            val updatedImportConWithFormatList = importConWithFormatList.reversed().map {
                    keyCon ->
                if(
                    isChange
                ) return@map keyCon
                val trimKeyCon = keyCon.trim().trim('|')
                val judgeKeyCon = "|${trimKeyCon}"
                val hasTsvVars = judgeKeyCon.startsWith("|${tsvVarsKey}=")
                val hasJsImport = judgeKeyCon.startsWith("|${jsImportKey}=")
                val hasAcImportAndAfterKey =
                    judgeKeyCon.startsWith("|${actionVarKey}=")
                val hasVarAndAfterKey =
                    judgeKeyCon.startsWith("|${varKey}=")
                val hasAcFuncAndAfterKey =
                    judgeKeyCon.startsWith("|${funcKey}=")
                val isWithAfterKey = hasAcImportAndAfterKey
                        || hasVarAndAfterKey
                        || hasAcFuncAndAfterKey
                val hasAfterKey = isWithAfterKey
                        && judgeKeyCon.contains("?${afterKey}=")
                if (
                    judgeKeyCon.isEmpty()
                    || hasTsvVars
                    || hasJsImport
                    || hasAfterKey
                ) return@map keyCon
    //                FileSystems.updateFile(
    //                    File(UsePath.cmdclickDefaultAppDirPath, "jsAc_updateWhenNotMatchSrcVarToUseVar.txt").absolutePath,
    //                    listOf(
    //                        "hasNotUseAfter: ${hasUseAfter}",
    //                        "hasNotAfter: ${hasAfter}",
    //                        "hasNotAfterAndUseAfter: ${hasAfterOrUseAfter}",
    //                        "isChange: ${isChange}",
    //                        "isUseAfterValue: ${isUseAfterValue}",
    //                        "keyCon: ${keyCon}",
    //                        "importConWithFormatList: ${importConWithFormatList}",
    //                    ).joinToString("\n\n")
    //                )
                isChange = true
                val isNotVarKey =
                    !judgeKeyCon.startsWith("|${varKey}=")
                if(
                    isNotVarKey
                ) {
                    errSignal = ErrSignal.ERR
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "jsAc_isNotVarKey.txt").absolutePath,
//                        listOf(
//                            "keyCon: |${keyCon}",
//                            "with: ${!"|${keyCon}".startsWith("|${varKey}=")}"
//                        ).joinToString("\n")
//                    )
                    return@map listOf(
                        keyCon,
                        "?${JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_LAST_VAR_KEY.key}="
                    ).joinToString(String())
                }
                val varReturnKeyValue =
                    findVarReturnSubKeyRegex.find(keyCon)?.value
                if(
                    varReturnKeyValue.isNullOrEmpty()
                ) {
                    errSignal = ErrSignal.ERR
                    return@map listOf(
                        keyCon,
                        "?${JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_LAST_RETURN_KEY.key}="
                    ).joinToString(String())
                }
                keyCon
            }.reversed()
            return updatedImportConWithFormatList to errSignal
        }
    }

    private object ImportConWithFormatListForUseAfter {

        fun update(
            importConWithFormatListByAcVar: List<String>,
            idList: List<String>,
            afterId: String?,
        ): Pair<List<String>, ErrSignal> {
            if (
                afterId.isNullOrEmpty()
            ) return importConWithFormatListByAcVar to ErrSignal.NO_ERR
            val afterKeyConRegex = Regex("\\?(${afterKey}=[^?|\n]+)")
            val afterKeyConPrefix = "?${afterKey}="
            val invalidAfterInAcImport =
                JsActionKeyManager.ActionImportManager.ActionImportKey.INVALID_AFTER_IN_AC_IMPORT.key
            var errSignal = ErrSignal.NO_ERR
            val updateImportConListByAfter = importConWithFormatListByAcVar.map {
                    keyCon ->
                val trimKeyCon = keyCon.trim()
                val hasTsvVars = trimKeyCon.startsWith("|${tsvVarsKey}=")
                val hasJsImport = trimKeyCon.startsWith("|${jsImportKey}=")
                if (
                    trimKeyCon.isEmpty()
                    || hasTsvVars
                    || hasJsImport
                ) return@map trimKeyCon
                val curAfterId = afterKeyConRegex.find(
                    trimKeyCon
                )?.value?.removePrefix(afterKeyConPrefix)
                val hasNotAfterId = curAfterId.isNullOrEmpty()
                if(
                    hasNotAfterId
                ) return@map listOf(
                    trimKeyCon,
                    "?${afterKey}=`${afterId}`"
                ).joinToString(String())
                val isUseAfterId =
                    idList.contains(curAfterId)
                if(
                    isUseAfterId
                ) return@map trimKeyCon
                errSignal = ErrSignal.ERR
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsAc_acAfter.txt").absolutePath,
//                    listOf(
//                        "trimKeyCon: ${trimKeyCon}",
//                        "curAftrKeyValueCon: ${afterKeyConRegex.find(
//                            trimKeyCon
//                        )?.value}",
//                        "curAfterId: ${curAfterId}"
//                    ).joinToString("\n\n")
//                )
                listOf(
                    trimKeyCon,
                    "?${invalidAfterInAcImport}=${curAfterId}"
                ).joinToString(String())
            }
            return updateImportConListByAfter to errSignal
        }
    }

    private fun updateImportConByWhenCondition(
        updatedImportConWithFormatList: List<String>,
        whenCondition: String?,
        funcVarConSrc: String?,
        afterIdSrc: String?,
        delayTime: Int?,
    ): List<String> {
        val ifBracketSatrtKeyValueCon = whenCondition.let{
            if(
                it.isNullOrEmpty()
            ) return@let String()
            listOf(
                "?${JsActionKeyManager.JsSubKey.IF_BRACKET_START.key}=",
                "?${JsActionKeyManager.JsSubKey.IF.key}=`${it}`"
            ).joinToString(String())
        }
        val ifBracketEndKeyValueCon = whenCondition.let {
            if(
                it.isNullOrEmpty()
            ) return@let String()
            "?${JsActionKeyManager.JsSubKey.IF_BRACKET_END.key}="
        }
        val setTimeoutBracketStartKeyValueCon = delayTime.let {
            if(
                it == null
                || it == 0
            ) return@let String()
            "?${JsActionKeyManager.JsSubKey.SET_TIMEOUT_START_BRACKET.key}="
        }
        val setTimeoutBracketEndKeyValueCon = delayTime.let {
            if(
                it == null
                || it == 0
            ) return@let String()
            "?${JsActionKeyManager.JsSubKey.SET_TIMEOUT_END_BRACKET.key}=${it}"
        }
        val funcVarKeyValueCon = funcVarConSrc.let {
            if(
                it.isNullOrEmpty()
            ) return@let String()
            "?${JsActionKeyManager.JsSubKey.FUNC_VAR.key}=${it}"
        }
        val afterKeyValueCon =
            afterIdSrc.let {
                if(
                    it.isNullOrEmpty()
                ) return@let String()
                "?${afterKey}=`${it}`"
            }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath,
//                "jsAc_updateImportConByWhenCondition.txt").absolutePath,
//            listOf(
//                "whenCondition: ${whenCondition}",
//                "useAfterKeyValue: ${useAfterKeyValue}",
//            ).joinToString("\n\n")
//        )
        return listOf(
            listOf(
                "|${JsActionKeyManager.JsActionsKey.JS.key}=",
                "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
                ifBracketSatrtKeyValueCon,
                setTimeoutBracketStartKeyValueCon,
                funcVarKeyValueCon,
                "?${JsActionKeyManager.JsSubKey.FUNC_BRACKET_START.key}=",
                afterKeyValueCon,
            ).joinToString(String())
        ) + updatedImportConWithFormatList +
                listOf(
                    listOf(
                        "|${JsActionKeyManager.JsActionsKey.JS.key}=",
                        "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
                        setTimeoutBracketEndKeyValueCon,
                        ifBracketEndKeyValueCon,
                        "?${JsActionKeyManager.JsSubKey.FUNC_BRACKET_END.key}=",
                        afterKeyValueCon,
                    ).joinToString(String())
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
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport_first_makeActionImportSrcCon.txt").absolutePath,
//            listOf(
//                "importPath: ${importPath}",
//                "beforeActionImportSrcCon: ${beforeActionImportSrcCon}",
//            ).joinToString("\n\n") + "\n-----\n"
//        )
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
            ).joinToString(String()).let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    mainKeySeparator
                )
            }.filter{ it.trim().isNotEmpty() }.mapIndexed  {
                    index, line ->
                val trimLine = line.trim()
                if(
                    trimLine.isEmpty()
                ) return@mapIndexed String()
                "|${trimLine}".replace(
                    Regex("^[|]{2,}"),
                    mainKeySeparator.toString()
                )
            }.map {
                JsActionKeyManager.ActionImportManager.putActionImportSubKey(it)
            }
        }
    }

}

private object PairToMapInList {

    private val jsMainKeyName = JsActionKeyManager.JsActionsKey.JS.key
    private val jsFuncMainKeyName = JsActionKeyManager.JsActionsKey.JS_FUNC.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key
    private val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
    private val tsvVarsMainKeyName = JsActionKeyManager.JsActionsKey.TSV_VARS.key
    private val afterSubKeyName = JsActionKeyManager.JsSubKey.AFTER.key
    private const val jsSubKeySeparator = '?'

    fun convert(
        keyToSubKeyConListByValidKey: List<Pair<String, String>>
    ): List<Pair<String, Map<String, String>>> {
        return keyToSubKeyConListByValidKey.mapIndexed { index, el ->
            val mainJsKeyName = el.first
            val mapConSrc = el.second
            val mainKey = JsActionKeyManager.JsActionsKey.values().firstOrNull {
                it.key == mainJsKeyName
            } ?: return@mapIndexed String() to emptyMap()
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_mapConSrc.txt").absolutePath,
//                listOf(
//                    "mainJsKeyName: ${mainJsKeyName}",
//                    "mapConSrc: ${mapConSrc}"
//                ).joinToString("\n\n") + "\n-----\n"
//            )
            when (mainKey) {
                JsActionKeyManager.JsActionsKey.ACTION_VAR,
                -> String() to emptyMap()
                JsActionKeyManager.JsActionsKey.JS,
                -> convertToJsMapOnlyIfBracketOrErrSignal(mapConSrc)
                JsActionKeyManager.JsActionsKey.JS_VAR
                -> VarShortSyntaxToJsFunc.toJsFunc(
                        mapConSrc,
                    )
                JsActionKeyManager.JsActionsKey.JS_FUNC
                -> convertFuncToJsFunc(
                    mapConSrc,
                )
                JsActionKeyManager.JsActionsKey.JS_PATH
                -> convertJsPathToJsFunc(
                    mapConSrc
                )
                JsActionKeyManager.JsActionsKey.TSV_VARS -> {
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

    private fun convertToJsMapOnlyIfBracketOrErrSignal(
        mapConSrc: String,
    ): Pair<String, Map<String, String>> {
        if(
            mapConSrc.isEmpty()
        ) return String() to emptyMap()
        val subKeyMap = CmdClickMap.createMap(
            QuoteTool.trimBothEdgeQuote(mapConSrc),
            jsSubKeySeparator
        ).toMap()
        val hasIfBracket = subKeyMap.containsKey(
            JsActionKeyManager.JsSubKey.IF_BRACKET_START.key
        ) || subKeyMap.containsKey(
            JsActionKeyManager.JsSubKey.IF_BRACKET_END.key
        )
        val hasSetTimeoutBracket = subKeyMap.containsKey(
            JsActionKeyManager.JsSubKey.SET_TIMEOUT_START_BRACKET.key
        ) || subKeyMap.containsKey(
            JsActionKeyManager.JsSubKey.SET_TIMEOUT_END_BRACKET.key
        )
        val hasFuncBracket = subKeyMap.containsKey(
            JsActionKeyManager.JsSubKey.FUNC_BRACKET_START.key
        ) || subKeyMap.containsKey(
            JsActionKeyManager.JsSubKey.FUNC_BRACKET_END.key
        )
        val hasActionImportPathNotFound = subKeyMap.get(
            JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
        )?.contains(JsActionKeyManager.PathExistChecker.notFoundCodePrefix) == true
        val hasActionImportPathNotRegisterInRepVal = subKeyMap.get(
            JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
        )?.contains(JsActionKeyManager.PathNotRegisterInRepValChecker.notRegisterCodePrefix) == true
        if(
            hasIfBracket
            || hasSetTimeoutBracket
            || hasFuncBracket
            || hasActionImportPathNotFound
            || hasActionImportPathNotRegisterInRepVal
        ) return jsMainKeyName to subKeyMap
        return jsMainKeyName to subKeyMap + mapOf(
            JsActionKeyManager.JsSubKey.FORBIDDEN_JS_KEY_DIRECT_SPECIFY.key
                    to String()
        )
    }
    private fun convertToTsvImportMap(
        mapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val tsvImportMapSrc = ImportMapMaker.comp(
            "${tsvVarsMainKeyName}=${mapConSrc}",
            "${tsvVarsMainKeyName}="
        )
        val importPathKey = JsActionKeyManager.CommonPathKey.IMPORT_PATH.key
        val importPath = tsvImportMapSrc.get(
            importPathKey
        ).let {
            JsActionKeyManager.PathExistChecker.makeCodeOrPath(it)
        }
        val tsvImportMap = tsvImportMapSrc.map {
            val key = it.key
            if(
                key != importPathKey
            ) return@map key to it.value
            key to importPath
        }.toMap()
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "ac_tsvVAls.txt").absolutePath,
//            listOf(
//                "mapConSrc: ${mapConSrc}",
//                "tsvImportMapSrc: ${tsvImportMapSrc}",
//                "importPathKey: ${importPathKey}",
//                "mapConSrc: ${mapConSrc}",
//                "importPath: ${tsvImportMapSrc.get(
//                    importPathKey
//                )}",
//                "tsvImportMap: ${tsvImportMap}"
//            ).joinToString("\n\n") + "\n-------------\n\n"
//        )
        return JsActionKeyManager.JsActionsKey.TSV_VARS.key to tsvImportMap
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
    private val itSuggerVarRegex = Regex("([^a-zA-Z0-9_])${itPronoun}([^a-zA-Z0-9_])")
    private val itSuggerVarRegexEndVer = Regex("([^a-zA-Z0-9_])${itPronoun}$")
    fun toJsFunc(
        varMapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val varName = QuoteTool.trimBothEdgeQuote(
            varMapConSrc.split(jsSubKeySeparator)
                .firstOrNull() ?: String()
        )
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
        ) ?: return String() to emptyMap()
        val nextIndex = 1
        val valueOrIfConList = makeVarKeyToConPairListForJsVarMacro(
            varMapConPairList,
            nextIndex
        )
        val valueOrFuncMapToSeedIndex = extractFirstValueOrFuncMap(
            valueOrIfConList,
        )
        val valueOrFuncMapSrc = valueOrFuncMapToSeedIndex.first
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_valueOrFuncMapSrc.txt").absolutePath,
//            listOf(
//                "varMapConPairListSrc: ${varMapConPairListSrc}",
//                "valueOrIfConList: ${valueOrIfConList}",
//                "valueOrFuncMapToSeedIndex: ${valueOrFuncMapToSeedIndex}",
//                "valueOrFuncMapSrc: ${valueOrFuncMapSrc}",
//            ).joinToString("\n\n")
//        )
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
//                "onlySubKeyMapToVarMapConPairList: ${onlySubKeyMapToVarMapConPairList}",
//                "onlySubKeyMap: ${onlySubKeyMap}",
//                "varMapConPairList: ${varMapConPairList}",
//                "valueOrIfConList: ${valueOrIfConList}",
//                "nextVarKeyToConPairList: ${nextVarKeyToConPairList}",
//                "jsKeyConMapSrc: ${jsKeyConMapSrc}"
//            ).joinToString("\n\n") + "\n---\n"
//        )
        return jsMainKeyName to jsKeyConMap
    }

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
        }
    }

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
        val isNotIfKey = ifEntryKeyToCon.first != ifSubKeyName
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

    private val tsvVars = JsActionKeyManager.JsActionsKey.TSV_VARS.key
    val tsvImportPreWord = TsvImportManager.tsvImportPreWord

    fun make(
        keyToSubKeyMapList: List<Pair<String, Map<String, String>>>,
    ): String {
        val tsvVarKeyName = JsActionKeyManager.JsActionsKey.TSV_VARS.key

        val keyToSubKeyMapListOnlyImport = keyToSubKeyMapList.filter {
                keyToSubKeyPair ->
            val mainJsKeyName = keyToSubKeyPair.first
            mainJsKeyName == tsvVarKeyName
        }
        return  keyToSubKeyMapListOnlyImport.map {
                keyToSubKeyMap ->
            execPut(
                keyToSubKeyMap
            )
        }.joinToString("\n")
    }
    private fun execPut(
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
            tsvImportPreWord,
            importImportPath
        ).joinToString(" ")
        val useMapCon = tsvImportMap.get(
            tsvVars
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
        return jsMapListWithoutAfterSubKey.map {
            execMake(
                it,
                jsMapListWithAfterSubKey,
            )
        }.joinToString("\n")
    }

    private fun execMake(
        jsMap: Map<String, String>?,
        jsMapListOnlyAfter: List<Map<String, String>>,
    ): String {
        if(
            jsMap.isNullOrEmpty()
        ) return String()
        createFuncStartBracket(
            jsMap,
        )?.let {
           return it
        }
        createFuncEndBracket(
            jsMap
        )?.let {
            return it
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
                )
            }
            else -> makeInsertFuncCon(
                jsMap,
                jsMapListOnlyAfter,
            )
        }
        return funcTemplate.format(
            insertFuncCon
        )
    }

    private fun createFuncStartBracket(
        jsMap: Map<String, String>,
    ): String? {
        val isNotFuncBracketStart = !jsMap.containsKey(
            JsActionKeyManager.JsSubKey.FUNC_BRACKET_START.key
        )
        if(
            isNotFuncBracketStart
        ) return null
        val funcBracketStartCon = "(function(){"
        val funcVarDifinition = jsMap.get(
            JsActionKeyManager.JsSubKey.FUNC_VAR.key
        ).let {
            if(
                it.isNullOrEmpty()
            ) return@let String()
            "var ${it} = "
        }
        val setTimeoutBracketStartCon = jsMap.containsKey(
            JsActionKeyManager.JsSubKey.SET_TIMEOUT_START_BRACKET.key
        ).let {
            if(
                !it
            ) return@let String()
            "setTimeout(function(){"
        }
        val ifBracketCon = jsMap.containsKey(
            JsActionKeyManager.JsSubKey.IF_BRACKET_START.key
        ).let {
            if(
                !it
            ) return@let String()
            val ifCondition = jsMap.get(
                JsActionKeyManager.JsSubKey.IF.key
            )
            if(
                ifCondition.isNullOrEmpty()
            ) return@let String()
            "if(${ifCondition}){"
        }
        return listOf(
            ifBracketCon,
            setTimeoutBracketStartCon,
            "${funcVarDifinition}${funcBracketStartCon}"
        ).filter { it.trim().isNotEmpty() }.joinToString("\n")
    }

    private fun createFuncEndBracket(
        jsMap: Map<String, String>,
    ): String? {
        val isNotFuncBracketEnd = !jsMap.containsKey(
            JsActionKeyManager.JsSubKey.FUNC_BRACKET_END.key
        )
        if(
            isNotFuncBracketEnd
        ) return null
        val setTimeoutBracketEndCon = jsMap.get(
            JsActionKeyManager.JsSubKey.SET_TIMEOUT_END_BRACKET.key
        ).let {
            if(
                it.isNullOrEmpty()
            ) return@let String()
            try{
                it.toInt()
            }catch(e: Exception){
                return@let "},0)"
            }
            "},${it})"
        } + ";"
        val funcBracketEndCon = "})();"
        val ifBracketCon = jsMap.containsKey(
            JsActionKeyManager.JsSubKey.IF_BRACKET_END.key
        ).let {
            if(
                !it
            ) return@let String()
            "}"
        }
        return listOf(
            funcBracketEndCon,
            setTimeoutBracketEndCon,
            ifBracketCon
        ).filter { it.trim().isNotEmpty() }.joinToString("\n")
    }

    private fun makeInsertLoopMethodCon(
        jsMap: Map<String, String>,
        jsMapListOnlyAfter: List<Map<String, String>>,
        loopMethodTemplate: String,
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
        )
        val compLoopMethodTemplateWithVar =
            loopMethodTemplateWithVar.format(
                listOf(
                    afterJsCon,
                    afterFuncCon
                ).joinToString("\n")
            )
        return listOf(
            compLoopMethodTemplateWithVar,
//            makeDeleteVarCon(jsMap),
        ).filter{
            it.trim().isNotEmpty()
        }.joinToString("\n")
    }

    private fun makeInsertFuncCon(
        jsMap: Map<String, String>,
        jsMapListOnlyAfter:  List<Map<String, String>>,
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
        val afterFuncCon = putAfterFunc(
            jsMap,
            jsMapListOnlyAfter,
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
            funcConWithVar,
            afterJsCon,
            afterFuncCon,
        ).filter {
            it.trim().isNotEmpty()
        }.joinToString("\n")
    }

    private fun putAfterFunc(
        jsMap: Map<String, String>,
        jsMapListOnlyAfter: List<Map<String, String>>,
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
        return afterJsMapList.map {
            afterJsMapSrc ->
            execMake(
                afterJsMapSrc,
                jsMapListOnlyAfter,
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
                    listOf("});"),
                ).flatten().joinToString("\n")
            }
            JsActionKeyManager.JsFuncManager.EnableLoopMethodType.MAP_METHOD_TYPE -> {
                listOf(
                    listOf("${functionName}(function(${elementArgName}, ${indexArgName}){"),
                    listOf(
                        "var ${elementValName} = ${elementArgName};",
                        "%s;",
                        "return ${elementValName};"
                    ).map { "\t${it}" },
                    listOf("});"),
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
    ): String {
        return loopArgsNameList.getOrNull(index)
            ?: String()
    }

    private fun makeFuncCon(
        jsMap: Map<String, String>,
        varargsStr: String,
    ): String {
        val functionName =
            jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
        val isOnlyVar = howVarOnly(jsMap)
        val funcConSrc = when(true){
            isOnlyVar ->
                JsActionKeyManager.JsVarManager.makeVarValue(jsMap)
            else -> makeFuncSignature(
                functionName,
                varargsStr,
            )
        }
        return "${funcConSrc};"
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
        ).map{
            val key = it.first
            if(
                key.isEmpty()
            ) return@map String()
            val valueStr = it.second
            valueStr
        }.filter { it.isNotEmpty() }
        return argList
    }

    private fun makeFuncTemplate(
        jsMap: Map<String, String>
    ): String {
        val ifCondition = jsMap.get(JsActionKeyManager.JsSubKey.IF.key)
        val delayMiliSec =
            try {
                jsMap.get(JsActionKeyManager.JsSubKey.DELAY.key)
                    ?.toInt()
            } catch (e: Exception){
                null
            }
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
        val funcTemplateSrc = JsActionKeyManager.makeFuncTemplateForIf(
            ifCondition,
            delayMiliSec
        )
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
