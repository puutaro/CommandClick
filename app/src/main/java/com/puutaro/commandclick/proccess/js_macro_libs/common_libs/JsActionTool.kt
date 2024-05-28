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
import com.puutaro.commandclick.util.str.QuoteTool
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
        val keyToSubKeyConWithReflectRepValDefalt = CmdClickMap.replaceHolderForJsAction(
            keyToSubKeyCon ?: String(),
            null
        )
        val keyToSubMapTypeMapToKeyToSubKeyConListByValidKey = createKeyToSubMapTypeMap(
            readSharePreferenceMap,
            keyToSubKeyConWithReflectRepValDefalt,
            setReplaceVariableMap,
        ) ?: return null
        val keyToSubMapTypeMap = keyToSubMapTypeMapToKeyToSubKeyConListByValidKey.first
        val actionImportedKeyToSubKeyConList = keyToSubMapTypeMapToKeyToSubKeyConListByValidKey.second
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
        val displayActionImportedAcCon = LogTool.DisplayActionImportedJsAcSrc.make(
            actionImportedKeyToSubKeyConList,
        )
        if(
            !macroDataMap.isNullOrEmpty()
        ) {
            LogTool.FirstJsActionLogSaver.save(
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
            readSharePreferenceMap,
            setReplaceVariableMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        )
        val jsActionMap = jsActionMapToJsConOnlyReplace?.first
        LogTool.FirstJsActionLogSaver.save(
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
            LogTool.KeyToSubKeyConTool.makeEvaluateAcCon(
                keyToSubKeyMapListWithoutAfterSubKey,
                keyToSubKeyMapListWithAfterSubKey,
            )
        LogTool.ForbiddenJsKeyDirectSpecifyErr.check(
            context,
            evaluateGeneCon
        ).let {
                isForbiddenJsKeyDirectSpecifyErr ->
            if(
                isForbiddenJsKeyDirectSpecifyErr
            ) return true
        }
        LogTool.QuoteNumCheck.check(
            context,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        ).let {
                isQuoteErr ->
            if(
                isQuoteErr
            ) return true
        }
        LogTool.MissVarKeyErr.check(
            context,
            actionImportedKeyToSubKeyConList
        ).let {
                isMissVarKeyErr ->
            if(
                isMissVarKeyErr
            ) return true
        }
        LogTool.MissAfterKeyErr.check(
            context,
            actionImportedKeyToSubKeyConList,
        ).let {
                isMissAfterKeyErr ->
            if(
                isMissAfterKeyErr
            ) return true
        }
        LogTool.IrregularAfterIdErr.check(
            context,
            actionImportedKeyToSubKeyConList,
        ).let {
                isIrregularAfterIdErr ->
            if(
                isIrregularAfterIdErr
            ) return true
        }
        LogTool.RunVarPrefixUseErrInAcImport.check(
            context,
            actionImportedKeyToSubKeyConList
        ).let {
                isRunVarPrefixUseErrInAcImport ->
            if(
                isRunVarPrefixUseErrInAcImport
            ) return true
        }
        LogTool.NotMatchToUseAfter.check(
            context,
            evaluateGeneCon
        ).let {
                isNotMatchToUseAfter ->
            if(
                isNotMatchToUseAfter
            ) return true
        }
        LogTool.PathNotFound.check(
            context,
            evaluateGeneCon,
            actionImportedKeyToSubKeyConList,
        ).let {
                isErrPath ->
            if(
                isErrPath
            ) {
                return true
            }
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
        LogTool.RunVarPrefixUsedErr.check(
            context,
            actionImportedKeyToSubKeyConList,
        ).let {
                isRunVarPrefixUsedErr ->
            if(
                isRunVarPrefixUsedErr
            ) return true
        }
        LogTool.NotMatchToUseVar.check(
            context,
            actionImportedKeyToSubKeyConList
        ).let {
                isNotMatchToUseVar ->
            if(
                isNotMatchToUseVar
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
        return false
    }

    private fun extractJsDataMap(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
    ): Pair<Map<String, String>, String?>? {
        val jsConToJsConOnlyReplace = convertJsCon(
            fragment,
            readSharePreferenceMap,
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
        readSharePreferenceMap: Map<String, String>,
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
        ActionImportPutter.initBeforeActionImportMap()
        var errType: ActionImportPutter.ErrSignal = ActionImportPutter.ErrSignal.NO_ERR
        for( i in 1..importRoopLimit) {
            keyToSubKeyConList = keyToSubKeyConList.map {
                    keyToSubKeyPair ->
                val mainKeyName = keyToSubKeyPair.first
                if(
                    mainKeyName != jsActionImportKeyName
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
                containImport =
                    putKeyToSubKeyCon.contains(jsActionImportSignal)
                makeKeyToSubConPairListByValidKey(
                    putKeyToSubKeyCon
                )
            }.flatten()
            if(errType == ActionImportPutter.ErrSignal.ERR) break
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
    private val actionImportKey =
        JsActionKeyManager.JsActionsKey.ACTION_IMPORT.key
    private val actionImportVirtualSubKey =
        JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
    private val missVarKey =
        JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_VAR_KEY.key
    private val missAfterKey =
        JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_AFTER_KEY.key
    private val irregularAfterIdKey =
        JsActionKeyManager.ActionImportManager.ActionImportKey.IRREGULAR_AFTER_ID.key
    private val useAfterKey =
        JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key
    private val useVarKey =
        JsActionKeyManager.ActionImportManager.ActionImportKey.USE_VAR.key
    private val afterKey = JsActionKeyManager.JsSubKey.AFTER.key
    private val varKey = JsActionKeyManager.JsSubKey.VAR.key
    private val beforeActionImportMap = mutableMapOf<String, String>()
    private val tsvImportKey = JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
    private val jsImportKey = JsActionKeyManager.JsActionsKey.JS_IMPORT.key
    private val afterIdToNotMatchAfterIdSeparator = "\t"


    enum class ErrSignal {
        NO_ERR,
        ERR,
    }
    enum class UseVarErrType {
        NOT_MATCH_SRC_VAR_TO_USE_VAR,
        MISS_VAR_KEY,
        RUN_VAR_PREFIX_USE_ERR_IN_AC_IMPORT,
    }


    enum class UseAfterErrType{
        NOT_MATCH_SRC_AFTER_TO_USE_AFTER,
        MISS_AFTER_KEY,
        IRREGULAR_AFTER_ID,
    }

    fun initBeforeActionImportMap(){
        beforeActionImportMap.clear()
    }
    fun put(
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyPair: Pair<String, String>,
    ): Pair<String, ErrSignal> {
        val subKeyCon = listOf(
            JsActionKeyManager.CommonPathKey.IMPORT_PATH.key,
            keyToSubKeyPair.second
        ).joinToString("=")
        val actionImportMap = ImportMapMaker.comp(
            subKeyCon,
            "${JsActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key}="
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

        val importConSrcToErrType = when(isNotFoundPrefix){
            true ->
                "${jsMainKey}=${actionImportVirtualSubKey}=${importPath}" to ErrSignal.NO_ERR
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
                val importConWithFormatList = makeActionImportFormatList(
                    importSrcCon,
                )
                val useVarValue = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        JsActionKeyManager.ActionImportManager.ActionImportKey.USE_VAR.key
                    )
                )
                val whenCondition = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        JsActionKeyManager.ActionImportManager.ActionImportKey.WHEN.key
                    )
                )
                val useAfterValue = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(useAfterKey)
                )
                val importConListToErrType = putIfBracketByWhen(
                    importConWithFormatList,
                    whenCondition,
                    useVarValue,
                    useAfterValue,
                )
                val importCon = importConListToErrType.first.joinToString("\n")
                val errType = importConListToErrType.second
                importCon to errType
            }
        }
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

    private fun makeCompMap2(
        subKeyCon: String,
        jsMainKeyPrefix: String,
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

    private fun putIfBracketByWhen(
        importConWithFormatList: List<String>,
        whenCondition: String?,
        useVarValue: String?,
        useAfterValue: String?,
    ): Pair<List<String>, ErrSignal> {

        val importConWithFormatListByUseVarToUseVarErr =
            ImportConWithFormatListForUseVar.update(
                importConWithFormatList,
                useVarValue,
            )
        val errSignalByUseVar = importConWithFormatListByUseVarToUseVarErr.second
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_errSignalByUseVar.txt").absolutePath,
//            listOf(
//                "errSignalByUseVar: ${errSignalByUseVar.name}",
//                "importConWithFormatListByUseVar: ${importConWithFormatListByUseVarToUseVarErr.first}",
//            ).joinToString("\n\n")
//        )
        if(errSignalByUseVar == ErrSignal.ERR){
            val importConWithFormatListByUseVar =
                importConWithFormatListByUseVarToUseVarErr.first
            val updatedImportConByWhenCondition = updateImportConByWhenCondition(
                importConWithFormatListByUseVar,
                whenCondition,
                null
            )
            return updatedImportConByWhenCondition to ErrSignal.ERR
        }
        val importConWithFormatListByUseVar =
            importConWithFormatListByUseVarToUseVarErr.first
        return ImportConWithFormatListForUseAfter.update(
            importConWithFormatListByUseVar,
            useAfterValue,
            whenCondition
        )
    }

    private object ImportConWithFormatListForUseVar {
        fun update(
            importConWithFormatList: List<String>,
            useVarValue: String?,
        ): Pair<List<String>, ErrSignal> {
            if (
                useVarValue.isNullOrEmpty()
            ) return importConWithFormatList to ErrSignal.NO_ERR
            val updatedVarKeyValueToErrType = makeVarKeyValueToSrcVarKeyCon(
                importConWithFormatList,
                useVarValue,
            )
            val errTypeForUseVar = updatedVarKeyValueToErrType.second
            val updatedVarKeyValue = updatedVarKeyValueToErrType.first
            val updatedImportConWithFormatList = execUpdateImportConWithFormatListForUseVar(
                updatedVarKeyValue,
                errTypeForUseVar,
                importConWithFormatList,
            )
            val errSignal = when (errTypeForUseVar == null) {
                true -> ErrSignal.NO_ERR
                else -> ErrSignal.ERR
            }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_varName.txt").absolutePath,
//            listOf(
//                "updatedImportConWithFormatList: ${updatedImportConWithFormatList}",
//                "errTypeForUseVar: ${errTypeForUseVar?.name}",
//                "errSignal: ${errSignal.name}",
//            ).joinToString("\n\n") + "\n-------------\n"
//        )
            return updatedImportConWithFormatList to errSignal
        }

        private fun execUpdateImportConWithFormatListForUseVar(
            updatedVarKeyValue: String,
            errType: UseVarErrType?,
            importConWithFormatList: List<String>,
        ): List<String> {
//        FileSystems.updateFile(
//            File(
//                UsePath.cmdclickDefaultAppDirPath,
//                "jsAc_execUpdateImportConWithFormatListForUseVar.txt"
//            ).absolutePath,
//            listOf(
//                "updatedVarKeyValue: ${updatedVarKeyValue}",
//                "errType: ${errType?.name}",
//            ).joinToString("\n\n")
//        )
            return when(errType){
                null -> updateImportConListByOrdinaryForUseVar(
                    importConWithFormatList,
                    updatedVarKeyValue,
                )
                UseVarErrType.NOT_MATCH_SRC_VAR_TO_USE_VAR -> {
                    updateWhenNotMatchSrcVarToUseVar(
                        importConWithFormatList,
                        updatedVarKeyValue,
                    )
                }
                UseVarErrType.MISS_VAR_KEY ->{
                    updateImportConSrcListWhenMissVarKey(
                        importConWithFormatList
                    )
                }
                UseVarErrType.RUN_VAR_PREFIX_USE_ERR_IN_AC_IMPORT -> {
                    updateWhenRunVarPrefixUseErrInAcImport(
                        importConWithFormatList,
                        updatedVarKeyValue,
                    )
                }
            }
        }

        private fun updateImportConListByOrdinaryForUseVar(
            importConWithFormatList: List<String>,
            updatedVarKeyValue: String,
        ): List<String> {
            val varKeyValueRegex = Regex(
                "\\|${varKey}=[^|?\n]+"
            )
            val useVarKeyValueRegex =
                Regex("\\?${useVarKey}=[^?|\n]+")
            val useVarAllow =
                JsActionKeyManager.ActionImportManager.useVarAllow
            return importConWithFormatList.reversed().mapIndexed {
                    index, keyCon ->
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath,
//                    "jsAc_ordinalyUpdateImportConList001.txt").absolutePath,
//                listOf(
//                    "index: ${index}",
//                    "keyCon: ${keyCon}",
//                ).joinToString("\n\n")
//            )
                if(
                    index != 0
                ) return@mapIndexed keyCon
                val updatedLastKeyCon = keyCon.replace(
                    varKeyValueRegex,
                    updatedVarKeyValue
                )
                if(updatedLastKeyCon != keyCon){
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsAc_ordinalyUpdateImportConList00.txt").absolutePath,
//                    listOf(
//                        "keyCon: ${keyCon}",
//                        "updatedVarKeyValue: ${updatedVarKeyValue}",
//                        "updatedLastKeyCon: ${updatedLastKeyCon}",
//                    ).joinToString("\n\n") + "\n-----------\n\n"
//                )
                    return@mapIndexed updatedLastKeyCon
                }
                val lastUseVarCon = useVarKeyValueRegex.find(
                    keyCon
                )?.value?.removePrefix("?${useVarKey}=")?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@mapIndexed keyCon
                val lastUseVarNameSrcToDestiPair = makeUseVarPair(lastUseVarCon)
                    ?: return@mapIndexed keyCon
                val lastSrcVarName = lastUseVarNameSrcToDestiPair.first
                val updatedDestiVarName = updatedVarKeyValue.removeSuffix(
                    "|${varKey}="
                ).let {
                    QuoteTool.trimBothEdgeQuote(it)
                }
                val replaceUseVarValue =
                    "?${useVarKey}=`${lastSrcVarName} ${useVarAllow} ${updatedDestiVarName}`"
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_ordinalyUpdateImportConList11.txt").absolutePath,
//                listOf(
//                    "replaceUseVarValue: ${replaceUseVarValue}",
//                    "updatedkeyCon: ${keyCon.replace(
//                        useVarKeyValueRegex,
//                        replaceUseVarValue
//                    )}",
//                ).joinToString("\n\n") + "\n-----------\n\n"
//            )
                keyCon.replace(
                    useVarKeyValueRegex,
                    replaceUseVarValue
                )
            }.reversed()
        }

        private fun updateWhenNotMatchSrcVarToUseVar(
            importConWithFormatList: List<String>,
            notMatchKeyCon: String,
        ): List<String> {
            val varKeyValueRegex = Regex("(\\|${varKey}=[^?|\n]+)")
            val useVarKeyValueRegex = Regex("(\\?${useVarKey}=[^?|\n]+)")
            return importConWithFormatList.reversed().mapIndexed {
                    index, keyCon ->
                if(
                    index != 0
                ) return@mapIndexed keyCon
                keyCon.replace(
                    varKeyValueRegex,
                    "$1${notMatchKeyCon}?"
                ).replace(
                    useVarKeyValueRegex,
                    "$1${notMatchKeyCon}?"
                )
            }.reversed()
        }

        private fun updateImportConSrcListWhenMissVarKey(
            importConWithFormatList: List<String>
        ): List<String> {
            return importConWithFormatList.reversed().mapIndexed {
                    index, keyCon ->
                if(
                    index != 0
                ) return@mapIndexed keyCon
                val judgeCon = "|${keyCon.trim()}"
                "${judgeCon}?${missVarKey}="
            }.reversed()
        }


        private fun updateWhenRunVarPrefixUseErrInAcImport(
            importConWithFormatList: List<String>,
            notMatchKeyCon: String,
        ): List<String> {
            val varKeyValueRegex = Regex("(\\|${varKey}=[^?|\n]+)")
            val useVarKeyValueRegex = Regex("(\\?${useVarKey}=[^?|\n]+)")
            return importConWithFormatList.reversed().mapIndexed {
                    index, keyCon ->
                if(
                    index != 0
                ) return@mapIndexed keyCon
                keyCon.replace(
                    varKeyValueRegex,
                    "$1${notMatchKeyCon}?"
                ).replace(
                    useVarKeyValueRegex,
                    "$1${notMatchKeyCon}?"
                )
            }.reversed()
        }

        private fun makeVarKeyValueToSrcVarKeyCon(
            importConWithFormatList: List<String>,
            useVarValue: String?,
        ): Pair<String, UseVarErrType?> {
            if(
                useVarValue.isNullOrEmpty()
            ) return String() to null
            val useVarNameSrcToDestiPair = makeUseVarPair(
                useVarValue
            ) ?: return String() to null
            val useSrcVarName =
                useVarNameSrcToDestiPair.first
            val lastJsElement =
                importConWithFormatList.lastOrNull()
                    ?: return String() to null
            val varNameByVarKeyOrUseVarKey = getVarNameFromJsEl(lastJsElement)
            if(
                varNameByVarKeyOrUseVarKey.isNullOrEmpty()
            ) {
                return listOf(
                    "|${varKey}=${varNameByVarKeyOrUseVarKey}",
                    "?${useVarKey}=${useVarValue}"
                ).joinToString(String()) to
                        UseVarErrType.MISS_VAR_KEY
            }
            val useDestiVarName =
                useVarNameSrcToDestiPair.second
            val escapeRunPrefix = JsActionKeyManager.JsVarManager.escapeRunPrefix
            val isEscapeRunPrefix = useSrcVarName.startsWith(escapeRunPrefix)
                    || useDestiVarName.startsWith(escapeRunPrefix)
            if(
                isEscapeRunPrefix
            ){
                val runVarPrefixErrConPrefix =
                    JsActionKeyManager.ActionImportManager
                        .ActionImportKey.RUN_VAR_PREFIX_USE_ERR_IN_AC_IMPORT.key
                val errConSeparator =
                    JsActionKeyManager.ActionImportManager.errConSeparator
                val runVarPrefixErrCon =
                    "?${runVarPrefixErrConPrefix}=${useSrcVarName}${errConSeparator}${useDestiVarName}"
                val runVarPrefixErrConInsertCon = listOf(
                    runVarPrefixErrCon,
                    "?${useVarKey}=${useVarValue}"
                ).joinToString(String())
                return runVarPrefixErrConInsertCon to UseVarErrType.RUN_VAR_PREFIX_USE_ERR_IN_AC_IMPORT
            }
            val varNameByOnlyVarKey = getVarNameFromJsElByOnlyVarKey(
                lastJsElement
            )
            if(
                varNameByOnlyVarKey != useSrcVarName
                && !varNameByOnlyVarKey.isNullOrEmpty()
            ){
                val notMatchSrcVarToUseVarKeyCon =
                    JsActionKeyManager.ActionImportManager
                        .ActionImportKey.NOT_MATCH_SRC_VAR_TO_USE_VAR.key
                val errConSeparator =
                    JsActionKeyManager.ActionImportManager.errConSeparator
                val notMatchSrcAfterToUseAfterKeyCon =
                    "?${notMatchSrcVarToUseVarKeyCon}=${varNameByOnlyVarKey}${errConSeparator}${useDestiVarName}"
                val insertVarCon = listOf(
                    notMatchSrcAfterToUseAfterKeyCon,
                    "?${useVarKey}=${useVarValue}"
                ).joinToString(String())
                return insertVarCon to
                        UseVarErrType.NOT_MATCH_SRC_VAR_TO_USE_VAR
            }

            return "|${varKey}=${useDestiVarName}" to null
        }

        private fun getVarNameFromJsEl(
            jsImportSubKeyCon: String,
        ): String? {
            val varNameWithQuote =
                Regex("\\|${varKey}=[^?|\n]+").find(
                    jsImportSubKeyCon
                )?.value?.removePrefix(
                    "|${varKey}="
                )
            val varName = QuoteTool.trimBothEdgeQuote(varNameWithQuote)
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcgetVarNameFromJsEl.txt").absolutePath,
//            listOf(
//                "jsImportSubKeyCon: ${jsImportSubKeyCon}",
//                "afterIdWithQuote: ${varNameWithQuote}",
//                "varName: ${varName}",
//            ).joinToString("\n\n") + "\n------------\n"
//        )
            if(
                varName.isNotEmpty()
            ) return varName
            val useVarValue = getUseVarValueFromEl(jsImportSubKeyCon)
            val useVarSrcToDestiPair = makeUseVarPair(
                useVarValue
            )
            return useVarSrcToDestiPair?.second
        }
        private fun getVarNameFromJsElByOnlyVarKey(
            jsImportSubKeyCon: String,
        ): String? {
            val varNameWithQuote =
                Regex("\\|${varKey}=[^?|\n]+").find(
                    jsImportSubKeyCon
                )?.value?.removePrefix(
                    "|${varKey}="
                )
            return QuoteTool.trimBothEdgeQuote(varNameWithQuote)
                .ifEmpty { null }
        }
        private fun getUseVarValueFromEl(
            jsImportSubKeyCon: String,
        ): String {
            val useVarNameWithQuote =
                Regex("\\?${useVarKey}=[^?|\n]+").find(
                    jsImportSubKeyCon
                )?.value?.removePrefix(
                    "?${useVarKey}="
                )
            return QuoteTool.trimBothEdgeQuote(useVarNameWithQuote)
        }

        private fun makeUseVarPair(
            useVarValue: String?
        ): Pair<String, String>? {
            val useVarAllow =
                JsActionKeyManager.ActionImportManager.useVarAllow
            return useVarValue
                ?.split(useVarAllow)
                ?.map {
                    it.trim()
                }?.let {
                        keyValueList ->
                    val key = keyValueList.firstOrNull()
                        ?: return@let null
                    val value = keyValueList.getOrNull(1)
                        ?: key
                    key to value
                }
        }
    }


    private object ImportConWithFormatListForUseAfter {

        private enum class UseAfterExtraMapKey(
        ) {
            IRREGULAR_AFTER_ID
        }
        fun update(
            importConWithFormatListByUseVar: List<String>,
            useAfterValue: String?,
            whenCondition: String?
        ): Pair<List<String>, ErrSignal> {
            if (
                useAfterValue.isNullOrEmpty()
            ) return updateImportConByWhenCondition(
                importConWithFormatListByUseVar,
                whenCondition,
            ) to ErrSignal.NO_ERR
            val useAfterKeyConToErrTypeToExtra = makeUseAfterKeyConToSrcAfterKeyCon(
                importConWithFormatListByUseVar,
                useAfterValue,
            )
            val useAfterKeyValue =
                useAfterKeyConToErrTypeToExtra.first
            val errType = useAfterKeyConToErrTypeToExtra.second
            val extraUseAfterMap = useAfterKeyConToErrTypeToExtra.third
            val updatedImportConWithFormatListByUseAfter =
                execUpdateImportConWithFormatListForUseAfter(
                    useAfterKeyValue,
                    errType,
                    importConWithFormatListByUseVar,
                    extraUseAfterMap,
                )
            if (
                whenCondition.isNullOrEmpty()
            ) {
                return updatedImportConWithFormatListByUseAfter to ErrSignal.NO_ERR
            }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsImoprtafterPhrase.txt").absolutePath,
//            listOf(
//                "firstorNull: ${updatedImportConWithFormatListByUseAfter.firstOrNull()}",
//                "importConWithFormatList: ${updatedImportConWithFormatListByUseAfter.firstOrNull()}",
//                "useAfterValue: ${useAfterValue}",
//            ).joinToString("\n\n") + "\n-------\n"
//        )
            val importConList = updateImportConByWhenCondition(
                updatedImportConWithFormatListByUseAfter,
                whenCondition,
                useAfterKeyValue,
            )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsImoprtafterPhrase11.txt").absolutePath,
//            listOf(
//                "importConWithFormatListByUseVar: ${importConWithFormatListByUseVar}",
//                "importConWithFormatList: ${updatedImportConWithFormatListByUseAfter.firstOrNull()}",
//                "useAfterValue: ${useAfterValue}",
//                "importConList: ${importConList}"
//            ).joinToString("\n\n") + "\n-------\n"
//        )
            val errSignal = when (errType == null) {
                true -> ErrSignal.NO_ERR
                else -> ErrSignal.ERR
            }
            return importConList to errSignal
        }

        private fun makeUseAfterKeyConToSrcAfterKeyCon(
            importConWithFormatList: List<String>,
            useAfterValue: String?,
        ): Triple<String, UseAfterErrType?, Map<UseAfterExtraMapKey, String>?> {
            if(
                useAfterValue.isNullOrEmpty()
            ) return Triple(
                String(),
                null,
                null
            )
            val afterKeyConRegex = Regex("\\?${afterKey}=[^?|\n]+")
            val firstJsElement = importConWithFormatList.firstOrNull {
                val trimLine = it.trim()
                val hasNotTsvImport = !trimLine.startsWith("|${tsvImportKey}=")
                val hasNotJsImport = !trimLine.startsWith("|${jsImportKey}=")
                it.isNotEmpty() && hasNotTsvImport && hasNotJsImport
            } ?: String()
            val afterId = getAfterIdFromJsEl(firstJsElement)
//        FileSystems.updateFile(
//            File(
//                UsePath.cmdclickDefaultAppDirPath,
//                "jsAc_makeUseAfterKeyConToSrcAfterKeyCon.txt"
//            ).absolutePath,
//            listOf(
//                "importConWithFormatList: ${importConWithFormatList}",
//                "importConWithFormatList.size: ${importConWithFormatList.size}",
//                "firstOrNull: ${importConWithFormatList.firstOrNull()}",
//                "afterId: ${afterId}",
//            ).joinToString("\n\n") + "\n-------------\n\n"
//        )
            val errConSeparator =
                JsActionKeyManager.ActionImportManager.errConSeparator
            val notMatchSrcAfterToUseAfter =
                JsActionKeyManager.ActionImportManager
                    .ActionImportKey.NOT_MATCH_SRC_AFTER_TO_USE_AFTER.key
            if(
                afterId.isNullOrEmpty()
            ) {
                return Triple(
                    listOf(
                        "?${afterKey}=${afterId}",
                        "?${useAfterKey}=${useAfterValue}"
                    ).joinToString(String()),
                    UseAfterErrType.MISS_AFTER_KEY,
                    null
                )

            }
            isMissAfterKeyErr(
                afterKeyConRegex,
                importConWithFormatList
            ).let {
                    isNotHasAllAfterKey ->
                if(
                    !isNotHasAllAfterKey
                ) return@let
                return Triple(
                    listOf(
                        "?${afterKey}=${afterId}",
                        "?${useAfterKey}=${useAfterValue}"
                    ).joinToString(String()),
                    UseAfterErrType.MISS_AFTER_KEY,
                    null
                )

            }
            extractAfterIdToNotMatchAfterId(importConWithFormatList).let {
                    afterIdToNotMatchAfterId ->
                if(
                    afterIdToNotMatchAfterId == null
                ) return@let
                val firstAfterId = afterIdToNotMatchAfterId.first
                val irregularAfterId = afterIdToNotMatchAfterId.second
                return Triple(
                    "?${afterKey}=${firstAfterId}${afterIdToNotMatchAfterIdSeparator}${irregularAfterId}",
                    UseAfterErrType.IRREGULAR_AFTER_ID,
                    mapOf(
                        UseAfterExtraMapKey.IRREGULAR_AFTER_ID to irregularAfterId
                    )
                )
            }

            val useAfterSrcToDestiPair = makeUseAfterPair(useAfterValue)
                ?: let {
                    val notMatchSrcAfterToUseAfterKeyCon =
                        "?${notMatchSrcAfterToUseAfter}=${afterId}${errConSeparator}"
                    val insertAfterCon = listOf(
                        notMatchSrcAfterToUseAfterKeyCon,
                        "?${useAfterKey}=${useAfterValue}"
                    ).joinToString(String())
                    return Triple(
                        insertAfterCon,
                        UseAfterErrType.NOT_MATCH_SRC_AFTER_TO_USE_AFTER,
                        null
                    )
                }
            val useCurAfterId = useAfterSrcToDestiPair.first
            val useDestiAfterId =
                useAfterSrcToDestiPair.second.let {
                        destiUseAfterId ->
                    val isMatch =
                        useCurAfterId == afterId
                    if(
                        isMatch
                    ) return@let destiUseAfterId
                    val notMatchSrcAfterToUseAfterKeyCon =
                        "?${notMatchSrcAfterToUseAfter}=${afterId}${errConSeparator}${useCurAfterId}"
                    val insertAfterCon = listOf(
                        notMatchSrcAfterToUseAfterKeyCon,
                        "?${useAfterKey}=${useAfterValue}"
                    ).joinToString(String())
                    return Triple(
                        insertAfterCon,
                        UseAfterErrType.NOT_MATCH_SRC_AFTER_TO_USE_AFTER,
                        null
                    )
                }
            return Triple(
                "?${afterKey}=${useDestiAfterId}",
                null,
            null,
            )
        }

        private fun execUpdateImportConWithFormatListForUseAfter(
            useAfterKeyValueSrc: String,
            errType: UseAfterErrType?,
            importConWithFormatList: List<String>,
            extraUseAfterMap: Map<UseAfterExtraMapKey, String>?
        ): List<String> {
            val afterKeyValueRegex = Regex("\\?${afterKey}=[^?|\n]+")
            return when(errType){
                null -> updateImportConListByOrdinary(
                    importConWithFormatList,
                    afterKeyValueRegex,
                    useAfterKeyValueSrc,
                )
                UseAfterErrType.NOT_MATCH_SRC_AFTER_TO_USE_AFTER ->
                    updateWhenNotMatchSrcAfterToUseAfter(
                        importConWithFormatList,
                        useAfterKeyValueSrc,
                    )
                UseAfterErrType.MISS_AFTER_KEY ->
                    updateImportConSrcListWhenMissAfterKey(
                        importConWithFormatList
                    )
                UseAfterErrType.IRREGULAR_AFTER_ID ->
                    updateWhenIrregularAfterId(
                        importConWithFormatList,
                        extraUseAfterMap?.get(
                            UseAfterExtraMapKey.IRREGULAR_AFTER_ID
                        ),
                    )
            }
        }

        private fun updateWhenNotMatchSrcAfterToUseAfter(
            importConWithFormatList: List<String>,
            useAfterKeyValue: String,
        ): List<String> {
            val afterKeyValueRegex = Regex("(\\?${afterKey}=[^?|\n]+)")
            return importConWithFormatList.map {
                val repLine = it.replace(
                    afterKeyValueRegex,
                    "$1${useAfterKeyValue}"
                )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_updateWhenNotMatchSrcAfterToUseAfter.txt").absolutePath,
//                listOf(
//                    "line ${it}",
//                    "repLine ${repLine}",
//                ).joinToString("\n\n") + "\n-------------\n"
//            )
                repLine
            }
        }

        private fun updateImportConSrcListWhenMissAfterKey(
            importConWithFormatList: List<String>
        ): List<String> {
            return importConWithFormatList.map {
                val judgeCon = "|${it.trim()}"
                val hasNotTsvImport = !judgeCon.contains("|${tsvImportKey}=")
                val hasNotJsImport = !judgeCon.contains("|${jsImportKey}=")
                val hasNotAfterKey = !judgeCon.contains("?${afterKey}=")
                val isUpdate = hasNotTsvImport
                        && hasNotJsImport
                        && hasNotAfterKey
                when(isUpdate){
                    true -> "${judgeCon}?${missAfterKey}="
                    else -> judgeCon
                }
            }
        }

        private fun updateWhenIrregularAfterId(
            importConWithFormatList: List<String>,
            irregularAfterId: String?,
        ): List<String> {
//            val useAfterKeyConAndIrregularAfterValue =
//                useAfterKeyConSrc.split(afterIdToNotMatchAfterIdSeparator)
////                val useAfterKeyCon =
////                    useAfterKeyConAndIllegularAfterValue.firstOrNull()
////                        ?: String()
//            val irregularAfterValue =
//                useAfterKeyConAndIrregularAfterValue.getOrNull(1)
//                    ?: String()
            return importConWithFormatList.map {
                val judgeCon = "|${it.trim()}"
                val hasTsvImport = judgeCon.contains("|${tsvImportKey}=")
                val hasJsImport = judgeCon.contains("|${jsImportKey}=")
                val curAfterId = getAfterIdFromJsEl(judgeCon)
                val isNotIrregular =
                    curAfterId != irregularAfterId
                            || curAfterId.isNullOrEmpty()
                val isNotUpdate = hasTsvImport
                        || hasJsImport
                        || isNotIrregular
//            FileSystems.updateFile(
//                File(
//                    UsePath.cmdclickDefaultAppDirPath,
//                    "jsAc_updateImportConWithFormatList.txt"
//                ).absolutePath,
//                listOf(
//                    "useAfterKeyConSrc: ${useAfterKeyConSrc}",
//                    "judgeCon: ${judgeCon}",
//                    "irregularAfterValue: ${irregularAfterValue}",
//                    "isNotUpdate: ${isNotUpdate}",
//                    "isNotIrregular: ${isNotIrregular}",
//                ).joinToString("\n\n") + "\n^--------\n\n"
//            )
                if(
                    isNotUpdate
                ) return@map judgeCon
                "${judgeCon}?${irregularAfterIdKey}=${irregularAfterId}"
            }
        }

        private fun updateImportConListByOrdinary(
            importConWithFormatList: List<String>,
            afterKeyValueRegex: Regex,
            useAfterKeyValueSrc: String,
        ): List<String> {
            val findUseAfterRegex = Regex(
                "${useAfterKey}=[^|?\n]+"
            )
            return importConWithFormatList.map {
                val jsConByUpdateAfter = it.replace(
                    afterKeyValueRegex,
                    useAfterKeyValueSrc
                )
                val useAfterKeyValue =
                    findUseAfterRegex.find(jsConByUpdateAfter)?.value
                        ?: return@map jsConByUpdateAfter
                val repUseAfterKeyValue = makeRepUseAfterKeyValue(
                    useAfterKeyValue,
                    jsConByUpdateAfter,
                    useAfterKeyValue,
                )
                jsConByUpdateAfter.replace(
                    useAfterKeyValue,
                    repUseAfterKeyValue
                )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_ordinalyUpdateImportConList.txt").absolutePath,
//                listOf(
//                    "useDesitAfterKeyId: ${useDesitAfterKeyId}",
//                    "repElByUseAfter: ${repElByUseAfter}",
//                ).joinToString("\n\n") + "\n-----------\n\n"
//            )
            }
        }

        private fun makeRepUseAfterKeyValue(
            useAfterKeyValue: String,
            jsConByUpdateAfter: String,
            useAfterKeyValueSrc: String,
        ): String {
            val useAfterValue = QuoteTool.trimBothEdgeQuote(
                useAfterKeyValue.removePrefix(
                    "${useAfterKey}="
                )
            )
            val srcAndDestiPair = makeUseAfterPair(useAfterValue)
                ?: return jsConByUpdateAfter
            val srcAfterId = srcAndDestiPair.first
//                val destiAfterId = srcAndDestiPair.second
            val useDestiAfterKeyId = getAfterIdFromJsEl(
                useAfterKeyValueSrc
            ) ?: return "${useAfterKey}=`${srcAfterId}`"
            val repUseAfterKeyValue =
                "${useAfterKey}=`${srcAfterId} => ${useDestiAfterKeyId}`"
            return repUseAfterKeyValue
        }

        private fun extractAfterIdToNotMatchAfterId(
            importConWithFormatList: List<String>,
        ): Pair<String, String>? {
            var afterId = String()
            return importConWithFormatList.map {
                    jsKeyCon ->
                val hasTsvImportKey = jsKeyCon.contains("|${tsvImportKey}=")
                val hasJsImportKey = jsKeyCon.contains("|${jsImportKey}=")
                if(
                    hasTsvImportKey
                    || hasJsImportKey
                    || jsKeyCon.isEmpty()
                ) {
//                FileSystems.updateFile(
//                    File(
//                        UsePath.cmdclickDefaultAppDirPath,
//                        "jsAc_extractAfterIdToNotMatchAfterId00.txt"
//                    ).absolutePath,
//                    listOf(
//                        "judgeJsKeyCon: ${judgeJsKeyCon}",
//                        "importConWithFormatList: ${importConWithFormatList}",
//                        "afterId: ${afterId}",
//                    ).joinToString("\n\n") + "\n-------------\n\n"
//                )
                    return@map String() to String()
                }
                val hasActionImportKey =
                    jsKeyCon.contains("|${actionImportKey}=")
                val useAfterId = when(hasActionImportKey){
                    true -> {
                        val useAfterValue = getUseAfterKeyFromEl(jsKeyCon)
                        makeUseAfterPair(useAfterValue)?.second
                    }
                    else -> {
                        getAfterIdFromJsEl(jsKeyCon)
                    }
                } ?: String()
                if(afterId.isEmpty()){
                    afterId = useAfterId
                }
//            FileSystems.updateFile(
//                File(
//                    UsePath.cmdclickDefaultAppDirPath,
//                "jsAc_extractAfterIdToNotMatchAfterId.txt"
//                ).absolutePath,
//            listOf(
//                "judgeJsKeyCon: ${judgeJsKeyCon}",
//                "importConWithFormatList: ${importConWithFormatList}",
//                "afterId: ${afterId}",
//                "useAfterId: ${useAfterId}",
//            ).joinToString("\n\n") + "\n-------------\n\n"
//            )
                when(afterId == useAfterId){
                    true -> String() to String()
                    else -> afterId to useAfterId
                }

            }.filter {
                    afterIdToUseAfterId ->
                afterIdToUseAfterId.first.isNotEmpty()
                        && afterIdToUseAfterId.second.isNotEmpty()
            }.firstOrNull()
        }

        private fun getUseAfterKeyFromEl(
            jsImportSubKeyCon: String,
        ): String {
            val useAfterIdWithQuote =
                Regex("\\?${useAfterKey}=[^?|\n]+").find(
                    jsImportSubKeyCon
                )?.value?.removePrefix(
                    "?${useAfterKey}="
                )
            return QuoteTool.trimBothEdgeQuote(useAfterIdWithQuote)
        }

        private fun getAfterIdFromJsEl(
            firstJsElement: String,
        ): String? {
            val afterIdWithQuote =
                Regex("\\?${afterKey}=[^?|\n]+").find(
                    firstJsElement
                )?.value?.removePrefix(
                    "?${afterKey}="
                )
            val afterId = QuoteTool.trimBothEdgeQuote(afterIdWithQuote)
            if(
                afterId.isNotEmpty()
            ) return afterId
            val useAfterValue = getUseAfterKeyFromEl(firstJsElement)
            val useAfterSrcToDestiPair = makeUseAfterPair(
                useAfterValue
            )
            return useAfterSrcToDestiPair?.second
        }

        private fun isMissAfterKeyErr(
            afterKeyConRegex: Regex,
            importConWithFormatList: List<String>
        ): Boolean {
            val tsvImportKey = JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
            val jsImportKey = JsActionKeyManager.JsActionsKey.JS_IMPORT.key
            val useAfterKeyConRegex = Regex(
                "\\?${JsActionKeyManager.ActionImportManager.ActionImportKey.USE_AFTER.key}=[^?|\n]+"
            )
            val importConWithFormatListForJudge =
                importConWithFormatList.joinToString("\n")
            val afterNum = afterKeyConRegex.findAll(
                importConWithFormatListForJudge
            ).count()
            val useAfterNum = useAfterKeyConRegex.findAll(
                importConWithFormatListForJudge
            ).count()
            val totalAfterNum = afterNum + useAfterNum
            val jsKeyConList = importConWithFormatList.filter {
                val trimLine = it.trim()
                val isNotEscapeKey =
                    !trimLine.startsWith("|${tsvImportKey}=")
                            && !trimLine.startsWith("|${jsImportKey}=")
                isNotEscapeKey && it.isNotEmpty()
            }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAC_isMissAfterKeyErr.txt").absolutePath,
//            listOf(
//                "importConWithFormatListForJudge: ${importConWithFormatListForJudge}",
//                "useAfterNum: ${useAfterNum}",
//                "afterNum: ${afterNum}",
//                "size: ${jsKeyConList.size}",
//            ).joinToString("\n\n") + "\n----------\n"
//        )
            return totalAfterNum != jsKeyConList.size
        }

        private fun makeUseAfterPair(
            useAfterValue: String?
        ): Pair<String, String>? {
            val useAfterSeparator =
                JsActionKeyManager.ActionImportManager.useAfterAllow
            return useAfterValue
                ?.split(useAfterSeparator)
                ?.map {
                    it.trim()
                }?.let {
                        keyValueList ->
                    val key = keyValueList.firstOrNull()
                        ?: return@let null
                    val value = keyValueList.getOrNull(1)
                        ?: key
                    key to value
                }
        }
    }

    private fun updateImportConByWhenCondition(
        updatedImportConWithFormatList: List<String>,
        whenCondition: String?,
        useAfterKeyValueSrc: String? = null
    ): List<String> {
        if(
            whenCondition.isNullOrEmpty()
        ) return updatedImportConWithFormatList
        val useAfterKeyValue =
            useAfterKeyValueSrc ?: String()
        return listOf(
            listOf(
                "|${JsActionKeyManager.JsActionsKey.JS.key}=",
                "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
                "?${JsActionKeyManager.JsSubKey.IF_BRACKET_START.key}=",
                useAfterKeyValue,
                "?${JsActionKeyManager.JsSubKey.IF.key}=`${whenCondition}`"
            ).joinToString(String())
        ) + updatedImportConWithFormatList +
                listOf(
                    listOf(
                        "|${JsActionKeyManager.JsActionsKey.JS.key}=",
                        "?${JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
                        "?${JsActionKeyManager.JsSubKey.IF_BRACKET_END.key}=",
                        useAfterKeyValue,
                    ).joinToString(String())
                )
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
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_mapConSrc.txt").absolutePath,
//                listOf(
//                    "mainJsKeyName: ${mainJsKeyName}",
//                    "mapConSrc: ${mapConSrc}"
//                ).joinToString("\n\n") + "\n-----\n"
//            )
            when (mainKey) {
                JsActionKeyManager.JsActionsKey.ACTION_IMPORT,
                -> String() to emptyMap()
                JsActionKeyManager.JsActionsKey.JS,
                -> convertToJsMapOnlyIfBracketOrErrSignal(mapConSrc)
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
        val hasActionImportPathNotFound = subKeyMap.get(
            JsActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key
        )?.contains(JsActionKeyManager.PathExistChecker.notFoundCodePrefix) == true
        if(
            hasIfBracket
            || hasActionImportPathNotFound
        ) return jsMainKeyName to subKeyMap
        return jsMainKeyName to subKeyMap + mapOf(
            JsActionKeyManager.JsSubKey.FORBIDDEN_JS_KEY_DIRECT_SPECIFY.key
                    to String()
        )
    }
    private fun convertToTsvImportMap(
        mapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val importPathKey = JsActionKeyManager.CommonPathKey.IMPORT_PATH.key
        val tsvImportMapSrc = ImportMapMaker.comp(
            "${importPathKey}=${mapConSrc}",
            "${importPathKey}="
        )
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
        return JsActionKeyManager.JsActionsKey.TSV_IMPORT.key to tsvImportMap
//        val commonKeysRegex =
//            JsActionKeyManager.CommonPathKey.values().map {
//                it.key
//            }.joinToString("|")
//        val regexStr = "[?|&](${commonKeysRegex})="
//        val compMapConSrc = compSrcMapCon(
//            mapConSrc,
//            regexStr,
//        )
//        val importPathKey = JsActionKeyManager.CommonPathKey.IMPORT_PATH.key
//        val tsvImportKey =
//            JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
//        val tsvImportMapCon = listOf(
//            importPathKey,
//            compMapConSrc
//        ).joinToString("=")
//
////        FileSystems.updateFile(
////            File(UsePath.cmdclickDefaultAppDirPath, "tsvImpotJsac.txt").absolutePath,
////            listOf(
////                "mapConSrc: ${mapConSrc}",
////                "compMapConSrc: ${compMapConSrc}",
////                "tsvImportMap: ${CmdClickMap.createMap(
////                    tsvImportMapCon,
////                    jsSubKeySeparator
////                ).toMap()}",
////            ).joinToString("\n\n") + "\n---\n"
////        )
//        val tsvImportMap = CmdClickMap.createMap(
//            tsvImportMapCon,
//            jsSubKeySeparator
//        ).toMap()
////        val pathCon = tsvImportMapSrc.get(
////            pathKey,
////        ).let {
////            JsActionKeyManager.PathExistChecker.makeCodeOrPath(it)
////        }
////        val tsvImportMap = tsvImportMapSrc.map {
////            val key = it.key
////            if(
////                key != pathKey
////            ) return@map key to it.value
////            key to pathCon
////        }.toMap()
//        return tsvImportKey to tsvImportMap
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
    private val escapeRunPrefix = JsActionKeyManager.JsVarManager.escapeRunPrefix
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
        val updatedBeforeVarName = updateBeforeVarName(
            varNameSrc,
            beforeVarName,
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
        ) ?: return (String() to mapOf<String, String>()) to updatedBeforeVarName
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
        return (jsMainKeyName to jsKeyConMap) to updatedBeforeVarName
    }

    private fun updateBeforeVarName(
        varNameSrc: String,
        beforeVarName: String,
    ): String {
        if(
            varNameSrc.startsWith(escapeRunPrefix)
        ) return beforeVarName
        if(
            varNameSrc == prevPronoun
        ) return beforeVarName
        return  varNameSrc
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
            funcConWithVar,
            afterJsCon,
            afterFuncCon,
//            makeDeleteVarCon(jsMap),
        ).filter {
            it.trim().isNotEmpty()
        }.joinToString("\n")
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
//    private fun makeDeleteVarCon(
//        jsMap: Map<String, String>
//    ): String {
//        val deleteVarName = jsMap.get(JsActionKeyManager.JsSubKey.DELETE_VAR.key)
//        if(
//            deleteVarName.isNullOrEmpty()
//        ) return String()
//        return "${deleteVarName} = null;"
//    }

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
//                val method =
//                    JsActionKeyManager.MethodManager.makeMethod(jsMap)
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
//                    listOf("})${method};"),
                ).flatten().joinToString("\n")
            }
            JsActionKeyManager.JsFuncManager.EnableLoopMethodType.MAP_METHOD_TYPE -> {
//                val method =
//                    JsActionKeyManager.MethodManager.makeMethod(jsMap)
                listOf(
                    listOf("${functionName}(function(${elementArgName}, ${indexArgName}){"),
                    listOf(
                        "var ${elementValName} = ${elementArgName};",
                        "%s;",
                        "return ${elementValName};"
                    ).map { "\t${it}" },
                    listOf("});"),
//                    listOf("})${method};")
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
//        val method =
//            JsActionKeyManager.MethodManager.makeMethod(jsMap)
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
        return "${funcConSrc};"
//        "${funcConSrc}${method};"
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
