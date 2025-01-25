package com.puutaro.commandclick.proccess.edit.setting_action.libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.runBlocking

class SettingReturnExecutor {

    private var isNext = true
    private val valueSeparator = SettingActionKeyManager.valueSeparator
    private val outputReturnSignal =
        SettingActionKeyManager.SettingReturnManager.OutputReturn.OUTPUT_RETURN
    private val returnSignal = SettingActionKeyManager.BreakSignal.RETURN_SIGNAL
    private val sIf = SettingActionKeyManager.SettingReturnManager.SettingReturnKey.S_IF

    suspend fun exec(
        fragment: Fragment,
        mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
        curMapLoopKey: String,
        topVarNameToValueStrMap: Map<String, String?>?,
        privateLoopKeyVarNameValueStrMapClass: SettingActionData.PrivateLoopKeyVarNameValueStrMap,
        loopKeyToVarNameValueStrMapClass: SettingActionData.LoopKeyToVarNameValueStrMap?,
        importedVarNameToValueStrMap: Map<String, String?>?,
        settingActionExitManager: SettingActionData.SettingActionExitManager,
        valueStrBeforeReplace: String,
        keyToSubKeyConWhere: String,
    ): Pair<
            Pair<
                    SettingActionKeyManager.SettingReturnManager.OutputReturn,
                    String
                    >?,
            SettingActionKeyManager.BreakSignal?
            >? {
        val context = fragment.context
        val varNameToValueStrMap = SettingActionKeyManager.makeVarNameToValueStrMap(
            curMapLoopKey,
            topVarNameToValueStrMap,
            importedVarNameToValueStrMap,
            loopKeyToVarNameValueStrMapClass,
            privateLoopKeyVarNameValueStrMapClass,
            null,
            null,
//                        mapOf(
//                            itPronoun to itPronounValueStrToExitSignal?.first
//                        )
        )
        val returnKeyValueStrPair = let {
            val valueStr = CmdClickMap.replaceByBackslashToNormal(
                valueStrBeforeReplace,
                varNameToValueStrMap,
            )
            when(valueStrBeforeReplace.isEmpty()) {
                true -> null
                else -> Pair(
                    outputReturnSignal,
                    valueStr,
                )
            }
        }
        val sIfKey = SettingActionKeyManager.SettingReturnManager.SettingReturnKey.S_IF.key
        val ifMapList = mainSubKeyPairList.filter {
            mainSubKeyPair ->
            val mainSubKey = mainSubKeyPair.first
//            val mainSubKeyMapSrc = mainSubKeyPair.second
            mainSubKey == sIfKey
        }
        IfErrManager.isMultipleSpecifyErr(
            context,
            ifMapList.size,
            sIf.key,
            keyToSubKeyConWhere,
        ).let {
            isMultipleSpecifyErr ->
            if(
                !isMultipleSpecifyErr
            ) return@let
            settingActionExitManager.setExit()
            return null to SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
        }
        val ifMap =
            ifMapList
                .firstOrNull()
                ?.second
        if(ifMap.isNullOrEmpty()){
            return Pair(
                returnKeyValueStrPair,
                returnSignal
            )
        }

        val isReturnToErrType = let {
            val argsPairList = CmdClickMap.createMap(
                ifMap.get(
                    SettingActionKeyManager.SettingSubKey.ARGS.key
                ),
                valueSeparator
            ).filter {
                it.first.isNotEmpty()
            }.map {
                    argNameToValueStr ->
                argNameToValueStr.first to
                        CmdClickMap.replaceByBackslashToNormal(
                            argNameToValueStr.second,
                            varNameToValueStrMap,
                        )
            }
            SettingIfManager.handle(
                sIf.key,
//                        judgeTargetStr,
                argsPairList,
                varNameToValueStrMap
            )
        }
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultSDebugAppDirPath,
//                                    "lsetting_isReturnToErrType_${valueStrBeforeReplace}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "valueStrBeforeReplace: $valueStrBeforeReplace",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "judgeTargetStr: ${judgeTargetStr}",
//                                    "argsPairList: ${argsPairList}",
//                                    "isReturnToErrType: ${isReturnToErrType}"
//                                ).joinToString("\n\n\n")
//                            )
        val errType = isReturnToErrType.second
        if(errType != null){
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_IF,
                    errType.errMessage,
                    keyToSubKeyConWhere
                )
            }
//                        isNext = false
            settingActionExitManager.setExit()
            return null to SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
        }
        IfErrManager.makeIfProcNameNotExistInRuntime(
            sIfKey,
            ifMap.get(sIfKey)
        ).let {
                (ifProcName, errMsg) ->
            if(
                errMsg == null
            ) return@let ifProcName
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_IF,
                    errMsg,
                    keyToSubKeyConWhere
                )
            }
            settingActionExitManager.setExit()
            return Pair(
                null,
                null
            )
        }
        val isReturnBool = isReturnToErrType.first ?: false
        if(isReturnBool){
            return returnKeyValueStrPair to returnSignal
        }
        return Pair(
            null,
            null
        )

//
//
//
//        val ifStackList =
//            mutableListOf<SettingIfManager.IfStack>()
//        mainSubKeyPairList.forEach {
//                mainSubKeyPair ->
//
//            val mainSubKey = mainSubKeyPair.first
//            val mainSubKeyMapSrc = mainSubKeyPair.second
////                    FileSystems.updateFile(
////                        File(UsePath.cmdclickDefaultAppDirPath, "iargsPairList_${settingVarName}.txt").absolutePath,
////                        listOf(
////                            "mainSubKeyMap: ${mainSubKeyMap}",
////
////                        ).joinToString("\n")
////                    )
////                        .map {
////                        replaceItPronoun(it.key) to replaceItPronoun(it.value)
////                    }.toMap()
//            val privateSubKeyClass = SettingActionKeyManager.SettingReturnManager.SettingReturnKey.entries.firstOrNull {
//                it.key == mainSubKey
//            } ?: return@forEach
//            val isNext = ifStackList.lastOrNull().let {
//                    ifStack ->
//                if(
//                    ifStack == null
//                ) return@let true
//                ifStack.bool
//            }
//            when(privateSubKeyClass) {
//                SettingActionKeyManager.SettingReturnManager.SettingReturnKey.ARGS -> {}
//                SettingActionKeyManager.SettingReturnManager.SettingReturnKey.S_IF -> {
//                    if(!isNext) {
////                        isNext = true
//                        return@forEach
//                    }
////                    val judgeTargetStr = mainSubKeyMapSrc.get(mainSubKey)?.let {
////                            judgeTargetStrSrc ->
////                        CmdClickMap.replaceByBackslashToNormal(
////                            judgeTargetStrSrc,
////                            varNameToValueStrMap,
////                        )
////                    } ?: return@forEach
//                    val argsPairList = CmdClickMap.createMap(
//                        mainSubKeyMapSrc.get(
//                            SettingActionKeyManager.SettingSubKey.ARGS.key
//                        ),
//                        valueSeparator
//                    ).filter {
//                        it.first.isNotEmpty()
//                    }.map {
//                            argNameToValueStr ->
//                        argNameToValueStr.first to
//                                CmdClickMap.replaceByBackslashToNormal(
//                                    argNameToValueStr.second,
//                                    varNameToValueStrMap,
//                                )
//                    }
//                    val isReturnToErrType = SettingIfManager.handle(
//                        sIf.key,
////                        judgeTargetStr,
//                        argsPairList,
//                        varNameToValueStrMap
//                    )
////                            FileSystems.updateFile(
////                                File(
////                                    UsePath.cmdclickDefaultSDebugAppDirPath,
////                                    "lsetting_isReturnToErrType_${valueStrBeforeReplace}.txt"
////                                ).absolutePath,
////                                listOf(
////                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
////                                    "valueStrBeforeReplace: $valueStrBeforeReplace",
////                                    "curMapLoopKey: ${curMapLoopKey}",
////                                    "judgeTargetStr: ${judgeTargetStr}",
////                                    "argsPairList: ${argsPairList}",
////                                    "isReturnToErrType: ${isReturnToErrType}"
////                                ).joinToString("\n\n\n")
////                            )
//                    val errType = isReturnToErrType.second
//                    if(errType != null){
//                        runBlocking {
//                            SettingActionErrLogger.sendErrLog(
//                                context,
//                                SettingActionErrLogger.SettingActionErrType.S_IF,
//                                errType.errMessage,
//                                keyToSubKeyConWhere
//                            )
//                        }
////                        isNext = false
//                        settingActionExitManager.setExit()
//                        return null to SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    }
//                    val sIfProcName = IfErrManager.makeIfProcNameNotExistInRuntime(
//                        mainSubKey,
//                        mainSubKeyMapSrc.get(mainSubKey)
//                    ).let {
//                            (ifProcName, errMsg) ->
//                        if(
//                            errMsg == null
//                        ) return@let ifProcName
//                        runBlocking {
//                            SettingActionErrLogger.sendErrLog(
//                                context,
//                                SettingActionErrLogger.SettingActionErrType.S_IF,
//                                errMsg,
//                                keyToSubKeyConWhere
//                            )
//                        }
//                        settingActionExitManager.setExit()
//                        return@forEach
//                    }
//                    val isReturnBool = isReturnToErrType.first ?: false
//                    if(isReturnBool){
//                        return returnKeyValueStrPair to returnSignal
//                    }
//                    ifStackList.add(
//                        SettingIfManager.IfStack(
//                            sIfProcName,
//                            false
//                        )
//                    )
////                    isNext = true
//                }
//                SettingActionKeyManager.SettingReturnManager.SettingReturnKey.S_IF_END -> {
//                    val sIfProcName = IfErrManager.makeIfProcNameNotExistInRuntime(
//                        mainSubKey,
//                        mainSubKeyMapSrc.get(mainSubKey)
//                    ).let {
//                            (ifProcName, errMsg) ->
//                        if(
//                            errMsg == null
//                        ) return@let ifProcName
//                        runBlocking {
//                            SettingActionErrLogger.sendErrLog(
//                                context,
//                                SettingActionErrLogger.SettingActionErrType.S_IF,
//                                errMsg,
//                                keyToSubKeyConWhere
//                            )
//                        }
//                        settingActionExitManager.setExit()
//                        return@forEach
//                    }
//                    if(
//                        ifStackList.lastOrNull()?.ifProcName != sIfProcName
//                    ) {
//                        return@forEach
//                    }
//                    if(
//                        ifStackList.isNotEmpty()
//                    ) {
//                        ifStackList.removeAt(ifStackList.lastIndex)
//                    }
//                }
//            }
////            if(privateSubKeyClass != sIf){
////                isNext = true
////            }
//        }
//        return Pair(
//            null,
//            null
//        )
    }
}