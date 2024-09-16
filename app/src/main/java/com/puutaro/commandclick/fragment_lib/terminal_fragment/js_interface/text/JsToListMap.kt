package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.libs.FilterAndMapModule
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.lang.ref.WeakReference
import java.util.SortedMap

class JsToListMap(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private val subSeparator = '?'

    @JavascriptInterface
    fun map(
        con: String,
        separator: String,
        extraMapCon: String,
    ): String {

        /*
        ## Description

        Recreate src contents
        In [js action](), this function corresponded to map method in other language's collection.

        ## con arg

        Recreate this con

        ## separator arg

        separator for contents

        - Convert contents to list by this separator in inner process

        ## extraMapCon arg

        filter setting

        | Key name        | value                     | Description                     |
        |-----------------|---------------------------|---------------------------------|
        | `removeRegex`        | regex string | Remove match string, <br> Enable Multiple specifications by  removeRegex1, removeRegex2, removeRegex3... <br> apply by order    |
        | `replaceStr`        | string                | When this option specified, `removeRegex`'s match string is replaced with this string  <br> Enable Multiple specifications by replaceStr1, replaceStr2, replaceStr3... <br> apply by order   |
        | `compPrefix`        | string                | Comp prefix <br> Enable Multiple specifications by compPrefix1, compPrefix2, compPrefix3... <br> apply by order   |
        | `compSuffix` | string                      | Comp suffix <br> Enable Multiple specifications by compPrefix1, compPrefix2, compPrefix3... <br> apply by order   |
        | `shellPath`     | path string               | shell path to remake src element by shell script |
        | `shellArgs`     | key-values separated by `?`               | shell script args. <br> Replace this arg name with value on execute  |
        | `shellOutput`     | string              | replace output with this string, if output is exist         |
        | `shellFannelPath`     | path string               | Fannel path used by inner process         |

        - Enable to edit key-value two field tsv by using ${key}, ${value} and ${line} variables in shellPath
        - ${key}, ${value}, ${line} is first field, second field, total in two field tsv line

        ## Example 1

        ```js.js
        var=runMap
            ?func=jsToListFilter.map
            ?args=
                lines=`${src contents}`
                &separator="\n"
                &matchLines=`${match contents}`
                &extraMap=`
                    |removeRegex1="^[\t]*"
                    |replaceStr1="prefix1"
                    |removeRegex2="[\t]*$"
                    |replaceStr2="suffix2"
                `
		```

		- ${src contents} con

        ```txt.txt
         aa
        bb\t
        cc
        ```

        - output

        ```txt.txt
        prefix1aa
        bbsuffix2
        cc
        ```

        ## Example 2

        ```js.js
        var=runMap
            ?func=jsToListFilter.map
            ?args=
                lines=`${src contents}`
                &separator="\n"
                &matchLines=`${match contents}`
                &extraMap=`
                    |compPrefix1=prefix1
                    |compPrefix2=prefix2
                    |compSuffix1=suffix1
                `
		```

		- ${src contents} con

        ```txt.txt
        aa
        bb
        cc
        ```

        - output

        ```txt.txt
        prefix2Prefix1aa
        bbSuffix2
        cc
        ```

        - before prefix`s first char is concat as Upper case

        ## Example key-value format

        ```js.js
        var=runMap
            ?func=jsToListMap.map
            ?args=
                lines=`homeFannelsPath\t${cmdclickConfigHomeFannelsPath}`
                &separator="\n"
                &extraMap=`
                    |shellFannelPath=${FANNEL_PATH}
                    |shellPath=${cmdclickConfigDiffCurToBeforeFilePath}
                    |shellOutput=\${key}`
                `
		```

		- ${src contents} con

        ```txt.txt
        aa\t/storage/emulated/0/aa.txt
        bb\t/storage/emulated/0/bb.txt
        cc\t/storage/emulated/0/cc.txt
        ```

        - output

        ```txt.txt
        aa (Exist diff ${src_file_path} and ${dest_file_path})
        cc (Exist diff ${src_file_path} and ${dest_file_path})
        ```

        ```sh.sh
        src_file_path="${value}"
        src_file_name="$(basename "${src_file_path}")"
        dest_file_path="${cmdclickConfigTempDirPath}/${src_file_name}"
        ${b} diff \
          "${src_file_path}" \
          "${dest_file_path}" \
          2>/dev/null
        ```

        - ${b} is busy box env path
        - ${key}, ${value}, ${line} is first field, second field, total in two field tsv line
        - ${cmdclickConfigTempDirPath} is definite by [replace variable](/home/xbabu/Desktop/share/android/CommandClick/md/developer/set_replace_variables.md)

        */


        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context
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
//        val currentAppDirPath = fannelPath?.let {
//            CcPathTool.getMainFannelDirPath(it)
//        } ?: String()
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
//                    currentAppDirPath,
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
            terminalFragment.busyboxExecutor,
            shellCon,
            shellArgsMapSrc,
            shellOutput
        )
        val mapMadeCon = mapDestLinesList.joinToString(separator)
        return mapMadeCon
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
            val semaphore = Semaphore(concurrentLimit)
            val listSize = mapSrcLinesList.size
            val channel = Channel<Pair<Int, String>>(listSize)
            val receiveLineMapList = mutableListOf<Pair<Int, String>>()
            runBlocking {
                val jobList = mapSrcLinesList.mapIndexed {
                        index, line ->
                    async {
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
                            val lineByShell =
                                FilterAndMapModule.ShellResultForToList.getResultByShell(
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
                }
                jobList.forEach { it.await() }
                channel.close()
                for (received in channel){
                    // Channelから受信
                    receiveLineMapList.add(received)
                }
            }
            receiveLineMapList.sortBy { it.first }
            return receiveLineMapList.map {
                it.second
            }
        }
    }
}