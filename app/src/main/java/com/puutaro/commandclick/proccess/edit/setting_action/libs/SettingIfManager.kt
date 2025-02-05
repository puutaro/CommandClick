package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File
import java.time.LocalDateTime


object SettingIfManager {

    private const val ifAnnotation = "ifAnnotation"

    data class IfStack(
        val ifProcName: String,
        val bool: Boolean,
    )

    enum class IfArgs(
        val str: String
    ){
        TARGET("target"),
        MATCHER("matcher"),
        VALUE("value"),
        REGEX("regex"),
        GLUE("glue"),
    }

    enum class GlueType(
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
        argsPairList: List<Pair<String, String>>,
        varNameToValueStrMap: Map<String, String?>?,
    ): Pair<Boolean?, IfCheckErr?> {
//        val judgeTargetStr = judgeTargetStrSrc.ifEmpty {
//            return makeJudgeTargetNotExistErr(
//                ifKeyName,
//                argsPairList,
//            )
//        }
        val dateList = mutableListOf<Pair<String, LocalDateTime>>()
        dateList.add("ifStart" to LocalDateTime.now())
        return IfArgMatcher.match(
            ifKeyName,
            argsPairList,
            varNameToValueStrMap,
        ).let {
            dateList.add("ifEnd" to LocalDateTime.now())
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lifInTime.txt").absolutePath,
//                dateList.joinToString("\n") + "\n\n===\n\n"
//            )
            it
        }
    }

    fun makeJudgeTargetNotExistErr(
        ifKeyName: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<Boolean?, IfCheckErr?> {
        val errWhere = makeLogErrWhere(
            ifKeyName,
            ifAnnotation,
            -1,
            makeArgsPairListCon(argsPairList),
        )
        return null to IfCheckErr(
            "${ifAnnotation} not exist: ${errWhere}"
        )
    }

    object IfArgMatcher {


        fun match(
            ifKeyName: String,
            argNameToSubKeyMapPairList: List<Pair<String, String>>,
            valueNameToValueStrMap: Map<String, String?>?,
        ): Pair<Boolean?, IfCheckErr?>{
            val matchListToErr = makeMatchListToErr(
                ifKeyName,
                argNameToSubKeyMapPairList,
                valueNameToValueStrMap,
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
//                    "matchList: ${matchList}",
//                    "argNameToSubKeyMapPairList: ${argNameToSubKeyMapPairList}",
//                    "argErr: ${argErr}",
//                ).joinToString("\n\n") + "\n\n==========\n\n"
//            )
            if(matchList.size == 1){
                return matchList.first() to null
            }
            val glueKey =
                IfArgs.GLUE.str
            val glueStr = argNameToSubKeyMapPairList.toMap().get(
                glueKey
            )
            val glue = GlueType.entries.firstOrNull {
                it.str == glueStr
            } ?: let {
                makeGlueKeyNotExistErr(
                    ifKeyName,
                    glueStr,
                    argNameToSubKeyMapPairList
                )
                val spanGlueKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    glueKey,
                )
                val spanGlueStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    glueStr.toString()
                )
                return null to IfCheckErr(
                    "${spanGlueKeyName} must be ${
                        GlueType.entries.map { "'${it.str}'" }.joinToString(" or ")
                    }: ${spanGlueStr} ${makeLogErrWhere(
                        ifKeyName, 
                        glueKey,
                        -1,
                        makeArgsPairListCon(argNameToSubKeyMapPairList)
                    )}")
            }
            val matchResult = culcMatchResult(
                glue,
                matchList,
            )
            return matchResult to null
        }

        fun culcMatchResult(
            glueType: GlueType,
            matchList: List<Boolean>,
        ):Boolean {
            return when(glueType){
                GlueType.AND -> {
                    matchList.all {
                        it
                    }
                }
                GlueType.OR -> {
                    matchList.any {
                        it
                    }
                }
            }
        }

        fun makeGlueKeyNotExistErr(
            ifKeyName: String,
            glueStr: String?,
            argNameToSubKeyMapPairList: List<Pair<String, String>>
        ): Pair<Boolean?, IfCheckErr?> {
            val glueKey = IfArgs.GLUE.str
            val spanGlueKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                glueKey,
            )
            val spanGlueStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                glueStr.toString()
            )
            return null to IfCheckErr(
                "${spanGlueKeyName} must be ${
                    GlueType.entries.map { "'${it.str}'" }.joinToString(" or ")
                }: ${spanGlueStr} ${makeLogErrWhere(
                    ifKeyName,
                    glueKey,
                    -1,
                    makeArgsPairListCon(argNameToSubKeyMapPairList)
                )}")
        }
        private fun makeMatchListToErr(
            ifKeyName: String,
            argsPairList: List<Pair<String, String>>,
            valueNameToValueStrMap: Map<String, String?>?,
        ): Pair<List<Boolean>?, IfCheckErr?> {
            val targetKey = IfArgs.TARGET.str
            val matcherKey = IfArgs.MATCHER.str
            val ifKeyList = IfArgs.entries.filter {
                it != IfArgs.GLUE
            }.map {
                    ifKeyClass ->
                ifKeyClass.str
            }
            val ifKeyMapList = makeIfKeyMapList(
                argsPairList,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "ljudge_makeMatchListToErr.txt").absolutePath,
//                listOf(
//                    "argsPairList: ${argsPairList}",
//                    "ifKeyMapList: ${ifKeyMapList}",
//                ).joinToString("\n\n") + "\n\n==========\n\n"
//            )
            let {
                val requireIfKeyList = listOf(
                    listOf(targetKey),
                    listOf(matcherKey),
                    listOf(
                        IfArgs.VALUE.str,
                        IfArgs.REGEX.str
                    ),
                )
                val judgeIfKeySetNum = 3
                argsPairList.filter { (ifKeyEntry, _) ->
                    ifKeyList.contains(ifKeyEntry)
                }.forEachIndexed { index, (ifKeyEntry, _) ->
                    val curIfKeyIndex = index % judgeIfKeySetNum
                    val curIfKeyList = requireIfKeyList.get(curIfKeyIndex)
                    if (
                        curIfKeyList.contains(ifKeyEntry)
                    ) return@forEachIndexed
                    return null to makeKeyNotExistErr(
                        ifKeyName,
                        curIfKeyList.joinToString(" or "),
                        index,
                        argsPairList,
                    )
                }
            }
            val matchTypeStrList = MatchType.entries
            val valueKey = IfArgs.VALUE.str
            val regexKey = IfArgs.REGEX.str
            val matchList = ifKeyMapList.mapIndexed { argIndex, ifKeyMap ->
                val targetStr = ifKeyMap[targetKey]?.let { targetStrSrc ->
                    CmdClickMap.replaceByBackslashToNormal(
                        targetStrSrc,
                        valueNameToValueStrMap,
                    )
                } ?: let {
                    val spanTargetKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        targetKey
                    )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        targetKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanTargetKey} key con not exist: ${errWhere}")
                }
                val matcherStr = ifKeyMap[matcherKey] ?: let {
                    val spanMatcherKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        matcherKey
                    )
                    val errWhere = makeLogErrWhere(
                        ifKeyName,
                        matcherKey,
                        argIndex,
                        makeArgsPairListCon(argsPairList)
                    )
                    return null to IfCheckErr("${spanMatcherKey} key con not exist: ${errWhere}")
                }
                val regexKeyCon = ifKeyMap[regexKey]?.let { regexConSrc ->
                    CmdClickMap.replaceByBackslashToNormal(
                        regexConSrc,
                        valueNameToValueStrMap,
                    )
                }
                val valueKeyCon = ifKeyMap[valueKey]?.let { valueConSrc ->
                    CmdClickMap.replaceByBackslashToNormal(
                        valueConSrc,
                        valueNameToValueStrMap,
                    )
                }
                if (regexKeyCon == null && valueKeyCon == null) {
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
                val matcherMatchType = matchTypeStrList.firstOrNull {
                    it.str == matcherStr
                } ?: let {
                    val spanMatchTypeKey =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.lightBlue,
                            matcherKey
                        )
                    val spanMatchTypeValueStr =
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            matcherStr
                        )
                    return null to IfCheckErr(
                        "${spanMatchTypeKey} must be ${
                            matchTypeStrList.map {
                                "'${it.str}'"
                            }.joinToString(" or ")
                        }: ${spanMatchTypeValueStr}, ${
                            makeLogErrWhere(
                                ifKeyName,
                                matcherKey,
                                argIndex,
                                makeArgsPairListCon(argsPairList)
                            )
                        }"
                    )
                }
                val isMatchToErr = when {
                    (valueKeyCon != null) ->
                        Matcher.byValue(
                            targetStr,
                            valueKeyCon,
                            matcherMatchType,
                            ifKeyName,
                            argsPairList,
                            argIndex,
                        )
                    (regexKeyCon != null) ->
                        Matcher.byRegex(
                            targetStr,
                            regexKeyCon,
                            matcherMatchType,
                            ifKeyName,
                            argsPairList,
                            argIndex,
                        )
                    else -> null to null
                }.let { (isMatch, err) ->
                    if (err != null) return null to err
                    isMatch
                }
                isMatchToErr
            }.filterNotNull()
            return matchList to null
        }

        private fun makeKeyNotExistErr(
            sIfOrIIfName: String,
            ifKey: String,
            index: Int,
            argsPairList: List<Pair<String, String>>,
        ): IfCheckErr {
            val spanMatcherKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                ifKey
            )
            val errWhere = makeLogErrWhere(
                sIfOrIIfName,
                ifKey,
                index,
                makeArgsPairListCon(argsPairList)
            )
            return IfCheckErr("${spanMatcherKey} key con not exist: ${errWhere}")
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
                    CheckTool.lightBlue,
                    regexKey
                )
            val spanMatchTypeKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
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
                    ifAnnotation,
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
                        CheckTool.lightBlue,
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
                        ifAnnotation
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
                        ifAnnotation
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

        private fun makeIfKeyMapList(
            argsPairList: List<Pair<String, String>>,
        ): List<
                Map<String, String>
                > {
            val ifKeyList = IfArgs.entries.filter {
                it != IfArgs.GLUE
            }.map {
                ifKeyClass ->
                ifKeyClass.str
            }
            val targetKey = IfArgs.TARGET.str
            val matcherKey = IfArgs.MATCHER.str
            val compareBaseKeyList = listOf(
                IfArgs.VALUE.str,
                IfArgs.REGEX.str
            )
            return argsPairList.mapIndexed {
                    index, argNameToValueStr ->
                val ifArgNameEntry = argNameToValueStr.first.trim()
                if(
                    !ifKeyList.contains(ifArgNameEntry)
                ) return@mapIndexed emptyMap()
                val argClass = IfArgs.entries.firstOrNull {
                    arg ->
                    arg.str == ifArgNameEntry
                } ?: return@mapIndexed emptyMap()
                val valueStr = argNameToValueStr.second.trim()
                when(argClass) {
                    IfArgs.GLUE,
                    IfArgs.VALUE,
                    IfArgs.REGEX,
                    IfArgs.MATCHER -> emptyMap()
                    IfArgs.TARGET -> {
                        val targetArgMap = mapOf(
                            targetKey to valueStr,
                        )
                        val matcherArgIndex = index + 1
                        val matcherArgsMap = let {
                            val matcherArgNameToValueStrEntry =
                                argsPairList.getOrNull(matcherArgIndex)
                                    ?: return@let emptyMap()
                            val matcherArgNameEntry = matcherArgNameToValueStrEntry.first.trim()
                            when (matcherArgNameEntry == matcherKey) {
                                false -> emptyMap()
                                else -> mapOf(
                                    matcherArgNameEntry to matcherArgNameToValueStrEntry.second.trim()
                                )
                            }
                        }
                        val compareKeyIndex = matcherArgIndex + 1
                        val compareArgsMap = let {
                            val compareArgNameToValueStrEntry =
                                argsPairList.getOrNull(compareKeyIndex)
                                    ?: return@let emptyMap()
                            val compareArgNameEntry = compareArgNameToValueStrEntry.first.trim()
                            when (compareBaseKeyList.contains(compareArgNameEntry)) {
                                false -> emptyMap()
                                else -> mapOf(
                                    compareArgNameEntry to compareArgNameToValueStrEntry.second.trim()
                                )
                            }
                        }
//                        val argsMap = let {
//                            val nextArgNameToValueStr =
//                                argsPairList.getOrNull(index + 1)
//                                    ?: return@let emptyMap()
//                            val nextArgName = nextArgNameToValueStr.first.trim()
//                            when (conpareBaseKeyList.contains(nextArgName)) {
//                                false -> emptyMap()
//                                else -> mapOf(
//                                    nextArgName to nextArgNameToValueStr.second.trim()
//                                )
//                            }
//                        }
                        targetArgMap + matcherArgsMap + compareArgsMap
//                        Pair(argName, (targetArgMap + matcherArgsMap + compareArgsMap))
                    }
                }
            }.filter {
                it.isNotEmpty()
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
            CheckTool.lightBlue,
            ifKeyName,
        )
        val spanSubKeyCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errBrown,
            subKeyCon,
        )
        val errWhere =
            "arg name: ${spanArgName}, index: ${spanArgIndex}, setting key: ${spanSIfKey}, subKeyCon: ${spanSubKeyCon}"
        return CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.lightBlue,
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