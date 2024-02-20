package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ActionToolForQr
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrLogoEditDialogLauncher
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsQrLogoEdit(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val readSharePreferenceMap =
        terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )
    private val currentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_state
    )

    @JavascriptInterface
    fun edit_S(
        clickFileName: String,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelPath,
            currentFannelState
        ) ?: return
        val parentDirPath = ActionToolForQr.getParentDirPath(
            editFragment
        )
        Toast.makeText(
           context,
           "show: ${parentDirPath}: ${clickFileName}",
           Toast.LENGTH_SHORT
        ).show()
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