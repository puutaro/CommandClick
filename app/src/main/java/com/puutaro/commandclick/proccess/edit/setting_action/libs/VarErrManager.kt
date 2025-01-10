package com.puutaro.commandclick.proccess.edit.setting_action.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.runBlocking

object VarErrManager {

    private val mainKeySeparator = SettingActionKeyManager.mainKeySeparator
    private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
    private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
    private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
    private val settingReturnKey =
        SettingActionKeyManager.SettingActionsKey.SETTING_RETURN.key
    private val globalVarNameRegex =
        SettingActionKeyManager.globalVarNameRegex

    private fun makeKeyToSubKeyListCon(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): String {
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val imageKey = keyToSubKeyCon.first
            val subKeyCon = keyToSubKeyCon.second
            "${mainKeySeparator}${imageKey}=${subKeyCon}"
        }.joinToString("\n")
    }

    fun isSameVarNameErr(
        context: Context?,
        settingKeyToVarNameList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val varNameList = settingKeyToVarNameList.filter {
                settingKeyToVarName ->
            settingKeyToVarName.first !=
                    settingReturnKey
        }.map {
            it.second
        }
        val duplicateVarNameMap =
            varNameList.groupingBy { it }.eachCount().filterValues { it > 1 }
        val duplicateVarNameList = duplicateVarNameMap.keys
        val isDuplicate = duplicateVarNameList.isNotEmpty()
        if(
            !isDuplicate
        ) return false
        val spanVarNameListCon =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                duplicateVarNameList.joinToString(". ")
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_VAR,
                "Var name must be unique: ${spanVarNameListCon}",
                keyToSubKeyConWhere,
            )
        }
        return true

    }

    fun isIrregularVarNameErr(
        context: Context?,
        settingKeyToVarNameListSrc: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val varMarkRegex = Regex("^[a-zA-Z0-9_]+$")
        val settingKeyToVarNameList = settingKeyToVarNameListSrc.filter {
                settingKeyToVarName ->
            settingKeyToVarName.first !=
                    settingReturnKey
        }
        settingKeyToVarNameList.forEach {
                settingKeyToVarName ->
            val varMarkEntry = settingKeyToVarName.second
            if(
                varMarkRegex.matches(varMarkEntry)
            ) return@forEach
            val settingKey = settingKeyToVarName.first
            val spanSettingKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    settingKey
                )
            val spanVarMarkEntry =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    varMarkEntry
                )
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_VAR,
                    "${spanSettingKey} key value must be variable( [a-zA-Z0-9_]+ ): ${spanVarMarkEntry}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isGlobalVarNullResultErr(
        context: Context?,
        varName: String,
        returnValueStr: String?,
        settingSubKey: SettingActionKeyManager.SettingSubKey,
        keyToSubKeyConWhere: String,
    ): Boolean {
        if(
            returnValueStr != null
        ) return false
        val isGlobal = globalVarNameRegex.matches(varName)
        if(
            !isGlobal
        ) return false
        val spanVarName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                varName
            )
        val spanSubKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                settingSubKey.key
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_VAR,
                "In global (uppercase), ${spanSubKeyName} result must be exist: ${spanVarName}",
                keyToSubKeyConWhere,
            )
        }
        return true

    }

    fun isAwaitNotDefiniteAsyncVarErr(
        context: Context?,
        settingKeyAndAsyncVarNameToAwaitVarNameList: List<
                Pair<
                        String,
                        Pair<
                                String,
                                List<String>?
                                >
                        >
                >,
        keyToSubKeyConWhere: String,
    ): Boolean {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyAndAsyncVarNameToAwaitVarNameList: ${settingKeyAndAsyncVarNameToAwaitVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
        val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
        settingKeyAndAsyncVarNameToAwaitVarNameList.forEachIndexed {
                index, settingKeyToAsyncVarNames ->
            val beforeSettingKeyAndAsyncVarNameToAwaitVarNameList =
                settingKeyAndAsyncVarNameToAwaitVarNameList.filterIndexed { innerIndex, pair ->
                    innerIndex < index
                }
            val beforeAsyncVarNameCon = beforeSettingKeyAndAsyncVarNameToAwaitVarNameList.map {
                val asyncVarName = it.second.first
                asyncVarName
            }.joinToString("\n")
            val curSettingKeyToAwaitVarNameList =
                settingKeyToAsyncVarNames.second
            val settingKey = curSettingKeyToAwaitVarNameList.first
            val awaitVarNameList =
                curSettingKeyToAwaitVarNameList.second
            awaitVarNameList?.forEach {
                    awaitVarName ->
                if(
                    beforeAsyncVarNameCon.contains(awaitVarName)
                ) return@forEach
                val spanSettingKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        settingKey
                    )
                val spanAwaitKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        awaitKey
                    )
                val spanAwaitVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        awaitVarName
                    )
                runBlocking {
                    SettingActionErrLogger.sendErrLog(
                        context,
                        SettingActionErrLogger.SettingActionErrType.S_VAR,
                        "Not before definite ${spanAwaitKeyName} var name: ${spanAwaitVarName}, var name: settingKeyName: ${spanSettingKeyName}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }
        }
        return false
    }

    fun isNotAwaitAsyncVarErrOrAwaitInAsyncVarErr(
        context: Context?,
        keyToSubKeyConList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {

        val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
        val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
        val subKeySeparator =
            SettingActionKeyManager.subKeySepartor

        keyToSubKeyConList.forEach {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                settingKey == settingReturnKey
            ) return@forEach
            val asyncVarNameEntry =
                keyToSubKeyCon.second
                    .split(subKeySeparator)
                    .firstOrNull()
                    ?: return@forEach
            if(
                !asyncVarNameEntry.startsWith(asyncPrefix)
            ) return@forEach
            val asyncVarMark = "${'$'}{${asyncVarNameEntry}}"
            val indexToSubKeyConWithVarMark = let indexToSubKeyConWithVarMark@ {
                keyToSubKeyConList.forEachIndexed findindexToSubKeyConWithVarMark@ { index, innerKeyToSubKeyCon ->
                    val subKeyCon = innerKeyToSubKeyCon.second
                    if (
                        !subKeyCon.contains(asyncVarMark)
                    ) return@findindexToSubKeyConWithVarMark
                    return@indexToSubKeyConWithVarMark index to innerKeyToSubKeyCon
                }
                null
            } ?: return@forEach
            val indexToSubKeyPairListWithAwait = let indexToSubKeyPairListWithAwait@ {
                keyToSubKeyConList.forEachIndexed findIndexToSubKeyPairListWithAwait@ { index, innerKeyToSubKeyCon ->
                    val subKeyCon = innerKeyToSubKeyCon.second
                    val subKeyPairList = CmdClickMap.createMap(
                        subKeyCon,
                        subKeySeparator
                    )
                    subKeyPairList.forEach execIndexToSubKeyPairListWithAwait@ {
                            subKeyToCon ->
                        if(
                            subKeyToCon.first != awaitKey
                        ) return@execIndexToSubKeyPairListWithAwait
                        val awaitVarNameList =
                            SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                                subKeyToCon.second
                            )
                        if(
                            !awaitVarNameList.contains(asyncVarNameEntry)
                        ) return@execIndexToSubKeyPairListWithAwait
                        return@indexToSubKeyPairListWithAwait index to subKeyPairList
                    }
//                            val awaitVarNameList = subKeyPairList.firstOrNull {
//                                subKeyToCon ->
//                                subKeyToCon.first == awaitKey
//                            }?.second?.let {
//                                ImageActionKeyManager.AwaitManager.getAwaitVarNameList(it)
//                            } ?: return@findIndexToSubKeyPairListWithAwait
//                            if(
//                                !awaitVarNameList.contains(asyncVarNameEntry)
//                            ) return@findIndexToSubKeyPairListWithAwait
//                            return@indexToSubKeyPairListWithAwait index to subKeyPairList
                }
                null
            }
            if(indexToSubKeyPairListWithAwait == null){
                val spanSettingKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        settingKey
                    )
                val spanAwaitKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        awaitKey
                    )
                val spanAwaitAsyncVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        asyncVarNameEntry
                    )
                val spanSettingReturnKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        settingReturnKey
                    )
                val spanAsyncPrefix =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        asyncPrefix
                    )
                runBlocking {
                    SettingActionErrLogger.sendErrLog(
                        context,
                        SettingActionErrLogger.SettingActionErrType.S_VAR,
                        "Not ${spanAwaitKeyName} async var: ${spanAwaitAsyncVarName} or forbidden ${spanAsyncPrefix} prefix var name in ${spanSettingReturnKey}, settingKeyName: ${spanSettingKeyName}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }
            val varMarkIndex = indexToSubKeyConWithVarMark.first
            val awaitVarNameIndex = indexToSubKeyPairListWithAwait.first
            if(
                varMarkIndex != awaitVarNameIndex
            ) {
                val spanSettingKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        settingKey
                    )
                val spanAwaitKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        awaitKey
                    )
                val spanAwaitAsyncVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        asyncVarNameEntry
                    )
                runBlocking {
                    SettingActionErrLogger.sendErrLog(
                        context,
                        SettingActionErrLogger.SettingActionErrType.S_VAR,
                        "Must ${spanAwaitKeyName} in using async var: ${spanAwaitAsyncVarName}, settingKeyName: ${spanSettingKeyName}",
                        keyToSubKeyConWhere,
                    )
                }
                return true
            }
            val isNotAwaitBeforeAsyncVarErr = let isNotAwaitBeforeAsyncVarErr@ {
                val subKeyPairList = indexToSubKeyPairListWithAwait.second
                val varMarkIndexInEqual = subKeyPairList.indexOfFirst {
                        subKeyPair ->
                    subKeyPair.second.contains(asyncVarMark)
                }
                val awaitVarNameIndexInEqual = subKeyPairList.indexOfFirst {
                        subKeyPair ->
                    if(
                        subKeyPair.first != awaitKey
                    ) return@indexOfFirst false
                    SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                        subKeyPair.second
                    ).contains(asyncVarNameEntry)
                }
                varMarkIndexInEqual <= awaitVarNameIndexInEqual
            }
            if(!isNotAwaitBeforeAsyncVarErr) return@forEach
            val spanSettingKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    settingKey
                )
            val spanAwaitKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    awaitKey
                )
            val spanAwaitAsyncVarName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    asyncVarNameEntry
                )
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_VAR,
                    "Must not ${spanAwaitKeyName} after in using async var: ${spanAwaitAsyncVarName}, settingKeyName: ${spanSettingKeyName}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isAwaitNotAsyncVarErr(
        context: Context?,
        settingKeyAndAsyncVarNameToAwaitVarNameList: List<
                Pair<
                        String,
                        Pair<
                                String,
                                List<String>?
                                >
                        >
                >,
        keyToSubKeyConWhere: String,
    ): Boolean {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyAndAsyncVarNameToAwaitVarNameList: ${settingKeyAndAsyncVarNameToAwaitVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
        val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
        val asyncPrefixList = listOf(
            asyncPrefix,
            runAsyncPrefix,
        )
        settingKeyAndAsyncVarNameToAwaitVarNameList.forEachIndexed {
                index, settingKeyToAsyncVarNames ->
            val curSettingKeyToAwaitVarNameList =
                settingKeyToAsyncVarNames.second
            val settingKey = curSettingKeyToAwaitVarNameList.first
            val awaitVarNameList = let {
                curSettingKeyToAwaitVarNameList.second
            }?: return@forEachIndexed
            val notAsyncVarNameList = awaitVarNameList.filter {
                    awaitVarName ->
                !asyncPrefixList.any{
                    awaitVarName.startsWith(it)
                }
            }
            if(
                notAsyncVarNameList.isEmpty()
            ) return@forEachIndexed
            val spanSettingKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    settingKey
                )
            val spanAwaitKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    awaitKey
                )
            val spanAwaitVarNameListCon =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    notAsyncVarNameList.joinToString(",")
                )
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_VAR,
                    "In ${spanAwaitKeyName}, must async var name: ${spanAwaitVarNameListCon}: settingKeyName: ${spanSettingKeyName}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isAwaitDuplicateAsyncVarErr(
        context: Context?,
        settingKeyAndAsyncVarNameToAwaitVarNameList: List<
                Pair<
                        String,
                        Pair<
                                String,
                                List<String>?
                                >
                        >
                >,
        keyToSubKeyConWhere: String,
    ): Boolean {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyAndAsyncVarNameToAwaitVarNameList: ${settingKeyAndAsyncVarNameToAwaitVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
        val awaitKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
        settingKeyAndAsyncVarNameToAwaitVarNameList.forEachIndexed {
                index, settingKeyToAsyncVarNames ->
            val curSettingKeyToAwaitVarNameList =
                settingKeyToAsyncVarNames.second
            val settingKey = curSettingKeyToAwaitVarNameList.first
            val awaitVarNameList = let {
                curSettingKeyToAwaitVarNameList.second
            }?: return@forEachIndexed
            val duplicateAsyncVarNameList = awaitVarNameList.groupBy { it }
                .mapValues { it.value.size }
                .filterKeys { it.isNotEmpty() }
                .filter {
                    it.value > 1
                }.map {
                    it.key
                }
            if(
                duplicateAsyncVarNameList.isEmpty()
            ) return@forEachIndexed
            val spanSettingKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    settingKey
                )
            val spanAwaitKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    awaitKey
                )
            val spanAwaitVarNameListCon =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    duplicateAsyncVarNameList.joinToString(",")
                )
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_VAR,
                    "Duplicate ${spanAwaitKeyName} var name: ${spanAwaitVarNameListCon}: settingKeyName: ${spanSettingKeyName}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isNotExistReturnOrFuncOrValue(
        context: Context?,
        keyToSubKeyConList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val settingVarMainKey = SettingActionKeyManager.SettingActionsKey.SETTING_VAR.key
        val funcKey = SettingActionKeyManager.SettingSubKey.FUNC.key
        val onReturnKey = SettingActionKeyManager.SettingSubKey.ON_RETURN.key
        val valueKey = SettingActionKeyManager.SettingSubKey.VALUE.key
        val funcKeyRegex = Regex("\\?${funcKey}=")
        val returnKeyRegex = Regex("\\?${onReturnKey}=")
        val valueKeyRegex = Regex("\\?${valueKey}=")
        keyToSubKeyConList.forEach {
                settingKeyToSubKeyCon ->
            val settingKeyName = settingKeyToSubKeyCon.first
            if(
                settingKeyName != settingVarMainKey
            ) return@forEach
            val subKeyCon = settingKeyToSubKeyCon.second
            val isContainFuncOrReturnOrValue = funcKeyRegex.containsMatchIn(subKeyCon)
                    || returnKeyRegex.containsMatchIn(subKeyCon)
                    || valueKeyRegex.containsMatchIn(subKeyCon)
            if(
                isContainFuncOrReturnOrValue
            ) return@forEach
//                    val spanSettingKeyName =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errRedCode,
//                            settingKeyName
//                        )
            val spanSubKeyCon =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    subKeyCon
                )
            val spanImageVarMainKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    settingVarMainKey
                )
            val spanFuncKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    funcKey
                )
            val spanOnReturnKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    onReturnKey
                )
            val spanOnValueKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    valueKey
                )
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_VAR,
                    "${spanFuncKey} or ${spanOnReturnKey} or ${spanOnValueKey} sub key must use in ${spanImageVarMainKey} settingKey: subKeyCon: ${spanSubKeyCon}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isRunPrefixVarUseErr(
        context: Context?,
        keyToSubKeyConList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val settingKeyToRunVarNameList = makeSettingKeyToPrivateRunVarNameList(
            keyToSubKeyConList
        )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sContetn00.txt").absolutePath,
//                    listOf(
//                        "settingKeyToRunVarNameList: ${settingKeyToRunVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
        val keyToSubKeyListCon = makeKeyToSubKeyListCon(
            keyToSubKeyConList,
        )
        settingKeyToRunVarNameList.forEach {
                settingKeyToVarName ->
            val runVarName = settingKeyToVarName.second
            val varNameWithDoll = "${'$'}{${runVarName}}"
            val isContain = keyToSubKeyListCon.contains(varNameWithDoll)
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "sContetn.txt").absolutePath,
//                        listOf(
//                            "settingKeyToRunVarNameList: ${settingKeyToRunVarNameList}",
//                            "varNameWithDoll: ${varNameWithSharp}",
//                            "keyToSubKeyListCon: ${keyToSubKeyListCon}",
//                            "isContain: ${isContain}",
//                        ).joinToString("\n\n") + "\n\n=============\n\n"
//                    )
            if(
                !isContain
            ) return@forEach
            val settingKeyName = settingKeyToVarName.first
            val spanSettingKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    settingKeyName
                )
            val spanRunVarName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    runVarName
                )
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_VAR,
                    "Forbidden to use run prefix var as variable:  ${spanSettingKeyName}: ${spanRunVarName}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isNotUseVarErr(
        context: Context?,
        settingKeyToPrivateVarNameList: List<Pair<String, String>>,
        keyToSubKeyConList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val keyToSubKeyListCon = makeKeyToSubKeyListCon(
            keyToSubKeyConList,
        )
        execIsNotUseVarErr(
            context,
            settingKeyToPrivateVarNameList,
            keyToSubKeyListCon,
            keyToSubKeyConWhere,
        ).let {
                isNotUseVarErr ->
            if(isNotUseVarErr) return true
        }
        return false
    }


    fun isBlankSVarOrSAcVarErr(
        context: Context?,
        settingKeyToPrivateVarNameList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val settingKeyToBlankVarNamePair = settingKeyToPrivateVarNameList.filter {
                settingKeyToPrivateVarName ->
            settingKeyToPrivateVarName.first != settingReturnKey
        }.firstOrNull {
            val varName = it.second
            varName.isEmpty()
        }
        if(
            settingKeyToBlankVarNamePair == null
        ) return false
        val spanBlankSettingKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                settingKeyToBlankVarNamePair.first
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_VAR,
                "${spanBlankSettingKeyName} must not blank",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

    fun isNotDefinitionVarErr(
        context: Context?,
        settingKeyToNoRunVarNameList: List<Pair<String, String>>,
        keyToSubKeyConList: List<Pair<String, String>>,
        stringVarKeyList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val regexStrTemplate = "([$][{]%s[}])"
//                val returnUseStringKeyList = settingKeyToNoRunVarNameList.filter {
//                        settingKeyToNoRunVarName ->
//                    settingKeyToNoRunVarName.first == settingReturnKey
//                }.map {
//                    it.second
//                }
        val stringKeyList = let {
            val stringKeyListInCode = settingKeyToNoRunVarNameList.filter {
                    settingKeyToNoRunVarName ->
                settingKeyToNoRunVarName.first !=
                        settingReturnKey
            }.map {
                it.second
            }
            val plusStringKeyList = stringVarKeyList?.filter {
                !stringKeyListInCode.contains(it)
            } ?: emptyList()
            stringKeyListInCode +
                    plusStringKeyList +
                    listOf(SettingActionKeyManager.ValueStrVar.itPronoun)
        }
        val settingKeyListConRegex = let {
            stringKeyList.map {
                regexStrTemplate.format(it)
            }.joinToString("|")
        }.toRegex()
        val keyToSubKeyListCon = makeKeyToSubKeyListCon(
            keyToSubKeyConList,
        )
        val keyToSubKeyListConWithRemoveVar = keyToSubKeyListCon.replace(
            settingKeyListConRegex,
            String()
        )
//                val notDefinitionStringKeyInReturn = returnUseStringKeyList.firstOrNull {
//                    !stringKeyList.contains(it)
//                }
        val findVarMarkRegex = Regex(".[$][{][a-zA-Z0-9_]+[}]")
        val leaveVarMarkList =
            findVarMarkRegex
                .findAll(keyToSubKeyListConWithRemoveVar).filter {
                        varMarkResult ->
                    val varMark = varMarkResult.value
                    !varMark.startsWith("\\")
                }.map {
                    it.value.replace(
                        Regex("^[^$]"),
                        String()
                    )
                }
        if(
            !leaveVarMarkList.any()
        ) return false
//                        ?: return false
//                    notDefinitionStringKeyInReturn
//                        ?: findVarMarkRegex
//                            .find(keyToSubKeyListConWithRemoveVar)
//                            ?.value
//                        ?: return false
        val spanLeaveVarMark =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                leaveVarMarkList.first()
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_VAR,
                "Not definition var: ${spanLeaveVarMark}",
                keyToSubKeyConWhere,
            )
        }
//
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisNotReplaceVarErr.txt").absolutePath,
//                    listOf(
//                        "settingKeyToNoRunVarNameList: ${settingKeyToNoRunVarNameList}",
//                        "keyToSubKeyConList: ${keyToSubKeyConList}",
//                        "keyToSubKeyListConWithRemoveVar: ${keyToSubKeyListConWithRemoveVar}",
//                        "settingKeyListConRegex: ${settingKeyListConRegex}",
//                        "leaveVarMark: ${leaveVarMark}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
        return true
    }

    fun isShadowTopLevelVarErr(
        context: Context?,
        settingKeyToNoRunVarNameList: List<Pair<String, String>>,
        topLevelValueStrKeyList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val returnTopAcVarNameMacro = SettingActionKeyManager.returnTopAcVarNameMacro
        val shadowSettingKeyToNoRunVarName = settingKeyToNoRunVarNameList.filter {
                settingKeyToNoRunVarName ->
            settingKeyToNoRunVarName.first != settingReturnKey
        }.firstOrNull {
                settingKeyToNoRunVarName ->
            val noRunVarName = settingKeyToNoRunVarName.second
            if(
                noRunVarName == returnTopAcVarNameMacro
            ) return@firstOrNull false
            topLevelValueStrKeyList?.contains(
                noRunVarName
            ) == true
        }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisShadowTopLevelVarErr.txt").absolutePath,
//                    listOf(
//                        "settingKeyToNoRunVarNameList: ${settingKeyToNoRunVarNameList}",
//                        "shadowSettingKeyToNoRunVarName: ${shadowSettingKeyToNoRunVarName}",
//                    ).joinToString("\n\n") + "\n\n=============\n\n"
//                )
        if(
            shadowSettingKeyToNoRunVarName == null
        ) return false
        val spanSettingKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            shadowSettingKeyToNoRunVarName.first
        )
        val spanShadowTopLevelVarKey =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                shadowSettingKeyToNoRunVarName.second
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_VAR,
                "shaddow top level var: ${spanShadowTopLevelVarKey} setting key ${spanSettingKey}",
                keyToSubKeyConWhere,
            )
        }

        return true
    }

    private fun execIsNotReplaceVarErr(
        context: Context?,
        subKeyListCon: String,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val replaceKeyToSubKeyListCon = subKeyListCon.replace(
            "${'$'}{${SettingActionKeyManager.ValueStrVar}}",
            String()
        )
//                varNameList.forEach {
//                        varName ->
//                    if(
//                        varName.startsWith(escapeRunPrefix)
//                    ) return@forEach
//                    val varNameWithDoll = "${'$'}{${varName}}"
//                    replaceKeyToSubKeyListCon = replaceKeyToSubKeyListCon.replace(
//                        varNameWithDoll,
//                        String()
//                    )
//                }
        val dolVarMarkRegex = Regex("[$][{][a-zA-Z0-9_]+[}]")
        val noReplaceVar = dolVarMarkRegex.find(replaceKeyToSubKeyListCon)?.value
        if(
            noReplaceVar.isNullOrEmpty()
            || noReplaceVar.startsWith("${'$'}{${escapeRunPrefix}")
        ) return false
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sexecIsNotReplaceVarErr.txt").absolutePath,
//                    listOf(
//                        "subKeyListCon: ${subKeyListCon}",
//                        "replaceKeyToSubKeyListCon: ${replaceKeyToSubKeyListCon}",
//                        "noReplaceVar: ${noReplaceVar}",
//                    ).joinToString("\n")
//                )
        val spanNoReplaceVar =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                noReplaceVar
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_VAR,
                "Not replace var: ${spanNoReplaceVar}",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

    private fun execIsNotUseVarErr(
        context: Context?,
        settingKeyToPrivateVarNameList: List<Pair<String, String>>,
        keyToSubKeyListCon: String,
        keyToSubKeyConWhere: String,
    ): Boolean {
        settingKeyToPrivateVarNameList.filter {
                settingKeyToPrivateVarName ->
            settingKeyToPrivateVarName.first != settingReturnKey
        }.forEach {
                settingKeyToVarName ->
            val varName = settingKeyToVarName.second
            if(
                varName.startsWith(escapeRunPrefix)
            ) return@forEach
            val varNameWithDoll = "${'$'}{${varName}}"
            val isContain =
                keyToSubKeyListCon.contains(varNameWithDoll)
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "sLog.txt").absolutePath,
//                        listOf(
//                            "varNameRegex: ${varNameWithDoll}",
//                            "keyToSubKeyListCon: ${keyToSubKeyListCon}"
//                        ).joinToString("\n\n") + "\n\n===========n\n\n"
//                    )
            if(isContain) return@forEach
            val settingKeyName = settingKeyToVarName.first
            val spanSettingKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    settingKeyName
                )
            val spanVarName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    varName
                )
            runBlocking {
//                        FileSystems.writeFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lsVar.txt").absolutePath,
//                            listOf(
//                                "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                                "keyToSubKeyListCon: ${keyToSubKeyListCon}",
//                            ).joinToString("\n")
//                        )
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_VAR,
                    "Not use private ${spanSettingKeyName}: ${spanVarName}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun makeSettingKeyToPrivateVarNameList(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = SettingActionKeyManager.subKeySepartor
        val settingKeyList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
                it.key
            }
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                !settingKeyList.contains(settingKey)
            ) return@map defaultReturnPair
            val varName = keyToSubKeyCon.second
                .split(subKeySeparator)
                .firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@map defaultReturnPair
            val isPrivateVarName = !globalVarNameRegex.matches(varName)
            when(isPrivateVarName){
                false -> String() to String()
                else -> settingKey to varName
            }
        }.filter {
            it.first.isNotEmpty()
                    && it.second.isNotEmpty()
        }.let {
            SettingActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(it)
        }
    }

    fun makeSettingKeyToNoRunVarNameList(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = SettingActionKeyManager.subKeySepartor
        val settingKeyList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
                it.key
            }
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                !settingKeyList.contains(settingKey)
            ) return@map defaultReturnPair
            val varName = keyToSubKeyCon.second
                .split(subKeySeparator)
                .firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@map defaultReturnPair
            if(
                varName.startsWith(escapeRunPrefix)
            ){
                return@map String() to String()
            }
            settingKey to varName
        }.filter {
            it.first.isNotEmpty()
                    && it.second.isNotEmpty()
        }.let {
            SettingActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(
                it
            )
        }
    }

    private fun makeSettingKeyToPrivateVarNameListWithBlank(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = SettingActionKeyManager.subKeySepartor
        val imageActionKeyList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
                it.key
            }
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                !imageActionKeyList.contains(settingKey)
            ) return@map defaultReturnPair
            val varName = keyToSubKeyCon.second.split(subKeySeparator).firstOrNull()?.let {
                QuoteTool.trimBothEdgeQuote(it)
            } ?: String()
            settingKey to varName
        }.filter {
            it.first.isNotEmpty()
        }
    }

    fun makeSettingKeyToSubKeyMap(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, Pair<String, List<String>?>>> {
        val defaultReturnPair =
            String() to Pair(String(), null)
        val subKeySeparator = SettingActionKeyManager.subKeySepartor
        val imageActionKeyList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
                it.key
            }
        val asyncVarPrefixList = listOf(
            asyncPrefix,
            runAsyncPrefix,
        )
        val awaitSubKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                !imageActionKeyList.contains(settingKey)
            ) return@map defaultReturnPair
            val varNameAndSubKeyConList =
                keyToSubKeyCon.second
                    .split(subKeySeparator)
            val varName = varNameAndSubKeyConList
                .firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: String()
            val isAsyncVarName = asyncVarPrefixList.any {
                varName.startsWith(it)
            }
            val awaitVarNameList = varNameAndSubKeyConList
                .filterIndexed { index, s ->
                    index > 0
                }.joinToString(subKeySeparator.toString()).let {
                    CmdClickMap.createMap(
                        it,
                        subKeySeparator
                    ).toMap().get(awaitSubKey)
                }?.let {
                    SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                        it,
                    )
                }
            if(
                !isAsyncVarName
                && awaitVarNameList.isNullOrEmpty()
            ) return@map defaultReturnPair
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "iaa.txt").absolutePath,
//                        listOf(
//                            "keyToSubKeyCon: ${keyToSubKeyCon}",
//                            "varName: ${varName}",
//                            "isAsyncVarName: ${isAsyncVarName}",
//                            "awaitVarNameList: ${awaitVarNameList}",
//                        ).joinToString("\n") + "\n\n=========\n\n"
//                    )
            settingKey to Pair(
                varName,
                awaitVarNameList
            )
        }.filter {
            val settingKey = it.first
            val varNameToAwaitVarList = it.second
            val varName = varNameToAwaitVarList.first
            settingKey.isNotEmpty()
                    && varName.isNotEmpty()

        }
    }

    fun makeSettingKeyAndAsyncVarNameToAwaitVarNameList(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, Pair<String, List<String>?>>> {
        val defaultReturnPair =
            String() to Pair(String(), null)
        val subKeySeparator = SettingActionKeyManager.subKeySepartor
        val imageActionKeyList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
                it.key
            }
        val asyncVarPrefixList = listOf(
            asyncPrefix,
            runAsyncPrefix,
        )
        val awaitSubKey = SettingActionKeyManager.SettingSubKey.AWAIT.key
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                !imageActionKeyList.contains(settingKey)
            ) return@map defaultReturnPair
            val varNameAndSubKeyConList =
                keyToSubKeyCon.second
                    .split(subKeySeparator)
            val varName = varNameAndSubKeyConList
                .firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: String()
            val isAsyncVarName = asyncVarPrefixList.any {
                varName.startsWith(it)
            }
            val awaitVarNameList = varNameAndSubKeyConList
                .filterIndexed { index, s ->
                    index > 0
                }.joinToString(
                    subKeySeparator.toString()
                ).let {
                    CmdClickMap.createMap(
                        it,
                        subKeySeparator
                    ).map awaitVarNameList@ {
                            subKeyToCon ->
                        if(
                            subKeyToCon.first != awaitSubKey
                        ) return@awaitVarNameList emptyList()
                        SettingActionKeyManager.AwaitManager.getAwaitVarNameList(
                            subKeyToCon.second,
                        )
                    }.flatten()
                    //..get(awaitSubKey)
                }
            if(
                !isAsyncVarName
                && awaitVarNameList.isEmpty()
            ) return@map defaultReturnPair
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "iaa.txt").absolutePath,
//                        listOf(
//                            "keyToSubKeyCon: ${keyToSubKeyCon}",
//                            "varName: ${varName}",
//                            "isAsyncVarName: ${isAsyncVarName}",
//                            "awaitVarNameList: ${awaitVarNameList}",
//                        ).joinToString("\n") + "\n\n=========\n\n"
//                    )
            settingKey to Pair(
                varName,
                awaitVarNameList
            )
        }.filter {
            val settingKey = it.first
            val varNameToAwaitVarList = it.second
            val varName = varNameToAwaitVarList.first
            settingKey.isNotEmpty()
                    && varName.isNotEmpty()

        }
    }

    private fun makeSettingKeyToPrivateRunVarNameList(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = SettingActionKeyManager.subKeySepartor
        val imageActionKeyList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
                it.key
            }
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                !imageActionKeyList.contains(settingKey)
            ) return@map defaultReturnPair
            val varName = keyToSubKeyCon.second
                .split(subKeySeparator)
                .firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@map defaultReturnPair
            val isRunVar = varName.startsWith(escapeRunPrefix)
            when(isRunVar){
                false -> String() to String()
                else -> settingKey to varName
            }
        }.filter {
            it.first.isNotEmpty()
                    && it.second.isNotEmpty()
        }.let {
            SettingActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(
                it
            )
        }
    }
}