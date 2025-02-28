package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.ImageVarMarkTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.SpeedReplacer
import com.puutaro.commandclick.util.str.VarMarkTool
import kotlinx.coroutines.runBlocking

object ImageActionVarErrManager {
    private val mainKeySeparator = ImageActionKeyManager.mainKeySeparator
    private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
    private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
    private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
    private val imageReturnKey =
        ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN.key
//    private val globalVarNameRegex = ImageActionKeyManager.globalVarNameRegex

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
                settingKeyToBitmapKey ->
            settingKeyToBitmapKey.first !=
                    imageReturnKey
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
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_VAR,
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
//        val varMarkRegex = Regex("^[a-zA-Z0-9_]+$")
        val settingKeyToVarNameList = settingKeyToVarNameListSrc.filter {
                settingKeyToVarName ->
            settingKeyToVarName.first !=
                    imageReturnKey
        }
        settingKeyToVarNameList.forEach {
                settingKeyToVarName ->
            val varMarkEntry = settingKeyToVarName.second
            if(
                VarMarkTool.matchStringVarBodyAlphaNum(varMarkEntry)
//                varMarkRegex.matches(varMarkEntry)
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
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
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
        returnBitmap: Bitmap?,
        imageAcSubKey: ImageActionKeyManager.ImageSubKey,
        keyToSubKeyConWhere: String,
    ): Boolean {
        if(
            returnBitmap != null
        ) return false
        val isGlobal =
            VarMarkTool.matchesUpperAlphNumOrUnderscore(varName)
            //globalVarNameRegex.matches(varName)
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
                imageAcSubKey.key
            )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_VAR,
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
        val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                        CheckTool.lightBlue,
                        settingKey
                    )
                val spanAwaitKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        awaitKey
                    )
                val spanAwaitVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        awaitVarName
                    )
                runBlocking {
                    ImageActionErrLogger.sendErrLog(
                        context,
                        ImageActionErrLogger.ImageActionErrType.I_VAR,
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

        val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
        val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
        val subKeySeparator =
            ImageActionKeyManager.subKeySepartor

        keyToSubKeyConList.forEach {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                settingKey == imageReturnKey
            ) return@forEach
            val asyncVarNameEntry =
                keyToSubKeyCon.second
                    .split(subKeySeparator)
                    .firstOrNull()
                    ?: return@forEach
            if(
                !asyncVarNameEntry.startsWith(asyncPrefix)
            ) return@forEach
            val asyncVarMark = "#{${asyncVarNameEntry}}"
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
                        val awaitVarNameList = ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
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
                        CheckTool.lightBlue,
                        settingKey
                    )
                val spanAwaitKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        awaitKey
                    )
                val spanAwaitAsyncVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        asyncVarNameEntry
                    )
                runBlocking {
                    ImageActionErrLogger.sendErrLog(
                        context,
                        ImageActionErrLogger.ImageActionErrType.I_VAR,
                        "Not ${spanAwaitKeyName} async var: ${spanAwaitAsyncVarName}, settingKeyName: ${spanSettingKeyName}",
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
                        CheckTool.lightBlue,
                        settingKey
                    )
                val spanAwaitKeyName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        awaitKey
                    )
                val spanAwaitAsyncVarName =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        asyncVarNameEntry
                    )
                runBlocking {
                    ImageActionErrLogger.sendErrLog(
                        context,
                        ImageActionErrLogger.ImageActionErrType.I_VAR,
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
                    ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
                        subKeyPair.second
                    ).contains(asyncVarNameEntry)
                }
                varMarkIndexInEqual <= awaitVarNameIndexInEqual
            }
            if(!isNotAwaitBeforeAsyncVarErr) return@forEach
            val spanSettingKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    settingKey
                )
            val spanAwaitKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    awaitKey
                )
            val spanAwaitAsyncVarName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    asyncVarNameEntry
                )
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
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
        val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                    CheckTool.lightBlue,
                    settingKey
                )
            val spanAwaitKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    awaitKey
                )
            val spanAwaitVarNameListCon =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    notAsyncVarNameList.joinToString(",")
                )
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
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
        val awaitKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                    CheckTool.lightBlue,
                    settingKey
                )
            val spanAwaitKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    awaitKey
                )
            val spanAwaitVarNameListCon =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    duplicateAsyncVarNameList.joinToString(",")
                )
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
                    "Duplicate ${spanAwaitKeyName} var name: ${spanAwaitVarNameListCon}: settingKeyName: ${spanSettingKeyName}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isNotExistReturnOrFunc(
        context: Context?,
        keyToSubKeyConList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val imageVarMainKey = ImageActionKeyManager.ImageActionsKey.IMAGE_VAR.key
        val funcKey = ImageActionKeyManager.ImageSubKey.FUNC.key
        val onReturnKey = ImageActionKeyManager.ImageSubKey.ON_RETURN.key
//        val funcKeyRegex = Regex("\\?${funcKey}=")
        val funcKeyWithHatena = "?${funcKey}="
//        val returnKeyRegex = Regex("\\?${onReturnKey}=")
        val returnKeyWithHatena = "?${onReturnKey}="
        keyToSubKeyConList.forEach {
                settingKeyToSubKeyCon ->
            val settingKeyName = settingKeyToSubKeyCon.first
            if(
                settingKeyName != imageVarMainKey
            ) return@forEach
            val subKeyCon = settingKeyToSubKeyCon.second
            val isContainFuncOrReturn =
                subKeyCon.contains(funcKeyWithHatena)
                        || subKeyCon.contains(returnKeyWithHatena)
//                funcKeyRegex.containsMatchIn(subKeyCon)
//                    || returnKeyRegex.containsMatchIn(subKeyCon)
            if(isContainFuncOrReturn) return@forEach
            val spanSettingKeyName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    settingKeyName
                )
            val spanSubKeyCon =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    subKeyCon
                )
            val spanImageVarMainKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    imageVarMainKey
                )
            val spanFuncKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    funcKey
                )
            val spanOnReturnKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    onReturnKey
                )
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
                    "${spanFuncKey} or ${spanOnReturnKey} sub key must use in ${spanImageVarMainKey} settingKey: subKeyCon: ${spanSubKeyCon}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun isRunPrefixUseErr(
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
            val varNameWithSharp = "#{${runVarName}}"
            val isContain = keyToSubKeyListCon.contains(varNameWithSharp)
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
                    CheckTool.lightBlue,
                    settingKeyName
                )
            val spanRunVarName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    runVarName
                )
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
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


    fun isBlankIVarOrIAcVarErr(
        context: Context?,
        settingKeyToPrivateVarNameList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val settingKeyToBlankVarNamePair = settingKeyToPrivateVarNameList.filter {
                settingKeyToPrivateBitmapVarKey ->
            settingKeyToPrivateBitmapVarKey.first != imageReturnKey
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
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_VAR,
                "${spanBlankSettingKeyName} must not blank",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

    fun isShadowTopLevelVarErr(
        context: Context?,
        settingKeyToNoRunVarNameList: List<Pair<String, String>>,
        topLevelBitmapStrKeyList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val shadowSettingKeyToNoRunVarName = settingKeyToNoRunVarNameList.filter {
                settingKeyToNoRunVarName ->
            settingKeyToNoRunVarName.first != imageReturnKey
        }.firstOrNull {
                settingKeyToNoRunVarName ->
            topLevelBitmapStrKeyList?.contains(
                settingKeyToNoRunVarName.second
            ) == true
        }
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
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_VAR,
                "shaddow top level var: ${spanShadowTopLevelVarKey} setting key ${spanSettingKey}",
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

    fun isNotDefinitionVarErr(
        context: Context?,
        settingKeyToNoRunVarNameList: List<Pair<String, String>>,
        keyToSubKeyConList: List<Pair<String, String>>,
        bitmapVarKeyList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Boolean {
//        val regexStrTemplate = "(#[{]%s[}])"
        val bitmapKeyList = let {
            val bitmapKeyListInCode = settingKeyToNoRunVarNameList.filter {
                    settingKeyToNoRunVarName ->
                settingKeyToNoRunVarName.first !=
                        imageReturnKey
            }.map {
                it.second
            }
            val plusBitmapKeyList = bitmapVarKeyList?.filter {
                !bitmapKeyListInCode.contains(it)
            } ?: emptyList()
            bitmapKeyListInCode +
                    plusBitmapKeyList +
                    listOf(ImageActionKeyManager.BitmapVar.itPronoun)
        }
//        val settingKeyListConRegex = let {
//            bitmapKeyList.map {
//                regexStrTemplate.format(it)
//            }.joinToString("|")
//        }.toRegex()
        val keyToSubKeyListCon = makeKeyToSubKeyListCon(
            keyToSubKeyConList,
        )
        val stringKeyToValueSeq = bitmapKeyList.asSequence().map {
                bitmapKey ->
            "#{${bitmapKey}}" to String()
        }
        val keyToSubKeyListConWithRemoveVar = SpeedReplacer.replace(
            keyToSubKeyListCon,
            stringKeyToValueSeq
        )
//        val keyToSubKeyListConWithRemoveVar = keyToSubKeyListCon.replace(
//            settingKeyListConRegex,
//            String()
//        )
        val leaveVarMark =
            ImageVarMarkTool.findAllVarMark(keyToSubKeyListConWithRemoveVar)
        if(
            !leaveVarMark.any()
        ) return false
//        val findVarMarkRegex = Regex("(?<!\\\\)#[{][a-zA-Z0-9_]+[}]")
//        val leaveVarMark =
//            findVarMarkRegex.find(keyToSubKeyListConWithRemoveVar)
//                ?.value
//                ?: return false
        val spanLeaveVarMark =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                leaveVarMark.first()
            )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_VAR,
                "Not difinition var: ${spanLeaveVarMark}",
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

    fun isNotReplaceStringVarErr(
        context: Context?,
        subKeyListCon: String,
        keyToSubKeyConWhere: String,
    ): Boolean {
        execIsNotReplaceStringVarErr(
            context,
            subKeyListCon,
            keyToSubKeyConWhere,
        ).let {
                isNotReplaceVar ->
            if(isNotReplaceVar) return true
        }
        return false
    }

    private fun execIsNotReplaceStringVarErr(
        context: Context?,
        subKeyListCon: String,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val dolVarMarkRegex = Regex("(?<!\\\\)[$][{][a-zA-Z0-9_]+[}]")
        val noReplaceVar = dolVarMarkRegex.find(subKeyListCon)?.value
        if(
            noReplaceVar.isNullOrEmpty()
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
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_VAR,
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
            settingKeyToPrivateVarName.first != imageReturnKey
        }.forEach {
                settingKeyToVarName ->
            val varName = settingKeyToVarName.second
            if(
                varName.startsWith(escapeRunPrefix)
            ) return@forEach
            val varNameWithSharp = "#{${varName}}"
            val isContain =
                keyToSubKeyListCon.contains(varNameWithSharp)
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
                    CheckTool.lightBlue,
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
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
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
        val subKeySeparator = ImageActionKeyManager.subKeySepartor
        val settingKeyList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
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
            val isPrivateVarName =
                !VarMarkTool.matchesUpperAlphNumOrUnderscore(varName)
//                !globalVarNameRegex.matches(varName)
            when(isPrivateVarName){
                false -> String() to String()
                else -> settingKey to varName
            }
        }.filter {
            it.first.isNotEmpty()
                    && it.second.isNotEmpty()
        }.let {
            ImageActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(it)
        }
    }

    fun makeSettingKeyToNoRunVarNameList(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = ImageActionKeyManager.subKeySepartor
        val settingKeyList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
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
            ImageActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(
                it
            )
        }
    }

    private fun makeSettingKeyToPrivateVarNameListWithBlank(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = ImageActionKeyManager.subKeySepartor
        val imageActionKeyList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
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
        val subKeySeparator = ImageActionKeyManager.subKeySepartor
        val imageActionKeyList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
                it.key
            }
        val asyncVarPrefixList = listOf(
            asyncPrefix,
            runAsyncPrefix,
        )
        val awaitSubKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                    ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
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
        val subKeySeparator = ImageActionKeyManager.subKeySepartor
        val imageActionKeyList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
                it.key
            }
        val asyncVarPrefixList = listOf(
            asyncPrefix,
            runAsyncPrefix,
        )
        val awaitSubKey = ImageActionKeyManager.ImageSubKey.AWAIT.key
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
                        ImageActionKeyManager.AwaitManager.getAwaitVarNameList(
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
        val subKeySeparator = ImageActionKeyManager.subKeySepartor
        val imageActionKeyList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
                it.key
            }
        return keyToSubKeyConList.asSequence().map {
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
        }.toList().let {
            ImageActionKeyManager.filterSettingKeyToDefinitionListByValidVarDefinition(
                it
            )
        }
    }
}