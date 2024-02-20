package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFileOrDirGetter(
    terminalFragment: TerminalFragment
) {
    private val activity = terminalFragment.activity
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
    private val fileOrDirGetterForSettingButton = terminalFragment.fileOrDirGetterForSettingButton

    @JavascriptInterface
    fun get(
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