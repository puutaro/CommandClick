package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ActionToolForQr
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrLogoEditDialogLauncher
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsQrLogoEdit(
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
    fun edit_S(
        clickFileName: String,
    ){
        /*

        ## Description

        Edit QR image by dialog in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Corresponding macro

        -> [EDIT_LOGO](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_list_logo.md#edit_logo)

        ## clickFileName arg

        clicked file name

        ## Example

        ```js.js
        var=runExecQr
           ?func=jsQrLogoEdit.exec_S
           ?args=
               &clickFileName=${file name}

        ```

        */

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