package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections.libs.FilterAndMapModule
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.RegexTool
import java.io.File

class JsToListFilter(terminalFragment: TerminalFragment) {

    @JavascriptInterface
    fun filter(
        lines: String,
        separator: String,
        matchLines: String,
        extraMapCon: String,
    ): String {
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            FilterAndMapModule.extraMapSeparator,
        ).toMap().toSortedMap()
        val removeRegexToReplaceKeyList = FilterAndMapModule.makeRemoveRegexToReplaceKeyPairList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.REMOVE_REGEX.key
        )
        val compPrefixList = FilterAndMapModule.makeTargetValueList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.COMP_PREFIX.key
        )
        val compSuffixList = FilterAndMapModule.makeTargetValueList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.COMP_SUFFIX.key
        )
        val matchRegexList = FilterAndMapModule.makeTargetValueList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.MATCH_REGEX.key
        ).map {
            RegexTool.convert(it)
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
            FilterAndMapModule.ExtraMapBaseKey.MATCH_CONDITION.key
        )
        val linesMatchTypeStr = extraMap.toMap().get(
            FilterAndMapModule.ExtraMapBaseKey.LINES_MATCH_TYPE.key
        )
        val matchRegexMatchTypeStr = extraMap.toMap().get(
            FilterAndMapModule.ExtraMapBaseKey.MATCH_REGEX_MATCH_TYPE.key
        )
        val matchLineList = matchLines.split(separator)

        val filterLineList = lines.split(separator).filter {
            srcLine ->
            val lineWithRemove = FilterAndMapModule.applyRemoveRegex(
                srcLine,
                removeRegexToReplaceKeyList,
                extraMap,
            )
            val lineWithCompPrefix = FilterAndMapModule.applyCompPrefix(
                lineWithRemove,
                compPrefixList
            )
            val lineWithCompSuffix = FilterAndMapModule.applyCompSuffix(
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
                matchRegexMatchTypeStr,
            )
        }
        return filterLineList.joinToString(separator)
    }

    private fun matcher(
        srcLine: String,
        matchRegexList: List<Regex>,
        matchConditionStr: String?,
        matchLineList: List<String>,
        linesMatchTypeStr: String?,
        matchRegexMatchTypeStr: String?
    ): Boolean {
        val linesMatchType = FilterAndMapModule.LinesMatchType.values().firstOrNull {
            it.type == linesMatchTypeStr
        } ?: FilterAndMapModule.LinesMatchType.NORMAL
        val matchInLines = judgeMatchLines(
            srcLine,
            matchLineList,
            linesMatchType,
        )

        val matchConditionType = FilterAndMapModule.MatchConditionType.values().firstOrNull {
            it.type == matchConditionStr
        } ?: FilterAndMapModule.MatchConditionType.AND
        val matchRegexMatchType = FilterAndMapModule.MatchRegexMatchType.values().firstOrNull {
            it.type == matchRegexMatchTypeStr
        } ?: FilterAndMapModule.MatchRegexMatchType.NORMAL
        val matchRegexListResult =judgeMatchRegexListResult(
            matchRegexList,
            matchRegexMatchType,
            matchConditionType,
            srcLine,
        )
        return matchRegexListResult
                && matchInLines
    }

    private fun judgeMatchLines(
        srcLine: String,
        matchLineList: List<String>,
        linesMatchType: FilterAndMapModule.LinesMatchType,
    ): Boolean {
        val isEmptyMatchLineList =
            matchLineList.filter { it.isNotEmpty() }.isEmpty()
        return when(linesMatchType){
            FilterAndMapModule.LinesMatchType.NORMAL ->
                matchLineList.contains(srcLine)
            FilterAndMapModule.LinesMatchType.DENY ->
                !matchLineList.contains(srcLine)
        } || isEmptyMatchLineList
    }

    private fun judgeMatchRegexListResult(
        matchRegexList: List<Regex>,
        matchRegexMatchType: FilterAndMapModule.MatchRegexMatchType,
        matchConditionType: FilterAndMapModule.MatchConditionType,
        srcLine: String,
    ): Boolean {
        val matchRegexListResultSrc = when(matchConditionType){
            FilterAndMapModule.MatchConditionType.AND -> {
                matchRegexList.all {
                    it.containsMatchIn(srcLine)
                }
            }
            FilterAndMapModule.MatchConditionType.OR -> {
                matchRegexList.any {
                    it.containsMatchIn(srcLine)
                }
            }
        }
        return when(matchRegexMatchType){
            FilterAndMapModule.MatchRegexMatchType.NORMAL
            -> matchRegexListResultSrc
            FilterAndMapModule.MatchRegexMatchType.DENY
            -> !matchRegexListResultSrc
        }
    }
}