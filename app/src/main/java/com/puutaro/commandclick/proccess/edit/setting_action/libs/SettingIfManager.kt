package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool


object SettingIfManager {

    private const val judgeTargetArgName = "judgeTarget"

    private enum class IfArgs(
        val str: String
    ){
        REGEX("regex"),
        MATCH_TYPE("matchType"),
        CONCAT_CONDITION("concatCondition"),
    }

    private enum class ConcatCondition(
        val str: String
    ){
        AND("and"),
        OR("or"),
    }

    private enum class MatchType(
        val str: String,
    ) {
        EQUAL("equal"),
        NOT_EQUAL("notEqual"),
    }

    class IfCheckErr(
        val errMessage: String
    )

    fun handle(
        ifKeyName: String,
        judgeTargetStrSrc: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<Boolean?, IfCheckErr?> {
        val judgeTargetStr = judgeTargetStrSrc.ifEmpty {
            val errWhere = makeLogErrWhere(
                ifKeyName,
                judgeTargetArgName,
                -1,
                makeArgsPairListCon(argsPairList),
            )
            return null to IfCheckErr(
                "${judgeTargetArgName} not exist: ${errWhere}"
            )
        }
        return IfArgMacher.match(
            ifKeyName,
            judgeTargetStr,
            argsPairList
        )
    }

    private object IfArgMacher {


        fun match(
            ifKeyName: String,
            judgeTargetStr: String,
            argNameToSubKeyMapPairList: List<Pair<String, String>>
        ): Pair<Boolean?, IfCheckErr?>{
            val matchListToErr = makeMatchListToErr(
                ifKeyName,
                judgeTargetStr,
                argNameToSubKeyMapPairList
            )
            val matchList = matchListToErr.first
            val argErr = matchListToErr.second
            if(
                matchList == null
                || argErr != null
                ){
                return null to argErr
            }
            if(matchList.size == 1){
                return matchList.first() to null
            }
            val concatConditionKey =
                IfArgs.CONCAT_CONDITION.str
            val concatConditionSTr = argNameToSubKeyMapPairList.toMap().get(
                concatConditionKey
            )
            val concatCondition = ConcatCondition.entries.firstOrNull {
                it.str == concatConditionSTr
            } ?: let {
                val spanConcatConditionKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    concatConditionKey,
                )
                val spanConcatConditionStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    concatConditionSTr.toString()
                )
                return null to IfCheckErr(
                    "${spanConcatConditionKeyName} must be ${
                        ConcatCondition.entries.map { "'${it.str}'" }.joinToString(" or ")
                    }: ${spanConcatConditionStr} ${makeLogErrWhere(
                        ifKeyName, 
                        concatConditionKey,
                        -1,
                        makeArgsPairListCon(argNameToSubKeyMapPairList)
                    )}")
            }
            val matchResult = when(concatCondition){
                ConcatCondition.AND -> {
                    matchList.all {
                        it
                    }
                }
                ConcatCondition.OR -> {
                    matchList.any {
                        it
                    }
                }
            }
            return matchResult to null
        }

        private fun makeMatchListToErr(
            ifKeyName: String,
            judgeTargetStr: String,
            argsPairList: List<Pair<String, String>>,
        ): Pair<List<Boolean>?, IfCheckErr?> {
            val argNameToSubKeyMapList = makeArgNameToSubKeyMapList(
                argsPairList,
            )
            val regexArgKey = IfArgs.REGEX.str
            argNameToSubKeyMapList.toMap().get(regexArgKey) ?: let {
                val spanRegexKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    regexArgKey
                )
                val errWhere = makeLogErrWhere(
                    ifKeyName,
                    regexArgKey,
                    -1,
                    makeArgsPairListCon(argsPairList)
                )
                return null to IfCheckErr("${spanRegexKey} key not exist: ${errWhere}")
            }
            val matchTypeKey = IfArgs.MATCH_TYPE.str
            val matchTypeStrList = MatchType.entries
            val matchList = argNameToSubKeyMapList.mapIndexed {
                argIndex, argNameToSubKeyMap ->
                val argName = argNameToSubKeyMap.first
                val ifArgs = IfArgs.entries.firstOrNull {
                    arg ->
                    arg.str == argName
                } ?: let {
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        regexArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("args not exist: ${errWhere}")
                }
                val subKeyMap = argNameToSubKeyMap.second
                when(ifArgs) {
                    IfArgs.CONCAT_CONDITION,
                    IfArgs.MATCH_TYPE
                        -> null
                    IfArgs.REGEX -> {
                        val regexStr = subKeyMap.get(regexArgKey).toString()
                        val regex = try {
                            regexStr.toRegex()
                        } catch (e: Exception) {
                            val spanRegexKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errRedCode,
                                IfArgs.REGEX.str
                            )
                            val spanJudgeRegexStr =
                                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                    CheckTool.errRedCode,
                                    regexStr
                                )
                            val errWhere = makeLogErrWhere(
                                ifKeyName,
                                regexArgKey,
                                argIndex,
                                makeArgsPairListCon(argsPairList)
                            )
                            return null to IfCheckErr("${spanRegexKey} key failure to compile: ${spanJudgeRegexStr}, ${errWhere}")
                        }
                        val isMatch = regex.matches(judgeTargetStr)
                        val matchTypeStr = subKeyMap.get(matchTypeKey)
                        val matchType = matchTypeStrList.firstOrNull {
                                it.str == matchTypeStr
                            } ?: let {
                                val spanMatchTypeKey =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.ligthBlue,
                                        matchTypeKey
                                    )
                                val spanMatchTypeValueStr =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.errRedCode,
                                        matchTypeStr.toString()
                                    )
                                return null to IfCheckErr(
                                   "${spanMatchTypeKey} must be ${
                                        matchTypeStrList.map {
                                            "'${it.str}'"
                                        }.joinToString(" or ")
                                    }: ${spanMatchTypeValueStr}, ${
                                        makeLogErrWhere(
                                            ifKeyName,
                                            matchTypeKey,
                                            argIndex,
                                            makeArgsPairListCon(argsPairList)
                                        )
                                    }"
                                )
                            }
                        val matchResult = when(matchType){
                            MatchType.EQUAL -> isMatch
                            MatchType.NOT_EQUAL -> !isMatch
                        }
                        matchResult
                    }
                }
            }.filter {
                it != null
            }.map {
                it ?: true
            }
            return matchList to null
        }


        private fun makeArgNameToSubKeyMapList(
            argsPairList: List<Pair<String, String>>,
        ): List<
                Pair<
                        String,
                        Map<String, String>
                        >
                > {
            val matchTypeArgName = IfArgs.MATCH_TYPE.str
            return argsPairList.mapIndexed {
                    index, argNameToValueStr ->
                val argName = argNameToValueStr.first
                val argClass = IfArgs.entries.firstOrNull {
                    arg ->
                    arg.str == argName
                } ?: return@mapIndexed Pair(String(), emptyMap())
                val valueStr = argNameToValueStr.second
                when(argClass) {
                    IfArgs.CONCAT_CONDITION -> {
                        val mainSubKeyMap = mapOf(
                            argName to valueStr,
                        )
                        Pair(argName, mainSubKeyMap)
                    }
                    IfArgs.MATCH_TYPE ->
                        Pair(String(), emptyMap())
                    IfArgs.REGEX -> {
                        val funcPartMap = mapOf(
                            argName to valueStr,
                        )
                        val argsMap = let {
                            val nextArgNameToValueStr =
                                argsPairList.getOrNull(index + 1)
                                    ?: return@let emptyMap()
                            val nextArgName = nextArgNameToValueStr.first
                            when (nextArgName == matchTypeArgName) {
                                false -> emptyMap()
                                else -> mapOf(
                                    matchTypeArgName to nextArgNameToValueStr.second
                                )
                            }
                        }
                        Pair(argName, (funcPartMap + argsMap))
                    }
                }
            }.filter {
                it.first.isNotEmpty()
            }
        }
    }

    private fun makeLogErrWhere(
        ifKeyName: String,
        argName: String,
        argIndex: Int,
        subKeyCon: String,
    ): String {
        val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argName
        )
        val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            argIndex.toString()
        )
        val spanSIfKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            ifKeyName,
        )
        val spanSubKeyCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errBrown,
            subKeyCon,
        )
        val errWhere =
            "arg name: ${spanArgName}, index: ${spanArgIndex}, setting key: ${spanSIfKey}, subKeyCon: ${spanSubKeyCon}"
        return CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.ligthBlue,
            errWhere
        )
    }

    private fun makeArgsPairListCon(
        argsPairList: List<Pair<String, String>>,
    ): String{
        return argsPairList.map{
            "${it.first}=${it.second}"
        }.joinToString(",")
            .replace("<", "＜")
            .replace(">", "＞")
            .replace("%", "％    ")
    }
}