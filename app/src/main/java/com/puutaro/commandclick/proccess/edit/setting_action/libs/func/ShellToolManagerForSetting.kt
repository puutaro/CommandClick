package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.EvalForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.str.QuoteTool
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
                val cmdStr =
                    FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.cmdListKeyToIndex,
                        where
                    ).let { cmdStrToErr ->
                    val funcErr = cmdStrToErr.second
                        ?: return@let cmdStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val cmd = QuoteTool.splitBySurroundedIgnore(
                    cmdStr,
                    cmdSeparator
                ).map {
                    "${'$'}{b} ${it} "
                }.joinToString(cmdSeparator.toString())
                val outToErr = busyboxExecutor?.getCmdOutputByErrHandle(
                    cmd,
                    null
                )
                val errStd = outToErr?.second
                if(!errStd.isNullOrEmpty()){
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to FuncCheckerForSetting.FuncCheckErr(
                        "Shell err: ${errStd}, ${spanWhere}"
                    )
                }
                return Pair (
                    outToErr?.first,
                    null
                ) to null
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
                    args.escapeKeyToDefaultValueStr,
                    where
                ).let { escapeToErr ->
                    val funcErr = escapeToErr.second
                        ?: return@let escapeToErr.first != switchOff
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
                    where,
                )
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
            where: String,
        ): Pair<Int, Pair<String?, FuncCheckerForSetting.FuncCheckErr?>> {
            val execCmdStr = makeCmd(
                shellMapArgs,
                procNameToCmdKeyMapList,
                inputLine,
                indexToVarNamePair,
                defaultTimeoutInt,
            )
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
        private fun makeCmd(
            shellMapArgs: ShellMethodArgClass.MapArgs,
            procNameToCmdKeyMapList: List<
                    Pair<String, Map<String, String>>
                    >,
            inputLine: String,
            indexToVarNamePair: Pair<String, String>,
            defaultTimeoutInt: Int,
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
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "llshell_${inputCon.take(4)}.txt").absolutePath,
//                listOf(
//                    "pipCmdList: ${pipCmdList}",
//                    "con: ${execShellCmd}"
//                ).joinToString("\n\n") + "\n========\n\n"
//            )
            return execShellCmd
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
            val cmdListKeyToIndex = Pair(
                ExecEnumArgs.CMD_LIST.key,
                ExecEnumArgs.CMD_LIST.index
            )

            enum class ExecEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                CMD_LIST("cmdList", 0, FuncCheckerForSetting.ArgType.STRING),
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
            val escapeKeyToDefaultValueStr = Pair(
                MapEnumArgs.ESCAPE.key,
                MapEnumArgs.ESCAPE.defaultValueStr
            )
            val indexVarNameKeyToDefaultValueStr = Pair(
                MapEnumArgs.INDEX_VAR_NAME.key,
                MapEnumArgs.INDEX_VAR_NAME.defaultValueStr
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
                ESCAPE("escape", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                INDEX_VAR_NAME("indexVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}