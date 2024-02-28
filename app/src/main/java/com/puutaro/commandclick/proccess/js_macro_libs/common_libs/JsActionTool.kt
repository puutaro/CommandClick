package com.puutaro.commandclick.proccess.js_macro_libs.common_libs


import TsvImportManager
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.LogTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.import.JsImportManager
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object JsActionTool {

    private val jsActionShiban =
        CommandClickScriptVariable.jsActionShiban
    private val jsKeyName = JsActionKeyManager.JsActionsKey.JS.key
    private val jsReplaceKeyName = JsActionKeyManager.JsActionsKey.REPLACE.key
    private val jsOverrideKeyName = JsActionKeyManager.JsActionsKey.OVERRIDE.key
    private val jsPathKeyName = JsActionKeyManager.JsActionsKey.JS_PATH.key
    private val jsConKeyName = JsActionKeyManager.JsActionsKey.JS_CON.key
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

    fun makeJsActionMap(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        keyToSubKeyCon: String?,
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>? = null,
    ): Map<String, String>? {
        val keyToSubConTypeMap = createKeyToSubConTypeMap(
            readSharePreferenceMap,
            keyToSubKeyCon,
            setReplaceVariableMap,
        ) ?: return null
        val jsRepValMapBeforeConcat = keyToSubConTypeMap.get(
            KeyToSubConType.WITH_REPLACE
        )?.let {
                extractJsRepValMap(
                    it
                )
            }
        val jsRepValMap = concatRepValMap(
            jsRepValMapBeforeConcat,
            extraRepValMap
        )

        val overrideMapList =
            makeOverrideMapList(
                keyToSubConTypeMap.get(
                    KeyToSubConType.WITH_OVERRIDE
                )
            )
        val keyToSubKeyConListWithAfterSubKey = keyToSubConTypeMap.get(
            KeyToSubConType.WITH_AFTER
        )

        val keyToSubKeyConListWithoutAfterSubKey =
            keyToSubConTypeMap.get(
                KeyToSubConType.WITH_OTHER
            ) ?: emptyList()

        val macroDataMap =
            extractMacroDataMap(keyToSubKeyConListWithoutAfterSubKey)
//        FileSystems.writeFile(
//            File(
//                UsePath.cmdclickDefaultAppDirPath,
//                "js_makeJsActionMap.txt").absolutePath,
//            listOf(
//                "keyToSubKeyCon: ${keyToSubKeyCon}",
//                "jsRepValMap: ${jsRepValMap}",
//                "overrideMapList: ${overrideMapList}",
//                "keyToSubKeyConListWithAfterSubKey: ${keyToSubKeyConListWithAfterSubKey}",
//                "keyToSubKeyConListWithoutAfterSubKey: ${keyToSubKeyConListWithoutAfterSubKey}",
//                "macroDataMap: ${macroDataMap}",
//                "onExtract: ${!macroDataMap.isNullOrEmpty()}",
//                "extractJsDataMap: ${
//                    extractJsDataMap(
//                    fragment,
//                        readSharePreferenceMap,
//                        setReplaceVariableMap,
//                    keyToSubKeyConListWithoutAfterSubKey,
//                    keyToSubKeyConListWithAfterSubKey,
//                        overrideMapList,
//                        jsRepValMap,
//                )
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
            keyToSubKeyConListWithoutAfterSubKey,
            keyToSubKeyConListWithAfterSubKey,
            overrideMapList,
            jsRepValMap,
        )
        LogTool.jsActionLog(
            keyToSubKeyCon,
            jsActionMap
        )
        return jsActionMap
    }

    private fun concatRepValMap(
        jsRepValMapBeforeConcat: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ): Map<String, String>? {
        return when(true){
            (jsRepValMapBeforeConcat.isNullOrEmpty()
                    && extraRepValMap.isNullOrEmpty())
            -> null
            (!jsRepValMapBeforeConcat.isNullOrEmpty()
                    && extraRepValMap.isNullOrEmpty())
            -> jsRepValMapBeforeConcat
            (jsRepValMapBeforeConcat.isNullOrEmpty()
                    && !extraRepValMap.isNullOrEmpty())
            -> extraRepValMap
            (!jsRepValMapBeforeConcat.isNullOrEmpty()
                    && !extraRepValMap.isNullOrEmpty())
            -> jsRepValMapBeforeConcat + extraRepValMap
            else -> null
        }
    }

    private fun extractJsDataMap(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>?,
        keyToSubKeyConListWithAfterSubKey: List<Pair<String, String>>?,
        overrideMapList: List<Map<String, String>>,
        jsRepValMap: Map<String, String>?,
    ): Map<String, String>? {
        val jsCon = convertJsCon(
            fragment,
            readSharePreferenceMap,
            setReplaceVariableMap,
            keyToSubKeyConListWithoutAfterSubKey,
            keyToSubKeyConListWithAfterSubKey,
            overrideMapList,
        )?.let {
            CmdClickMap.replaceHolder(
                it,
                jsRepValMap,
            )
        }
        if(
            jsCon.isNullOrEmpty()
        ) return null
        return makeJsDataMap(
            jsCon,
        )
    }

    private fun extractMacroDataMap(
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>
    ): Map<String, String>? {
        if(
            keyToSubKeyConListWithoutAfterSubKey.size != 1
        ) return null
        val keyToSubKeyCon = keyToSubKeyConListWithoutAfterSubKey.first()
        val mainKeyName = keyToSubKeyCon.first
        if (
            mainKeyName != jsKeyName
        ) return null
        val jsMapCon = keyToSubKeyCon.second
        val jsKeySeparator = '!'
        val jsMap = CmdClickMap.createMap(
            jsMapCon,
            jsKeySeparator
        ).toMap().filterKeys { it.isNotEmpty() }
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

    private fun createKeyToSubConTypeMap(
        readSharePreferenceMap: Map<String, String>,
        keyToSubKeyCon: String?,
        setReplaceVariableMap: Map<String, String>?,
    ): Map<KeyToSubConType, List<Pair<String, String>>?>? {
        val keyToSubKeyConList = KeyToSubKeyConListMaker.make(
            keyToSubKeyCon,
            readSharePreferenceMap,
            setReplaceVariableMap,
        )
        if (
            keyToSubKeyConList.isEmpty()
        ) return null
        val keyToSubKeyConListWithReplaceKey = filterByReplaceKey(
            keyToSubKeyConList
        )
        val keyToSubKeyConListWithoutReplaceKey =  keyToSubKeyConList.filter {
            !keyToSubKeyConListWithReplaceKey.contains(it)
        }
        val keyToSubKeyConListWithAfterSubKey =
            filterByAfterJsSubKey(keyToSubKeyConListWithoutReplaceKey)

        val overrideToOther = devideOverrideAndOther(
            keyToSubKeyConListWithoutReplaceKey,
            keyToSubKeyConListWithAfterSubKey
        )
        val keyToSubKeyConListWithOverrideKey =
            overrideToOther.first
        val keyToSubKeyConListWithoutAfterSubKey =
            overrideToOther.second
        val keyToSubKeyConListForMacro =
            makeKeyToSubKeyConListForMacro(
                keyToSubKeyConListWithoutAfterSubKey
            )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jcreateJsPairList.txt").absolutePath,
//            listOf(
//                "keyToSubKeyConList: ${keyToSubKeyConList}",
//                "keyToSubKeyConListWithAfterSubKey: ${keyToSubKeyConListWithAfterSubKey}",
//                "keyToSubKeyConListWithOverrideKey: ${keyToSubKeyConListWithOverrideKey}",
//                "keyToSubKeyConListWithoutAfterSubKey: ${keyToSubKeyConListWithoutAfterSubKey}",
//                "keyToSubKeyConListForMacro: ${keyToSubKeyConListForMacro}",
//                "keyToSubKeyConListUntilFirstUnPipAble: ${
//                    makeKeyToSubKeyConListUntilFirstUnPipAble(
//                    keyToSubKeyConListWithoutAfterSubKey
//                )
//                }"
//            ).joinToString("\n\n")
//        )
        if(
            keyToSubKeyConListForMacro != null
        ) return mapOf(
            KeyToSubConType.WITH_REPLACE
                    to null,
            KeyToSubConType.WITH_AFTER
                    to null,
            KeyToSubConType.WITH_OVERRIDE
                    to null,
            KeyToSubConType.WITH_OTHER
                    to listOf(keyToSubKeyConListForMacro),
        )
        return mapOf(
            KeyToSubConType.WITH_REPLACE
                    to keyToSubKeyConListWithReplaceKey,
            KeyToSubConType.WITH_AFTER
                    to keyToSubKeyConListWithAfterSubKey,
            KeyToSubConType.WITH_OVERRIDE
                    to keyToSubKeyConListWithOverrideKey,
            KeyToSubConType.WITH_OTHER
                    to makeKeyToSubKeyConListUntilFirstUnPipAble(
                keyToSubKeyConListWithoutAfterSubKey
            ),
        )
    }

    private fun devideOverrideAndOther(
        keyToSubKeyConList: List<Pair<String, String>>,
        keyToSubKeyConListWithAfterSubKey: List<Pair<String, String>>
    ): Pair<List<Pair<String, String>>?, List<Pair<String, String>>?> {
        val keyToSubKeyConListWithoutAfterSubKeySrcWithOverride =
            keyToSubKeyConList.filter {
                !keyToSubKeyConListWithAfterSubKey.contains(it)
            }

        val keyToSubKeyConListWithOverrideKey =
            filterByOverrideJsSubKey(
                keyToSubKeyConListWithoutAfterSubKeySrcWithOverride
            )
        val keyToSubKeyConListWithoutAfterSubKeySrc =
            keyToSubKeyConListWithoutAfterSubKeySrcWithOverride.filter {
                !keyToSubKeyConListWithOverrideKey.contains(it)
            }
        val keyToSubKeyConListWithoutAfterSubKey =
            JsPathAndConToFunc.convertJaPathToFunc(
                keyToSubKeyConListWithoutAfterSubKeySrc,
            )
        return keyToSubKeyConListWithOverrideKey to
                keyToSubKeyConListWithoutAfterSubKey
    }


    private fun makeKeyToSubKeyConListForMacro(
        keyToSubKeyConList: List<Pair<String, String>>?
    ): Pair<String, String>? {
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
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>?
    ): List<Pair<String, String>> {
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

    private fun filterByOverrideJsSubKey(
        keyToSubKeyConList: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        return keyToSubKeyConList.filter {
            val mainKeyName = it.first
            mainKeyName == jsOverrideKeyName
        }
    }

    private fun filterByReplaceKey(
        keyToSubKeyConList: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        return keyToSubKeyConList.filter {
            val mainKeyName = it.first
            return@filter mainKeyName == jsReplaceKeyName
        }
    }


    private fun extractJsRepValMap(
        keyToSubKeyConList: List<Pair<String, String>>
    ): Map<String, String> {
       return keyToSubKeyConList.map {
            val mainKeyName = it.first
            if(
                mainKeyName != jsReplaceKeyName
            ) return@map emptyList()
            val jsKeySeparator = '!'
            val subKeyMapCon = it.second
            CmdClickMap.createMap(
                subKeyMapCon,
                jsKeySeparator
            )
        }.flatten().toMap()
    }

    private fun filterByAfterJsSubKey(
        keyToSubKeyConList: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        val afterKeyName = JsActionKeyManager.JsSubKey.AFTER.key
        return keyToSubKeyConList.filter {
            val mainKeyName = it.first
            if(
                mainKeyName != jsKeyName
            ) return@filter false
            val jsKeySeparator = '!'
            val subKeyMapCon = it.second
            val subKeyMap = CmdClickMap.createMap(
                subKeyMapCon,
                jsKeySeparator
            ).toMap()
            subKeyMap.keys.contains(afterKeyName)
        }
    }

    private fun howPipUnableComponent(
        mainKeyNameToSubKeyCon: Pair<String, String>
    ): Boolean {
        val jsKeySeparator = '!'
        val mainKeyName = mainKeyNameToSubKeyCon.first
        if(
            mainKeyName == jsPathKeyName
        ) return true
        if(
            mainKeyName != jsKeyName
        ) return false
        val subKeyMapCon = mainKeyNameToSubKeyCon.second
        val subKeyMap = CmdClickMap.createMap(
            subKeyMapCon,
            jsKeySeparator
        ).toMap()
        val funcName = subKeyMap.get(funcSubKeyName)?.trim()
        return !JsActionKeyManager.JsFuncManager.howPipAbleFunc(funcName)
    }

    private fun howFuncMacroComponent(
        mainKeyNameToSubKeyCon: Pair<String, String>
    ): Boolean {
        val jsKeySeparator = '!'
        val mainKeyName = mainKeyNameToSubKeyCon.first
        if(
            mainKeyName == jsPathKeyName
        ) return false
        if(
            mainKeyName != jsKeyName
        ) return false
        val subKeyMapCon = mainKeyNameToSubKeyCon.second
        val subKeyMap = CmdClickMap.createMap(
            subKeyMapCon,
            jsKeySeparator
        ).toMap()
        val macroSrc = subKeyMap.get(funcSubKeyName)?.trim()
        return howMacroFunc(
            macroSrc,
        )
    }


    private fun convertJsCon(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>?,
        keyToSubKeyConListWithAfterSubKey: List<Pair<String, String>>?,
        overrideMapList: List<Map<String, String>>,
    ): String? {
        if(
            keyToSubKeyConListWithoutAfterSubKey.isNullOrEmpty()
        ) return null
        val tsvImportCon = ImportConMaker.make(
            JsActionKeyManager.JsActionsKey.TSV_IMPORT.key
                    to TsvImportManager.tsvImportPreWord,
            keyToSubKeyConListWithoutAfterSubKey
        )
        val jsImportCon = ImportConMaker.make(
            JsActionKeyManager.JsActionsKey.JS_IMPORT.key
                    to JsImportManager.jsImportPreWord,
            keyToSubKeyConListWithoutAfterSubKey
        )
        val jsMapListWithoutAfterSubKey = makeJsMapList(
            keyToSubKeyConListWithoutAfterSubKey,
            overrideMapList,
        )
        val jsMapListWithAfterSubKey = makeJsMapList(
            keyToSubKeyConListWithAfterSubKey,
            overrideMapList,
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
//                "overrideMapList: ${overrideMapList}",
//                "keyToSubKeyConListWithAfterSubKey: ${keyToSubKeyConListWithAfterSubKey}",
//                "keyToSubKeyConListWithoutAfterSubKey: ${keyToSubKeyConListWithoutAfterSubKey}",
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
//            ).joinToString("\n\n")
//        )
        return JavaScriptLoadUrl.makeRawJsConFromContents(
            fragment,
            readSharePreferenceMap,
            jsConBeforeJsImport,
            setReplaceVariableMap,
        )
    }

    private fun makeJsMapList(
        keyToSubConMapList: List<Pair<String, String>>?,
        overrideMapList: List<Map<String, String>>,
    ): List<Map<String, String>> {
        if(
            keyToSubConMapList.isNullOrEmpty()
        ) return emptyList()
        val jsMapSrc = keyToSubConMapList.map {
            val mainKeyName = it.first
            if(
                mainKeyName != jsKeyName
            ) return@map emptyMap()
            val jsMapCon = it.second
            val jsKeySeparator = '!'
            CmdClickMap.createMap(
                jsMapCon,
                jsKeySeparator
            ).toMap()
        }.filter { it.isNotEmpty() }
        val overrideKeyName =
            JsActionKeyManager.JsSubKey.ID.key
        return jsMapSrc.map {
            val id = it.get(
                overrideKeyName
            ) ?: return@map it
                val overrideMapSrc = JsActionKeyManager.OverrideManager.makeOverrideMap(
                overrideMapList,
                id,
            )
            it.plus(overrideMapSrc)
        }
    }

    private fun makeOverrideMapList(
        keyToSubKeyConListWithOverrideKey: List<Pair<String, String>>?,
    ): List<Map<String, String>> {
        if(
            keyToSubKeyConListWithOverrideKey.isNullOrEmpty()
        ) return emptyList()
        return keyToSubKeyConListWithOverrideKey.map {
            val mainKeyName = it.first
            if(
                mainKeyName != jsOverrideKeyName
            ) return@map emptyMap()
            val jsOverrideMapCon = it.second
            val jsKeySeparator = '!'
            CmdClickMap.createMap(
                jsOverrideMapCon,
                jsKeySeparator
            ).toMap()
        }.filter { it.isNotEmpty() }
    }

    private enum class KeyToSubConType {
        WITH_REPLACE,
        WITH_OVERRIDE,
        WITH_AFTER,
        WITH_OTHER,
    }

}

private object JsPathAndConToFunc {

    private val jsKeyName = JsActionKeyManager.JsActionsKey.JS.key
    private val jsPathKeyName = JsActionKeyManager.JsActionsKey.JS_PATH.key
    private val jsConKeyName = JsActionKeyManager.JsActionsKey.JS_CON.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key

    fun convertJaPathToFunc(
        keyToSubKeyConListWithoutAfterSubKeySrc: List<Pair<String, String>>,
    ): List<Pair<String, String>>{
        return convertMacroToJsFunc(
            keyToSubKeyConListWithoutAfterSubKeySrc,
        ).let {
            convertJsConToJsFunc(
                it
            )
        }
    }

    private fun convertMacroToJsFunc(
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        return keyToSubKeyConListWithoutAfterSubKey.mapIndexed {
                index, keyToSubKeyCon ->
            val mainKeyName = keyToSubKeyCon.first
            if(
                mainKeyName != jsPathKeyName
            ) return@mapIndexed keyToSubKeyCon
            val jsPathCon = QuoteTool.trimBothEdgeQuote(
                keyToSubKeyCon.second
            ).trim()
            val isJsInterface = JsActionKeyManager.JsPathManager.isJsInterface(
                jsPathCon
            )
            val isMacro = macroValueList.contains(jsPathCon)
            when(true) {
                isMacro,
                isJsInterface
                -> toJsFunc(
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
    }

    private fun convertJsConToJsFunc(
        keyToSubKeyConListWithAfterSubKey: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        return keyToSubKeyConListWithAfterSubKey.mapIndexed {
                index, keyToSubKeyCon ->
            val mainKeyName = keyToSubKeyCon.first
            if(
                mainKeyName != jsConKeyName
            ) return@mapIndexed keyToSubKeyCon
            val jsCon = QuoteTool.compBothQuote(keyToSubKeyCon.second)
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsConConvertTofunc.txt").absolutePath,
//                listOf(
//                    "keyToSubKeyCon.first: ${keyToSubKeyCon.first}",
//                    "keyToSubKeyCon.second: ${keyToSubKeyCon.second}",
//                    "jsCon: ${jsCon}",
//                    "jsFuncCon: ${toJsFuncForJsCon(
//                        jsCon
//                    )}",
//                ).joinToString("\n\n")
//            )
            toJsFuncForJsCon(
                jsCon
            )
        }
    }

    private fun toJsFuncForJsCon(
        jsCon: String,
    ): Pair<String, String> {
        val jsConPrefix = JsActionKeyManager.JsPathManager.Flag.JS_CON_PREFIX.flag
        val jsConWithPrefix = "${jsConPrefix}${jsCon}"
        val jsKeyCon = listOf(
            "${funcSubKeyName}=${jsConWithPrefix}",
        ).filter { it.isNotEmpty() }.joinToString("!")
        return jsKeyName to jsKeyCon
    }

    private fun toJsFuncForPath(
        index: Int,
        keyToSubKeyCon: List<Pair<String, String>>?,
        jsPathCon: String,
    ): Pair<String, String> {
        if(
            keyToSubKeyCon.isNullOrEmpty()
        ) return String() to String()
        val repMapPrefix = "repMapCon"
        val argsSrc = makeArgsForJsPathMacro(
            keyToSubKeyCon,
            index,
        ).let {
            if(
                it.isNotEmpty()
            ) return@let it
            "${repMapPrefix}=CMDCLICK_BLANK_ARGS=-"
        }
        val argsCon = listOf(
            "path=${jsPathCon}",
            argsSrc
        ).joinToString("&")
        val jsKeyCon = listOf(
            "${funcSubKeyName}=jsUrl.loadJsPath",
            "${JsActionKeyManager.JsSubKey.ARGS.key}=\"${argsCon}\"",
            "${JsActionKeyManager.JsSubKey.DESC.key}=path: ${jsPathCon}"
        ).filter { it.isNotEmpty() }.joinToString("!")
        return jsKeyName to jsKeyCon
    }

    private fun toJsFunc(
        index: Int,
        keyToSubKeyConListWithoutAfterSubKey: List<Pair<String, String>>,
        jsPathCon: String,
    ): Pair<String, String> {
        val args = makeArgsForJsPathMacro(
            keyToSubKeyConListWithoutAfterSubKey,
            index,
        )
        val jsKeyCon = listOf(
            "${funcSubKeyName}=${jsPathCon}",
            args,
        ).filter { it.isNotEmpty() }.joinToString("!")
        return jsKeyName to jsKeyCon
    }

    private fun makeArgsForJsPathMacro(
        keyToSubKeyConListWithAfterSubKey: List<Pair<String, String>>,
        index: Int,
    ): String {
        val nextKeyToSubCon = keyToSubKeyConListWithAfterSubKey.getOrNull(
            index + 1
        ) ?: return String()
        val argsSubKeyName = JsActionKeyManager.JsSubKey.ARGS.key
        val nextMainKeyName = nextKeyToSubCon.first
        if(
            nextMainKeyName != argsSubKeyName
        ) return String()
        val argsConSrc = nextKeyToSubCon.second
        val argsCon = QuoteTool.replaceBySurroundedIgnore(
            argsConSrc,
            '!',
            "&"
        )
        return "${argsSubKeyName}=${argsCon}"
    }
}



private object KeyToSubKeyConListMaker {

    private const val keySeparator = '|'
    val jsActionsKeyPlusList =
        listOf(JsActionKeyManager.JsSubKey.ARGS.key) +
                JsActionKeyManager.JsActionsKey.values().map {
                    it.key
                }

    fun make(
        keyToSubKeyCon: String?,
        readSharePreferenceMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): List<Pair<String, String>> {
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val keyToSubKeyConListSrc = makeKeyToSubConPairList(
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
                val mainJsKeyName = keyToSubKeyPair.first
                if(
                    mainJsKeyName != jsActionsKeyName
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
                makeKeyToSubConPairList(
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
        return filterByValidKey(keyToSubKeyConList)
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
        val jsActionsPathSeparator = '!'
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


    private fun makeKeyToSubConPairList(
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

private object ImportConMaker {

    fun make(
        importKeyNameToImportPreWord: Pair<String, String>,
        keyToSubKeyConList: List<Pair<String, String>>,
    ): String {
        val importKeyName = importKeyNameToImportPreWord.first
        val keyToSubKeyMapListOnlyImport = keyToSubKeyConList.filter {
                keyToSubKeyPair ->
            val mainJsKeyName = keyToSubKeyPair.first
            mainJsKeyName == importKeyName
        }
        val importPreWord = importKeyNameToImportPreWord.second
        return  keyToSubKeyMapListOnlyImport.map {
                keyToSubKeyPair ->
            execPut(
                importPreWord,
                keyToSubKeyPair
            )
        }.joinToString("\n")
    }

    fun execPut(
        importPreWord: String,
        keyToSubKeyCon: Pair<String, String>,
    ): String {
        val tsvImportPathSeparator = '!'
        val subKeyCon = keyToSubKeyCon.second
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
        ) {
            LogSystems.stdWarn("Not found func: ${jsMap}")
            return String()
        }
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
        val afterFuncCon = putAfterFunc(
            jsMap,
            jsMapListOnlyAfter
        )
        val compLoopMethodTemplateWithVar =
            loopMethodTemplateWithVar.format(
                afterFuncCon
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
        val varPrefix = JsActionKeyManager.FuncFrag.VAR_PREFIX.flag
        val isVar = functionName?.startsWith(varPrefix) == true
        val funcConSrc = when(true){
            isVar -> QuoteTool
                .splitBySurroundedIgnore(varargsStr, ',').firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: "\"\""
            else -> "${functionName}(${varargsStr})"
        }
        val isJsCon = JsActionKeyManager.JsFuncManager.isJsCon(functionName)
        val isMethod = !method.isNullOrEmpty()
        return when(true){
            isJsCon -> "${makeJsCon(functionName)};"
            isMethod -> "${funcConSrc}.${method};"
            else -> "${funcConSrc};"
        }
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
        val funcTemplateSrc = when(
            ifCondition.isNullOrEmpty()
        ){
            false -> """
            |if(${ifCondition}){ 
            |    %s 
            |}   
            |""".trimMargin()
            else -> "%s"
        }
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
