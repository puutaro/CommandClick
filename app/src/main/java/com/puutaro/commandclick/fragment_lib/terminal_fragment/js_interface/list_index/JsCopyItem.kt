package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFile
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyFileHere
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecCopyPath
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsCopyItem(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )
    private val currentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_state
    )

    @JavascriptInterface
    fun copyPath(
        selectedItem: String,
        listIndexPosition: Int
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecCopyPath.copyPath(
            editFragment,
            selectedItem,
            listIndexPosition
        )
    }

    @JavascriptInterface
    fun copyFile_S(
        selectedItem: String,
        listIndexPosition: Int
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecCopyFile.copyFile(
            editFragment,
            selectedItem,
            listIndexPosition,
        )
    }

    @JavascriptInterface
    fun copyFileHere_S(
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecCopyFileHere.copyFileHere(
            editFragment,
            selectedItem,
            listIndexPosition
        )
    }
}