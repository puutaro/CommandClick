package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.LogTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JsToast(
    fragment: Fragment
) {
    val context = fragment.context

    @JavascriptInterface
    fun short(
        contents: String,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                context,
                contents,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @JavascriptInterface
    fun errLog(
        contents: String
    ){
        if(
            LogTool.howEscapeErrMessage(contents)
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
            Toast.makeText(
                context,
                contents,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}