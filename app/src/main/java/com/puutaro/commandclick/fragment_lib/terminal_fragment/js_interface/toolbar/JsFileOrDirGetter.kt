package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFileOrDirGetter(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
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
    private val filterMapSeparator = '|'

    @JavascriptInterface
    fun get_S(
        onDirectoryPick: Boolean,
        filterMapCon: String,
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
        val filterMap = CmdClickMap.createMap(
            filterMapCon,
            filterMapSeparator,
        ).toMap()
        val filterPrefixListCon = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.PREFIX.key,
        ) ?: String()
        val filterSuffixListCon = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.SUFFIX.key,
        ) ?: String()
        val filterShellPathCon =
            EditSettingExtraArgsTool.makeShellCon(
                filterMap
            )
        val listener =
            context as? TerminalFragment.OnGetFileListenerForTerm
                ?: return
        listener.onGetFileForTerm(
            parentDirPath,
            onDirectoryPick,
            filterPrefixListCon,
            filterSuffixListCon,
            filterShellPathCon,
        )
//        fileOrDirGetterForSettingButton?.get(
//            parentDirPath,
//            onDirectoryPick
//        )
    }
}