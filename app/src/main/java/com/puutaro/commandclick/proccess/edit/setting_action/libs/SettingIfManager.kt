package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object SettingIfManager {

    private const val judgeTargetArgName = "judgeTarget"

    private enum class IfArgs(
        val str: String
    ){
        MATCH_TYPE("matchType"),
        BASE("base"),
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
        EQUAL("=="),
        NOT_EQUAL("!="),
        IN("in"),
        NOT_IN("!in"),
        LESS("<"),
        LESS_EQUAL("<="),
        GREATER(">"),
        GREATER_EQUAL(">="),
    }

    private val numMachTypeList = listOf(
        MatchType.LESS,
        MatchType.LESS_EQUAL,
        MatchType.GREATER,
        MatchType.GREATER_EQUAL,
    )

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
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "ljudge_match.txt").absolutePath,
//                listOf(
//                    "judgeTargetStr: ${judgeTargetStr}",
//                    "matchList: ${matchList}",
//                    "argErr: ${argErr}",
//                ).joinToString("\n\n") + "\n\n==========\n\n"
//            )
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
            val matchTypeArgKey = IfArgs.MATCH_TYPE.str
            argNameToSubKeyMapList.toMap().get(matchTypeArgKey) ?: let {
                val spanBaseKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    matchTypeArgKey
                )
                val errWhere = makeLogErrWhere(
                    ifKeyName,
                    matchTypeArgKey,
                    -1,
                    makeArgsPairListCon(argsPairList)
                )
                return null to IfCheckErr("${spanBaseKey} key not exist: ${errWhere}")
            }
            val matchTypeKeyClass = IfArgs.MATCH_TYPE
            val matchTypeStrList = MatchType.entries
            val baseKey = IfArgs.BASE.str
            val matchList = argNameToSubKeyMapList.mapIndexed {
                argIndex, argNameToSubKeyMap ->
                val argName = argNameToSubKeyMap.first
                val ifArgs = IfArgs.entries.firstOrNull {
                    arg ->
                    arg.str == argName
                } ?: let {
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        matchTypeArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("args not exist: ${errWhere}")
                }
                if(
                    ifArgs != matchTypeKeyClass
                ) return@mapIndexed null
                val subKeyMap = argNameToSubKeyMap.second
                val matchTypeKey = ifArgs.str
                val matchTypeStr = subKeyMap.get(matchTypeKey)
                val baseCon = subKeyMap.get(baseKey) ?: let {
                    val spanBaseKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        baseKey
                    )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        baseKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanBaseKey} key con not exist: ${errWhere}")
                }
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
                val isMatchToErr = when(matchType){
                    MatchType.EQUAL ->{
                        Matcher.isEqual(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                        )
//                        let{
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "ljudge.txt").absolutePath,
//                                listOf(
//                                    "judgeTargetStr: ${judgeTargetStr}",
//                                    "baseCon: ${baseCon}",
//                                    "argsPairList: ${argsPairList}",
//                                    "it: ${it}",
//                                ).joinToString("\n\n") + "\n\n==========\n\n"
//                            )
//                            it
//                        }
                    }
                    MatchType.NOT_EQUAL ->{
                        Matcher.isEqual(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                        ).let {
                            (isMatch, err) ->
                            isMatch?.let {
                                !it
                            } to err
                        }
                    }
                    MatchType.IN ->
                        Matcher.isIn(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                        )
                    MatchType.NOT_IN ->
                        Matcher.isIn(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                        ).let {
                                (isMatch, err) ->
                            isMatch?.let {
                                !it
                            } to err
                        }
                    MatchType.LESS ->
                        Matcher.isNumCompare(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                    MatchType.LESS_EQUAL ->
                        Matcher.isNumCompare(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                    MatchType.GREATER ->
                        Matcher.isNumCompare(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                    MatchType.GREATER_EQUAL ->
                        Matcher.isNumCompare(
                            judgeTargetStr,
                            baseCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                }.let {
                    (isMatch, err) ->
                    if(
                        err != null
                    ) return null to err
                    isMatch
                }
                isMatchToErr
            }.filter {
                it != null
            }.map {
                it ?: true
            }
            return matchList to null
        }

        private object Matcher {
            fun isNumCompare(
                judgeTargetStr: String,
                baseFloatStr: String,
                argsPairList: List<Pair<String, String>>,
                ifKeyName: String,
                argIndex: Int,
                matchType: MatchType,
            ): Pair<Boolean?, IfCheckErr?> {
                val judgeTargetFloat = strToFloat(
                    argsPairList,
                    judgeTargetStr,
                    judgeTargetArgName,
                    argIndex,
                    ifKeyName,
                ).let {
                    (judgeTargetFloat, err) ->
                    if(err != null){
                        return null to err
                    }
                    judgeTargetFloat
                }
                val baseArgKey = IfArgs.BASE.str
                val baseFloat = strToFloat(
                    argsPairList,
                    baseFloatStr,
                    baseArgKey,
                    argIndex,
                    ifKeyName,
                ).let {
                        (baseFloat, err) ->
                    if(err != null){
                        return null to err
                    }
                    baseFloat
                }
                return when(matchType){
                    MatchType.LESS -> judgeTargetFloat < baseFloat
                    MatchType.LESS_EQUAL -> judgeTargetFloat <= baseFloat
                    MatchType.GREATER -> judgeTargetFloat > baseFloat
                    MatchType.GREATER_EQUAL -> judgeTargetFloat >= baseFloat
                    else -> false
                } to null

            }

            private fun strToFloat(
                argsPairList: List<Pair<String, String>>,
                targetFloatStr: String,
                targetArgKey: String,
                argIndex: Int,
                ifKeyName: String,
            ): Pair<Float, IfCheckErr?> {
                return try {
                    targetFloatStr.toFloat() to null
                } catch (e: Exception) {
                    val spanNumMatchTypeKeysCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.ligthBlue,
                        numMachTypeList.map {
                            it.str
                        }.joinToString(",")
                    )
                    val spanTargetKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        targetArgKey
                    )
                    val spanTargetStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            targetFloatStr.toString()
                        )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        targetArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return -1f to IfCheckErr(
                        "In ${spanNumMatchTypeKeysCon}, ${spanTargetKey} key must be number: ${spanTargetStr}, ${errWhere}"
                    )
                }
            }
            fun isEqual(
                judgeTargetStr: String,
                regexStr: String,
                argsPairList: List<Pair<String, String>>,
                ifKeyName: String,
                argIndex: Int,
            ): Pair<Boolean?, IfCheckErr?> {
                val baseArgKey = IfArgs.BASE.str
                val regex = try {
                    regexStr.toRegex()
                } catch (e: Exception) {
                    val spanBaseKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        baseArgKey
                    )
                    val spanJudgeRegexStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            regexStr
                        )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        baseArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanBaseKey} key failure to compile as regex: ${spanJudgeRegexStr}, ${errWhere}")
                }
                return try {
                    regex.matches(judgeTargetStr) to null
                } catch (e: Exception) {
                    val spanJudgeTargetArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        judgeTargetArgName
                    )
                    val spanJudgeRegexStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            regexStr
                        )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        baseArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    null to IfCheckErr("Irregular ${spanJudgeTargetArgName} :${spanJudgeRegexStr}, ${errWhere}")
                }
            }

            fun isIn(
                judgeTargetStr: String,
                regexStr: String,
                argsPairList: List<Pair<String, String>>,
                ifKeyName: String,
                argIndex: Int,
            ): Pair<Boolean?, IfCheckErr?> {
                val baseArgKey = IfArgs.BASE.str
                val regex = try {
                    regexStr.toRegex()
                } catch (e: Exception) {
                    val spanBaseKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        baseArgKey
                    )
                    val spanJudgeRegexStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            regexStr
                        )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        baseArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanBaseKey} key failure to compile as regex: ${spanJudgeRegexStr}, ${errWhere}")
                }
                return try {
                    regex.containsMatchIn(judgeTargetStr) to null
                } catch (e: Exception) {
                    val spanJudgeTargetArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        judgeTargetArgName
                    )
                    val spanJudgeRegexStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            regexStr
                        )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        baseArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    null to IfCheckErr("Irregular ${spanJudgeTargetArgName} :${spanJudgeRegexStr}, ${errWhere}")
                }
//                return judgeTargetStr.contains(baseStr) to null

            }
        }

        private fun makeArgNameToSubKeyMapList(
            argsPairList: List<Pair<String, String>>,
        ): List<
                Pair<
                        String,
                        Map<String, String>
                        >
                > {
            val baseArgName = IfArgs.BASE.str
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
                    IfArgs.BASE ->
                        Pair(String(), emptyMap())
                    IfArgs.MATCH_TYPE -> {
                        val funcPartMap = mapOf(
                            argName to valueStr,
                        )
                        val argsMap = let {
                            val nextArgNameToValueStr =
                                argsPairList.getOrNull(index + 1)
                                    ?: return@let emptyMap()
                            val nextArgName = nextArgNameToValueStr.first
                            when (nextArgName == baseArgName) {
                                false -> emptyMap()
                                else -> mapOf(
                                    baseArgName to nextArgNameToValueStr.second
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