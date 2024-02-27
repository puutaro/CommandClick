package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleEditItem
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecWriteItem
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsEditorItem(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
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
    fun edit_S(
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecSimpleEditItem.edit(
            editFragment,
            selectedItem,
            listIndexPosition,
        )
    }

    @JavascriptInterface
    fun write_S(
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecWriteItem.write(
            editFragment,
            selectedItem,
            listIndexPosition
        )
    }
}