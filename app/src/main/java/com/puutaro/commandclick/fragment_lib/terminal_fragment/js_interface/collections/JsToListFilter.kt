package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

class JsToListFilter(terminalFragment: TerminalFragment) {

    private val extraMapSeparator = '|'

    private enum class ExtraMapBaseKey(
        val key: String
    ) {
        REMOVE_REGEX("removeRegex"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        MATCH_REGEX("matchRegex"),
        MATCH_CONDITION("matchCondition"),
        LINES_MATCH_TYPE("linesMatchType"),
    }

    private enum class MatchConditionType(
        val type: String,
    ){
        AND("and"),
        OR("or"),
    }

    private enum class LinesMatchType(
        val type: String,
    ){
        NORMAL("normal"),
        DENY("deny"),
    }

    @JavascriptInterface
    fun filter(
        lines: String,
        separator: String,
        matchLines: String,
        extraMapCon: String,
    ): String {
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            extraMapSeparator,
        ).toMap().toSortedMap()
        val removeRegexList = makeTargetValueList(
            extraMap,
            ExtraMapBaseKey.REMOVE_REGEX.key
        ).map {
            Regex(it)
        }
        val compPrefixList = makeTargetValueList(
            extraMap,
            ExtraMapBaseKey.COMP_PREFIX.key
        )
        val compSuffixList = makeTargetValueList(
            extraMap,
            ExtraMapBaseKey.COMP_SUFFIX.key
        )
        val matchRegexList = makeTargetValueList(
            extraMap,
            ExtraMapBaseKey.MATCH_REGEX.key
        ).map {
            Regex(it)
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsToListFilter.txt").absolutePath,
//            listOf(
//                "removRegexList: ${makeTargetValueList(
//                    extraMap,
//                    ExtraMapBaseKey.REMOVE_REGEX.key
//                )}",
//                "compPrefixList: ${makeTargetValueList(
//                    extraMap,
//                    ExtraMapBaseKey.COMP_PREFIX.key
//                )}"
//            ).joinToString("\n\n\n")
//        )
        val matchConditionStr = extraMap.toMap().get(
            ExtraMapBaseKey.MATCH_CONDITION.key
        )
        val linesMatchTypeStr = extraMap.toMap().get(
            ExtraMapBaseKey.LINES_MATCH_TYPE.key
        )
        val matchLineList = matchLines.split(separator)

        val filterLineList = lines.split(separator).filter {
            srcLine ->
            val lineWithRemove = applyRemoveRegex(
                srcLine,
                removeRegexList,
            )
            val lineWithCompPrefix = applyCompPrefix(
                lineWithRemove,
                compPrefixList
            )
            val lineWithCompSuffix = applyCompSuffix(
                lineWithCompPrefix,
                compSuffixList
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsToLitFil.txt").absolutePath,
//                listOf(
//                    "lineWithRemove: ${lineWithRemove}",
//                    "lineWithCompPrefix: ${lineWithCompPrefix}",
//                    "lineWithCompSuffix: ${lineWithCompSuffix}",
//                    "match: ${matcher(
//                        lineWithCompSuffix,
//                        matchRegexList,
//                        matchConditionStr,
//                        matchLineList,
//                        linesMatchTypeStr,
//                    )}",
//                ).joinToString("\n\n") + "\n\n----\n\n"
//            )
            matcher(
                lineWithCompSuffix,
                matchRegexList,
                matchConditionStr,
                matchLineList,
                linesMatchTypeStr,
            )
        }
        return filterLineList.joinToString(separator)
    }


    private fun applyRemoveRegex(
        srcLine: String,
        removeRegexList: List<Regex>,
    ): String {
        var line = srcLine
        removeRegexList.forEach {
            line = line.replace(it, String())
        }
        return line
    }


    private fun applyCompPrefix(
        srcLine: String,
        compPrefixList: List<String>,
    ): String {
        var line = srcLine
        compPrefixList.forEach {
            line = when(line.startsWith(it)){
                true -> line
                else -> "$it$line"
            }
        }
        return line
    }

    private fun applyCompSuffix(
        srcLine: String,
        compSuffixList: List<String>,
    ): String {
        var line = srcLine
        compSuffixList.forEach {
            line = when(line.endsWith(it)){
                true -> line
                else -> "$line$it"
            }
        }
        return line
    }

    private fun matcher(
        srcLine: String,
        matchRegexList: List<Regex>,
        matchConditionStr: String?,
        matchLineList: List<String>,
        linesMatchTypeStr: String?,
    ): Boolean {
        val matchConditionType = MatchConditionType.values().firstOrNull {
            it.type == matchConditionStr
        } ?: MatchConditionType.AND


        val linesMatchType = LinesMatchType.values().firstOrNull {
            it.type == linesMatchTypeStr
        } ?: LinesMatchType.NORMAL
        val matchInLines = when(linesMatchType){
            LinesMatchType.NORMAL -> matchLineList.contains(srcLine)
            LinesMatchType.DENY -> !matchLineList.contains(srcLine)
        }
        return when(matchConditionType){
            MatchConditionType.AND -> {
                matchRegexList.all {
                    it.containsMatchIn(srcLine)
                            && matchInLines
                }
            }
            MatchConditionType.OR -> {
                matchRegexList.any {
                    it.containsMatchIn(srcLine)
                            && matchInLines
                }
            }
        }
    }

    private fun makeTargetValueList(
        extraMap: Map<String, String>,
        removeRegexBaseKey: String,
    ): List<String> {
        return extraMap.filter {
                keyToValue ->
            val key = keyToValue.key
            val okPrefix = key.startsWith(removeRegexBaseKey)
            val okNumSuffix = try {
                key.removePrefix(removeRegexBaseKey)
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

}