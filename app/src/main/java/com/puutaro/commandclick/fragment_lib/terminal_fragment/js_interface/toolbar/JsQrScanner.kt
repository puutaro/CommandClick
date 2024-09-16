package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsQrScanner(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {


    @JavascriptInterface
    fun scan_S(){

        /*
        ## Description

        Scan QR code and execute

        ## Corresponding macro

        -> [QR_SCAN](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#qr_scan)

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
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        execQrScan(
            editFragment,
//            currentAppDirPath,
        )
    }
}

private fun execQrScan(
    editFragment: EditFragment,
//    currentAppDirPath: String,
) {
//    val filterDirInWithListIndex = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//        editFragment,
//        ListIndexForEditAdapter.indexListMap,
//        ListIndexForEditAdapter.listIndexTypeKey
//    )
//    val activeCurrentDirPath = when (
//        editFragment.existIndexList && filterDirInWithListIndex.isNotEmpty()
//    ) {
//        true -> filterDirInWithListIndex
//        else -> currentAppDirPath
//    }

    QrScanner.scanFromCamera(
        editFragment,
//        activeCurrentDirPath,
    )
}
