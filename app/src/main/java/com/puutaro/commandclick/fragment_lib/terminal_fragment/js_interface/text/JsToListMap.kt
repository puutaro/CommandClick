package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.libs.FilterAndMapModule
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.util.SortedMap

class JsToListMap(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val subSeparator = '?'
    private val busyboxExecutor = terminalFragment.busyboxExecutor

    @JavascriptInterface
    fun map(
        con: String,
        separator: String,
        extraMapCon: String,
    ): String {
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            FilterAndMapModule.extraMapSeparator,
        ).toMap().toSortedMap()
        val removeRegexToReplaceKeyList =
            FilterAndMapModule.makeRemoveRegexToReplaceKeyPairList(
                extraMap,
                FilterAndMapModule.ExtraMapBaseKey.REMOVE_REGEX.key
            )
        val compPrefixList = FilterAndMapModule.makeTargetValueList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.COMP_PREFIX.key
        )
//       FileSystems.writeFile(
//           File(UsePath.cmdclickDefaultAppDirPath, "jsToListMap.txt").absolutePath,
//           listOf(
//               "removRegexList: ${makeTargetValueList(
//                   extraMap,
//                   ExtraMapBaseKey.REMOVE_REGEX.key
//               )}",
//               "compPrefixList: ${makeTargetValueList(
//                   extraMap,
//                   ExtraMapBaseKey.COMP_PREFIX.key
//               )}"
//           ).joinToString("\n\n\n")
//       )
        val compSuffixList = FilterAndMapModule.makeTargetValueList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.COMP_SUFFIX.key
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
        val mapSrcLinesList = con.split(separator)
        val mapDestLinesList = ToListMapOperator.operate(
            mapSrcLinesList,
            removeRegexToReplaceKeyList,
            extraMap,
            compPrefixList,
            compSuffixList,
            busyboxExecutor,
            shellCon,
            shellArgsMapSrc,
            shellOutput
        )
        return mapDestLinesList.joinToString(separator)
    }

    private object ToListMapOperator {
        fun operate(
            mapSrcLinesList: List<String>,
            removeRegexToReplaceKeyList: List<Pair<Regex, String>>,
            extraMap: SortedMap<String, String>,
            compPrefixList: List<String>,
            compSuffixList: List<String>,
            busyboxExecutor: BusyboxExecutor?,
            shellCon: String?,
            shellArgsMapSrc: Map<String, String>,
            shellOutput: String?
        ): List<String> {
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
                            // Channelに文字列を送信
                            channel.send(
                                Pair(index, lineByShell)
                            )
                        }
                    }
                    channel.close()
                }

                launch {
                    for (received in channel){
                        // Channelから受信
                        receiveLineMapList.add(received)
                    }
                }
            }
            receiveLineMapList.sortBy { it.first }
            return receiveLineMapList.map {
                it.second
            }
        }
    }
}