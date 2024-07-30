package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsQrScanner(
    terminalFragment: TerminalFragment
) {

    private val activity = terminalFragment.activity
    private val fannelInfoMap =
        terminalFragment.fannelInfoMap
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
    fun scan_S(){

        /*
        ## Description

        Scan QR code and execute

        ## Corresponding macro

        -> [QR_SCAN](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#qr_scan)

        */

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

    QrScanner.scanFromCamera(
        editFragment,
        activeCurrentDirPath,
    )
}
