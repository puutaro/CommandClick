package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object ShellToolManagerForSetting {

    private const val cmdSeparator = '|'
    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr
    private const val switchOff = "OFF"


    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        argsPairListBeforeBsEscape: List<Pair<String, String>>,
        busyboxExecutor: BusyboxExecutor?,
        varNameToValueStrMap: Map<String, String?>?,
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
        val args =
            methodNameClass.args
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "ltimeoutHelp.txt").absolutePath,
//            listOf(
//                "${busyboxExecutor?.getCmdOutput(
//                    """${'$'}{b} timeout --help"""
//                )}"
//            ).joinToString("\n")
//        )
        when(args){
            is ShellMethodArgClass.ExecArgs -> {
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
                val cmdStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.cmdKeyToDefaultValueStr,
                    where
                ).let { cmdStrToErr ->
                    val funcErr = cmdStrToErr.second
                        ?: return@let cmdStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val inputCon = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.inputConKeyToDefaultValueStr,
                    where
                ).let { inputConToErr ->
                    val funcErr = inputConToErr.second
                        ?: return@let inputConToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
//                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.cmdKeyToDefaultValueStr,
//                    where
//                ).let { cmdStrToErr ->
//                    val funcErr = cmdStrToErr.second
//                        ?: return@let cmdStrToErr.first
//                    return Pair(
//                        null,
//                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
                val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.separatorKeyToDefaultValueStr,
                    where
                ).let { separatorToErr ->
                    val funcErr = separatorToErr.second
                        ?: return@let separatorToErr.first
                    return Pair(
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
                            return Pair(
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
                val timeoutInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timeoutKeyToDefaultValueStr,
                    where
                ).let { timeoutIntToErr ->
                    val funcErr = timeoutIntToErr.second
                    if(funcErr != null){
                        return Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val timeoutIntSrc =
                        timeoutIntToErr.first
                    timeoutIntSrc
                }
                val semaphoreInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.semaphoreKeyToDefaultValueStr,
                    where
                ).let { semaphoreIntToErr ->
                    val funcErr = semaphoreIntToErr.second
                        ?: return@let semaphoreIntToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val enableEscape = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.onEscapeKeyToDefaultValueStr,
                    where
                ).let { onEscapeToErr ->
                    val funcErr = onEscapeToErr.second
                        ?: return@let onEscapeToErr.first != switchOff
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val indexVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.indexVarNameKeyToDefaultValueStr,
                    where
                ).let { indexVarNameToErr ->
                    val funcErr = indexVarNameToErr.second
                        ?: return@let indexVarNameToErr.first
                    return Pair(
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
                    return Pair(
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
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }

                val argsPairListForCmd = when(enableEscape){
                    true -> SettingFuncTool.makeArgsPairListByEscape(
                        argsPairListBeforeBsEscape,
                        varNameToValueStrMap,
                    )
                    else -> argsPairList
                }
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "largPar.txt").absolutePath,
//                    listOf(
//                        "argsPairListBeforeBsEscape: ${argsPairListBeforeBsEscape}",
//                        "argsPairList: ${argsPairList}",
//                        "argsPairListForCmd: ${argsPairListForCmd}"
//                    ).joinToString("\n\n")
//                )
                return ExecCmd.exec(
                    busyboxExecutor,
                    args,
                    argsPairListForCmd,
                    cmdStr,
                    inputCon,
                    separator,
                    joinStr,
                    semaphoreInt,
                    timeoutInt,
                    indexVarName,
                    delimiter,
                    fieldVarPrefix,
                    where
                )
            }
            is ShellMethodArgClass.MapArgs -> {
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
    //                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
    //                    mapArgMapList,
    //                    args.cmdKeyToDefaultValueStr,
    //                    where
    //                ).let { cmdStrToErr ->
    //                    val funcErr = cmdStrToErr.second
    //                        ?: return@let cmdStrToErr.first
    //                    return Pair(
    //                        null,
    //                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
    //                    ) to funcErr
    //                }
                val inputCon = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.inputConKeyToDefaultValueStr,
                    where
                ).let { inputConToErr ->
                    val funcErr = inputConToErr.second
                        ?: return@let inputConToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
//                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
//                    mapArgMapList,
//                    args.cmdKeyToDefaultValueStr,
//                    where
//                ).let { cmdStrToErr ->
//                    val funcErr = cmdStrToErr.second
//                        ?: return@let cmdStrToErr.first
//                    return Pair(
//                        null,
//                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    ) to funcErr
//                }
                val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.separatorKeyToDefaultValueStr,
                    where
                ).let { separatorToErr ->
                    val funcErr = separatorToErr.second
                        ?: return@let separatorToErr.first
                    return Pair(
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
                            return Pair(
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
                val timeoutInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.timeoutKeyToDefaultValueStr,
                    where
                ).let { timeoutIntToErr ->
                    val funcErr = timeoutIntToErr.second
                    if(funcErr != null){
                        return Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val timeoutIntSrc =
                        timeoutIntToErr.first
                    timeoutIntSrc
                }
                val semaphoreInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.semaphoreKeyToDefaultValueStr,
                    where
                ).let { semaphoreIntToErr ->
                    val funcErr = semaphoreIntToErr.second
                        ?: return@let semaphoreIntToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val enableEscape = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.onEscapeKeyToDefaultValueStr,
                    where
                ).let { escapeToErr ->
                    val funcErr = escapeToErr.second
                        ?: return@let escapeToErr.first != switchOff
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val alreadyUseVarName = mutableListOf<String>()
                val indexVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.indexVarNameKeyToDefaultValueStr,
                    where
                ).let { indexVarNameToErr ->
                    val funcErr = indexVarNameToErr.second
                        ?: return@let indexVarNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                alreadyUseVarName.add(indexVarName)
                val delimiter = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.delimiterKeyToDefaultValueStr,
                    where
                ).let { delimiterToErr ->
                    val funcErr = delimiterToErr.second
                        ?: return@let delimiterToErr.first
                    return Pair(
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
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }

                val argsPairListForCmd = when(enableEscape){
                    true -> SettingFuncTool.makeArgsPairListByEscape(
                        argsPairListBeforeBsEscape,
                        varNameToValueStrMap,
                    )
                    else -> argsPairList
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lescape.txt").absolutePath,
//                    listOf(
//                        "disableEscape: ${enableEscape}",
//                        "argsPairListBeforeBsEscape: ${argsPairListBeforeBsEscape}",
//                        "argsPairListForCmd: ${argsPairListForCmd}",
//                    ).joinToString("\n")
//                )
                return MapCmd.exec(
                    busyboxExecutor,
                    args,
                    argsPairListForCmd,
                    inputCon,
                    separator,
                    joinStr,
                    semaphoreInt,
                    timeoutInt,
                    indexVarName,
                    delimiter,
                    fieldVarPrefix,
                    where,
                )
            }
        }
    }

    private object ExecCmd {
        suspend fun exec(
            busyboxExecutor: BusyboxExecutor?,
            execMapArgs: ShellMethodArgClass.ExecArgs,
            argsPairList: List<Pair<String, String>>,
            cmdStr: String,
            inputCon: String,
            separator: String,
            joinStr: String,
            semaphoreInt: Int,
            timeoutInt: Int,
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
                >{
            val mainCmd = makeMainCmd(
                execMapArgs,
                cmdStr,
                argsPairList
            )
            val inputConList = when(separator == defaultNullMacroStr){
                true -> listOf(inputCon)
                false -> inputCon.split(separator)
            }
            val semaphore = when(semaphoreInt > 0){
                true -> Semaphore(semaphoreInt)
                else -> null
            }
            return withContext(Dispatchers.IO) {
                val indexToOutputList = when (semaphore == null) {
                    false -> inputConList.mapIndexed { index, inputLine ->
                        async {
                            semaphore.withPermit {
                                val indexToVarNamePair = Pair(indexVarName, index.toString())
                                getCmdOutput(
                                    busyboxExecutor,
                                    execMapArgs,
                                    mainCmd,
                                    index,
                                    inputLine,
                                    indexToVarNamePair,
                                    timeoutInt,
                                    delimiter,
                                    fieldVarPrefix,
                                    where,
                                )
                            }
                        }
                    }

                    else -> inputConList.mapIndexed { index, inputLine ->
                        async {
                            val indexToVarNamePair = Pair(indexVarName, index.toString())
                            val indexAndOutputToErr = getCmdOutput(
                                busyboxExecutor,
                                execMapArgs,
                                mainCmd,
                                index,
                                inputLine,
                                indexToVarNamePair,
                                timeoutInt,
                                delimiter,
                                fieldVarPrefix,
                                where,
                            )
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "llshell_${index}.txt").absolutePath,
//                                listOf(
//                                    "indexToOutput: ${indexToOutput}",
//                                ).joinToString("\n\n") + "\n========\n\n"
//                            )
                            indexAndOutputToErr
                        }
                    }
                }
                val replaceStrToFuncErrList = indexToOutputList.awaitAll().sortedBy {
                        indexToReplaceJob ->
                    indexToReplaceJob.first
                }.map {
                        indexToReplaceJob ->
                    indexToReplaceJob.second
                }
                replaceStrToFuncErrList.firstOrNull {
                        replaceStrToFuncErr ->
                    replaceStrToFuncErr.second != null
                }?.second?.let {
                        funcErr ->
                    return@withContext Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val replaceStr = replaceStrToFuncErrList.filter {
                        (replaceStr, _) ->
                    replaceStr != null
                }.map {
                        (replaceStr, _) ->
                    replaceStr
                }.joinToString(joinStr)
                Pair(
                    replaceStr,
                    null) to null
            }
        }

        private fun getCmdOutput(
            busyboxExecutor: BusyboxExecutor?,
            shellMapArgs: ShellMethodArgClass.ExecArgs,
            mainCmd: String,
            index: Int,
            inputLine: String,
            indexToVarNamePair: Pair<String, String>,
            timeoutInt: Int,
            delimiter: String,
            fieldVarPrefix: String,
            where: String,
        ): Pair<Int, Pair<String?, FuncCheckerForSetting.FuncCheckErr?>> {
            val execCmdStr = makeCmd(
                shellMapArgs,
                mainCmd,
                inputLine,
                indexToVarNamePair,
                timeoutInt,
                delimiter,
                fieldVarPrefix,
            )
            return execGetCmdOutput(
                busyboxExecutor,
                execCmdStr,
                index,
                where,
            )
        }

        private fun makeCmd(
            shellMapArgs: ShellMethodArgClass.ExecArgs,
            mainCmd: String,
            inputLine: String,
            indexToVarNamePair: Pair<String, String>,
            timeoutInt: Int,
            delimiter: String,
            fieldVarPrefix: String,
        ): String {
            val pipCmdList = let {
                val busyboxCmd = "${'$'}{b} ${mainCmd}"
                when(timeoutInt <= 0) {
                    true -> busyboxCmd
                    else -> "${'$'}{b} timeout -t ${timeoutInt} ${busyboxCmd}"
                }
            }
            val inputConForShellVar = inputLine.replace(
                "\"",
                "\\\""
            )
            val totalCmd = """echo "${inputConForShellVar}" | ${pipCmdList} """
            val execShellCmd =
                listOf(
                    "set -ue",
                    totalCmd,
                    String()
                ).joinToString(";\n").replace(
                    "${'$'}${indexToVarNamePair.first}",
                    indexToVarNamePair.second
                )

            return SettingFuncTool.replaceShellCmdByFieldVarName(
                execShellCmd,
                inputConForShellVar,
                delimiter,
                fieldVarPrefix,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "llshell_${inputCon.take(4)}.txt").absolutePath,
//                listOf(
//                    "pipCmdList: ${pipCmdList}",
//                    "con: ${execShellCmd}"
//                ).joinToString("\n\n") + "\n========\n\n"
//            )
        }


        private fun makeMainCmd(
            execMapArgs: ShellMethodArgClass.ExecArgs,
            cmdStr: String,
            argsPairList: List<Pair<String, String>>
        ): String {
            val valueKey = execMapArgs.valueKeyToDefaultValueStr.first
            val encloseKey = execMapArgs.encloseKeyToDefaultValueStr.first
            val argOrOpNameToValueKeyMapList = makeArgOrOpNameToValueKeyMapList(
                argsPairList,
                execMapArgs,
            )
            val argOrOpCon = argOrOpNameToValueKeyMapList.map {
                    (argOrOpName, valueKeyMap) ->
                val argOrOpCon = valueKeyMap.get(argOrOpName)
                val value = valueKeyMap.get(valueKey)
                val encloseStr = (valueKeyMap.get(encloseKey) ?: "\"")
                when(value.isNullOrEmpty()){
                    true -> argOrOpCon
                    else -> "$argOrOpCon ${encloseStr}$value${encloseStr}"
                }
            }.joinToString(" ")
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "llshell00_${cmdStr}.txt").absolutePath,
//                listOf(
//                    "argOrOpNameToValueKeyMapList: ${argOrOpNameToValueKeyMapList}",
//                    "argOrOpCon: ${argOrOpCon}",
//                ).joinToString("\n\n") + "\n========\n\n"
//            )
            return """${cmdStr} ${argOrOpCon}""".trimIndent()

        }

        private fun makeArgOrOpNameToValueKeyMapList(
            argsPairList: List<Pair<String, String>>,
            execMethodArgs: ShellMethodArgClass.ExecArgs,
        ): List<
                Pair<String, Map<String, String>>
                > {
            val valueKey = execMethodArgs.valueKeyToDefaultValueStr.first
            val encloseKey = execMethodArgs.encloseKeyToDefaultValueStr.first
            val valueMapKeyList = listOf(
                valueKey,
                encloseKey
            )
            val noRegisterMapArgList = execMethodArgs.entries.map {
                it.key
            }
            return argsPairList.mapIndexed {
                    index, (argOrOpName, argOrOpCon) ->
//                val argClass = mapMethodArgs.entries.firstOrNull {
//                        arg ->
//                    arg.key == argName
//                } ?: return@mapIndexed String() to emptyMap()
//                val curArgKey = argClass.key
                if(
                    argOrOpName.isEmpty()
                    || noRegisterMapArgList.any {
                            argName ->
                        argName.startsWith(argOrOpName)
                                || argName.endsWith(argOrOpName)
                    }
//                    argClass != ShellMethodArgClass.MapArgs.MapEnumArgs.CMD

                ) {
                    return@mapIndexed String() to emptyMap()
                }
                val argOrOpMap = mapOf(
                    argOrOpName to argOrOpCon,
                )
                val valueStrMap = let {
                    val focusArgsPairList = argsPairList.filterIndexed {
                            innerIndex, _ ->
                        innerIndex > index
                    }
                    if(
                        focusArgsPairList.isEmpty()
                    ) return@let emptyMap()
                    val nextArgOrOpKeyIndex = focusArgsPairList.indexOfFirst {
                        (key, valueStr) ->
                        !valueMapKeyList.contains(key)
                    }
                    if(
                        nextArgOrOpKeyIndex < 0
                    ) return@let emptyMap()
                    focusArgsPairList.filterIndexed {
                            innerIndex, _ ->
                        innerIndex < nextArgOrOpKeyIndex
                    }.toMap()
                }
//                argsPairList.filterIndexed()
//                    val nextArgNameToValueStr =
//                        argsPairList.getOrNull(index + 1)
//                            ?: return@let emptyMap()
//                    val nextArgName = nextArgNameToValueStr.first
//                    when (nextArgName == valueKey) {
//                        false -> emptyMap()
//                        else -> {
//                            val valueStr =
//                                nextArgNameToValueStr.second
//                            mapOf(
//                                valueKey to valueStr
//                            )
//                        }
//                    }
//                }
                Pair(argOrOpName, (argOrOpMap + valueStrMap))
            }.filter {
                it.first.isNotEmpty()
            }
        }
    }

    private object MapCmd {
        suspend fun exec(
            busyboxExecutor: BusyboxExecutor?,
            shellMapArgs: ShellMethodArgClass.MapArgs,
            argsPairList: List<Pair<String, String>>,
            inputCon: String,
            separator: String,
            joinStr: String,
            semaphoreInt: Int,
            defaultTimeoutInt: Int,
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
            val procNameToCmdKeyMapList = makeProcNameToCmdKeyMapList(
                argsPairList,
                shellMapArgs,
                defaultTimeoutInt,
                where,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "largNameToSubKeyMapListForCmd.txt").absolutePath,
//                procNameToCmdKeyMapList.joinToString("\n") + "\n========\n\n"
//            )
            if(procNameToCmdKeyMapList.isEmpty()){
                val spanCmdDescriptionKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    "${shellMapArgs.cmdKeyToDefaultValueStr.first} description"
                )
                val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    where
                )
                return Pair(
                    null,
                    SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                ) to FuncCheckerForSetting.FuncCheckErr(
                    "${spanCmdDescriptionKey} key not exist: ${spanWhere}"
                )
            }
            val inputConList = when(separator == defaultNullMacroStr){
                true -> listOf(inputCon)
                false -> inputCon.split(separator)
            }
            val semaphore = when(semaphoreInt > 0){
                true -> Semaphore(semaphoreInt)
                else -> null
            }
            return withContext(Dispatchers.IO) {
                val indexToOutputList = when(semaphore == null) {
                    false ->  inputConList.mapIndexed { index, inputLine ->
                        async {
                            semaphore.withPermit {
                                val indexToVarNamePair = Pair(indexVarName, index.toString())
                                getCmdOutput(
                                    busyboxExecutor,
                                    shellMapArgs,
                                    index,
                                    inputLine,
                                    procNameToCmdKeyMapList,
                                    indexToVarNamePair,
                                    defaultTimeoutInt,
                                    delimiter,
                                    fieldVarPrefix,
                                    where,
                                )
                            }
                        }
                    }
                    else -> inputConList.mapIndexed { index, inputLine ->
                        async {
                            val indexToVarNamePair = Pair(indexVarName, index.toString())
                            val indexAndOutputToErr = getCmdOutput(
                                busyboxExecutor,
                                shellMapArgs,
                                index,
                                inputLine,
                                procNameToCmdKeyMapList,
                                indexToVarNamePair,
                                defaultTimeoutInt,
                                delimiter,
                                fieldVarPrefix,
                                where,
                            )
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "llshell_${index}.txt").absolutePath,
//                                listOf(
//                                    "indexToOutput: ${indexToOutput}",
//                                ).joinToString("\n\n") + "\n========\n\n"
//                            )
                            indexAndOutputToErr
                        }
                    }
                }
                val replaceStrToFuncErrList = indexToOutputList.awaitAll().sortedBy {
                        indexToReplaceJob ->
                    indexToReplaceJob.first
                }.map {
                        indexToReplaceJob ->
                    indexToReplaceJob.second
                }
                replaceStrToFuncErrList.firstOrNull {
                        replaceStrToFuncErr ->
                    replaceStrToFuncErr.second != null
                }?.second?.let {
                        funcErr ->
                    return@withContext Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val replaceStr = replaceStrToFuncErrList.filter {
                    (replaceStr, _) ->
                    replaceStr != null
                }.map {
                        (replaceStr, _) ->
                    replaceStr
                }.joinToString(joinStr)
                Pair(
                    replaceStr,
                    null) to null
            }

        }

        private fun getCmdOutput(
            busyboxExecutor: BusyboxExecutor?,
            shellMapArgs: ShellMethodArgClass.MapArgs,
            index: Int,
            inputLine: String,
            procNameToCmdKeyMapList: List<Pair<String, Map<String, String>>>,
            indexToVarNamePair: Pair<String, String>,
            defaultTimeoutInt: Int,
            delimiter: String,
            fieldVarPrefix: String,
            where: String,
        ): Pair<Int, Pair<String?, FuncCheckerForSetting.FuncCheckErr?>> {
            val execCmdStr = makeCmd(
                shellMapArgs,
                procNameToCmdKeyMapList,
                inputLine,
                indexToVarNamePair,
                defaultTimeoutInt,
                delimiter,
                fieldVarPrefix,
            )
            return execGetCmdOutput(
                busyboxExecutor,
                execCmdStr,
                index,
                where,
            )
        }
        private fun makeCmd(
            shellMapArgs: ShellMethodArgClass.MapArgs,
            procNameToCmdKeyMapList: List<
                    Pair<String, Map<String, String>>
                    >,
            inputLine: String,
            indexToVarNamePair: Pair<String, String>,
            defaultTimeoutInt: Int,
            delimiter: String,
            fieldVarPrefix: String,
        ): String {
            val timeoutKeyClass = ShellMethodArgClass.MapArgs.MapEnumArgs.TIMEOUT
            val cmdKey = shellMapArgs.cmdKeyToDefaultValueStr.first
            val pipCmdList = procNameToCmdKeyMapList.mapIndexed { index, (procName, cmdMap) ->
//                val argClass = mapEnumArgsEntries.firstOrNull { arg ->
//                    arg.key == argName
//                } ?: return@mapIndexed String()
//                if(argClass != ShellMethodArgClass.MapArgs.MapEnumArgs.CMD) {
//                    return@mapIndexed String()
//                }
                val cmd = cmdMap.get(cmdKey)
                    ?: return String()
                val timeoutFloatStr = cmdMap.get(
                    timeoutKeyClass.key
                ) ?: defaultTimeoutInt.toString()
                val busyboxCmd = "${'$'}{b} ${cmd}"
                when(timeoutFloatStr.toInt() <= 0) {
                    true -> busyboxCmd
                    else -> "${'$'}{b} timeout -t ${timeoutFloatStr} ${busyboxCmd}"
                }
            }.filter {
                it.isNotEmpty()
            }
            val inputConForShellVar = inputLine.replace(
                "\"",
                "\\\""
            )
            val tempVarName = "temp_var"
            val defTempVar = """${tempVarName}="${inputConForShellVar}" """
            val echoTempVar = """echo "${'$'}${tempVarName}" """
            val updateTempVarCmdTemplate = """${tempVarName}="$(${echoTempVar} | %s )" """
            val updateTempVarCmdsCon = pipCmdList.map {
                updateTempVarCmdTemplate.format(it)
            }.joinToString("\n")
            val execShellCmd =
                listOf(
                    "set -ue",
                    defTempVar,
                    updateTempVarCmdsCon,
                    echoTempVar,
                    String()
                ).joinToString(";\n").replace(
                    "${'$'}${indexToVarNamePair.first}",
                    indexToVarNamePair.second
                )
            return SettingFuncTool.replaceShellCmdByFieldVarName(
                execShellCmd,
                inputConForShellVar,
                delimiter,
                fieldVarPrefix,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "llshell_${inputCon.take(4)}.txt").absolutePath,
//                listOf(
//                    "pipCmdList: ${pipCmdList}",
//                    "con: ${execShellCmd}"
//                ).joinToString("\n\n") + "\n========\n\n"
//            )
        }

        private fun makeTimeoutFloatErr(
            mapArgs: ShellMethodArgClass.MapArgs,
            timeoutFloatStr: String?,
            index: Int,
            where: String,
        ): FuncCheckerForSetting.FuncCheckErr {
            val spanTimeoutKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                mapArgs.timeoutKeyToDefaultValueStr.first
            )
            val spanTimeoutFloatStrName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                timeoutFloatStr.toString()
            )
            val spanIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                index.toString()
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            return FuncCheckerForSetting.FuncCheckErr(
                "In ${spanTimeoutKey} key (${spanIndex}), timeout convert float err: ${spanTimeoutFloatStrName}, ${spanWhere} "
            )

        }

        private fun makeProcNameToCmdKeyMapList(
            argsPairList: List<Pair<String, String>>,
            mapMethodArgs: ShellMethodArgClass.MapArgs,
            defaultTimeoutInt: Int,
            where: String,
        ): List<
                Pair<String, Map<String, String>>
                > {
            val timeoutKey = mapMethodArgs.timeoutKeyToDefaultValueStr.first
            val cmdKey = mapMethodArgs.cmdKeyToDefaultValueStr.first
            val noRegisterMapArgList = mapMethodArgs.entries.map {
                it.key
            }.filter {
                it != cmdKey
            }
            return argsPairList.mapIndexed {
                    index, (procName, valueStr) ->
//                val argClass = mapMethodArgs.entries.firstOrNull {
//                        arg ->
//                    arg.key == argName
//                } ?: return@mapIndexed String() to emptyMap()
//                val curArgKey = argClass.key
                if(
                    procName.isEmpty()
                    || noRegisterMapArgList.any {
                        argName ->
                        argName.startsWith(procName)
                                || argName.endsWith(procName)
                    }
//                    argClass != ShellMethodArgClass.MapArgs.MapEnumArgs.CMD

                    ) {
                    return@mapIndexed String() to emptyMap()
                }
                val cmdMap = mapOf(
                    cmdKey to valueStr,
                )
                val replaceStrMap = let {
                    val defaultTimeoutMap = mapOf(
                        timeoutKey to defaultTimeoutInt.toString()
                    )
                    val nextArgNameToValueStr =
                        argsPairList.getOrNull(index + 1)
                            ?: return@let defaultTimeoutMap
                    val nextArgName = nextArgNameToValueStr.first
                    when (nextArgName == timeoutKey) {
                        false -> defaultTimeoutMap
                        else -> {
                            val timeoutIntStr =
                                nextArgNameToValueStr.second
                            try {
                                timeoutIntStr.toFloat()
                            } catch (e: Exception){
                                makeTimeoutFloatErr(
                                    mapMethodArgs,
                                    timeoutIntStr,
                                    index,
                                    where,
                                )
                            }
                            mapOf(
                                timeoutKey to nextArgNameToValueStr.second
                            )
                        }
                    }
                }
                Pair(procName, (cmdMap + replaceStrMap))
            }.filter {
                it.first.isNotEmpty()
            }
        }
    }

    private fun execGetCmdOutput(
        busyboxExecutor: BusyboxExecutor?,
        execCmdStr: String,
        index: Int,
        where: String,
    ): Pair<Int, Pair<String?, FuncCheckerForSetting.FuncCheckErr?>> {
        val outToErr = busyboxExecutor?.getCmdOutputByErrHandle(
            execCmdStr,
            null,
        )
        val errStd = outToErr?.second
        if(!errStd.isNullOrEmpty()){
            val spanIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                "L${index}"
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            val spanExecCmdStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                execCmdStr.split("\n").joinToString(" ")
            )
            return index to Pair(
                null,
                FuncCheckerForSetting.FuncCheckErr(
                    "Shell err: ${errStd}, ${spanIndex}, madeCmd: ${spanExecCmdStr}, ${spanWhere}"
                )
            )
        }
        return index to Pair(outToErr?.first, null)
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ShellMethodArgClass,
    ){
        EXEC("exec", ShellMethodArgClass.ExecArgs),
        MAP("map", ShellMethodArgClass.MapArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ShellMethodArgClass {
        data object ExecArgs : ShellMethodArgClass(), ArgType {
            override val entries = ExecEnumArgs.entries
            val inputConKeyToDefaultValueStr = Pair(
                ExecEnumArgs.INPUT_CON.key,
                ExecEnumArgs.INPUT_CON.defaultValueStr,
            )
            val cmdKeyToDefaultValueStr = Pair(
                ExecEnumArgs.CMD.key,
                ExecEnumArgs.CMD.defaultValueStr,
            )
            val valueKeyToDefaultValueStr = Pair(
                ExecEnumArgs.VALUE.key,
                ExecEnumArgs.VALUE.defaultValueStr,
            )
            val encloseKeyToDefaultValueStr = Pair(
                ExecEnumArgs.ENCLOSE.key,
                ExecEnumArgs.ENCLOSE.defaultValueStr,
            )
            val timeoutKeyToDefaultValueStr = Pair(
                ExecEnumArgs.TIMEOUT.key,
                ExecEnumArgs.TIMEOUT.defaultValueStr,
            )
            val separatorKeyToDefaultValueStr = Pair(
                ExecEnumArgs.SEPARATOR.key,
                ExecEnumArgs.SEPARATOR.defaultValueStr,
            )
            val joinStrKeyToDefaultValueStr = Pair(
                ExecEnumArgs.JOIN_STR.key,
                ExecEnumArgs.JOIN_STR.defaultValueStr,
            )
            val semaphoreKeyToDefaultValueStr = Pair(
                ExecEnumArgs.SEMAPHORE.key,
                ExecEnumArgs.SEMAPHORE.defaultValueStr,
            )
            val onEscapeKeyToDefaultValueStr = Pair(
                ExecEnumArgs.ON_ESCAPE.key,
                ExecEnumArgs.ON_ESCAPE.defaultValueStr,
            )
            val indexVarNameKeyToDefaultValueStr = Pair(
                ExecEnumArgs.INDEX_VAR_NAME.key,
                ExecEnumArgs.INDEX_VAR_NAME.defaultValueStr,
            )
            val delimiterKeyToDefaultValueStr = Pair(
                ExecEnumArgs.DELIMITER.key,
                ExecEnumArgs.DELIMITER.defaultValueStr,
            )
            val fieldVarPrefixKeyToDefaultValueStr = Pair(
                ExecEnumArgs.FIELD_VAR_PREFIX.key,
                ExecEnumArgs.FIELD_VAR_PREFIX.defaultValueStr,
            )


            enum class ExecEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                INPUT_CON("inputCon", String(), FuncCheckerForSetting.ArgType.STRING),
                CMD("cmd", null, FuncCheckerForSetting.ArgType.STRING),
                VALUE("value", String(), FuncCheckerForSetting.ArgType.STRING),
                ENCLOSE("enclose", "\"", FuncCheckerForSetting.ArgType.STRING),
                TIMEOUT("timeout", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                SEPARATOR("separator", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEMAPHORE("semaphore", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                ON_ESCAPE("onEscape", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                INDEX_VAR_NAME("indexVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                DELIMITER("delimiter", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                FIELD_VAR_PREFIX("fieldVarPrefix", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object MapArgs : ShellMethodArgClass(), ArgType {
            override val entries = MapEnumArgs.entries
            val inputConKeyToDefaultValueStr = Pair(
                MapEnumArgs.INPUT_CON.key,
                MapEnumArgs.INPUT_CON.defaultValueStr
            )
            val cmdKeyToDefaultValueStr = Pair(
                MapEnumArgs.CMD.key,
                MapEnumArgs.CMD.defaultValueStr
            )
            val timeoutKeyToDefaultValueStr = Pair(
                MapEnumArgs.TIMEOUT.key,
                MapEnumArgs.TIMEOUT.defaultValueStr
            )
            val separatorKeyToDefaultValueStr = Pair(
                MapEnumArgs.SEPARATOR.key,
                MapEnumArgs.SEPARATOR.defaultValueStr
            )
            val joinStrKeyToDefaultValueStr = Pair(
                MapEnumArgs.JOIN_STR.key,
                MapEnumArgs.JOIN_STR.defaultValueStr
            )
            val semaphoreKeyToDefaultValueStr = Pair(
                MapEnumArgs.SEMAPHORE.key,
                MapEnumArgs.SEMAPHORE.defaultValueStr
            )
            val onEscapeKeyToDefaultValueStr = Pair(
                MapEnumArgs.ON_ESCAPE.key,
                MapEnumArgs.ON_ESCAPE.defaultValueStr
            )
            val indexVarNameKeyToDefaultValueStr = Pair(
                MapEnumArgs.INDEX_VAR_NAME.key,
                MapEnumArgs.INDEX_VAR_NAME.defaultValueStr
            )
            val delimiterKeyToDefaultValueStr = Pair(
                MapEnumArgs.DELIMITER.key,
                MapEnumArgs.DELIMITER.defaultValueStr
            )
            val fieldVarPrefixKeyToDefaultValueStr = Pair(
                MapEnumArgs.FIELD_VAR_PREFIX.key,
                MapEnumArgs.FIELD_VAR_PREFIX.defaultValueStr
            )


            enum class MapEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                INPUT_CON("inputCon", String(), FuncCheckerForSetting.ArgType.STRING),
                CMD("cmd", null, FuncCheckerForSetting.ArgType.STRING),
                TIMEOUT("timeout", 5.toString(), FuncCheckerForSetting.ArgType.INT),
                SEPARATOR("separator", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEMAPHORE("semaphore", 0.toString(), FuncCheckerForSetting.ArgType.INT),
                ON_ESCAPE("onEscape", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                INDEX_VAR_NAME("indexVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                DELIMITER("delimiter", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                FIELD_VAR_PREFIX("fieldVarPrefix", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}