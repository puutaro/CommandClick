package com.puutaro.commandclick.proccess.edit.setting_action

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingFuncManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

class SettingActionManager {

//    private val settingActionKeyName = SettingActionKeyManager.SettingActionsKey.SETTING_ACTION.key
//    private val jsPathKeyName = JsActionKeyManager.JsActionsKey.JS_FUNC.key
    private val funcSubKeyName = JsActionKeyManager.JsSubKey.FUNC.key


    companion object {
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

        fun makeSettingActionKeyToSubKeyList(
            fragment: Fragment,
            fannelInfoMap: Map<String, String>,
            keyToSubKeyCon: String?,
            setReplaceVariableMapSrc: Map<String, String>?,
        ): List<Pair<String, String>>? {
            val setReplaceVariableMap = makeSetRepValMap(
                fragment,
                fannelInfoMap,
                setReplaceVariableMapSrc
            )
            val keyToSubKeyConWithReflectRepValDefalt = CmdClickMap.replaceHolderForJsAction(
                keyToSubKeyCon ?: String(),
                null
            )
            val keyToSubKeyConList = createKeyToSubMapTypeMap(
                fragment.context,
                fannelInfoMap,
                keyToSubKeyConWithReflectRepValDefalt,
                setReplaceVariableMap,
            ) ?: return null
            return keyToSubKeyConList
        }

        private fun createKeyToSubMapTypeMap(
            context: Context?,
            fannelInfoMap: Map<String, String>,
            keyToSubKeyCon: String?,
            setReplaceVariableMap: Map<String, String>?,
        ): List<Pair<String, String>>? {
            val keyToSubKeyConList = KeyToSubKeyMapListMaker.make(
                context,
                keyToSubKeyCon,
                fannelInfoMap,
                setReplaceVariableMap,
            )
//        val keyToSubKeyMapList =
//            keyToSubKeyMapListToKeyToSubKeyConListByValidKey.first
//        val actionImportedKeyToSubKeyConList = keyToSubKeyConList
//            keyToSubKeyMapListToKeyToSubKeyConListByValidKey.second
            if (
                keyToSubKeyConList.isEmpty()
            ) return null
            return keyToSubKeyConList
        }

    }

    fun exec(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        keyToSubKeyConList: List<Pair<String, String>>?,
    ): Map<String, String> {
        return SettingActionExecutor.exec(
            fragment,
            fannelInfoMap,
            setReplaceVariableMapSrc,
            busyboxExecutor,
            keyToSubKeyConList,
        )
    }


    object SettingActionExecutor {

        private val varNameToValueMap = mutableMapOf<String, String>()
        private val innerVarNameToValueMap = mutableMapOf<String, String>()


        fun exec(
            fragment: Fragment,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            keyToSubKeyConList: List<Pair<String, String>>?,
        ): Map<String, String> {
            varNameToValueMap.clear()
            if(
                keyToSubKeyConList.isNullOrEmpty()
                ) return emptyMap()
            keyToSubKeyConList.forEach {
                keyToSubKeyCon ->
                val curSettingActionKeyStr = keyToSubKeyCon.first
                val curSettingActionKey = SettingActionKeyManager.SettingActionsKey.entries.firstOrNull {
                    it.key == curSettingActionKeyStr
                } ?: return@forEach
                val subKeyCon = let {
                    val repMap =
                        (setReplaceVariableMapSrc ?: emptyMap()) +
                                varNameToValueMap +
                                innerVarNameToValueMap
                    CmdClickMap.replace(
                        keyToSubKeyCon.second,
                        repMap
                    )
                }
                when(
                    curSettingActionKey
                ){
                    SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR -> {}
//                    SettingActionKeyManager.SettingActionsKey.SETTING_TSV_VARS -> {}
                    SettingActionKeyManager.SettingActionsKey.SETTING_VAR -> {
                        SettingVar.exec(
                            curSettingActionKey.key,
                            subKeyCon,
                            busyboxExecutor,
                        )?.let {
                            val varValue = it.second
                            if(
                                varValue == SettingActionKeyManager.CommandMacro.EXIT_SIGNAL.name
                            ){
                                return varNameToValueMap
                            }
                            val varName = it.first
                            varNameToValueMap.put(varName, varValue)
                        }
                    }
                    SettingActionKeyManager.SettingActionsKey.SETTING_INNER_VAR -> {
                        SettingVar.exec(
                            curSettingActionKey.key,
                            subKeyCon,
                            busyboxExecutor,
                        )?.let {
                            val varValue = it.second
                            if(
                                varValue == SettingActionKeyManager.CommandMacro.EXIT_SIGNAL.name
                            ){
                                return varNameToValueMap
                            }
                            val varName = it.first
                            innerVarNameToValueMap.put(varName, varValue)
                        }
                    }
                }
            }
            return varNameToValueMap
        }

        private object SettingVar {

            private val settingVarKey = SettingActionKeyManager.SettingSubKey.SETTING_VAR.key
            private const val escapeRunPrefix = "run"
            private const val itPronoun = "it"
            private const val itReplaceVarStr = "${'$'}{${itPronoun}}"
//            Regex("([^a-zA-Z0-9_])${itPronoun}([^a-zA-Z0-9_])")
//            private val itSuggerVarRegexEndVer = Regex("([^a-zA-Z0-9_])${itPronoun}$")
            private var itPronounValue = String()
            private var isNext = true
            private val valueSeparator = SettingActionKeyManager.valueSeparator

            fun exec(
                settingVarKey: String,
                subKeyCon: String,
                busyboxExecutor: BusyboxExecutor?,
            ): Pair<String, String>? {
                val mainSubKeyPairList = makeMainSubKeyPairList(
                    settingVarKey,
                    subKeyCon,
                )
                val settingVarName = PairListTool.getValue(
                    mainSubKeyPairList,
                    settingVarKey
                )?.get(settingVarKey)
                    ?: return null
                mainSubKeyPairList.forEach {
                        mainSubKeyPair ->
                    val mainSubKey = mainSubKeyPair.first
                    val mainSubKeyMap = mainSubKeyPair.second.map {
                        replaceItPronoun(it.key) to replaceItPronoun(it.value)
                    }.toMap()
                    val innerSubKeyClass = SettingActionKeyManager.SettingSubKey.entries.firstOrNull {
                        it.key == mainSubKey
                    } ?: return@forEach
                    when(innerSubKeyClass) {
                        SettingActionKeyManager.SettingSubKey.SETTING_VAR,
                        SettingActionKeyManager.SettingSubKey.ARGS -> {}
                        SettingActionKeyManager.SettingSubKey.VALUE -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            mainSubKeyMap.get(mainSubKey)?.let {
                                itPronounValue = it
                            }
                            isNext = true
                        }
                        SettingActionKeyManager.SettingSubKey.RETURN -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            return settingVarName to
                                    (mainSubKeyMap.get(mainSubKey) ?: String())
                        }
                        SettingActionKeyManager.SettingSubKey.FUNC -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val funcTypeDotMethod = mainSubKeyMap.get(mainSubKey)
                                ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMap.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            )
                            SettingFuncManager.handle(
                                funcTypeDotMethod,
                                argsPairList,
                                busyboxExecutor
                            )?.let{
                                itPronounValue = it
                            }
                        }
                        SettingActionKeyManager.SettingSubKey.S_IF -> {
                            if(!isNext) {
                                isNext = true
                                return@forEach
                            }
                            val judgeTargetStr = mainSubKeyMap.get(mainSubKey)
                                ?: return@forEach
                            val argsPairList = CmdClickMap.createMap(
                                mainSubKeyMap.get(
                                    SettingActionKeyManager.SettingSubKey.ARGS.key
                                ),
                                valueSeparator
                            )
                            SettingIfManager.handle(
                                judgeTargetStr,
                                argsPairList
                            )?.let {
                                isNext = it
                            }
                        }
                    }
                    if(innerSubKeyClass != SettingActionKeyManager.SettingSubKey.S_IF){
                        isNext = true
                    }
                }
                return when(settingVarName.startsWith(escapeRunPrefix)){
                    true -> null
                    else -> settingVarName to itPronounValue
                }
            }

            fun makeMainSubKeyPairList(
                settingVarKey: String,
                subKeyCon: String,
            ): List<
                    Pair<
                            String,
                            Map<String, String>
                            >
                    > {
                val subKeySeparator = SettingActionKeyManager.subKeySepartor
                val subKeyToConList = CmdClickMap.createMap(
                    "${settingVarKey}=${subKeyCon}",
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
                        SettingActionKeyManager.SettingSubKey.VALUE,
                        SettingActionKeyManager.SettingSubKey.RETURN -> {
                            val mainSubKeyMap = mapOf(
                                innerSubKeyName to innerSubKeyCon,
                            )
                            Pair(innerSubKeyName, mainSubKeyMap)
                        }
                        SettingActionKeyManager.SettingSubKey.FUNC,
                        SettingActionKeyManager.SettingSubKey.S_IF-> {
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

            private fun replaceItPronoun(con: String): String {
                return con.replace(itReplaceVarStr, itPronounValue)
//                    .replace(itSuggerVarRegexEndVer, "$1${itPronounValue}")
            }
        }
    }



    private object PairListTool {

        fun getValue(
            pairList: List<Pair<String, Map<String, String>>>?,
            key: String
        ): Map<String, String>? {
            return getPair(
                pairList,
                key
            )
        }

        fun getPair(
            pairList: List<Pair<String, Map<String, String>>>?,
            key: String
        ): Map<String, String>? {
            if(
                pairList.isNullOrEmpty()
            ) return null
            return pairList.firstOrNull {
                it.first == key
            }?.second
        }
    }
}


private object KeyToSubKeyMapListMaker {

    private const val keySeparator = '|'
    private val settingActionsKeyPlusList =
        SettingActionKeyManager.SettingActionsKey.entries.map {
            it.key
        }

    fun make(
        context: Context?,
        keyToSubKeyCon: String?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ): List<Pair<String, String>> {
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val keyToSubKeyConListSrc = makeKeyToSubConPairListByValidKey(
            keyToSubKeyCon
        )
        val importRoopLimit = 5
//        var containImport = false
        val settingActionVarKeyName = SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
        val settingActionImportSignal = "${settingActionVarKeyName}="
        var keyToSubKeyConList = keyToSubKeyConListSrc
//        ActionImportPutter.initBeforeActionImportMap(
//            setReplaceVariableMap
//        )
//        for( i in 1..importRoopLimit) {
//            var errType: ActionImportPutter.ErrSignal =
//                ActionImportPutter.ErrSignal.NO_ERR
//            keyToSubKeyConList = keyToSubKeyConList.map {
//                    keyToSubKeyPair ->
//                val mainKeyName = keyToSubKeyPair.first
//                if(
//                    mainKeyName != settingActionVarKeyName
//                ) return@map listOf(keyToSubKeyPair)
//                val putKeyToSubKeyConToErrType =
//                    ActionImportPutter.put(
//                        context,
//                        currentFannelName,
//                        setReplaceVariableMap,
//                        keyToSubKeyPair
//                    )
//                val putKeyToSubKeyCon = putKeyToSubKeyConToErrType.first
//                errType = putKeyToSubKeyConToErrType.second
////                FileSystems.updateFile(
////                    File(UsePath.cmdclickDefaultAppDirPath, "jsAc_import.txt").absolutePath,
////                    listOf(
////                        "putKeyToSubKeyCon: ${putKeyToSubKeyCon}",
////                        "\n\nupdatePutKeyToSubKeyCon ${makeKeyToSubConPairListByValidKey(
////                            putKeyToSubKeyCon
////                        )}",
////                        "errType: ${errType.name}",
//////                        "containImport: ${containImport}",
////                        "roopNum: ${i}",
////                    ).joinToString("\n\n") + "\n--------\n"
////                )
//                makeKeyToSubConPairListByValidKey(
//                    putKeyToSubKeyCon
//                )
//            }.flatten()
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_import_flattern_after.txt").absolutePath,
////                listOf(
////                    "keyToSubKeyConList: ${keyToSubKeyConList}",
////                    "errType: ${errType.name}",
////                    "roopNum: ${i}",
//////                    "containImport: ${containImport}"
////                ).joinToString("\n\n") + "\n--------\n"
////            )
//            if(errType == ActionImportPutter.ErrSignal.ERR) break
//            val containImport = keyToSubKeyConList.find{
//                val key = it.first
//                key == settingActionVarKeyName
//            } != null
//            if(!containImport) break
//        }
        val keyToSubKeyConListByValidKey =
            filterByValidKey(keyToSubKeyConList)
        return keyToSubKeyConListByValidKey
//        PairToMapInList.convert(
//            keyToSubKeyConListByValidKey
//        ) to keyToSubKeyConListByValidKey
    }

    private fun filterByValidKey(
        keyToSubKeyConList: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        return keyToSubKeyConList.filter {
            val mainKeyName = it.first
            settingActionsKeyPlusList.contains(mainKeyName)
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
            settingActionsKeyPlusList.contains(mainKey)
        }.map {
            val mainKey = it.first
            val subKeyAfterStr = it.second
            mainKey to subKeyAfterStr
        }
    }
}


//private object ImportMapMaker {
//    fun comp(
//        subKeyCon: String,
//        firstSubKeyWithEqualPrefix: String,
//    ): Map<String, String> {
//        val subKeySeparator = '?'
//        val subKeyConList = subKeyCon.split(subKeySeparator)
//        val importPathKeyCon = subKeyConList.firstOrNull()
//            ?: String()
//        val endsQuote = extractEndsQuote(
//            importPathKeyCon.removePrefix(firstSubKeyWithEqualPrefix),
//        )
//        if (
//            endsQuote.isNullOrEmpty()
//        ) {
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_makeActionImportMap1.txt").absolutePath,
////                listOf(
////                    "subKeyCon: ${subKeyCon}",
////                    "importPathKeyCon: ${importPathKeyCon}",
////                ).joinToString("\n\n")
////            )
//            return CmdClickMap.createMap(
//                subKeyCon,
//                subKeySeparator
//            ).toMap()
//        }
//        val otherKeyCon = subKeyConList.filterIndexed { index, _ ->
//            index > 0
//        }.joinToString(subKeySeparator.toString())
//        val compImportPathKeyCon = listOf(
//            firstSubKeyWithEqualPrefix,
//            endsQuote,
//            importPathKeyCon.removePrefix(firstSubKeyWithEqualPrefix),
//        ).joinToString(String())
//        val compOtherKeyCon = listOf(
//            otherKeyCon,
//            endsQuote
//        ).joinToString(String())
//        val compQuoteSubKeyCon = listOf(
//            compImportPathKeyCon,
//            subKeySeparator.toString(),
//            compOtherKeyCon
//        ).joinToString(String())
////        FileSystems.updateFile(
////            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_makeActionImportMap2.txt").absolutePath,
////            listOf(
////                "subKeyCon: ${subKeyCon}",
////                "importPathKeyCon: ${importPathKeyCon}",
////                "compImportPathKeyCon: ${compImportPathKeyCon}",
////                "importPathKeyPrefix: ${importPathKeyPrefix}",
////                "otherKeyCon: ${otherKeyCon}",
////                "compOtherKeyCon: ${compOtherKeyCon}",
////                "compQuoteSubKeyCon: ${compQuoteSubKeyCon}",
////                "map: ${CmdClickMap.createMap(
////                    compQuoteSubKeyCon,
////                    subKeySeparator
////                ).toMap()}"
////            ).joinToString("\n\n")
////        )
//        return CmdClickMap.createMap(
//            compQuoteSubKeyCon,
//            subKeySeparator
//        ).toMap()
//    }
//
//    private fun extractEndsQuote(
//        importPathKeyCon: String,
//    ): String? {
//        val quoteList = listOf("`", "\"")
//        quoteList.forEach {
//            val isOnlyEndQuote = importPathKeyCon.endsWith(it)
//                    && !importPathKeyCon.startsWith(it)
//            if(
//                isOnlyEndQuote
//            ) return it
//        }
//        return null
//    }
//}
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
//    fun put(
//        context: Context?,
//        currentFannelName: String,
//        setReplaceVariableMap: Map<String, String>?,
//        keyToSubKeyPair: Pair<String, String>,
//    ): Pair<String, ErrSignal> {
//        val subKeyCon = listOf(
//            actionVarKey,
//            keyToSubKeyPair.second
//        ).joinToString("=")
//        val actionImportMap = ImportMapMaker.comp(
//            subKeyCon,
//            "${actionVarKey}="
//        )
//        val importPathSrc = QuoteTool.trimBothEdgeQuote(
//            actionImportMap.get(
//                JsActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
//            )
//        )
//        if(
//            importPathSrc.isEmpty()
//        ){
//            val missImportKey =
//                "${JsActionKeyManager.ActionImportManager.ActionImportKey.MISS_IMPORT_PATH.key}="
//            val actionVarSec = subKeyCon.replace(
//                Regex("(${actionVarKey}=[^?|]+)"),
//                "|$1?${missImportKey}"
//            )
//            return actionVarSec to ErrSignal.ERR
//        }
////        FileSystems.updateFile(
////            File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport.txt").absolutePath,
////            listOf(
////                "keyToSubKeyPair.second: ${keyToSubKeyPair.second}",
////                "subKeyCon: ${subKeyCon}",
////                "actionImportMap: ${actionImportMap}",
////                "importPathSrc: ${importPathSrc}",
////            ).joinToString("\n\n") + "\n-----\n"
////        )
//        val importPath =
//            JsActionKeyManager.PathExistChecker.makeCodeOrPath(importPathSrc)
//        val isNotFoundPrefix =
//            importPath.startsWith(JsActionKeyManager.PathExistChecker.notFoundCodePrefix)
//        val pathNotRegisterInRepValErrSignal =
//            JsActionKeyManager.PathNotRegisterInRepValChecker.echoErrSignal(
//                importPath,
//                beforeActionImportMap,
//                replaceVariableMapCon
//            )
//        val importConSrcToErrType = when(true){
//            isNotFoundPrefix ->
//                "${jsMainKey}=?${actionImportVirtualSubKey}=${importPath}" to ErrSignal.NO_ERR
//            !pathNotRegisterInRepValErrSignal.isNullOrEmpty() ->
//                "${jsMainKey}=?${actionImportVirtualSubKey}=${pathNotRegisterInRepValErrSignal}" to ErrSignal.NO_ERR
//            else -> {
//                val importConListToErrType = makeImportConSrcToErrType(
//                    context,
//                    actionImportMap,
//                    importPath,
////                    currentAppDirPath,
//                    currentFannelName,
//                    setReplaceVariableMap,
//                )
//                val importCon = importConListToErrType.first.joinToString("\n")
//                val errType = importConListToErrType.second
//                importCon to errType
//            }
//        }
////        FileSystems.updateFile(
////            File(UsePath.cmdclickDefaultAppDirPath, "jsAcImport_put.txt").absolutePath,
////            listOf(
////                "importPath: ${importPath}",
////                "isNotFoundPrefix: ${isNotFoundPrefix}",
////                "pathNotRegisterInRepValErrSignal: ${pathNotRegisterInRepValErrSignal}",
////                "!pathNotRegisterInRepValErrSignal.isNullOrEmpty(): ${!pathNotRegisterInRepValErrSignal.isNullOrEmpty()}",
////                "importConSrcToErrType ${importConSrcToErrType}",
////            ).joinToString("\n") + "\n-----------------\n"
////        )
//        val importConSrc = importConSrcToErrType.first
//        val errType = importConSrcToErrType.second
//        if(errType == ErrSignal.ERR){
//            return importConSrc to ErrSignal.ERR
//        }
//        return importConSrc.let {
//            ListSettingVariableListMaker.execRemoveMultipleNewLinesAndReplace(
//                it,
//                setReplaceVariableMap,
////                currentAppDirPath,
//                currentFannelName,
//            ) to errType
//        }
//    }

//    private fun makeImportConSrcToErrType(
//        context: Context?,
//        actionImportMap: Map<String, String>,
//        importPath: String,
////        currentAppDirPath: String,
//        currentFannelName: String,
//        setReplaceVariableMap: Map<String, String>?,
//    ): Pair<List<String>, ErrSignal> {
//        val importSrcConBeforeReplace = makeActionImportSrcCon(
//            context,
//            importPath,
////            currentAppDirPath,
//            currentFannelName,
//            setReplaceVariableMap,
//        )
////            .replace(
////            afterKeyConRegex,
////            "?${invalidAfterInAcImport}=$1"
////        )
//        val repMap = makeRepValHolderMap(
//            actionImportMap.get(
//                JsActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
//            )
//        )
//        val idSubKeyConRegex = Regex("\\?${idSubKey}=[^?|\n]+")
//        val idSubKeyPrefix = "?${idSubKey}="
//        val importSrcCon = CmdClickMap.replaceHolderForJsAction(
//            importSrcConBeforeReplace,
//            repMap
//        )
////        val idList = idSubKeyConRegex.findAll(
////            importSrcCon
////        ).map {
////            QuoteTool.trimBothEdgeQuote(
////                it.value.removePrefix(idSubKeyPrefix)
////            )
////        }.filter { it.isNotEmpty() }.toList()
//        val importConWithFormatList = makeActionImportFormatList(
//            importSrcCon,
//        )
//        val actionVarValue = QuoteTool.trimBothEdgeQuote(
//            actionImportMap.get(
//                actionVarKey
//            )
//        )
//        val whenCondition = QuoteTool.trimBothEdgeQuote(
//            actionImportMap.get(
//                JsActionKeyManager.ActionImportManager.ActionImportKey.WHEN.key
//            )
//        )
////        val afterId = QuoteTool.trimBothEdgeQuote(
////            actionImportMap.get(afterKey)
////        )
////        val delayTime = QuoteTool.trimBothEdgeQuote(
////            actionImportMap.get(delayKey)
////        ).let {
////            if(
////                it.isEmpty()
////            ) return@let null
////            try {it.toInt()} catch(e: Exception){
////                null
////            }
////        }
//        return putIfVarFuncBracketToErrType(
//            importConWithFormatList,
//            whenCondition,
//            actionVarValue,
////            afterId,
////            idList,
////            delayTime,
//        )
//    }

//    private fun putIfVarFuncBracketToErrType(
//        importConWithFormatList: List<String>,
//        whenCondition: String?,
//        actionVarValue: String?,
////        afterId: String?,
////        idList: List<String>,
////        delayTime: Int?,
//    ): Pair<List<String>, ErrSignal> {
//        val importConWithFormatListByAcVarToAcVarErr =
//            ImportConWithFormatListForAcVar.validate(
//                importConWithFormatList,
//                actionVarValue,
//            )
//        val errSignalByAcVar =
//            importConWithFormatListByAcVarToAcVarErr.second
////        FileSystems.updateFile(
////            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_errSignalByUseVar.txt").absolutePath,
////            listOf(
////                "errSignalByUseVar: ${errSignalByUseVar.name}",
////                "importConWithFormatListByUseVar: ${importConWithFormatListByUseVarToUseVarErr.first}",
////            ).joinToString("\n\n")
////        )
////        if(errSignalByAcVar == ErrSignal.ERR) {
////            val importConWithFormatListByAcVar =
////                importConWithFormatListByAcVarToAcVarErr.first
////            val updatedImportConByWhenCondition = updateImportConByWhenCondition(
////                importConWithFormatListByAcVar,
////                whenCondition,
////                actionVarValue,
//////                afterId,
//////                delayTime,
////            )
////            return updatedImportConByWhenCondition to ErrSignal.ERR
////        }
////        val importConWithFormatListByAcVar =
////            importConWithFormatListByAcVarToAcVarErr.first
//////        val updatedImportConWithFormatListByAfterToErrType =
//////            ImportConWithFormatListForUseAfter.update(
//////                importConWithFormatListByAcVar,
////////                idList,
////////                afterId,
//////            )
//////        val updatedImportConWithFormatListByAfter =
//////            updatedImportConWithFormatListByAfterToErrType.first
//////        val errType =
//////            updatedImportConWithFormatListByAfterToErrType.second
////        return updateImportConByWhenCondition(
////            updatedImportConWithFormatListByAfter,
////            whenCondition,
//////            actionVarValue,
//////            afterId,
//////            delayTime,
////        ) to errType
//    }

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

    private fun updateImportConByWhenCondition(
        updatedImportConWithFormatList: List<String>,
        whenCondition: String?,
//        funcVarConSrc: String?,
//        afterIdSrc: String?,
//        delayTime: Int?,
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
//        val setTimeoutBracketStartKeyValueCon = delayTime.let {
//            if(
//                it == null
//                || it == 0
//            ) return@let String()
//            "?${JsActionKeyManager.JsSubKey.SET_TIMEOUT_START_BRACKET.key}="
//        }
//        val setTimeoutBracketEndKeyValueCon = delayTime.let {
//            if(
//                it == null
//                || it == 0
//            ) return@let String()
//            "?${JsActionKeyManager.JsSubKey.SET_TIMEOUT_END_BRACKET.key}=${it}"
//        }
//        val funcVarKeyValueCon = funcVarConSrc.let {
//            if(
//                it.isNullOrEmpty()
//            ) return@let String()
//            "?${JsActionKeyManager.JsSubKey.FUNC_VAR.key}=${it}"
//        }
//        val afterKeyValueCon =
//            afterIdSrc.let {
//                if(
//                    it.isNullOrEmpty()
//                ) return@let String()
//                "?${afterKey}=`${it}`"
//            }
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
                "?${SettingActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
                ifBracketSatrtKeyValueCon,
//                setTimeoutBracketStartKeyValueCon,
//                funcVarKeyValueCon,
//                "?${JsActionKeyManager.JsSubKey.FUNC_BRACKET_START.key}=",
//                afterKeyValueCon,
            ).joinToString(String())
        ) + updatedImportConWithFormatList +
                listOf(
                    listOf(
                        "|${JsActionKeyManager.JsActionsKey.JS.key}=",
                        "?${SettingActionKeyManager.VirtualSubKey.ACTION_IMPORT_CON.key}=",
//                        setTimeoutBracketEndKeyValueCon,
                        ifBracketEndKeyValueCon,
//                        "?${JsActionKeyManager.JsSubKey.FUNC_BRACKET_END.key}=",
//                        afterKeyValueCon,
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
        context: Context?,
        importPath: String,
//        currentAppDirPath: String,
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
            context,
            importPath,
            File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath,
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
                    Regex("^[${mainKeySeparator}]{2,}"),
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
            val mainKey = SettingActionKeyManager.SettingActionsKey.entries.firstOrNull {
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
                SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR,
                    -> String() to emptyMap()
//                SettingActionKeyManager.SettingActionsKey.SETTING_ACTION,
//                    -> convertToJsMapOnlyIfBracketOrErrSignal(mapConSrc)
                SettingActionKeyManager.SettingActionsKey.SETTING_VAR,
                    -> VarShortSyntaxToJsFunc.toJsFunc(
                    mapConSrc,
                )
                SettingActionKeyManager.SettingActionsKey.SETTING_INNER_VAR
                    -> VarShortSyntaxToJsFunc.toJsFunc(
                    mapConSrc,
                )
//                SettingActionKeyManager.SettingActionsKey.SETTING_ACTION,
//                    -> convertFuncToJsFunc(
//                    mapConSrc,
//                )
//                JsActionKeyManager.JsActionsKey.JS_PATH
//                    -> convertJsPathToJsFunc(
//                    mapConSrc
//                )
//                SettingActionKeyManager.SettingActionsKey.SETTING_TSV_VARS -> {
////                    FileSystems.updateFile(
////                        File(UsePath.cmdclickDefaultAppDirPath, "tsvComp.txt").absolutePath,
////                        listOf(
////                            "mapConSrc: ${mapConSrc}",
////                            "comp: ${QuoteTool.trimBothEdgeQuote(mapConSrc)}"
////                        ).joinToString("\n\n") + "\n\n-------\n\n"
////                    )
//                    convertToTsvImportMap(
//                        QuoteTool.trimBothEdgeQuote(mapConSrc),
//                    )
//                }
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
                val useKeyForAfterJsConForVar = JsActionKeyManager.OnlySubKeyMapForShortSyntax.UseKeyForAfterJsConForVar.entries.firstOrNull {
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
    MacroForToolbarButton.Macro.entries
        .map {
            it.name
        } + JsPathMacroForListIndex.entries
        .map {
            it.name
        } + JsMacroForQr.entries
        .map {
            it.name
        }
