package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMap
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.UrlHistoryAddToTsv
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsAddToUrlHistory(
    terminalFragment: TerminalFragment
) {
    private val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val currentFannelState = FannelInfoTool.getCurrentStateName(
        fannelInfoMap
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
}