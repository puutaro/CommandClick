package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ActionToolForQr
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrLogoEditDialogLauncher
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsQrLogoEdit(
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
    fun edit_S(
        clickFileName: String,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val parentDirPath = ActionToolForQr.getParentDirPath(
            editFragment
        )
        ToastUtils.showShort("show: ${parentDirPath}: ${clickFileName}")
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                QrLogoEditDialogLauncher.launch(
                    editFragment,
                    parentDirPath,
                    clickFileName,
                    editFragment.qrDialogConfig ?: emptyMap(),
//                    qrDialogConfigMap
                )
            }
        }
    }
}