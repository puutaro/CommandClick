package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.util.map.CmdClickMap


object SettingIfManager {

    private const val judgeTargetArgName = "judgeTarget"
    private const val judgeTargetArgIndex = -1

    fun handle(
        ifKeyName: String,
        judgeTargetStrSrc: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<Boolean?, IfCheckErr?> {
        val judgeTargetStr = checkNotExistIfArgsVarErr(
            ifKeyName,
            judgeTargetStrSrc,
            judgeTargetArgName,
            judgeTargetArgIndex,
//            varNameToValueStrMap,
        ).let {
                judgeTargetStrToErr ->
            if(
                judgeTargetStrToErr.second != null
            ) return null to judgeTargetStrToErr.second
            judgeTargetStrToErr.first
        } ?: return null to launchTypeCheckErr(
            ifKeyName,
            judgeTargetArgName,
            judgeTargetArgIndex,
            judgeTargetStrSrc,
            "not exist",
        )
        val ifCheckErr = checkArgs(
            ifKeyName,
            argsPairList,
//            varNameToValueStrMap,
        )
        if(
            ifCheckErr != null
        ) return null to ifCheckErr
        val conditionsIndex = 0
        val conditionsArgNameToValueStr = argsPairList.get(conditionsIndex)
        val conditionsStr = conditionsArgNameToValueStr.second
        val regexToMatchTypePairListToArgErr = ConditionsManager.makeRegexToMatchTypePairList(
            ifKeyName,
            conditionsStr,
            conditionsArgNameToValueStr.first,
            conditionsIndex,
            conditionsArgNameToValueStr.second,
        )
        val regexToMatchTypePairList =
            regexToMatchTypePairListToArgErr.first
        val argErr = regexToMatchTypePairListToArgErr.second
        if(
            regexToMatchTypePairList.isNullOrEmpty()
            || argErr != null
        ){
            return null to argErr
        }
        val concatConditionIndex = 1
        val concatConditionArgNameToValueStr =
            argsPairList.get(concatConditionIndex)
        val concatConditionArgName =
            concatConditionArgNameToValueStr.first
        val concatConditionStr =
            concatConditionArgNameToValueStr.second
        val concatCondition = ConcatCondition.entries.firstOrNull {
            it.str == concatConditionStr
        } ?: let {
            return raiseConcatConditionErr(
                ifKeyName,
                concatConditionArgName,
                concatConditionStr,
                concatConditionIndex
            )
        }

        val judgeList = ConditionsManager.makeJudgeList(
            judgeTargetStr,
            regexToMatchTypePairList,
        )
        return when(concatCondition){
            ConcatCondition.AND -> {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lifargs.txt").absolutePath,
//                    listOf(
//                        "argsPairList.get(0).second: ${argsPairList.get(0).second}",
//                        "argsPairList.get(1).second: ${argsPairList.get(1).second}",
//                        "judgeBaseRegex: ${judgeBaseRegex}",
//                        "matchType: ${matchType}",
//                    ).joinToString("\n")
//                )
                judgeList.all {
                    it
                }
            }
            ConcatCondition.OR -> {
                judgeList.any {
                    it
                }
            }
        } to null

    }

    private fun raiseConcatConditionErr(
        ifKeyName: String,
        concatConditionKeyName: String,
        concatConditionStr: String,
        argIndex: Int,
    ): Pair<Boolean?, IfCheckErr> {
        val spanConcatConditionKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            concatConditionKeyName,
        )
        val spanConcatConditionStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            concatConditionStr
        )
        return null to IfCheckErr(
            "${spanConcatConditionKeyName} must be ${
                ConcatCondition.entries.map { "'${it.str}'" }.joinToString(" or ")
            }: ${spanConcatConditionStr} ${makeErrWhere(ifKeyName, argIndex)}")
    }

    class IfCheckErr(
        val errMessage: String
    )

    private fun checkArgs(
        ifKeyName: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>?,
    ): IfCheckErr? {
        if(
            argsNameList.isEmpty()
        ) return null
        argsNameList.forEachIndexed {
                index, argName ->
            val argPair = argsPairList.getOrNull(index)
                ?: let {
                    val spanArgsNameListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        argsNameList.joinToString(", ")
                    )
                    val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        ifKeyName,
                    )
                    return IfCheckErr(
                        "'${spanSIfKey}' method all args not exist: args list: ${spanArgsNameListCon}"
                    )
                }
            if(
                argPair.first.isEmpty()
            ) {
                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    argName
                )
                val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    (index + 1).toString()
                )
                val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    ifKeyName,
                )
                return IfCheckErr("'${spanSIfKey}' method args not exist: arg name: ${spanArgName}, index: ${spanArgIndex}")
            }
            checkNotExistIfArgsVarErr(
                ifKeyName,
                argPair.second,
                argName,
                index,
//                varNameToValueStrMap,
            ).let {
                    strToArgErr ->
                val argErr = strToArgErr.second
                    ?: return@let
                return argErr
            }
        }
        return null
    }

    private enum class ConcatCondition(
        val str: String
    ){
        AND("and"),
        OR("or"),
    }

    private val argsNameList = listOf(
        "conditions",
        "concatCondition"
    )
//    private val argsNameList = listOf(
//        "judgeBaseRegex",
//        "matchType",
//    )

    private object ConditionsManager {

        private const val conditionsSeparator = '|'
        private enum class MatchType(
            val str: String,
        ) {
            EQUAL("equal"),
            NOT_EQUAL("notEqual"),
        }

        fun makeConditionsMap(conditionsMapCon: String?): Map<String, String> {
            if(
                conditionsMapCon.isNullOrEmpty()
            ) return emptyMap()
            return CmdClickMap.createMap(
                conditionsMapCon,
                conditionsSeparator,
            ).toMap()

        }
        enum class ConditionsKey(
            val key: String
        ) {
            REGEX("regex"),
            MATCH_TYPE("matchType"),
        }

        fun makeJudgeList(
            judgeTargetStr: String,
            regexToMatchTypePairList: List<Pair<Regex, MatchType>>,
        ): List<Boolean> {
            return regexToMatchTypePairList.map {
                regexToMatchType ->
                val regex =  regexToMatchType.first
                val isMatch =
                    regex.matches(judgeTargetStr)
                val matchType =  regexToMatchType.second
                when(matchType){
                    MatchType.EQUAL -> isMatch
                    MatchType.NOT_EQUAL -> !isMatch
                }
            }.sorted().distinct()
        }


        fun makeRegexToMatchTypePairList(
            ifKeyName: String,
            conditionsMapCon: String,
            argName: String,
            argIndex: Int,
            argStr: String,
        ): Pair<
                List<Pair<Regex, MatchType>>?,
                IfCheckErr?
                > {

            val regexKeyName = ConditionsKey.REGEX.key
            val matchTypeKey = ConditionsKey.MATCH_TYPE.key
            val matchTypeStrList = MatchType.entries
            val conditionsMap = makeConditionsMap(
                conditionsMapCon
            )
            val regexKeyList = conditionsMap.map {
                val key = it.key
                if(
                    !key.startsWith(regexKeyName)
                ) return@map String()
                key
            }.filter {
                it.isNotEmpty()
            }.sorted().distinct()
            val regexToMatchTypePairList = regexKeyList.map {
                    regexNumKey ->
                val numSuffix = try {
                    regexNumKey.removePrefix(regexKeyName).toInt()
                } catch(e: Exception){
                    val spanRegexKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        regexKeyName
                    )
                    val spanRegexNumKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        regexNumKey
                    )
                    return null to launchTypeCheckErr(
                        ifKeyName,
                        argName,
                        argIndex,
                        argStr,
                        "${spanRegexKey} suffix must be number: ${spanRegexNumKey}, ${makeErrWhere(ifKeyName, argIndex)}"
                    )
                }
                val regexStr =
                    conditionsMap.get(
                        regexNumKey
                    )
                if(regexStr.isNullOrEmpty()){
                    val spanRegexNumKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        regexNumKey
                    )
                    return null to launchTypeCheckErr(
                        ifKeyName,
                        argName,
                        argIndex,
                        argStr,
                        "${spanRegexNumKey} not exist: ${makeErrWhere(ifKeyName, argIndex)}"
                    )
                }
                val regex = try {
                    Regex(regexStr)
                } catch (e: Exception) {
                    val spanRegexNumKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        regexNumKey
                    )
                    val spanJudgeBaseRegexStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        regexStr
                            .replace("<", "＜")
                            .replace(">", "＞")
                            .replace("%", "％    ")
                    )
                    return null to IfCheckErr("${spanRegexNumKey} key failure to compile: ${spanJudgeBaseRegexStr}, ${makeErrWhere(ifKeyName, argIndex)}")
                }
                val matchTypeNumKey = "${matchTypeKey}${numSuffix}"
                val matchTypeNumValueSrc = conditionsMap.get(
                    matchTypeNumKey
                )
                if(matchTypeNumValueSrc.isNullOrEmpty()){
                    val spanMatchTypeNumKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        matchTypeNumKey
                    )
                    return null to launchTypeCheckErr(
                        ifKeyName,
                        argName,
                        argIndex,
                        argStr,
                        "${spanMatchTypeNumKey} not exist: ${makeErrWhere(ifKeyName, argIndex)}"
                    )
                }
                val matchTypeNumValue =
                    matchTypeStrList.firstOrNull {
                        it.str == matchTypeNumValueSrc
                    }
                if(matchTypeNumValue == null){
                    val spanMatchTypeNumKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        matchTypeNumKey
                    )
                    val spanMatchTypeNumValueSrc = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        matchTypeNumValueSrc
                    )
                    return null to launchTypeCheckErr(
                        ifKeyName,
                        argName,
                        argIndex,
                        argStr,
                        "${spanMatchTypeNumKey} must be ${
                            matchTypeStrList.map { "'${it.str}'" 
                            }.joinToString(" or ")
                        }: ${spanMatchTypeNumValueSrc}, ${makeErrWhere(ifKeyName, argIndex)}"
                    )
                }
                regex to matchTypeNumValue
            }
            if(
                regexToMatchTypePairList.isEmpty()
            ){
                return null to IfCheckErr("Arg empty err: ${makeErrWhere(ifKeyName, argIndex)}")
            }
            return Pair(
                regexToMatchTypePairList,
                null
            )
        }
    }

    private fun makeErrWhere(
        ifKeyName: String,
        argIndex: Int,
    ): String {
        val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argsNameList.get(argIndex)
        )
        val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argIndex.toString()
        )
        val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            ifKeyName,
        )
        return "arg name: ${spanArgName}, index: ${spanArgIndex}, setting key: ${spanSIfKey}"
    }

    private fun checkNotExistIfArgsVarErr(
        ifKeyName: String,
        argStr: String,
        argName: String,
        index: Int,
//        varNameToValueStrMap: Map<String, String?>?,
    ): Pair<String?, IfCheckErr?> {
        if(
            argStr.isNotEmpty()
        ) return argStr to null
        return null to launchTypeCheckErr(
            ifKeyName,
            argName,
            index,
            argStr,
            "not exist string if args var name"
        )
    }

    private fun launchTypeCheckErr(
        ifKeyName: String,
        argName: String,
        index: Int,
        argStr: String,
        valurStrErrBody: String,
    ): IfCheckErr {
        val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argName
        )
        val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            (index + 1).toString()
        )
        val spanArgStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argStr
        )
        val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            ifKeyName,
        )
        return IfCheckErr(
            "Arg ${spanArgName} ${valurStrErrBody}: ${spanArgStr}, ${spanSIfKey} arg, index: ${spanArgIndex}"
        )
    }
}