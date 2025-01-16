package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object SettingIfManager {

    private const val judgeTargetArgName = "judgeTarget"

    enum class IfArgs(
        val str: String
    ){
        MATCH_TYPE("matchType"),
        VALUE("value"),
        REGEX("regex"),
        CONCAT_CONDITION("concatCondition"),
    }

    enum class ConcatCondition(
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
            return makeJudgeTargetNotExistErr(
                ifKeyName,
                argsPairList,
            )
        }
        return IfArgMatcher.match(
            ifKeyName,
            judgeTargetStr,
            argsPairList
        )
    }

    fun makeJudgeTargetNotExistErr(
        ifKeyName: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<Boolean?, IfCheckErr?> {
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

    object IfArgMatcher {


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
//                    "argNameToSubKeyMapPairList: ${argNameToSubKeyMapPairList}",
//                    "argErr: ${argErr}",
//                ).joinToString("\n\n") + "\n\n==========\n\n"
//            )
            if(matchList.size == 1){
                return matchList.first() to null
            }
            val concatConditionKey =
                IfArgs.CONCAT_CONDITION.str
            val concatConditionStr = argNameToSubKeyMapPairList.toMap().get(
                concatConditionKey
            )
            val concatCondition = ConcatCondition.entries.firstOrNull {
                it.str == concatConditionStr
            } ?: let {
                makeConcatConditionKeyNotExistErr(
                    ifKeyName,
                    concatConditionStr,
                    argNameToSubKeyMapPairList
                )
                val spanConcatConditionKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    concatConditionKey,
                )
                val spanConcatConditionStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    concatConditionStr.toString()
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
            val matchResult = culcMatchResult(
                concatCondition,
                matchList,
            )
            return matchResult to null
        }

        fun culcMatchResult(
            concatCondition: ConcatCondition,
            matchList: List<Boolean>,
        ):Boolean {
            return when(concatCondition){
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
        }

        fun makeConcatConditionKeyNotExistErr(
            ifKeyName: String,
            concatConditionStr: String?,
            argNameToSubKeyMapPairList: List<Pair<String, String>>
        ): Pair<Boolean?, IfCheckErr?> {
            val concatConditionKey = IfArgs.CONCAT_CONDITION.str
            val spanConcatConditionKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                concatConditionKey,
            )
            val spanConcatConditionStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                concatConditionStr.toString()
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
        private fun makeMatchListToErr(
            ifKeyName: String,
            judgeTargetStr: String,
            argsPairList: List<Pair<String, String>>,
        ): Pair<List<Boolean>?, IfCheckErr?> {
            val argNameToSubKeyMapList = makeArgNameToSubKeyMapList(
                argsPairList,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "ljudge_makeMatchListToErr.txt").absolutePath,
//                listOf(
//                    "judgeTargetStr: ${judgeTargetStr}",
//                    "argsPairList: ${argsPairList}",
//                    "argNameToSubKeyMapList: ${argNameToSubKeyMapList}",
//                ).joinToString("\n\n") + "\n\n==========\n\n"
//            )
            val matchTypeArgKey = IfArgs.MATCH_TYPE.str
            argNameToSubKeyMapList.toMap().get(matchTypeArgKey) ?: let {
                val spanMatchTypeKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    matchTypeArgKey
                )
                val errWhere = makeLogErrWhere(
                    ifKeyName,
                    matchTypeArgKey,
                    -1,
                    makeArgsPairListCon(argsPairList)
                )
                return null to IfCheckErr("${spanMatchTypeKey} key not exist: ${errWhere}")
            }
            val matchTypeKeyClass = IfArgs.MATCH_TYPE
            val matchTypeStrList = MatchType.entries
            val valueKey = IfArgs.VALUE.str
            val regexKey = IfArgs.REGEX.str
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
                val regexKeyCon = subKeyMap.get(regexKey)
                val valueKeyCon = subKeyMap.get(valueKey)
                if(
                    regexKeyCon == null
                    && valueKeyCon == null
                ) {
                    val spanValueKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        valueKey
                    )
                    val spanRegexKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        regexKey
                    )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        "${valueKey} or ${regexKey}",
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanValueKey} or ${spanRegexKey} key con not exist: ${errWhere}")
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
                val isMatchToErr = when(true) {
                    (valueKeyCon != null) ->
                        Matcher.byValue(
                            judgeTargetStr,
                            valueKeyCon,
                            matchType,
                            ifKeyName,
                            argsPairList,
                            argIndex,
                        )
                    (regexKeyCon != null) ->
                        Matcher.byRegex(
                            judgeTargetStr,
                            regexKeyCon,
                            matchType,
                            ifKeyName,
                            argsPairList,
                            argIndex,
                        )
                    else -> null to null
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

        private fun numCompareErrInRegexKey(
            regexKey: String,
            matchType: MatchType,
            matchTypeStr: String?,
            argsPairList: List<Pair<String, String>>,
            ifKeyName: String,
            argIndex: Int,
        ):  IfCheckErr {
            val spanRegexKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    regexKey
                )
            val spanMatchTypeKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.ligthBlue,
                    matchType.str
                )
            val spanMatchTypeValueStr =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    matchTypeStr.toString()
                )
            val numTypeKeysCon = numMachTypeList.map {
                "'${it.str}'"
            }.joinToString(", ")
            return IfCheckErr(
                "In ${spanRegexKey} key, ${spanMatchTypeKey} (${numTypeKeysCon}) must not use: ${spanMatchTypeValueStr}, ${
                    makeLogErrWhere(
                        ifKeyName,
                        matchType.str,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                }"
            )
        }

        private object Matcher {

            fun byValue(
                judgeTargetStr: String,
                valueCon: String,
                matchType: MatchType,
                ifKeyName: String,
                argsPairList: List<Pair<String, String>>,
                argIndex: Int,
            ): Pair<Boolean?, IfCheckErr?> {
                return when(matchType){
                    MatchType.EQUAL ->{
                        (judgeTargetStr == valueCon) to null
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
                    MatchType.NOT_EQUAL ->
                        (judgeTargetStr != valueCon) to null
                    MatchType.IN ->
                        judgeTargetStr.contains(valueCon) to null
                    MatchType.NOT_IN ->
                        !judgeTargetStr.contains(valueCon) to null
                    MatchType.LESS ->
                        isNumCompare(
                            judgeTargetStr,
                            valueCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                    MatchType.LESS_EQUAL ->
                        isNumCompare(
                            judgeTargetStr,
                            valueCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                    MatchType.GREATER ->
                        isNumCompare(
                            judgeTargetStr,
                            valueCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                    MatchType.GREATER_EQUAL ->
                        isNumCompare(
                            judgeTargetStr,
                            valueCon,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                            matchType,
                        )
                }
            }

            fun byRegex(
                judgeTargetStr: String,
                regexStr: String,
                matchType: MatchType,
                ifKeyName: String,
                argsPairList: List<Pair<String, String>>,
                argIndex: Int,
            ): Pair<Boolean?, IfCheckErr?> {
                return when(matchType){
                    MatchType.EQUAL ->{
                        isEqualByRegex(
                            judgeTargetStr,
                            regexStr,
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
                        isEqualByRegex(
                            judgeTargetStr,
                            regexStr,
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
                        isInByRegex(
                            judgeTargetStr,
                            regexStr,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                        )
                    MatchType.NOT_IN ->
                        isInByRegex(
                            judgeTargetStr,
                            regexStr,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                        ).let {
                                (isMatch, err) ->
                            isMatch?.let {
                                !it
                            } to err
                        }
                    MatchType.LESS,
                    MatchType.LESS_EQUAL,
                    MatchType.GREATER,
                    MatchType.GREATER_EQUAL ->
                        return null to numCompareErrInRegexKey(
                            IfArgs.REGEX.str,
                            matchType,
                            matchType.str,
                            argsPairList,
                            ifKeyName,
                            argIndex,
                        )
                }
            }

            private fun isNumCompare(
                judgeTargetStr: String,
                valueFloatStr: String,
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
                val valueArgKey = IfArgs.VALUE.str
                val valueFloat = strToFloat(
                    argsPairList,
                    valueFloatStr,
                    valueArgKey,
                    argIndex,
                    ifKeyName,
                ).let {
                        (valueFloat, err) ->
                    if(err != null){
                        return null to err
                    }
                    valueFloat
                }
                return when(matchType){
                    MatchType.LESS -> judgeTargetFloat < valueFloat
                    MatchType.LESS_EQUAL -> judgeTargetFloat <= valueFloat
                    MatchType.GREATER -> judgeTargetFloat > valueFloat
                    MatchType.GREATER_EQUAL -> judgeTargetFloat >= valueFloat
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
            private fun isEqualByRegex(
                judgeTargetStr: String,
                regexStr: String,
                argsPairList: List<Pair<String, String>>,
                ifKeyName: String,
                argIndex: Int,
            ): Pair<Boolean?, IfCheckErr?> {
                val valueArgKey = IfArgs.VALUE.str
                val regex = try {
                    regexStr.toRegex()
                } catch (e: Exception) {
                    val spanValueKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        valueArgKey
                    )
                    val spanJudgeRegexStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            regexStr
                        )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        valueArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanValueKey} key failure to compile as regex: ${spanJudgeRegexStr}, ${errWhere}")
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
                        valueArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    null to IfCheckErr("Irregular ${spanJudgeTargetArgName} :${spanJudgeRegexStr}, ${errWhere}")
                }
            }

            private fun isInByRegex(
                judgeTargetStr: String,
                regexStr: String,
                argsPairList: List<Pair<String, String>>,
                ifKeyName: String,
                argIndex: Int,
            ): Pair<Boolean?, IfCheckErr?> {
                val VALUEArgKey = IfArgs.VALUE.str
                val regex = try {
                    regexStr.toRegex()
                } catch (e: Exception) {
                    val spanValueKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        VALUEArgKey
                    )
                    val spanJudgeRegexStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            regexStr
                        )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        VALUEArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanValueKey} key failure to compile as regex: ${spanJudgeRegexStr}, ${errWhere}")
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
                        VALUEArgKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    null to IfCheckErr("Irregular ${spanJudgeTargetArgName} :${spanJudgeRegexStr}, ${errWhere}")
                }
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
            val conpareBaseKeyList = listOf(
                IfArgs.VALUE.str,
                IfArgs.REGEX.str
            )
            return argsPairList.mapIndexed {
                    index, argNameToValueStr ->
                val argName = argNameToValueStr.first.trim()
                val argClass = IfArgs.entries.firstOrNull {
                    arg ->
                    arg.str == argName
                } ?: return@mapIndexed Pair(String(), emptyMap())
                val valueStr = argNameToValueStr.second.trim()
                when(argClass) {
                    IfArgs.CONCAT_CONDITION -> {
                        val mainSubKeyMap = mapOf(
                            argName to valueStr,
                        )
                        Pair(argName, mainSubKeyMap)
                    }
                    IfArgs.VALUE,
                    IfArgs.REGEX->
                        Pair(String(), emptyMap())
                    IfArgs.MATCH_TYPE -> {
                        val funcPartMap = mapOf(
                            argName to valueStr,
                        )
                        val argsMap = let {
                            val nextArgNameToValueStr =
                                argsPairList.getOrNull(index + 1)
                                    ?: return@let emptyMap()
                            val nextArgName = nextArgNameToValueStr.first.trim()
                            when (conpareBaseKeyList.contains(nextArgName)) {
                                false -> emptyMap()
                                else -> mapOf(
                                    nextArgName to nextArgNameToValueStr.second.trim()
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