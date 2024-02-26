package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsQrScanner(
    terminalFragment: TerminalFragment
) {

    private val activity = terminalFragment.activity
    private val readSharePreferenceMap =
        terminalFragment.readSharePreferenceMap
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
    fun scan_S(){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        execQrScan(
            editFragment,
            currentAppDirPath,
        )
    }
}

private fun execQrScan(
    editFragment: EditFragment,
    currentAppDirPath: String,
) {
    val filterDirInWithListIndex = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
        editFragment,
        ListIndexForEditAdapter.indexListMap,
        ListIndexForEditAdapter.listIndexTypeKey
    )
    val activeCurrentDirPath = when (
        editFragment.existIndexList && filterDirInWithListIndex.isNotEmpty()
    ) {
        true -> filterDirInWithListIndex
        else -> currentAppDirPath
    }

    QrScanner(
        editFragment,
        activeCurrentDirPath,
    ).scanFromCamera()
}
