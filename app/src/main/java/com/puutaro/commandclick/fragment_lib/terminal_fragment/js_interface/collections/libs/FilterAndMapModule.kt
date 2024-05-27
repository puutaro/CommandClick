package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections.libs

object FilterAndMapModule {

    const val extraMapSeparator = '|'

    val removeAndReplacePair = Pair("removeRegex", "replaceStr")

    enum class ExtraMapBaseKey(
        val key: String
    ) {
        REMOVE_REGEX(removeAndReplacePair.first),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        MATCH_REGEX("matchRegex"),
        MATCH_REGEX_MATCH_TYPE("matchRegexMatchType"),
        MATCH_CONDITION("matchCondition"),
        LINES_MATCH_TYPE("linesMatchType"),
    }

    enum class MatchConditionType(
        val type: String,
    ) {
        AND("and"),
        OR("or"),
    }

    enum class LinesMatchType(
        val type: String,
    ) {
        NORMAL("normal"),
        DENY("deny"),
    }

    enum class MatchRegexMatchType(
        val type: String,
    ) {
        NORMAL("normal"),
        DENY("deny"),
    }

    fun applyRemoveRegex(
        srcLine: String,
        removeRegexToReplaceKeyList: List<Pair<Regex, String>>,
        extraMap: Map<String, String>
    ): String {
        var line = srcLine
        removeRegexToReplaceKeyList.forEach {
            val removeRegex = it.first
            val replaceKey = it.second
            val replaceStr = extraMap.get(replaceKey)
                ?: String()
            line = line.replace(removeRegex, replaceStr)
        }
        return line
    }


    fun applyCompPrefix(
        srcLine: String,
        compPrefixList: List<String>,
    ): String {
        var line = srcLine
        compPrefixList.forEach {
            line = when (line.startsWith(it)) {
                true -> line
                else -> "$it${line.replaceFirstChar { it.uppercase() }}"
            }
        }
        return line
    }

    fun applyCompSuffix(
        srcLine: String,
        compSuffixList: List<String>,
    ): String {
        var line = srcLine
        compSuffixList.forEach {
            line = when (line.endsWith(it)) {
                true -> line
                else -> "$it$line"
            }
        }
        return line
    }

    fun makeTargetValueList(
        extraMap: Map<String, String>,
        removeRegexBaseKey: String,
    ): List<String> {
        return extraMap.filter {
                keyToValue ->
            val key = keyToValue.key
            val okPrefix = key.startsWith(removeRegexBaseKey)
            val okNumSuffix = try {
                key.removePrefix(removeRegexBaseKey).toInt()
                true
            } catch(e: Exception){
                false
            }
            okPrefix && okNumSuffix
        }.map {
                keyToValue ->
            keyToValue.value
        }
    }

    fun makeRemoveRegexToReplaceKeyPairList(
        extraMap: Map<String, String>,
        removeRegexBaseKey: String,
    ): List<Pair<Regex, String>> {
        return extraMap.filter {
                keyToValue ->
            val key = keyToValue.key
            val okPrefix = key.startsWith(removeRegexBaseKey)
            val okNumSuffix = try {
                key.removePrefix(removeRegexBaseKey).toInt()
                true
            } catch(e: Exception){
                false
            }
            okPrefix && okNumSuffix
        }.map {
                keyToValue ->
            val replaceKey =
                keyToValue.key.replace(
                    Regex("^${removeAndReplacePair.first}"),
                    removeAndReplacePair.second
                )
            val removeRegexStr = keyToValue.value
            Regex(removeRegexStr) to replaceKey
        }
    }


}