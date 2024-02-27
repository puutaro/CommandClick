package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFileOrDirGetter(
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
    private val fileOrDirGetterForSettingButton = terminalFragment.fileOrDirGetterForSettingButton

    @JavascriptInterface
    fun get_S(
        onDirectoryPick: Boolean
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val parentDirPath =
            when (
                editFragment.existIndexList
            ) {
                true -> ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
                else -> currentAppDirPath
            }
        fileOrDirGetterForSettingButton?.get(
            parentDirPath,
            onDirectoryPick
        )
    }
}