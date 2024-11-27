package com.puutaro.commandclick.proccess.edit.setting_action

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.ImportMapMaker
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingFuncManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.VirtualSubFannel
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class SettingActionManager {

    companion object {

        object BeforeActionImportMapManager {
            private val beforeActionImportMap = mutableMapOf<String, String>()

            suspend fun get(
                importPath: String
            ): String? {
                mutex.withLock {
                    return beforeActionImportMap.get(importPath)
                }
            }
            private val mutex = Mutex()
            suspend fun put(
                importPath: String,
                importCon: String
            ) {
                mutex.withLock {
                    beforeActionImportMap.put(importPath, importCon)
                }
            }

            suspend fun init() {
                mutex.withLock {
                    beforeActionImportMap.clear()
                }
            }
        }

        object GlobalEditManager {
            private var globalEdit = false

            suspend fun get(
            ): Boolean {
                mutex.withLock {
                    return globalEdit
                }
            }
            private val mutex = Mutex()
            suspend fun exit(
            ) {
                mutex.withLock {
                    globalEdit = true
                }
            }

            suspend fun init() {
                mutex.withLock {
                    globalEdit = false
                }
            }
        }
    }

    suspend fun exec(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        keyToSubKeyCon: String?,
    ): Map<String, String> {
        val keyToSubKeyConList = makeSettingActionKeyToSubKeyList(
            fragment,
            fannelInfoMap,
            keyToSubKeyCon,
            setReplaceVariableMapSrc,
            null,
        )
        val settingActionExecutor = SettingActionExecutor(
            WeakReference(fragment),
            fannelInfoMap,
            setReplaceVariableMapSrc,
            busyboxExecutor,
        )
        settingActionExecutor.makeResultLoopKeyToVarNameValueMap(
            keyToSubKeyConList,
            SettingActionExecutor.mapRoopKeyUnit,
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
        val setReplaceVariableMap = makeSetRepValMap(
            fragment,
            fannelInfoMap,
            setReplaceVariableMapSrc
        )
        val keyToSubKeyConWithReflectRepValDefalt = CmdClickMap.replaceHolderForJsAction(
            keyToSubKeyCon ?: String(),
            repValsMap
        )
        val keyToSubKeyConList = KeyToSubKeyMapListMaker.make(
            keyToSubKeyConWithReflectRepValDefalt,
        )
        if (
            keyToSubKeyConList.isEmpty()
        ) return null
        return keyToSubKeyConList
    }


    class SettingActionExecutor(
        private val fragmentRef: WeakReference<Fragment>,
        private val fannelInfoMap: Map<String, String>,
        private val setReplaceVariableMapSrc: Map<String, String>?,
        private val busyboxExecutor: BusyboxExecutor?,
    ) {

        private val loopKeyToVarNameValueMap = mutableMapOf<String, MutableMap<String, String>>()
        private val innerLoopKeyVarNameValueMap = mutableMapOf<String, MutableMap<String, String>>()
        private var exitSignal = false

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

        fun getResultLoopKeyToVarNameValueMap(): Map<String, String> {
            return loopKeyToVarNameValueMap.get(mapRoopKeyUnit)
                ?: emptyMap()
        }


        suspend fun makeResultLoopKeyToVarNameValueMap(
            keyToSubKeyConList: List<Pair<String, String>>?,
            curMapLoopKey: String,
            renewalVarName: String?
        ) {
            val context = fragmentRef.get()?.context
                ?: return
            loopKeyToVarNameValueMap.get(curMapLoopKey)?.clear()
            innerLoopKeyVarNameValueMap.get(curMapLoopKey)?.clear()
            if(
                keyToSubKeyConList.isNullOrEmpty()
                ) return
            val globalVarNameRegex = "[A-Z0-9_]+".toRegex()
            keyToSubKeyConList.forEach {
                keyToSubKeyConSrc ->
                val globalEdit = GlobalEditManager.get()
                if(
                    globalEdit
                    || exitSignal
                ) return
                val keyToSubKeyCon = let {
                    val repMap =
                        (setReplaceVariableMapSrc ?: emptyMap()) +
                                (loopKeyToVarNameValueMap.get(curMapLoopKey) ?: emptyMap()) +
                                (innerLoopKeyVarNameValueMap.get(curMapLoopKey) ?: emptyMap())
                    val firstCon = CmdClickMap.replace(
                        keyToSubKeyConSrc.first,
                        repMap
                    )
                    val secondCon = CmdClickMap.replace(
                        keyToSubKeyConSrc.second,
                        repMap
                    )
                    firstCon to secondCon
                }
                val curSettingActionKeyStr = keyToSubKeyCon.first
                val curSettingActionKey = SettingActionKeyManager.SettingActionsKey.entries.firstOrNull {
                    it.key == curSettingActionKeyStr
                } ?: return@forEach
                val subKeyCon = keyToSubKeyCon.second
                when(
                    curSettingActionKey
                ){
                    SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR -> {
                        val renewalVarNameToImportedKeyToSubKeyConList = SettingImport.import(
                            context,
                            fannelInfoMap,
                            setReplaceVariableMapSrc,
                            keyToSubKeyCon,
                        )
                        val renewalVarNameSrc =
                            renewalVarNameToImportedKeyToSubKeyConList.first
                        val importedKeyToSubKeyConList =
                            renewalVarNameToImportedKeyToSubKeyConList.second
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "sImport.txt").absolutePath,
//                            listOf(
//                                "renewalVarNameSrc: ${renewalVarNameSrc}",
//                                "importedKeyToSubKeyConList: ${importedKeyToSubKeyConList}",
//                            ).joinToString("\n") + "\n\n=============\n\n"
//                        )
                        makeResultLoopKeyToVarNameValueMap(
                            importedKeyToSubKeyConList,
                            addLoopKey(curMapLoopKey),
                            renewalVarNameSrc,
                        )
                    }
                    SettingActionKeyManager.SettingActionsKey.SETTING_VAR -> {
                        SettingVar().exec(
                            context,
                            curSettingActionKey.key,
                            subKeyCon,
                            busyboxExecutor,
                        )?.let {
                            val varValue = it.second
                            if(
                                varValue == SettingActionKeyManager.CommandMacro.EXIT_SIGNAL.name
                            ){
                                exitSignal = true
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "sExit.txt").absolutePath,
//                                    listOf(
//                                        "exitSignal: ${exitSignal}",
//                                    ).joinToString("\n") + "\n\n==========\n\n"
//                                )
                                return
                            }
                            val varName = it.first
                            val isGlobal = globalVarNameRegex.matches(varName)
                            innerLoopKeyVarNameValueMap.get(
                                curMapLoopKey
                            ).let {
                                curInnerMapLoopKeyVarNameValueMap ->
                                when(curInnerMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                    false -> curInnerMapLoopKeyVarNameValueMap.put(varName, varValue)
                                    else -> innerLoopKeyVarNameValueMap.put(
                                        curMapLoopKey,
                                        mutableMapOf(varName to varValue)
                                    )
                                }
                            }
                            if(isGlobal) {
//                                val beforeLoopKeyToVarNameValueMap = loopKeyToVarNameValueMap.toString()
                                val removedLoopKey = removeLoopKey(curMapLoopKey)
                                val varNameForPut = renewalVarName ?: varName
                                loopKeyToVarNameValueMap.get(
                                    removedLoopKey
                                ).let {
                                        curMapLoopKeyVarNameValueMap ->
                                    when(curMapLoopKeyVarNameValueMap.isNullOrEmpty()) {
                                        false -> curMapLoopKeyVarNameValueMap.put(varNameForPut, varValue)
                                        else -> loopKeyToVarNameValueMap.put(
                                            removedLoopKey,
                                            mutableMapOf(varNameForPut to varValue)
                                        )
                                    }
                                }
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "sloopKeyToVarNameValueMap.txt").absolutePath,
//                                    listOf(
//                                        "curMapLoopKey: ${curMapLoopKey}",
//                                        "removedLoopKey: ${removedLoopKey}",
//                                        "varName: ${varName}",
//                                        "varNameForPut: ${varNameForPut}",
//                                        "beforeLoopKeyToVarNameValueMap: ${beforeLoopKeyToVarNameValueMap}",
//                                        "loopKeyToVarNameValueMap: ${loopKeyToVarNameValueMap}",
//                                    ).joinToString("\n") + "\n\n==========\n\n"
//                                )
                            }
                        }
                    }
                }
            }
        }


        private object SettingImport {

            private val valueSeparator = SettingActionKeyManager.valueSeparator
            private val settingActionVarKey = SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
//            private var replaceVariableMapCon = String()

//            fun initBeforeActionImportMap(
//                setReplaceVariableMap: Map<String, String>?
//            ){
////                beforeActionImportMap.clear()
//                replaceVariableMapCon =
//                    makeSetRepValeMapCon(setReplaceVariableMap)
//            }

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


            suspend fun import(
                context: Context?,
                fannelInfoMap: Map<String, String>,
                setReplaceVariableMap: Map<String, String>?,
                keyToSubKeyCon: Pair<String, String>?,
            ): Pair<String, List<Pair<String, String>>> {
                val keyToSubKeyContents = listOf(
                    settingActionVarKey,
                    keyToSubKeyCon?.second ?: String()
                ).joinToString("=")
                val actionImportMap = ImportMapMaker.comp(
                    keyToSubKeyContents,
                    "${settingActionVarKey}="
                )
                val renewalVarName = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        settingActionVarKey
                    )
                )
                val judgeTargetStr = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.S_IF.key
                    )
                )
                val argsPairList = CmdClickMap.createMap(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.ARGS.key
                    ),
                    valueSeparator
                )
                val isImport = when(judgeTargetStr.isEmpty()) {
                    true -> true
                    else -> SettingIfManager.handle(
                        judgeTargetStr,
                        argsPairList
                    )
                }
                if(
                    isImport != true
                ) return String() to emptyList()
                val importPathSrc = QuoteTool.trimBothEdgeQuote(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.IMPORT_PATH.key
                    )
                )
                val importSrcConBeforeReplace = makeActionImportSrcCon(
                    context,
                    importPathSrc,
                    fannelInfoMap,
                    setReplaceVariableMap,
                )
                val repMap = makeRepValHolderMap(
                    actionImportMap.get(
                        SettingActionKeyManager.ActionImportManager.ActionImportKey.REPLACE.key
                    )
                )
                val importSrcCon = CmdClickMap.replaceHolderForJsAction(
                    importSrcConBeforeReplace,
                    repMap
                )
                return renewalVarName to KeyToSubKeyMapListMaker.make(
                    importSrcCon,
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

            private suspend fun makeActionImportSrcCon(
                context: Context?,
                importPath: String,
//        currentAppDirPath: String,
                fannelInfoMap: Map<String, String>,
                setReplaceVariableMap: Map<String, String>?,
            ): String {
                val beforeActionImportSrcCon = withContext(Dispatchers.IO) {
                    BeforeActionImportMapManager.get(importPath)
                }
                if(
                    !beforeActionImportSrcCon.isNullOrEmpty()
                ) {
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport_first_makeActionImportSrcCon.txt").absolutePath,
//                        listOf(
//                            "importPath: ${importPath}",
//                            "beforeActionImportSrcCon: ${beforeActionImportSrcCon}",
//                        ).joinToString("\n\n") + "\n\n========\n\n"
//                    )
                    return beforeActionImportSrcCon
                }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath,"jsAcImport_first_makeActionImportSrcCon.txt").absolutePath,
//            listOf(
//                "importPath: ${importPath}",
//                "beforeActionImportSrcCon: ${beforeActionImportSrcCon}",
//            ).joinToString("\n\n") + "\n-----\n"
//        )
                val currentFannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
                val actionImportSrcCon = SettingFile.read(
                    context,
                    importPath,
                    File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath,
                    setReplaceVariableMap,
                )
                withContext(Dispatchers.IO) {
                    BeforeActionImportMapManager.put(
                        importPath,
                        actionImportSrcCon,
                    )
                }
                return actionImportSrcCon
            }
        }

        private class SettingVar {

            private val escapeRunPrefix = "run"
            private val itPronoun = "it"
            private val itReplaceVarStr = "${'$'}{${itPronoun}}"
            private var itPronounValue = String()
            private var isNext = true
            private val valueSeparator = SettingActionKeyManager.valueSeparator

            fun exec(
                context: Context?,
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
                            itPronounValue = mainSubKeyMap.get(mainSubKey)
                                ?: String()
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
                            itPronounValue = SettingFuncManager.handle(
                                context,
                                funcTypeDotMethod,
                                argsPairList,
                                busyboxExecutor
                            ) ?: String()
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
                val isEscape =
                    settingVarName.startsWith(escapeRunPrefix)
                            && itPronounValue != SettingActionKeyManager.CommandMacro.EXIT_SIGNAL.name
                return when(isEscape){
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
        keyToSubKeyCon: String?,
    ): List<Pair<String, String>> {
        val keyToSubKeyConListSrc = makeKeyToSubConPairListByValidKey(
            keyToSubKeyCon
        )
        val keyToSubKeyConListByValidKey =
            filterByValidKey(keyToSubKeyConListSrc)
        return keyToSubKeyConListByValidKey
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
