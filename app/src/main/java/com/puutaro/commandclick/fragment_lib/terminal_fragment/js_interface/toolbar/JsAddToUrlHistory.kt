package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMap
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.UrlHistoryAddToTsv
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsAddToUrlHistory(
    terminalFragment: TerminalFragment
) {
    private val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = SharePrefTool.getCurrentStateName(
        readSharePreferenceMap
    )

    @JavascriptInterface
    fun add_S(
        argsMapCon: String,
        separator: String,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val argsMap = JsMap.createMapFromCon(
            argsMapCon,
            separator
        ) ?: emptyMap()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                UrlHistoryAddToTsv(
                    editFragment,
                    argsMap
                ).invoke()
            }
        }
    }

//    private fun makeUrlHistoryList(
//        editFragment: EditFragment,
//        argsMapCon: String,
//        separator: String,
//    ): String {
//        return takeFromUrlHistoryList(
//            editFragment,
//            argsMapCon,
//            separator,
//        ).map {
//            val titleUrlList = it.split("\t")
//            val title = titleUrlList.first()
//            "${title}\t${icons8Ring}"
//        }.joinToString("\t")
//    }
//
//    private fun takeFromUrlHistoryList(
//        editFragment: EditFragment,
//        argsMapCon: String,
//        separator: String,
//    ): List<String> {
//        val historyFirstExtractNum = 50
//        val busyboxExecutor = editFragment.busyboxExecutor
//        val argsMap = JsMap.createMapFromCon(
//            argsMapCon,
//            separator
//        ) ?: emptyMap()
////            JsActionDataMapKeyObj.getJsMacroArgs(
////            jsActionMap
////        ) ?: emptyMap()
//
//        val shellCon = EditSettingExtraArgsTool.makeShellCon(
//            argsMap,
//        ).let {
//            if(
//                it.isNotEmpty()
//            ) return@let it
//            defaultShellCon
//        }
//        val takeLines = 5
//        val readSharePreferenceMap = editFragment.readSharePreferenceMap
//        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
//            readSharePreferenceMap,
//            SharePrefferenceSetting.current_app_dir
//        )
//
//        val urlHistoryParentDirPath = File(
//            currentAppDirPath,
//            UsePath.cmdclickUrlSystemDirRelativePath
//        ).absolutePath
//        val srcTsvCon = ReadText(
//            File(
//                urlHistoryParentDirPath,
//                UsePath.cmdclickUrlHistoryFileName,
//            ).absolutePath
//        ).textToList().let {
//            TsvTool.uniqByTitle(it)
//        }.take(historyFirstExtractNum).joinToString("\n")
//        return ShellTool.filter(
//            srcTsvCon,
//            busyboxExecutor,
//            shellCon,
//            argsMap
//        ).split("\n").filter {
//            it.split("\t").size == 2
//        }.take(takeLines).reversed()
//    }
//
//    private val defaultShellCon = """
//        echo "${ShellTool.shellConReplaceMark}" | \
//            ${'$'}{b} awk '{
//            	if(\
//            		${'$'}0 !~ /\thttp:\/\// \
//            		&& ${'$'}0 !~ /\thttps:\/\// \
//            	) next
//            	print ${'$'}0
//            }'
//        """.trimIndent()
}