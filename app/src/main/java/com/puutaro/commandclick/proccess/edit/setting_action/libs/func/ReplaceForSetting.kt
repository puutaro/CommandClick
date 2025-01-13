package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object ReplaceForSetting {

    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >
    {
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
        val args = methodNameClass.args
        return when(args){
            is ReplaceArgClass.MapEvalArgs -> {
                val replaceEnumArgsEntries = args.entries
                val formalArgIndexToNameToTypeList = replaceEnumArgsEntries.mapIndexed {
                        index, formalArgsNameToType ->
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
                val defaultSeparator =
                    args.separatorKeyToDefaultValueStr.second
                val separator = argsPairList.toMap().get(
                    args.separatorKeyToDefaultValueStr.first
                ) ?: defaultSeparator
                ?: defaultNullMacroStr
                val argNameToSubKeyMapList = makeArgNameToSubKeyMapList(
                    argsPairList,
                    args.entries
                )
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
//                        val joinStrSrc = joinStrToErr.first
//                        val defaultJoinStr = args.joinStrKeyToDefaultValueStr.second
//                        if (
//                            joinStrSrc == defaultJoinStr
//                        ) separator
//                        else joinStrSrc
                    }
                }
                val semaphoreInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.semaphoreKeyToDefaultValueStr,
                    where
                ).let { joinStrToErr ->
                    val funcErr = joinStrToErr.second
                        ?: return@let joinStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.removeRegexKeyToDefaultValueStr,
                    where
                ).let { removeRegexConToErr ->
                    val funcErr = removeRegexConToErr.second
                        ?: return@let removeRegexConToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val inputConList = when(separator == defaultNullMacroStr){
                    true -> listOf(inputCon)
                    false -> inputCon.split(separator)
                }
                val semaphore = when(semaphoreInt > 0){
                    true -> Semaphore(semaphoreInt)
                    else -> null
                }
                withContext(Dispatchers.IO) {
                    val indexToReplaceJobList = when(semaphore == null) {
                        false ->  inputConList.mapIndexed { index, inputLine ->
                            async {
                                semaphore.withPermit {
                                    index to Replacer.replace(
                                        argNameToSubKeyMapList,
                                        args,
                                        inputLine,
                                        where,
                                    )
                                }
                            }
                        }
                        else -> inputConList.mapIndexed { index, inputLine ->
                            async {
                                index to Replacer.replace(
                                    argNameToSubKeyMapList,
                                    args,
                                    inputLine,
                                    where,
                                )
                            }
                        }
                    }
                    val replaceStrToFuncErrList = indexToReplaceJobList.awaitAll().sortedBy {
                        indexToReplaceJob ->
                        indexToReplaceJob.first
                    }.map {
                        indexToReplaceJob ->
                        indexToReplaceJob.second
                    }
                    replaceStrToFuncErrList.firstOrNull {
                        replaceStrToFuncErr ->
                        replaceStrToFuncErr.second != null
                    }?.let {
                        funcErr ->
                        return@let null to funcErr
                    }
                    val replaceStr = replaceStrToFuncErrList.map {
                        replaceStrToFuncErr ->
                        replaceStrToFuncErr.first
                    }.joinToString(joinStr)
                    return@withContext Pair(replaceStr, null) to null
                }
            }
        }
    }

    private object Replacer {
        fun replace(
            argNameToSubKeyMapList: List<Pair<String, Map<String, String>>>,
            replaceForSettingArgs: ReplaceArgClass.MapEvalArgs,
            inputCon: String,
            where: String,
        ): Pair<String?, FuncCheckerForSetting.FuncCheckErr?> {
            val removeRegexKeyClass =
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.REMOVE_REGEX
            val replaceKeyClass =
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.REPLACE_STR
            var replaceInputCon = inputCon
            val mapEnumArgsEntries = replaceForSettingArgs.entries
            argNameToSubKeyMapList.forEachIndexed { index, (argName, subKeyMap) ->
                val argClass = mapEnumArgsEntries.firstOrNull { arg ->
                    arg.key == argName
                } ?: return@forEachIndexed
                if(argClass != removeRegexKeyClass) return@forEachIndexed
                val removeRegexStr =
                    subKeyMap.get(
                        removeRegexKeyClass.key
                    )
                val removeRegex = try {
                    removeRegexStr
                        ?: throw Exception()
                    removeRegexStr.toRegex()
                } catch (e: Exception) {
                    return Pair(
                        null,
                        makeRegexCompileErr(
                            replaceForSettingArgs,
                            removeRegexStr,
                            index,
                            where,
                        )
                    )
                }
                val replaceStr =
                    subKeyMap.get(
                        replaceKeyClass.key
                    ) ?: String()
                val tempReplaceInputCon = try {
                    replaceInputCon.replace(
                        removeRegex,
                        replaceStr
                    )
                } catch (e: Exception) {
                    return Pair(
                        null,
                        makeReplaceErr(
                            replaceForSettingArgs,
                            inputCon,
                            removeRegexStr,
                            replaceStr,
                            index,
                            where,
                        )
                    )
                }
                replaceInputCon = tempReplaceInputCon
            }
            return Pair(
                replaceInputCon,
                null
            )
        }

        private fun makeRegexCompileErr(
            replaceForSettingArgs: ReplaceArgClass.MapEvalArgs,
            removeRegexStr: String?,
            index: Int,
            where: String,
        ): FuncCheckerForSetting.FuncCheckErr {
            val spanRemoveRegexKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                replaceForSettingArgs.removeRegexKeyToDefaultValueStr.first
            )
            val spanRemoveRegexStrName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                removeRegexStr.toString()
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
                "In ${spanRemoveRegexKey} key (${spanIndex}), regex compile err: ${spanRemoveRegexStrName}, ${spanWhere} "
            )

        }

        private fun makeReplaceErr(
            replaceForSettingArgs: ReplaceArgClass.MapEvalArgs,
            inputCon: String,
            removeRegexStr: String?,
            replaceStr: String,
            index: Int,
            where: String,
        ): FuncCheckerForSetting.FuncCheckErr {
            val spanInputConKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                replaceForSettingArgs.inputConKeyToDefaultValueStr.first
            )
            val spanInputConValue = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                inputCon
            )
            val spanRemoveRegexKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                replaceForSettingArgs.removeRegexKeyToDefaultValueStr.first
            )
            val spanRemoveRegexStrName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                removeRegexStr.toString()
            )
            val spanReplaceKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                replaceForSettingArgs.replaceStrKeyToDefaultValueStr.first
            )
            val spanReplaceStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                replaceStr
            )
            val spanIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                index.toString()
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            val plusWhere = listOf(
                "index: ${spanIndex}",
                "${spanReplaceKey}: ${spanReplaceStr}",
                "${spanInputConKey}: ${spanInputConValue}",
                "${spanRemoveRegexKey}: ${spanRemoveRegexStrName}"
            ).joinToString(", ")
            return FuncCheckerForSetting.FuncCheckErr(
                "Regex replace err: ${plusWhere}, ${spanWhere}"
            )
        }
    }

    private fun makeArgNameToSubKeyMapList(
        argsPairList: List<Pair<String, String>>,
        mapEnumArgsEntries: EnumEntries<ReplaceArgClass. MapEvalArgs. MapEnumArgs>
    ): List<
            Pair<
                    String,
                    Map<String, String>
                    >
            > {
        return argsPairList.mapIndexed {
                index, argNameToValueStr ->
            val argName = argNameToValueStr.first
            val argClass = mapEnumArgsEntries.firstOrNull {
                    arg ->
                arg.key == argName
            } ?: return@mapIndexed Pair(String(), emptyMap())
            val valueStr = argNameToValueStr.second
            val replaceStrKeyClass =
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.REPLACE_STR
            when(argClass) {
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.SEMAPHORE,
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.JOIN_STR,
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.INPUT_CON ->
                   String() to emptyMap()
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.SEPARATOR
                    -> {
                    val mainSubKeyMap = mapOf(
                        argName to valueStr,
                    )
                    Pair(argName, mainSubKeyMap)
                }
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.REPLACE_STR ->
                    Pair(String(), emptyMap())
                ReplaceArgClass.MapEvalArgs.MapEnumArgs.REMOVE_REGEX -> {
                    val removeRegexMap = mapOf(
                        argName to valueStr,
                    )
                    val replaceStrMap = let {
                        val nextArgNameToValueStr =
                            argsPairList.getOrNull(index + 1)
                                ?: return@let emptyMap()
                        val nextArgName = nextArgNameToValueStr.first
                        val replaceStrKey = replaceStrKeyClass.key
                        when (nextArgName == replaceStrKey) {
                            false -> emptyMap()
                            else -> mapOf(
                                replaceStrKey to nextArgNameToValueStr.second
                            )
                        }
                    }
                    Pair(argName, (removeRegexMap + replaceStrMap))
                }
            }
        }.filter {
            it.first.isNotEmpty()
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ReplaceArgClass
    ) {
        MAP("map", ReplaceArgClass.MapEvalArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ReplaceArgClass {
        data object MapEvalArgs : ReplaceArgClass(),
            ArgType {
            override val entries = MapEnumArgs.entries
            val inputConKeyToDefaultValueStr = Pair(
                MapEnumArgs.INPUT_CON.key,
                MapEnumArgs.INPUT_CON.defaultValueStr
            )
            val separatorKeyToDefaultValueStr = Pair(
                MapEnumArgs.SEPARATOR.key,
                MapEnumArgs.SEPARATOR.defaultValueStr
            )
            val removeRegexKeyToDefaultValueStr = Pair(
                MapEnumArgs.REMOVE_REGEX.key,
                MapEnumArgs.REMOVE_REGEX.defaultValueStr
            )
            val replaceStrKeyToDefaultValueStr = Pair(
                MapEnumArgs.REPLACE_STR.key,
                MapEnumArgs.REPLACE_STR.defaultValueStr
            )
            val joinStrKeyToDefaultValueStr = Pair(
                MapEnumArgs.JOIN_STR.key,
                MapEnumArgs.JOIN_STR.defaultValueStr
            )
            val semaphoreKeyToDefaultValueStr = Pair(
                MapEnumArgs.SEMAPHORE.key,
                MapEnumArgs.SEMAPHORE.defaultValueStr
            )

            enum class MapEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ) {
                INPUT_CON("inputCon", null, FuncCheckerForSetting.ArgType.STRING),
                REMOVE_REGEX("removeRegex", null, FuncCheckerForSetting.ArgType.STRING),
                REPLACE_STR("replaceStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEMAPHORE("semaphore", 0.toString(), FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}