package com.puutaro.commandclick.proccess.js_macro_libs.common_libs


import TsvImportManager
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.LogTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
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
        val jsRepValHolderMap = makeRepValHolderMap(
            keyToSubMapTypeMap
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
                macroDataMap
            )
            return macroDataMap
        }
        val jsActionMap = extractJsDataMap(
            fragment,
            readSharePreferenceMap,
            setReplaceVariableMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
            jsRepValHolderMap,
        )
        LogTool.jsActionLog(
            keyToSubKeyCon,
            jsActionMap
        )
        return jsActionMap
    }

    private fun makeRepValHolderMap(
        keyToSubConTypeMap:  Map<KeyToSubConType, List<Pair<String, Map<String, String>>>?>
    ): Map<String, String>? {
        return keyToSubConTypeMap.get(
            KeyToSubConType.WITH_REPLACE
        )?.map {
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
    ): Map<String, String>? {
        val jsCon = convertJsCon(
            fragment,
            readSharePreferenceMap,
            setReplaceVariableMap,
            jsRepValHolderMap,
            keyToSubKeyMapListWithoutAfterSubKey,
            keyToSubKeyMapListWithAfterSubKey,
        )
        if(
            jsCon.isNullOrEmpty()
        ) return null
        return makeJsDataMap(
            jsCon,
        )
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
//        if(
//            mainKeyName == jsPathKeyName
//        ) return true
//        return false
//        if(
//            mainKeyName != jsKeyName
//        ) return false
////        val subKeyMap = mainKeyNameToSubKeyCon.second
////        val funcName = subKeyMap.get(funcSubKeyName)?.trim()
//        return false
//        !JsActionKeyManager.JsFuncManager.howPipAbleFunc(funcName)
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
    ): String? {
        if(
            keyToSubKeyMapListWithoutAfterSubKey.isNullOrEmpty()
        ) return null
        val tsvImportCon = ImportConMaker.make(
            JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
                    to TsvImportManager.tsvImportPreWord,
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
            CmdClickMap.replaceHolder(
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
        return JavaScriptLoadUrl.makeRawJsConFromContents(
            fragment,
            readSharePreferenceMap,
            jsConBeforeJsImport,
            setReplaceVariableMap,
        )
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
    val jsActionsKeyPlusList =
        listOf(
            JsActionKeyManager.JsSubKey.ARGS.key,
            JsActionKeyManager.JsSubKey.VAR_VALUE.key,
        ) +
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
        val jsActionsPathSeparator = '?'
        val subKeyCon = keyToSubKeyMap.second
        return QuoteTool.splitBySurroundedIgnore(
            subKeyCon,
            jsActionsPathSeparator
        )
            .map {
            val importPath = QuoteTool.trimBothEdgeQuote(it)
            QuoteTool.replaceBySurroundedIgnore(
                SettingFile.read(
                    importPath,
                    File(currentAppDirPath, currentFannelName).absolutePath,
                    setReplaceVariableMap,
                ),
                ',',
                "\n"
            )
        }.joinToString("\n").let {
            ListSettingVariableListMaker.execRemoveMultipleNewLinesAndReplace(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
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
    private val jsConMainKeyName = JsActionKeyManager.JsActionsKey.JS_CON.key
    private val jsPathKeyName = JsActionKeyManager.JsActionsKey.JS_PATH.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key
    private val varSubKeyName = JsActionKeyManager.JsSubKey.VAR.key
    private val varValueSubKeyName = JsActionKeyManager.JsSubKey.VAR_VALUE.key
    private val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
    private val jsSubKeySeparator = '?'
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
                -> toJsFuncForJsVar(
                    index,
                    keyToSubKeyConListByValidKey,
                    QuoteTool.trimBothEdgeQuote(mapConSrc),
                )

                JsActionKeyManager.JsActionsKey.JS_CON
                -> toJsFuncForJsCon(
                    mapConSrc
                )

                JsActionKeyManager.JsActionsKey.JS_PATH
                -> convertJsPathToJsFunc(
                    QuoteTool.trimBothEdgeQuote(mapConSrc),
                    index,
                    keyToSubKeyConListByValidKey,
                )

                JsActionKeyManager.JsActionsKey.TSV_IMPORT,
                JsActionKeyManager.JsActionsKey.JS_IMPORT ->
                    convertToImportMap(
                        mainKey,
                        QuoteTool.trimBothEdgeQuote(mapConSrc),
                    )
            }
        }
    }

    private fun convertToImportMap(
        jsActionMainKey: JsActionKeyManager.JsActionsKey,
        importListCon: String,
    ): Pair<String, Map<String, String>> {
        val importMsp = mapOf(
            JsActionKeyManager.CommonPathKey.PATH.key to importListCon
        )
        return jsActionMainKey.key to importMsp
    }

    private fun convertJsPathToJsFunc(
        jsPathCon: String,
        index: Int,
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>,
    ): Pair<String, Map<String, String>> {

        val isJsInterface = JsActionKeyManager.JsPathManager.isJsInterface(
            jsPathCon
        )
        val isMacro = macroValueList.contains(jsPathCon)
        return when (true) {
            isMacro,
            isJsInterface
            -> macroOrJsInterToJsFuncForJsPath(
                index,
                keyToSubKeyConListWithoutAfterSubKey,
                jsPathCon,
            )

            else -> toJsFuncForPath(
                index,
                keyToSubKeyConListWithoutAfterSubKey,
                jsPathCon,
            )
        }
    }

    private fun macroOrJsInterToJsFuncForJsPath(
        index: Int,
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>,
        jsPathCon: String,
    ): Pair<String, Map<String, String>> {
        val args = makeArgsForJsPathMacro(
            keyToSubKeyConListWithoutAfterSubKey,
            index,
        )
        val jsKeyCon = mapOf(
            funcSubKeyName to jsPathCon,
            argsSubKeyName to args,
        )
        return jsMainKeyName to jsKeyCon
    }

    private fun toJsFuncForPath(
        index: Int,
        keyToSubKeyCon: List<Pair<String, String>>?,
        jsPathCon: String,
    ): Pair<String, Map<String, String>> {
        if (
            keyToSubKeyCon.isNullOrEmpty()
        ) return String() to emptyMap()
        val repMapPrefix = "repMapCon"
        val argsSrc = makeArgsForJsPathMacro(
            keyToSubKeyCon,
            index,
        ).let {
            if (
                it.isNotEmpty()
            ) return@let it
            "${repMapPrefix}=CMDCLICK_BLANK_ARGS=-"
        }
        val argsCon = listOf(
            "path=${jsPathCon}",
            "${repMapPrefix}=${argsSrc}"
        ).joinToString("&")
        val jsKeyCon = mapOf(
            funcSubKeyName to "jsUrl.loadJsPath",
            JsActionKeyManager.JsSubKey.ARGS.key to argsCon,
            JsActionKeyManager.JsSubKey.DESC.key to "path: ${jsPathCon}",
        )
        return jsMainKeyName to jsKeyCon
    }

    private fun makeArgsForJsPathMacro(
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>,
        index: Int,
    ): String {
        val nextKeyToSubCon = keyToSubKeyConListWithoutAfterSubKey.getOrNull(
            index + 1
        ) ?: return String()
        val nextMainKeyName = nextKeyToSubCon.first
        if (
            nextMainKeyName != argsSubKeyName
        ) return String()
        val jsSubKeySeparator = '?'
        val argsConSrc = nextKeyToSubCon.second

        return QuoteTool.replaceBySurroundedIgnore(
            argsConSrc,
            jsSubKeySeparator,
            "&"
        )
    }

    private fun makeValueForJsVarMacro(
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>,
        index: Int,
    ): String {
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "var_makeValueForJsVarMacro.txt").absolutePath,
//            listOf(
//                "keyToSubKeyConListWithoutAfterSubKey: ${keyToSubKeyConListWithoutAfterSubKey}",
//                "keyToSubKeyConListWithoutAfterSubKey.getOrNull(\n" +
//                        "            index + 1\n" +
//                        "        ): ${keyToSubKeyConListWithoutAfterSubKey.getOrNull(
//                            index + 1
//                        )}"
//            ).joinToString("\n\n\n")
//        )
        val nextKeyToSubCon = keyToSubKeyConListWithoutAfterSubKey.getOrNull(
            index + 1
        ) ?: return String()
        val nextMainKeyName = nextKeyToSubCon.first
        if (
            nextMainKeyName != varValueSubKeyName
        ) return String()
        val varValue = QuoteTool.trimBothEdgeQuote(nextKeyToSubCon.second)
        return varValue
    }

    private fun toJsFuncForJsVar(
        index: Int,
        keyToSubKeyCon: List<Pair<String, String>>?,
        jsVarName: String,
    ): Pair<String, Map<String, String>> {
        if (
            keyToSubKeyCon.isNullOrEmpty()
        ) return String() to emptyMap()
        val varValueStr = makeValueForJsVarMacro(
            keyToSubKeyCon,
            index,
        )
        val jsKeyCon = mapOf(
            varSubKeyName to jsVarName,
            JsActionKeyManager.JsSubKey.VAR_VALUE.key to varValueStr,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "var.txt").absolutePath,
//            listOf(
//                "jsKeyCon: ${jsKeyCon}"
//            ).joinToString("\n\n\n")
//        )
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

    fun execPut(
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
        ).map {
            val importPath = QuoteTool.trimBothEdgeQuote(it)
            listOf(
                importPreWord,
                importPath
            ).joinToString(" ")
        }.joinToString("\n")
    }
}

private object JsConPutter {

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
        val afterJsCon = AfterJsConMaker.make(jsMap)
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
            JsActionKeyManager.ArgsManager.makeVarArgs(jsMap)

        val funcCon = makeFuncCon(
            jsMap,
            varargsStr,
        )
        val funcConWithVar = makeFuncConWithVarOrReturn(
            jsMap,
            funcCon,
        )
        val afterJsCon = AfterJsConMaker.make(jsMap)
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
                    listOf("});"),
                ).flatten().joinToString("\n")
            }
            isMapMethod -> {
                listOf(
                    listOf("${functionName}(function(${elementArgName}, ${indexArgName}){"),
                    listOf(
                        "var ${elementValName} = ${elementArgName};",
                        "%s;",
                        "return ${elementValName};"
                    ).map { "\t${it}" },
                    listOf("});")
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
            jsMap.get(JsActionKeyManager.JsSubKey.METHOD.key)
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
        val isJsCon = JsActionKeyManager.JsFuncManager.isJsCon(functionName)
        val isMethod = !method.isNullOrEmpty()
        return when(true){
            isJsCon -> "${makeJsCon(functionName)};"
            isMethod -> "${funcConSrc}.${method};"
            else -> "${funcConSrc};"
        }
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
        val prefix =
            jsMap.get(JsActionKeyManager.JsSubKey.PREFIX.key)
                ?: String()
        return "${prefix}${functionName}(${varargsStr})"
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
        val funcTemplateSrc = makeFuncTemplate(ifCondition)
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

private object AfterJsConMaker {

    private val ifSentence = "if"
    private val ifAfterSentence = "ifAfter"
    private val afterValSeprator = ":"
    private val ifAfterPrefix = "${ifAfterSentence}${afterValSeprator}"

    fun make(
        jsMap: Map<String, String>,
    ): String {
            val varNameToJsConPairCon = jsMap.get(
                JsActionKeyManager.JsSubKey.AFTER_JS_CON.key
            )
            val varNameJsConPairList = CmdClickMap.createMap(
                varNameToJsConPairCon,
                '&'
            )
            val afterJsConList = varNameJsConPairList.mapIndexed {
                    index, jsConSrc ->
                val last2IndexPairList =
                    takeLast2IndexInThis(
                        varNameJsConPairList,
                        index,
                    )
                execMakeAfterJsCon(
                    jsConSrc,
                    last2IndexPairList,
                )
            }
        return afterJsConList.joinToString("\n")
    }

    private fun takeLast2IndexInThis(
        varNameJsConPairList: List<Pair<String, String>>,
        index: Int,
    ): List<Pair<String, String>> {
        return varNameJsConPairList.filterIndexed {
                innerIndex, el ->
            if(
                innerIndex >= index
            ) return@filterIndexed false
            !QuoteTool.trimBothEdgeQuote(
                el.first
            ).startsWith(ifAfterPrefix)
        }.takeLast(2)
    }

    private fun execMakeAfterJsCon(
        varNameToJsCon: Pair<String, String>,
        last2IndexPairList: List<Pair<String, String>?>
    ): String {
        val varName = varNameToJsCon.first.trim()
        val jsCon = varNameToJsCon.second.trim()
        val isIfSentence =
            varName == ifSentence
        val afterAndValCon = QuoteTool.trimBothEdgeQuote(varName)
        val isIfAfter =
            afterAndValCon.startsWith(
                ifAfterPrefix
            )
        return when(true){
            isIfSentence -> String()
            isIfAfter -> execMakeIfAfterJsCon(
                afterAndValCon,
                jsCon,
                last2IndexPairList,
            )
            else -> execMakeAfterJsCon(
                varName,
                jsCon,
                last2IndexPairList.lastOrNull(),
            )
        }
    }

    private fun execMakeIfAfterJsCon(
        afterAndValCon: String,
        jsCon: String,
        last2IndexPairList: List<Pair<String, String>?>
    ): String {
        val varName =
            afterAndValCon.removePrefix(ifAfterPrefix)
        val beforePreIfPair =
            last2IndexPairList.firstOrNull()
        val isBeforeIf =
            beforePreIfPair?.first == ifSentence
                    && last2IndexPairList.getOrNull(1)?.first != ifSentence
        if(
            !isBeforeIf
        ) return String()
        val funcTemplate = makeFuncTemplaceForAfterJsCon(
            beforePreIfPair
        )
        val logJsCon = makeLogConForAfterJsCon(varName)
        val ifAfterJsCon = makeAfterJsCon(
            varName,
            jsCon,
        )
        return makeIfCon(
            funcTemplate,
            logJsCon,
            ifAfterJsCon,
        )
    }

    private fun execMakeAfterJsCon(
        varName: String,
        jsCon: String,
        preIndexPair: Pair<String, String>?,
    ): String {
        val logJsCon = makeLogConForAfterJsCon(varName)
        val afterJsCon = makeAfterJsCon(
            varName,
            jsCon,
        )
        val funcTemplate = makeFuncTemplaceForAfterJsCon(
            preIndexPair
        )
        return makeIfCon(
            funcTemplate,
            logJsCon,
            afterJsCon,
        )
    }

    fun makeIfCon(
        funcTemplate: String,
        logJsCon: String,
        afterJsCon: String,
    ): String {
        val isIf = funcTemplate.contains(ifSentence)
        val afterJsConWithLog = listOf(
            logJsCon,
            afterJsCon
        ).joinToString("\n").let{
            when(isIf){
                true -> it.replace("\n", "\n\t")
                else -> it
            }
        }
        return funcTemplate.format(
            afterJsConWithLog
        )
    }

    private fun makeAfterJsCon(
        varName: String,
        jsCon: String,
    ): String {
        val disableVar = varName
            .contains(" ")
                || varName.contains("\"")
                || varName.contains("\'")
                || varName.contains("`")
                || varName.isEmpty()
        val varNameCon = when(disableVar) {
            true -> String()
            else -> listOf(
                "var",
                varName,
                "="
            ).joinToString(" ")
        }
        return listOf(
            varNameCon,
            UsePath.compExtend(
                jsCon,
                ";"
            ),
        ).joinToString(" ").trim()
    }

    private fun makeFuncTemplaceForAfterJsCon(
        preIndexPair: Pair<String, String>?
    ): String {
        val preIndexVarName = preIndexPair?.first
        val isIf =
            preIndexVarName == ifSentence
        val ifCondition = when(isIf){
            true -> QuoteTool.trimBothEdgeQuote(preIndexPair?.second)
            else -> String()
        }
        return makeFuncTemplate(
            ifCondition
        )
    }

    private fun makeLogConForAfterJsCon(
        varName: String,
    ): String {
        val logVarName = QuoteTool.trimBothEdgeQuote(
            varName
        )
        return listOf(
            "//_/_/_/ ${logVarName} start",
            "jsFileSystem.stdLog(\"${logVarName}\");",
        ).joinToString("\n")
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


private fun makeFuncTemplate(
    ifCondition: String?
): String {
    return when (
        ifCondition.isNullOrEmpty()
    ) {
        false -> """
            |if(${ifCondition}){ 
            |    %s 
            |}   
            |""".trimMargin()

        else -> "%s"
    }
}