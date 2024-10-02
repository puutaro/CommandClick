package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.EditToolbarSwitcher
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class JsToolbarSwitcher(
    private val terminalFragmentRef: WeakReference<TerminalFragment,>
) {
    @JavascriptInterface
    fun switch_S(
        execPlayBtnLongPress: String
    ){
        /*
        ## Description

        Change toolbar on edit box

        ## Corresponding macro

        -> [WEB_SEARCH](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#web_search)

        -> [PAGE_SEARCH](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#page_search)

        -> [NORMAL](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#normal)

        ## execPlayBtnLongPress

        Specify long press type string

        | Long press type string        | Description                               |
        |-------------|------------------------------------------|
        | `WEB_SEARCH` | Switch web search mode |
        | `PAGE_SEARCH` | Switch page search mode |
        | `NORMAL` | Switch normal toolbar |

        */

        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
//        CoroutineScope(Dispatchers.Main).launch {
//            val editFragment = withContext(Dispatchers.Main) {
//                TargetFragmentInstance.getCurrentEditFragmentFromFragment(
//                    activity,
////                    currentAppDirPath,
//                    currentFannelName,
//                    currentFannelState,
//                )
//            } ?: return@launch
//            withContext(Dispatchers.Main) {
//                EditToolbarSwitcher.switch(
//                    editFragment,
//                    execPlayBtnLongPress
//                )
//            }
//        }
    }
}