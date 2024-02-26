package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.EditToolbarSwitcher
import com.puutaro.commandclick.util.state.FannelPrefGetter
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsEditToolbarSwitcher(
    terminalFragment: TerminalFragment
) {
    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = FannelPrefGetter.getCurrentAppDirPath(
        readSharePreferenceMap
    )
    private val currentFannelName = FannelPrefGetter.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val currentFannelState = FannelPrefGetter.getCurrentStateName(
        readSharePreferenceMap
    )

    @JavascriptInterface
    fun switch_S(
        switchMacro: String,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main){
                EditToolbarSwitcher.switch(
                    editFragment,
                    switchMacro,
                )
            }
        }
    }
}