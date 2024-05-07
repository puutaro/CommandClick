package com.puutaro.commandclick.proccess.js_macro_libs.common_libs


import TsvImportManager
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
    private val replaceMainKeyName = JsActionKeyManager.JsActionsKey.REPLACE.key
    private val overrideMainKeyName = JsActionKeyManager.JsActionsKey.OVERRIDE.key
    private val jsPathKeyName = JsActionKeyManager.JsActionsKey.JS_PATH.key
    private val jsConKeyName = JsActionKeyManager.JsActionsKey.JS_CON.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key
    private val afterSubKeyName = JsActionKeyManager.JsSubKey.AFTER.key


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
        val keyToSubMapTypeMap = createKeyToSubMapTypeMap(
            readSharePreferenceMap,
            keyToSubKeyCon,
            setReplaceVariableMap,
        ) ?: return null
        val keyToSubKeyMapListWithReplace = keyToSubMapTypeMap.get(
            KeyToSubConType.WITH_REPLACE
        )
        val jsRepValHolderMap = makeRepValHolderMap(
            keyToSubKeyMapListWithReplace
        )

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
        if(
            !macroDataMap.isNullOrEmpty()
        ) {
            LogTool.jsActionLog(
                keyToSubKeyCon,
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
            jsRepValHolderMap,
        )
        val jsActionMap = jsActionMapToJsConOnlyReplace?.first
        LogTool.jsActionLog(
            keyToSubKeyCon,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
            jsActionMapToJsConOnlyReplace
        )
        val context = fragment.context
        val checkJsCon = jsActionMap?.get(JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key)
        val isSyntaxErr = LogTool.SyntaxCheck.checkJsAcSyntax(
            context,
            checkJsCon
        )
        if(isSyntaxErr){
            return mapOf(
                JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
                        to JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON.key,
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key to String()
            )
        }
        val isVarNotUseErr = LogTool.VarNotUse.checkJsAsSyntaxForVarNotUse(
            context,
            checkJsCon
//            keyToSubKeyMapListWithReplace,
//            keyToSubKeyMapListWithoutAfterSubKey,
//            keyToSubKeyMapListWithAfterSubKey,
        )
        if(isVarNotUseErr){
            return mapOf(
                JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
                        to JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON.key,
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key to String()
            )
        }
        return jsActionMap
    }

    private fun makeRepValHolderMap(
        keyToSubKeyMapListWithReplace:   List<Pair<String, Map<String, String>>>?
    ): Map<String, String>? {
        return keyToSubKeyMapListWithReplace?.map {
            it.second.map{
                    repValMapSrc ->
                repValMapSrc.key to repValMapSrc.value
            }
        }?.flatten()?.toMap()
    }

    private fun extractJsDataMap(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
        jsRepValHolderMap: Map<String, String>?,
    ): Pair<Map<String, String>, String?>? {
        val jsConToJsConOnlyReplace = convertJsCon(
            fragment,
            readSharePreferenceMap,
            setReplaceVariableMap,
            jsRepValHolderMap,
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
    ): Map<KeyToSubConType, List<Pair<String, Map<String, String>>>?>? {
        val keyToSubKeyMapList = KeyToSubKeyMapListMaker.make(
            keyToSubKeyCon,
            readSharePreferenceMap,
            setReplaceVariableMap,
        )
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

        val replaceKeyToSubKeyMapList = filterByMainKey(
            keyToSubKeyMapList,
            replaceMainKeyName
        )
        val keyToSubKeyMapListWithoutReplaceKey =  keyToSubKeyMapListByOverride.filter {
            !replaceKeyToSubKeyMapList.contains(it)
        }
        val afterKeyToSubKeyMapList =
            filterByAfterJsSubKey(
                keyToSubKeyMapListWithoutReplaceKey,
            )

        val keyToSubKeyMapListWithoutAfterSubKey = keyToSubKeyMapListWithoutReplaceKey.filter {
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
            KeyToSubConType.WITH_REPLACE
                    to null,
            KeyToSubConType.WITH_AFTER
                    to null,
            KeyToSubConType.WITH_OTHER
                    to listOf(keyToSubKeyMapListForMacro),
        )
        return mapOf(
            KeyToSubConType.WITH_REPLACE
                    to replaceKeyToSubKeyMapList,
            KeyToSubConType.WITH_AFTER
                    to afterKeyToSubKeyMapList,
            KeyToSubConType.WITH_OTHER
                    to makeKeyToSubKeyConListUntilFirstUnPipAble(
                keyToSubKeyMapListWithoutAfterSubKey
            ),
        )
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
        jsRepValHolderMap: Map<String, String>?,
        keyToSubKeyMapListWithoutAfterSubKey: List<Pair<String, Map<String, String>>>?,
        keyToSubKeyMapListWithAfterSubKey: List<Pair<String, Map<String, String>>>?,
    ): Pair<String?, String?> {
        if(
            keyToSubKeyMapListWithoutAfterSubKey.isNullOrEmpty()
        ) return null to null
        val tsvImportCon = TsvImportConMaker.make(
            keyToSubKeyMapListWithoutAfterSubKey
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
        ).joinToString("\n").let {
            CmdClickMap.replaceHolderForJsAction(
                it,
                jsRepValHolderMap,
            )
        }

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
        WITH_REPLACE,
        WITH_AFTER,
        WITH_OTHER,
    }
}


private object KeyToSubKeyMapListMaker {

    private const val keySeparator = '|'
    private const val jsActionEndComma = ','
    val jsActionsKeyPlusList =
//        listOf(
//            JsActionKeyManager.JsSubKey.ARGS.key,
//            JsActionKeyManager.JsSubKey.VAR_VALUE.key,
//            JsActionKeyManager.JsSubKey.IF.key,
//        ) +
                JsActionKeyManager.JsActionsKey.values().map {
                    it.key
                }

    fun make(
        keyToSubKeyCon: String?,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): List<Pair<String, Map<String, String>>> {
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
        val jsActionsKeyName = JsActionKeyManager.JsActionsKey.ACTION_IMPORT.key
        val jsActionImportSignal = "${jsActionsKeyName}="
        var keyToSubKeyConList = keyToSubKeyConListSrc
        for( i in 1..importRoopLimit) {
            keyToSubKeyConList = keyToSubKeyConList.map {
                    keyToSubKeyPair ->
                val mainKeyName = keyToSubKeyPair.first
                if(
                    mainKeyName != jsActionsKeyName
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
        )
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
        keyToSubKeyMap: Pair<String, String>,
    ): String {
        val subKeyCon = keyToSubKeyMap.second
        val importPath = QuoteTool.trimBothEdgeQuote(subKeyCon)
        val importConSrc = SettingFile.read(
            importPath,
            File(currentAppDirPath, currentFannelName).absolutePath,
            setReplaceVariableMap,
        )
        return QuoteTool.splitBySurroundedIgnore(
            importConSrc,
            jsActionEndComma,
        ).firstOrNull()?.let {
            val importConSrcWithMark =
                JsActionKeyManager.ActionImportManager.putActionImportSubKey(it)
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, "jsImport.txt").absolutePath,
                listOf(
                    "importConSrc: ${importConSrc}",
                    "importConSrcWithMark: ${importConSrcWithMark}",
                ).joinToString("\n")
            )
            ListSettingVariableListMaker.execRemoveMultipleNewLinesAndReplace(
                importConSrcWithMark,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
        } ?: String()
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
    private val jsPathMainKeyName = JsActionKeyManager.JsActionsKey.JS_PATH.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key
    private val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
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
            when (mainKey) {
                JsActionKeyManager.JsActionsKey.ACTION_IMPORT,
                -> String() to emptyMap()

                JsActionKeyManager.JsActionsKey.JS,
                JsActionKeyManager.JsActionsKey.OVERRIDE,
                JsActionKeyManager.JsActionsKey.REPLACE,
                -> mainKey.key to CmdClickMap.createMap(
                    QuoteTool.trimBothEdgeQuote(mapConSrc),
                    jsSubKeySeparator
                ).toMap()

                JsActionKeyManager.JsActionsKey.JS_VAR
                -> VarShortSyntaxToJsFunc.toJsFunc(
                    mapConSrc
                )

                JsActionKeyManager.JsActionsKey.JS_CON
                -> String() to emptyMap()
//                    toJsFuncForJsCon(
//                    mapConSrc
//                )

                JsActionKeyManager.JsActionsKey.JS_PATH
                -> convertJsPathToJsFunc(
                    mapConSrc,
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
        val tsvImportKey =
            JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
        val tsvImportMapCon = listOf(
            JsActionKeyManager.CommonPathKey.PATH.key,
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
        return tsvImportKey to CmdClickMap.createMap(
            tsvImportMapCon,
            jsSubKeySeparator
        ).toMap()
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
            JsActionKeyManager.CommonPathKey.PATH.key to importListCon
        )
        return jsActionMainKey.key to importMap
    }

    private fun convertJsPathToJsFunc(
        jsPathMapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val jsPathConPairListSrcBeforeFilter = CmdClickMap.createMap(
            "${jsPathMainKeyName}=${jsPathMapConSrc}",
            '?'
        )
        val jsPathConPairListSrc = JsActionKeyManager.OnlySubKeyMapForShortSyntax.filterForFunc(
            jsPathConPairListSrcBeforeFilter
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAcFunc.txt").absolutePath,
//            listOf(
//                "jsPathMapConSrc: ${jsPathMapConSrc}",
//                "jsPathConPairListSrcBeforeFilter: ${jsPathConPairListSrcBeforeFilter}",
//                "jsPathConPairListSrc: ${jsPathConPairListSrc}",
//            ).joinToString("\n\n")
//        )
        val onlySubKeyMapToJsPathConPairList =
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.extractForFunc(
                jsPathConPairListSrc
            )
        val onlySubKeyMapSrc = onlySubKeyMapToJsPathConPairList.first
        val actionImportVirtualSubKey = JsActionKeyManager.actionImportVirtualSubKey
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
            onlySubKeyMapToJsPathConPairList.second
                ?: emptyList()
        val jsPathCon = CmdClickMap.getFirst(
            jsPathConPairList,
            jsPathMainKeyName
        ) ?: return String() to mapOf()
        val argsMapCon = CmdClickMap.getFirst(
            jsPathConPairList,
            argsSubKeyName
        ) ?: String()
        val jsPathStr = QuoteTool.trimBothEdgeQuote(jsPathCon)
        val isJsPathCon = File(jsPathStr).isFile
        return when (true) {
            isJsPathCon -> toJsFuncForPath(
                jsPathStr,
                argsMapCon,
                onlySubKeyMap
            )
            else -> macroOrJsInterToJsFuncForJsPath(
                jsPathStr,
                argsMapCon,
                jsActionImportMarkMap,
                onlySubKeyMap
            )
        }
    }

    private fun macroOrJsInterToJsFuncForJsPath(
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
        )
        return jsMainKeyName to jsKeyCon
    }


    private fun toJsFuncForJsCon(
        jsCon: String,
    ): Pair<String, Map<String, String>> {
        val jsConPrefix = JsActionKeyManager.JsConManager.Flag.JS_CON_PREFIX.flag
        val jsConWithPrefix = "${jsConPrefix}${jsCon}"
        val jsMap = mapOf(
            funcSubKeyName to jsConWithPrefix,
        )
        return jsMainKeyName to jsMap
    }
}

private object VarShortSyntaxToJsFunc {

    private val jsVarMainKeyName = JsActionKeyManager.JsActionsKey.JS_VAR.key
    private val varSubKeyName = JsActionKeyManager.JsSubKey.VAR.key
    private val jsMainKeyName = JsActionKeyManager.JsActionsKey.JS.key
    private val varValueSubKeyName = JsActionKeyManager.JsSubKey.VAR_VALUE.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key
    private val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
    private const val varReturnSubKeyName = "varReturn"
    private const val exitSubKeyName = "exit"
    private val suggerIf = JsActionKeyManager.JsSubKey.IF.key
    private const val jsSubKeySeparator = '?'
    fun toJsFunc(
        varMapConSrc: String,
    ): Pair<String, Map<String, String>> {
        val varMapConPairListSrcBeforeFilter = CmdClickMap.createMap(
            "${jsVarMainKeyName}=${varMapConSrc}",
            jsSubKeySeparator
        )
        val varMapConPairListSrc = JsActionKeyManager.OnlySubKeyMapForShortSyntax.filterForVar(
            varMapConPairListSrcBeforeFilter
        )
        val onlySubKeyMapToVarMapConPairList =
            JsActionKeyManager.OnlySubKeyMapForShortSyntax.extractForVar(
                varMapConPairListSrc
            )
        val onlySubKeyMapSrc = onlySubKeyMapToVarMapConPairList.first
        val varMapConPairListBeforeExcludeFirstIf = onlySubKeyMapToVarMapConPairList.second
            ?: emptyList()
        val onlyIfSubKeyMapToVarMapConPairListExcludeIfSubKey = extractFirstInExcludePairList(
            varMapConPairListBeforeExcludeFirstIf
        )
        val onlyIfMap =
            onlyIfSubKeyMapToVarMapConPairListExcludeIfSubKey.first
        val varMapConPairList =
            onlyIfSubKeyMapToVarMapConPairListExcludeIfSubKey.second
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc.txt").absolutePath,
//            listOf(
//                "varMapConSrc: ${varMapConSrc}",
//                "varMapConPairListSrcBeforeFilter: ${varMapConPairListSrcBeforeFilter}",
//                "varMapConPairListSrc: ${varMapConPairListSrc}",
//                "varMapConPairListBeforeExcludeFirstIf: ${varMapConPairListBeforeExcludeFirstIf}",
//                "onlyIfMap: ${onlyIfMap}",
//                "onlySubKeyMapToVarMapConPairList: ${onlySubKeyMapToVarMapConPairList}",
//                "onlySubKeyMap: ${onlySubKeyMap}",
//                "varMapConPairList: ${varMapConPairList}"
//            ).joinToString("\n\n")
//        )
        val jsVarName = CmdClickMap.getFirst(
            varMapConPairList,
            jsVarMainKeyName
        ) ?: return String() to mapOf()
        val nextIndex = 1
        val valueOrIfConList = makeVarKeyToConPairListForJsVarMacro(
            varMapConPairList,
            nextIndex
        )
        val valueOrFuncMapToSeedIndex = extractFirstValueOrFuncMap(
            valueOrIfConList,
        )
        val valueOrFuncMap = valueOrFuncMapToSeedIndex.first
        val seedIndex = valueOrFuncMapToSeedIndex.second
        val nextNextIndex =
            nextIndex + seedIndex

        val nextVarKeyToConPairList = makeVarKeyToConPairListForJsVarMacro(
            varMapConPairList,
            nextNextIndex
        )
        val actionImportVirtualSubKey = JsActionKeyManager.actionImportVirtualSubKey
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
        val jsKeyConMapSrc = jsActionImportMarkMap + onlyIfMap + mapOf(
            varSubKeyName to jsVarName,
        ) + valueOrFuncMap +
                onlySubKeyMap +
                extractAfterJsConForVar(
                    jsVarName,
                    nextVarKeyToConPairList
                )
        val jsKeyConMap = jsKeyConMapSrc.filterKeys { it.isNotEmpty() }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "var.txt").absolutePath,
//            listOf(
//                "varMapConPairList: ${varMapConPairList}",
//                "valueOrIfConList: ${valueOrIfConList}",
//                "nextValueOrIfConList: ${nextValueOrIfConList}",
//                "firstValueStr: ${firstValueStr}",
//                "jsVarName: ${jsVarName}",
//                "firstValueStr: ${firstValueStr}",
//                "afterJsCon: ${makeAfterJsConForVar(
//                    jsVarName,
//                    nextValueOrIfConList
//                )}",
//                "jsKeyCon: ${jsKeyCon}"
//            ).joinToString("\n\n\n")
//        )
        return jsMainKeyName to jsKeyConMap
    }

    private fun extractFirstInExcludePairList(
        varMapConPairListSrc: List<Pair<String, String>>
    ): Pair<
            Map<String, String>,
            List<Pair<String, String>>
            >{
        val untilSubKeyList = listOf(
            varValueSubKeyName,
            funcSubKeyName,
        )
        val untilSubKeyIndex = varMapConPairListSrc.indexOfFirst {
            val subKeyName = it.first
            untilSubKeyList.contains(subKeyName)
        }
        val ifSubKeyContainEntryPairList =
            varMapConPairListSrc.take(untilSubKeyIndex)
        val defaultEmptyMap = emptyMap<String, String>()
        val firstIfPair = ifSubKeyContainEntryPairList.firstOrNull {
            val subKeyName = it.first
            subKeyName == suggerIf
        } ?: return defaultEmptyMap to varMapConPairListSrc
        val firstIfCondition = firstIfPair.second
        if(
            firstIfCondition.isEmpty()
        ) return defaultEmptyMap to varMapConPairListSrc
        var firstMatch = true
        val varMapConPairListExcludeFirstIf = varMapConPairListSrc.filter {
            if(
                firstIfPair == it
                && firstMatch
            ) {
                firstMatch = false
                return@filter false
            }
            true
        }
        return mapOf(
            firstIfPair
        ) to varMapConPairListExcludeFirstIf
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
        val extractSubKeyList = listOf(
            varValueSubKeyName,
            funcSubKeyName,
            varReturnSubKeyName,
            exitSubKeyName,
        )
        val afterJsCon = nextValueOrFuncOrIfConList.mapIndexed {
                index, keyToCon ->
            val subKeyName = keyToCon.first
            val isNotTargetSubKey = !extractSubKeyList.contains(subKeyName)
            if(
                isNotTargetSubKey
            ) return@mapIndexed String()
            return@mapIndexed when(subKeyName) {
                varValueSubKeyName -> makeVarSentence(
                    varName,
                    keyToCon,
                    nextValueOrFuncOrIfConList,
                    index,
                )
                funcSubKeyName -> makeFuncSentence(
                    varName,
                    keyToCon,
                    nextValueOrFuncOrIfConList,
                    index,
                )
                varReturnSubKeyName -> makeVarReturnSentence(
                    keyToCon,
                    nextValueOrFuncOrIfConList,
                    index,
                )
                exitSubKeyName -> makeExitSentence(
                    keyToCon,
                    nextValueOrFuncOrIfConList,
                    index,
                )
                else -> return@mapIndexed String()
            }
        }.filter { it.isNotEmpty() }
            .joinToString(
                jsAfterConSeparator.toString()
            ).replace("\n", "")
        return mapOf(
            JsActionKeyManager.JsSubKey.AFTER_JS_CON.key
                    to afterJsCon
        )
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
        val isNotIfKey = ifEntryKeyToCon.first != suggerIf
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
    ): String {
        val funcConSrc =
            QuoteTool.trimBothEdgeQuote(keyToCon.second)
        if(
            funcConSrc.isEmpty()
        ) return String()
        val argsIndex = index + 1
        val funcSentence = execMakeFuncSentence(
            varName,
            funcConSrc,
            nextValueOrFuncOrIfConList,
            argsIndex,
        )
        return addIfSentence(
            funcSentence,
            nextValueOrFuncOrIfConList,
            index,
        )
    }

    private fun makeVarReturnSentence(
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
    ): String {
            val varReturnValue =
                QuoteTool.trimBothEdgeQuote(keyToCon.second)

            val funcSentence = execMakeReturnSentence(
                varReturnValue
            )
            return addIfSentence(
                funcSentence,
                nextValueOrFuncOrIfConList,
                index,
            )
    }

    private fun execMakeReturnSentence(
        returnValueSrc: String,
    ): String {
        val varReturnSentence = JsActionKeyManager.NoQuoteHandler.makeForVarReturn(returnValueSrc)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "err.txt").absolutePath,
//            listOf(
//                "returnValueSrc: ${returnValueSrc}",
//                "returnValue: ${varReturnSentence}"
//            ).joinToString("\n\n")
//        )
        return listOf(
            "var return",
            "\"${varReturnSentence}\""
        ).joinToString("=")
    }

    private fun makeExitSentence(
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
    ): String {
        val exitMessage =
            QuoteTool.trimBothEdgeQuote(keyToCon.second)
        val funcSentence = execMakeExitSentence(exitMessage)
        return addIfSentence(
            funcSentence,
            nextValueOrFuncOrIfConList,
            index,
        )
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
    ): String {
        val funcSentenceTemplate = listOf(
            varName,
            "`${funcConSrc}(%s)`"
        ).joinToString("=")
        val argsNameToCon = nextValueOrFuncOrIfConList?.getOrNull(argsIndex)
            ?: return funcSentenceTemplate.format(String())
        val argsKeyEntry = argsNameToCon.first
        if(
            argsKeyEntry != argsSubKeyName
        ) return funcSentenceTemplate.format(String())
        val argsCon = argsNameToCon.second
        val argsOnlyJsMap =  mapOf(
            argsSubKeyName to argsCon
        )
        val varargsStr =
            JsActionKeyManager.ArgsManager.makeVarArgs(
                argsOnlyJsMap,
                argsSubKeyName
            )
        return funcSentenceTemplate.format(varargsStr)
    }

    private fun makeVarSentence(
        varName: String,
        keyToCon: Pair<String, String>,
        nextValueOrFuncOrIfConList: List<Pair<String, String>>?,
        index: Int,
    ): String {
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
        return addIfSentence(
            varSentence,
            nextValueOrFuncOrIfConList,
            index,
        )
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
        val importPath = tsvImportMap.get(
            JsActionKeyManager.CommonPathKey.PATH.key
        ) ?: return String()
        val importMainSentence = listOf(
            importPreWord,
            importPath
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
            JsActionKeyManager.CommonPathKey.PATH.key
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
                jsMapListWithAfterSubKey
            )
        }.joinToString("\n")
    }

    private fun execMake(
        jsMap: Map<String, String>?,
        jsMapListOnlyAfter: List<Map<String, String>>
    ): String {
        if(
            jsMap.isNullOrEmpty()
        ) return String()
        val functionName = jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
        if(
            functionName.isNullOrEmpty()
        ) LogSystems.stdSys(
            "func name is null: ${jsMap}"
        )
        val funcTemplate =
            makeFuncTemplate(jsMap)
        val loopMethodTemplate =
            makeLoopMethodTemplate(jsMap)


        val insertFuncCon = when(loopMethodTemplate.isNullOrEmpty()){
            false -> makeInsertLoopMethodCon(
                    jsMap,
                    jsMapListOnlyAfter,
                    loopMethodTemplate,
                )
            else -> makeInsertFuncCon(
                jsMap,
                jsMapListOnlyAfter,
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
    ): String {
        val loopMethodTemplateWithVar = makeFuncConWithVarOrReturn(
            jsMap,
            loopMethodTemplate,
        )
        val afterJsCon = JsActionKeyManager.AfterJsConMaker.make(jsMap)
            .replace("\n", "\n\t")
        val afterFuncCon = putAfterFunc(
            jsMap,
            jsMapListOnlyAfter
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
            makeEndToast(jsMap),
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
        val exitJudgeCon = makeFuncConWithVarExitJudge(
            jsMap,
        )

        val afterFuncCon = putAfterFunc(
            jsMap,
            jsMapListOnlyAfter
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
        jsMapListOnlyAfter: List<Map<String, String>>
    ): String {
        val afterId = jsMap.get(JsActionKeyManager.JsSubKey.ID.key)
        val afterKeyName = JsActionKeyManager.JsSubKey.AFTER.key
        val afterJsMap = jsMapListOnlyAfter.firstOrNull {
            val afterValueList =
                it.get(afterKeyName)
                    ?.split("&")
                    ?.map { it.trim() }
            if(
                afterValueList.isNullOrEmpty()
            ) return@firstOrNull false
            afterValueList.contains(afterId)
        }
        return execMake(
            afterJsMap,
            jsMapListOnlyAfter
        ).split("\n").map {
            "\t${it}"
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

    private fun makeToastMessage(
        message: String,
    ): String {
        return """
            jsToast.short(`${message}`);
        """.trimIndent()
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
    ): String? {
        val jsInterfacePrefix =
            JsActionKeyManager.JsFuncManager.LoopMethodFlag.JS_INTERFACE_PREFIX.flag
        val filterMethodSuffix =
            JsActionKeyManager.JsFuncManager.LoopMethodFlag.FILTER_METHOD_SUFFIX_.flag
        val mapMethodSuffix =
            JsActionKeyManager.JsFuncManager.LoopMethodFlag.MAP_METHOD_SUFFIX_.flag
        val forEachMethodSuffix =
            JsActionKeyManager.JsFuncManager.LoopMethodFlag.FOR_EACH_METHOD_SUFFIX_.flag
        val functionName =
            jsMap.get(JsActionKeyManager.JsSubKey.FUNC.key)
        val isJsInterface =
            functionName?.startsWith(jsInterfacePrefix) == true
        if(isJsInterface) return null
        val isFilterMethod = JsActionKeyManager.JsFuncManager.isLoopMethod(
            functionName,
            filterMethodSuffix
        )
        val isMapMethod = JsActionKeyManager.JsFuncManager.isLoopMethod(
            functionName,
            mapMethodSuffix,
        )
        val isForEachMethod = JsActionKeyManager.JsFuncManager.isLoopMethod(
            functionName,
            forEachMethodSuffix
        )
        val loopArgsNameList = makeVarArgsForLoopMethod(
            jsMap
        )
        val elementValName = getLoopArgName(
            loopArgsNameList,
            0,
            JsActionKeyManager.JsFuncManager.DefaultLoopArgsName.EL.default
        )
        val elementArgName = elementValName + "Src"
        val indexArgName = getLoopArgName(
            loopArgsNameList,
            1,
            JsActionKeyManager.JsFuncManager.DefaultLoopArgsName.INDEX.default
        )
        return when(true) {
            isFilterMethod -> {
                val method =
                    JsActionKeyManager.MethodManager.makeMethod(jsMap)
                val boolValName = getLoopArgName(
                    loopArgsNameList,
                    2,
                    JsActionKeyManager.JsFuncManager.DefaultLoopArgsName.BOOL.default
                )
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
            isMapMethod -> {
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
            isForEachMethod -> {
                listOf(
                    listOf("${functionName}(function(${elementArgName}, ${indexArgName}){"),
                    listOf(
                        "var ${elementValName} = ${elementArgName};",
                        "%s;"
                    ).map { "\t${it}" },
                    listOf("});")
                ).flatten().joinToString("\n")
            }
            else -> null
        }
    }

    private fun getLoopArgName(
        loopArgsNameList: List<String>,
        index: Int,
        defaultArgName: String
    ): String {
        return loopArgsNameList.getOrNull(index).let {
            if(
                it.isNullOrEmpty()
            ) return@let defaultArgName
            it
        }
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
//        val isJsCon = JsActionKeyManager.JsFuncManager.isJsCon(functionName)
//        return when(true){
//            isJsCon -> "${makeJsCon(functionName)};"
//            else -> "${funcConSrc}${method};"
//        }
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

private fun makeJsCon(
    funcName: String?,
): String {
    if(
        funcName.isNullOrEmpty()
    ) return String()
    val jsConPrefix =
        JsActionKeyManager.JsFuncManager.LoopMethodFlag.JS_CON_PREFIX.flag
    val jsConSrc =
        funcName.trim().removePrefix(jsConPrefix)
    return QuoteTool.trimBothEdgeQuote(jsConSrc)
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

private fun trimBothEdgeQuoteForJsAc(
    targetStr: String
): String {
    val trimCon = QuoteTool.trimBothEdgeQuote(targetStr)
    val subKeyRegex = "[`\"']([|?&]as)['`\"]"
    return trimCon.replace(
        subKeyRegex,
        "$1"
    )
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
