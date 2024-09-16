package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class JsToast(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun short(
        contents: String,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            ToastUtils.showShort(
                contents,
            )
        }
    }

    @JavascriptInterface
    fun errLog(
        contents: String
    ){
        if(
            CheckTool.EscapeErrMessage.howEscapeErrMessage(contents)
        ) return
        CoroutineScope(Dispatchers.Main).launch {
            ToastUtils.showShort(contents)
        }
    }

    @JavascriptInterface
    fun long(
        contents: String,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            ToastUtils.showLong(
                contents,
            )
        }
    }
}