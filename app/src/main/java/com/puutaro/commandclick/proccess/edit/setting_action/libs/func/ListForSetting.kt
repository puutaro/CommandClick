package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager.ConcatCondition
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.PairListTool
import com.puutaro.commandclick.util.str.VarMarkTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.enums.EnumEntries

object ListForSetting {

    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            > {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
//        FuncCheckerForSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.argsNameToTypeList,
//            argsPairList,
////            varNameToValueStrMap
//        )?.let { argsCheckErr ->
//            return null to argsCheckErr
//        }
//        val argsList = argsPairList.map {
//            it.second
//        }
        val args =
            methodNameClass.args
        return withContext(Dispatchers.Main) {
            when (args) {
                is ListMethodArgClass.RndArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val listCon =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.strsKeyToIndex,
                            where
                        ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.separatorToIndex,
                            where
                        ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.random(),
                        null
                    ) to null
                }
                is ListMethodArgClass.ShufArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val listCon = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.strsKeyToIndex,
                        where
                    ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.separatorToIndex,
                            where
                        ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.shuffled().joinToString(separator),
                        null
                    ) to null
                }
                is ListMethodArgClass.TakeArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val listCon =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.strsKeyToIndex,
                            where
                        ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.separatorToIndex,
                            where
                        ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val takeNum =
                        FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                            mapArgMapList,
                            args.numToIndex,
                            where
                        ).let { takeNumToErr ->
                        val funcErr = takeNumToErr.second
                            ?: return@let takeNumToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.take(takeNum).joinToString(separator),
                        null
                    ) to null
                }
                is ListMethodArgClass.TakeLastArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val listCon = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.strsKeyToIndex,
                        where
                    ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.separatorToIndex,
                        where
                    ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val takeLastNum = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.numToIndex,
                        where
                    ).let { takeLastNumToErr ->
                        val funcErr = takeLastNumToErr.second
                            ?: return@let takeLastNumToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.takeLast(takeLastNum).joinToString(separator),
                        null,
                    ) to null
                }
                is ListMethodArgClass.JoinArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val con = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.strsKeyToIndex,
                        where
                    ).let { conToErr ->
                        val funcErr = conToErr.second
                            ?: return@let conToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.separatorToIndex,
                        where
                    ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val joinStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.joinStrToIndex,
                        where
                    ).let { joinStrToErr ->
                        val funcErr = joinStrToErr.second
                            ?: return@let joinStrToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        con.split(separator).joinToString(joinStr),
                        null,
                    ) to null
                }
                is ListMethodArgClass.FilterArgs -> {
                    val formalArgIndexToNameToTypeList =
                        args.entries.mapIndexed { index, formalArgsNameToType ->
                            Triple(
                                index,
                                formalArgsNameToType.key,
                                formalArgsNameToType.type,
                            )
                        }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val inputCon = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.inputConKeyToDefaultValueStr,
                        where
                    ).let { inputConToErr ->
                        val funcErr = inputConToErr.second
                            ?: return@let inputConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.matchTypeKeyToDefaultValueStr,
                        where
                    ).let { matchTypeStrToErr ->
                        val funcErr = matchTypeStrToErr.second
                            ?: return@let matchTypeStrToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.separatorKeyToDefaultValueStr,
                        where
                    ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val defaultSeparator =
                        args.separatorKeyToDefaultValueStr.second
                    val isSeparatorNull =
                        separator == defaultSeparator
                    val joinStr = when(isSeparatorNull) {
                        true -> String()
                        else -> FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                            mapArgMapList,
                            args.joinStrKeyToDefaultValueStr,
                            where
                        ).let { joinStrToErr ->
                            val funcErr = joinStrToErr.second
                            if (funcErr != null) {
                                return@withContext Pair(
                                    null,
                                    SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                                ) to funcErr
                            }
                            SettingFuncTool.makeJoinStrBySeparator(
                                joinStrToErr,
                                separator,
                                args.joinStrKeyToDefaultValueStr.second,
                            )
                        }
                    }
                    val semaphoreInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                        mapArgMapList,
                        args.semaphoreKeyToDefaultValueStr,
                        where
                    ).let { semaphoreIntToErr ->
                        val funcErr = semaphoreIntToErr.second
                            ?: return@let semaphoreIntToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
//                    val elVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                        mapArgMapList,
//                        args.elVarNameKeyToDefaultValueStr,
//                        where
//                    ).let { elVarNameToErr ->
//                        val funcErr = elVarNameToErr.second
//                            ?: return@let elVarNameToErr.first
//                        return@withContext Pair(
//                            null,
//                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
//                        ) to funcErr
//                    }
                    val indexVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.indexVarNameKeyToDefaultValueStr,
                        where
                    ).let { indexVarNameToErr ->
                        val funcErr = indexVarNameToErr.second
                            ?: return@let indexVarNameToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val delimiter = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.delimiterKeyToDefaultValueStr,
                        where
                    ).let { delimiterToErr ->
                        val funcErr = delimiterToErr.second
                            ?: return@let delimiterToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val fieldVarPrefix = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.fieldVarPrefixKeyToDefaultValueStr,
                        where
                    ).let { fieldVarNameToErr ->
                        val funcErr = fieldVarNameToErr.second
                            ?: return@let fieldVarNameToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val alreadyUseVarNameList = listOf(
//                        elVarName,
                        indexVarName,
                        fieldVarPrefix
                    ).filter {
                        it != defaultNullMacroStr
                    }
                    val isDuplicate =
                        let {
                            val sortedAlreadyUseVarNameList =
                                alreadyUseVarNameList.sortedBy { it }
                            sortedAlreadyUseVarNameList !=
                                    sortedAlreadyUseVarNameList.distinct()
                        }
                    if(isDuplicate){
                        val spanIndexVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            args.indexVarNameKeyToDefaultValueStr.first
                        )
//                        val spanElVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.ligthBlue,
//                            args.elVarNameKeyToDefaultValueStr.first
//                        )
                        val spanFieldVarPrefix = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            args.fieldVarPrefixKeyToDefaultValueStr.first
                        )
                        val alreadyUseVarListCon = alreadyUseVarNameList.joinToString(", ")
                        val spanAlreadyUseVarListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.ligthBlue,
                            alreadyUseVarListCon
                        )
                        val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errBrown,
                            where
                        )
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to  FuncCheckerForSetting. FuncCheckErr(
                            "Must be different from ${spanIndexVarName} and ${spanFieldVarPrefix}: ${spanAlreadyUseVarListCon}, ${spanWhere} "
                        )
                    }
                    val boolToErr = Filter.filter(
                        funcName,
                        methodNameStr,
                        args,
                        argsPairList,
                        inputCon,
                        separator,
                        joinStr,
                        semaphoreInt,
//                        elVarName,
                        indexVarName,
                        delimiter,
                        fieldVarPrefix,
                        where,
                    )
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lfilteer.txt").absolutePath,
//                        listOf(
//                            "elVarName: ${elVarName}",
//                            "indexVarName: ${indexVarName}",
//                            "fieldVarPrefix: ${fieldVarPrefix}",
//                            "delimiter: ${delimiter}",
//                            "argsPairList: ${argsPairList}",
//                            "boolToErr: ${boolToErr}"
//                        ).joinToString("\n\n") + "\n=====\n\n"
//                    )
                    boolToErr
                }
            }
        }
    }

    private object Filter {
        suspend fun filter(
            funcName: String,
            methodNameStr: String,
            args: ListMethodArgClass.FilterArgs,
            argsPairList: List<Pair<String, String>>,
            inputCon: String,
            separator: String,
            joinStr: String,
            semaphoreInt: Int,
//            elVarName: String,
            indexVarName: String,
            delimiter: String,
            fieldVarPrefix: String,
            where: String,
        ): Pair<
                Pair<
                        String?,
                        SettingActionKeyManager.BreakSignal?
                        >?,
                FuncCheckerForSetting.FuncCheckErr?
                > {
            val inputConList = when (separator == defaultNullMacroStr) {
                true -> listOf(inputCon)
                false -> inputCon.split(separator)
            }
            val semaphore = when (semaphoreInt > 0) {
                true -> Semaphore(semaphoreInt)
                else -> null
            }
            val indexAndLineAndMatchErrJobListJob = withContext(Dispatchers.IO) {
                when (semaphore == null) {
                    false -> inputConList.mapIndexed { index, inputLine ->
                        async {
                            semaphore.withPermit {
                                val boolToErr = culcBool(
                                    funcName,
                                    methodNameStr,
                                    args,
                                    argsPairList,
                                    inputLine,
//                                    elVarName,
                                    indexVarName,
                                    index,
                                    delimiter,
                                    fieldVarPrefix,
                                )
                                Triple(
                                    index,
                                    inputLine,
                                    boolToErr
//                                    SettingIfManager.IfArgMatcher.match(
//                                        "${funcName}.${methodNameStr}",
//                                        targetConWithReplace,
//                                        argsPairListWithReplace,
//                                    )
                                )
                            }
                        }
                    }

                    else -> inputConList.mapIndexed { index, inputLine ->
                        async {
                            val boolToErr = culcBool(
                                funcName,
                                methodNameStr,
                                args,
                                argsPairList,
                                inputLine,
//                                elVarName,
                                indexVarName,
                                index,
                                delimiter,
                                fieldVarPrefix,
                            )
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "lfilteer_culcBool.txt").absolutePath,
//                                listOf(
//                                    "elVarName: ${elVarName}",
//                                    "indexVarName: ${indexVarName}",
//                                    "fieldVarPrefix: ${fieldVarPrefix}",
//                                    "delimiter: ${delimiter}",
//                                    "argsPairList: ${argsPairList}",
//                                    "boolToErr: ${boolToErr}"
//                                ).joinToString("\n\n") + "\n=====\n\n"
//                            )
                            Triple(
                                index,
                                inputLine,
                                boolToErr
//                                    SettingIfManager.IfArgMatcher.match(
//                                        "${funcName}.${methodNameStr}",
//                                        targetConWithReplace,
//                                        argsPairListWithReplace,
//                                    )
                            )
//                            Triple(
//                                index,
//                                inputLine,
//                                SettingIfManager.IfArgMatcher.match(
//                                    "${funcName}.${methodNameStr}",
//                                    targetConWithReplace,
//                                    argsPairListWithReplace,
//                                )
//                            )
                        }
                    }
                }
            }
            val lineToMatchErrList =
                indexAndLineAndMatchErrJobListJob
                    .awaitAll()
                    .sortedBy {
                        (index, _, _) ->
                        index
                    }.map {
                        (_, line, matchErr) ->
                        line to matchErr
                    }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lfilteer_culcBool_lineToMatchErrJobList.txt").absolutePath,
//                listOf(
//                    "elVarName: ${elVarName}",
//                    "indexVarName: ${indexVarName}",
//                    "fieldVarPrefix: ${fieldVarPrefix}",
//                    "delimiter: ${delimiter}",
//                    "argsPairList: ${argsPairList}",
//                    "lineToMatchErrJobList: ${lineToMatchErrList}"
//                ).joinToString("\n\n") + "\n=====\n\n"
//            )
            lineToMatchErrList.firstOrNull {
                (_, boolToErr) ->
                boolToErr.second != null
            }?.let {
                    (_, boolToErr) ->
                val spanPlusWhere =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                return null to FuncCheckerForSetting.FuncCheckErr(
                    listOf(
                        boolToErr.second?.errMessage.toString(),
                        spanPlusWhere
                    ).joinToString(", ")
                )
            }
            val filterLineListCon = lineToMatchErrList.filter {
                lineAndMatchErr ->
                lineAndMatchErr.second.first ?: false
            }.map {
                lineAndMatchErr ->
                lineAndMatchErr.first
            }.joinToString(joinStr)
            return Pair(filterLineListCon, null) to null
        }
    }

    private fun culcBool(
        funcName: String,
        methodNameStr: String,
        args: ListMethodArgClass.FilterArgs,
        argsPairList: List<Pair<String, String>>,
        inputLine: String,
//        elVarName: String,
        indexVarName: String,
        elIndex: Int,
        delimiter: String,
        fieldVarPrefix: String,
    ): Pair<Boolean?, SettingIfManager. IfCheckErr?> {
        val argsPairListWithReplace = replaceArgNameToValueStrList(
            inputLine,
            argsPairList,
//            elVarName,
            indexVarName,
            elIndex,
            delimiter,
            fieldVarPrefix,
        )
        val ifKeyPairsList = makeIfKeyPairsList(
            argsPairListWithReplace
        )
        val targetKey = args.targetToDefaultValueStr.first
        val boolToErrList = ifKeyPairsList.map {
                ifKeyPairList ->
            val targetCon = PairListTool.getValue(
                ifKeyPairList,
                targetKey
            ) ?: inputLine
//            if(targetCon.isNullOrEmpty()){
//                return SettingIfManager.makeJudgeTargetNotExistErr(
//                        "${funcName}.${methodNameStr}",
//                        argsPairList,
//                    )
//            }
            SettingIfManager.IfArgMatcher.match(
                "${funcName}.${methodNameStr}",
                targetCon,
                ifKeyPairList.filter{
                    (ifKey, _) ->
                    ifKey != targetKey
                },
            )
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "llistForseettg${elIndex}.txt").absolutePath,
//            listOf(
//                "index: ${elIndex}",
//                "indexVarName: ${indexVarName}",
//                "fieldVarPrefix: ${fieldVarPrefix}",
//                "delimiter: ${delimiter}",
//                "argsPairList: ${argsPairList}",
//                "argsPairListWithReplace: ${argsPairListWithReplace}",
//                "ifKeyPairsList: ${ifKeyPairsList}",
//                "boolToErrList: ${boolToErrList}",
//                "boolToErrList.size: ${boolToErrList.size}",
//                "ifKeyPairsList: ${ifKeyPairsList}",
//            ).joinToString("\n\n") + "\n=====\n\n"
//        )
        boolToErrList.firstOrNull {
                (_, err) ->
            err != null
        }.let {
                boolToErr ->
            val err = boolToErr?.second
                ?: return@let
            return null to err
        }
        return when(boolToErrList.size == 1) {
            true -> boolToErrList.first()
            else -> {
                val concatConditionStr = PairListTool.getValue(
                    argsPairList,
                    args.concatConditionKeyToDefaultValueStr.first,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "llistForseettg${elIndex}_condition.txt").absolutePath,
//                    listOf(
//                        "index: ${elIndex}",
//                        "indexVarName: ${indexVarName}",
//                        "fieldVarPrefix: ${fieldVarPrefix}",
//                        "delimiter: ${delimiter}",
//                        "argsPairList: ${argsPairList}",
//                        "argsPairListWithReplace: ${argsPairListWithReplace}",
//                        "ifKeyPairsList: ${ifKeyPairsList}",
//                        "concatCondition ${ConcatCondition.entries.firstOrNull {
//                            it.str == concatConditionStr
//                        }}",
//                        "err ${SettingIfManager.IfArgMatcher.makeConcatConditionKeyNotExistErr(
//                            "${funcName}.${methodNameStr}",
//                            concatConditionStr,
//                            argsPairList
//                        )}"
//                    ).joinToString("\n\n") + "\n=====\n\n"
//                )
                val concatCondition = ConcatCondition.entries.firstOrNull {
                    it.str == concatConditionStr
                } ?: let {
                    return SettingIfManager.IfArgMatcher.makeConcatConditionKeyNotExistErr(
                            "${funcName}.${methodNameStr}",
                            concatConditionStr,
                            argsPairList
                        )
                }
                SettingIfManager.IfArgMatcher.culcMatchResult(
                    concatCondition,
                    boolToErrList.map {
                        it.first ?: true
                    },
                ) to null
            }
        }
    }

    private fun makeIfKeyPairsList(
        argsPairList: List<Pair<String, String>>,
    ):  List<List<Pair<String, String>>> {
        val targetKey =
            ListMethodArgClass.FilterArgs.FilterEnumArgs.TARGET.key
        val matchTypeKey =
            ListMethodArgClass.FilterArgs.FilterEnumArgs.MATCH_TYPE.key
        val valueKey =
            ListMethodArgClass.FilterArgs.FilterEnumArgs.VALUE.key
        val regexKey =
            ListMethodArgClass.FilterArgs.FilterEnumArgs.REGEX.key
        val lastDefiniteKeyList = listOf(
            valueKey,
            regexKey
        )
        val ifArgsKeyList = listOf(
            targetKey,
            matchTypeKey,
            valueKey,
            ListMethodArgClass.FilterArgs.FilterEnumArgs.REGEX.key,
        )
        val ifArgNameToValueStrList = argsPairList.filter {
            (argName, _) ->
            ifArgsKeyList.contains(argName)
        }
        val defaultMapReturnPair =
            listOf(String() to String())
        val filterKeyToValueStrList = ifArgNameToValueStrList.filter {
            (ifArgName, _) ->
            ifArgsKeyList.contains(ifArgName)
        }.reversed()
        val judgeKeyToValueStrList = filterKeyToValueStrList.mapIndexed {
            index, (filterKey, valueStr) ->
            if(
                index != 0
                && !lastDefiniteKeyList.contains(filterKey)
            ) return@mapIndexed defaultMapReturnPair
            val lastFilterKeyToValueStr =
                filterKey to valueStr
            val matchTypeOrTargetKeyPairList = let {
                val focusFilterKeyToValueStrList = filterKeyToValueStrList.filterIndexed { innerIndex, _ ->
                    innerIndex > index
                }
                val nextValueKeyIndex =
                    focusFilterKeyToValueStrList.indexOfFirst { (filterKey, _) ->
                        lastDefiniteKeyList.contains(filterKey)
                    }
                when (
                    nextValueKeyIndex < 0
                ) {
                    true -> focusFilterKeyToValueStrList
                    else -> focusFilterKeyToValueStrList.filterIndexed { innerIndex, _ ->
                        innerIndex < nextValueKeyIndex
                    }
                }.reversed()
            }
            matchTypeOrTargetKeyPairList + listOf(lastFilterKeyToValueStr)
        }.filter {
                ifKeyPairs ->
            ifKeyPairs.all {
                    (ifKey, _) ->
                ifKey.isNotEmpty()
            }
        }.reversed()
        return judgeKeyToValueStrList
    }

    private fun replaceArgNameToValueStrList(
        inputLine: String,
        argsPairList: List<Pair<String, String>>,
//        elVarName: String,
        indexVarName: String,
        elIndex: Int,
        delimiter: String,
        fieldVarPrefix: String,
    ): List<Pair<String, String>> {
        return argsPairList.map { (argName, valueStr) ->
            argName to replaceByElAndFieldVar(
                valueStr,
                inputLine,
//                elVarName,
                indexVarName,
                elIndex,
                delimiter,
                fieldVarPrefix,
            )
        }
    }

    private fun replaceByElAndFieldVar(
        valueStr: String,
        inputLine: String,
//        elVarName: String,
        indexVarName: String,
        elIndex: Int,
        delimiter: String,
        fieldVarPrefix: String,
    ): String {
        return SettingFuncTool.FieldVarPrefix.makeFieldVarNameToValueStrList(
            inputLine,
            delimiter,
            fieldVarPrefix
        ).let {
                fieldVarNameToValueStrList ->
            SettingFuncTool.FieldVarPrefix.replaceElementByFieldVarName(
                valueStr,
                fieldVarNameToValueStrList,
                fieldVarPrefix,
            ).let replaceByIndex@ {
                if(
                    indexVarName == defaultNullMacroStr
                ) return@replaceByIndex it
                VarMarkTool.replaceByValue(
                    it,
                    indexVarName,
                    elIndex.toString(),
                )
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ListMethodArgClass,
    ){
        RND("rnd", ListMethodArgClass.RndArgs),
        SHUF("shuf", ListMethodArgClass.ShufArgs),
        TAKE("take", ListMethodArgClass.TakeArgs),
        TAKE_LAST("takeLast", ListMethodArgClass.TakeLastArgs),
        JOIN("join", ListMethodArgClass.JoinArgs),
        FILTER("filter", ListMethodArgClass.FilterArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ListMethodArgClass {
        data object RndArgs : ListMethodArgClass(), ArgType {
            override val entries = RndEnumArgs.entries
            val strsKeyToIndex = Pair(
                RndEnumArgs.STRS.key,
                RndEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                RndEnumArgs.SEPARATOR.key,
                RndEnumArgs.SEPARATOR.index
            )

            enum class RndEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object ShufArgs : ListMethodArgClass(), ArgType {
            override val entries = ShufEnumArgs.entries
            val strsKeyToIndex = Pair(
                ShufEnumArgs.STRS.key,
                ShufEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                ShufEnumArgs.SEPARATOR.key,
                ShufEnumArgs.SEPARATOR.index
            )

            enum class ShufEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object TakeArgs : ListMethodArgClass(), ArgType {
            override val entries = TakeEnumArgs.entries
            val strsKeyToIndex = Pair(
                TakeEnumArgs.STRS.key,
                TakeEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                TakeEnumArgs.SEPARATOR.key,
                TakeEnumArgs.SEPARATOR.index
            )
            val numToIndex = Pair(
                TakeEnumArgs.NUM.key,
                TakeEnumArgs.NUM.index
            )

            enum class TakeEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
                NUM("num", 2, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object TakeLastArgs : ListMethodArgClass(), ArgType {
            override val entries = TakeLastEnumArgs.entries
            val strsKeyToIndex = Pair(
                TakeLastEnumArgs.STRS.key,
                TakeLastEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                TakeLastEnumArgs.SEPARATOR.key,
                TakeLastEnumArgs.SEPARATOR.index
            )
            val numToIndex = Pair(
                TakeLastEnumArgs.NUM.key,
                TakeLastEnumArgs.NUM.index
            )

            enum class TakeLastEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
                NUM("num", 2, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object JoinArgs : ListMethodArgClass(), ArgType {
            override val entries = JoinEnumArgs.entries
            val strsKeyToIndex = Pair(
                JoinEnumArgs.STRS.key,
                JoinEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                JoinEnumArgs.SEPARATOR.key,
                JoinEnumArgs.SEPARATOR.index
            )
            val joinStrToIndex = Pair(
                JoinEnumArgs.JOIN_STR.key,
                JoinEnumArgs.JOIN_STR.index
            )

            enum class JoinEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", 2, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object FilterArgs : ListMethodArgClass(), ArgType {
            override val entries = FilterEnumArgs.entries
            val inputConKeyToDefaultValueStr = Pair(
                FilterEnumArgs.INPUT_CON.key,
                FilterEnumArgs.INPUT_CON.defaultValueStr
            )
            val separatorKeyToDefaultValueStr = Pair(
                FilterEnumArgs.SEPARATOR.key,
                FilterEnumArgs.SEPARATOR.defaultValueStr
            )
            val joinStrKeyToDefaultValueStr = Pair(
                FilterEnumArgs.JOIN_STR.key,
                FilterEnumArgs.JOIN_STR.defaultValueStr
            )
            val matchTypeKeyToDefaultValueStr = Pair(
                FilterEnumArgs.MATCH_TYPE.key,
                FilterEnumArgs.MATCH_TYPE.defaultValueStr
            )
            val valueKeyToDefaultValueStr = Pair(
                FilterEnumArgs.VALUE.key,
                FilterEnumArgs.VALUE.defaultValueStr
            )
            val targetToDefaultValueStr = Pair(
                FilterEnumArgs.TARGET.key,
                FilterEnumArgs.TARGET.defaultValueStr
            )
            val regexKeyToDefaultValueStr = Pair(
                FilterEnumArgs.REGEX.key,
                FilterEnumArgs.REGEX.defaultValueStr
            )
            val semaphoreKeyToDefaultValueStr = Pair(
                FilterEnumArgs.SEMAPHORE.key,
                FilterEnumArgs.SEMAPHORE.defaultValueStr
            )
//            val elVarNameKeyToDefaultValueStr = Pair(
//                FilterEnumArgs.EL_VAR_NAME.key,
//                FilterEnumArgs.EL_VAR_NAME.defaultValueStr
//            )
            val indexVarNameKeyToDefaultValueStr = Pair(
                FilterEnumArgs.INDEX_VAR_NAME.key,
                FilterEnumArgs.INDEX_VAR_NAME.defaultValueStr
            )
            val delimiterKeyToDefaultValueStr = Pair(
                FilterEnumArgs.DELIMITER.key,
                FilterEnumArgs.DELIMITER.defaultValueStr
            )
            val fieldVarPrefixKeyToDefaultValueStr = Pair(
                FilterEnumArgs.FIELD_VAR_PREFIX.key,
                FilterEnumArgs.FIELD_VAR_PREFIX.defaultValueStr
            )
            val concatConditionKeyToDefaultValueStr = Pair(
                FilterEnumArgs.CONCAT_CONDITION.key,
                FilterEnumArgs.CONCAT_CONDITION.defaultValueStr
            )

            enum class FilterEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                INPUT_CON("inputCon", null, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                MATCH_TYPE(SettingIfManager.IfArgs.MATCH_TYPE.str, null, FuncCheckerForSetting.ArgType.STRING),
                VALUE(SettingIfManager.IfArgs.VALUE.str, String(), FuncCheckerForSetting.ArgType.STRING),
                REGEX(SettingIfManager.IfArgs.REGEX.str, String(), FuncCheckerForSetting.ArgType.STRING),
                TARGET("target", null, FuncCheckerForSetting.ArgType.STRING),
                SEMAPHORE("semaphore", 0.toString(), FuncCheckerForSetting.ArgType.INT),
//                EL_VAR_NAME("elVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                INDEX_VAR_NAME("indexVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                DELIMITER("delimiter", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                FIELD_VAR_PREFIX("fieldVarPrefix", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                CONCAT_CONDITION("concatCondition", null, FuncCheckerForSetting.ArgType.STRING),
            }
        }

    }
}
