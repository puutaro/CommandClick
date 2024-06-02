package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections.libs.FilterAndMapModule
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.RegexTool
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.time.LocalDateTime
import java.util.SortedMap

class JsToListFilter(
    terminalFragment: TerminalFragment
)  {

    private val context = terminalFragment.context
    private val subSeparator = '?'
    private val busyboxExecutor = terminalFragment.busyboxExecutor

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
        val fannelPath = extraMap.get(
            FilterAndMapModule.ExtraMapBaseKey.SHELL_FANNEL_PATH.key
        )
        val currentAppDirPath = fannelPath?.let {
            CcPathTool.getMainFannelDirPath(it)
        } ?: String()
        val fannelName = fannelPath?.let {
            File(
                CcPathTool.getMainFannelFilePath(it)
            ).name
        } ?: String()
        val replaceVariableMap = fannelPath?.let {
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                context,
                it
            )
        }
        val shellCon =
            extraMap.get(FilterAndMapModule.ExtraMapBaseKey.SHELL_PATH.key)?.let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    ReadText(it).readText(),
                    replaceVariableMap,
                    currentAppDirPath,
                    fannelName
                )

            }
        val shellArgsMapSrc =
            extraMap.get(FilterAndMapModule.ExtraMapBaseKey.SHELL_ARGS.key).let {
                CmdClickMap.createMap(
                    it,
                    subSeparator
                ).toMap()
            }
        val shellOutput =
            extraMap.get(FilterAndMapModule.ExtraMapBaseKey.SHELL_OUTPUT.key)

        val matchLineList = matchLines.split(separator)

        val filterSrcLinesList = lines.split(separator)
        val filterLineList = ToListFilterOperator.operate(
            filterSrcLinesList,
            removeRegexToReplaceKeyList,
            extraMap,
            compPrefixList,
            compSuffixList,
            matchRegexList,
            matchConditionStr,
            matchLineList,
            linesMatchTypeStr,
            matchRegexMatchTypeStr,
            busyboxExecutor,
            shellCon,
            shellArgsMapSrc,
            shellOutput
        )
        return filterLineList.joinToString(separator)
    }

    private object ToListFilterOperator {

        fun operate(
            mapSrcLinesList: List<String>,
            removeRegexToReplaceKeyList: List<Pair<Regex, String>>,
            extraMap: SortedMap<String, String>,
            compPrefixList: List<String>,
            compSuffixList: List<String>,
            matchRegexList: List<Regex>,
            matchConditionStr: String?,
            matchLineList: List<String>,
            linesMatchTypeStr: String?,
            matchRegexMatchTypeStr: String?,
            busyboxExecutor: BusyboxExecutor?,
            shellCon: String?,
            shellArgsMapSrc: Map<String, String>,
            shellOutput: String?
        ): List<String> {
            val notInsertMark = "\${CMDCLICK_NOT_INSERT}"
            val concurrentLimit = 50
            val semaphore: Semaphore = Semaphore(concurrentLimit)
            val listSize = mapSrcLinesList.size
            val channel = Channel<Pair<Int, String>>(listSize)
            val receiveLineMapList = mutableListOf<Pair<Int, String>>()
            runBlocking {
                launch {
                    mapSrcLinesList.forEachIndexed {
                            index, line ->
                        semaphore.withPermit {
                            val lineWithRemove = FilterAndMapModule.applyRemoveRegex(
                                line,
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
                            val lineByShell = FilterAndMapModule.ShellResultForToList.getResultByShell(
                                busyboxExecutor,
                                lineWithCompSuffix,
                                shellArgsMapSrc,
                                shellCon,
                                shellOutput
                            )
                            val isInsert = matcher(
                                lineByShell,
                                matchRegexList,
                                matchConditionStr,
                                matchLineList,
                                linesMatchTypeStr,
                                matchRegexMatchTypeStr,
                            )
                            val insertLine = when(isInsert){
                                false -> notInsertMark
                                else -> lineWithCompSuffix
                            }
//                            FileSystems.writeFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "jsToListFilter_${LocalDateTime.now()}.txt").absolutePath,
//                                listOf(
//                                    "lineWithRemove: ${lineWithRemove}",
//                                    "lineWithCompPrefix: ${lineWithCompPrefix}",
//                                    "lineWithCompSuffix: ${lineWithCompSuffix}",
//                                    "lineByShell: ${lineByShell}",
//                                    "insertLine: ${insertLine}",
//                                ).joinToString("\n\n")
//                            )
                            // Channelに文字列を送信
                            channel.send(
                                Pair(index, insertLine)
                            )
                        }
                    }
                    channel.close()
                }

                launch {
                    for (rowNumToLine in channel){
                        // Channelから受信
                        receiveLineMapList.add(rowNumToLine)
                    }
                }
            }
            val rowNumToLineFilterList = receiveLineMapList.filter {
                it.second != notInsertMark
            }.toMutableList()
            rowNumToLineFilterList.sortBy {
                it.first
            }
            return rowNumToLineFilterList.map {
                it.second
            }
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
}