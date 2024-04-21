package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.LogTool

class JsToast(
    fragment: Fragment
) {
    val context = fragment.context

    @JavascriptInterface
    fun short(
        contents: String,
    ) {
        ToastUtils.showShort(contents)
    }

    @JavascriptInterface
    fun errLog(
        contents: String
    ){
        if(
            LogTool.howEscapeErrMessage(contents)
        ) return
        ToastUtils.showShort(contents)
    }

    @JavascriptInterface
    fun long(
        contents: String,
    ) {
        ToastUtils.showLong(contents)
    }
}